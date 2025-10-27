package com.thomsonreuters.dataconnect.dataintegration.dto;

import com.thomsonreuters.dataconnect.dataintegration.model.entity.MetaObjectAttribute;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.DataType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

 class MetaObjectAttributeDTOTest {

    @Test
    void shouldSetAndGetAllFieldsCorrectly_WhenDTOIsInitialized() {
        UUID id = UUID.randomUUID();
        String metaObjectAttributeId = "attribute_123";
        DataType dataType = DataType.STRING; // Replace with actual enum value
        String dbColumnName = "test_column";
        UUID metaObjectId = UUID.randomUUID();
        String description = "Test Description";
        String displayName = "Test Display Name";
        boolean isMandatory = true;
        boolean isPrimary = false;
        boolean isSystemGen = true;
        int seqNum = 1;
        String jsonTag = "test_json_tag";
        Map<String, MetaObjectAttribute> attributes = new HashMap<>();
        List<MetaObjectRelationDTO> childRelations = List.of(new MetaObjectRelationDTO());

        MetaObjectAttributeDTO dto = new MetaObjectAttributeDTO();
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
        dto.setAttributes(attributes);
        dto.setChildRelations(childRelations);

        assertEquals(id, dto.getId());
        assertEquals(metaObjectAttributeId, dto.getSystemName());
        assertEquals(dataType, dto.getDataType());
        assertEquals(dbColumnName, dto.getDbColumn());
        assertEquals(description, dto.getDescription());
        assertEquals(displayName, dto.getDisplayName());
        assertEquals(isMandatory, dto.isMandatory());
        assertEquals(isPrimary, dto.isPrimary());
        assertEquals(isSystemGen, dto.isSysAttribute());
        assertEquals(seqNum, dto.getSeqNum());
        assertEquals(attributes, dto.getAttributes());
        assertEquals(childRelations, dto.getChildRelations());
    }
}