package com.aditya.dataconnect.executionengine.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;


@JsonPropertyOrder({"id", "description", "db_table","schema", "onesource_domain", "display_name", "system_name","domain_object","is_autogen_id","usage_count","is_event_enabled","business_name", "attributes"})

@Setter
@Getter
@JsonInclude(JsonInclude.Include.ALWAYS)
@AllArgsConstructor
public class MetaObjectDTO extends MetaObjectDetailsDTO {

    @JsonProperty("attributes")
    private Set<MetaObjectAttributeDTO> attributes = new HashSet<>();

    @JsonProperty("child_relations")
    private Set<MetaObjectRelationDTO> childRelations= new HashSet<>();

    public MetaObjectDTO() {
    }

    public MetaObjectDTO(String oneSourceDomain, String metaObjectDescription, String dbTable ,String displayName, boolean isAutogenId) {
        this.setDbTable(dbTable);
        this.setDescription(metaObjectDescription);
        this.setOneSourceDomain(oneSourceDomain);
        this.setDisplayName(displayName);
        this.setAutogenId(isAutogenId);

    }


}

