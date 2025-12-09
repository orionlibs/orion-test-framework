package com.yapily.orione2e.api.service.hosted_payments.submit_institution;

import com.yapily.orione2e.api.APICall;
import com.yapily.orione2e.api.HTTPResponseHandler;
import com.yapily.orione2e.api.service.hosted_payments.submit_institution.payload.response.SubmitInstitutionResponse;
import java.io.IOException;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class SubmitInstitutionAPI extends APICall
{
    String body;
    String jwt;


    public SubmitInstitutionAPI(String hostedPaymentRequestId, String hostedPaymentId, String endpoint, String jwt)
    {
        super(endpoint.replace("{hostedPaymentRequestId}", hostedPaymentRequestId).replace("{hostedPaymentId}", hostedPaymentId));
        this.jwt = jwt;
        this.body = """
                        {
                          "institutionId": "mock-sandbox",
                          "institutionCountryCode": "GB"
                        }
                        """;
    }


    @Override
    public SubmitInstitutionResponse call() throws IOException
    {
        HttpPost request = new HttpPost(endpoint);
        oauthHeader(request, jwt);
        request.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));
        HTTPResponseHandler response = super.makeAPICall(request);
        return new SubmitInstitutionResponse(response);
    }
}
