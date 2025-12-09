package com.yapily.orione2e.model;

import com.yapily.orione2e.api.service.applications.CreateSubapplicationAPI;
import com.yapily.orione2e.api.service.applications.payload.request.CreateSubapplicationRequest;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationService
{
    public Map<String, String> endpoints;


    public CreateSubapplicationAPI getCreateSubapplicationAPI(CreateSubapplicationRequest req, String applicationId, String applicationSecret, String endpoint)
    {
        return new CreateSubapplicationAPI(req, applicationId, applicationSecret, endpoint);
    }
}
