package com.yapily.orione2e.mock_server;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.yapily.orione2e.E2ETestBase;
import com.yapily.orione2e.extension.mock_server.MockServerExtension;
import com.yapily.orione2e.extension.mock_server.MockServerUrl;
import com.yapily.orione2e.extension.mock_server.WithMockServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

//@WithMockServer(setup = ExampleStubs.class)
@ExtendWith(MockServerExtension.class)
public class MockServerTest extends E2ETestBase
{
    @Test
    @WithMockServer
    void testWithFreshServer(WireMockServer server, @MockServerUrl String baseUrl)
    {
        server.start();
        server.stop();
    }
}
