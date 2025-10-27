package com.thomsonreuters.dataconnect.dataintegration.entity.pojo;

import com.thomsonreuters.dataconnect.dataintegration.model.pojo.RequestBodyDataUnitList;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RequestBodyDataUnitListTest {

    @Test
    void shouldSetAndGetObjectIdsCorrectly_WhenSetterAndGetterAreUsed() {
        // Create test data
        List<Object> objIds = List.of(UUID.randomUUID(), UUID.randomUUID());

        // Create an instance of RequestBodyDataUnitList
        RequestBodyDataUnitList requestBodyDataUnitList = new RequestBodyDataUnitList();

        // Set obj_ids using the setter
        requestBodyDataUnitList.setObjIds(objIds);

        // Assert the obj_ids are correctly set
        assertEquals(objIds, requestBodyDataUnitList.getObjIds());
    }

    @Test
    void shouldGenerateNonNullStringRepresentation_WhenToStringIsCalled() {
        // Create test data
        List<Object> objIds = List.of(UUID.randomUUID());

        // Create an instance of RequestBodyDataUnitList
        RequestBodyDataUnitList requestBodyDataUnitList = new RequestBodyDataUnitList();
        requestBodyDataUnitList.setObjIds(objIds);

        // Assert the toString method does not throw exceptions
        assertNotNull(requestBodyDataUnitList.toString());
    }

}