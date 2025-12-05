package com.yapily.orione2e.extension.capture_traffic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.protocol.HttpContext;

public class RecordingRequestInterceptor implements HttpRequestInterceptor
{
    public static final String CTX_REQ_METHOD = "capture.request.method";
    public static final String CTX_REQ_URI = "capture.request.uri";
    public static final String CTX_REQ_BODY = "capture.request.body";
    private final int snippetMaxBytes;


    public RecordingRequestInterceptor(int snippetMaxBytes)
    {
        this.snippetMaxBytes = snippetMaxBytes;
    }


    @Override
    public void process(HttpRequest request, EntityDetails entity, HttpContext context) throws HttpException, IOException
    {
        try
        {
            String method = request.getMethod();
            String uri = request.getRequestUri(); // request.getRequestUri() available on HttpRequest
            context.setAttribute(CTX_REQ_METHOD, method);
            context.setAttribute(CTX_REQ_URI, uri);
            String snippet = "";
            // Only ClassicHttpRequest may have an entity we can buffer
            if(request instanceof ClassicHttpRequest)
            {
                ClassicHttpRequest classic = (ClassicHttpRequest)request;
                if(classic.getEntity() != null)
                {
                    byte[] bytes = EntityUtils.toByteArray(classic.getEntity()); // consumes original entity
                    ContentType ct = ContentType.APPLICATION_OCTET_STREAM;
                    if(classic.getEntity().getContentType() != null)
                    {
                        try
                        {
                            ct = ContentType.parse(classic.getEntity().getContentType());
                        }
                        catch(Exception ignored)
                        {
                        }
                    }
                    // replace with buffered entity so client still sends it
                    classic.setEntity(new ByteArrayEntity(bytes, ct));
                    int len = Math.min(bytes.length, snippetMaxBytes);
                    snippet = new String(bytes, 0, len, StandardCharsets.UTF_8);
                }
            }
            context.setAttribute(CTX_REQ_BODY, snippet);
        }
        catch(Exception e)
        {
            // best-effort: don't break the request pipeline
            context.setAttribute(CTX_REQ_BODY, "capture-failed:" + e.getMessage());
        }
    }
}
