package com.yapily.orione2e.api.service.hosted_payments.submit_institution.payload.request;

import lombok.Data;

@Data
public class SubmitInstitutionRequest
{
    public String institutionId;
    public String institutionCountryCode;
}
