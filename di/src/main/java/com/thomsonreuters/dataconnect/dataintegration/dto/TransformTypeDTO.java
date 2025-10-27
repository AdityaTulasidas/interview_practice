package com.thomsonreuters.dataconnect.dataintegration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.TransformationType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransformTypeDTO {

    @JsonProperty("id")
    private int id;
    @JsonProperty("system_name")
    private String systemName;
    @JsonProperty("description")
    private String description;
    @JsonProperty("display_name")
    private String displayName;
    @JsonProperty(value = "created_by")
    private String createdBy;

    @JsonProperty(value = "created_at")
    private LocalDateTime createdAt;

    @JsonProperty(value = "updated_by")
    private String updatedBy;


    @JsonProperty(value = "updated_at")
    private LocalDateTime updatedAt;


    public TransformTypeDTO(TransformationType entity) {
        this.id = entity.getId();
        this.systemName = entity.getSystemName();
        this.description = entity.getDescription();
        this.displayName = entity.getDisplayName();
        this.createdBy = entity.getCreatedBy();
        this.createdAt = entity.getCreatedAt();
        this.updatedBy = entity.getUpdatedBy();
        this.updatedAt = entity.getUpdatedAt();
    }
}
