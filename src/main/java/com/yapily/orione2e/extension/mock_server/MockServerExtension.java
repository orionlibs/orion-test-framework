package com.yapily.orione2e.extension.mock_server;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class MockServerExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback, ParameterResolver
{
    private static final ExtensionContext.Namespace NS = ExtensionContext.Namespace.create(MockServerExtension.class);
    private static final String SERVER_KEY = "mock-server";


    @Override
    public void beforeAll(ExtensionContext context) throws Exception
    {
        // If class-level annotation present, start one server for the class
        WithMockServer classAnno = context.getRequiredTestClass().getAnnotation(WithMockServer.class);
        if(classAnno != null)
        {
            startAndStoreServer(context, classAnno, storeForClass(context));
        }
    }


    @Override
    public void afterAll(ExtensionContext context) throws Exception
    {
        WithMockServer classAnno = context.getRequiredTestClass().getAnnotation(WithMockServer.class);
        if(classAnno != null)
        {
            stopAndRemoveServer(storeForClass(context));
        }
    }


    @Override
    public void beforeEach(ExtensionContext context) throws Exception
    {
        // If method-level annotation present -> start per-test server
        Optional<Method> methodOpt = context.getTestMethod();
        if(methodOpt.isPresent())
        {
            Method method = methodOpt.get();
            WithMockServer methodAnno = method.getAnnotation(WithMockServer.class);
            if(methodAnno != null)
            {
                startAndStoreServer(context, methodAnno, storeForMethod(context));
                return;
            }
        }
        // No method annotation -> if class-level present we've already started in beforeAll, so nothing to do.
    }


    @Override
    public void afterEach(ExtensionContext context) throws Exception
    {
        // If method-level annotation present -> stop server
        Optional<Method> methodOpt = context.getTestMethod();
        if(methodOpt.isPresent())
        {
            Method method = methodOpt.get();
            WithMockServer methodAnno = method.getAnnotation(WithMockServer.class);
            if(methodAnno != null)
            {
                stopAndRemoveServer(storeForMethod(context));
            }
            else
            {
                // not method-level => nothing to do (class-level server will be stopped in afterAll)
            }
        }
    }
    // ---------- ParameterResolver ----------


    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
    {
        Parameter p = parameterContext.getParameter();
        // Accept WireMockServer
        if(WireMockServer.class.equals(p.getType()))
        {
            return true;
        }
        // Accept @MockServerUrl annotations for String or java.net.URI
        if(p.isAnnotationPresent(MockServerUrl.class))
        {
            Class<?> t = p.getType();
            return String.class.equals(t) || java.net.URI.class.equals(t);
        }
        return false;
    }


    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
    {
        // Look for server from method store first, then class store
        ExtensionContext.Store methodStore = storeForMethod(extensionContext);
        WireMockServer server = methodStore.get(SERVER_KEY, WireMockServer.class);
        if(server == null)
        {
            server = storeForClass(extensionContext).get(SERVER_KEY, WireMockServer.class);
        }
        if(server == null)
        {
            throw new ParameterResolutionException("No WireMockServer available: is @WithMockServer present on the test method or class?");
        }
        Parameter p = parameterContext.getParameter();
        if(WireMockServer.class.equals(p.getType()))
        {
            return server;
        }
        if(p.isAnnotationPresent(MockServerUrl.class))
        {
            if(String.class.equals(p.getType()))
            {
                return server.baseUrl();
            }
            else if(java.net.URI.class.equals(p.getType()))
            {
                return java.net.URI.create(server.baseUrl());
            }
        }
        throw new ParameterResolutionException("Unsupported parameter type for MockServerExtension: " + p);
    }
    // ---------- helpers ----------


    private void startAndStoreServer(ExtensionContext context, WithMockServer config, ExtensionContext.Store store) throws Exception
    {
        // avoid double-start
        if(store.get(SERVER_KEY, WireMockServer.class) != null)
        {
            return;
        }
        WireMockConfiguration cfg = WireMockConfiguration.wireMockConfig();
        if(config.port() > 0)
        {
            cfg.port(config.port());
        }
        else
        {
            cfg.dynamicPort();
        }
        WireMockServer server = new WireMockServer(cfg);
        server.start();
        // optional setup
        Class<? extends MockServerSetup> setupClass = config.setup();
        if(setupClass != null && setupClass != NoOpMockServerSetup.class)
        {
            MockServerSetup setup = setupClass.getDeclaredConstructor().newInstance();
            setup.setup(server);
        }
        store.put(SERVER_KEY, server);
    }


    private void stopAndRemoveServer(ExtensionContext.Store store)
    {
        WireMockServer server = store.remove(SERVER_KEY, WireMockServer.class);
        if(server != null && server.isRunning())
        {
            server.stop();
        }
    }


    private ExtensionContext.Store storeForMethod(ExtensionContext ctx)
    {
        return ctx.getStore(ExtensionContext.Namespace.create(NS, ctx.getRequiredTestMethod()));
    }


    private ExtensionContext.Store storeForClass(ExtensionContext ctx)
    {
        return ctx.getStore(ExtensionContext.Namespace.create(NS, ctx.getRequiredTestClass()));
    }
}
