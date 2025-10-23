package com.aditya.dataconnect.executionengine.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.aditya.dataconnect.executionengine.utils.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
public class MetaObjectDetailsDTO extends BaseDTO {

    @JsonProperty("id")
    private UUID id;


    @JsonProperty("description")
    private String description;

    @NotNull
    @NotBlank
    @NotEmpty
    @JsonProperty("db_table")
    @Schema(example = "db_table")
    private String dbTable;

    @NotNull
    @NotBlank
    @NotEmpty
    @JsonProperty("schema")
    private String schema;


    @JsonProperty("display_name")
    private String displayName;

    @NotNull
    @NotBlank
    @NotEmpty
    @JsonProperty("onesource_domain")
    private String oneSourceDomain;


    @JsonProperty(value = "system_name")
    private String systemName;

    @JsonProperty("domain_object")
    @NotNull
    @NotBlank
    @NotEmpty
    private String domainObject;


    @JsonProperty("is_autogen_id")
    private boolean isAutogenId;

    @JsonProperty(value = "usage_count")
    private Integer usageCount;

    @JsonProperty("is_event_enabled")
    private boolean isEventEnabled ;

}
