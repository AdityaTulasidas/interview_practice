package com.thomsonreuters.dataconnect.dataintegration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.TransformationFunction;
import jakarta.persistence.Column;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransformationFunctionDTO {
    @JsonProperty("id")
    private int id;
    @JsonProperty("system_name")
    private String systemName;
    @JsonProperty("description")
    private String description;
    @JsonProperty("display_name")
    private String displayName;
    @JsonProperty("onesource_domain")
    private String onesourceDomain;
    @JsonProperty(value = "created_by")
    private String createdBy;
    @JsonProperty(value = "created_at")
    private LocalDateTime createdAt;
    @JsonProperty(value = "updated_by")
    private String updatedBy;
    @JsonProperty(value = "type")
    private String type;
    @JsonProperty(value = "updated_at")
    private LocalDateTime updatedAt;



    public TransformationFunctionDTO(TransformationFunction entity) {
        this.id = entity.getId();
        this.systemName = entity.getSystemName();
        this.description = entity.getDescription();
        this.displayName = entity.getDisplayName();
        this.onesourceDomain = entity.getOnesourceDomain();
        this.createdBy = entity.getCreatedBy();
        this.createdAt = entity.getCreatedAt();
        this.updatedBy = entity.getUpdatedBy();
        this.updatedAt = entity.getUpdatedAt();

    }
}

