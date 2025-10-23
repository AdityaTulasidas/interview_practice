package com.thomsonreuters.dataconnect.executionengine.dto;

import com.thomsonreuters.dataconnect.executionengine.model.entity.enums.DataType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MetaObjectAttributeDTOTest {

    @Test
    void shouldSetAndGetFields_WhenValidValuesProvided() {
        MetaObjectAttributeDTO metaObjectAttributeDTO = new MetaObjectAttributeDTO();
        metaObjectAttributeDTO.setSystemName("Attribute ID");
        metaObjectAttributeDTO.setDataType(DataType.STRING);
        metaObjectAttributeDTO.setDbColumn("Column Name");
        metaObjectAttributeDTO.setDescription("Test Description");
        metaObjectAttributeDTO.setDisplayName("Test Display Name");
        metaObjectAttributeDTO.setMandatory(true);
        metaObjectAttributeDTO.setPrimary(false);
        metaObjectAttributeDTO.setSysAttribute(true);
        metaObjectAttributeDTO.setSeqNum(1);

        assertEquals("Attribute ID", metaObjectAttributeDTO.getSystemName());
        assertEquals(DataType.STRING, metaObjectAttributeDTO.getDataType());
        assertEquals("Column Name", metaObjectAttributeDTO.getDbColumn());
        assertEquals("Test Description", metaObjectAttributeDTO.getDescription());
        assertEquals("Test Display Name", metaObjectAttributeDTO.getDisplayName());
        assertTrue(metaObjectAttributeDTO.isMandatory());
        assertFalse(metaObjectAttributeDTO.isPrimary());
        assertTrue(metaObjectAttributeDTO.isSysAttribute());
        assertEquals(1, metaObjectAttributeDTO.getSeqNum());
    }

    @Test
    void shouldInitializeFields_WhenUsingParameterizedConstructor() {
        UUID metaObjectId = UUID.randomUUID();
        MetaObjectAttributeDTO metaObjectAttributeDTO = new MetaObjectAttributeDTO();
        metaObjectAttributeDTO.setSystemName("Attribute ID");
        metaObjectAttributeDTO.setDataType(DataType.INTEGER);
        metaObjectAttributeDTO.setDbColumn("Column Name");
        metaObjectAttributeDTO.setDescription("Test Description");
        metaObjectAttributeDTO.setDisplayName("Test Display Name");
        metaObjectAttributeDTO.setMandatory(true);
        metaObjectAttributeDTO.setPrimary(false);
        metaObjectAttributeDTO.setSysAttribute(true);
        metaObjectAttributeDTO.setSeqNum(1);




        assertEquals("Attribute ID", metaObjectAttributeDTO.getSystemName());
        assertEquals(DataType.INTEGER, metaObjectAttributeDTO.getDataType());
        assertEquals("Column Name", metaObjectAttributeDTO.getDbColumn());
        assertEquals("Test Description", metaObjectAttributeDTO.getDescription());
        assertEquals("Test Display Name", metaObjectAttributeDTO.getDisplayName());
        assertTrue(metaObjectAttributeDTO.isMandatory());
        assertFalse(metaObjectAttributeDTO.isPrimary());
        assertTrue(metaObjectAttributeDTO.isSysAttribute());
        assertEquals(1, metaObjectAttributeDTO.getSeqNum());
    }
}