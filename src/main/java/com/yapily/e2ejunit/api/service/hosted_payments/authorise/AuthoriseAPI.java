package com.yapily.e2ejunit.api.service.hosted_payments.authorise;

import com.yapily.e2ejunit.api.APICall;
import com.yapily.e2ejunit.api.HTTPResponseHandler;
import java.io.IOException;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class AuthoriseAPI extends APICall
{
    private String body;
    private String jwt;


    public AuthoriseAPI(String hostedPaymentRequestId, String hostedPaymentId, String endpoint, String jwt)
    {
        super(endpoint.replace("{hostedPaymentRequestId}", hostedPaymentRequestId).replace("{hostedPaymentId}", hostedPaymentId));
        this.jwt = jwt;
        this.body = """
                        {
                          "hostedAuthRedirect": "https://prototypes.yapily.com/auth-link2.html"
                        }
                        """;
    }


    @Override
    public AuthoriseResponse call() throws IOException
    {
        HttpPost request = new HttpPost(endpoint);
        oauthHeader(request, jwt);
        request.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));
        HTTPResponseHandler response = super.makeAPICall(request);
        return new AuthoriseResponse(response);
    }
}
