package com.thomsonreuters.dataconnect.dataintegration.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@MappedSuperclass
public class BaseDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "created_by", access = JsonProperty.Access.READ_ONLY)
    private String createdBy;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "updated_by", access = JsonProperty.Access.READ_ONLY)
    private String updatedBy;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "created_at", access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "updated_at", access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;
}
