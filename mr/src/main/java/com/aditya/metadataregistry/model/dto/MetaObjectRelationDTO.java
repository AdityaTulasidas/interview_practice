package com.thomsonreuters.metadataregistry.model.dto;

import com.fasterxml.jackson.annotation.*;
import com.thomsonreuters.metadataregistry.utils.BaseDTO;
import com.thomsonreuters.metadataregistry.utils.NotBlankIfPresent;
import com.thomsonreuters.metadataregistry.utils.enumvalidators.ValidRelationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;



@JsonPropertyOrder({"id","description","system_name","parent_object_id", "parent_obj_rel_col", "child_object_id", "child_obj_rel_col", "relation_type"})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.ALWAYS)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetaObjectRelationDTO extends BaseDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Getter
    private UUID id;


    @Getter
    @JsonProperty("description")
    @NotBlankIfPresent
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
    @ValidRelationType
    @JsonProperty("relation_type")
    private String relationType;

}
