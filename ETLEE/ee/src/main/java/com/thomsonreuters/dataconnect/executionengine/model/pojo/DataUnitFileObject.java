package com.thomsonreuters.dataconnect.executionengine.model.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thomsonreuters.dataconnect.executionengine.services.datastreamAdaptor.impl.DataUnitContent;
import lombok.Data;

import java.util.List;

@Data
public class DataUnitFileObject implements DataUnitContent {
    //This class is used to represent a list of data units, which can be used for various operations in the execution engine.
    @JsonProperty("folder")
    private String folder;
    @JsonProperty("file_list")
    private List<String> fileList;
}
