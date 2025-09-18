package com.aditya.ETLExecutionEngine.data;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@Data
public class DataUnit {
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = DataUnitList.class, name = "list"),
            @JsonSubTypes.Type(value = DataUnitFileObject.class, name = "file_object"),
            @JsonSubTypes.Type(value = DataSetCollection.class, name = "data_set_collection")
    })
    private DataUnitContent content;
}
 