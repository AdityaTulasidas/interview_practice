package com.thomsonreuters.dataconnect.dataintegration.dto;

import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.ExecType;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.JobType;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DatasyncJobConfigurationUpdateRequestDTOTest {

    @Test
    void shouldSetAllFieldsCorrectly_WhenDTOIsInitialized() {
        UUID id = UUID.randomUUID();
        String sysName = "Test Job";
        String description = "Test Description";
        JobType jobType = JobType.PLATFORM;
        String sourceRegionId = "sourceRegionId";
        String customerTenantId = "customerTenantId";
        String onesourceDomain = "BF_AR";
        ExecType execType = ExecType.REAL_TIME;

        DatasyncJobConfigurationUpdateRequestDTO dto = new DatasyncJobConfigurationUpdateRequestDTO();
        dto.setId(id);
        dto.setSystemName(sysName);
        dto.setDescription(description);
        dto.setJobType(jobType);
        SourceRegionalTenantDTO source = new SourceRegionalTenantDTO();
        source.setRegionalTenantId(sourceRegionId);
        dto.setSource(source);
        dto.setCustomerTenantSysName(customerTenantId);
        dto.setOnesourceDomain(onesourceDomain);
        dto.setExecType(Collections.singletonList(execType));

        assertEquals(id, dto.getId());
        assertEquals(sysName, dto.getSystemName());
        assertEquals(description, dto.getDescription());
        assertEquals(jobType, dto.getJobType());
        assertEquals(sourceRegionId, dto.getSource().getRegionalTenantId());
        assertEquals(customerTenantId, dto.getCustomerTenantSysName());
        assertEquals(onesourceDomain, dto.getOnesourceDomain());
        assertEquals(Collections.singletonList(execType), dto.getExecType());
    }
}