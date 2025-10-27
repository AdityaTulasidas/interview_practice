package com.thomsonreuters.dataconnect.dataintegration.entity.pojo;

import com.thomsonreuters.dataconnect.dataintegration.model.pojo.DataCollectionObject;
import com.thomsonreuters.dataconnect.dataintegration.model.pojo.RequestBodyDataUnitObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestBodyDataUnitObjectTest {

    @Test
    void shouldSetAndGetDataCollectionObjectCorrectly_WhenSettersAndGettersAreUsed() {
        // Create a DataCollectionObject instance
        DataCollectionObject dataCollectionObject = new DataCollectionObject();

        // Create an instance of RequestBodyDataUnitObject
        RequestBodyDataUnitObject requestBodyDataUnitObject = new RequestBodyDataUnitObject();

        // Set data_collection_object using the setter
        requestBodyDataUnitObject.setDataCollectionObject(dataCollectionObject);

        // Assert the data_collection_object is correctly set
        assertEquals(dataCollectionObject, requestBodyDataUnitObject.getDataCollectionObject());
    }

    @Test
    void shouldGenerateNonNullStringRepresentation_WhenToStringIsCalled() {
        // Create an instance of RequestBodyDataUnitObject
        RequestBodyDataUnitObject requestBodyDataUnitObject = new RequestBodyDataUnitObject();

        // Assert the toString method does not throw exceptions
        assertNotNull(requestBodyDataUnitObject.toString());
    }

}