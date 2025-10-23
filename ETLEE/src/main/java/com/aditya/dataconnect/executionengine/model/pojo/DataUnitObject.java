package com.aditya.dataconnect.executionengine.model.pojo;

import com.aditya.dataconnect.executionengine.services.datastreamAdaptor.impl.DataUnitContent;
import lombok.Data;

@Data
public class DataUnitObject implements DataUnitContent {
    private DataCollectionObject datacollectionobject;
}
