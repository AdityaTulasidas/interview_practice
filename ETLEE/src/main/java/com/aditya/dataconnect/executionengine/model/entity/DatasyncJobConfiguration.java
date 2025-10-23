package com.aditya.dataconnect.executionengine.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.aditya.dataconnect.executionengine.model.entity.enums.ExecType;
import com.aditya.dataconnect.executionengine.model.entity.enums.JobType;
import com.aditya.dataconnect.executionengine.utils.BaseEntity;
import com.thomsonreuters.dep.api.jpa.mapping.annotations.ApiField;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "onesource_datasync_job")
@Getter
@Setter
@NoArgsConstructor
public class DatasyncJobConfiguration extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name="description")
    private String description;

    @JsonProperty("meta_object_sys_name")
    private String metaObjectSysName;

    @JsonProperty("customer_tenant_sys_name")
    private String customerTenantSysName;

    @Enumerated(EnumType.STRING)
    @Column(name="job_type")
    private JobType jobType;

    @Column(name="source_region")
    private String sourceRegion;

    @Column(name="client_id")
    private String clientId;

    @Column(name="onesource_domain")
    private String onesourceDomain;

    @Enumerated(EnumType.STRING)
    @Column(name="exec_type")
    private ExecType execType;

    @ApiField("systemName")
    @Column(name="system_name")
    @JsonProperty("system_name")
    private String systemName;

    @JsonProperty("source_tenant_id")
    private String sourceTenantId;

    @PreUpdate
    public void preUpdate() {
        this.setUpdatedAt(LocalDateTime.now());
    }
}
