package com.aditya.dataconnect.executionengine.transformation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.aditya.dataconnect.executionengine.model.entity.enums.TransformType;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Getter
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Transformations {
    @JsonProperty("type")
    public TransformType type;

    @JsonProperty("seq")
    public int seq;

    @JsonProperty("func_name")
    private String funcName;

    @JsonProperty("region")
    private String region;

    @JsonProperty("exec_leg")
    private String execLeg;

    @JsonProperty("params")
    private List<TransformParams> params;

    public String getValue(String name){
        for (TransformParams param : params) {
            if (param.getName().equals(name)) {
                return  param.getValue();
            }
        }
        return null;
    }
}