package com.thomsonreuters.dataconnect.executionengine.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@MappedSuperclass
public class BaseDTO {

    @JsonIgnore
    @JsonProperty("created_by")
    private String createdBy;
    @JsonIgnore
    @JsonProperty("updated_by")
    private String updatedBy;
    @JsonIgnore
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    @JsonIgnore
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
