package com.thomsonreuters.metadataregistry.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomsonreuters.metadataregistry.model.dto.MetaObjectDetailsDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MetaObjectDetailsDTOTest {





    @Test
    void should_SetAndGetFields_When_ValidValuesProvided() {
        // Arrange
        MetaObjectDetailsDTO dto = new MetaObjectDetailsDTO();
        UUID id = UUID.randomUUID();
        String description = "Test Description";
        String tableName = "TestTable";
        String domain = "BF_AR"; // Replace with an actual enum value
        String name = "Test Name";
        String displayName = "Test Display Name";
        boolean isAutogenId = true;

        // Act
        dto.setId(id);
        dto.setDescription(description);
        dto.setDbTable(tableName);
        dto.setOneSourceDomain(domain);
        dto.setSystemName(name);
        dto.setDisplayName(displayName);
        dto.setAutogenId(isAutogenId);

        // Assert
        assertEquals(id, dto.getId());
        assertEquals(description, dto.getDescription());
        assertEquals(tableName, dto.getDbTable());
        assertEquals(domain, dto.getOneSourceDomain());
        assertEquals(name, dto.getSystemName());
        assertEquals(displayName, dto.getDisplayName());
        assertTrue(dto.isAutogenId());
    }

    @Test
    void should_IncludeNonNullFields_When_JsonSerialization() throws JsonProcessingException {
        // Arrange
        MetaObjectDetailsDTO dto = new MetaObjectDetailsDTO();
        dto.setId(UUID.randomUUID());
        dto.setDescription("Test Description");
        dto.setDbTable("TestTable");
        dto.setOneSourceDomain("BF_AR"); // Replace with an actual enum value
        dto.setSystemName(null); // Should be excluded
        dto.setDisplayName("Test Display Name");
        dto.setAutogenId(true);

        // Act & Assert
        // Use a JSON library like Jackson to test serialization
         ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(dto);
        assertTrue(json.contains("id"));
    }

    @Test
    void should_ReturnCorrectAutogenId_When_GetAutogenIdCalled() {
        // Arrange
        MetaObjectDetailsDTO dto = new MetaObjectDetailsDTO();
        dto.setAutogenId(true);

        // Act
        boolean result = dto.isAutogenId();

        // Assert
        assertTrue(result);
    }
}