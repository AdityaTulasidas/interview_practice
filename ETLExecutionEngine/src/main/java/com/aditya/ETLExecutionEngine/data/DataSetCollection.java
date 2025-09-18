package com.aditya.ETLExecutionEngine.data;

import lombok.Data;

import java.util.List;

@Data
public class DataSetCollection implements DataUnitContent {
    private List<DataSet> dataSets;
}
 