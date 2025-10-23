package com.thomsonreuters.metadataregistry.dto;

import com.thomsonreuters.metadataregistry.model.dto.MetaObjectAttributeDTO;
import com.thomsonreuters.metadataregistry.model.entity.enums.DataType;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class MetaObjectAttributeDTOTest {

    @Test
    void shouldSetAndGetFields_WhenValidValuesProvided() {
        MetaObjectAttributeDTO metaObjectAttributeDTO = new MetaObjectAttributeDTO();
        metaObjectAttributeDTO.setSystemName("Attribute ID");
        metaObjectAttributeDTO.setDataType(DataType.STRING);
        metaObjectAttributeDTO.setDbColumn("Column Name");
        metaObjectAttributeDTO.setMetaObjectSysName("test");
        metaObjectAttributeDTO.setDescription("Test Description");
        metaObjectAttributeDTO.setDisplayName("Test Display Name");
        metaObjectAttributeDTO.setMandatory(true);
        metaObjectAttributeDTO.setPrimary(false);
        metaObjectAttributeDTO.setSysAttribute(true);
        metaObjectAttributeDTO.setSeqNum(1);
        metaObjectAttributeDTO.setEventEnabled(true);

        assertEquals("Attribute ID", metaObjectAttributeDTO.getSystemName());
        assertEquals(DataType.STRING, metaObjectAttributeDTO.getDataType());
        assertEquals("Column Name", metaObjectAttributeDTO.getDbColumn());
        assertNotNull(metaObjectAttributeDTO.getMetaObjectSysName());
        assertEquals("Test Description", metaObjectAttributeDTO.getDescription());
        assertEquals("Test Display Name", metaObjectAttributeDTO.getDisplayName());
        assertTrue(metaObjectAttributeDTO.isMandatory());
        assertFalse(metaObjectAttributeDTO.isPrimary());
        assertTrue(metaObjectAttributeDTO.isSysAttribute());
        assertEquals(1, metaObjectAttributeDTO.getSeqNum());
        assertTrue(metaObjectAttributeDTO.isEventEnabled());
    }

    @Test
    void shouldInitializeFields_WhenUsingParameterizedConstructor() {
        MetaObjectAttributeDTO metaObjectAttributeDTO = new MetaObjectAttributeDTO();
        metaObjectAttributeDTO.setSystemName("Attribute ID");
        metaObjectAttributeDTO.setDataType(DataType.INTEGER);
        metaObjectAttributeDTO.setDbColumn("Column Name");
        metaObjectAttributeDTO.setMetaObjectSysName("test");
        metaObjectAttributeDTO.setDescription("Test Description");
        metaObjectAttributeDTO.setDisplayName("Test Display Name");
        metaObjectAttributeDTO.setMandatory(true);
        metaObjectAttributeDTO.setPrimary(false);
        metaObjectAttributeDTO.setSysAttribute(true);
        metaObjectAttributeDTO.setSeqNum(1);
        metaObjectAttributeDTO.setEventEnabled(true);





        assertEquals("Attribute ID", metaObjectAttributeDTO.getSystemName());
        assertEquals(DataType.INTEGER, metaObjectAttributeDTO.getDataType());
        assertEquals("Column Name", metaObjectAttributeDTO.getDbColumn());
        assertEquals("test", metaObjectAttributeDTO.getMetaObjectSysName());
        assertEquals("Test Description", metaObjectAttributeDTO.getDescription());
        assertEquals("Test Display Name", metaObjectAttributeDTO.getDisplayName());
        assertTrue(metaObjectAttributeDTO.isMandatory());
        assertFalse(metaObjectAttributeDTO.isPrimary());
        assertTrue(metaObjectAttributeDTO.isSysAttribute());
        assertEquals(1, metaObjectAttributeDTO.getSeqNum());
        assertTrue(metaObjectAttributeDTO.isEventEnabled());
    }
}