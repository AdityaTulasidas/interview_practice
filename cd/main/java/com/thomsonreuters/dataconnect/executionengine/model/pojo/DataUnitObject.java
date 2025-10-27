package com.thomsonreuters.dataconnect.executionengine.model.pojo;

import com.thomsonreuters.dataconnect.executionengine.services.datastreamAdaptor.impl.DataUnitContent;
import lombok.Data;

@Data
public class DataUnitObject implements DataUnitContent {
    private DataCollectionObject datacollectionobject;
}
