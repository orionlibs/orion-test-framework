package com.yapily.orione2e.api.service.hosted_payments.execute;

import com.yapily.orione2e.api.HTTPResponse;
import com.yapily.orione2e.api.HTTPResponseHandler;
import java.util.Map;

public class ExecutePaymentRequestResponse implements HTTPResponse
{
    private final Map<String, Object> body;


    public ExecutePaymentRequestResponse(HTTPResponseHandler httpResponseHandler)
    {
        this.body = getResponseBody(httpResponseHandler);
    }


    public String authorisationUrl()
    {
        Map<String, Object> data = (Map<String, Object>)body.get("data");
        return (String)data.get("authorisationUrl");
    }
}
