package com.yapily.e2ejunit;

import com.yapily.e2ejunit.model.AccountDetails;
import com.yapily.e2ejunit.model.Configuration;
import com.yapily.e2ejunit.model.Configuration.Service;
import com.yapily.e2ejunit.model.HostedPaymentService;
import com.yapily.e2ejunit.model.HttpClientConfiguration;
import com.yapily.e2ejunit.model.IAMService;
import java.io.InputStream;
import java.util.Map;
import tools.jackson.dataformat.yaml.YAMLMapper;

public class E2ETestBase
{
    protected AccountDetails accountDetails;
    protected IAMService iamService;
    protected HostedPaymentService hostedPaymentService;
    YAMLMapper yamlMapper;


    public E2ETestBase()
    {
        yamlMapper = YAMLMapper.builder().build();
    }


    protected void loadTestConfiguration(String configurationFileName)
    {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configurationFileName);
        Configuration configuration = yamlMapper.readValue(inputStream, Configuration.class);
        HttpClientConfiguration.connectionTimeout = configuration.httpClient.get("connectionTimeout");
        HttpClientConfiguration.responseTimeout = configuration.httpClient.get("responseTimeout");
        accountDetails = AccountDetails.builder()
                        .applicationId(configuration.account.get("applicationId"))
                        .applicationSecret(configuration.account.get("applicationSecret"))
                        .userId(configuration.account.get("userId"))
                        .applicationUserId(configuration.account.get("applicationUserId"))
                        .build();
        Service iamServiceTemp = configuration.services.get("iam");
        iamService = IAMService.builder()
                        .endpoints(iamServiceTemp.endpoints)
                        .build();
        for(Map.Entry<String, String> endpoint : iamService.endpoints.entrySet())
        {
            endpoint.setValue(iamServiceTemp.host.concat(endpoint.getValue()));
        }
        Service hostedPaymentServiceTemp = configuration.services.get("hostedPayments");
        hostedPaymentService = HostedPaymentService.builder()
                        .endpoints(hostedPaymentServiceTemp.endpoints)
                        .build();
        for(Map.Entry<String, String> endpoint : hostedPaymentService.endpoints.entrySet())
        {
            endpoint.setValue(hostedPaymentServiceTemp.host.concat(endpoint.getValue()));
        }
    }
}
