package com.aditya.ETLExecutionEngine.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@JsonPropertyOrder({"id","description","meta_object_relation_id","parent_object_id", "parent_obj_rel_col", "child_object_id", "child_obj_rel_col", "relation_type"})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MetaObjectRelationDTO extends BaseDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @JsonProperty("description")
    private String description;

    @JsonProperty(value = "meta_object_relation_id")
    private String metaObjectRelationId;


    @JsonProperty("parent_object_id")
    private UUID parentObjectId;

    @JsonProperty("parent_obj_rel_col")
    private String parentObjRelCol;


    @JsonProperty("child_object_id")
    private UUID childObjectId;

    @JsonProperty("child_obj_rel_col")
    private String childObjRelCol;


    @JsonProperty("relation_type")
    private String relationType;

    public MetaObjectRelationDTO(String description, String metaObjectRelationId, UUID parentObjectId, String parentObjRelCol, UUID childObjectId, String childObjRelCol, String relationType) {
        this.description = description;
        this.metaObjectRelationId = metaObjectRelationId;
        this.parentObjectId = parentObjectId;
        this.parentObjRelCol = parentObjRelCol;
        this.childObjectId = childObjectId;
        this.childObjRelCol = childObjRelCol;
        this.relationType = relationType;
    }

}