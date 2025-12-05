package com.yapily.orione2e.extension.rate_limited;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

public class RateLimitedExtension implements InvocationInterceptor
{
    // Per-key lock object to synchronize wait-and-start logic
    private static final ConcurrentHashMap<String, Object> LOCKS = new ConcurrentHashMap<>();
    // Per-key last start time in nanoseconds (monotonic from System.nanoTime())
    private static final ConcurrentHashMap<String, AtomicLong> LAST_START_NS = new ConcurrentHashMap<>();


    @Override
    public void interceptTestMethod(Invocation<Void> invocation,
                    ReflectiveInvocationContext<Method> invocationContext,
                    ExtensionContext extensionContext) throws Throwable
    {
        RateLimited rl = findRateLimited(invocationContext.getExecutable(), extensionContext);
        if(rl == null)
        {
            invocation.proceed();
        }
        else
        {
            proceedWithRateLimit(invocation, rl);
        }
    }


    private RateLimited findRateLimited(Method method, ExtensionContext ctx)
    {
        RateLimited rl = method.getAnnotation(RateLimited.class);
        if(rl != null)
        {
            return rl;
        }
        return ctx.getTestClass().map(c -> c.getAnnotation(RateLimited.class)).orElse(null);
    }


    private void proceedWithRateLimit(Invocation<Void> invocation, RateLimited rl) throws Throwable
    {
        final String key = (rl.key() == null || rl.key().isEmpty()) ? "default" : rl.key();
        final long minNs = TimeUnit.MILLISECONDS.toNanos(Math.max(0L, rl.minIntervalMs()));
        Object lock = LOCKS.computeIfAbsent(key, k -> new Object());
        AtomicLong lastStart = LAST_START_NS.computeIfAbsent(key, k -> new AtomicLong(0L));
        // Wait until at least minNs elapsed since lastStart, then record a new start time and proceed.
        synchronized(lock)
        {
            while(true)
            {
                long now = System.nanoTime();
                long prev = lastStart.get();
                long elapsed = now - prev;
                long waitNs = minNs - elapsed;
                if(waitNs <= 0L)
                {
                    // allowed to start now — record start time and exit loop
                    lastStart.set(System.nanoTime());
                    break;
                }
                // convert to ms + nanos for Thread.sleep
                long sleepMs = TimeUnit.NANOSECONDS.toMillis(waitNs);
                int sleepNanos = (int)(waitNs - TimeUnit.MILLISECONDS.toNanos(sleepMs));
                try
                {
                    if(sleepMs > 0 || sleepNanos > 0)
                    {
                        // Sleep for the needed amount (may wake earlier)
                        Thread.sleep(sleepMs, sleepNanos);
                    }
                    else
                    {
                        // busy-yield tiny waits (shouldn't happen often)
                        Thread.yield();
                    }
                }
                catch(InterruptedException ie)
                {
                    // preserve interruption status and propagate
                    Thread.currentThread().interrupt();
                    throw ie;
                }
            }
        }
        // Proceed with the invocation outside the synchronized lock so the test runs concurrently with others waiting.
        invocation.proceed();
    }
}
