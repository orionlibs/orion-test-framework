package com.yapily.orione2e.extension.capture_traffic;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ApacheHttpTrafficRecorder
{
    public static final class Exchange
    {
        public final String method;
        public final String uri;
        public final int status; // -1 if missing
        public final String requestSnippet;
        public final String responseSnippet;


        public Exchange(String method, String uri, int status, String requestSnippet, String responseSnippet)
        {
            this.method = method;
            this.uri = uri;
            this.status = status;
            this.requestSnippet = requestSnippet;
            this.responseSnippet = responseSnippet;
        }


        @Override
        public String toString()
        {
            return "Exchange{" +
                            "method='" + method + '\'' +
                            ", uri='" + uri + '\'' +
                            ", status=" + status +
                            ", requestSnippet='" + requestSnippet + '\'' +
                            ", responseSnippet='" + responseSnippet + '\'' +
                            '}';
        }
    }


    private final List<Exchange> exchanges = new CopyOnWriteArrayList<>();


    void add(Exchange e)
    {
        exchanges.add(e);
    }


    public List<Exchange> getExchanges()
    {
        return List.copyOf(exchanges);
    }


    public void clear()
    {
        exchanges.clear();
    }
}
