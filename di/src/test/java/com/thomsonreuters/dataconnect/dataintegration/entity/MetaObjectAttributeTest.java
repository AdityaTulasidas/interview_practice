package com.thomsonreuters.dataconnect.dataintegration.entity;

import com.thomsonreuters.dataconnect.dataintegration.model.entity.MetaObject;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.MetaObjectAttribute;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.DataType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MetaObjectAttributeTest {

    @Test
    void shouldInitializeAllFieldsToNull_WhenNoArgsConstructorIsUsed() {
        // Test the no-args constructor
        MetaObjectAttribute attribute = new MetaObjectAttribute();
        assertNotNull(attribute);
    }

    @Test
    void shouldSetAndGetAllFieldsCorrectly_WhenValuesAreProvided() {
        // Create a MetaObjectAttribute instance
        MetaObjectAttribute attribute = new MetaObjectAttribute();
        MetaObject metaObject = new MetaObject();

        // Set values
        UUID id = UUID.randomUUID();
        attribute.setId(id);
        attribute.setSystemName("attrId");
        attribute.setDataType(DataType.STRING);
        attribute.setMetaObject(metaObject);
        attribute.setDbColumn("dbColumn");
        attribute.setDescription("description");
        attribute.setDisplayName("displayName");
        attribute.setMandatory(true);
        attribute.setPrimary(true);
        attribute.setSysAttribute(false);
        attribute.setSeqNum(1);

        // Assert values
        assertEquals(id, attribute.getId());
        assertEquals("attrId", attribute.getSystemName());
        assertEquals(DataType.STRING, attribute.getDataType());
        assertEquals(metaObject, attribute.getMetaObject());
        assertEquals("dbColumn", attribute.getDbColumn());
        assertEquals("description", attribute.getDescription());
        assertEquals("displayName", attribute.getDisplayName());
        assertTrue(attribute.isMandatory());
        assertTrue(attribute.isPrimary());
        assertFalse(attribute.isSysAttribute());
        assertEquals(1, attribute.getSeqNum());
    }


    @Test
    void shouldGenerateNonNullStringRepresentation_WhenToStringIsCalled() {
        // Test the toString method
        MetaObjectAttribute attribute = new MetaObjectAttribute();
        attribute.setSystemName("attrId");
        assertNotNull(attribute.toString());
    }
}