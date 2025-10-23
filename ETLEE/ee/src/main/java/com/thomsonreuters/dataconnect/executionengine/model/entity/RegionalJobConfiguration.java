package com.thomsonreuters.dataconnect.executionengine.model.entity;

import com.thomsonreuters.dataconnect.executionengine.model.entity.enums.ExecutionLeg;
import com.thomsonreuters.dataconnect.executionengine.model.entity.enums.JobType;
import com.thomsonreuters.dataconnect.executionengine.model.entity.enums.RegionType;
import com.thomsonreuters.dataconnect.executionengine.transformation.Transformations;
import com.thomsonreuters.dataconnect.executionengine.utils.TransformationsConverter;
import jakarta.persistence.*;
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

    @Column(name = "source_tenant_id")
    private String sourceTenantId;

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

    @Column(name = "target_tenant_id")
    private String targetTenantId;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "transform_context")
    @Convert(converter = TransformationsConverter.class)
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
    private String  onesourceDomain;

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
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
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