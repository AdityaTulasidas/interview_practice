package com.thomsonreuters.dataconnect.dataintegration.dto;

import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.ExecType;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.JobType;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DatasyncJobConfigurationRequestDTOTest {

   @Test
   void shouldSetAndGetAllFieldsCorrectly_WhenDTOIsInitialized() {
       UUID id = UUID.randomUUID();
       String sysName = "Test Job";
       String description = "Test Description";
       String metaObjectSysName = "Test MetaObject";
       UUID metaObjectId = UUID.randomUUID();
       JobType jobType = JobType.PLATFORM;
       String sourceRegion = "AMER";
       String targetRegion = "EMEA";
       String customerId = "customerId";
       String customerTenantSysName = "customerTenantSysName";
       String clientId = "clientId";
       String onesourceDomain = "BF_AR";
       String sourceTenantId = "sourceTenantId";
       ExecType execType = ExecType.REAL_TIME;

       DatasyncJobConfigurationRequestDTO dto = new DatasyncJobConfigurationRequestDTO();
       dto.setId(id);
       dto.setSystemName(sysName);
       dto.setDescription(description);
       dto.setMetaObjectSysName(metaObjectSysName);
       dto.setJobType(jobType);
       SourceRegionDTO sourceRegionDTO = new SourceRegionDTO();
       sourceRegionDTO.setRegion(sourceRegion);
       sourceRegionDTO.setRegionalTenantId(sourceTenantId);
       dto.setSource(sourceRegionDTO);
       dto.setCustomerTenantSysName(customerTenantSysName);
       dto.setOnesourceDomain(onesourceDomain);
       dto.setExecType(Collections.singletonList(execType));

       assertEquals(id, dto.getId());
       assertEquals(sysName, dto.getSystemName());
       assertEquals(description, dto.getDescription());
       assertEquals(metaObjectSysName, dto.getMetaObjectSysName());
       assertEquals(jobType, dto.getJobType());
       assertEquals(sourceRegion, dto.getSource().getRegion());
       assertEquals(customerTenantSysName, dto.getCustomerTenantSysName());
       assertEquals(onesourceDomain, dto.getOnesourceDomain());
       assertEquals(sourceTenantId, dto.getSource().getRegionalTenantId());
       assertEquals(Collections.singletonList(execType), dto.getExecType());
   }

   @Test
   void shouldInitializeFieldsCorrectly_WhenUsingConstructor() {
       DatasyncJobConfigurationRequestDTO dto = new DatasyncJobConfigurationRequestDTO();
       dto.setId(UUID.randomUUID());
       dto.setSystemName("systemName");
       dto.setDescription("description");
       dto.setMetaObjectSysName("metaObjectSysName");
       dto.setJobType(JobType.PLATFORM);
       SourceRegionDTO source = new SourceRegionDTO();
       source.setRegion("AMER");
       source.setRegionalTenantId("regionalTenantId");
       dto.setSource(source);
       dto.setCustomerTenantSysName("customerTenantId");
       dto.setOnesourceDomain("BF_AR");
       dto.setExecType(Collections.singletonList(ExecType.REAL_TIME));
       // targets, transformations, activities can be set as needed

       assertEquals("regionalTenantId", dto.getSource().getRegionalTenantId());
       assertEquals(JobType.PLATFORM, dto.getJobType());
   }
}
