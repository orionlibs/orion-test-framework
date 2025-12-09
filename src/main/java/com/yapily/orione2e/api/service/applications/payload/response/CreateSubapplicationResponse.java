package com.yapily.orione2e.api.service.applications.payload.response;

import com.yapily.orione2e.api.HTTPResponse;
import com.yapily.orione2e.api.HTTPResponseHandler;
import java.util.Map;

public class CreateSubapplicationResponse implements HTTPResponse
{
    private final Map<String, Object> body;


    public CreateSubapplicationResponse(HTTPResponseHandler httpResponseHandler)
    {
        this.body = getResponseBody(httpResponseHandler);
    }


    public String getSubapplicationId()
    {
        Map<String, Object> data = (Map<String, Object>)body.get("data");
        return (String)data.get("id");
    }
}
