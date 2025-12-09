package com.yapily.orione2e.api.service.hosted_payments.submit_institution;

import com.yapily.orione2e.api.APICall;
import com.yapily.orione2e.api.HTTPResponseHandler;
import com.yapily.orione2e.api.service.hosted_payments.submit_institution.payload.request.SubmitInstitutionRequest;
import com.yapily.orione2e.api.service.hosted_payments.submit_institution.payload.response.SubmitInstitutionResponse;
import com.yapily.orione2e.utils.JSONUtils;
import java.io.IOException;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class SubmitInstitutionAPI extends APICall
{
    SubmitInstitutionRequest req;
    String jwt;


    public SubmitInstitutionAPI(SubmitInstitutionRequest req, String hostedPaymentRequestId, String hostedPaymentId, String endpoint, String jwt)
    {
        super(endpoint.replace("{hostedPaymentRequestId}", hostedPaymentRequestId).replace("{hostedPaymentId}", hostedPaymentId));
        this.jwt = jwt;
        this.req = req;
    }


    @Override
    public SubmitInstitutionResponse call() throws IOException
    {
        HttpPost request = new HttpPost(endpoint);
        oauthHeader(request, jwt);
        request.setEntity(new StringEntity(JSONUtils.toJSON(req), ContentType.APPLICATION_JSON));
        HTTPResponseHandler response = super.makeAPICall(request);
        return new SubmitInstitutionResponse(response);
    }
}
