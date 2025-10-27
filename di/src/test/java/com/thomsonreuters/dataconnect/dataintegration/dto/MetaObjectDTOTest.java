package com.thomsonreuters.dataconnect.dataintegration.dto;


import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

 class MetaObjectDTOTest {

    @Test
    void shouldSetAndGetAllFieldsCorrectly_WhenDTOIsInitialized() {
        UUID id = UUID.randomUUID();
        String description = "Test Description";
        String tableName = "test_table";
        String oneSourceDomain = "BF_AR"; // Replace with actual enum value
        Map<String, MetaObjectAttributeDTO> attributes = new HashMap<>();
        Set<MetaObjectRelationDTO> childRelations = new HashSet<>();

        MetaObjectDTO dto = new MetaObjectDTO();
        dto.setId(id);
        dto.setDescription(description);
        dto.setDbTable(tableName);
        dto.setOneSourceDomain(oneSourceDomain);
        dto.setAttributes(attributes);
        dto.setChildRelations(childRelations);

        assertEquals(id, dto.getId());
        assertEquals(description, dto.getDescription());
        assertEquals(tableName, dto.getDbTable());
        assertEquals(oneSourceDomain, dto.getOneSourceDomain());
        assertEquals(attributes, dto.getAttributes());
        assertEquals(childRelations, dto.getChildRelations());
    }
}