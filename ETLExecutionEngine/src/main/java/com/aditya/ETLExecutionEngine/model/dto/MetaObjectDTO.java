package com.aditya.ETLExecutionEngine.model.dto;

import com.aditya.ETLExecutionEngine.model.enums.OnesourceDomain;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@JsonPropertyOrder({"id", "description", "table_name", "one_source_domain", "display_name", "autogen_id","name", "attributes"})
@Setter
@Getter
public class MetaObjectDTO extends MetaObjectDetailsDTO {

    @JsonProperty("attributes")
    private Set<MetaObjectAttributeDTO> attributes = new HashSet<>();

    @JsonProperty("child_relations")
    private Set<MetaObjectRelationDTO> childRelations= new HashSet<>();

    public MetaObjectDTO() {
    }

    public MetaObjectDTO(OnesourceDomain oneSourceDomain, String metaObjectDescription, String dbTable , String displayName, boolean isAutogenId) {
        this.setTableName(dbTable);
        this.setDescription(metaObjectDescription);
        this.setOneSourceDomain(oneSourceDomain);
        this.setDisplayName(displayName);
        this.setAutogenId(isAutogenId);

    }

}