package com.thomsonreuters.dataconnect.executionengine.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({
        "id",
        "job_name",
        "job_type",
        "customer_tenant_id",
        "client_id",
        "when_accepted",
        "when_started",
        "job_id",
        "when_completed",
        "status",
        "records_read",
        "records_failed",
        "records_written",
        "customer_id",
        "created_by",
        "created_at",
        "updated_by",
        "updated_at"})
public class JobExecutionLogResponseDTO {

    @Setter
    @Getter
    private UUID id;

    @JsonProperty("job_name")
    private String jobName;

    @JsonProperty("job_type")
    private String jobType;
    @JsonProperty("customer_tenant_id")
    private String customerTenantId;
    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("when_accepted")
    private LocalDateTime whenAccepted;

    @JsonProperty("when_started")
    private LocalDateTime whenStarted;

    @JsonProperty("job_id")
    private UUID jobId;


    @JsonProperty("when_completed")
    private LocalDateTime whenCompleted;

    @JsonProperty("status")
    private String status;

    @JsonProperty("records_read")
    private Integer recTransferedCnt;

    @JsonProperty("records_failed")
    private Integer recFailedCnt;

    @JsonProperty("records_written")
    private Integer recordsWritten;

    @JsonProperty("customer_id")
    private String customerId;

    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @JsonProperty("updated_by")
    private String updatedBy;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
