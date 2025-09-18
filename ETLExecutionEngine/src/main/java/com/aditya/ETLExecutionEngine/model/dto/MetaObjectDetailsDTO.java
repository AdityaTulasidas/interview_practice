package com.aditya.ETLExecutionEngine.model.dto;

import com.aditya.ETLExecutionEngine.model.enums.OnesourceDomain;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Pattern;
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


    @JsonProperty("table_name")
    @Pattern(
            regexp = "^\\w+\\.\\w+$"
    )
    @Schema(example = "schema.table_name")
    private String tableName;

    @JsonProperty("onesource_domain")
    @Enumerated(EnumType.STRING)
    private OnesourceDomain oneSourceDomain;


    @JsonProperty(value = "system_name")
    private String systemName;


    @JsonProperty("display_name")
    private String displayName;


    @JsonProperty("is_autogen_id")
    private boolean isAutogenId;
    @JsonProperty(value = "usage_count")
    private Integer usageCount;

    @JsonProperty("is_event_enabled")
    private boolean isEventEnabled ;


    public boolean getAutogenId() {
        return isAutogenId;
    }
}