package com.thomsonreuters.metadataregistry.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;


@JsonPropertyOrder({"id", "description", "db_table","schema", "onesource_domain", "display_name", "system_name","domain_object","is_autogen_id","usage_count","is_event_enabled","business_name", "attributes"})

@Setter
@Getter
@JsonInclude(JsonInclude.Include.ALWAYS)
@AllArgsConstructor
@NoArgsConstructor
public class MetaObjectDTO extends MetaObjectDetailsDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<MetaObjectAttributeDTO> attributes = new HashSet<>();

    @JsonProperty("child_relations")
    @JsonIgnore
    private Set<MetaObjectRelationDTO> childRelations= new HashSet<>();


}

