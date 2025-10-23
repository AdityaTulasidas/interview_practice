package com.thomsonreuters.metadataregistry.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thomsonreuters.metadataregistry.model.entity.DomainObject;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DomainObjectDTO {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("system_name")
    private String systemName;

    @JsonProperty("object_name")
    private String objectName;

    @JsonProperty("domain_sys_name")
    private String domainSysName;

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




    public DomainObjectDTO() {}

        public DomainObjectDTO(DomainObject type) {
        this.id = type.getId();
        this.systemName = type.getSystemName();
        this.objectName = type.getObjectName();
        this.domainSysName = type.getDomainSysName();
        this.description = type.getDescription();
        this.createdBy = type.getCreatedBy();
        this.createdAt = type.getCreatedAt();
        this.updatedBy = type.getUpdatedBy();
        this.updatedAt = type.getUpdatedAt();
    }
}
