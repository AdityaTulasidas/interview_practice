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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;


@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"id", "name", "data_type", "db_column_name", "description", "display_name", "is_mandatory", "is_primary", "is_system_gen", "seq_num", "json_tag"})
@JsonInclude(JsonInclude.Include.ALWAYS)
public class MetaObjectAttributePutDTO extends BaseDTO {
    @Setter
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @JsonProperty(value = "system_name")
    @NotNull
    @NotEmpty
    @NotBlank
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


    @JsonProperty("logical_key")
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
