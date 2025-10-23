package com.thomsonreuters.metadataregistry.exceptionhandler;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataSourceCatalogException extends RuntimeException {
    private final String code;

    public DataSourceCatalogException(String message, String code) {
        super(message);
        this.code = code;
    }
}