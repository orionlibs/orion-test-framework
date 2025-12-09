package com.yapily.orione2e.model;

import com.yapily.orione2e.api.service.hosted_payments.authorise.AuthoriseAPI;
import com.yapily.orione2e.api.service.hosted_payments.authorise.payload.request.AuthoriseRequest;
import com.yapily.orione2e.api.service.hosted_payments.exchange_code.authorisation.AuthorisationAPI;
import com.yapily.orione2e.api.service.hosted_payments.exchange_code.exchange.ExchangeCodeAPI;
import com.yapily.orione2e.api.service.hosted_payments.exchange_code.exchange.payload.request.ExchangeCodeRequest;
import com.yapily.orione2e.api.service.hosted_payments.execute.ExecutePaymentRequestAPI;
import com.yapily.orione2e.api.service.hosted_payments.payment_info.GetPaymentRequestInfoAPI;
import com.yapily.orione2e.api.service.hosted_payments.payment_request.CreatePaymentRequestAPI;
import com.yapily.orione2e.api.service.hosted_payments.payment_request.payload.request.CreatePaymentRequestRequest;
import com.yapily.orione2e.api.service.hosted_payments.status.GetPaymentRequestStatusAPI;
import com.yapily.orione2e.api.service.hosted_payments.submit_institution.SubmitInstitutionAPI;
import com.yapily.orione2e.api.service.hosted_payments.submit_institution.payload.request.SubmitInstitutionRequest;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class HostedPaymentService
{
    public Map<String, String> endpoints;


    public CreatePaymentRequestAPI createPaymentRequestAPI(CreatePaymentRequestRequest req, String endpoint, String jwt)
    {
        return new CreatePaymentRequestAPI(req, endpoint, jwt);
    }


    public GetPaymentRequestInfoAPI getPaymentRequestInfoAPI(String paymentRequestId, String endpoint, String jwt)
    {
        return new GetPaymentRequestInfoAPI(paymentRequestId, endpoint, jwt);
    }


    public SubmitInstitutionAPI submitInstitutionAPI(SubmitInstitutionRequest req, String hostedPaymentRequestId, String hostedPaymentId, String endpoint, String jwt)
    {
        return new SubmitInstitutionAPI(req, hostedPaymentRequestId, hostedPaymentId, endpoint, jwt);
    }


    public AuthoriseAPI authoriseAPI(String hostedPaymentRequestId, String hostedPaymentId, AuthoriseRequest request, String endpoint, String jwt)
    {
        return new AuthoriseAPI(hostedPaymentRequestId, hostedPaymentId, request, endpoint, jwt);
    }


    public AuthorisationAPI authorisationAPI(String endpoint)
    {
        return new AuthorisationAPI(endpoint);
    }


    public ExchangeCodeAPI exchangeCodeAPI(ExchangeCodeRequest req, String endpoint, String jwt)
    {
        return new ExchangeCodeAPI(req, endpoint, jwt);
    }


    public ExecutePaymentRequestAPI executePaymentRequestAPI(String hostedPaymentRequestId, String hostedPaymentId, String endpoint, String jwt, String consentToken)
    {
        return new ExecutePaymentRequestAPI(hostedPaymentRequestId, hostedPaymentId, endpoint, jwt, consentToken);
    }


    public GetPaymentRequestStatusAPI getPaymentRequestStatusAPI(String hostedPaymentRequestId, String hostedPaymentId, String endpoint, String jwt)
    {
        return new GetPaymentRequestStatusAPI(hostedPaymentRequestId, hostedPaymentId, endpoint, jwt);
    }
}
