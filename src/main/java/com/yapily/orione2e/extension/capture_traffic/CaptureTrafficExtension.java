package com.yapily.orione2e.extension.capture_traffic;

import com.yapily.orione2e.extension.capture_traffic.ApacheHttpTrafficRecorder.Exchange;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CaptureTrafficExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver
{
    private static final Logger log = LoggerFactory.getLogger(CaptureTrafficExtension.class);
    private static final ExtensionContext.Namespace NS = ExtensionContext.Namespace.create(CaptureTrafficExtension.class);
    private static final String RECORDER_KEY = "apache-recorder";
    private static final String CLIENT_KEY = "apache-client";


    @Override
    public void beforeEach(ExtensionContext context) throws Exception
    {
        CaptureTraffic cfg = findAnnotation(context);
        if(cfg == null)
        {
            return;
        }
        int snippetMaxBytes = cfg.snippetMaxBytes();
        ApacheHttpTrafficRecorder recorder = new ApacheHttpTrafficRecorder();
        CloseableHttpClient client = HttpClientsWithRecorder.createRecordingClient(recorder, snippetMaxBytes);
        getStore(context).put(RECORDER_KEY, recorder);
        getStore(context).put(CLIENT_KEY, client);
        log.info("[CaptureTraffic] started recorder and client for test: {}",
                        context.getDisplayName());
    }


    @Override
    public void afterEach(ExtensionContext context) throws Exception
    {
        CaptureTraffic cfg = findAnnotation(context);
        if(cfg == null)
        {
            return;
        }
        // close client
        CloseableHttpClient client = getStore(context).remove(CLIENT_KEY, CloseableHttpClient.class);
        if(client != null)
        {
            try
            {
                client.close();
            }
            catch(Exception e)
            {
                log.warn("Failed to close recording client", e);
            }
        }
        ApacheHttpTrafficRecorder recorder = getStore(context).remove(RECORDER_KEY, ApacheHttpTrafficRecorder.class);
        if(recorder != null)
        {
            List<Exchange> ex = recorder.getExchanges();
            log.info("[CaptureTraffic] {} exchanges recorded for test {}", ex.size(), context.getDisplayName());
            for(ApacheHttpTrafficRecorder.Exchange e : ex)
            {
                log.info("Exchange: {}", e);
            }
        }
    }


    // ParameterResolver
    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException
    {
        Class<?> t = parameterContext.getParameter().getType();
        if(ApacheHttpTrafficRecorder.class.equals(t))
        {
            return true;
        }
        if(CloseableHttpClient.class.equals(t))
        {
            return true;
        }
        return false;
    }


    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException
    {
        Class<?> t = parameterContext.getParameter().getType();
        if(ApacheHttpTrafficRecorder.class.equals(t))
        {
            ApacheHttpTrafficRecorder r = getStore(extensionContext).get(RECORDER_KEY, ApacheHttpTrafficRecorder.class);
            if(r == null)
            {
                throw new ParameterResolutionException("No ApacheHttpTrafficRecorder available - is @CaptureTraffic present?");
            }
            return r;
        }
        if(CloseableHttpClient.class.equals(t))
        {
            CloseableHttpClient c = getStore(extensionContext).get(CLIENT_KEY, CloseableHttpClient.class);
            if(c == null)
            {
                throw new ParameterResolutionException("No CloseableHttpClient available - is @CaptureTraffic present?");
            }
            return c;
        }
        throw new ParameterResolutionException("Unsupported parameter type: " + t);
    }


    // helpers
    private ExtensionContext.Store getStore(ExtensionContext ctx)
    {
        return ctx.getStore(NS);
    }


    private CaptureTraffic findAnnotation(ExtensionContext ctx)
    {
        Optional<Method> m = ctx.getTestMethod();
        if(m.isPresent())
        {
            CaptureTraffic at = m.get().getAnnotation(CaptureTraffic.class);
            if(at != null)
            {
                return at;
            }
        }
        return ctx.getTestClass().map(c -> c.getAnnotation(CaptureTraffic.class)).orElse(null);
    }
}
