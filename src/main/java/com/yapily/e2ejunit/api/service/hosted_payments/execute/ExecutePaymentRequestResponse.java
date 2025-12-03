package com.yapily.e2ejunit.api.service.hosted_payments.execute;

import com.yapily.e2ejunit.api.HTTPResponse;
import com.yapily.e2ejunit.api.HTTPResponseHandler;
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
