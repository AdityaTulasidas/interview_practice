package com.thomsonreuters.dataconnect.executionengine.entity;

import com.thomsonreuters.dataconnect.executionengine.model.entity.RegionalJobConfiguration;
import com.thomsonreuters.dataconnect.executionengine.model.entity.enums.ExecutionLeg;
import com.thomsonreuters.dataconnect.executionengine.model.entity.enums.JobType;
import com.thomsonreuters.dataconnect.executionengine.transformation.Transformations;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RegionalJobConfigurationTest {

    @Test
    void testGetterAndSetterMethods() {
        UUID id = UUID.randomUUID();
        String jobName = "Test Job";
        JobType type = JobType.PLATFORM;
        ExecutionLeg execLeg = ExecutionLeg.SOURCE;
        String sourceTenantId = "sourceTenantId";
        String targetTenantId = "targetTenantId";
        UUID datasyncJobId = UUID.randomUUID();
        boolean isActive = true;
        String inAdaptorId = "inAdaptorId";
        String outAdaptorId = "outAdaptorId";
        Map<String, String> inAdaptorExecContext = new HashMap<>();
        inAdaptorExecContext.put("key1", "value1");
        Map<String, String> outAdaptorExecContext = new HashMap<>();
        outAdaptorExecContext.put("key2", "value2");
        String clientId = "clientId";
        String customerTenantSysName = "customerTenantSysName";
        String datasyncJobSysName = "datasyncJobSysName";
        String sourceRegion = "AMER";
        String targetRegion = "EMEA";
        String onesourceDomain = "BF_AR";
        UUID metaObjectId = UUID.randomUUID();
        String metaObjectSysName = "metaObjectSysName";
        String createdBy = "createdBy";
        LocalDateTime createdAt = LocalDateTime.now();
        String updatedBy = "updatedBy";
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime lastRunDate = LocalDateTime.now();
        List<Transformations> transformContext = null;

        RegionalJobConfiguration config = new RegionalJobConfiguration();
        config.setId(id);
        config.setType(type);
        config.setExecLeg(execLeg);
        config.setSourceTenantId(sourceTenantId);
        config.setTargetTenantId(targetTenantId);
        config.setDatasyncJobId(datasyncJobId);
        config.setActive(isActive);
        config.setInAdaptorId(inAdaptorId);
        config.setOutAdaptorId(outAdaptorId);
        config.setInAdaptorExecContext(inAdaptorExecContext);
        config.setOutAdaptorExecContext(outAdaptorExecContext);
        config.setClientId(clientId);
        config.setCustomerTenantSysName(customerTenantSysName);
        config.setDatasyncJobSysName(datasyncJobSysName);
        config.setSourceRegion(sourceRegion);
        config.setTargetRegion(targetRegion);
        config.setOnesourceDomain(onesourceDomain);
        config.setMetaObjectSysName(metaObjectSysName);
        config.setCreatedBy(createdBy);
        config.setCreatedAt(createdAt);
        config.setUpdatedBy(updatedBy);
        config.setUpdatedAt(updatedAt);
        config.setLastRunDate(lastRunDate);
        config.setTransformContext(transformContext);

        assertEquals(id, config.getId());
        assertEquals(type, config.getType());
        assertEquals(execLeg, config.getExecLeg());
        assertEquals(sourceTenantId, config.getSourceTenantId());
        assertEquals(targetTenantId, config.getTargetTenantId());
        assertEquals(datasyncJobId, config.getDatasyncJobId());
        assertEquals(isActive, config.isActive());
        assertEquals(inAdaptorId, config.getInAdaptorId());
        assertEquals(outAdaptorId, config.getOutAdaptorId());
        assertEquals(inAdaptorExecContext, config.getInAdaptorExecContext());
        assertEquals(outAdaptorExecContext, config.getOutAdaptorExecContext());
        assertEquals(clientId, config.getClientId());
        assertEquals(customerTenantSysName, config.getCustomerTenantSysName());
        assertEquals(datasyncJobSysName, config.getDatasyncJobSysName());
        assertEquals(sourceRegion, config.getSourceRegion());
        assertEquals(targetRegion, config.getTargetRegion());
        assertEquals(onesourceDomain, config.getOnesourceDomain());
        assertEquals(metaObjectSysName, config.getMetaObjectSysName());
        assertEquals(createdBy, config.getCreatedBy());
        assertEquals(createdAt, config.getCreatedAt());
        assertEquals(updatedBy, config.getUpdatedBy());
        assertEquals(updatedAt, config.getUpdatedAt());
        assertEquals(lastRunDate, config.getLastRunDate());
        assertEquals(transformContext, config.getTransformContext());
    }


    @Test
    void testPreUpdate() {
        RegionalJobConfiguration config = new RegionalJobConfiguration();
        LocalDateTime beforeUpdate = LocalDateTime.now(); // Capture time before update
        config.onUpdate();
        LocalDateTime afterUpdate = LocalDateTime.now(); // Capture time after update

        assertNotNull(config.getUpdatedAt());
        assertTrue((!config.getUpdatedAt().isBefore(beforeUpdate)) && (!config.getUpdatedAt().isAfter(afterUpdate)));
    }

    @Test
    void testCollections() {
        RegionalJobConfiguration config = new RegionalJobConfiguration();

        Map<String, String> inAdaptorExecContext = new HashMap<>();
        inAdaptorExecContext.put("key1", "value1");
        config.setInAdaptorExecContext(inAdaptorExecContext);

        Map<String, String> outAdaptorExecContext = new HashMap<>();
        outAdaptorExecContext.put("key2", "value2");
        config.setOutAdaptorExecContext(outAdaptorExecContext);

        assertEquals(1, config.getInAdaptorExecContext().size());
        assertEquals("value1", config.getInAdaptorExecContext().get("key1"));

        assertEquals(1, config.getOutAdaptorExecContext().size());
        assertEquals("value2", config.getOutAdaptorExecContext().get("key2"));
    }
    @Deprecated
    void testPrePersist() {
        RegionalJobConfiguration config = new RegionalJobConfiguration();
        config.onCreate();

        assertNotNull(config.getCreatedAt());
        assertTrue(config.getCreatedAt().isBefore(LocalDateTime.now()) || config.getCreatedAt().isEqual(LocalDateTime.now()));
    }

}