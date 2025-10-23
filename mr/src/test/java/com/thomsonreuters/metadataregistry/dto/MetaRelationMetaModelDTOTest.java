package com.thomsonreuters.metadataregistry.dto;

import com.thomsonreuters.metadataregistry.model.dto.MetaObjectDTO;
import com.thomsonreuters.metadataregistry.model.dto.MetaRelationMetaModelDTO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MetaRelationMetaModelDTOTest {

    @Test
    void shouldSetAndGetFields_WhenValidValuesProvided() {
        MetaRelationMetaModelDTO metaRelationMetaModelDTO = new MetaRelationMetaModelDTO();
        MetaObjectDTO parentObject = new MetaObjectDTO();
        metaRelationMetaModelDTO.setParentObject(parentObject);

        assertEquals(parentObject, metaRelationMetaModelDTO.getParentObject());
    }
}