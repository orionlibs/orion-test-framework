package com.yapily.e2ejunit.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDetails
{
    public String applicationId;
    public String applicationSecret;
    public String userId;
    public String applicationUserId;
}
