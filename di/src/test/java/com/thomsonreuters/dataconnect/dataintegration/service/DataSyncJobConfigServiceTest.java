//package com.thomsonreuters.dataconnect.dataintegration.service;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.thomsonreuters.dataconnect.dataintegration.configuration.ModelMapperConfig;
//import com.thomsonreuters.dataconnect.dataintegration.dto.*;
//import com.thomsonreuters.dataconnect.dataintegration.exceptionhandler.DataSyncJobException;
//import com.thomsonreuters.dataconnect.dataintegration.model.entity.*;
//import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.*;
//import com.thomsonreuters.dataconnect.dataintegration.repository.*;
//import com.thomsonreuters.dataconnect.dataintegration.services.DataSyncJobConfigService;
//import com.thomsonreuters.dataconnect.dataintegration.services.DataSyncService;
//import com.thomsonreuters.dep.api.spring.ApiSupport;
//import com.thomsonreuters.dep.api.spring.response.ApiCollectionFactory;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
///**
// * Clean re-write of the test class after previous merge/corruption.
// * Adds a focused test validating updateRegionalJobs() business rule:
// *  - region_tenant_id updated ONLY for SOURCE row
// *  - TARGET rows keep regionTenantId null, but propagate customerTenantId, clientId and update targetTenantId
// */
//class DataSyncJobConfigServiceTest {
//
//    @Mock
//    private RestTemplate restTemplate;
//
//    @Mock
//    private ApiSupport apiSupport;
//
//    @Mock
//    private ApiCollectionFactory apiCollection;
//
//    @Mock
//    private DataSyncService dataSyncService;
//
//    @Mock
//    private DatasyncJobConfigRepository datasyncJobConfigRepository;
//
//    @Mock
//    private MetaObjectRepository metaObjectRepository;
//
//    @Mock
//    private RegionalJobConfigRepository regionalJobConfigRepository;
//
//    @Mock
//    private ModelMapperConfig modelMapperConfig;
//
//    @Mock
//    private DatasyncActivityRepository datasyncActivityRepository;
//
//    @Mock
//    private TransformationRepository transformationRepository;
//
//    @Mock
//    private TransformParamRepository transformParamRepository;
//
//    @Mock
//    private DataSyncTransformationRepository dataSyncTransformationRepository;
//
//    @Mock
//    private TargetRegionRepository targetRegionRepository;
//
//    @Mock
//    private OnesourceRegionRepository onesourceRegionRepository;
//
//    @Mock
//    private MetaObjectRelationRepository metaObjectRelationRepository;
//
//    @InjectMocks
//    private DataSyncJobConfigService dataSyncJobConfigService;
//
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        when(modelMapperConfig.modelMapper()).thenReturn(new org.modelmapper.ModelMapper());
//        when(targetRegionRepository.findByDatasyncJobSysName(anyString())).thenReturn(Collections.emptyList());
//        when(targetRegionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
//        when(metaObjectRelationRepository.existsByChildObject_Id(any())).thenReturn(false);
//    }
//
//    /* ---------- Basic validation / existing tests re-created ---------- */
//
//    @Test
//    void shouldThrowException_WhenMetaObjectIsNotFound() {
//        DatasyncJobConfigurationRequestDTO request = new DatasyncJobConfigurationRequestDTO();
//        request.setMetaObjectSysName("TestMetaObject");
//
//        when(metaObjectRepository.existsBySystemName(request.getMetaObjectSysName())).thenReturn(false);
//
//        DataSyncJobException exception = assertThrows(DataSyncJobException.class, () ->
//                dataSyncJobConfigService.createDataSyncJob(request));
//
//        assertEquals("Invalid meta object system name", exception.getMessage());
//    }
//
//    @Test
//    void shouldThrowException_WhenJobToUpdateIsNotFound() {
//        DatasyncJobConfigurationUpdateRequestDTO updateRequest = new DatasyncJobConfigurationUpdateRequestDTO();
//        updateRequest.setId(UUID.randomUUID());
//
//        when(datasyncJobConfigRepository.findById(updateRequest.getId())).thenReturn(Optional.empty());
//
//        DataSyncJobException exception = assertThrows(DataSyncJobException.class, () ->
//                dataSyncJobConfigService.updateDataSyncJob(updateRequest));
//
//        assertEquals("DataSync Job not found", exception.getMessage());
//    }
//
//    @Test
//    void shouldDeactivateRegionalJobSuccessfully_WhenJobExists() {
//        UUID jobId = UUID.randomUUID();
//        String region = "AMER";
//        RegionalJobConfiguration regionalJob = new RegionalJobConfiguration();
//        regionalJob.setActive(true);
//
//        when(regionalJobConfigRepository.findByDatasyncJobIdAndExecLegAndTargetRegion(jobId, ExecutionLeg.TARGET ,region))
//                .thenReturn(Optional.of(regionalJob));
//
//        dataSyncJobConfigService.deactivateRegionalJob(jobId, region);
//
//        assertFalse(regionalJob.isActive());
//        verify(regionalJobConfigRepository).save(regionalJob);
//    }
//
//    @Test
//    void shouldNotDeactivateRegionalJob_WhenJobDoesNotExist() {
//        UUID jobId = UUID.randomUUID();
//        when(regionalJobConfigRepository.findByDatasyncJobIdAndExecLegAndTargetRegion(jobId, ExecutionLeg.TARGET ,"AMER"))
//                .thenReturn(Optional.empty());
//
//        dataSyncJobConfigService.deactivateRegionalJob(jobId, "AMER");
//        verify(regionalJobConfigRepository, never()).save(any());
//    }
//
//
//
//    @Test
//    void shouldValidateCuratedDataSuccessfully_WhenCustomerJobTypeIsProvided() {
//        DatasyncJobConfigurationRequestDTO request = new DatasyncJobConfigurationRequestDTO();
//        request.setJobType(JobType.CUSTOMER);
//        request.setCustomerTenantSysName("ct");
//        SourceRegionDTO sourceRegionDTO = new SourceRegionDTO();
//        sourceRegionDTO.setRegion("AMER");
//        sourceRegionDTO.setRegionalTenantId("RT1");
//        request.setSource(sourceRegionDTO);
//        TargetRegionDTO targetRegionDTO = new TargetRegionDTO();
//        targetRegionDTO.setRegion("EMEA");
//        targetRegionDTO.setRegionalTenantId("RT2");
//        request.setTargets(List.of(targetRegionDTO));
//
//        assertDoesNotThrow(() -> dataSyncJobConfigService.validateCuratedData(request));
//    }
//
//    @Test
//    void shouldThrowException_WhenCustomerJobTypeMissingFields() {
//        DatasyncJobConfigurationRequestDTO request = new DatasyncJobConfigurationRequestDTO();
//        request.setJobType(JobType.CUSTOMER);
//        DataSyncJobException ex = assertThrows(DataSyncJobException.class,
//                () -> dataSyncJobConfigService.validateCuratedData(request));
//        assertEquals("Customer Tenant System Name is mandatory for CUSTOMER job type", ex.getMessage());
//    }
//
//    @Test
//    void shouldValidateCuratedDataSuccessfully_WhenPlatformJobTypeProvided() {
//        DatasyncJobConfigurationRequestDTO request = new DatasyncJobConfigurationRequestDTO();
//        request.setJobType(JobType.PLATFORM);
//        request.setCustomerTenantSysName(null);
//        assertDoesNotThrow(() -> dataSyncJobConfigService.validateCuratedData(request));
//    }
//
//    @Test
//    void shouldThrowException_InvalidJobTypeInCuratedValidation() {
//        DatasyncJobConfigurationRequestDTO request = new DatasyncJobConfigurationRequestDTO();
//        request.setJobType(null);
//        DataSyncJobException ex = assertThrows(DataSyncJobException.class,
//                () -> dataSyncJobConfigService.validateCuratedData(request));
//        assertEquals("Invalid job type", ex.getMessage());
//    }
//
//    @Test
//    void shouldThrowException_WhenTransformationContextInvalid() {
//        DatasyncJobConfigurationRequestDTO request = new DatasyncJobConfigurationRequestDTO();
//        Transformations invalid = new Transformations();
//        invalid.setSeq(0); // invalid
//        invalid.setType(TransformType.BUILT_IN);
//        invalid.setFuncName("Missing");
//        java.util.List<Transformations> invalidList = new java.util.ArrayList<>();
//        invalidList.add(invalid);
//        request.setTransformations(invalidList);
//
//        when(transformationRepository.findBySystemName("Missing")).thenReturn(Optional.empty());
//
//        DataSyncJobException ex = assertThrows(DataSyncJobException.class,
//                () -> dataSyncJobConfigService.validateTransformationContext(invalidList, request));
//        assertEquals("Invalid transformation context", ex.getMessage());
//    }
//
//    @Test
//    void shouldValidateTransformationContextSuccessfully() throws DataSyncJobException {
//        DatasyncJobConfigurationRequestDTO request = new DatasyncJobConfigurationRequestDTO();
//        Transformations valid = new Transformations();
//        valid.setSeq(1);
//        valid.setType(TransformType.BUILT_IN);
//        valid.setFuncName("FuncA");
//        valid.setParams(List.of(new TransformParams("field_name", "tenant_id")));
//
//        java.util.List<Transformations> validList = new java.util.ArrayList<>();
//        validList.add(valid);
//
//        request.setTransformations(validList);
//
//        TransformationFunction function = new TransformationFunction();
//        when(transformationRepository.findBySystemName("FuncA")).thenReturn(Optional.of(function));
//
//        TransformationFunctionParam param = new TransformationFunctionParam();
//        param.setId(1);
//        param.setSystemName("field_name");
//        param.setDisplayName("Field Name");
//        param.setDescription("desc");
//        param.setTransformFuncId("FuncA");
//        when(transformParamRepository.findByTransformFuncId("FuncA")).thenReturn(List.of(param));
//
//        assertDoesNotThrow(() ->
//                dataSyncJobConfigService.validateTransformationContext(validList, request));
//    }
//
//    /* ---------- New test for updateRegionalJobs SOURCE-only region_tenant update rule ---------- */
//    @Test
//    void shouldUpdateSourceRegionTenantAndPropagateCustomerAndClientButKeepTargetRegionTenantNull() throws DataSyncJobException {
//        UUID jobId = UUID.randomUUID();
//
//        // SOURCE existing
//        RegionalJobConfiguration source = new RegionalJobConfiguration();
//        source.setExecLeg(ExecutionLeg.SOURCE);
//        source.setSourceTenantId("OLD_REGION_TENANT");
//        source.setCustomerTenantSysName("OLD_CUST_TENANT");
//        source.setActive(true);
//        source.setTargetRegion("EMEA"); // legacy CSV
//
//        // TARGET existing (EMEA)
//        RegionalJobConfiguration target = new RegionalJobConfiguration();
//        target.setExecLeg(ExecutionLeg.TARGET);
//        target.setSourceTenantId(null); // must remain null
//        target.setTargetTenantId("OLD_TARGET_TENANT");
//        target.setCustomerTenantSysName("OLD_CUST_TENANT");
//        target.setActive(true);
//
//        when(regionalJobConfigRepository.findByDatasyncJobIdAndExecLeg(jobId, ExecutionLeg.SOURCE)).thenReturn(Optional.of(source));
//        when(regionalJobConfigRepository.findByDatasyncJobIdAndExecLegAndTargetRegion(jobId, ExecutionLeg.TARGET, "EMEA"))
//                .thenReturn(Optional.of(target));
//
//        // Build update request
//        DatasyncJobConfigurationRequestDTO request = new DatasyncJobConfigurationRequestDTO();
//        SourceRegionDTO sourceRegionDTO = new SourceRegionDTO();
//        sourceRegionDTO.setRegion("AMER");
//        sourceRegionDTO.setRegionalTenantId("NEW_REGION_TENANT");
//        request.setSource(sourceRegionDTO);
//
//        request.setExecType(List.of(ExecType.REAL_TIME));
//
//        request.setCustomerTenantSysName("NEW_CUST_TENANT");
//        request.setJobType(JobType.CUSTOMER);
//
//        TargetRegionDTO newTarget = new TargetRegionDTO();
//        newTarget.setRegion("EMEA");
//        newTarget.setRegionalTenantId("NEW_TARGET_TENANT");
//        request.setTargets(List.of(newTarget));
//
//        TargetRegionDTO existingTarget = new TargetRegionDTO();
//        existingTarget.setRegion("EMEA");
//        existingTarget.setRegionalTenantId("OLD_TARGET_TENANT");
//
//        dataSyncJobConfigService.updateRegionalJobs(
//                jobId,
//                request,
//                List.of(existingTarget),
//                request.getTargets()
//        );
//
//        // SOURCE assertions
//        assertEquals("NEW_REGION_TENANT", source.getSourceTenantId());
//        assertEquals("NEW_CUST_TENANT", source.getCustomerTenantSysName());
//
//        // TARGET assertions
//        assertNull(source.getTargetTenantId(), "Target regionTenantId must remain null");
//        assertEquals("NEW_CUST_TENANT", target.getCustomerTenantSysName());
//        assertEquals("NEW_TARGET_TENANT", target.getTargetTenantId());
//
//        verify(regionalJobConfigRepository, atLeastOnce()).save(source);
//        verify(regionalJobConfigRepository, atLeastOnce()).save(target);
//    }
//
//    /* ---------- createDataSyncJob basic success path ---------- */
//    @Test
//    void shouldCreateDataSyncJobSuccessfully_WhenRequestIsValid() throws DataSyncJobException, JsonProcessingException {
//        DatasyncJobConfigurationRequestDTO request = new DatasyncJobConfigurationRequestDTO();
//        request.setMetaObjectSysName("meta_obj_sys_name");
//        request.setJobType(JobType.CUSTOMER);
//        request.setCustomerTenantSysName("tenant1");
//        request.setExecType(Collections.singletonList(ExecType.REAL_TIME));
//        request.setOnesourceDomain("BF_AR");
//
//        SourceRegionDTO source = new SourceRegionDTO();
//        source.setRegion("AMER");
//        source.setRegionalTenantId("regionalTenant1");
//        request.setSource(source);
//
//        TargetRegionDTO target = new TargetRegionDTO();
//        target.setRegion("EMEA");
//        target.setRegionalTenantId("EA0");
//        request.setTargets(List.of(target));
//
//        ActivityDTO activity = new ActivityDTO();
//        activity.setSysName("act.sys");
//        activity.setActivityId(1);
//        activity.setExecType("TARGET");
//        activity.setExecSeq(1);
//        activity.setEventType("POST-COMPLETION");
//        activity.setActivityType("built-in");
//        request.setActivities(List.of(activity));
//
//        Transformations transformations = new Transformations();
//        transformations.setSeq(1);
//        transformations.setType(TransformType.BUILT_IN);
//        transformations.setFuncName("FuncA");
//        transformations.setParams(List.of(new TransformParams("field_name", "tenant_id")));
//        request.setTransformations(new ArrayList<>(List.of(transformations)));
//
//        TransformationFunction function = new TransformationFunction();
//        when(transformationRepository.findBySystemName("FuncA")).thenReturn(Optional.of(function));
//        TransformationFunctionParam param = new TransformationFunctionParam();
//        param.setSystemName("field_name");
//        param.setTransformFuncId("FuncA");
//        when(transformParamRepository.findByTransformFuncId("FuncA")).thenReturn(List.of(param));
//
//        request.setSystemName("job.system.name");
//
//        // Mock metaObjectRepository for meta_obj
//        MetaObject metaObject = new MetaObject();
//        metaObject.setSystemName("meta_obj_sys_name");
//        metaObject.setOneSourceDomain("BF_AR");
//        when(metaObjectRepository.findBySystemName("meta_obj_sys_name")).thenReturn(Optional.of(metaObject));
//
//        when(metaObjectRepository.existsBySystemName(request.getMetaObjectSysName())).thenReturn(true);
//        when(metaObjectRepository.findBySystemName(request.getMetaObjectSysName())).thenReturn(Optional.of(metaObject));
//
//        when(datasyncJobConfigRepository.existsBySystemName(anyString())).thenReturn(false);
//        OnesourceRegion regionRepo = new OnesourceRegion();
//        regionRepo.setSystemName("AMER");
//        when(onesourceRegionRepository.findBySystemName(anyString())).thenReturn(Optional.of(regionRepo));
//
//        UUID jobId = UUID.randomUUID();
//        when(datasyncJobConfigRepository.save(any(DatasyncJobConfiguration.class))).thenAnswer(invocation -> {
//            DatasyncJobConfiguration saved = invocation.getArgument(0);
//            saved.setId(jobId);
//            return saved;
//        });
//
//        String result = dataSyncJobConfigService.createDataSyncJob(request);
//        assertEquals(jobId.toString(), result);
//        verify(regionalJobConfigRepository, times(2)).save(any(RegionalJobConfiguration.class)); // SOURCE + 1 TARGET
//    }
//
//    /* ---------- New tests: propagation when targets omitted / empty on update ---------- */
//
//    @Test
//    void shouldPropagateCustomerTenantToTargets_WhenTargetsOmittedInUpdate() throws DataSyncJobException, JsonProcessingException {
//        // Arrange
//        UUID jobId = UUID.randomUUID();
//        // Existing main job
//        DatasyncJobConfiguration existingJob = new DatasyncJobConfiguration();
//        existingJob.setId(jobId);
//        existingJob.setMetaObjectSysName("meta_obj");
//        existingJob.setJobType(JobType.CUSTOMER);
//        existingJob.setSystemName("old.job");
//        existingJob.setCustomerTenantSysName("OLD_CT");
//
//        // Existing SOURCE row
//        RegionalJobConfiguration source = new RegionalJobConfiguration();
//        source.setExecLeg(ExecutionLeg.SOURCE);
//        source.setActive(true);
//        source.setCustomerTenantSysName("OLD_CT");
//        source.setSourceTenantId("OLD_REGION_TENANT");
//
//        // Existing TARGET row
//        RegionalJobConfiguration target = new RegionalJobConfiguration();
//        target.setExecLeg(ExecutionLeg.TARGET);
//        target.setActive(true);
//        target.setCustomerTenantSysName("OLD_CT");
//        target.setTargetTenantId("OLD_TARGET_TENANT");
//
//        // Mock metaObjectRepository for meta_obj
//        MetaObject metaObject = new MetaObject();
//        metaObject.setSystemName("meta_obj");
//        metaObject.setOneSourceDomain("BF_AR");
//        when(metaObjectRepository.findBySystemName("meta_obj")).thenReturn(Optional.of(metaObject));
//
//        when(metaObjectRepository.existsBySystemName(existingJob.getMetaObjectSysName())).thenReturn(true);
//
//        when(datasyncJobConfigRepository.findById(jobId)).thenReturn(Optional.of(existingJob));
//        when(datasyncJobConfigRepository.save(any(DatasyncJobConfiguration.class))).thenAnswer(inv -> inv.getArgument(0));
//        when(regionalJobConfigRepository.findByDatasyncJobIdAndIsActive(jobId, true)).thenReturn(Set.of(source, target));
//        when(regionalJobConfigRepository.save(any(RegionalJobConfiguration.class))).thenAnswer(inv -> inv.getArgument(0));
//        when(regionalJobConfigRepository.findByDatasyncJobIdAndExecLeg(jobId, ExecutionLeg.SOURCE)).thenReturn(Optional.of(source));
//
//        // Act
//        DatasyncJobConfigurationUpdateRequestDTO updateRequest = new DatasyncJobConfigurationUpdateRequestDTO();
//        updateRequest.setId(jobId);
//        updateRequest.setCustomerTenantSysName("NEW_CT");
//        updateRequest.setTargets(null); // omitted
//        SourceRegionalTenantDTO sourceRegionDTO = new SourceRegionalTenantDTO();
//        sourceRegionDTO.setRegionalTenantId("NEW_REGION_TENANT");
//        updateRequest.setSource(sourceRegionDTO);
//        updateRequest.setExecType(List.of(ExecType.REAL_TIME));
//
//        dataSyncJobConfigService.updateDataSyncJob(updateRequest);
//
//        // Assert
//        assertEquals("NEW_CT", source.getCustomerTenantSysName(), "SOURCE customerTenantSysName should be updated");
//        assertEquals("NEW_REGION_TENANT", source.getSourceTenantId(), "SOURCE sourceTenantId should be updated");
//        assertEquals("NEW_CT", target.getCustomerTenantSysName(), "TARGET customerTenantSysName should be updated");
//        // TargetTenantId should remain unchanged since targets are omitted
//        assertEquals("OLD_TARGET_TENANT", target.getTargetTenantId(), "TARGET targetTenantId should remain unchanged");
//        verify(regionalJobConfigRepository, atLeastOnce()).save(source);
//        verify(regionalJobConfigRepository, atLeastOnce()).save(target);
//    }
//
//    @Test
//    void shouldPropagateCustomerTenantToTargets_WhenTargetsEmptyInUpdate() throws DataSyncJobException, JsonProcessingException {
//        UUID jobId = UUID.randomUUID();
//        // Existing main job
//        DatasyncJobConfiguration existingJob = new DatasyncJobConfiguration();
//        existingJob.setId(jobId);
//        existingJob.setMetaObjectSysName("meta_obj2");
//        existingJob.setJobType(JobType.CUSTOMER);
//        existingJob.setSystemName("old.job2");
//        existingJob.setCustomerTenantSysName("OLD_CT2");
//
//        // Existing SOURCE row
//        RegionalJobConfiguration source = new RegionalJobConfiguration();
//        source.setExecLeg(ExecutionLeg.SOURCE);
//        source.setActive(true);
//        source.setCustomerTenantSysName("OLD_CT2");
//        source.setSourceTenantId("OLD_REGION_TENANT2");
//
//        // Existing TARGET row
//        RegionalJobConfiguration target = new RegionalJobConfiguration();
//        target.setExecLeg(ExecutionLeg.TARGET);
//        target.setActive(true);
//        target.setCustomerTenantSysName("OLD_CT2");
//        target.setTargetTenantId("OLD_TARGET_TENANT2");
//
//        // Mock metaObjectRepository for meta_obj2
//        MetaObject metaObject = new MetaObject();
//        metaObject.setSystemName("meta_obj2");
//        metaObject.setOneSourceDomain("BF_AR");
//        when(metaObjectRepository.findBySystemName("meta_obj2")).thenReturn(Optional.of(metaObject));
//
//        when(metaObjectRepository.existsBySystemName(existingJob.getMetaObjectSysName())).thenReturn(true);
//
//        when(datasyncJobConfigRepository.findById(jobId)).thenReturn(Optional.of(existingJob));
//        when(datasyncJobConfigRepository.save(any(DatasyncJobConfiguration.class))).thenAnswer(inv -> inv.getArgument(0));
//        when(regionalJobConfigRepository.findByDatasyncJobIdAndIsActive(jobId, true)).thenReturn(Set.of(source, target));
//        when(regionalJobConfigRepository.save(any(RegionalJobConfiguration.class))).thenAnswer(inv -> inv.getArgument(0));
//        when(regionalJobConfigRepository.findByDatasyncJobIdAndExecLeg(jobId, ExecutionLeg.SOURCE)).thenReturn(Optional.of(source));
//
//        // Update request with empty targets list
//        DatasyncJobConfigurationUpdateRequestDTO updateRequest = new DatasyncJobConfigurationUpdateRequestDTO();
//        updateRequest.setId(jobId);
//        updateRequest.setCustomerTenantSysName("NEW_CT2");
//        updateRequest.setTargets(Collections.emptyList()); // empty
//        SourceRegionalTenantDTO sourceRegionDTO = new SourceRegionalTenantDTO();
//        sourceRegionDTO.setRegionalTenantId("NEW_REGION_TENANT2");
//        updateRequest.setSource(sourceRegionDTO);
//        updateRequest.setExecType(List.of(ExecType.REAL_TIME));
//
//        dataSyncJobConfigService.updateDataSyncJob(updateRequest);
//
//        // SOURCE updated
//        assertEquals("NEW_CT2", source.getCustomerTenantSysName(), "SOURCE customerTenantSysName should be updated");
//        assertEquals("NEW_REGION_TENANT2", source.getSourceTenantId(), "SOURCE sourceTenantId should be updated");
//        assertEquals("NEW_CT2", target.getCustomerTenantSysName(), "TARGET customerTenantSysName should be updated");
//        // TargetTenantId should remain unchanged since targets are empty
//        assertEquals("OLD_TARGET_TENANT2", target.getTargetTenantId(), "TARGET targetTenantId should remain unchanged");
//        verify(regionalJobConfigRepository, atLeastOnce()).save(source);
//        verify(regionalJobConfigRepository, atLeastOnce()).save(target);
//    }
//
//
//}
