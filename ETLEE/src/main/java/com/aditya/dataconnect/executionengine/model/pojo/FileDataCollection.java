package com.aditya.dataconnect.executionengine.model.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.aditya.dataconnect.executionengine.dto.MetaObjectDTO;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;

@Data
public class FileDataCollection {
    @JsonProperty("parent_meta_model")
    private MetaObjectDTO parentMetaModel;
    @JsonProperty("parent_data_object")
    private List<LinkedHashMap<String,Object>> parentDataObject; // this is used for backward compatibility with DataStreamObject, it will be deprecated in future
    @JsonProperty("child_data_object")
    private List<LinkedHashMap<String,Object>> childDataObject;
    @JsonProperty("child_meta_model")
    private MetaObjectDTO childMetaModel;
}
