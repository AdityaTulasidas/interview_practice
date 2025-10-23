package com.thomsonreuters.metadataregistry.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

import com.thomsonreuters.metadataregistry.model.entity.OnesourceRegion;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OnesourceRegionDTO {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("system_name")
    private String systemName;

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("description")
    private String description;

    @JsonProperty(value = "created_by")
    private String createdBy;

    @JsonProperty(value = "created_at")
    private LocalDateTime createdAt;

    @JsonProperty(value = "updated_by")
    private String updatedBy;


    @JsonProperty(value = "updated_at")
    private LocalDateTime updatedAt;



    public OnesourceRegionDTO(OnesourceRegion region) {
        this.id = region.getId();
        this.systemName = region.getSystemName();
        this.displayName = region.getDisplayName();
        this.createdBy = region.getCreatedBy();
        this.createdAt = region.getCreatedAt();
        this.updatedBy = region.getUpdatedBy();
        this.updatedAt = region.getUpdatedAt();
        this.description = region.getDescription();
    }

    public OnesourceRegionDTO() {}
}
