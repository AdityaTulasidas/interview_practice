package com.aditya.dataconnect.executionengine.model.pojo;

import com.aditya.dataconnect.executionengine.model.entity.enums.DataType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class PrimaryKeyInfo {
    private String pkName;
    private DataType pkDataType;
    private List<Map<String, Object>> compositePKColNameAndType;
    private boolean isComposite;
}