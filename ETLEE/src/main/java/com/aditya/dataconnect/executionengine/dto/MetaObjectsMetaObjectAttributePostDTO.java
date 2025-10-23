package com.aditya.dataconnect.executionengine.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.aditya.dataconnect.executionengine.model.entity.enums.DataType;
import com.aditya.dataconnect.executionengine.utils.BaseDTO;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@NoArgsConstructor
@Getter
@Setter
@JsonPropertyOrder({"id", "name", "data_type", "db_column_name", "description", "display_name", "is_mandatory", "is_primary", "is_system_gen", "seq_num", "json_tag", "is_event_enabled", "order_by"})
@JsonInclude(JsonInclude.Include.ALWAYS)
public class MetaObjectsMetaObjectAttributePostDTO extends BaseDTO {

    @JsonProperty(value = "id")
    private UUID id;

    @JsonProperty(value = "name")
    private String name;

    @JsonProperty("data_type")
    @Enumerated(EnumType.STRING)
    private DataType dataType;

    @JsonProperty("db_column_name")
    private String dbColumnName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("is_mandatory")
    private boolean isMandatory;
    @JsonProperty("is_primary")
    private boolean isPrimary;
    @JsonProperty("is_system_gen")
    private boolean isSystemGen;

    @JsonProperty(value = "seq_num")
    private int seqNum;

    @JsonProperty("json_tag")
    private String jsonTag;

    @JsonProperty("is_event_enabled")
    private boolean isEventEnabled;

    @JsonProperty("order_by")
    private Integer orderBy;
}
