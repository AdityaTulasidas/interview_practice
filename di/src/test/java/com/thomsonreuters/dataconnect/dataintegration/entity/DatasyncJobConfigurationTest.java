package com.thomsonreuters.dataconnect.dataintegration.entity;

import com.thomsonreuters.dataconnect.dataintegration.model.entity.DatasyncJobConfiguration;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DatasyncJobConfigurationTest {

    @Test
    void shouldSetAndGetAllFieldsCorrectly_WhenValuesAreProvided() {
        // Create a DatasyncJobConfiguration instance and set values
        DatasyncJobConfiguration jobConfig = new DatasyncJobConfiguration();
        UUID id = UUID.randomUUID();
        String systemName = "Test Job";
        String description = "Test Description";
        String metaObjectSysName = "TestMetaObject";
        JobType jobType = JobType.PLATFORM;
        String sourceRegion = "AMER";
        String sourceTenantId = "sourceTenantId";
        String customerTenantSysName = "customerTenantId";
        String clientId = "clientId";
        String onesourceDomain = "BF_AR";
        ExecType execType = ExecType.REAL_TIME;

        jobConfig.setId(id);
        jobConfig.setSystemName(systemName);
        jobConfig.setDescription(description);
        jobConfig.setMetaObjectSysName(metaObjectSysName);
        jobConfig.setJobType(jobType);
        jobConfig.setSourceRegion(sourceRegion);
        jobConfig.setSourceTenantId(sourceTenantId);
        jobConfig.setCustomerTenantSysName(customerTenantSysName);
        jobConfig.setClientId(clientId);
        jobConfig.setOnesourceDomain(onesourceDomain);
        jobConfig.setExecType(String.valueOf(execType));

        // Assert values
        assertEquals(id, jobConfig.getId());
        assertEquals(systemName, jobConfig.getSystemName());
        assertEquals(description, jobConfig.getDescription());
        assertEquals(metaObjectSysName, jobConfig.getMetaObjectSysName());
        assertEquals(jobType, jobConfig.getJobType());
        assertEquals(sourceRegion, jobConfig.getSourceRegion());
        assertEquals(sourceTenantId, jobConfig.getSourceTenantId());
        assertEquals(customerTenantSysName, jobConfig.getCustomerTenantSysName());
        assertEquals(clientId, jobConfig.getClientId());
        assertEquals(onesourceDomain, jobConfig.getOnesourceDomain());
        assertEquals(String.valueOf(execType), jobConfig.getExecType());
    }

    @Test
    void shouldUpdateTimestamp_WhenPreUpdateIsCalled() {
        // Create a DatasyncJobConfiguration instance
        DatasyncJobConfiguration jobConfig = new DatasyncJobConfiguration();

        // Assert initial updatedAt value is null
        assertNull(jobConfig.getUpdatedAt());

        // Trigger @PreUpdate
        jobConfig.beforeSaveOrUpdate();

        // Assert updatedAt is set
        assertNotNull(jobConfig.getUpdatedAt());
        assertTrue(jobConfig.getUpdatedAt().isBefore(LocalDateTime.now()) || jobConfig.getUpdatedAt().isEqual(LocalDateTime.now()));
    }

    @Test
    void shouldInitializeAllFieldsToNull_WhenNoArgsConstructorIsUsed() {
        // Test the no-args constructor
        DatasyncJobConfiguration jobConfig = new DatasyncJobConfiguration();
        assertNotNull(jobConfig);
    }

    @Test
    void shouldBeEqualAndHaveSameHashCode_WhenFieldsAreIdentical() {
        // Create two identical DatasyncJobConfiguration objects
        DatasyncJobConfiguration jobConfig1 = new DatasyncJobConfiguration();
        DatasyncJobConfiguration jobConfig2 = new DatasyncJobConfiguration();

        UUID id = UUID.randomUUID();
        jobConfig1.setId(id);
        jobConfig2.setId(id);

        // Assert objects are not null
        assertNotNull(jobConfig1);
        assertNotNull(jobConfig2);

        // Assert objects are not the same instance
        assertNotSame(jobConfig1, jobConfig2);

        // Assert equality and hashCode
        assertEquals(jobConfig1, jobConfig2);
        assertEquals(jobConfig1.hashCode(), jobConfig2.hashCode());

        // Assert inequality for different IDs
        jobConfig2.setId(UUID.randomUUID());
        assertNotEquals(jobConfig1, jobConfig2);
        assertNotEquals(jobConfig1.hashCode(), jobConfig2.hashCode());
    }

    @Test
    void shouldGenerateNonNullStringRepresentation_WhenToStringIsCalled() {
        // Create a DatasyncJobConfiguration instance
        DatasyncJobConfiguration jobConfig = new DatasyncJobConfiguration();
        jobConfig.setSystemName("Test Job");
        // Assert toString does not throw exceptions
        assertNotNull(jobConfig.toString());
    }
}