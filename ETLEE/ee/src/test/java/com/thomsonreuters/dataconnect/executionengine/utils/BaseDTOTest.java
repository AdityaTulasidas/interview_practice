package com.thomsonreuters.dataconnect.executionengine.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BaseDTOTest {

    private BaseDTO baseDTO;

    @BeforeEach
    void setUp() {
        baseDTO = new BaseDTO();
    }

    @Test
    void shouldSetAndGetCreatedBy() {
        String createdBy = "user1";
        baseDTO.setCreatedBy(createdBy);
        assertEquals(createdBy, baseDTO.getCreatedBy());
    }

    @Test
    void shouldSetAndGetUpdatedBy() {
        String updatedBy = "user2";
        baseDTO.setUpdatedBy(updatedBy);
        assertEquals(updatedBy, baseDTO.getUpdatedBy());
    }

    @Test
    void shouldSetAndGetCreatedAt() {
        LocalDateTime createdAt = LocalDateTime.now();
        baseDTO.setCreatedAt(createdAt);
        assertEquals(createdAt, baseDTO.getCreatedAt());
    }

    @Test
    void shouldSetAndGetUpdatedAt() {
        LocalDateTime updatedAt = LocalDateTime.now();
        baseDTO.setUpdatedAt(updatedAt);
        assertEquals(updatedAt, baseDTO.getUpdatedAt());
    }
}