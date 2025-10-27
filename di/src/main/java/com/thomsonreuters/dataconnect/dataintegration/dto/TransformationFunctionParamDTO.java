package com.thomsonreuters.dataconnect.dataintegration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TransformationFunctionParamDTO {
    private Integer id;

    @JsonProperty("system_name")
    private String systemName;
    @JsonProperty("description")
    private String description;
    @JsonProperty("display_name")
    private String displayName;
    @JsonProperty("transform_func_id")
    private String transformFuncId;

}