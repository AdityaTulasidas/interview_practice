package com.thomsonreuters.metadataregistry.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.thomsonreuters.metadataregistry.utils.BaseDTO;
import com.thomsonreuters.metadataregistry.utils.NotBlankIfPresent;
import com.thomsonreuters.metadataregistry.utils.NotNullOrStringNull;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"id", "description", "db_table","schema", "onesource_domain", "display_name", "system_name","domain_object","meta_object_sys_name","is_autogen_id","usage_count","is_event_enabled", "attributes"})
public class MetaObjectDetailsDTO extends BaseDTO {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;


    @JsonProperty("description")
    @NotBlankIfPresent
    private String description;

    @NotNull
    @NotBlank
    @NotEmpty
    @JsonProperty("db_table")
    @Schema(example = "db_table")
    @NotNullOrStringNull
    private String dbTable;

    @NotNull
    @NotBlank
    @NotEmpty
    @JsonProperty("schema")
    @NotNullOrStringNull
    private String schema;


    @JsonProperty("display_name")
    @NotEmpty
    @NotNull
    @NotBlank
    @NotNullOrStringNull
    private String displayName;

    @NotNull
    @NotBlank
    @NotEmpty
    @JsonProperty("onesource_domain")
    private String oneSourceDomain;

    @JsonProperty(value = "business_name",access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    @NotBlank
    @NotEmpty
    private String businessName;

    @JsonProperty(value = "system_name", access = JsonProperty.Access.READ_ONLY)
    private String systemName;

    @JsonProperty("domain_object")
    @NotNull
    @NotBlank
    @NotEmpty
    private String domainObject;


    @JsonProperty("is_autogen_id")
    private boolean isAutogenId;

    @JsonProperty(value = "usage_count", access = JsonProperty.Access.READ_ONLY)
    private Integer usageCount;

    @JsonProperty("is_event_enabled")
    private boolean isEventEnabled ;

}
