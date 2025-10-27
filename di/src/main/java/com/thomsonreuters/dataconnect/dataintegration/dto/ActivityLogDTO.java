package com.thomsonreuters.dataconnect.dataintegration.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;


@Setter
@Getter
public class ActivityLogDTO {

    @JsonProperty("id")
    @JsonIgnore
    private UUID id;

    @JsonProperty("job_execution_id")
    private UUID jobExecutionId;

    @JsonProperty("region_job_execution_id")
    private UUID regionJobExecutionId;

    @JsonProperty("activity_id")
    private String activityId;

    @JsonProperty("component")
    private String component;

    @JsonProperty("thread_id")
    private String threadId;

    @JsonProperty("description")
    private String description;

    @JsonProperty("status")
    private String status;

    @JsonProperty("created_by")
    @JsonIgnore
    private String createdBy;

    @JsonProperty("created_at")
    @JsonIgnore
    private LocalDateTime createdAt;

    @JsonProperty("updated_by")
    @JsonIgnore
    private String updatedBy;

    @JsonProperty("updated_at")
    @JsonIgnore
    private LocalDateTime updatedAt;

}
