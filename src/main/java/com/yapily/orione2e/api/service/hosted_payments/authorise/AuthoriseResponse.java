package com.yapily.orione2e.api.service.hosted_payments.authorise;

import com.yapily.orione2e.api.HTTPResponse;
import com.yapily.orione2e.api.HTTPResponseHandler;
import java.util.Map;

public class AuthoriseResponse implements HTTPResponse
{
    private final Map<String, Object> body;


    public AuthoriseResponse(HTTPResponseHandler httpResponseHandler)
    {
        this.body = getResponseBody(httpResponseHandler);
    }


    public String authorisationUrl()
    {
        Map<String, Object> data = (Map<String, Object>)body.get("data");
        return (String)data.get("authorisationUrl");
    }
}
