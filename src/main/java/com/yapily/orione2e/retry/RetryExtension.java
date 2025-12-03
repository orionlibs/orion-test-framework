package com.yapily.orione2e.retry;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetryExtension implements InvocationInterceptor
{
    private static final Logger log = LoggerFactory.getLogger(RetryExtension.class);


    @Override
    public void interceptTestMethod(Invocation<Void> invocation,
                    ReflectiveInvocationContext<Method> invocationContext,
                    ExtensionContext extensionContext) throws Throwable
    {
        Retry retry = findRetry(invocationContext.getExecutable(), extensionContext);
        if(retry == null)
        {
            invocation.proceed();
        }
        else
        {
            executeWithRetry(invocation, retry);
        }
    }


    @Override
    public void interceptTestTemplateMethod(Invocation<Void> invocation,
                    ReflectiveInvocationContext<Method> invocationContext,
                    ExtensionContext extensionContext) throws Throwable
    {
        // Covers @TestTemplate / parameterized tests where applicable
        Retry retry = findRetry(invocationContext.getExecutable(), extensionContext);
        if(retry == null)
        {
            invocation.proceed();
        }
        else
        {
            executeWithRetry(invocation, retry);
        }
    }


    private Retry findRetry(Method method, ExtensionContext context)
    {
        // look for annotation on method first, then on the test class
        Retry r = method.getAnnotation(Retry.class);
        if(r != null)
        {
            return r;
        }
        return context.getTestClass().map(c -> c.getAnnotation(Retry.class)).orElse(null);
    }


    private void executeWithRetry(Invocation<Void> invocation, Retry retry) throws Throwable
    {
        int attempts = Math.max(1, retry.attempts());
        long delayMs = Math.max(0, retry.delayMs());
        List<Throwable> attemptFailures = new ArrayList<>(attempts);
        Throwable last = null;
        for(int attempt = 1; attempt <= attempts; attempt++)
        {
            try
            {
                invocation.proceed();
                // success -> return immediately
                if(attempt > 1)
                {
                    log.debug("Test succeeded on attempt {}/{}", attempt, attempts);
                }
                return;
            }
            catch(Throwable t)
            {
                last = t;
                attemptFailures.add(t);
                // log the failure for diagnostics
                log.warn("Test attempt {}/{} failed: {}", attempt, attempts, t.toString());
                // if last attempt, attach previous failures as suppressed and rethrow
                if(attempt == attempts)
                {
                    // Attach previous failures (all except the last one) as suppressed to the final throwable
                    for(int i = 0; i < attemptFailures.size() - 1; i++)
                    {
                        Throwable suppressed = attemptFailures.get(i);
                        try
                        {
                            last.addSuppressed(suppressed);
                        }
                        catch(Exception ex)
                        {
                            // ignore failures to add suppressed — not critical
                            log.debug("Could not add suppressed exception: {}", ex.toString());
                        }
                    }
                    // If the last throwable is InterruptedException or caused by it, re-interrupt the thread
                    if(isOrCausedByInterruptedException(last))
                    {
                        Thread.currentThread().interrupt();
                    }
                    // Rethrow the final throwable (with suppressed children attached)
                    throw last;
                }
                // otherwise wait then retry
                if(delayMs > 0)
                {
                    try
                    {
                        Thread.sleep(delayMs);
                    }
                    catch(InterruptedException ie)
                    {
                        // re-interrupt and include it as a suppressed exception on the last throwable, then throw
                        Thread.currentThread().interrupt();
                        last.addSuppressed(ie);
                        throw ie;
                    }
                }
            }
        }
    }


    private boolean isOrCausedByInterruptedException(Throwable t)
    {
        Throwable cur = t;
        while(cur != null)
        {
            if(cur instanceof InterruptedException)
            {
                return true;
            }
            cur = cur.getCause();
        }
        return false;
    }
}
