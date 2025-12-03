package com.yapily.orione2e.extension.mock_server;

import com.github.tomakehurst.wiremock.WireMockServer;

public interface MockServerSetup
{
    void setup(WireMockServer server) throws Exception;
}
