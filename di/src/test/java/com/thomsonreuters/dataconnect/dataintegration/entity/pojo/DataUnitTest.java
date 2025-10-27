package com.thomsonreuters.dataconnect.dataintegration.entity.pojo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomsonreuters.dataconnect.dataintegration.dto.MetaObjectDTO;
import com.thomsonreuters.dataconnect.dataintegration.model.pojo.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DataUnitTest {

    @Test
    void shouldSetAndGetContentSuccessfully_WhenContentIsProvided() {
        // Create a DataUnitContent instance
        DataUnitContent content = new DataUnitList(new ArrayList<>());

        // Set and get the content in DataUnit
        DataUnit dataUnit = new DataUnit();
        dataUnit.setContent(content);

        // Assert the content is correctly set and retrieved
        assertEquals(content, dataUnit.getContent());
    }

    @Test
    void shouldGenerateNonNullStringRepresentation_WhenToStringIsCalled() {
        // Create a DataUnit instance
        DataUnit dataUnit = new DataUnit();
        dataUnit.setContent(new DataUnitList(new ArrayList<>()));

        // Assert the toString method does not throw exceptions
        assertNotNull(dataUnit.toString());
    }

    @Test
    void shouldBeEqualAndHaveSameHashCode_WhenContentIsIdentical() {
        // Create two identical DataUnit objects
        DataUnitContent content = new DataUnitList(new ArrayList<>());

        DataUnit dataUnit1 = new DataUnit();
        dataUnit1.setContent(content);

        DataUnit dataUnit2 = new DataUnit();
        dataUnit2.setContent(content);

        // Assert equality and hashCode
        assertEquals(dataUnit1, dataUnit2);
        assertEquals(dataUnit1.hashCode(), dataUnit2.hashCode());
    }
}