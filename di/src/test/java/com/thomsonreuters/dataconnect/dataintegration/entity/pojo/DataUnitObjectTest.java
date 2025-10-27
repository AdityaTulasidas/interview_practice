package com.thomsonreuters.dataconnect.dataintegration.entity.pojo;

import com.thomsonreuters.dataconnect.dataintegration.model.pojo.DataCollectionObject;
import com.thomsonreuters.dataconnect.dataintegration.model.pojo.DataUnitObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataUnitObjectTest {

    @Test
    void shouldSetDataCollectionObjectCorrectly_WhenConstructorIsUsed() {
        // Create a DataCollectionObject instance
        DataCollectionObject dataCollectionObject = new DataCollectionObject();

        // Create a DataUnitObject instance using the constructor
        DataUnitObject dataUnitObject = new DataUnitObject(dataCollectionObject);

        // Assert the DataCollectionObject is correctly set
        assertEquals(dataCollectionObject, dataUnitObject.getDatacollectionobject());
    }

    @Test
    void shouldSetDataCollectionObjectCorrectly_WhenSetterIsUsed() {
        // Create a DataUnitObject instance
        DataUnitObject dataUnitObject = new DataUnitObject(null);

        // Create a DataCollectionObject instance
        DataCollectionObject dataCollectionObject = new DataCollectionObject();

        // Set the DataCollectionObject using the setter
        dataUnitObject.setDatacollectionobject(dataCollectionObject);

        // Assert the DataCollectionObject is correctly set
        assertEquals(dataCollectionObject, dataUnitObject.getDatacollectionobject());
    }

    @Test
    void shouldGenerateNonNullStringRepresentation_WhenToStringIsCalled() {
        // Create a DataUnitObject instance
        DataUnitObject dataUnitObject = new DataUnitObject(new DataCollectionObject());

        // Assert the toString method does not throw exceptions
        assertNotNull(dataUnitObject.toString());
    }

    @Test
    void shouldBeEqualAndHaveSameHashCode_WhenDataCollectionObjectsAreIdentical() {
        // Create two identical DataUnitObject objects
        DataCollectionObject dataCollectionObject = new DataCollectionObject();

        DataUnitObject dataUnitObject1 = new DataUnitObject(dataCollectionObject);
        DataUnitObject dataUnitObject2 = new DataUnitObject(dataCollectionObject);

        // Assert equality and hashCode
        assertEquals(dataUnitObject1, dataUnitObject2);
        assertEquals(dataUnitObject1.hashCode(), dataUnitObject2.hashCode());
    }
}