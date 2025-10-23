package com.thomsonreuters.dataconnect.executionengine.entity;

import com.thomsonreuters.dataconnect.executionengine.model.entity.DatasyncJobConfiguration;
import com.thomsonreuters.dataconnect.executionengine.model.entity.enums.ExecType;
import com.thomsonreuters.dataconnect.executionengine.model.entity.enums.JobType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DatasyncJobConfigurationTest {

    @Test
    void testGetterAndSetterMethods() {
        DatasyncJobConfiguration jobConfig = new DatasyncJobConfiguration();
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        jobConfig.setId(id);
        jobConfig.setDescription("Test Description");
        jobConfig.setMetaObjectSysName("MetaObjectSysName");
        jobConfig.setCustomerTenantSysName("CustomerTenantSysName");
        jobConfig.setJobType(JobType.PLATFORM);
        jobConfig.setSourceRegion("AMER");
        jobConfig.setClientId("clientId");
        jobConfig.setOnesourceDomain("BF_AR");
        jobConfig.setExecType(ExecType.BATCH);
        jobConfig.setSystemName("TestSystem");
        jobConfig.setSourceTenantId("sourceTenantId");
        jobConfig.setCreatedAt(now);
        jobConfig.setUpdatedAt(now);

        assertEquals(id, jobConfig.getId());
        assertEquals("Test Description", jobConfig.getDescription());
        assertEquals("MetaObjectSysName", jobConfig.getMetaObjectSysName());
        assertEquals("CustomerTenantSysName", jobConfig.getCustomerTenantSysName());
        assertEquals(JobType.PLATFORM, jobConfig.getJobType());
        assertEquals("AMER", jobConfig.getSourceRegion());
        assertEquals("clientId", jobConfig.getClientId());
        assertEquals("BF_AR", jobConfig.getOnesourceDomain());
        assertEquals(ExecType.BATCH, jobConfig.getExecType());
        assertEquals("TestSystem", jobConfig.getSystemName());
        assertEquals("sourceTenantId", jobConfig.getSourceTenantId());
        assertEquals(now, jobConfig.getCreatedAt());
        assertEquals(now, jobConfig.getUpdatedAt());
    }

    @Test
    void testPreUpdate() {
        DatasyncJobConfiguration jobConfig = new DatasyncJobConfiguration();
        jobConfig.preUpdate();
        assertNotNull(jobConfig.getUpdatedAt());
    }

    @Test
    void testNullValues() {
        DatasyncJobConfiguration jobConfig = new DatasyncJobConfiguration();

        assertDoesNotThrow(() -> {
            jobConfig.setDescription(null);
            jobConfig.setMetaObjectSysName(null);
            jobConfig.setCustomerTenantSysName(null);
            jobConfig.setJobType(null);
            jobConfig.setSourceRegion(null);
            jobConfig.setClientId(null);
            jobConfig.setOnesourceDomain(null);
            jobConfig.setExecType(null);
            jobConfig.setSystemName(null);
            jobConfig.setSourceTenantId(null);
        });
    }
}