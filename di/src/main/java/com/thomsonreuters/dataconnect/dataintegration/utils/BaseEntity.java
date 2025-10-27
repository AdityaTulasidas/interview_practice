package com.thomsonreuters.dataconnect.dataintegration.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public class BaseEntity {

    @JsonProperty("created_by")
    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    @JsonProperty("updated_by")
    private String updatedBy;

    @Column(name = "created_at")
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
