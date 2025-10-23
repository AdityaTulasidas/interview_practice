package com.thomsonreuters.metadataregistry.dto;

import com.thomsonreuters.metadataregistry.model.dto.MetaObjectPostDTO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MetaObjectPostDTOTest {

    @Test
    void shouldSetAndGetFields_WhenValidValuesProvided() {
        MetaObjectPostDTO metaObjectPostDTO = new MetaObjectPostDTO();
        metaObjectPostDTO.setDescription("Test Description");
        metaObjectPostDTO.setDbTable("Test Table");
        metaObjectPostDTO.setDisplayName("Test Display Name");
        metaObjectPostDTO.setAutogenId(true);
        metaObjectPostDTO.setOneSourceDomain("BF_AR");
        metaObjectPostDTO.setSystemName("Test System");
        metaObjectPostDTO.setSchema("Test Schema");
        metaObjectPostDTO.setEventEnabled(true);
        metaObjectPostDTO.setDomainObject("Test Domain Object");
        metaObjectPostDTO.setBusinessName("Test Business Name");

        assertEquals("Test Description", metaObjectPostDTO.getDescription());
        assertEquals("Test Table", metaObjectPostDTO.getDbTable());
        assertEquals("Test Display Name", metaObjectPostDTO.getDisplayName());
        assertEquals("Test System", metaObjectPostDTO.getSystemName());
        assertEquals("BF_AR", metaObjectPostDTO.getOneSourceDomain());
        assertEquals("Test Schema", metaObjectPostDTO.getSchema());
        assertEquals("Test Domain Object", metaObjectPostDTO.getDomainObject());
        assertEquals("Test Business Name", metaObjectPostDTO.getBusinessName());
        assertTrue(metaObjectPostDTO.isEventEnabled());
        assertTrue(metaObjectPostDTO.isAutogenId());
    }
}