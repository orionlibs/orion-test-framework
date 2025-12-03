package com.yapily.orione2e.extension.mock_server;

import com.github.tomakehurst.wiremock.WireMockServer;

public class NoOpMockServerSetup implements MockServerSetup
{
    @Override
    public void setup(WireMockServer server) throws Exception
    {
        //no-op
    }
}
