package com.thomsonreuters.metadataregistry.dto;

import com.thomsonreuters.metadataregistry.model.dto.MetaObjectAttributeDTO;
import com.thomsonreuters.metadataregistry.model.dto.MetaObjectsMetaObjectAttributePostDTO;
import com.thomsonreuters.metadataregistry.model.entity.enums.DataType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MetaObjectsMetaObjectAttributePostDTOTest {

    @Test
    void shouldCreateObjectWithDefaultValues_WhenDefaultConstructorIsUsed() {
        // Act
        MetaObjectsMetaObjectAttributePostDTO dto = new MetaObjectsMetaObjectAttributePostDTO();

        // Assert
        assertNull(dto.getId());
        assertNull(dto.getSystemName());
        assertNull(dto.getDataType());
        assertNull(dto.getDbColumn());
        assertNull(dto.getDescription());
        assertNull(dto.getDisplayName());
        assertFalse(dto.isMandatory());
        assertFalse(dto.isPrimary());
        assertFalse(dto.isSysAttribute());
        assertEquals(0, dto.getSeqNum());
    }

    @Test
    void shouldCreateObjectWithProvidedValues_WhenParameterizedConstructorIsUsed() {
        // Arrange
        String systemName = "attr-1";
        DataType dataType = DataType.STRING;
        String dbColumnName = "column_name";
        String description = "Test description";
        String displayName = "Test Display Name";
        boolean isMandatory = true;
        boolean isPrimary = false;
        boolean isSystemGen = true;
        int seqNum = 1;

        // Act
        MetaObjectsMetaObjectAttributePostDTO dto=new MetaObjectAttributeDTO();
        dto.setMetaObjectSysName("meta_obj");
        dto.setDataType(dataType);
        dto.setSystemName(systemName);
        dto.setDbColumn(dbColumnName);
        dto.setDescription(description);
        dto.setDisplayName(displayName);
        dto.setMandatory(isMandatory);
        dto.setPrimary(isPrimary);
        dto.setSysAttribute(isSystemGen);
        dto.setSeqNum(seqNum);
        dto.setEventEnabled(true);
        dto.setLogicalKey(1);
        dto.setMetaObjectSysName("meta_obj");
        dto.setOrderBy(1);


        // Assert
        assertEquals(systemName, dto.getSystemName());
        assertEquals(dataType, dto.getDataType());
        assertEquals(dbColumnName, dto.getDbColumn());
        assertEquals(description, dto.getDescription());
        assertEquals(displayName, dto.getDisplayName());
        assertTrue(dto.isMandatory());
        assertFalse(dto.isPrimary());
        assertTrue(dto.isSysAttribute());
        assertEquals(seqNum, dto.getSeqNum());
        assertEquals(1, dto.getLogicalKey());
        assertEquals("meta_obj", dto.getMetaObjectSysName());
        assertEquals(1, dto.getOrderBy());
        assertTrue(dto.isEventEnabled());

    }

    @Test
    void shouldSetAndGetFields_WhenUsingSettersAndGetters() {
        // Arrange
        UUID id = UUID.randomUUID();
        String metaObjectAttributeId = "attr-1";
        DataType dataType = DataType.STRING;
        String dbColumnName = "column_name";
        String description = "Test description";
        String displayName = "Test Display Name";
        boolean isMandatory = true;
        boolean isPrimary = false;
        boolean isSystemGen = true;
        int seqNum = 1;

        MetaObjectsMetaObjectAttributePostDTO dto = new MetaObjectsMetaObjectAttributePostDTO();

        // Act
        dto.setId(id);
        dto.setSystemName(metaObjectAttributeId);
        dto.setDataType(dataType);
        dto.setDbColumn(dbColumnName);
        dto.setDescription(description);
        dto.setDisplayName(displayName);
        dto.setMandatory(isMandatory);
        dto.setPrimary(isPrimary);
        dto.setSysAttribute(isSystemGen);
        dto.setSeqNum(seqNum);

        // Assert
        assertEquals(id, dto.getId());
        assertEquals(metaObjectAttributeId, dto.getSystemName());
        assertEquals(dataType, dto.getDataType());
        assertEquals(dbColumnName, dto.getDbColumn());
        assertEquals(description, dto.getDescription());
        assertEquals(displayName, dto.getDisplayName());
        assertTrue(dto.isMandatory());
        assertFalse(dto.isPrimary());
        assertTrue(dto.isSysAttribute());
        assertEquals(seqNum, dto.getSeqNum());
    }
}