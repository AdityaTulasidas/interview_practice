package com.thomsonreuters.dataconnect.dataintegration.entity.pojo;

import com.thomsonreuters.dataconnect.dataintegration.dto.MetaObjectDTO;
import com.thomsonreuters.dataconnect.dataintegration.model.pojo.DataCollectionObject;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DataCollectionObjectTest {

    @Test
    void shouldInitializeFieldsCorrectly_WhenConstructorIsUsed() {
        MetaObjectDTO metaModel = new MetaObjectDTO();
        Map<String, DataCollectionObject> childList = new HashMap<>();
        DataCollectionObject dataCollectionObject = new DataCollectionObject(metaModel, childList);

        assertEquals(metaModel, dataCollectionObject.getMetaModel());
        assertEquals(childList, dataCollectionObject.getChildList());
    }

    @Test
    void shouldSetChildListCorrectly_WhenChildIsAdded() {
        DataCollectionObject dataCollectionObject = new DataCollectionObject();
        DataCollectionObject child = new DataCollectionObject();
        dataCollectionObject.setChildList("child1", child);

        assertNotNull(dataCollectionObject.getChildList());
        assertEquals(child, dataCollectionObject.getChildList().get("child1"));
    }

    @Test
    void shouldReturnMetaObjectId_WhenMetaModelIsSet() {
        MetaObjectDTO metaModel = new MetaObjectDTO();
        metaModel.setId(UUID.randomUUID());
        DataCollectionObject dataCollectionObject = new DataCollectionObject();
        dataCollectionObject.setMetaModel(metaModel);

        assertEquals(metaModel.getId().toString(), dataCollectionObject.getMetaObjectId());
    }
}