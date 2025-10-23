package com.aditya.dataconnect.executionengine.pojo;

import com.aditya.dataconnect.executionengine.model.pojo.DataCollectionObject;
import com.aditya.dataconnect.executionengine.model.pojo.DataUnitObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataUnitObjectTest {

    @Test
    void testGetterAndSetterMethods() {
        DataCollectionObject dataCollectionObject = new DataCollectionObject();
        DataUnitObject dataUnitObject = new DataUnitObject();
        dataUnitObject.setDatacollectionobject(dataCollectionObject);

        assertEquals(dataCollectionObject, dataUnitObject.getDatacollectionobject());
    }

    @Test
    void testToStringMethod() {
        DataUnitObject dataUnitObject = new DataUnitObject();
        dataUnitObject.setDatacollectionobject(new DataCollectionObject());

        String toString = dataUnitObject.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("datacollectionobject"));
    }

    @Test
    void testEqualsAndHashCode() {
        DataUnitObject dataUnitObject1 = new DataUnitObject();
        DataUnitObject dataUnitObject2 = new DataUnitObject();

        assertEquals(dataUnitObject1, dataUnitObject2);
        assertEquals(dataUnitObject1.hashCode(), dataUnitObject2.hashCode());

        dataUnitObject1.setDatacollectionobject(new DataCollectionObject());
        assertNotEquals(dataUnitObject1, dataUnitObject2);
        assertNotEquals(dataUnitObject1.hashCode(), dataUnitObject2.hashCode());
    }
}