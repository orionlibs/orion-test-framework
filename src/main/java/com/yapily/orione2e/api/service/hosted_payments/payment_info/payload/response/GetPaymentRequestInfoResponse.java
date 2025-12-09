package com.yapily.orione2e.api.service.hosted_payments.payment_info.payload.response;

import com.yapily.orione2e.api.HTTPResponse;
import com.yapily.orione2e.api.HTTPResponseHandler;
import java.util.Map;

public class GetPaymentRequestInfoResponse implements HTTPResponse
{
    private final Map<String, Object> body;


    public GetPaymentRequestInfoResponse(HTTPResponseHandler httpResponseHandler)
    {
        this.body = getResponseBody(httpResponseHandler);
    }


    public String hostedPaymentId()
    {
        Map<String, Object> data = (Map<String, Object>)body.get("data");
        return (String)data.get("hostedPaymentId");
    }
}
