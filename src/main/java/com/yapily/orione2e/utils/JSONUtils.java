package com.yapily.orione2e.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtils
{
    public static final ObjectMapper OBJECT_MAPPER;

    static
    {
        OBJECT_MAPPER = new ObjectMapper();
    }

    public static String toJSON(Object object)
    {
        try
        {
            return OBJECT_MAPPER.writeValueAsString(object);
        }
        catch(JsonProcessingException e)
        {
            throw new RuntimeException(e);
        }
    }
}
