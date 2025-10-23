package com.thomsonreuters.metadataregistry.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

import java.util.List;
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class DataSourceSearchDTO<T> {
    @JsonProperty("display_name")
    private String displayName;
    private String systemName;
    private String domain;
    private String customerTenantId;
    private String regionalTenantId;
    private String onesourceRegion;
    private Integer numberOfRecords;
    // Represents a list of items of generic type T, allowing flexibility to store any type of data.
    // This is useful for handling search results or collections of data dynamically.
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