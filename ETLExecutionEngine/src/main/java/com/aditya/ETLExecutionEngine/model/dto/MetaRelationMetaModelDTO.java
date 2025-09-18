package com.aditya.ETLExecutionEngine.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class MetaRelationMetaModelDTO extends MetaObjectRelationDTO{

    @JsonProperty("parent_object")
    private  MetaObjectDTO parentObject;


    @JsonProperty(value = "child_relations")
    private Set<MetaRelationModelDTO> childObjectRelations;

}


 