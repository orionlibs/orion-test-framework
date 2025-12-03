package com.yapily.orione2e.api;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;

public abstract class APICall
{
    protected String endpoint;
    private Map<String, Object> requestBody;


    public APICall(String endpoint)
    {
        this.endpoint = endpoint;
    }


    public APICall(String endpoint, Map<String, Object> requestBody)
    {
        this(endpoint);
        this.requestBody = requestBody;
    }


    public void basicAuthHeader(HttpUriRequestBase request, String token)
    {
        request.addHeader("Authorization", "Basic " + token);
    }


    public void oauthHeader(HttpUriRequestBase request, String token)
    {
        request.addHeader("Authorization", "Bearer " + token);
    }


    public void consentTokenHeader(HttpUriRequestBase request, String token)
    {
        request.addHeader("consentToken", token);
    }


    protected HTTPResponseHandler makeAPICall(HttpUriRequestBase request) throws IOException
    {
        return HTTPClient.INSTANCE.execute(request, new HTTPResponseHandler());
    }


    public abstract HTTPResponse call() throws IOException, InterruptedException;


    public void overrideRequestBodyKey(String key, Object value)
    {
        setNestedElement(key, value);
    }


    private void setNestedElement(String path, Object newValue)
    {
        List<String> keys = List.of(path.split("\\."));
        if(keys.size() > 1)
        {
            String lastKey = keys.removeLast();
            for(String key : keys)
            {
                requestBody = (Map<String, Object>)requestBody.get(key);
            }
            requestBody.put(lastKey, newValue);
        }
        else
        {
            requestBody.put(keys.getFirst(), newValue);
        }
    }
}
