package com.yapily.orione2e.api.service.hosted_payments.exchange_code.exchange.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ExchangeCodeRequest
{
    public String code;
    @JsonProperty(value = "id_token")
    public String idToken;
    public String state;
}
