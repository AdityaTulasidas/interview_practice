package com.aditya.dataconnect.executionengine.dto;//package com.thomsonreuters.dataconnect.executionengine.dto;
//
//import com.thomsonreuters.dataconnect.executionengine.model.entity.enums.OnesourceDomain;
//import org.junit.jupiter.api.Test;
//
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//class MetaObjectDTOTest {
//
//    @Test
//    void testAllArgsConstructorAndGetters() {
//        OnesourceDomain oneSourceDomain = OnesourceDomain.BF_AR;
//        String description = "Test Description";
//        String tableName = "test_table";
//        String displayName = "Test Display Name";
//        boolean isAutogenId = true;
//
//        MetaObjectDTO dto = new MetaObjectDTO(oneSourceDomain, description, tableName, displayName, isAutogenId);
//
//        assertEquals(oneSourceDomain, dto.getOneSourceDomain());
//        assertEquals(description, dto.getDescription());
//        assertEquals(tableName, dto.getTableName());
//        assertEquals(displayName, dto.getDisplayName());
//        assertEquals(isAutogenId, dto.isAutogenId());
//    }
//
//    @Test
//    void testNoArgsConstructorAndSetters() {
//        MetaObjectDTO dto = new MetaObjectDTO();
//
//        UUID id = UUID.randomUUID();
//        String description = "Test Description";
//        String tableName = "test_table";
//        OnesourceDomain oneSourceDomain = OnesourceDomain.BF_AR;
//        String name = "Test Name";
//        String displayName = "Test Display Name";
//        boolean isAutogenId = true;
//        Map<String, MetaObjectAttributeDTO> attributes = new HashMap<>();
//        Set<MetaObjectRelationDTO> childRelations = new HashSet<>();
//
//        dto.setId(id);
//        dto.setDescription(description);
//        dto.setTableName(tableName);
//        dto.setOneSourceDomain(oneSourceDomain);
//        dto.setName(name);
//        dto.setDisplayName(displayName);
//        dto.setAutogenId(isAutogenId);
//        dto.setAttributes(attributes);
//        dto.setChildRelations(childRelations);
//
//        assertEquals(id, dto.getId());
//        assertEquals(description, dto.getDescription());
//        assertEquals(tableName, dto.getTableName());
//        assertEquals(oneSourceDomain, dto.getOneSourceDomain());
//        assertEquals(name, dto.getName());
//        assertEquals(displayName, dto.getDisplayName());
//        assertEquals(isAutogenId, dto.isAutogenId());
//        assertEquals(attributes, dto.getAttributes());
//        assertEquals(childRelations, dto.getChildRelations());
//    }
//
//    @Test
//    void testDefaultValues() {
//        MetaObjectDTO dto = new MetaObjectDTO();
//        assertNotNull(dto.getAttributes());
//        assertNotNull(dto.getChildRelations());
//    }
//}