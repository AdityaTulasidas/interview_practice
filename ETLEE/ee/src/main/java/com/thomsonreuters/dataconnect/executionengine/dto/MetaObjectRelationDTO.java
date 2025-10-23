package com.thomsonreuters.dataconnect.executionengine.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.thomsonreuters.dataconnect.executionengine.utils.BaseDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MetaObjectRelationDTO extends BaseDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Getter
    private UUID id;


    @Getter
    @JsonProperty("description")
    private String description;

    @JsonProperty(value = "system_name", access = JsonProperty.Access.READ_ONLY)
    private String systemName;


    @Getter
    @JsonProperty("parent_object_id")
    @NotNull
    private UUID parentObjectId;

    @JsonProperty("parent_obj_rel_col")
    @NotNull
    @NotEmpty
    @NotBlank
    @Getter
    private String parentObjRelCol;


    @Getter
    @JsonProperty("child_object_id")
    @NotNull
    private UUID childObjectId;

    @JsonProperty("child_obj_rel_col")
    @NotNull
    @NotEmpty
    @NotBlank
    @Getter
    private String childObjRelCol;

    @Getter
    @NotNull
    @JsonProperty("relation_type")
    private String relationType;


    @JsonProperty(value = "child_object", access = JsonProperty.Access.READ_ONLY)
    private MetaObjectDTO childObject;

    public MetaObjectRelationDTO(String description, String metaObjectRelationId, UUID parentObjectId, String parentObjRelCol, UUID childObjectId, String childObjRelCol, String relationType) {
        this.description = description;
        this.systemName = metaObjectRelationId;
        this.parentObjectId = parentObjectId;
        this.parentObjRelCol = parentObjRelCol;
        this.childObjectId = childObjectId;
        this.childObjRelCol = childObjRelCol;
        this.relationType = relationType;
    }





}
