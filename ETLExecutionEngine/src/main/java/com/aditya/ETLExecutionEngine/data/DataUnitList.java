package com.aditya.ETLExecutionEngine.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DataUnitList implements DataUnitContent {
    //This class is used to represent a list of data units, which can be used for various operations in the execution engine.
    @JsonProperty("obj_ids")
    private List<?> objIds;

}