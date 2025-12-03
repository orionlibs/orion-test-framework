package com.yapily.orione2e.mock_server;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.yapily.orione2e.extension.mock_server.MockServerSetup;

public class ExampleStubs implements MockServerSetup
{
    @Override
    public void setup(WireMockServer server)
    {
        server.stubFor(get(urlEqualTo("/health"))
                        .willReturn(aResponse().withStatus(200).withBody("ok")));
        server.stubFor(post(urlEqualTo("/login"))
                        .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                                        .withBody("{\"token\":\"abc\"}")));
    }
}
