package com.yapily.orione2e.api;

import com.yapily.orione2e.model.HttpClientConfiguration;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;

public class HTTPClient
{
    public static CloseableHttpClient INSTANCE;

    static
    {
        createHttpClient();
    }

    private static void createHttpClient()
    {
        if(INSTANCE == null)
        {
            ConnectionConfig connectionConfig = ConnectionConfig.custom()
                            .setConnectTimeout(Timeout.ofSeconds(HttpClientConfiguration.connectionTimeout))
                            .setSocketTimeout(Timeout.ofSeconds(3))
                            .build();
            RequestConfig requestConfig = RequestConfig.custom()
                            .setResponseTimeout(Timeout.ofSeconds(HttpClientConfiguration.responseTimeout))
                            .build();
            PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
            connectionManager.setDefaultConnectionConfig(connectionConfig);
            connectionManager.setMaxTotal(200);
            connectionManager.setDefaultMaxPerRoute(20);
            INSTANCE = HttpClients.custom()
                            .setConnectionManager(connectionManager)
                            .setDefaultRequestConfig(requestConfig)
                            .setRedirectStrategy(new NoRedirectStrategy())
                            .build();
        }
    }
}
