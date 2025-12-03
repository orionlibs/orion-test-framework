package com.yapily.orione2e.api;

import java.net.URI;
import org.apache.hc.client5.http.protocol.RedirectStrategy;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.protocol.HttpContext;

public class NoRedirectStrategy implements RedirectStrategy
{
    @Override
    public boolean isRedirected(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) throws HttpException
    {
        return false;
    }


    @Override
    public URI getLocationURI(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) throws HttpException
    {
        return null;
    }


    @Override
    public boolean isRedirectAllowed(HttpHost currentTarget, HttpHost newTarget, HttpRequest redirect, HttpContext context)
    {
        return RedirectStrategy.super.isRedirectAllowed(currentTarget, newTarget, redirect, context);
    }
}
