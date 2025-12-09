package com.yapily.orione2e.api.service.applications.payload.request;

import lombok.Data;

@Data
public class CreateSubapplicationRequest
{
    public String name;
    public String merchantCategoryCode;
    public String ppcUserGroup;
    public Boolean isContractPresent;
}
