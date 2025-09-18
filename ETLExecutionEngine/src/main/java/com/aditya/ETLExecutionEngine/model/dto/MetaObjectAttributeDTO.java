package com.aditya.ETLExecutionEngine.model.dto;

@Setter
@Getter
@NoArgsConstructor
public class MetaObjectAttributeDTO extends MetaObjectsMetaObjectAttributePostDTO {

    @JsonProperty("meta_object_id")
    private UUID metaObjectId;

    @JsonProperty("child_relations")
    @JsonIgnore
    private List<MetaObjectRelationDTO> childRelations;

}
 