package com.yapily.orione2e.api.service.hosted_payments.payment_info;

import com.yapily.orione2e.api.APICall;
import com.yapily.orione2e.api.HTTPResponseHandler;
import com.yapily.orione2e.api.service.hosted_payments.payment_info.payload.response.GetPaymentRequestInfoResponse;
import java.io.IOException;
import org.apache.hc.client5.http.classic.methods.HttpGet;

public class GetPaymentRequestInfoAPI extends APICall
{
    private String jwt;


    public GetPaymentRequestInfoAPI(String paymentRequestId, String endpoint, String jwt)
    {
        super(endpoint.replace("{hostedPaymentRequestId}", paymentRequestId));
        this.jwt = jwt;
    }


    @Override
    public GetPaymentRequestInfoResponse call() throws IOException
    {
        HttpGet request = new HttpGet(endpoint);
        oauthHeader(request, jwt);
        HTTPResponseHandler response = super.makeAPICall(request);
        return new GetPaymentRequestInfoResponse(response);
    }
}
