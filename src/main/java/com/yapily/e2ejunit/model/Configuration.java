package com.yapily.e2ejunit.model;

import java.util.HashMap;
import java.util.Map;

public class Configuration
{
    public Map<String, Long> httpClient = new HashMap<>();
    public Map<String, String> account = new HashMap<>();
    public Map<String, Service> services = new HashMap<>();


    public static class Service
    {
        public String host = "";
        public Map<String, String> endpoints;
    }
}
