package com.thomsonreuters.dataconnect.dataintegration.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.JobType;

import com.thomsonreuters.dataconnect.dataintegration.utils.BaseEntity;
import com.thomsonreuters.dep.api.jpa.mapping.annotations.ApiClass;
import com.thomsonreuters.dep.api.jpa.mapping.annotations.ApiField;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@ApiClass(DatasyncJobConfiguration.class)
@Entity
@Table(name = "onesource_datasync_job")
@Getter
@Setter
@NoArgsConstructor
public class DatasyncJobConfiguration extends BaseEntity {

    @Id
    @GeneratedValue
    @JsonProperty("id")
    private UUID id;

    @ApiField("systemName")
    @Column(name="system_name")
    @JsonProperty("system_name")
    private String systemName;

    @ApiField("description")
    @Column(name="description")
    @JsonProperty("description")
    private String description;

    @ApiField("metaObjectSysName")
    @Column(name="meta_object_sys_name")
    @JsonProperty("meta_object_sys_name")
    private String metaObjectSysName;

    @ApiField("customerTenantSysName")
    @Column(name="customer_tenant_sys_name")
    @JsonProperty("customer_tenant_sys_name")
    private String customerTenantSysName;

    @ApiField("jobType")
    @Enumerated(EnumType.STRING)
    @Column(name="job_type")
    @JsonProperty("job_type")
    private JobType jobType;

    @ApiField("sourceRegion")
    @Column(name="source_region")
    @JsonProperty("source_region")
    private String sourceRegion;

    @ApiField("sourceTenantId")
    @Column(name="source_tenant_id")
    @JsonProperty("source_tenant_id")
    private String sourceTenantId;

    @ApiField("clientId")
    @Column(name="client_id")
    @JsonProperty("client_id")
    private String clientId;

    @ApiField("onesourceDomain")
    @Column(name="onesource_domain")
    @JsonProperty("onesource_domain")
    private String onesourceDomain;

    @ApiField("execType")
    @Column(name="exec_type")
    @JsonProperty("exec_type")
    private String execType;

    @ApiField("isUgeCustomer")
    @Column(name="is_uge_customer")
    @JsonProperty("is_uge_customer")
    private Boolean isUgeCustomer;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @PrePersist
    @PreUpdate
    public void beforeSaveOrUpdate() {
        // Trim string fields
        if (systemName != null) systemName = systemName.trim();
        if (description != null) description = description.trim();
        if (metaObjectSysName != null) metaObjectSysName = metaObjectSysName.trim();
        if (customerTenantSysName != null) customerTenantSysName = customerTenantSysName.trim();
        if (sourceRegion != null) sourceRegion = sourceRegion.trim();
        if (sourceTenantId != null) sourceTenantId = sourceTenantId.trim();
        if (clientId != null) clientId = clientId.trim();
        if (onesourceDomain != null) onesourceDomain = onesourceDomain.trim();
        if (execType != null) execType = execType.trim();
        if (updatedBy != null) updatedBy = updatedBy.trim();

        // Set updated timestamp
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DatasyncJobConfiguration that = (DatasyncJobConfiguration) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(systemName, that.systemName) &&
                Objects.equals(description, that.description) &&
                Objects.equals(metaObjectSysName, that.metaObjectSysName) &&
                Objects.equals(customerTenantSysName, that.customerTenantSysName) &&
                jobType == that.jobType &&
                Objects.equals(sourceRegion, that.sourceRegion) &&
                Objects.equals(sourceTenantId, that.sourceTenantId) &&
                Objects.equals(clientId, that.clientId) &&
                Objects.equals(onesourceDomain, that.onesourceDomain) &&
                Objects.equals(execType, that.execType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, systemName, description, metaObjectSysName, customerTenantSysName, jobType, sourceRegion, sourceTenantId, clientId, onesourceDomain, execType, isUgeCustomer);
    }
}
