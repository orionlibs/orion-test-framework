package com.yapily.e2ejunit.api.service.hosted_payments.execute;

import com.yapily.e2ejunit.api.APICall;
import com.yapily.e2ejunit.api.HTTPResponseHandler;
import java.io.IOException;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class ExecutePaymentRequestAPI extends APICall
{
    private String body;
    private String jwt;
    private String consentToken;


    public ExecutePaymentRequestAPI(String hostedPaymentRequestId, String hostedPaymentId, String endpoint, String jwt, String consentToken)
    {
        super(endpoint.replace("{paymentRequestId}", hostedPaymentRequestId).replace("{hostedPaymentId}", hostedPaymentId));
        this.jwt = jwt;
        this.consentToken = consentToken;
        this.body = "{}";
    }


    @Override
    public ExecutePaymentRequestResponse call() throws IOException
    {
        HttpPost request = new HttpPost(endpoint);
        oauthHeader(request, jwt);
        consentTokenHeader(request, consentToken);
        request.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));
        HTTPResponseHandler response = super.makeAPICall(request);
        return new ExecutePaymentRequestResponse(response);
    }
}
