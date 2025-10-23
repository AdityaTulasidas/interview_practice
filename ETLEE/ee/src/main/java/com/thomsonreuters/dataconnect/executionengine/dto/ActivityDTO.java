package com.thomsonreuters.dataconnect.executionengine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivityDTO {
    @JsonProperty("sys_name")
    private String sysName;

    @JsonProperty("activity_id")
    private Integer activityId;

    @JsonProperty("exec_type")
    private String execType;

    @JsonProperty("exec_seq")
    private Integer execSeq;

    @JsonProperty("event_type")
    private String eventType;

    @JsonProperty("activity_type")
    private String activityType;

    // Explicit getters to satisfy environments where Lombok annotation processing may be unavailable
    public String getSysName() {
        return sysName;
    }
    public Integer getActivityId() {
        return activityId;
    }
    public String getExecType() {
        return execType;
    }
    public Integer getExecSeq() {
        return execSeq;
    }
    public String getEventType() {
        return eventType;
    }
    public String getActivityType() {
        return activityType;
    }
}