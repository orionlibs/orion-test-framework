package com.yapily.e2ejunit.api.service.hosted_payments.exchange_code.exchange;

import com.yapily.e2ejunit.api.HTTPResponse;
import com.yapily.e2ejunit.api.HTTPResponseHandler;
import java.util.Map;

public class ExchangeCodeResponse implements HTTPResponse
{
    private final Map<String, Object> body;


    public ExchangeCodeResponse(HTTPResponseHandler httpResponseHandler)
    {
        this.body = getResponseBody(httpResponseHandler);
    }


    public String consentToken()
    {
        return this.body.get("consentToken").toString();
    }
}
