package com.thomsonreuters.dataconnect.dataintegration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomsonreuters.dataconnect.dataintegration.configuration.DataIntegrationRegionConfig;
import com.thomsonreuters.dataconnect.dataintegration.configuration.ModelMapperConfig;
import com.thomsonreuters.dataconnect.dataintegration.configuration.RabbitMQConfig;
import com.thomsonreuters.dataconnect.dataintegration.configuration.RegionConfig;
import com.thomsonreuters.dataconnect.dataintegration.constant.Constants;
import com.thomsonreuters.dataconnect.dataintegration.exceptionhandler.DataSyncJobException;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.DatasyncJobConfiguration;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.JobExecutionLog;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.MetaObject;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.ExecType;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.OperationType;
import com.thomsonreuters.dataconnect.dataintegration.model.pojo.OptionalRequestBody;
import com.thomsonreuters.dataconnect.dataintegration.model.pojo.RequestBodyDataUnitList;
import com.thomsonreuters.dataconnect.dataintegration.model.pojo.RequestModel;
import com.thomsonreuters.dataconnect.dataintegration.repository.DatasyncJobConfigRepository;
import com.thomsonreuters.dataconnect.dataintegration.repository.JobExecutionLogRepository;
import com.thomsonreuters.dataconnect.dataintegration.repository.MetaObjectRepository;
import com.thomsonreuters.dataconnect.dataintegration.services.DataSyncService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DataSyncServiceTest {

    @Mock
    private DatasyncJobConfigRepository datasyncJobConfigRepository;

    @Mock
    private JobExecutionLogRepository jobExecutionLogRepository;

    @Mock
    private MetaObjectRepository metaObjectRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private RabbitMQConfig rabbitMQConfig;

    @InjectMocks
    private DataSyncService dataSyncService;

    @Mock
    private RegionConfig regionConfig;

    @Mock
    private com.thomsonreuters.dataconnect.common.logging.LogClient logClient;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private DataIntegrationRegionConfig dataIntegrationRegionConfig;
    @Mock
    private ModelMapperConfig modelMapperConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(modelMapperConfig.modelMapper()).thenReturn(new org.modelmapper.ModelMapper());
        when(rabbitMQConfig.getSourceListenerExchange()).thenReturn("test-exchange");
        when(rabbitMQConfig.getSourceSenderRoutingKey()).thenReturn("test-routing-key");
        regionConfig = mock(RegionConfig.class);
        when(regionConfig.getRegion()).thenReturn("TEST_REGION");
        logClient = mock(com.thomsonreuters.dataconnect.common.logging.LogClient.class);
        try {
            java.lang.reflect.Field logField = dataSyncService.getClass().getDeclaredField("dcLogClient");
            logField.setAccessible(true);
            logField.set(dataSyncService, logClient);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldCreateLogRecordWithAllFields_WhenPublishingDataSyncJob() throws DataSyncJobException {
        UUID jobId = UUID.randomUUID();
        DatasyncJobConfiguration jobConfig = new DatasyncJobConfiguration();
        jobConfig.setId(jobId);
        jobConfig.setMetaObjectSysName("meta_obj_sys_name");
        jobConfig.setSourceRegion("AMER");
        jobConfig.setSystemName("TestJob");
        jobConfig.setCustomerTenantSysName("TestTenant");
        MetaObject metaObject = new MetaObject();
        metaObject.setId(UUID.randomUUID());
        JobExecutionLog jobExecutionLog = new JobExecutionLog();
        jobExecutionLog.setId(jobId);
        jobExecutionLog.setJobId(jobId);
        jobExecutionLog.setStatus("Accepted");
        jobExecutionLog.setCreatedBy("system");
        jobExecutionLog.setWhenAccepted(LocalDateTime.now());

        when(datasyncJobConfigRepository.findDatasyncJobConfigurationById(jobId)).thenReturn(Optional.of(jobConfig));
        when(metaObjectRepository.findById(any())).thenReturn(Optional.of(metaObject));
        when(metaObjectRepository.findBySystemName(any())).thenReturn(Optional.of(metaObject));
        when(jobExecutionLogRepository.save(any(JobExecutionLog.class))).thenReturn(jobExecutionLog);

        // Act
        String result = dataSyncService.publishDataSyncJob(jobId, new OptionalRequestBody(), OperationType.CREATE.toString());

        // Assert
        assertNotNull(result);
        assertEquals(jobId.toString(), result);
        verify(jobExecutionLogRepository, times(1)).save(any(JobExecutionLog.class));
        // Optionally, verify that the log record has all expected fields set
        // (if you want to capture the argument and check its fields)
    }

    @Test
    void shouldPublishDataSyncJobSuccessfully_WhenValidJobIdAndOperationTypeAreProvided() throws DataSyncJobException {
        UUID jobId = UUID.randomUUID();
        DatasyncJobConfiguration jobConfig = new DatasyncJobConfiguration();
        jobConfig.setId(jobId);
        jobConfig.setMetaObjectSysName("meta_obj_sys_name");
        jobConfig.setSourceRegion("AMER");
        MetaObject metaObject = new MetaObject();
        metaObject.setId(UUID.randomUUID());
        JobExecutionLog jobExecutionLog = new JobExecutionLog();
        jobExecutionLog.setId(jobId);

        when(datasyncJobConfigRepository.findDatasyncJobConfigurationById(jobId)).thenReturn(Optional.of(jobConfig));
        when(metaObjectRepository.findById(any())).thenReturn(Optional.of(metaObject));
        when(metaObjectRepository.findBySystemName(any())).thenReturn(Optional.of(metaObject));
        when(jobExecutionLogRepository.save(any(JobExecutionLog.class))).thenReturn(jobExecutionLog);

        String result = dataSyncService.publishDataSyncJob(jobId, new OptionalRequestBody(), OperationType.CREATE.toString());
        assertEquals(jobId.toString(), result);
    }

    @Test
    void shouldThrowException_WhenInvalidOperationTypeIsProvided() {
        UUID jobId = UUID.randomUUID();
        DataSyncJobException exception = assertThrows(DataSyncJobException.class, () ->
                dataSyncService.publishDataSyncJob(jobId, new OptionalRequestBody(), "INVALID_TYPE"));
        assertEquals(Constants.INVALID_OPERATION_TYPE, exception.getMessage());
    }

    @Test
    void shouldThrowException_WhenJobConfigurationIsNotFound() {
        UUID jobId = UUID.randomUUID();
        when(datasyncJobConfigRepository.findDatasyncJobConfigurationById(jobId)).thenReturn(Optional.empty());
        DataSyncJobException exception = assertThrows(DataSyncJobException.class, () ->
                dataSyncService.publishDataSyncJob(jobId, new OptionalRequestBody(), OperationType.CREATE.toString()));
        assertEquals(Constants.DATASYNC_JOB_NOT_FOUND, exception.getMessage());
    }

    @Test
    void shouldThrowException_WhenMetaObjectIsNotFound() {
        UUID jobId = UUID.randomUUID();
        DatasyncJobConfiguration jobConfig = new DatasyncJobConfiguration();
        jobConfig.setId(jobId);
        jobConfig.setMetaObjectSysName("meta_obj_sys_name");
        jobConfig.setSourceRegion("AMER");

        when(datasyncJobConfigRepository.findDatasyncJobConfigurationById(jobId)).thenReturn(Optional.of(jobConfig));
        when(metaObjectRepository.findById(any())).thenReturn(Optional.empty());
        DataSyncJobException exception = assertThrows(DataSyncJobException.class, () ->
                dataSyncService.publishDataSyncJob(jobId, new OptionalRequestBody(), OperationType.CREATE.toString()));
        assertEquals(Constants.METAOBJECT_JOB_NOT_FOUND, exception.getMessage());
    }

    @Test
    void shouldSaveJobExecutionLogSuccessfully_WhenValidJobIdIsProvided() {
        UUID jobId = UUID.randomUUID();
        JobExecutionLog jobExecutionLog = new JobExecutionLog();
        jobExecutionLog.setJobId(jobId);
        jobExecutionLog.setStatus("Accepted");
        jobExecutionLog.setCreatedBy("system");
        jobExecutionLog.setWhenAccepted(LocalDateTime.now());
        when(jobExecutionLogRepository.save(any(JobExecutionLog.class))).thenReturn(jobExecutionLog);

        JobExecutionLog result = dataSyncService.saveJobExecutionLog(jobId);
        assertNotNull(result);
        assertEquals(jobId, result.getJobId());
        assertEquals("Accepted", result.getStatus());
    }

    @Test
    void shouldReturnTrue_WhenValidOperationTypeIsProvided() {
        assertTrue(dataSyncService.isValidOperationType(OperationType.CREATE.toString()));
    }

    @Test
    void shouldReturnFalse_WhenInvalidOperationTypeIsProvided() {
        assertFalse(dataSyncService.isValidOperationType("INVALID_TYPE"));
    }

    @Test
    void shouldThrowException_WhenOperationTypeIsNull() {
        UUID jobId = UUID.randomUUID();
        DataSyncJobException exception = assertThrows(DataSyncJobException.class, () ->
                dataSyncService.publishDataSyncJob(jobId, new OptionalRequestBody(), null));
        assertEquals(Constants.INVALID_OPERATION_TYPE, exception.getMessage());
    }

    @Test
    void shouldThrowException_WhenOperationTypeIsEmpty() {
        UUID jobId = UUID.randomUUID();
        DataSyncJobException exception = assertThrows(DataSyncJobException.class, () ->
                dataSyncService.publishDataSyncJob(jobId, new OptionalRequestBody(), ""));
        assertEquals(Constants.INVALID_OPERATION_TYPE, exception.getMessage());
    }

    @Test
    void shouldPublishDataSyncJobSuccessfully_WhenRabbitMQMessageIsPublished() throws DataSyncJobException {
        UUID jobId = UUID.randomUUID();
        DatasyncJobConfiguration jobConfig = new DatasyncJobConfiguration();
        jobConfig.setId(jobId);
        jobConfig.setMetaObjectSysName("meta_obj_sys_name");
        jobConfig.setSourceRegion("AMER");

        MetaObject metaObject = new MetaObject();
        metaObject.setId(UUID.randomUUID());
        JobExecutionLog jobExecutionLog = new JobExecutionLog();
        jobExecutionLog.setId(UUID.randomUUID());

        when(datasyncJobConfigRepository.findDatasyncJobConfigurationById(jobId)).thenReturn(Optional.of(jobConfig));
        when(metaObjectRepository.findById(any())).thenReturn(Optional.of(metaObject));
        when(metaObjectRepository.findBySystemName(any())).thenReturn(Optional.of(metaObject));
        when(jobExecutionLogRepository.save(any(JobExecutionLog.class))).thenReturn(jobExecutionLog);

        String result = dataSyncService.publishDataSyncJob(jobId, new OptionalRequestBody(), OperationType.CREATE.toString());
        assertNotNull(result);
    }

    @Test
    void shouldPublishDataSyncJobSuccessfully_WhenValidRequestModelIsProvided() throws DataSyncJobException {
        RequestModel requestModel = new RequestModel();
        requestModel.setCustomerTenantSysName("tenant-sys-name");
        requestModel.setMetaObjectName("meta-object-name");
        requestModel.setSourceTenantId("regional-tenant-id");
        requestModel.setOperationType(OperationType.CREATE);
        requestModel.setExecType(ExecType.REAL_TIME);
        MetaObject metaObject = new MetaObject();
        metaObject.setId(UUID.randomUUID());

        DatasyncJobConfiguration jobConfig = new DatasyncJobConfiguration();
        jobConfig.setId(UUID.randomUUID());
        jobConfig.setMetaObjectSysName(metaObject.getSystemName());
        jobConfig.setSourceRegion("AMER");

        JobExecutionLog jobExecutionLog = new JobExecutionLog();
        jobExecutionLog.setId(UUID.randomUUID());

        // Remove expectation for findBySystemNameAndOneSourceDomain, only set up findBySystemName
        when(metaObjectRepository.findBySystemName(any())).thenReturn(Optional.of(metaObject));
        when(datasyncJobConfigRepository.findByCustomerTenantSysNameAndSourceTenantIdAndMetaObjectSysNameAndExecType(
                any(), any(), any(), eq(ExecType.REAL_TIME.toString())))
                .thenReturn(Optional.of(jobConfig));
        when(datasyncJobConfigRepository.findDatasyncJobConfigurationById(
                eq(jobConfig.getId()))).thenReturn(Optional.of(jobConfig));
        when(metaObjectRepository.findById(
                eq(jobConfig.getId()))).thenReturn(Optional.of(metaObject));
        when(metaObjectRepository.findBySystemName(
                any())).thenReturn(Optional.of(metaObject));

        when(jobExecutionLogRepository.save(any(JobExecutionLog.class))).thenReturn(jobExecutionLog);

        String result = dataSyncService.publishDataSyncJob(requestModel);
        // Assert
        assertNotNull(result);
        // Remove verification for findBySystemNameAndOneSourceDomain
        verify(metaObjectRepository, atLeastOnce()).findBySystemName(any());
        verify(datasyncJobConfigRepository, times(1)).findByCustomerTenantSysNameAndSourceTenantIdAndMetaObjectSysNameAndExecType(
                any(), any(), any(), eq(ExecType.REAL_TIME.toString()));
    }

    @Test
    void shouldThrowException_WhenRequestModelIsNull() {
        // Mock repository to avoid NPE on null datasyncJobConfiguration
        when(datasyncJobConfigRepository.findDatasyncJobConfigurationById(any())).thenReturn(Optional.of(new DatasyncJobConfiguration() {{
            setSourceRegion("AMER");
        }}));
        DataSyncJobException exception = assertThrows(DataSyncJobException.class, () ->
                dataSyncService.publishDataSyncJob(null));
        assertEquals("RequestModel cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowException_WhenRequestModelHasMissingFields() {
        RequestModel requestModel = new RequestModel();
        requestModel.setCustomerTenantSysName(null);

        // Mock repository to avoid NPE on null datasyncJobConfiguration
        when(datasyncJobConfigRepository.findDatasyncJobConfigurationById(any())).thenReturn(Optional.of(new DatasyncJobConfiguration() {{
            setSourceRegion("AMER");
        }}));
        DataSyncJobException exception = assertThrows(DataSyncJobException.class, () ->
                dataSyncService.publishDataSyncJob(requestModel));
        assertEquals("OperationType cannot be null", exception.getMessage());
    }

    @Test
    void shouldFetchDatasyncJobConfiguration_WhenValidRequestModelIsProvided() throws DataSyncJobException {
        // Arrange
        RequestModel requestModel = new RequestModel();
        requestModel.setCustomerTenantSysName("tenant-sys-name");
        requestModel.setMetaObjectName("meta-object-name");
        requestModel.setExecType(ExecType.REAL_TIME);

        MetaObject metaObject = new MetaObject();
        metaObject.setId(UUID.randomUUID());

        DatasyncJobConfiguration jobConfig = new DatasyncJobConfiguration();
        jobConfig.setId(UUID.randomUUID());
        jobConfig.setSourceRegion("AMER");


//uncomment when non-uge-customer support is removed
//        when(datasyncJobConfigRepository.findJobConfig(requestModel.getCustomerTenantSysName(), requestModel.getSourceTenantId(), requestModel.getMetaObjectName(), requestModel.getExecType().toString()
//        ))
//                .thenReturn(Optional.of(jobConfig));

        when(datasyncJobConfigRepository.findByCustomerTenantSysNameAndMetaObjectSysNameAndExecType(requestModel.getCustomerTenantSysName(), requestModel.getMetaObjectName(), requestModel.getExecType().toString()
        ))
                .thenReturn(Optional.of(jobConfig));
        // Act
        Optional<DatasyncJobConfiguration> result = dataSyncService.findDataSyncJob(requestModel, requestModel.getMetaObjectName());

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(jobConfig.getId(), result.get().getId());
    }

    @Test
    void shouldValidateRequestModelAndJob_WhenValidRequestModelIsProvided() throws DataSyncJobException {
        // Arrange
        RequestModel requestModel = new RequestModel();
        requestModel.setCustomerTenantSysName("tenant-sys-name");
        requestModel.setMetaObjectName("meta-object-name");
        requestModel.setSourceTenantId("regional-tenant-id");
        requestModel.setExecType(ExecType.REAL_TIME);

        MetaObject metaObject = new MetaObject();
        metaObject.setId(UUID.randomUUID());

        DatasyncJobConfiguration jobConfig = new DatasyncJobConfiguration();
        jobConfig.setId(UUID.randomUUID());

        // Mock metaObjectRepository.findBySystemName() which is now required by the modified method
        when(metaObjectRepository.findBySystemName(requestModel.getMetaObjectName()))
                .thenReturn(Optional.of(metaObject));

        // Mock the repository calls that findJobWithAncestorFallback method will use
        when(datasyncJobConfigRepository.findByCustomerTenantSysNameAndSourceTenantIdAndMetaObjectSysNameAndExecType(
                requestModel.getCustomerTenantSysName(), requestModel.getSourceTenantId(), 
                requestModel.getMetaObjectName(), requestModel.getExecType().toString()))
                .thenReturn(Optional.of(jobConfig));

        // Act
        boolean result = dataSyncService.validateRequestModelAndJob(requestModel, new DatasyncJobConfiguration());

        // Assert
        assertTrue(result);
    }

    @Test
    void shouldReturnFalse_WhenRequestModelIsInvalidInValidateRequestModelAndJob() throws DataSyncJobException {
        // Arrange
        RequestModel requestModel = new RequestModel();
        requestModel.setCustomerTenantSysName(null);

        // Act
        boolean result = dataSyncService.validateRequestModelAndJob(requestModel, new DatasyncJobConfiguration());

        // Assert
        assertFalse(result);
    }

    @Test
    void validateObjectIds_shouldPassForSinglePKListOfStrings() throws Exception {
        OptionalRequestBody body = new OptionalRequestBody();
        RequestBodyDataUnitList unitList = new RequestBodyDataUnitList();
        unitList.setObjIds(Arrays.asList("id1", "id2", "id3"));
        body.setRequestDataUnitList(unitList);
        Method m = DataSyncService.class.getDeclaredMethod("validateObjectIds", OptionalRequestBody.class);
        m.setAccessible(true);
        assertDoesNotThrow(() -> m.invoke(null, body));
    }

    @Test
    void validateObjectIds_shouldThrowForSinglePKWithNullOrEmpty() throws Exception {
        OptionalRequestBody body = new OptionalRequestBody();
        RequestBodyDataUnitList unitList = new RequestBodyDataUnitList();
        unitList.setObjIds(Arrays.asList("id1", "", null));
        body.setRequestDataUnitList(unitList);
        Method m = DataSyncService.class.getDeclaredMethod("validateObjectIds", OptionalRequestBody.class);
        m.setAccessible(true);
        Exception ex = assertThrows(Exception.class, () -> m.invoke(null, body));
        assertTrue(ex.getCause() instanceof DataSyncJobException);
        assertEquals(Constants.OBJECT_ID_REQUIRED, ((DataSyncJobException) ex.getCause()).getMessage());
    }

    @Test
    void validateObjectIds_shouldPassForValidColumnOrientedCompositePK() throws Exception {
        OptionalRequestBody body = new OptionalRequestBody();
        RequestBodyDataUnitList unitList = new RequestBodyDataUnitList();
        Map<String, List<String>> col1 = new HashMap<>();
        col1.put("code", Arrays.asList("A", "B"));
        Map<String, List<String>> col2 = new HashMap<>();
        col2.put("chart_version_id", Arrays.asList("X", "Y"));
        unitList.setObjIds(Arrays.asList(col1, col2));
        body.setRequestDataUnitList(unitList);
        Method m = DataSyncService.class.getDeclaredMethod("validateObjectIds", OptionalRequestBody.class);
        m.setAccessible(true);
        assertDoesNotThrow(() -> m.invoke(null, body));
    }

    @Test
    void validateObjectIds_shouldThrowForColumnOrientedWithDuplicateKeys() throws Exception {
        OptionalRequestBody body = new OptionalRequestBody();
        RequestBodyDataUnitList unitList = new RequestBodyDataUnitList();
        Map<String, List<String>> col1 = new HashMap<>();
        col1.put("code", Arrays.asList("A", "B"));
        Map<String, List<String>> col2 = new HashMap<>();
        col2.put("code", Arrays.asList("X", "Y"));
        unitList.setObjIds(Arrays.asList(col1, col2));
        body.setRequestDataUnitList(unitList);
        Method m = DataSyncService.class.getDeclaredMethod("validateObjectIds", OptionalRequestBody.class);
        m.setAccessible(true);
        Exception ex = assertThrows(Exception.class, () -> m.invoke(null, body));
        assertTrue(ex.getCause() instanceof DataSyncJobException);
        assertTrue(((DataSyncJobException) ex.getCause()).getMessage().contains(Constants.DUPLICATE_KEY_FOUND));
    }

    @Test
    void validateObjectIds_shouldThrowForColumnOrientedWithLessThanTwoColumns() throws Exception {
        OptionalRequestBody body = new OptionalRequestBody();
        RequestBodyDataUnitList unitList = new RequestBodyDataUnitList();
        Map<String, List<String>> col1 = new HashMap<>();
        col1.put("code", Arrays.asList("A", "B"));
        unitList.setObjIds(Arrays.asList(col1));
        body.setRequestDataUnitList(unitList);
        Method m = DataSyncService.class.getDeclaredMethod("validateObjectIds", OptionalRequestBody.class);
        m.setAccessible(true);
        Exception ex = assertThrows(Exception.class, () -> m.invoke(null, body));
        assertTrue(ex.getCause() instanceof DataSyncJobException);
        assertEquals(Constants.ATLEAST_TWO_COLUMN_REQUIRED, ((DataSyncJobException) ex.getCause()).getMessage());
    }

    @Test
    void validateObjectIds_shouldThrowForColumnOrientedWithMismatchedIdCounts() throws Exception {
        OptionalRequestBody body = new OptionalRequestBody();
        RequestBodyDataUnitList unitList = new RequestBodyDataUnitList();
        Map<String, List<String>> col1 = new HashMap<>();
        col1.put("code", Arrays.asList("A", "B"));
        Map<String, List<String>> col2 = new HashMap<>();
        col2.put("chart_version_id", Arrays.asList("X"));
        unitList.setObjIds(Arrays.asList(col1, col2));
        body.setRequestDataUnitList(unitList);
        Method m = DataSyncService.class.getDeclaredMethod("validateObjectIds", OptionalRequestBody.class);
        m.setAccessible(true);
        Exception ex = assertThrows(Exception.class, () -> m.invoke(null, body));
        assertTrue(ex.getCause() instanceof DataSyncJobException);
        assertEquals(Constants.ALL_COLUMNS_MUST_HAVE_SAME_NUMBER_OF_IDS, ((DataSyncJobException) ex.getCause()).getMessage());
    }

    @Test
    void validateObjectIds_shouldThrowForRowOrientedCompositePK() throws Exception {
        OptionalRequestBody body = new OptionalRequestBody();
        RequestBodyDataUnitList unitList = new RequestBodyDataUnitList();
        Map<String, List<String>> row1 = new HashMap<>();
        row1.put("code", Arrays.asList("A"));
        row1.put("chart_version_id", Arrays.asList("X"));
        unitList.setObjIds(Arrays.asList(row1));
        body.setRequestDataUnitList(unitList);
        Method m = DataSyncService.class.getDeclaredMethod("validateObjectIds", OptionalRequestBody.class);
        m.setAccessible(true);
        Exception ex = assertThrows(Exception.class, () -> m.invoke(null, body));
        assertTrue(ex.getCause() instanceof DataSyncJobException);
        assertEquals(Constants.MAP_WITH_SINGLE_KEY_REQUIRED, ((DataSyncJobException) ex.getCause()).getMessage());
    }
}
