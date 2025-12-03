package com.yapily.orione2e.api;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;

public class HTTPResponseHandler implements HttpClientResponseHandler<HTTPResponseHandler>
{
    private Header[] headers;
    private Map<String, String> headersAsMap;
    private int status;
    private String body;


    @Override
    public HTTPResponseHandler handleResponse(ClassicHttpResponse response) throws HttpException, IOException
    {
        this.headers = response.getHeaders();
        this.status = response.getCode();
        final HttpEntity entity = response.getEntity();
        return entity == null ? this : handleEntity(entity);
    }


    public HTTPResponseHandler handleEntity(HttpEntity entity) throws IOException
    {
        try
        {
            this.body = EntityUtils.toString(entity);
        }
        catch(Exception e)
        {
            throw new IOException("Unable to convert response entity to string");
        }
        return this;
    }


    public Header[] getHeaders()
    {
        return headers;
    }


    public Header getHeaders(String headerName)
    {
        return Arrays.stream(headers).filter(header -> header.getName().toLowerCase().equals(headerName.toLowerCase())).findFirst().get();
    }


    public Map<String, String> getHeadersAsMap()
    {
        return headersAsMap;
    }


    public void convertHeadersToMap(Header[] headers)
    {
        headersAsMap = new HashMap<>();
        for(Header header : headers)
        {
            headersAsMap.put(header.getName(), header.getValue());
        }
    }


    public int getStatus()
    {
        return status;
    }


    public String getBody()
    {
        return body;
    }
}
