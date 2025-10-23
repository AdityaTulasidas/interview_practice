package com.thomsonreuters.metadataregistry.model.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.Valid;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@JsonPropertyOrder({"id", "description", "db_table", "schema","onesource_domain", "display_name", "system_name","domain_object","is_autogen_id","usage_count","is_event_enabled", "attributes"})
@Getter
@Setter
@JsonInclude(JsonInclude.Include.ALWAYS)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class MetaObjectPutDTO extends  MetaObjectDetailsPutDTO{



    @Valid
    private Set<MetaObjectAttributePutDTO> attributes = new HashSet<>();






}
