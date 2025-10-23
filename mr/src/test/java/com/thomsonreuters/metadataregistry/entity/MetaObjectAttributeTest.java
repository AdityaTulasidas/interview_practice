package com.thomsonreuters.metadataregistry.entity;

import com.thomsonreuters.metadataregistry.model.entity.MetaObject;
import com.thomsonreuters.metadataregistry.model.entity.MetaObjectAttribute;
import com.thomsonreuters.metadataregistry.model.entity.enums.DataType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MetaObjectAttributeTest {

    @Test
    void shouldSetFields_WhenMetaObjectAttributeIsCreated() {
        String attributeId = "attr-1";
        DataType dataType = DataType.STRING;
        String dbColumnName = "column_name";
        String description = "Test description";
        String displayName = "Test Display Name";
        boolean isMandatory = true;
        boolean isPrimary = false;
        boolean isSystemGen = true;
        int seqNum = 1;

        MetaObject metaObject = new MetaObject();

        MetaObjectAttribute attribute=new MetaObjectAttribute();
        attribute.setSystemName(attributeId);
        attribute.setDataType(dataType);
        attribute.setMetaObject(metaObject);
        attribute.setDbColumn(dbColumnName);
        attribute.setDescription(description);
        attribute.setDisplayName(displayName);
        attribute.setMandatory(isMandatory);
        attribute.setPrimary(isPrimary);
        attribute.setSysAttribute(isSystemGen);
        attribute.setSeqNum(seqNum);
        assertEquals(attributeId, attribute.getSystemName());
        assertEquals(dataType, attribute.getDataType());
        assertEquals(dbColumnName, attribute.getDbColumn());
        assertEquals(description, attribute.getDescription());
        assertEquals(displayName, attribute.getDisplayName());
        assertTrue(attribute.isMandatory());
        assertFalse(attribute.isPrimary());
        assertTrue(attribute.isSysAttribute());
        assertEquals(seqNum, attribute.getSeqNum());
        assertEquals(metaObject, attribute.getMetaObject());
    }
}