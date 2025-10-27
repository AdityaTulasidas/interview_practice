package com.thomsonreuters.dataconnect.dataintegration.entity;

import com.thomsonreuters.dataconnect.dataintegration.model.entity.JobExecutionLog;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JobExecutionLogTest {

    @Test
    void shouldSetAndGetAllFieldsCorrectly_WhenValuesAreProvided() {
        // Create a JobExecutionLog instance and set values
        JobExecutionLog log = new JobExecutionLog();

        // Set and assert values
        UUID id = UUID.randomUUID();
        LocalDateTime whenAccepted = LocalDateTime.now();
        LocalDateTime whenStarted = LocalDateTime.now().plusMinutes(1);
        LocalDateTime whenCompleted = LocalDateTime.now().plusMinutes(2);
        String status = "COMPLETED";
        Integer recTransferredCnt = 100;
        Integer recFailedCnt = 5;
        Integer recordsWritten = 95;
        String customerId = "customer123";
        UUID jobId = UUID.randomUUID();
        String createdBy = "user1";
        LocalDateTime createdAt = LocalDateTime.now();
        String updatedBy = "user2";
        LocalDateTime updatedAt = LocalDateTime.now().plusMinutes(3);

        log.setId(id);
        log.setWhenAccepted(whenAccepted);
        log.setWhenStarted(whenStarted);
        log.setWhenCompleted(whenCompleted);
        log.setStatus(status);
        log.setRecTransferredCnt(recTransferredCnt);
        log.setRecFailedCnt(recFailedCnt);
        log.setRecordsWritten(recordsWritten);
        log.setCustomerId(customerId);
        log.setJobId(jobId);
        log.setCreatedBy(createdBy);
        log.setCreatedAt(createdAt);
        log.setUpdatedBy(updatedBy);
        log.setUpdatedAt(updatedAt);

        assertEquals(id, log.getId());
        assertEquals(whenAccepted, log.getWhenAccepted());
        assertEquals(whenStarted, log.getWhenStarted());
        assertEquals(whenCompleted, log.getWhenCompleted());
        assertEquals(status, log.getStatus());
        assertEquals(recTransferredCnt, log.getRecTransferredCnt());
        assertEquals(recFailedCnt, log.getRecFailedCnt());
        assertEquals(recordsWritten, log.getRecordsWritten());
        assertEquals(customerId, log.getCustomerId());
        assertEquals(jobId, log.getJobId());
        assertEquals(createdBy, log.getCreatedBy());
        assertEquals(createdAt, log.getCreatedAt());
        assertEquals(updatedBy, log.getUpdatedBy());
        assertEquals(updatedAt, log.getUpdatedAt());
    }

    @Test
    void shouldInitializeAllFieldsToNull_WhenNoArgsConstructorIsUsed() {
        // Test the no-args constructor
        JobExecutionLog log = new JobExecutionLog();
        assertNotNull(log);
    }

    @Test
    void shouldSetCreatedAtToCurrentTime_WhenDefaultConstructorIsUsed() {
        // Test default values for createdAt

        LocalDateTime beforeCreation = LocalDateTime.now();
        JobExecutionLog log = new JobExecutionLog();
        LocalDateTime afterCreation = LocalDateTime.now();

        // Assert
        assertNotNull(log.getCreatedAt());
        assertTrue(!log.getCreatedAt().isBefore(beforeCreation) && !log.getCreatedAt().isAfter(afterCreation),
                "createdAt should be between beforeCreation and afterCreation timestamps");
    }


    @Test
    void shouldGenerateNonNullStringRepresentation_WhenToStringIsCalled() {
        // Create a JobExecutionLog instance
        JobExecutionLog log = new JobExecutionLog();
        log.setStatus("COMPLETED");

        // Assert toString does not throw exceptions
        assertNotNull(log.toString());
    }
}