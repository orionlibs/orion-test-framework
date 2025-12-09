package com.yapily.orione2e.api.service.applications;

import com.yapily.orione2e.api.APICall;
import com.yapily.orione2e.api.HTTPResponseHandler;
import com.yapily.orione2e.api.service.applications.payload.request.CreateSubapplicationRequest;
import com.yapily.orione2e.api.service.applications.payload.response.CreateSubapplicationResponse;
import com.yapily.orione2e.utils.JSONUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class CreateSubapplicationAPI extends APICall
{
    private String basicAuth;
    private CreateSubapplicationRequest req;


    public CreateSubapplicationAPI(CreateSubapplicationRequest req, String applicationId, String applicationSecret, String endpoint)
    {
        super(endpoint);
        this.req = req;
        this.basicAuth = applicationId + ":" + applicationSecret;
    }


    @Override
    public CreateSubapplicationResponse call() throws IOException
    {
        String encodedAuth = Base64.getEncoder().encodeToString(basicAuth.getBytes(StandardCharsets.UTF_8));
        HttpPost request = new HttpPost(endpoint);
        basicAuthHeader(request, encodedAuth);
        request.setEntity(new StringEntity(JSONUtils.toJSON(req), ContentType.APPLICATION_FORM_URLENCODED));
        HTTPResponseHandler response = super.makeAPICall(request);
        return new CreateSubapplicationResponse(response);
    }
}
