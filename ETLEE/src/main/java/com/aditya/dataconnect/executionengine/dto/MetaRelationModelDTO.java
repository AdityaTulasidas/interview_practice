package com.aditya.dataconnect.executionengine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MetaRelationModelDTO extends MetaObjectRelationDTO{

    @JsonProperty(value = "child_object")
    private MetaObjectDTO childObject;
}
