package com.yapily.orione2e.api.service.hosted_payments.payment_request;

import com.yapily.orione2e.api.APICall;
import com.yapily.orione2e.api.HTTPResponseHandler;
import com.yapily.orione2e.api.service.hosted_payments.payment_request.payload.response.CreatePaymentRequestResponse;
import java.io.IOException;
import java.util.UUID;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class CreatePaymentRequestAPI extends APICall
{
    String body;
    String jwt;


    public CreatePaymentRequestAPI(String userId, String applicationUserId, String endpoint, String jwt)
    {
        super(endpoint);
        this.jwt = jwt;
        String paymentIdempotencyId = UUID.randomUUID().toString().replace("-", "");
        this.body = """
                        {
                          "userId": "%s",
                          "applicationUserId": "%s",
                          "institutionIdentifiers": {
                            "institutionId": "mock-sandbox",
                            "institutionCountryCode": "GB"
                          },
                          "userSettings": {
                            "language": "EN",
                            "location": "GB"
                          },
                          "redirectUrl": "https://tpp-application.com/",
                          "paymentRequestDetails": {
                            "paymentIdempotencyId": "%s",
                            "amountDetails": {
                              "amountToPay": 1,
                              "currency": "GBP"
                            },
                            "reference": "Test Payment",
                            "contextType": "OTHER",
                            "type": "DOMESTIC_PAYMENT",
                            "payee": {
                              "name": "Jane Doe",
                              "accountIdentifications": [
                                {
                                  "type": "SORT_CODE",
                                  "identification": "123456"
                                },
                                {
                                  "type": "ACCOUNT_NUMBER",
                                  "identification": "12345678"
                                }
                              ]
                            },
                            "payer": {
                              "name": "John Doe",
                              "accountIdentifications": [
                                {
                                  "type": "SORT_CODE",
                                  "identification": "121212"
                                },
                                {
                                  "type": "ACCOUNT_NUMBER",
                                  "identification": "87654321"
                                }
                              ]
                            }
                          }
                        }
                        """.formatted(userId, applicationUserId, paymentIdempotencyId);
    }


    @Override
    public CreatePaymentRequestResponse call() throws IOException
    {
        HttpPost request = new HttpPost(endpoint);
        oauthHeader(request, jwt);
        request.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));
        HTTPResponseHandler response = super.makeAPICall(request);
        return new CreatePaymentRequestResponse(response);
    }
}
