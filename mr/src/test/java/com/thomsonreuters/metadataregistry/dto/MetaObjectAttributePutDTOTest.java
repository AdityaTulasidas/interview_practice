package com.thomsonreuters.metadataregistry.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomsonreuters.metadataregistry.model.dto.MetaObjectAttributePutDTO;
import com.thomsonreuters.metadataregistry.model.entity.enums.DataType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MetaObjectAttributePutDTOTest {

    @Test
    void should_SetAndGetFields_When_ValidValuesProvided() {
        // Arrange
        MetaObjectAttributePutDTO dto = new MetaObjectAttributePutDTO();
        UUID id = UUID.randomUUID();
        DataType dataType = DataType.STRING; // Replace with an actual enum value
        String dbColumnName = "column_name";
        String description = "Test Description";
        String displayName = "Test Display Name";
        boolean isMandatory = true;
        boolean isPrimary = false;
        boolean isSystemGen = true;

        // Act
        dto.setId(id);
        dto.setDataType(dataType);
        dto.setDbColumn(dbColumnName);
        dto.setDescription(description);
        dto.setDisplayName(displayName);
        dto.setMandatory(isMandatory);
        dto.setPrimary(isPrimary);
        dto.setSysAttribute(isSystemGen);

        // Assert
        assertEquals(id, dto.getId());
        assertEquals(dataType, dto.getDataType());
        assertEquals(dbColumnName, dto.getDbColumn());
        assertEquals(description, dto.getDescription());
        assertEquals(displayName, dto.getDisplayName());
        assertTrue(dto.isMandatory());
        assertFalse(dto.isPrimary());
        assertTrue(dto.isSysAttribute());
    }

    @Test
    void should_IncludeNonNullFields_When_JsonSerialization() throws JsonProcessingException {
        // Arrange
        MetaObjectAttributePutDTO dto = new MetaObjectAttributePutDTO();
        dto.setId(UUID.randomUUID());
        dto.setDataType(DataType.STRING); // Replace with an actual enum value
        dto.setDbColumn("column_name");
        dto.setDescription(null); // Should be excluded
        dto.setDisplayName("Test Display Name");
        dto.setMandatory(true);
        dto.setPrimary(false);
        dto.setSysAttribute(true);

        // Act
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(dto);

        // Assert
        assertTrue(json.contains("id"));
        assertTrue(json.contains("data_type"));
        assertTrue(json.contains("db_column"));
        assertTrue(json.contains("display_name"));
        assertTrue(json.contains("is_mandatory"));
        assertTrue(json.contains("is_sys_attribute"));
    }

    @Test
    void should_HandleNullValues_When_FieldsAreNotSet() {
        // Arrange
        MetaObjectAttributePutDTO dto = new MetaObjectAttributePutDTO();

        // Assert
        assertNull(dto.getId());
        assertNull(dto.getDataType());
        assertNull(dto.getDbColumn());
        assertNull(dto.getDescription());
        assertNull(dto.getDisplayName());
        assertFalse(dto.isMandatory());
        assertFalse(dto.isPrimary());
        assertFalse(dto.isSysAttribute());
    }
}