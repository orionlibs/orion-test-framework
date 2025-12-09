package com.yapily.orione2e.api.service.hosted_payments.payment_request.payload.response;

import com.yapily.orione2e.api.HTTPResponse;
import com.yapily.orione2e.api.HTTPResponseHandler;
import java.util.Map;

public class CreatePaymentRequestResponse implements HTTPResponse
{
    private final Map<String, Object> body;


    public CreatePaymentRequestResponse(HTTPResponseHandler httpResponseHandler)
    {
        this.body = getResponseBody(httpResponseHandler);
    }


    public String hostedAuthToken()
    {
        Map<String, Object> data = (Map<String, Object>)body.get("data");
        String hostedUrl = (String)data.get("hostedUrl");
        String[] hashParts = hostedUrl.split("#");
        String[] equalsParts = hashParts[1].split("=");
        return equalsParts[1];
    }


    public String hostedPaymentRequestId()
    {
        Map<String, Object> data = (Map<String, Object>)body.get("data");
        return (String)data.get("paymentRequestId");
    }
}
