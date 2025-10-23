package com.thomsonreuters.dataconnect.executionengine.data;

import lombok.Data;

import java.util.concurrent.ConcurrentHashMap;

@Data
public class DataRow {
    ConcurrentHashMap<String, Object> row;
}
