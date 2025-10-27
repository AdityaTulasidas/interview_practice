package com.thomsonreuters.dataconnect.dataintegration.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.thomsonreuters.dataconnect.dataintegration.dto.Transformations;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.List;

@Converter(autoApply = true)
public class TransformationsConverter implements AttributeConverter<List<Transformations>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Transformations> attribute) {
        try {
            if (attribute == null) {
                return null; // Handle null case
            }
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting Transformations to JSON", e);
        }
    }

    @Override
    public List<Transformations> convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.isEmpty()) {
                return null; // Handle null or empty case
            }
            return objectMapper.readValue(dbData, objectMapper.getTypeFactory().constructCollectionType(List.class, Transformations.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JSON to Transformations", e);
        }
    }
}