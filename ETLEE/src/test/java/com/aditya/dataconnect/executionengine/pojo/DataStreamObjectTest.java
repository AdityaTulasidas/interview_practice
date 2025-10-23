package com.aditya.dataconnect.executionengine.pojo;

import com.aditya.dataconnect.executionengine.dto.MetaObjectDTO;
import com.aditya.dataconnect.executionengine.model.pojo.DataStreamObject;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class DataStreamObjectTest {

    @Test
    void testGetterAndSetterMethods() {
        MetaObjectDTO parentMetaModel = new MetaObjectDTO();
        MetaObjectDTO childMetaModel = new MetaObjectDTO();
        List<ConcurrentHashMap<String, Object>> parentDataObject = List.of(new ConcurrentHashMap<>());
        List<ConcurrentHashMap<String, Object>> childDataObject = List.of(new ConcurrentHashMap<>());

        DataStreamObject dataStreamObject = new DataStreamObject();
        dataStreamObject.setParentMetaModel(parentMetaModel);
        dataStreamObject.setChildMetaModel(childMetaModel);
        dataStreamObject.setParentDataObject(parentDataObject);
        dataStreamObject.setChildDataObject(childDataObject);

        assertEquals(parentMetaModel, dataStreamObject.getParentMetaModel());
        assertEquals(childMetaModel, dataStreamObject.getChildMetaModel());
        assertEquals(parentDataObject, dataStreamObject.getParentDataObject());
        assertEquals(childDataObject, dataStreamObject.getChildDataObject());
    }

    @Test
    void testToStringMethod() {
        DataStreamObject dataStreamObject = new DataStreamObject();
        dataStreamObject.setParentMetaModel(new MetaObjectDTO());
        dataStreamObject.setChildMetaModel(new MetaObjectDTO());

        String toString = dataStreamObject.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("parentMetaModel"));
        assertTrue(toString.contains("childMetaModel"));
    }

    @Test
    void testEqualsAndHashCode() {
        DataStreamObject dataStreamObject1 = new DataStreamObject();
        DataStreamObject dataStreamObject2 = new DataStreamObject();

        assertEquals(dataStreamObject1, dataStreamObject2);
        assertEquals(dataStreamObject1.hashCode(), dataStreamObject2.hashCode());

        dataStreamObject1.setParentMetaModel(new MetaObjectDTO());
        assertNotEquals(dataStreamObject1, dataStreamObject2);
        assertNotEquals(dataStreamObject1.hashCode(), dataStreamObject2.hashCode());
    }
}