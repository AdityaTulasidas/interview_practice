package com.thomsonreuters.dataconnect.dataintegration.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thomsonreuters.dataconnect.dataintegration.dto.Transformations;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.ExecutionLeg;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.JobType;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "regional_datasync_job")
@Getter
@Setter
@NoArgsConstructor
public class RegionalJobConfiguration {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "job_type")
    @Enumerated(EnumType.STRING)
    private JobType type;

    @Column(name = "exec_leg")
    @Enumerated(EnumType.STRING)
    private ExecutionLeg execLeg;

    @Column(name = "datasync_job_id")
    private UUID datasyncJobId;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "in_adaptor_id")
    private String inAdaptorId;

    @Column(name = "out_adaptor_id")
    private String outAdaptorId;

    @ElementCollection
    @CollectionTable(name = "in_adaptor_exec_context", joinColumns = @JoinColumn(name = "id"))
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    private Map<String, String> inAdaptorExecContext;

    @ElementCollection
    @CollectionTable(name = "out_adaptor_exec_context", joinColumns = @JoinColumn(name = "id"))
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    private Map<String, String> outAdaptorExecContext;

    @Column(name = "source_tenant_id")
    private String sourceTenantId;

    @Column(name = "target_tenant_id")
    @JsonProperty("target_tenant_id")
    private String targetTenantId;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "transform_context")
    @Convert(converter = com.thomsonreuters.dataconnect.dataintegration.utils.TransformationsConverter.class)
    private List<Transformations> transformContext;

    @Column(name = "customer_tenant_sys_name")
    private String customerTenantSysName;

    @Column(name = "datasync_job_sys_name")
    private String datasyncJobSysName;

    @Column(name = "source_region")
    private String sourceRegion;

    @Column(name = "target_region")
    private String targetRegion;

    @Column(name = "onesource_domain")
    private String onesourceDomain;

    @Column(name = "meta_object_sys_name")
    private String metaObjectSysName;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_run_date")
    private LocalDateTime lastRunDate;

    @PrePersist
    @PreUpdate
    public void beforeSaveOrUpdate() {
        // Trim string fields
        if (inAdaptorId != null) inAdaptorId = inAdaptorId.trim();
        if (outAdaptorId != null) outAdaptorId = outAdaptorId.trim();
        if (sourceTenantId != null) sourceTenantId = sourceTenantId.trim();
        if (targetTenantId != null) targetTenantId = targetTenantId.trim();
        if (clientId != null) clientId = clientId.trim();
        if (customerTenantSysName != null) customerTenantSysName = customerTenantSysName.trim();
        if (datasyncJobSysName != null) datasyncJobSysName = datasyncJobSysName.trim();
        if (sourceRegion != null) sourceRegion = sourceRegion.trim();
        if (targetRegion != null) targetRegion = targetRegion.trim();
        if (onesourceDomain != null) onesourceDomain = onesourceDomain.trim();
        if (metaObjectSysName != null) metaObjectSysName = metaObjectSysName.trim();
        if (createdBy != null) createdBy = createdBy.trim();
        if (updatedBy != null) updatedBy = updatedBy.trim();

        // Set timestamps
        if (createdAt == null) {
            createdAt = LocalDateTime.now(); // Only set on create
        }
        updatedAt = LocalDateTime.now(); // Always set on update
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegionalJobConfiguration that = (RegionalJobConfiguration) o;
        return isActive == that.isActive &&
                Objects.equals(id, that.id) &&
                type == that.type &&
                execLeg == that.execLeg &&
                Objects.equals(datasyncJobId, that.datasyncJobId) &&
                Objects.equals(inAdaptorId, that.inAdaptorId) &&
                Objects.equals(outAdaptorId, that.outAdaptorId) &&
                Objects.equals(inAdaptorExecContext, that.inAdaptorExecContext) &&
                Objects.equals(outAdaptorExecContext, that.outAdaptorExecContext) &&
                Objects.equals(sourceTenantId, that.sourceTenantId) &&
                Objects.equals(targetTenantId, that.targetTenantId) &&
                Objects.equals(clientId, that.clientId) &&
                Objects.equals(transformContext, that.transformContext) &&
                Objects.equals(customerTenantSysName, that.customerTenantSysName) &&
                Objects.equals(datasyncJobSysName, that.datasyncJobSysName) &&
                Objects.equals(sourceRegion, that.sourceRegion) &&
                Objects.equals(targetRegion, that.targetRegion) &&
                Objects.equals(onesourceDomain, that.onesourceDomain) &&
                Objects.equals(metaObjectSysName, that.metaObjectSysName) &&
                Objects.equals(createdBy, that.createdBy) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(updatedBy, that.updatedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, execLeg, datasyncJobId, isActive, inAdaptorId, outAdaptorId, inAdaptorExecContext, outAdaptorExecContext, sourceTenantId, targetTenantId, clientId, transformContext, customerTenantSysName, datasyncJobSysName, sourceRegion, targetRegion, onesourceDomain, metaObjectSysName, createdBy, createdAt, updatedBy);
    }
}
