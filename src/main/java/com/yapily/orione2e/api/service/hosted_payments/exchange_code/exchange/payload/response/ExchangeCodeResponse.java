package com.yapily.orione2e.api.service.hosted_payments.exchange_code.exchange.payload.response;

import com.yapily.orione2e.api.HTTPResponse;
import com.yapily.orione2e.api.HTTPResponseHandler;
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
