package com.yapily.orione2e.extension.capture_traffic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpResponseInterceptor;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.protocol.HttpContext;

public class RecordingResponseInterceptor implements HttpResponseInterceptor
{
    private final ApacheHttpTrafficRecorder recorder;
    private final int snippetMaxBytes;


    public RecordingResponseInterceptor(ApacheHttpTrafficRecorder recorder, int snippetMaxBytes)
    {
        this.recorder = recorder;
        this.snippetMaxBytes = snippetMaxBytes;
    }


    @Override
    public void process(HttpResponse response, EntityDetails entity, HttpContext context) throws HttpException, IOException
    {
        try
        {
            String method = Optional.ofNullable(context.getAttribute(RecordingRequestInterceptor.CTX_REQ_METHOD))
                            .map(Object::toString).orElse("UNKNOWN");
            String uri = Optional.ofNullable(context.getAttribute(RecordingRequestInterceptor.CTX_REQ_URI))
                            .map(Object::toString).orElse("UNKNOWN");
            String reqSnippet = Optional.ofNullable(context.getAttribute(RecordingRequestInterceptor.CTX_REQ_BODY))
                            .map(Object::toString).orElse("");
            int status = -1;
            String respSnippet = "";
            if(response instanceof ClassicHttpResponse)
            {
                ClassicHttpResponse classicResp = (ClassicHttpResponse)response;
                if(classicResp.getCode() > 0)
                {
                    status = classicResp.getCode();
                }
                if(classicResp.getEntity() != null)
                {
                    byte[] bytes = EntityUtils.toByteArray(classicResp.getEntity()); // consumes
                    ContentType ct = ContentType.APPLICATION_OCTET_STREAM;
                    if(classicResp.getEntity().getContentType() != null)
                    {
                        try
                        {
                            ct = ContentType.parse(classicResp.getEntity().getContentType());
                        }
                        catch(Exception ignored)
                        {
                        }
                    }
                    // replace buffered entity for client code
                    classicResp.setEntity(new ByteArrayEntity(bytes, ct));
                    int len = Math.min(bytes.length, snippetMaxBytes);
                    respSnippet = new String(bytes, 0, len, StandardCharsets.UTF_8);
                }
            }
            recorder.add(new ApacheHttpTrafficRecorder.Exchange(method, uri, status, reqSnippet, respSnippet));
        }
        catch(Exception e)
        {
            recorder.add(new ApacheHttpTrafficRecorder.Exchange(
                            Optional.ofNullable(context.getAttribute(RecordingRequestInterceptor.CTX_REQ_METHOD)).map(Object::toString).orElse("UNK"),
                            Optional.ofNullable(context.getAttribute(RecordingRequestInterceptor.CTX_REQ_URI)).map(Object::toString).orElse("UNK"),
                            -1,
                            "capture-failed",
                            e.getMessage()
            ));
        }
    }
}
