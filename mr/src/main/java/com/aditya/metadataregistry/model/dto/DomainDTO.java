package com.thomsonreuters.metadataregistry.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thomsonreuters.metadataregistry.model.entity.Domain;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DomainDTO {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("type_id")
    private Integer typeId;

    @JsonProperty("system_name")
    private String systemName;

    @JsonProperty("is_system")
    private Boolean isSystem;

    @JsonProperty(value = "created_by")
    private String createdBy;
    @JsonProperty(value = "created_at")
    private LocalDateTime createdAt;

    @JsonProperty(value = "updated_by")
    private String updatedBy;


    @JsonProperty(value = "updated_at")
    private LocalDateTime updatedAt;



    public DomainDTO(Domain domain) {
        this.id =  domain.getId();
        this.name = domain.getName();
        this.typeId = domain.getTypeId();
        this.systemName = domain.getSystemName();
        this.isSystem = domain.isSystem();
        this.createdBy = domain.getCreatedBy();
        this.createdAt = domain.getCreatedAt();
        this.updatedBy = domain.getUpdatedBy();
        this.updatedAt = domain.getUpdatedAt();
    }

    public DomainDTO() {}
}
