package com.yapily.e2ejunit.api.service.hosted_payments.exchange_code.authorisation;

import com.yapily.e2ejunit.api.APICall;
import com.yapily.e2ejunit.api.HTTPResponseHandler;
import java.io.IOException;
import org.apache.hc.client5.http.classic.methods.HttpGet;

public class AuthorisationAPI extends APICall
{
    public AuthorisationAPI(String endpoint)
    {
        super(endpoint);
    }


    @Override
    public AuthorisationResponse call() throws IOException
    {
        HttpGet request = new HttpGet(endpoint);
        HTTPResponseHandler response = super.makeAPICall(request);
        return new AuthorisationResponse(response);
    }
}
