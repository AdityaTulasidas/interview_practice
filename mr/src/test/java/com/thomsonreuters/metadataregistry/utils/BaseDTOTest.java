package com.thomsonreuters.metadataregistry.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BaseDTOTest {

    @Test
    void shouldSetAndGetFields_WhenBaseDTOIsUsed() {
        BaseDTO baseDTO = new BaseDTO();
        LocalDateTime now = LocalDateTime.now();

        baseDTO.setCreatedBy("user1");
        baseDTO.setUpdatedBy("user2");
        baseDTO.setCreatedAt(now);
        baseDTO.setUpdatedAt(now);

        assertEquals("user1", baseDTO.getCreatedBy());
        assertEquals("user2", baseDTO.getUpdatedBy());
        assertEquals(now, baseDTO.getCreatedAt());
        assertEquals(now, baseDTO.getUpdatedAt());
    }
}