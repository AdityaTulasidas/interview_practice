package com.thomsonreuters.dataconnect.dataintegration.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thomsonreuters.dataconnect.dataintegration.dto.Params;
import lombok.Data;

import java.util.List;

@Data
public class TransformationContext {

    @JsonProperty("type")
    private String type;
    @JsonProperty("seq")
    private int seq;
    @JsonProperty("function_id")
    private String functionId;
    @JsonProperty(value = "params")
List<Params> params;
}