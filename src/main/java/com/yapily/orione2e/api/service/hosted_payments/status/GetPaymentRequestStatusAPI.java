package com.yapily.orione2e.api.service.hosted_payments.status;

import com.yapily.orione2e.api.APICall;
import com.yapily.orione2e.api.HTTPResponseHandler;
import java.io.IOException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.core5.util.Timeout;

public class GetPaymentRequestStatusAPI extends APICall
{
    private String jwt;


    public GetPaymentRequestStatusAPI(String hostedPaymentRequestId, String hostedPaymentId, String endpoint, String jwt)
    {
        super(endpoint.replace("{paymentRequestId}", hostedPaymentRequestId).replace("{hostedPaymentId}", hostedPaymentId));
        this.jwt = jwt;
    }


    @Override
    public GetPaymentRequestStatusResponse call() throws IOException, InterruptedException
    {
        HttpGet request = new HttpGet(endpoint);
        RequestConfig requestConfig = RequestConfig.custom()
                        .setConnectionRequestTimeout(Timeout.ofSeconds(10))
                        .setConnectTimeout(Timeout.ofSeconds(10))
                        .setResponseTimeout(Timeout.ofSeconds(10))
                        .build();
        request.setConfig(requestConfig);
        Thread.sleep(5000);
        oauthHeader(request, jwt);
        HTTPResponseHandler response = super.makeAPICall(request);
        return new GetPaymentRequestStatusResponse(response);
    }
}
