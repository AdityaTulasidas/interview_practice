package com.thomsonreuters.dataconnect.dataintegration.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.thomsonreuters.dataconnect.dataintegration.utils.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.*;



@Setter
@Getter
public class MetaObjectDTO extends BaseDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;


    @JsonProperty("description")
    private String description;


    @JsonProperty("db_table")
    @Schema(example = "db_table")
    private String dbTable;


    @JsonProperty("schema")
    private String schema;


    @JsonProperty("display_name")
    private String displayName;


    @JsonProperty("onesource_domain")
    private String oneSourceDomain;

    @JsonProperty(value = "business_name",access = JsonProperty.Access.WRITE_ONLY)
    private String businessName;

    @JsonProperty(value = "system_name", access = JsonProperty.Access.READ_ONLY)
    private String systemName;

    @JsonProperty("domain_object")

    private String domainObject;


    @JsonProperty("is_autogen_id")
    private boolean isAutogenId;

    @JsonProperty(value = "usage_count", access = JsonProperty.Access.READ_ONLY)
    private Integer usageCount;

    @JsonProperty("is_event_enabled")
    private boolean isEventEnabled ;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Map<String, MetaObjectAttributeDTO> attributes = new HashMap<>();
    @JsonProperty("child_relations")
    @JsonIgnore
    private Set<MetaObjectRelationDTO> childRelations= new HashSet<>();


    public MetaObjectDTO() {
    }
    public MetaObjectDTO(String oneSourceDomain, String metaObjectDescription, String dbTable , String displayName, boolean isAutogenId) {
        this.oneSourceDomain = oneSourceDomain;
        this.description = metaObjectDescription;
        this.dbTable = dbTable;
        this.displayName= displayName;
        this.isAutogenId= isAutogenId;

    }

}

