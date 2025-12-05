package com.yapily.orione2e.extension.capture_traffic;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;

public class HttpClientsWithRecorder
{
    public static CloseableHttpClient createRecordingClient(ApacheHttpTrafficRecorder recorder, int snippetMaxBytes)
    {
        RecordingRequestInterceptor req = new RecordingRequestInterceptor(snippetMaxBytes);
        RecordingResponseInterceptor resp = new RecordingResponseInterceptor(recorder, snippetMaxBytes);
        return HttpClients.custom()
                        .addRequestInterceptorFirst(req)
                        .addResponseInterceptorLast(resp)
                        .build();
    }
}
