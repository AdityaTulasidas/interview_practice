package com.thomsonreuters.dataconnect.dataintegration.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CommonUtils {
    private CommonUtils() {
        // Private constructor to prevent instantiation
    }
    public static String generateResponse(String id, String message) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(message + id);
    }
}
