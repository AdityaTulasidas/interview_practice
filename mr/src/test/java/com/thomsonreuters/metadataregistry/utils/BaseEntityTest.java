package com.thomsonreuters.metadataregistry.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BaseEntityTest {

    @Test
    void shouldSetAndGetFields_WhenBaseEntityIsUsed() {
        BaseEntity baseEntity = new BaseEntity();
        LocalDateTime now = LocalDateTime.now();

        baseEntity.setCreatedBy("user1");
        baseEntity.setUpdatedBy("user2");
        baseEntity.setCreatedAt(now);
        baseEntity.setUpdatedAt(now);

        assertEquals("user1", baseEntity.getCreatedBy());
        assertEquals("user2", baseEntity.getUpdatedBy());
        assertEquals(now, baseEntity.getCreatedAt());
        assertEquals(now, baseEntity.getUpdatedAt());
    }
}