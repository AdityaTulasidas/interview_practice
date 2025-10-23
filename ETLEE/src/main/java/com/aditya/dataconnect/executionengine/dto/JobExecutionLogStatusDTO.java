package com.aditya.dataconnect.executionengine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({
        "job_id",
        "when_accepted",
        "when_started",
        "when_completed",
        "status"})
public class JobExecutionLogStatusDTO {

    @JsonProperty("job_id")
    private UUID jobId;

    @JsonProperty("when_accepted")
    private LocalDateTime whenAccepted;

    @JsonProperty("when_started")
    private LocalDateTime whenStarted;


    @JsonProperty("when_completed")
    private LocalDateTime whenCompleted;

    @JsonProperty("status")
    private String status;

}
