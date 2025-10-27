package com.thomsonreuters.dataconnect.dataintegration.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "job_execution_log")
@Data
@Setter
@Getter
public class JobExecutionLog {

    @Id
    @GeneratedValue
    private UUID id;
    @Column(name = "accepted_at")
    private LocalDateTime whenAccepted;

    @Column(name = "started_at")
    private LocalDateTime whenStarted;

    @Column(name = "completed_at")
    private LocalDateTime whenCompleted;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "records_read")
    private Integer recTransferredCnt;

    @Column(name = "records_failed")
    private Integer recFailedCnt;

    @Column(name = "records_written")
    private Integer recordsWritten;

    @Column(name = "customer_tenant_id")
    private String customerId;

    @Column(name = "regional_job_id", nullable = false)
    private UUID jobId;

    @Column(name = "created_by", length = 100, nullable = false, updatable = false)
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    //newly added fields
    @Column(name = "client_id")
    private String clientId;

    @Column(name = "region")
    private String region;

    @JsonProperty("regional_job_sys_name")
    @Column(name = "regional_job_sys_name")
    private String regionalJobSysName;

    @Column(name = "regional_tenant_id")
    private String regionalTenantId;

    @Column(name = "type")
    private String type;

    @Column(name = "job_execution_id", nullable = false)
    private UUID jobExecutionId;

    @PrePersist
    public void ensureId() {
        if (this.jobExecutionId == null) {
            this.jobExecutionId = UUID.randomUUID();
        }
    }
}
