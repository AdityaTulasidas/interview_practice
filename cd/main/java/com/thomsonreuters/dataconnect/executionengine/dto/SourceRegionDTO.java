package com.thomsonreuters.dataconnect.executionengine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SourceRegionDTO {
    @JsonProperty("region")
    private String region;

    @JsonProperty("regional_tenant")
    private String regionalTenantId;
}
