package com.yapily.e2ejunit.model;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IAMService
{
    public Map<String, String> endpoints;
}
