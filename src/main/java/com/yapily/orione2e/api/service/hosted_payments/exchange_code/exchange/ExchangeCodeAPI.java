package com.yapily.orione2e.api.service.hosted_payments.exchange_code.exchange;

import com.yapily.orione2e.api.APICall;
import com.yapily.orione2e.api.HTTPResponseHandler;
import com.yapily.orione2e.api.service.hosted_payments.exchange_code.exchange.payload.request.ExchangeCodeRequest;
import com.yapily.orione2e.api.service.hosted_payments.exchange_code.exchange.payload.response.ExchangeCodeResponse;
import com.yapily.orione2e.utils.JSONUtils;
import java.io.IOException;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class ExchangeCodeAPI extends APICall
{
    ExchangeCodeRequest req;
    String jwt;


    public ExchangeCodeAPI(ExchangeCodeRequest req, String endpoint, String jwt)
    {
        super(endpoint);
        this.jwt = jwt;
        this.req = req;
    }


    @Override
    public ExchangeCodeResponse call() throws IOException
    {
        HttpPost request = new HttpPost(endpoint);
        oauthHeader(request, jwt);
        request.setEntity(new StringEntity(JSONUtils.toJSON(req), ContentType.APPLICATION_JSON));
        HTTPResponseHandler response = super.makeAPICall(request);
        return new ExchangeCodeResponse(response);
    }
}
