package com.thomsonreuters.metadataregistry.utils;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonProperty(value = "created_by", access = JsonProperty.Access.READ_ONLY)
    @Column(name = "created_by")
    private String createdBy;

    @JsonProperty(value = "updated_by", access = JsonProperty.Access.READ_ONLY)
    @Column(name = "updated_by")
    private String updatedBy;

    @JsonProperty(value = "created_at", access = JsonProperty.Access.READ_ONLY)
    @Column(name = "created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdAt;

    @JsonProperty(value = "updated_at", access = JsonProperty.Access.READ_ONLY)
    @Column(name = "updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime updatedAt;
}
