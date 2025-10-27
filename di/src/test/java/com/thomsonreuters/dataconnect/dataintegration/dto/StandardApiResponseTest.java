package com.thomsonreuters.dataconnect.dataintegration.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StandardApiResponseTest {

    @Test
    void shouldSetAndGetMessageAndDataCorrectly_WhenStandardApiResponseIsInitialized() {
        StandardApiResponse response = new StandardApiResponse("Success", "Data");

        assertEquals("Success", response.getMessage());
        assertEquals("Data", response.getData());

        response.setMessage("Error");
        response.setData("ErrorData");

        assertEquals("Error", response.getMessage());
        assertEquals("ErrorData", response.getData());
    }
}