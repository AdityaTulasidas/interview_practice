package com.thomsonreuters.dataconnect.dataintegration.entity.pojo;

import com.thomsonreuters.dataconnect.dataintegration.model.pojo.DataUnitList;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DataUnitListTest {

    @Test
    void shouldInitializeObjIdsCorrectly_WhenConstructorIsUsed() {
        // Create test data
        List<Object> objIds = List.of(UUID.randomUUID(), UUID.randomUUID());

        // Create a DataUnitList instance using the constructor
        DataUnitList dataUnitList = new DataUnitList(objIds);

        // Assert the objIds are correctly set
        assertEquals(objIds, dataUnitList.getObjIds());
    }

    @Test
    void shouldSetObjIdsCorrectly_WhenSetterIsUsed() {
        // Create a DataUnitList instance
        DataUnitList dataUnitList = new DataUnitList(null);

        // Create test data
        List<Object> objIds = List.of(UUID.randomUUID(), UUID.randomUUID());

        // Set objIds using the setter
        dataUnitList.setObjIds(objIds);

        // Assert the objIds are correctly set
        assertEquals(objIds, dataUnitList.getObjIds());
    }

    @Test
    void shouldGenerateNonNullStringRepresentation_WhenToStringIsCalled() {
        // Create test data
        List<Object> objIds = List.of(UUID.randomUUID());

        // Create a DataUnitList instance
        DataUnitList dataUnitList = new DataUnitList(objIds);

        // Assert the toString method does not throw exceptions
        assertNotNull(dataUnitList.toString());
    }

    @Test
    void shouldBeEqualAndHaveSameHashCode_WhenObjIdsAreIdentical() {
        // Create test data
        List<Object> objIds = List.of(UUID.randomUUID());

        // Create two identical DataUnitList objects
        DataUnitList dataUnitList1 = new DataUnitList(objIds);
        DataUnitList dataUnitList2 = new DataUnitList(objIds);

        // Assert equality and hashCode
        assertEquals(dataUnitList1, dataUnitList2);
        assertEquals(dataUnitList1.hashCode(), dataUnitList2.hashCode());
    }
}