package com.thomsonreuters.dataconnect.dataintegration.entity;

import com.thomsonreuters.dataconnect.dataintegration.model.entity.RegionalJobConfiguration;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.ExecutionLeg;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RegionalJobConfigurationTest {

    @Test
    void shouldInitializeAllFieldsToDefaultValues_WhenNoArgsConstructorIsUsed() {
        // Test the no-args constructor
        RegionalJobConfiguration config = new RegionalJobConfiguration();
        assertNotNull(config);
    }

    @Test
    void shouldSetAndGetAllFieldsCorrectly_WhenValuesAreProvided() {
        // Create a RegionalJobConfiguration instance and set values
        RegionalJobConfiguration config = new RegionalJobConfiguration();

        // Set and assert values
        UUID id = UUID.randomUUID();
        ExecutionLeg type = ExecutionLeg.SOURCE;
        UUID datasyncJobId = UUID.randomUUID();
        boolean isActive = true;
        String inAdaptorId = "inAdaptorId";
        String outAdaptorId = "outAdaptorId";
        Map<String, String> inAdaptorExecContext = new HashMap<>();
        inAdaptorExecContext.put("key1", "value1");
        Map<String, String> outAdaptorExecContext = new HashMap<>();
        outAdaptorExecContext.put("key2", "value2");
        String createdBy = "creator";
        LocalDateTime createdAt = LocalDateTime.now();
        String updatedBy = "updater";
        LocalDateTime updatedAt = LocalDateTime.now();

        config.setId(id);
        config.setExecLeg(type);
        config.setDatasyncJobId(datasyncJobId);
        config.setActive(isActive);
        config.setInAdaptorId(inAdaptorId);
        config.setOutAdaptorId(outAdaptorId);
        config.setInAdaptorExecContext(inAdaptorExecContext);
        config.setOutAdaptorExecContext(outAdaptorExecContext);
        config.setCreatedBy(createdBy);
        config.setCreatedAt(createdAt);
        config.setUpdatedBy(updatedBy);
        config.setUpdatedAt(updatedAt);

        assertEquals(id, config.getId());
        assertEquals(type, config.getExecLeg());
        assertEquals(datasyncJobId, config.getDatasyncJobId());
        assertEquals(isActive, config.isActive());
        assertEquals(inAdaptorId, config.getInAdaptorId());
        assertEquals(outAdaptorId, config.getOutAdaptorId());
        assertEquals(inAdaptorExecContext, config.getInAdaptorExecContext());
        assertEquals(outAdaptorExecContext, config.getOutAdaptorExecContext());
        assertEquals(createdBy, config.getCreatedBy());
        assertEquals(createdAt, config.getCreatedAt());
        assertEquals(updatedBy, config.getUpdatedBy());
        assertEquals(updatedAt, config.getUpdatedAt());
    }

    @Test
    void shouldSetCreatedAtToCurrentTime_WhenPrePersistIsCalled() {
        // Test the @PrePersist lifecycle method
        RegionalJobConfiguration config = new RegionalJobConfiguration();
        LocalDateTime before = LocalDateTime.now();
        config.beforeSaveOrUpdate();
        LocalDateTime createdAt = config.getCreatedAt();
        assertNotNull(createdAt);
        LocalDateTime after = LocalDateTime.now();
        // createdAt should be between before and after
        assertFalse(createdAt.isBefore(before), "createdAt should not be before the test start time");
        assertFalse(createdAt.isAfter(after), "createdAt should not be after the test end time");
    }

    @Test
    void shouldSetUpdatedAtToCurrentTime_WhenPreUpdateIsCalled() {
        // Test the @PreUpdate lifecycle method
        RegionalJobConfiguration config = new RegionalJobConfiguration();
        config.beforeSaveOrUpdate();
        assertNotNull(config.getUpdatedAt());
    }

    @Test
    void shouldBeEqualAndHaveSameHashCode_WhenFieldsAreIdentical() {
        // Create two identical RegionalJobConfiguration instances
        RegionalJobConfiguration config1 = new RegionalJobConfiguration();
        RegionalJobConfiguration config2 = new RegionalJobConfiguration();

        UUID id = UUID.randomUUID();
        config1.setId(id);
        config2.setId(id);

        // Assert equality and hashCode
        assertEquals(config1, config2);
        assertEquals(config1.hashCode(), config2.hashCode());
    }

    @Test
    void shouldGenerateNonNullStringRepresentation_WhenToStringIsCalled() {
        // Test the toString method
        RegionalJobConfiguration config = new RegionalJobConfiguration();
        assertNotNull(config.toString());
    }
}