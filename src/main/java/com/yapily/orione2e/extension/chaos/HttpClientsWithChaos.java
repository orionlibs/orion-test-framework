package com.yapily.orione2e.extension.chaos;

import java.util.concurrent.ThreadLocalRandom;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;

public class HttpClientsWithChaos
{
    /**
     * Create a new CloseableHttpClient with chaos interceptors installed.
     * If seed >= 0, a deterministic Random is used (new Random(seed)).
     * If seed < 0, ThreadLocalRandom is used.
     */
    public static CloseableHttpClient createChaosClient(long latencyMs, double dropRate, long seed)
    {
        final java.util.Random rnd = (seed >= 0L) ? new java.util.Random(seed) : ThreadLocalRandom.current();
        ChaosRequestInterceptor chaos = new ChaosRequestInterceptor(latencyMs, dropRate, rnd);
        return HttpClients.custom()
                        // ensure our interceptor runs early (first) so it affects everything
                        .addRequestInterceptorFirst(chaos)
                        .build();
    }
}
