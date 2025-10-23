package com.thomsonreuters.metadataregistry.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class MetaObjectDetailsPutDTO extends BaseDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
@JsonProperty(access = JsonProperty.Access.READ_ONLY)
private UUID id;


    @JsonProperty("description")
    @NotBlankIfPresent
    private String description;

    @NotNull
    @NotBlank
    @NotEmpty
    @NotNullOrStringNull
    @JsonProperty("db_table")
    @Schema(example = "db_table")
    private String dbTable;


    @JsonProperty(value = "onesource_domain")
    @NotNull
    @NotBlank
    @NotEmpty
    private String oneSourceDomain;

    @NotNull
    @NotBlank
    @NotEmpty
    @NotNullOrStringNull
    @JsonProperty("schema")
    private String schema;


    @JsonProperty("display_name")
    @NotNull
    @NotBlank
    @NotEmpty
    @NotNullOrStringNull
    private String displayName;


    @JsonProperty("is_autogen_id")
    private boolean isAutogenId;

    @JsonProperty(value = "usage_count", access = JsonProperty.Access.READ_ONLY)
    private Integer usageCount;

    @JsonProperty("is_event_enabled")
    private boolean isEventEnabled ;





}
