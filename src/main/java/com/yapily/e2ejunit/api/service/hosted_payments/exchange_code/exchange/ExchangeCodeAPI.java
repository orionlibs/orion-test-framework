package com.yapily.e2ejunit.api.service.hosted_payments.exchange_code.exchange;

import com.yapily.e2ejunit.api.APICall;
import com.yapily.e2ejunit.api.HTTPResponseHandler;
import java.io.IOException;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class ExchangeCodeAPI extends APICall
{
    private String body;
    private String jwt;


    public ExchangeCodeAPI(String code, String idToken, String state, String endpoint, String jwt)
    {
        super(endpoint);
        this.jwt = jwt;
        this.body = """
                        {
                          "code": "%s",
                          "id_token": "%s",
                          "state": "%s"
                        }
                        """.formatted(code, idToken, state);
    }


    @Override
    public ExchangeCodeResponse call() throws IOException
    {
        HttpPost request = new HttpPost(endpoint);
        oauthHeader(request, jwt);
        request.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));
        HTTPResponseHandler response = super.makeAPICall(request);
        return new ExchangeCodeResponse(response);
    }
}
