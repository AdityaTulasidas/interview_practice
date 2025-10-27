package com.thomsonreuters.dataconnect.dataintegration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JobExecutionLogDto {
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("when_accepted")
    private LocalDateTime whenAccepted;

    @JsonProperty("when_started")
    private LocalDateTime whenStarted;

    @JsonProperty("when_completed")
    private LocalDateTime whenCompleted;

    @JsonProperty("status")
    private String status;

    @JsonProperty("records_read")
    private Integer recTransferredCnt;

    @JsonProperty("records_failed")
    private Integer recFailedCnt;

    @JsonProperty("records_written")
    private Integer recordsWritten;

    @JsonProperty("customer_id")
    private String customerId;

    @JsonProperty("job_id")
    private UUID jobId;

    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_by")
    private String updatedBy;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    // Getters and setters omitted for brevity
}