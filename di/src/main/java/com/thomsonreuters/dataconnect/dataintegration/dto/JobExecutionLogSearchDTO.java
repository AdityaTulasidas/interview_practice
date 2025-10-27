package com.thomsonreuters.dataconnect.dataintegration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Data
public class JobExecutionLogSearchDTO {
    @JsonProperty("region")
    private String region;

    @JsonProperty("status")
    private String status;

    @JsonProperty("accepted_at")
    private LocalDateTime whenAccepted;

    @JsonProperty("started_at")
    private LocalDateTime whenStarted;

    @JsonProperty("completed_at")
    private LocalDateTime whenCompleted;

    @JsonProperty("records_read")
    private Integer recTransferredCnt;

    @JsonProperty("records_failed")
    private Integer recFailedCnt;

    @JsonProperty("records_written")
    private Integer recordsWritten;

    @JsonProperty("regional_job_sys_name")
    private String regionalJobSysName;

    @JsonProperty("customer_tenant_id")
    private String customerId;
}