package com.thomsonreuters.dataconnect.dataintegration.dto;

import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.RelationType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

 class MetaObjectRelationDTOTest {

    @Test
    void shouldSetAndGetAllFieldsCorrectly_WhenDTOIsInitialized() {
        UUID id = UUID.randomUUID();
        String description = "Test Description";
        String metaObjectRelationId = "relation_123";
        UUID parentObjectId = UUID.randomUUID();
        String parentObjRelCol = "parent_column";
        UUID childObjectId = UUID.randomUUID();
        String childObjRelCol = "child_column";
        String relationType = "PARENT"; // Replace with actual enum value
        MetaObjectDTO childObject = new MetaObjectDTO();

        MetaObjectRelationDTO dto = new MetaObjectRelationDTO();
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
}