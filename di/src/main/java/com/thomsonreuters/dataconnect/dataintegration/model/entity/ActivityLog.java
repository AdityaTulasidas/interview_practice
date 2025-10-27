package com.thomsonreuters.dataconnect.dataintegration.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Setter
@Getter
@Table(name = "activity_log")
public class ActivityLog {
    @Id
    @Column(name = "id", nullable = false, length = 128)
    private UUID id;

    @Column(name = "job_execution_id")
    private UUID jobExecutionId;

    @Column(name = "region_job_execution_id")
    private UUID regionJobExecutionId;

    @Column(name = "activity_id")
    private String activityId;

    @Column(name = "component")
    private String component;

    @Column(name = "thread_id")
    private String threadId;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private String status;

    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
