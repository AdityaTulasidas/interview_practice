package com.thomsonreuters.dataconnect.dataintegration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SourceRegionalTenantDTO {
    @JsonProperty("regional_tenant")
    private String regionalTenantId;
}
