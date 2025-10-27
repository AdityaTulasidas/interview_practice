package com.thomsonreuters.dataconnect.dataintegration.model.entity;

import com.thomsonreuters.dataconnect.dataintegration.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Represents a per-target region record derived from the targets[] array in the
 * DatasyncJobConfiguration create / update requests.
 * Business Rules:
 *  - One row per (datasync_job_sys_name, target_region)
 *  - region_tenant may change on update (overwrite)
 *  - Physical delete when a region is removed from request
 *  - datasync_job_sys_name stores the job_name value (as per clarification)
 *
 * NOTE: Audit columns (created_by, updated_by, created_at, updated_at) removed from mapping
 * because underlying table does not contain them (SQL errors 42703).
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "target_regions",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_job_region", columnNames = {"datasync_job_sys_name", "target_region"})
       })
public class TargetRegion extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "target_region", nullable = false)
    private String targetRegion;

    // Column name in DB is target_tenant_id (stores region_tenant from request)
    @Column(name = "target_tenant_id" )
    private String regionTenantId;

    // Column is named datasync_job_sys_name but we store job_name (per user instruction)
    @Column(name = "datasync_job_sys_name", nullable = false)
    private String datasyncJobSysName;
}
