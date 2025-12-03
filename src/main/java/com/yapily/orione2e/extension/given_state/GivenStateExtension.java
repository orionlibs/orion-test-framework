package com.yapily.orione2e.extension.given_state;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.opentest4j.TestAbortedException;

public class GivenStateExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback
{
    private static final String FAIL_OPEN_PROP = "givenstate.failOpen";
    private static final ExtensionContext.Namespace NS = ExtensionContext.Namespace.create(GivenStateExtension.class);
    private static final String LOADED_FIXTURES = "loaded-fixtures"; // maps to List<LoadedFixture>


    private static class LoadedFixture
    {
        final String fixturePath;
        final StateLoader loader;


        LoadedFixture(String fixturePath, StateLoader loader)
        {
            this.fixturePath = fixturePath;
            this.loader = loader;
        }
    }


    // ----- BEFORE ALL (class-scoped fixtures) -----
    @Override
    public void beforeAll(ExtensionContext context) throws Exception
    {
        Class<?> testClass = context.getRequiredTestClass();
        GivenState[] states = testClass.getAnnotationsByType(GivenState.class);
        if(states.length == 0)
        {
            return;
        }
        List<LoadedFixture> loaded = new ArrayList<>();
        for(GivenState gs : states)
        {
            try
            {
                StateLoader loader = gs.loader().getDeclaredConstructor().newInstance();
                loader.load(gs.value(), context);
                loaded.add(new LoadedFixture(gs.value(), loader));
            }
            catch(Throwable t)
            {
                handleLoadFailure(t, "class-scoped GivenState '" + gs.value() + "'", context);
                // if failOpen, skip remaining loads; else the exception has been rethrown
                if(isFailOpen())
                {
                    break;
                }
            }
        }
        // store for cleanup later
        getStoreForClass(context).put(LOADED_FIXTURES, loaded);
    }


    @Override
    public void afterAll(ExtensionContext context) throws Exception
    {
        // cleanup class-scoped fixtures in reverse order
        List<LoadedFixture> loaded = getStoreForClass(context).remove(LOADED_FIXTURES, List.class);
        if(loaded != null)
        {
            Collections.reverse(loaded);
            for(LoadedFixture lf : loaded)
            {
                try
                {
                    lf.loader.cleanup(lf.fixturePath, context);
                }
                catch(Throwable t)
                {
                    // best-effort cleanup; log via System.err for simplicity
                    System.err.println("Failed to cleanup fixture " + lf.fixturePath + ": " + t);
                }
            }
        }
    }


    // ----- BEFORE EACH (method-scoped fixtures) -----
    @Override
    public void beforeEach(ExtensionContext context) throws Exception
    {
        Optional<Method> methodOpt = context.getTestMethod();
        if(!methodOpt.isPresent())
        {
            return;
        }
        Method method = methodOpt.get();
        GivenState[] states = method.getAnnotationsByType(GivenState.class);
        if(states.length == 0)
        {
            return;
        }
        List<LoadedFixture> loaded = new ArrayList<>();
        for(GivenState gs : states)
        {
            try
            {
                StateLoader loader = gs.loader().getDeclaredConstructor().newInstance();
                loader.load(gs.value(), context);
                loaded.add(new LoadedFixture(gs.value(), loader));
            }
            catch(Throwable t)
            {
                handleLoadFailure(t, "method-scoped GivenState '" + gs.value() + "'", context);
                if(isFailOpen())
                {
                    break;
                }
            }
        }
        getStoreForMethod(context).put(LOADED_FIXTURES, loaded);
    }


    @Override
    public void afterEach(ExtensionContext context) throws Exception
    {
        // cleanup method-scoped fixtures in reverse order
        List<LoadedFixture> loaded = getStoreForMethod(context).remove(LOADED_FIXTURES, List.class);
        if(loaded != null)
        {
            Collections.reverse(loaded);
            for(LoadedFixture lf : loaded)
            {
                try
                {
                    lf.loader.cleanup(lf.fixturePath, context);
                }
                catch(Throwable t)
                {
                    System.err.println("Failed to cleanup fixture " + lf.fixturePath + ": " + t);
                }
            }
        }
    }
    // ----- helpers -----


    private ExtensionContext.Store getStoreForClass(ExtensionContext ctx)
    {
        return ctx.getStore(NS);
    }


    private ExtensionContext.Store getStoreForMethod(ExtensionContext ctx)
    {
        return ctx.getStore(ExtensionContext.Namespace.create(NS, ctx.getRequiredTestMethod()));
    }


    private boolean isFailOpen()
    {
        return Boolean.parseBoolean(System.getProperty(FAIL_OPEN_PROP, "false"));
    }


    private void handleLoadFailure(Throwable t, String description, ExtensionContext ctx) throws Exception
    {
        String reason = "Failed to load " + description + ": " + t;
        if(isFailOpen())
        {
            // skip the test (or class) by throwing TestAbortedException
            throw new TestAbortedException(reason, t);
        }
        else
        {
            // rethrow to fail the test setup
            if(t instanceof Exception)
            {
                throw (Exception)t;
            }
            else
            {
                throw new RuntimeException(t);
            }
        }
    }
}
