package com.yapily.e2ejunit;

import static org.assertj.core.api.Assertions.assertThat;

import com.yapily.e2ejunit.api.service.hosted_payments.authorise.AuthoriseAPI;
import com.yapily.e2ejunit.api.service.hosted_payments.authorise.AuthoriseResponse;
import com.yapily.e2ejunit.api.service.hosted_payments.exchange_code.authorisation.AuthorisationAPI;
import com.yapily.e2ejunit.api.service.hosted_payments.exchange_code.authorisation.AuthorisationResponse;
import com.yapily.e2ejunit.api.service.hosted_payments.exchange_code.exchange.ExchangeCodeAPI;
import com.yapily.e2ejunit.api.service.hosted_payments.exchange_code.exchange.ExchangeCodeResponse;
import com.yapily.e2ejunit.api.service.hosted_payments.execute.ExecutePaymentRequestAPI;
import com.yapily.e2ejunit.api.service.hosted_payments.execute.ExecutePaymentRequestResponse;
import com.yapily.e2ejunit.api.service.hosted_payments.payment_info.GetPaymentRequestInfoAPI;
import com.yapily.e2ejunit.api.service.hosted_payments.payment_info.GetPaymentRequestInfoResponse;
import com.yapily.e2ejunit.api.service.hosted_payments.payment_request.CreatePaymentRequestAPI;
import com.yapily.e2ejunit.api.service.hosted_payments.payment_request.CreatePaymentRequestResponse;
import com.yapily.e2ejunit.api.service.hosted_payments.status.GetPaymentRequestStatusAPI;
import com.yapily.e2ejunit.api.service.hosted_payments.status.GetPaymentRequestStatusResponse;
import com.yapily.e2ejunit.api.service.hosted_payments.submit_institution.SubmitInstitutionAPI;
import com.yapily.e2ejunit.api.service.hosted_payments.submit_institution.SubmitInstitutionResponse;
import com.yapily.e2ejunit.api.service.iam.IAMGetAccessTokenAPI;
import com.yapily.e2ejunit.api.service.iam.IAMGetAccessTokenResponse;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class Test1 extends E2ETestBase
{
    public Test1()
    {
        loadTestConfiguration("test1Config.yaml");
    }


    @Test
    void testConfiguration()
    {
        assertThat(accountDetails.applicationId).isEqualTo("a95658fd-86a2-4af8-8d0a-b32705ae0a60");
        assertThat(accountDetails.applicationSecret).isEqualTo("W7AXkv2sZaKPqM83kDUwpPGDzHBPbJkg");
        assertThat(accountDetails.userId).isEqualTo("47db84f0-4584-452f-88e6-2fc99289a593");
        assertThat(accountDetails.applicationUserId).isEqualTo("user_0.46995763313053973");
        Map<String, String> iamEndpoints = iamService.endpoints;
        assertThat(iamEndpoints.get("accessToken")).isEqualTo("https://staging.iam.yapily.com/auth/realms/open-banking/protocol/openid-connect/token");
        Map<String, String> hostedEndpoints = hostedPaymentService.endpoints;
        assertThat(hostedEndpoints.get("paymentRequest")).isEqualTo("https://staging.iam.yapily.com/hosted/payment-requests");
        assertThat(hostedEndpoints.get("paymentInfo")).isEqualTo("https://staging.iam.yapily.com/hosted/ui/pis/payment-requests/{hostedPaymentRequestId}/info");
        assertThat(hostedEndpoints.get("submitInstitution")).isEqualTo("https://staging.iam.yapily.com/hosted/ui/pis/payment-requests/{hostedPaymentRequestId}/payments/{hostedPaymentId}/submit-institution");
        assertThat(hostedEndpoints.get("authorisePayment")).isEqualTo("https://staging.iam.yapily.com/hosted/ui/pis/payment-requests/{hostedPaymentRequestId}/payments/{hostedPaymentId}/authorise");
        assertThat(hostedEndpoints.get("executePayment")).isEqualTo("https://staging.iam.yapily.com/hosted/ui/pis/payment-requests/{hostedPaymentRequestId}/payments/{hostedPaymentId}/execute");
        assertThat(hostedEndpoints.get("exchangeCode")).isEqualTo("https://staging.iam.yapily.com/exchange-code");
        assertThat(hostedEndpoints.get("paymentStatus")).isEqualTo("https://staging.iam.yapily.com/hosted/ui/pis/payment-requests/{hostedPaymentRequestId}/payments/{hostedPaymentId}/status");
    }


    @Test
    void testRedirectPaymentFlow() throws IOException, InterruptedException
    {
        IAMGetAccessTokenAPI iamGetAccessTokenAPI = new IAMGetAccessTokenAPI(accountDetails.applicationId,
                        accountDetails.applicationSecret,
                        iamService.endpoints.get("accessToken"));
        IAMGetAccessTokenResponse iamResponse = iamGetAccessTokenAPI.call();
        assertThat(iamResponse.getAccessToken()).hasSizeGreaterThan(15);
        CreatePaymentRequestAPI paymentRequestAPI = new CreatePaymentRequestAPI(accountDetails.userId,
                        accountDetails.applicationUserId,
                        hostedPaymentService.endpoints.get("paymentRequest"),
                        iamResponse.getAccessToken());
        CreatePaymentRequestResponse createPaymentRequestResponse = paymentRequestAPI.call();
        assertThat(createPaymentRequestResponse.hostedAuthToken()).hasSizeGreaterThan(15);
        assertThat(createPaymentRequestResponse.hostedPaymentRequestId()).hasSizeGreaterThan(15);
        GetPaymentRequestInfoAPI getPaymentRequestInfoAPI = new GetPaymentRequestInfoAPI(createPaymentRequestResponse.hostedPaymentRequestId(),
                        hostedPaymentService.endpoints.get("paymentInfo"),
                        iamResponse.getAccessToken());
        GetPaymentRequestInfoResponse getPaymentRequestInfoResponse = getPaymentRequestInfoAPI.call();
        assertThat(getPaymentRequestInfoResponse.hostedPaymentId()).hasSizeGreaterThan(15);
        SubmitInstitutionAPI submitInstitutionAPI = new SubmitInstitutionAPI(createPaymentRequestResponse.hostedPaymentRequestId(),
                        getPaymentRequestInfoResponse.hostedPaymentId(),
                        hostedPaymentService.endpoints.get("submitInstitution"),
                        iamResponse.getAccessToken());
        SubmitInstitutionResponse submitInstitutionResponse = submitInstitutionAPI.call();
        assertThat(submitInstitutionResponse.hostedAuthToken()).hasSizeGreaterThan(15);
        assertThat(submitInstitutionResponse.hostedPaymentRequestId()).hasSizeGreaterThan(15);
        AuthoriseAPI authoriseAPI = new AuthoriseAPI(createPaymentRequestResponse.hostedPaymentRequestId(),
                        getPaymentRequestInfoResponse.hostedPaymentId(),
                        hostedPaymentService.endpoints.get("authorisePayment"),
                        iamResponse.getAccessToken());
        AuthoriseResponse authoriseResponse = authoriseAPI.call();
        assertThat(authoriseResponse.authorisationUrl()).hasSizeGreaterThan(15);
        AuthorisationAPI authorisationAPI = new AuthorisationAPI(authoriseResponse.authorisationUrl());
        AuthorisationResponse authorisationResponse = authorisationAPI.call();
        assertThat(authorisationResponse.location()).hasSizeGreaterThan(15);
        assertThat(authorisationResponse.code()).hasSizeGreaterThan(15);
        assertThat(authorisationResponse.idToken()).hasSizeGreaterThan(15);
        assertThat(authorisationResponse.state()).hasSizeGreaterThan(15);
        ExchangeCodeAPI exchangeCodeAPI = new ExchangeCodeAPI(authorisationResponse.code(),
                        authorisationResponse.idToken(),
                        authorisationResponse.state(),
                        hostedPaymentService.endpoints.get("exchangeCode"),
                        iamResponse.getAccessToken());
        ExchangeCodeResponse exchangeCodeResponse = exchangeCodeAPI.call();
        assertThat(exchangeCodeResponse.consentToken()).hasSizeGreaterThan(15);
        ExecutePaymentRequestAPI executePaymentRequestAPI = new ExecutePaymentRequestAPI(authorisationResponse.code(),
                        authorisationResponse.idToken(),
                        authorisationResponse.state(),
                        hostedPaymentService.endpoints.get("executePayment"),
                        iamResponse.getAccessToken());
        ExecutePaymentRequestResponse executePaymentRequestResponse = executePaymentRequestAPI.call();
        assertThat(executePaymentRequestResponse.authorisationUrl()).hasSizeGreaterThan(15);
        GetPaymentRequestStatusAPI getPaymentRequestStatusAPI = new GetPaymentRequestStatusAPI(createPaymentRequestResponse.hostedPaymentRequestId(),
                        getPaymentRequestInfoResponse.hostedPaymentId(),
                        hostedPaymentService.endpoints.get("paymentStatus"),
                        iamResponse.getAccessToken());
        GetPaymentRequestStatusResponse getPaymentRequestStatusResponse = getPaymentRequestStatusAPI.call();
        assertThat(getPaymentRequestStatusResponse.phases().contains("FINISHED")).isTrue();
    }
}
