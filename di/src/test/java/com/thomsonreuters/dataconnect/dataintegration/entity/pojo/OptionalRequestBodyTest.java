package com.thomsonreuters.dataconnect.dataintegration.entity.pojo;

import com.thomsonreuters.dataconnect.dataintegration.model.pojo.OptionalRequestBody;
import com.thomsonreuters.dataconnect.dataintegration.model.pojo.RequestBodyDataUnitList;
import com.thomsonreuters.dataconnect.dataintegration.model.pojo.RequestBodyDataUnitObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OptionalRequestBodyTest {

    @Test
    void shouldSetAndGetRequestDataUnitListAndObjectCorrectly_WhenSettersAndGettersAreUsed() {
        // Create instances of RequestBodyDataUnitList and RequestBodyDataUnitObject
        RequestBodyDataUnitList dataUnitList = new RequestBodyDataUnitList();
        RequestBodyDataUnitObject dataUnitObject = new RequestBodyDataUnitObject();

        // Create an OptionalRequestBody instance
        OptionalRequestBody optionalRequestBody = new OptionalRequestBody();

        // Set values using setters
        optionalRequestBody.setRequestDataUnitList(dataUnitList);
        optionalRequestBody.setRequestDataUnitObject(dataUnitObject);

        // Assert values are correctly set and retrieved
        assertEquals(dataUnitList, optionalRequestBody.getRequestDataUnitList());
        assertEquals(dataUnitObject, optionalRequestBody.getRequestDataUnitObject());
    }

    @Test
    void shouldGenerateNonNullStringRepresentation_WhenToStringIsCalled() {
        // Create an OptionalRequestBody instance
        OptionalRequestBody optionalRequestBody = new OptionalRequestBody();

        // Assert the toString method does not throw exceptions
        assertNotNull(optionalRequestBody.toString());
    }

}