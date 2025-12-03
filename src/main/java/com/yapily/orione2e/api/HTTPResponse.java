package com.yapily.orione2e.api;

import java.util.HashMap;
import java.util.Map;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

public interface HTTPResponse
{
    default Map<String, Object> getResponseBody(HTTPResponseHandler httpResponseHandler)
    {
        JsonMapper mapper = new JsonMapper();
        if(httpResponseHandler.getBody() != null && !httpResponseHandler.getBody().isEmpty())
        {
            return mapper.readValue(httpResponseHandler.getBody(), new TypeReference<HashMap<String, Object>>()
            {
            });
        }
        else
        {
            return new HashMap<>();
        }
    }


    default Map<String, String> getResponseHeaders(HTTPResponseHandler httpResponseHandler)
    {
        return httpResponseHandler.getHeadersAsMap();
    }
}
