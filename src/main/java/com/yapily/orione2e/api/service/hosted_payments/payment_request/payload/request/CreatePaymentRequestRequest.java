package com.yapily.orione2e.api.service.hosted_payments.payment_request.payload.request;

import java.util.List;
import lombok.Data;

@Data
public class CreatePaymentRequestRequest
{
    public String userId;
    public String applicationUserId;
    public InstitutionIdentifiers institutionIdentifiers;
    public UserSettings userSettings;
    public String redirectUrl;
    public PaymentRequestDetails paymentRequestDetails;


    @Data
    public static class InstitutionIdentifiers
    {
        public String institutionId;
        public String institutionCountryCode;
    }


    @Data
    public static class UserSettings
    {
        public String language;
        public String location;
    }


    @Data
    public static class PaymentRequestDetails
    {
        public String paymentIdempotencyId;
        public AmountDetails amountDetails;
        public String reference;
        public String contextType;
        public String type;
        public Payee payee;
        public Payer payer;
    }


    @Data
    public static class AmountDetails
    {
        public Long amountToPay;
        public String currency;
    }


    @Data
    public static class Payee
    {
        public String name;
        public List<AccountIdentifications> accountIdentifications;
    }


    @Data
    public static class Payer
    {
        public String name;
        public List<AccountIdentifications> accountIdentifications;
    }


    @Data
    public static class AccountIdentifications
    {
        public String type;
        public String identification;
    }
}
