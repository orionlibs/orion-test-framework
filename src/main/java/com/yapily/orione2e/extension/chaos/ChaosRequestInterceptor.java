package com.yapily.orione2e.extension.chaos;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.protocol.HttpContext;

public class ChaosRequestInterceptor implements HttpRequestInterceptor
{
    private final long latencyMs;
    private final double dropRate;
    private final Random randomness; // may be ThreadLocalRandom via wrapper


    public ChaosRequestInterceptor(long latencyMs, double dropRate, Random randomness)
    {
        this.latencyMs = Math.max(0L, latencyMs);
        this.dropRate = Math.min(1.0, Math.max(0.0, dropRate));
        this.randomness = Objects.requireNonNull(randomness);
    }


    private static long nextLong(Random rnd, long origin, long bound)
    {
        if(rnd instanceof ThreadLocalRandom)
        {
            return ThreadLocalRandom.current().nextLong(origin, bound);
        }
        // Random has no nextLong(origin,bound) pre-Java8; implement simple choice
        long r = Math.abs(rnd.nextLong());
        return origin + (r % (bound - origin));
    }


    @Override
    public void process(HttpRequest request, EntityDetails entity, HttpContext context) throws HttpException, IOException
    {
        // introduce randomized latency
        try
        {
            long sleepMs = (latencyMs <= 0) ? 0L : nextLong(randomness, 0L, latencyMs + 1L);
            if(sleepMs > 0)
            {
                Thread.sleep(sleepMs);
            }
        }
        catch(InterruptedException ie)
        {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while simulating network latency", ie);
        }
        // simulate drop
        double r = randomness.nextDouble();
        if(r < dropRate)
        {
            // choose a realistic exception: SocketTimeoutException simulates timeout; otherwise IOException
            if(randomness.nextBoolean())
            {
                throw new SocketTimeoutException("Simulated network timeout by ChaosNetwork (r=" + r + ")");
            }
            else
            {
                throw new IOException("Simulated network drop by ChaosNetwork (r=" + r + ")");
            }
        }
        // otherwise proceed normally (nothing to do here)
    }
}
