package com.thomsonreuters.dataconnect.dataintegration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class TransformationContextDTO {


    @NotNull
    @NotEmpty
    @JsonProperty("type_id")
    private String typeId;
    @JsonProperty(value = "seq",defaultValue = "1")
    @Min(value = 1)
    private int seq;
    @NotNull
    @NotEmpty
    @JsonProperty("function_id")
    private String functionId;
    @NotNull
    @NotEmpty
    @JsonProperty("field_name")
    private String fieldName;
    @NotNull
    @NotEmpty
    @JsonProperty("source_value")
    private String sourceValue;
    @NotNull
    @NotEmpty
    @JsonProperty("target_value")
    private String targetValue;
    @NotNull
    @NotEmpty
    @JsonProperty(value = "exec_type",defaultValue = "TARGET")
    @Pattern(regexp = "^(?i)(SOURCE|TARGET)$")
    private String execType;
}