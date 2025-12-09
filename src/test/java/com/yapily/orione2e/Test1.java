package com.yapily.orione2e;

import static org.assertj.core.api.Assertions.assertThat;

import com.yapily.orione2e.api.service.hosted_payments.authorise.payload.request.AuthoriseRequest;
import com.yapily.orione2e.api.service.hosted_payments.authorise.payload.response.AuthoriseResponse;
import com.yapily.orione2e.api.service.hosted_payments.exchange_code.authorisation.payload.response.AuthorisationResponse;
import com.yapily.orione2e.api.service.hosted_payments.exchange_code.exchange.payload.request.ExchangeCodeRequest;
import com.yapily.orione2e.api.service.hosted_payments.exchange_code.exchange.payload.response.ExchangeCodeResponse;
import com.yapily.orione2e.api.service.hosted_payments.execute.payload.response.ExecutePaymentRequestResponse;
import com.yapily.orione2e.api.service.hosted_payments.payment_info.payload.response.GetPaymentRequestInfoResponse;
import com.yapily.orione2e.api.service.hosted_payments.payment_request.payload.request.CreatePaymentRequestRequest;
import com.yapily.orione2e.api.service.hosted_payments.payment_request.payload.response.CreatePaymentRequestResponse;
import com.yapily.orione2e.api.service.hosted_payments.status.payload.response.GetPaymentRequestStatusResponse;
import com.yapily.orione2e.api.service.hosted_payments.submit_institution.payload.request.SubmitInstitutionRequest;
import com.yapily.orione2e.api.service.hosted_payments.submit_institution.payload.response.SubmitInstitutionResponse;
import com.yapily.orione2e.api.service.iam.payload.response.IAMGetAccessTokenResponse;
import com.yapily.orione2e.extension.failfast.FailFast;
import com.yapily.orione2e.extension.failfast.FailFastExtension;
import com.yapily.orione2e.extension.lifecycle.AfterEachTestExecutionListener;
import com.yapily.orione2e.extension.lifecycle.BeforeEachTestExecutionListener;
import com.yapily.orione2e.extension.requires_resource.RequiresResource;
import com.yapily.orione2e.extension.requires_resource.ResourceExecutionCondition;
import com.yapily.orione2e.extension.retry.RetryExtension;
import com.yapily.orione2e.utils.AssertionUtils;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BeforeEachTestExecutionListener.class)
@ExtendWith(AfterEachTestExecutionListener.class)
@ExtendWith(RetryExtension.class)
@ExtendWith(ResourceExecutionCondition.class)
@ExtendWith(FailFastExtension.class)
@FailFast("critical connectivity check")
class Test1 extends E2ETestBase
{
    public Test1()
    {
        loadTestConfiguration("test1Config.yaml");
    }


    @Test
    @Scenario(description = "success scenario", tags = {"configuration", "success"})
    void testConfiguration()
    {
        AssertionUtils.assertPublicInstanceFieldValuesContainExactly(accountDetails,
                        "a95658fd-86a2-4af8-8d0a-b32705ae0a60",
                        "W7AXkv2sZaKPqM83kDUwpPGDzHBPbJkg",
                        "47db84f0-4584-452f-88e6-2fc99289a593",
                        "user_0.46995763313053973");
        Map<String, String> iamEndpoints = iamService.endpoints;
        assertThat(iamEndpoints.get("accessToken")).isEqualTo("https://staging.iam.yapily.com/auth/realms/open-banking/protocol/openid-connect/token");
        Map<String, String> hostedEndpoints = hostedPaymentService.endpoints;
        AssertionUtils.assertMapValuesContainExactly(hostedEndpoints,
                        "https://staging-api.yapily.com/hosted/payment-requests",
                        "https://staging-api.yapily.com/hosted/ui/pis/payment-requests/{hostedPaymentRequestId}/info",
                        "https://staging-api.yapily.com/hosted/ui/pis/payment-requests/{hostedPaymentRequestId}/payments/{hostedPaymentId}/submit-institution",
                        "https://staging-api.yapily.com/hosted/ui/pis/payment-requests/{hostedPaymentRequestId}/payments/{hostedPaymentId}/authorise",
                        "https://staging-api.yapily.com/hosted/ui/pis/payment-requests/{hostedPaymentRequestId}/payments/{hostedPaymentId}/execute",
                        "https://staging-api.yapily.com/exchange-code",
                        "https://staging-api.yapily.com/hosted/ui/pis/payment-requests/{hostedPaymentRequestId}/payments/{hostedPaymentId}/status");
    }


    @Test
    @Scenario(description = "success scenario", tags = {"simulation", "success"})
    //@Retry(attempts = 3, delayMs = 200)
    @RequiresResource(host = "https://staging.iam.yapily.com", port = 443, timeoutMs = 300)
    @RequiresResource(host = "https://staging-api.yapily.com", port = 443, timeoutMs = 300)
    void testRedirectPaymentFlow() throws IOException, InterruptedException
    {
        IAMGetAccessTokenResponse iamResponse = iamService.getAccessTokenAPI(accountDetails.applicationId,
                                        accountDetails.applicationSecret,
                                        iamService.endpoints.get("accessToken"))
                        .call();
        assertThat(iamResponse.getAccessToken()).hasSizeGreaterThan(15);
        CreatePaymentRequestRequest request1 = new CreatePaymentRequestRequest();
        request1.setUserId(accountDetails.userId);
        request1.setApplicationUserId(accountDetails.applicationUserId);
        CreatePaymentRequestRequest.InstitutionIdentifiers institutionIdentifiers = new CreatePaymentRequestRequest.InstitutionIdentifiers();
        institutionIdentifiers.setInstitutionId("mock-sandbox");
        institutionIdentifiers.setInstitutionCountryCode("GB");
        request1.setInstitutionIdentifiers(institutionIdentifiers);
        CreatePaymentRequestRequest.UserSettings userSettings = new CreatePaymentRequestRequest.UserSettings();
        userSettings.setLanguage("EN");
        userSettings.setLocation("GB");
        request1.setUserSettings(userSettings);
        request1.setRedirectUrl("https://tpp-application.com/");
        CreatePaymentRequestRequest.AmountDetails amountDetails = new CreatePaymentRequestRequest.AmountDetails();
        amountDetails.setAmountToPay(1L);
        amountDetails.setCurrency("GBP");
        CreatePaymentRequestRequest.AccountIdentifications accountIdentifications1 = new CreatePaymentRequestRequest.AccountIdentifications();
        accountIdentifications1.setIdentification("123456");
        accountIdentifications1.setType("SORT_CODE");
        CreatePaymentRequestRequest.AccountIdentifications accountIdentifications2 = new CreatePaymentRequestRequest.AccountIdentifications();
        accountIdentifications2.setIdentification("12345678");
        accountIdentifications2.setType("ACCOUNT_NUMBER");
        CreatePaymentRequestRequest.Payee payee = new CreatePaymentRequestRequest.Payee();
        payee.setName("Jane Doe");
        payee.setAccountIdentifications(List.of(accountIdentifications1, accountIdentifications2));
        CreatePaymentRequestRequest.AccountIdentifications accountIdentifications3 = new CreatePaymentRequestRequest.AccountIdentifications();
        accountIdentifications3.setIdentification("121212");
        accountIdentifications3.setType("SORT_CODE");
        CreatePaymentRequestRequest.AccountIdentifications accountIdentifications4 = new CreatePaymentRequestRequest.AccountIdentifications();
        accountIdentifications4.setIdentification("87654321");
        accountIdentifications4.setType("ACCOUNT_NUMBER");
        CreatePaymentRequestRequest.Payer payer = new CreatePaymentRequestRequest.Payer();
        payer.setName("John Doe");
        payer.setAccountIdentifications(List.of(accountIdentifications3, accountIdentifications4));
        CreatePaymentRequestRequest.PaymentRequestDetails paymentRequestDetails = new CreatePaymentRequestRequest.PaymentRequestDetails();
        paymentRequestDetails.setPaymentIdempotencyId(UUID.randomUUID().toString().replace("-", ""));
        paymentRequestDetails.setReference("Test Payment");
        paymentRequestDetails.setContextType("OTHER");
        paymentRequestDetails.setType("DOMESTIC_PAYMENT");
        paymentRequestDetails.setAmountDetails(amountDetails);
        paymentRequestDetails.setPayee(payee);
        paymentRequestDetails.setPayer(payer);
        request1.setPaymentRequestDetails(paymentRequestDetails);
        CreatePaymentRequestResponse createPaymentRequestResponse = hostedPaymentService.createPaymentRequestAPI(request1,
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
        SubmitInstitutionRequest request2 = new SubmitInstitutionRequest();
        request2.setInstitutionId("mock-sandbox");
        request2.setInstitutionCountryCode("GB");
        SubmitInstitutionResponse submitInstitutionResponse = hostedPaymentService.submitInstitutionAPI(request2,
                                        createPaymentRequestResponse.hostedPaymentRequestId(),
                                        getPaymentRequestInfoResponse.hostedPaymentId(),
                                        hostedPaymentService.endpoints.get("submitInstitution"),
                                        createPaymentRequestResponse.hostedAuthToken())
                        .call();
        assertThat(submitInstitutionResponse.hostedPaymentRequestId()).hasSizeGreaterThan(15);
        AuthoriseRequest request3 = new AuthoriseRequest();
        request3.setHostedAuthRedirect("https://prototypes.yapily.com/auth-link2.html");
        AuthoriseResponse authoriseResponse = hostedPaymentService.authoriseAPI(createPaymentRequestResponse.hostedPaymentRequestId(),
                                        getPaymentRequestInfoResponse.hostedPaymentId(),
                                        request3,
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
        ExchangeCodeRequest request4 = new ExchangeCodeRequest();
        request4.setCode(authorisationResponse.code());
        request4.setIdToken(authorisationResponse.idToken());
        request4.setState(authorisationResponse.state());
        ExchangeCodeResponse exchangeCodeResponse = hostedPaymentService.exchangeCodeAPI(request4,
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
