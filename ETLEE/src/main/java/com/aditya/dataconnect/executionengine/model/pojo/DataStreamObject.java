package com.aditya.dataconnect.executionengine.model.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.aditya.dataconnect.executionengine.dto.MetaObjectDTO;
import com.aditya.dataconnect.executionengine.services.datastreamAdaptor.impl.DataUnitContent;
import lombok.Data;

import java.util.List;

import java.util.concurrent.ConcurrentHashMap;

@Data
public class DataStreamObject  implements DataUnitContent {
    @JsonProperty("parent_meta_model")
    private MetaObjectDTO parentMetaModel;
    @JsonProperty("parent_data_object")
    private List<ConcurrentHashMap<String, Object> >parentDataObject; // this is used for backward compatibility with DataStreamObject, it will be deprecated in future
    @JsonProperty("child_data_object")
    private List<ConcurrentHashMap<String,Object>> childDataObject;
    @JsonProperty("child_meta_model")
    private MetaObjectDTO childMetaModel;




}
