package com.thomsonreuters.dataconnect.dataintegration.entity.pojo;

import com.thomsonreuters.dataconnect.dataintegration.model.pojo.DataCollectionObject;
import com.thomsonreuters.dataconnect.dataintegration.model.pojo.DataSyncRequest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DataSyncRequestTest {

    @Test
    void shouldSetAndGetObjIdsAndDataCollectionObjectSuccessfully_WhenValuesAreProvided() {
        // Create test data
        List<UUID> objIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        DataCollectionObject dataCollectionObject = new DataCollectionObject();

        // Create and set values in DataSyncRequest
        DataSyncRequest request = new DataSyncRequest();
        request.setObjIds(objIds);
        request.setDataCollectionObject(dataCollectionObject);

        // Assert values are correctly set
        assertEquals(objIds, request.getObjIds());
        assertEquals(dataCollectionObject, request.getDataCollectionObject());
    }

    @Test
    void shouldInitializeFieldsToNull_WhenNoArgsConstructorIsUsed() {
        // Test the no-args constructor
        DataSyncRequest request = new DataSyncRequest();
        assertNull(request.getObjIds());
        assertNull(request.getDataCollectionObject());
    }

    @Test
    void shouldGenerateNonNullStringRepresentation_WhenToStringIsCalled() {
        // Create test data
        List<UUID> objIds = List.of(UUID.randomUUID());
        DataCollectionObject dataCollectionObject = new DataCollectionObject();

        // Set values in DataSyncRequest
        DataSyncRequest request = new DataSyncRequest();
        request.setObjIds(objIds);
        request.setDataCollectionObject(dataCollectionObject);

        // Assert the toString method does not throw exceptions
        assertNotNull(request.toString());
    }

}