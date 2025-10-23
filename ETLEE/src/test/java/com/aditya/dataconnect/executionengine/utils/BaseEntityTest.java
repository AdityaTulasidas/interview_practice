package com.aditya.dataconnect.executionengine.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BaseEntityTest {

    private BaseEntity baseEntity;

    @BeforeEach
    void setUp() {
        baseEntity = new BaseEntity();
    }

    @Test
    void shouldSetAndGetCreatedBy() {
        String createdBy = "user1";
        baseEntity.setCreatedBy(createdBy);
        assertEquals(createdBy, baseEntity.getCreatedBy());
    }

    @Test
    void shouldSetAndGetUpdatedBy() {
        String updatedBy = "user2";
        baseEntity.setUpdatedBy(updatedBy);
        assertEquals(updatedBy, baseEntity.getUpdatedBy());
    }

    @Test
    void shouldSetAndGetCreatedAt() {
        LocalDateTime createdAt = LocalDateTime.now();
        baseEntity.setCreatedAt(createdAt);
        assertEquals(createdAt, baseEntity.getCreatedAt());
    }

    @Test
    void shouldSetAndGetUpdatedAt() {
        LocalDateTime updatedAt = LocalDateTime.now();
        baseEntity.setUpdatedAt(updatedAt);
        assertEquals(updatedAt, baseEntity.getUpdatedAt());
    }
}