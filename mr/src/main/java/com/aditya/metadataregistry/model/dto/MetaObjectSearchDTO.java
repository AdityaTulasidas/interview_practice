package com.thomsonreuters.metadataregistry.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class MetaObjectSearchDTO<T> {
    @JsonProperty("name")
    private String displayName;
    private String oneSourceDomain;
    private Integer numberOfRecords;
    // List of items (MetaObjectDTO) returned in the current page of the search result
    private List<T> items;
    @JsonProperty("_meta")
    private Meta meta;
    @Data
    public static class Meta {
        private long count;  // Total number of items
        private int limit;   // Page size
        private int offset;  // Current page number
    }
 }