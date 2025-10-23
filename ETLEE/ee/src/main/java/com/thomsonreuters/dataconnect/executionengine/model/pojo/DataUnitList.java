package com.thomsonreuters.dataconnect.executionengine.model.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thomsonreuters.dataconnect.executionengine.services.datastreamAdaptor.impl.DataUnitContent;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class DataUnitList implements DataUnitContent {
    //This class is used to represent a list of data units, which can be used for various operations in the execution engine.
    @JsonProperty("obj_ids")
    private List<?> objIds;

}
