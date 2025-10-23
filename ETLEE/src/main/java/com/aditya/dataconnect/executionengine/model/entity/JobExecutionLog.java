package com.aditya.dataconnect.executionengine.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thomsonreuters.dep.api.jpa.mapping.annotations.ApiClass;
import com.thomsonreuters.dep.api.jpa.mapping.annotations.ApiField;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@ApiClass(JobExecutionLog.class)
@Entity
@Table(name = "job_execution_log")
@Getter
@Setter
@NoArgsConstructor
public class JobExecutionLog {

    @Setter
    @Getter
    @Id
    @GeneratedValue
    private UUID id;

    @ApiField("whenAccepted")
    @Column(name = "accepted_at", nullable = false)
    @JsonProperty("when_accepted")
    private LocalDateTime whenAccepted;

    @ApiField("whenStarted")
    @Column(name = "started_at")
    @JsonProperty("when_started")
    private LocalDateTime whenStarted;

    @ApiField("jobId")
    @JsonProperty("job_id")
    @Column(name = "regional_job_id", nullable = false)
    private UUID jobId;

    @ApiField("whenCompleted")
    @JsonProperty("when_completed")
    @Column(name = "completed_at")
    private LocalDateTime whenCompleted;

    @ApiField("status")
    @JsonProperty("status")
    @Column(name = "status", length = 50)
    private String status;

    @ApiField("recTransferedCnt")
    @JsonProperty("records_read")
    @Column(name = "records_read")
    private Integer recTransferedCnt;


    @ApiField("recFailedCnt")
    @JsonProperty("records_failed")
    @Column(name = "records_failed")
    private Integer recFailedCnt;

    @ApiField("recordsWritten")
    @JsonProperty("records_written")
    @Column(name = "records_written")
    private Integer recordsWritten;

    @ApiField("customerId")
    @JsonProperty("customer_id")
    @Column(name = "customer_tenant_id", length = 100)
    private String customerId;

    @ApiField("createdBy")
    @JsonProperty("created_by")
    @Column(name = "created_by", length = 100, nullable = false)
    private String createdBy;

    @ApiField("createdAt")
    @JsonProperty("created_at")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ApiField("updatedBy")
    @Column(name = "updated_by", length = 100)
    @JsonProperty("updated_by")
    private String updatedBy;

    @ApiField("updatedAt")
    @JsonProperty("updated_at")
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