package com.thomsonreuters.dataconnect.dataintegration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TransformationDTO {
    private Integer id;
    @JsonProperty("system_name")
    private String systemName;
    @JsonProperty("description")
    private String description;
    @JsonProperty("display_name")
    private String displayName;
    @JsonProperty("transform_type")
    private String transformType;
    @JsonProperty("onesource_domain")
    private String onesourceDomain;
    @JsonProperty(value = "parameters",access = JsonProperty.Access.READ_ONLY)
    private List<TransformationFunctionParamDTO> builtinTransformationParam;
}