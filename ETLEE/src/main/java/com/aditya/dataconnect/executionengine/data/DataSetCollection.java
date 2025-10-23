package com.aditya.dataconnect.executionengine.data;

import com.aditya.dataconnect.executionengine.services.datastreamAdaptor.impl.DataUnitContent;
import lombok.Data;

import java.util.List;

@Data
public class DataSetCollection implements DataUnitContent {
    private List<DataSet> dataSets;
}
