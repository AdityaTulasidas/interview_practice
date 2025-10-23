package com.thomsonreuters.dataconnect.executionengine.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.thomsonreuters.dataconnect.executionengine.model.entity.enums.DataType;
import com.thomsonreuters.dataconnect.executionengine.utils.BaseDTO;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter

public class MetaObjectAttributeDTO extends BaseDTO {
    @Setter
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @JsonProperty(value = "system_name")
    private String systemName;


    @Setter
    @Getter
    @NotNull
    @JsonProperty("data_type")
    @Enumerated(EnumType.STRING)
    private DataType dataType;


    @JsonProperty("db_column")
    @NotNull
    @NotEmpty
    @NotBlank
    private String dbColumn;

    @JsonProperty("description")
    private String description;

    @NotNull
    @JsonProperty("display_name")
    @NotEmpty
    @NotBlank
    private String displayName;


    @JsonProperty(value = "meta_object_sys_name", access = JsonProperty.Access.READ_ONLY)
    private String metaObjectSysName;

    @JsonProperty("logical_key")
    private int logicalKey;

    @JsonProperty("is_mandatory")
    private boolean isMandatory;
    @JsonProperty("is_primary")
    private boolean isPrimary;
    @JsonProperty("is_sys_attribute")
    private boolean isSysAttribute;

    @JsonProperty(value = "seq_num")
    private int seqNum;


    @JsonProperty("is_event_enabled")
    private boolean isEventEnabled;

    @JsonProperty("order_by")
    private Integer orderBy;


}
