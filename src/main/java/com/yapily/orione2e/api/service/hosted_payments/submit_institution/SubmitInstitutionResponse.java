package com.yapily.orione2e.api.service.hosted_payments.submit_institution;

import com.yapily.orione2e.api.HTTPResponse;
import com.yapily.orione2e.api.HTTPResponseHandler;
import java.util.Map;

public class SubmitInstitutionResponse implements HTTPResponse
{
    private final Map<String, Object> body;


    public SubmitInstitutionResponse(HTTPResponseHandler httpResponseHandler)
    {
        this.body = getResponseBody(httpResponseHandler);
    }


    public String hostedPaymentRequestId()
    {
        Map<String, Object> data = (Map<String, Object>)body.get("data");
        return (String)data.get("paymentRequestId");
    }
}
