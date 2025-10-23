package com.aditya.dataconnect.executionengine.model.pojo;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.aditya.dataconnect.executionengine.data.DataSetCollection;
import com.aditya.dataconnect.executionengine.services.datastreamAdaptor.impl.DataUnitContent;
import lombok.Data;

@Data
public class DataUnit {
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = DataUnitList.class, name = "list"),
            @JsonSubTypes.Type(value = DataUnitObject.class, name = "object"),
            @JsonSubTypes.Type(value = DataUnitFileObject.class, name = "file_object"),
            @JsonSubTypes.Type(value = DataSetCollection.class, name = "data_set_collection")
    })
    private DataUnitContent content;
}
