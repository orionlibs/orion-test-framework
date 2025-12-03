package com.yapily.orione2e;

import static org.assertj.core.api.Assertions.assertThat;

import com.yapily.orione2e.api.service.hosted_payments.authorise.AuthoriseResponse;
import com.yapily.orione2e.api.service.hosted_payments.exchange_code.authorisation.AuthorisationResponse;
import com.yapily.orione2e.api.service.hosted_payments.exchange_code.exchange.ExchangeCodeResponse;
import com.yapily.orione2e.api.service.hosted_payments.execute.ExecutePaymentRequestResponse;
import com.yapily.orione2e.api.service.hosted_payments.payment_info.GetPaymentRequestInfoResponse;
import com.yapily.orione2e.api.service.hosted_payments.payment_request.CreatePaymentRequestResponse;
import com.yapily.orione2e.api.service.hosted_payments.status.GetPaymentRequestStatusResponse;
import com.yapily.orione2e.api.service.hosted_payments.submit_institution.SubmitInstitutionResponse;
import com.yapily.orione2e.api.service.iam.IAMGetAccessTokenResponse;
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
        assertThat(hostedEndpoints.get("paymentRequest")).isEqualTo("https://staging-api.yapily.com/hosted/payment-requests");
        assertThat(hostedEndpoints.get("paymentInfo")).isEqualTo("https://staging-api.yapily.com/hosted/ui/pis/payment-requests/{hostedPaymentRequestId}/info");
        assertThat(hostedEndpoints.get("submitInstitution")).isEqualTo("https://staging-api.yapily.com/hosted/ui/pis/payment-requests/{hostedPaymentRequestId}/payments/{hostedPaymentId}/submit-institution");
        assertThat(hostedEndpoints.get("authorisePayment")).isEqualTo("https://staging-api.yapily.com/hosted/ui/pis/payment-requests/{hostedPaymentRequestId}/payments/{hostedPaymentId}/authorise");
        assertThat(hostedEndpoints.get("executePayment")).isEqualTo("https://staging-api.yapily.com/hosted/ui/pis/payment-requests/{hostedPaymentRequestId}/payments/{hostedPaymentId}/execute");
        assertThat(hostedEndpoints.get("exchangeCode")).isEqualTo("https://staging-api.yapily.com/exchange-code");
        assertThat(hostedEndpoints.get("paymentStatus")).isEqualTo("https://staging-api.yapily.com/hosted/ui/pis/payment-requests/{hostedPaymentRequestId}/payments/{hostedPaymentId}/status");
    }


    @Test
    void testRedirectPaymentFlow() throws IOException, InterruptedException
    {
        IAMGetAccessTokenResponse iamResponse = iamService.getAccessTokenAPI(accountDetails.applicationId,
                                        accountDetails.applicationSecret,
                                        iamService.endpoints.get("accessToken"))
                        .call();
        assertThat(iamResponse.getAccessToken()).hasSizeGreaterThan(15);
        CreatePaymentRequestResponse createPaymentRequestResponse = hostedPaymentService.createPaymentRequestAPI(accountDetails.userId,
                                        accountDetails.applicationUserId,
                                        hostedPaymentService.endpoints.get("paymentRequest"),
                                        iamResponse.getAccessToken())
                        .call();
        assertThat(createPaymentRequestResponse.hostedAuthToken()).hasSizeGreaterThan(15);
        assertThat(createPaymentRequestResponse.hostedPaymentRequestId()).hasSizeGreaterThan(15);
        GetPaymentRequestInfoResponse getPaymentRequestInfoResponse = hostedPaymentService.getPaymentRequestInfoAPI(createPaymentRequestResponse.hostedPaymentRequestId(),
                                        hostedPaymentService.endpoints.get("paymentInfo"),
                                        createPaymentRequestResponse.hostedAuthToken())
                        .call();
        assertThat(getPaymentRequestInfoResponse.hostedPaymentId()).hasSizeGreaterThan(15);
        SubmitInstitutionResponse submitInstitutionResponse = hostedPaymentService.submitInstitutionAPI(createPaymentRequestResponse.hostedPaymentRequestId(),
                                        getPaymentRequestInfoResponse.hostedPaymentId(),
                                        hostedPaymentService.endpoints.get("submitInstitution"),
                                        createPaymentRequestResponse.hostedAuthToken())
                        .call();
        assertThat(submitInstitutionResponse.hostedPaymentRequestId()).hasSizeGreaterThan(15);
        AuthoriseResponse authoriseResponse = hostedPaymentService.authoriseAPI(createPaymentRequestResponse.hostedPaymentRequestId(),
                                        getPaymentRequestInfoResponse.hostedPaymentId(),
                                        hostedPaymentService.endpoints.get("authorisePayment"),
                                        createPaymentRequestResponse.hostedAuthToken())
                        .call();
        assertThat(authoriseResponse.authorisationUrl()).hasSizeGreaterThan(15);
        AuthorisationResponse authorisationResponse = hostedPaymentService.authorisationAPI(authoriseResponse.authorisationUrl())
                        .call();
        assertThat(authorisationResponse.location()).hasSizeGreaterThan(15);
        assertThat(authorisationResponse.code()).hasSizeGreaterThan(15);
        assertThat(authorisationResponse.idToken()).hasSizeGreaterThan(15);
        assertThat(authorisationResponse.state()).hasSizeGreaterThan(15);
        ExchangeCodeResponse exchangeCodeResponse = hostedPaymentService.exchangeCodeAPI(authorisationResponse.code(),
                                        authorisationResponse.idToken(),
                                        authorisationResponse.state(),
                                        hostedPaymentService.endpoints.get("exchangeCode"),
                                        iamResponse.getAccessToken())
                        .call();
        assertThat(exchangeCodeResponse.consentToken()).hasSizeGreaterThan(15);
        ExecutePaymentRequestResponse executePaymentRequestResponse = hostedPaymentService.executePaymentRequestAPI(authorisationResponse.code(),
                                        authorisationResponse.idToken(),
                                        authorisationResponse.state(),
                                        hostedPaymentService.endpoints.get("executePayment"),
                                        iamResponse.getAccessToken())
                        .call();
        assertThat(executePaymentRequestResponse.authorisationUrl()).hasSizeGreaterThan(15);
        GetPaymentRequestStatusResponse getPaymentRequestStatusResponse = hostedPaymentService.getPaymentRequestStatusAPI(createPaymentRequestResponse.hostedPaymentRequestId(),
                                        getPaymentRequestInfoResponse.hostedPaymentId(),
                                        hostedPaymentService.endpoints.get("paymentStatus"),
                                        iamResponse.getAccessToken())
                        .call();
        assertThat(getPaymentRequestStatusResponse.phases().contains("FINISHED")).isTrue();
    }
}
