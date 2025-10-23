package com.thomsonreuters.metadataregistry.utils;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@MappedSuperclass
@JsonInclude(JsonInclude.Include.NON_NULL)

public class BaseDTO {


    @JsonProperty(value = "created_by", access = JsonProperty.Access.READ_ONLY)
    private String createdBy;

    @JsonProperty(value= "updated_by", access = JsonProperty.Access.READ_ONLY)
    private String updatedBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @JsonProperty(value = "created_at", access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @JsonProperty(value = "updated_at", access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;
}
