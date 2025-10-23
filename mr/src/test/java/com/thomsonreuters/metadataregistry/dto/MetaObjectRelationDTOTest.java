package com.thomsonreuters.metadataregistry.dto;

import com.thomsonreuters.metadataregistry.model.dto.MetaObjectRelationDTO;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MetaObjectRelationDTOTest {

    @Test
    void shouldSetAndGetFields_WhenValidValuesProvided() {
        MetaObjectRelationDTO metaObjectRelationDTO = new MetaObjectRelationDTO();
        metaObjectRelationDTO.setDescription("Test Description");
        metaObjectRelationDTO.setParentObjectId(UUID.randomUUID());
        metaObjectRelationDTO.setParentObjRelCol("Parent Column");
        metaObjectRelationDTO.setChildObjectId(UUID.randomUUID());
        metaObjectRelationDTO.setChildObjRelCol("Child Column");
        metaObjectRelationDTO.setRelationType("PARENT");
        metaObjectRelationDTO.setSystemName("Test System");

        assertEquals("Test Description", metaObjectRelationDTO.getDescription());
        assertNotNull(metaObjectRelationDTO.getParentObjectId());
        assertEquals("Parent Column", metaObjectRelationDTO.getParentObjRelCol());
        assertNotNull(metaObjectRelationDTO.getChildObjectId());
        assertEquals("Child Column", metaObjectRelationDTO.getChildObjRelCol());
        assertEquals("PARENT", metaObjectRelationDTO.getRelationType());
        assertEquals("Test System", metaObjectRelationDTO.getSystemName());
    }


}