package com.aditya.dataconnect.executionengine.services.datastreamAdaptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.aditya.dataconnect.executionengine.model.pojo.DataUnit;

public class DataUnitDeserializer {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static DataUnit deserializeDataUnit(String jsonPayload) throws Exception {
        return objectMapper.readValue(jsonPayload, DataUnit.class);
    }
}