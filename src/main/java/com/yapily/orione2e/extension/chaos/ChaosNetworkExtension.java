package com.yapily.orione2e.extension.chaos;

import java.lang.reflect.Method;
import java.util.Optional;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class ChaosNetworkExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback, ParameterResolver
{
    private static final ExtensionContext.Namespace NS = ExtensionContext.Namespace.create(ChaosNetworkExtension.class);
    private static final String CLIENT_KEY = "chaos-client";


    @Override
    public void beforeAll(ExtensionContext context) throws Exception
    {
        // class-level annotation -> create client and store in class store
        Optional<Class<?>> testClass = context.getTestClass();
        if(testClass.isPresent())
        {
            Chaos ann = testClass.get().getAnnotation(Chaos.class);
            if(ann != null)
            {
                CloseableHttpClient client = HttpClientsWithChaos.createChaosClient(ann.latencyMs(), ann.dropRate(), ann.seed());
                getStoreForClass(context).put(CLIENT_KEY, client);
            }
        }
    }


    @Override
    public void afterAll(ExtensionContext context) throws Exception
    {
        CloseableHttpClient c = getStoreForClass(context).remove(CLIENT_KEY, CloseableHttpClient.class);
        if(c != null)
        {
            try
            {
                c.close();
            }
            catch(Exception ignored)
            {
            }
        }
    }


    @Override
    public void beforeEach(ExtensionContext context) throws Exception
    {
        // method-level annotation takes precedence
        Optional<Method> m = context.getTestMethod();
        if(m.isPresent())
        {
            Chaos ann = m.get().getAnnotation(Chaos.class);
            if(ann != null)
            {
                CloseableHttpClient client = HttpClientsWithChaos.createChaosClient(ann.latencyMs(), ann.dropRate(), ann.seed());
                getStoreForMethod(context).put(CLIENT_KEY, client);
                return;
            }
        }
        // otherwise, if class-scoped client already exists, nothing to do here (it will be provided by param resolver)
    }


    @Override
    public void afterEach(ExtensionContext context) throws Exception
    {
        CloseableHttpClient c = getStoreForMethod(context).remove(CLIENT_KEY, CloseableHttpClient.class);
        if(c != null)
        {
            try
            {
                c.close();
            }
            catch(Exception ignored)
            {
            }
        }
    }


    // ParameterResolver: provide CloseableHttpClient when requested
    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException
    {
        return CloseableHttpClient.class.equals(parameterContext.getParameter().getType());
    }


    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException
    {
        // prefer method-level client, then class-level
        CloseableHttpClient client = getStoreForMethod(extensionContext).get(CLIENT_KEY, CloseableHttpClient.class);
        if(client == null)
        {
            client = getStoreForClass(extensionContext).get(CLIENT_KEY, CloseableHttpClient.class);
        }
        if(client == null)
        {
            throw new ParameterResolutionException("No Chaos CloseableHttpClient available: annotate test with @ChaosNetwork on method or class, or request your own client via HttpClientsWithChaos.createChaosClient(...).");
        }
        return client;
    }


    // helpers
    private ExtensionContext.Store getStoreForMethod(ExtensionContext ctx)
    {
        return ctx.getStore(ExtensionContext.Namespace.create(NS, ctx.getRequiredTestMethod()));
    }


    private ExtensionContext.Store getStoreForClass(ExtensionContext ctx)
    {
        return ctx.getStore(ExtensionContext.Namespace.create(NS, ctx.getRequiredTestClass()));
    }
}
