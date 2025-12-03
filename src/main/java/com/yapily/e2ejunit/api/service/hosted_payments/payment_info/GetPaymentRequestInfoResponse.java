package com.yapily.e2ejunit.api.service.hosted_payments.payment_info;

import com.yapily.e2ejunit.api.HTTPResponse;
import com.yapily.e2ejunit.api.HTTPResponseHandler;
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
