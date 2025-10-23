package com.thomsonreuters.metadataregistry.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomsonreuters.metadataregistry.exceptionhandler.MetaDataRegistryException;

public class CommonUtils {


    public static String generateResponse(String id, String message) throws MetaDataRegistryException {
        try{
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(message + id);
        }
        catch (JsonProcessingException e) {
            throw new MetaDataRegistryException("Error while generating response", "INTERNAL_SERVER_ERROR");
        }
    }

    private CommonUtils() {

    }

}
