package com.yapily.orione2e.capture_traffic;

import static org.assertj.core.api.Assertions.assertThat;

import com.yapily.orione2e.extension.capture_traffic.ApacheHttpTrafficRecorder;
import com.yapily.orione2e.extension.capture_traffic.ApacheHttpTrafficRecorder.Exchange;
import com.yapily.orione2e.extension.capture_traffic.CaptureTraffic;
import com.yapily.orione2e.extension.capture_traffic.CaptureTrafficExtension;
import java.util.List;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(CaptureTrafficExtension.class)
public class CaptureTrafficTest
{
    @Test
    @CaptureTraffic(snippetMaxBytes = 2048)
    void captureExample(CloseableHttpClient client, ApacheHttpTrafficRecorder recorder) throws Exception
    {
        HttpGet req = new HttpGet("https://httpbin.org/get");
        try(ClassicHttpResponse resp = client.executeOpen(null, req, null))
        {
            int code = resp.getCode();
            assertThat(code).isEqualTo(200);
        }
        // Wait if needed — but with synchronous client the interceptors already recorded exchange
        List<Exchange> exchanges = recorder.getExchanges();
        assertThat(exchanges).isNotEmpty();
        Exchange e = exchanges.get(0);
        assertThat(e.method).isEqualTo("GET");
        assertThat(e.uri).contains("/get");
        assertThat(e.status).isEqualTo(200);
    }
}
