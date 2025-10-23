package com.aditya.dataconnect.executionengine.dto;

import com.aditya.dataconnect.executionengine.model.entity.enums.ExecType;
import com.aditya.dataconnect.executionengine.model.entity.enums.JobType;
import com.aditya.dataconnect.executionengine.transformation.Transformations;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DatasyncJobConfigurationUpdateRequestDTOTest {

    @Test
    void shouldSetAndGetAllFieldsCorrectly_WhenUpdatingDatasyncJobConfiguration() {
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
        java.util.List<TargetRegionDTO> targets = Collections.singletonList(target);
        java.util.List<ExecType> execType = Collections.singletonList(ExecType.REAL_TIME);
        String systemName = "TestSystem";
        String onesourceDomain = "BF_AR";
        Transformations transformation = new Transformations();
        transformation.setSeq(1);
        transformation.setFuncName("func");
        java.util.List<Transformations> transformations = Collections.singletonList(transformation);
        ActivityDTO activity = new ActivityDTO();
        activity.setSysName("activity1");
        activity.setActivityId(100);
        java.util.List<ActivityDTO> activities = Collections.singletonList(activity);
        Boolean isUgeCustomer = true;

        DatasyncJobConfigurationRequestDTO dto = new DatasyncJobConfigurationRequestDTO();
        dto.setId(id);
        dto.setDescription(description);
        dto.setMetaObjectSysName(metaObjectSysName);
        dto.setJobType(jobType);
        dto.setCustomerTenantSysName(customerTenantSysName);
        dto.setSource(source);
        dto.setTargets(targets);
        dto.setExecType(execType);
        dto.setSystemName(systemName);
        dto.setOnesourceDomain(onesourceDomain);
        dto.setTransformations(transformations);
        dto.setActivities(activities);
        dto.setIsUgeCustomer(isUgeCustomer);

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
}