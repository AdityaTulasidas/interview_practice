package com.aditya.dataconnect.executionengine.dto;

import com.aditya.dataconnect.executionengine.model.entity.enums.ExecType;
import com.aditya.dataconnect.executionengine.model.entity.enums.JobType;
import com.aditya.dataconnect.executionengine.transformation.Transformations;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DatasyncJobConfigurationRequestDTOTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        UUID id = UUID.randomUUID();
        String description = "Test Description";
        String metaObjectSysName = "MetaObjectSysName";
        JobType jobType = JobType.PLATFORM;
        String customerTenantSysName = "CustomerTenantSysName";
        SourceRegionDTO source = new SourceRegionDTO();
        source.setRegion("AMER");
        source.setRegionalTenantId("tenant1");
        TargetRegionDTO target = new TargetRegionDTO();
        target.setRegion("EMEA");
        target.setRegionalTenantId("tenant2");
        List<TargetRegionDTO> targets = Collections.singletonList(target);
        List<ExecType> execType = Collections.singletonList(ExecType.REAL_TIME);
        String systemName = "TestSystem";
        String onesourceDomain = "BF_AR";
        Transformations transformation = new Transformations();
        transformation.setSeq(1);
        transformation.setFuncName("func");
        List<Transformations> transformations = Collections.singletonList(transformation);
        ActivityDTO activity = new ActivityDTO();
        activity.setSysName("activity1");
        activity.setActivityId(100);
        List<ActivityDTO> activities = Collections.singletonList(activity);
        Boolean isUgeCustomer = true;

        DatasyncJobConfigurationRequestDTO dto = new DatasyncJobConfigurationRequestDTO(
                id, description, metaObjectSysName, jobType, customerTenantSysName, source, targets, execType, systemName, onesourceDomain, transformations, activities, isUgeCustomer
        );

        assertEquals(id, dto.getId());
        assertEquals(description, dto.getDescription());
        assertEquals(metaObjectSysName, dto.getMetaObjectSysName());
        assertEquals(jobType, dto.getJobType());
        assertEquals(customerTenantSysName, dto.getCustomerTenantSysName());
        assertEquals(source, dto.getSource());
        assertEquals(targets, dto.getTargets());
        assertEquals(execType, dto.getExecType());
        assertEquals(systemName, dto.getSystemName());
        assertEquals(onesourceDomain, dto.getOnesourceDomain());
        assertEquals(transformations, dto.getTransformations());
        assertEquals(activities, dto.getActivities());
        assertEquals(isUgeCustomer, dto.getIsUgeCustomer());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        DatasyncJobConfigurationRequestDTO dto = new DatasyncJobConfigurationRequestDTO();

        UUID id = UUID.randomUUID();
        dto.setId(id);
        dto.setDescription("Test Description");
        dto.setMetaObjectSysName("MetaObjectSysName");
        dto.setJobType(JobType.PLATFORM);
        dto.setCustomerTenantSysName("CustomerTenantSysName");
        SourceRegionDTO source = new SourceRegionDTO();
        source.setRegion("AMER");
        source.setRegionalTenantId("tenant1");
        dto.setSource(source);
        TargetRegionDTO target = new TargetRegionDTO();
        target.setRegion("EMEA");
        target.setRegionalTenantId("tenant2");
        dto.setTargets(Collections.singletonList(target));
        dto.setExecType(Collections.singletonList(ExecType.REAL_TIME));
        dto.setSystemName("TestSystem");
        dto.setOnesourceDomain("BF_AR");
        Transformations transformation = new Transformations();
        transformation.setSeq(1);
        transformation.setFuncName("func");
        dto.setTransformations(Collections.singletonList(transformation));
        ActivityDTO activity = new ActivityDTO();
        activity.setSysName("activity1");
        activity.setActivityId(100);
        dto.setActivities(Collections.singletonList(activity));
        dto.setIsUgeCustomer(true);

        assertEquals(id, dto.getId());
        assertEquals("Test Description", dto.getDescription());
        assertEquals("MetaObjectSysName", dto.getMetaObjectSysName());
        assertEquals(JobType.PLATFORM, dto.getJobType());
        assertEquals("CustomerTenantSysName", dto.getCustomerTenantSysName());
        assertEquals(source, dto.getSource());
        assertEquals(Collections.singletonList(target), dto.getTargets());
        assertEquals(Collections.singletonList(ExecType.REAL_TIME), dto.getExecType());
        assertEquals("TestSystem", dto.getSystemName());
        assertEquals("BF_AR", dto.getOnesourceDomain());
        assertEquals(Collections.singletonList(transformation), dto.getTransformations());
        assertEquals(Collections.singletonList(activity), dto.getActivities());
        assertEquals(true, dto.getIsUgeCustomer());
    }
}