package com.yapily.orione2e.model;

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
