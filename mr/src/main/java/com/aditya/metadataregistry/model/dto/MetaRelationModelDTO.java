package com.thomsonreuters.metadataregistry.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MetaRelationModelDTO extends MetaObjectRelationDTO{

    @JsonProperty(value = "child_object", access = JsonProperty.Access.READ_ONLY)
    private MetaObjectDTO childObject;
}
