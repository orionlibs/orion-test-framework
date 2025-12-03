package com.yapily.e2ejunit.api.service.hosted_payments.exchange_code.authorisation;

import com.yapily.e2ejunit.api.HTTPResponse;
import com.yapily.e2ejunit.api.HTTPResponseHandler;
import java.util.Map;

public class AuthorisationResponse implements HTTPResponse
{
    private final Map<String, Object> body;
    private Map<String, String> headersAsMap;


    public AuthorisationResponse(HTTPResponseHandler httpResponseHandler)
    {
        this.body = getResponseBody(httpResponseHandler);
        this.headersAsMap = getResponseHeaders(httpResponseHandler);
    }


    public String location()
    {
        return this.headersAsMap.get("Location");
    }


    public String code()
    {
        String[] hashParts = location().split("#");
        String[] ampersandParts = hashParts[1].split("&");
        String[] equalsParts = ampersandParts[0].split("=");
        return equalsParts[1];
    }


    public String state()
    {
        String[] hashParts = location().split("#");
        String[] ampersandParts = hashParts[1].split("&");
        String[] equalsParts = ampersandParts[1].split("=");
        return equalsParts[1];
    }


    public String idToken()
    {
        String[] hashParts = location().split("#");
        String[] ampersandParts = hashParts[1].split("&");
        String[] equalsParts = ampersandParts[2].split("=");
        return equalsParts[1];
    }
}
