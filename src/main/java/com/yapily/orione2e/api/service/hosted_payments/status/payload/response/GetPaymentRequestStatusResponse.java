package com.yapily.orione2e.api.service.hosted_payments.status.payload.response;

import com.yapily.orione2e.api.HTTPResponse;
import com.yapily.orione2e.api.HTTPResponseHandler;
import java.util.List;
import java.util.Map;

public class GetPaymentRequestStatusResponse implements HTTPResponse
{
    private final Map<String, Object> body;


    public GetPaymentRequestStatusResponse(HTTPResponseHandler httpResponseHandler)
    {
        this.body = getResponseBody(httpResponseHandler);
    }


    public List<String> phases()
    {
        Map<String, Object> data = (Map<String, Object>)body.get("data");
        return (List<String>)data.get("phases");
    }
}
