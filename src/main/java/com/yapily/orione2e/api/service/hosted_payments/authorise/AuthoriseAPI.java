package com.yapily.orione2e.api.service.hosted_payments.authorise;

import com.yapily.orione2e.api.APICall;
import com.yapily.orione2e.api.HTTPResponseHandler;
import com.yapily.orione2e.api.service.hosted_payments.authorise.payload.request.AuthoriseRequest;
import com.yapily.orione2e.api.service.hosted_payments.authorise.payload.response.AuthoriseResponse;
import com.yapily.orione2e.utils.JSONUtils;
import java.io.IOException;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class AuthoriseAPI extends APICall
{
    String jwt;
    AuthoriseRequest req;


    public AuthoriseAPI(String hostedPaymentRequestId, String hostedPaymentId, AuthoriseRequest request, String endpoint, String jwt)
    {
        super(endpoint.replace("{hostedPaymentRequestId}", hostedPaymentRequestId).replace("{hostedPaymentId}", hostedPaymentId));
        this.jwt = jwt;
        this.req = request;
    }


    @Override
    public AuthoriseResponse call() throws IOException
    {
        HttpPost request = new HttpPost(endpoint);
        oauthHeader(request, jwt);
        request.setEntity(new StringEntity(JSONUtils.toJSON(req), ContentType.APPLICATION_JSON));
        HTTPResponseHandler response = super.makeAPICall(request);
        return new AuthoriseResponse(response);
    }
}
