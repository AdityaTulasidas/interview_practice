package com.thomsonreuters.dataconnect.dataintegration.model.pojo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class JsonParser implements ParserInterface {

    public ObjectMapper objectMapper;

    public JsonParser() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Header readHeader(InputStream inputStream) throws IOException {
        try {
            JsonNode rootNode = objectMapper.readTree(inputStream);
            JsonNode headerNode = rootNode.path("header");
            // Validate and parse headerNode
            return objectMapper.treeToValue(headerNode, Header.class);
        } catch (IOException e) {
            // Handle exception and raise error
            throw new IOException("Failed to read and validate header", e);
        }
    }

    @Override
    public DataUnitContent readData(InputStream inputStream) throws IOException {
        try {
            JsonNode rootNode = objectMapper.readTree(inputStream);
            JsonNode dataNode = rootNode.path("data");
            // Validate and parse dataNode
            DataUnit dataunit = objectMapper.treeToValue(dataNode, DataUnit.class);
            return dataunit.getContent();
        } catch (IOException e) {
            // Handle exception and raise error
            throw new IOException("Failed to read and validate data", e);
        }
    }


}


