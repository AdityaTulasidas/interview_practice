package com.thomsonreuters.metadataregistry.model.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.thomsonreuters.metadataregistry.model.entity.enums.DataType;
import com.thomsonreuters.metadataregistry.utils.BaseDTO;
import com.thomsonreuters.metadataregistry.utils.NotBlankIfPresent;
import com.thomsonreuters.metadataregistry.utils.NotNullOrStringNull;
import com.thomsonreuters.metadataregistry.utils.enumvalidators.ValidDataType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import jakarta.validation.constraints.*;
import lombok.*;


import java.util.UUID;


@NoArgsConstructor

@Getter
@Setter
@JsonPropertyOrder({"id", "db_column", "system_name","data_type","description", "display_name", "is_mandatory", "is_primary", "is_sys_attribute", "seq_num","logical_key", "meta_object_sys_name", "is_event_enabled", "order_by"})
@JsonInclude(JsonInclude.Include.ALWAYS)
public class MetaObjectsMetaObjectAttributePostDTO extends BaseDTO {
    @Setter
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @JsonProperty(value = "system_name")
    @NotNull
    @NotBlank
    @NotEmpty
    @NotNullOrStringNull
    private String systemName;


    @Setter
    @Getter
    @NotNull
    @JsonProperty("data_type")
    @Enumerated(EnumType.STRING)
    @ValidDataType
    private DataType dataType;


    @JsonProperty("db_column")
    @NotNull
    @NotEmpty
    @NotBlank
    @NotNullOrStringNull
    private String dbColumn;

    @JsonProperty("description")
    @NotBlankIfPresent
    private String description;

    @NotNull
    @JsonProperty("display_name")
    @NotEmpty
    @NotBlank
    @NotNullOrStringNull
    private String displayName;


    @JsonProperty(value = "meta_object_sys_name", access = JsonProperty.Access.READ_ONLY)
    private String metaObjectSysName;

    @JsonProperty("logical_key")
    @Min(value = 0)
    private int logicalKey;

    @JsonProperty("is_mandatory")
    private boolean isMandatory;
    @JsonProperty("is_primary")
    private boolean isPrimary;
    @JsonProperty("is_sys_attribute")
    private boolean isSysAttribute;

    @JsonProperty(value = "seq_num", access = JsonProperty.Access.READ_ONLY)
    private int seqNum;


    @JsonProperty("is_event_enabled")
    private boolean isEventEnabled;

    @JsonProperty("order_by")
    private Integer orderBy;
}
