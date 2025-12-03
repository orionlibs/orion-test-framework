package com.yapily.orione2e.model;

import com.yapily.orione2e.api.service.iam.IAMGetAccessTokenAPI;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IAMService
{
    public Map<String, String> endpoints;


    public IAMGetAccessTokenAPI getAccessTokenAPI(String applicationId, String applicationSecret, String endpoint)
    {
        return new IAMGetAccessTokenAPI(applicationId, applicationSecret, endpoint);
    }
}
