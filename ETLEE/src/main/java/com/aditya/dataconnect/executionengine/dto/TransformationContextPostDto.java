package com.aditya.dataconnect.executionengine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.aditya.dataconnect.executionengine.model.pojo.Params;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
public class TransformationContextPostDto {


    @NotNull
    @NotEmpty
    @JsonProperty("type")
    private String type;
    @JsonProperty(value = "seq",defaultValue = "1")
    @Min(value = 1)
    private int seq;
    @NotNull
    @NotEmpty
    @JsonProperty("function_id")
    private String functionId;
    @NotNull
    @NotEmpty
    @JsonProperty(value = "exec_type",defaultValue = "TARGET")
    @Pattern(regexp = "^(?i)(SOURCE|TARGET)$")
    private String execType;
    @JsonProperty("params")
    private List<Params> params;
}
