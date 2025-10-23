package com.aditya.dataconnect.executionengine.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MetaObjectRelationDTOTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        UUID id = UUID.randomUUID();
        String description = "Test Description";
        String metaObjectRelationId = "relation-id";
        UUID parentObjectId = UUID.randomUUID();
        String parentObjRelCol = "parent_col";
        UUID childObjectId = UUID.randomUUID();
        String childObjRelCol = "child_col";
        String relationType = "PARENT";

        MetaObjectRelationDTO dto = new MetaObjectRelationDTO(
                description, metaObjectRelationId, parentObjectId, parentObjRelCol, childObjectId, childObjRelCol, relationType
        );
        dto.setId(id);

        assertEquals(id, dto.getId());
        assertEquals(description, dto.getDescription());
        assertEquals(parentObjectId, dto.getParentObjectId());
        assertEquals(parentObjRelCol, dto.getParentObjRelCol());
        assertEquals(childObjectId, dto.getChildObjectId());
        assertEquals(childObjRelCol, dto.getChildObjRelCol());
        assertEquals(relationType, dto.getRelationType());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        MetaObjectRelationDTO dto = new MetaObjectRelationDTO();

        UUID id = UUID.randomUUID();
        String description = "Test Description";
        String metaObjectRelationId = "relation-id";
        UUID parentObjectId = UUID.randomUUID();
        String parentObjRelCol = "parent_col";
        UUID childObjectId = UUID.randomUUID();
        String childObjRelCol = "child_col";
        String relationType = "PARENT";

        dto.setId(id);
        dto.setDescription(description);
        dto.setSystemName(metaObjectRelationId);
        dto.setParentObjectId(parentObjectId);
        dto.setParentObjRelCol(parentObjRelCol);
        dto.setChildObjectId(childObjectId);
        dto.setChildObjRelCol(childObjRelCol);
        dto.setRelationType(relationType);

        assertEquals(id, dto.getId());
        assertEquals(description, dto.getDescription());
        assertEquals(parentObjectId, dto.getParentObjectId());
        assertEquals(parentObjRelCol, dto.getParentObjRelCol());
        assertEquals(childObjectId, dto.getChildObjectId());
        assertEquals(childObjRelCol, dto.getChildObjRelCol());
        assertEquals(relationType, dto.getRelationType());
    }

    @Test
    void testDefaultValues() {
        MetaObjectRelationDTO dto = new MetaObjectRelationDTO();
        assertNotNull(dto);
    }
}