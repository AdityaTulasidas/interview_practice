package com.thomsonreuters.metadataregistry.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.Valid;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
@JsonPropertyOrder({"id", "db_column_name", "system_name","data_type","description", "display_name", "is_mandatory", "is_primary", "is_system_gen", "seq_num","logical_key", "meta_object_sys_name", "is_event_enabled", "order_by"})
public class MetaObjectAttributeDTO extends MetaObjectsMetaObjectAttributePostDTO {




    @JsonProperty("child_relations")
    @JsonIgnore
    private List<MetaObjectRelationDTO> childRelations;





}
