package com.yapily.orione2e.api.service.iam.payload.response;

import com.yapily.orione2e.api.HTTPResponse;
import com.yapily.orione2e.api.HTTPResponseHandler;
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
