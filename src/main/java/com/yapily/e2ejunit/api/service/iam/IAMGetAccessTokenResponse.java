package com.yapily.e2ejunit.api.service.iam;

import com.yapily.e2ejunit.api.HTTPResponse;
import com.yapily.e2ejunit.api.HTTPResponseHandler;
import java.util.Map;

public class IAMGetAccessTokenResponse implements HTTPResponse
{
    private final Map<String, Object> body;


    public IAMGetAccessTokenResponse(HTTPResponseHandler httpResponseHandler)
    {
        this.body = getResponseBody(httpResponseHandler);
    }


    public String getAccessToken()
    {
        return (String)this.body.get("access_token");
    }
}
