package com.thomsonreuters.dataconnect.dataintegration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransformationParamGetDTO {
    private Integer id;

    @JsonProperty("system_name")
    private String systemName;
    @JsonProperty("description")
    private String description;
    @JsonProperty("display_name")
    private String displayName;
    @JsonProperty("transform_func_sys_name")
    private String transformFuncId;
    @JsonProperty(value = "created_by")
    private String createdBy;

    @JsonProperty(value = "created_at")
    private LocalDateTime createdAt;

    @JsonProperty(value = "updated_by")
    private String updatedBy;


    @JsonProperty(value = "updated_at")
    private LocalDateTime updatedAt;


}