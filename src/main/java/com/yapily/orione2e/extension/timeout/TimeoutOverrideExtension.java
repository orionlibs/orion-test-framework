package com.yapily.orione2e.extension.timeout;

import java.lang.reflect.Method;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.opentest4j.TestAbortedException;

public class TimeoutOverrideExtension implements InvocationInterceptor
{
    private final ExecutorService executor = Executors.newCachedThreadPool();


    @Override
    public void interceptTestMethod(Invocation<Void> invocation,
                    ReflectiveInvocationContext<Method> invocationContext,
                    ExtensionContext extensionContext) throws Throwable
    {
        Method testMethod = invocationContext.getExecutable();
        TimeoutOverride timeout = testMethod.getAnnotation(TimeoutOverride.class);
        if(timeout == null)
        {
            // No timeout override, proceed normally
            invocation.proceed();
            return;
        }
        long timeoutMs = timeout.valueMs();
        Future<?> future = executor.submit(() -> {
            try
            {
                invocation.proceed();
            }
            catch(Throwable t)
            {
                throw new CompletionException(t);
            }
        });
        try
        {
            future.get(timeoutMs, TimeUnit.MILLISECONDS);
        }
        catch(TimeoutException e)
        {
            future.cancel(true); // attempt to interrupt the test thread
            throw new TestAbortedException("Test exceeded @TimeoutOverride of " + timeoutMs + "ms", e);
        }
        catch(ExecutionException e)
        {
            // unwrap test exception
            Throwable cause = e.getCause();
            if(cause instanceof AssertionError)
            {
                throw (AssertionError)cause;
            }
            if(cause instanceof Exception)
            {
                throw (Exception)cause;
            }
            throw cause;
        }
    }


    // Shutdown executor when JVM exits
    static
    {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try
            {
                Executors.newCachedThreadPool().shutdownNow();
            }
            catch(Exception ignored)
            {
            }
        }));
    }
}
