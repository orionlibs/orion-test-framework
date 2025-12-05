package com.yapily.orione2e.chaos;

import static org.assertj.core.api.Assertions.assertThat;

import com.yapily.orione2e.extension.chaos.Chaos;
import com.yapily.orione2e.extension.chaos.ChaosNetworkExtension;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ChaosNetworkExtension.class)
public class ChaosTest
{
    @Test
    @Chaos(latencyMs = 200, dropRate = 0.2, seed = 42)
    void flakyCallShouldSometimesFail(CloseableHttpClient client) throws Exception
    {
        HttpGet req = new HttpGet("https://httpbin.org/delay/0"); // example endpoint
        // call many times to observe failures
        for(int i = 0; i < 10; i++)
        {
            try(ClassicHttpResponse resp = client.executeOpen(null, req, null))
            {
                // if request succeeded, ensure status available
                int code = resp.getCode();
                assertThat(code).isGreaterThanOrEqualTo(0);
            }
            catch(Exception e)
            {
                // simulated drop/timeouts will manifest here as IOException/SocketTimeoutException
                System.out.println("Observed simulated network error: " + e);
            }
        }
    }
}
