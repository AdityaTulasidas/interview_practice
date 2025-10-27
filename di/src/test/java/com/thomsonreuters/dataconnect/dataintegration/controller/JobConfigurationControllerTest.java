package com.thomsonreuters.dataconnect.dataintegration.controller;

import com.thomsonreuters.dataconnect.dataintegration.configuration.RegionConfig;
import com.thomsonreuters.dataconnect.dataintegration.controllers.JobConfigurationController;
import com.thomsonreuters.dataconnect.dataintegration.dto.DatasyncJobConfigurationRequestDTO;
import com.thomsonreuters.dataconnect.dataintegration.dto.DatasyncJobConfigurationUpdateRequestDTO;
import com.thomsonreuters.dataconnect.dataintegration.exceptionhandler.DataSyncJobException;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.DatasyncJobConfiguration;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.MetaObject;
import com.thomsonreuters.dataconnect.dataintegration.services.DataSyncJobConfigService;
import com.thomsonreuters.dataconnect.dataintegration.services.MetaObjectService;
import com.thomsonreuters.dep.api.spring.response.ApiCollection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JobConfigurationControllerTest {

    @Mock
    private DataSyncJobConfigService dataSyncJobConfigService;

    @InjectMocks
    private JobConfigurationController jobConfigurationController;

    @Mock
    private RegionConfig regionConfig;

    @Mock
    private com.thomsonreuters.dataconnect.common.logging.LogClient logClient;

    @Mock
    private com.thomsonreuters.dataconnect.dataintegration.repository.ActivityLogRepository activityLogRepository;

    @Mock
    private MetaObjectService metaObjectService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        try {
            java.lang.reflect.Field metaField = jobConfigurationController.getClass().getDeclaredField("metaObjectService");
            metaField.setAccessible(true);
            metaField.set(jobConfigurationController, metaObjectService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldCreateDataSyncJobSuccessfully_WhenRequestIsValid() throws Exception {
        // Arrange
        DatasyncJobConfigurationRequestDTO request = new DatasyncJobConfigurationRequestDTO();
        request.setSystemName("TestJob");
        request.setCustomerTenantSysName("tenant123");
        UUID metaObjectId = UUID.randomUUID();
        request.setMetaObjectSysName("TestMetaObject");
        String jobId = UUID.randomUUID().toString();

        when(regionConfig.getRegion()).thenReturn("us-east-1");
        when(dataSyncJobConfigService.createDataSyncJob(request)).thenReturn(jobId);
        //when(logClient.publishLog(any(LogRecord.class))).thenReturn(true);
        when(activityLogRepository.save(any())).thenReturn(null);
        doNothing().when(metaObjectService).incrementUsageCount(metaObjectId, 1, true);
        MetaObject metaObject = new MetaObject();
        metaObject.setId(metaObjectId);
        when(dataSyncJobConfigService.getMetaObjectDetails(anyString())).thenReturn(metaObject);
        // Add a mock BindingResult for the createDataSyncJob call
        org.springframework.validation.BindingResult bindingResult = mock(org.springframework.validation.BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        ResponseEntity<Object> response = jobConfigurationController.createDataSyncJob(request, bindingResult);

        // Assert
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCodeValue());
        assertNotNull(response.getBody());
        verify(dataSyncJobConfigService, times(1)).createDataSyncJob(request);
        verify(metaObjectService, times(1)).incrementUsageCount(metaObjectId, 1, true);
    }


    @Test
    void shouldReturnJobConfigurationSuccessfully_WhenJobIdIsValid() throws DataSyncJobException {
        UUID jobId = UUID.randomUUID();
        DatasyncJobConfigurationRequestDTO responseDTO = new DatasyncJobConfigurationRequestDTO();
        responseDTO.setId(jobId);

        when(dataSyncJobConfigService.getJobConfigurationDetails(jobId)).thenReturn(responseDTO);

        ResponseEntity<Object> response = jobConfigurationController.getJobConfiguration(jobId);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        verify(dataSyncJobConfigService, times(1)).getJobConfigurationDetails(jobId);
    }

    @Test
    void shouldUpdateDataSyncJobSuccessfully_WhenRequestIsValid() throws Exception {
        UUID jobId = UUID.randomUUID();
        DatasyncJobConfigurationUpdateRequestDTO request = new DatasyncJobConfigurationUpdateRequestDTO();
        request.setId(jobId);
        String updatedJobId = jobId.toString();

        when(dataSyncJobConfigService.updateDataSyncJob(request)).thenReturn(updatedJobId);

        // Add a mock BindingResult for the updateDataSyncJob call
        org.springframework.validation.BindingResult bindingResult = mock(org.springframework.validation.BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        ResponseEntity<Object> response = jobConfigurationController.updateDataSyncJob(jobId, request, bindingResult);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(dataSyncJobConfigService, times(1)).updateDataSyncJob(request);
    }

    @Test
    void shouldReturnJobs_WhenRequestIsValid() throws DataSyncJobException {

        ApiCollection<DatasyncJobConfiguration> mockCollection = mock(ApiCollection.class);
        when(mockCollection.getItems()).thenReturn(List.of(new DatasyncJobConfiguration()));
        ResponseEntity<?> mockResponse = ResponseEntity.ok(mockCollection);
        when(dataSyncJobConfigService.searchJobConfiguration(0, 10, "+jobname", null))
                .thenReturn((ResponseEntity) mockResponse);

        ResponseEntity<?> response = jobConfigurationController.getDataSyncJobs(0, 10, "+jobname", null);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockCollection, response.getBody());
        verify(dataSyncJobConfigService, times(1)).searchJobConfiguration(0, 10, "+jobname", null);
    }

    @Test
    void shouldAllowMultipleValidFilters() throws DataSyncJobException {
        String filter = "\"jobname\" eq 'test'; \"metaObjectId\" contains 'abc'";
        ApiCollection<DatasyncJobConfiguration> mockCollection = mock(ApiCollection.class);
        when(mockCollection.getItems()).thenReturn(List.of(new DatasyncJobConfiguration()));
        ResponseEntity<?> mockResponse = ResponseEntity.ok(mockCollection);
        when(dataSyncJobConfigService.searchJobConfiguration(0, 10, null, filter))
                .thenReturn((ResponseEntity) mockResponse);
        ResponseEntity<?> response = jobConfigurationController.getDataSyncJobs(0, 10, null, filter);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockCollection, response.getBody());
    }

    @Test
    void shouldThrowException_WhenOffsetIsNegative() {
        DataSyncJobException ex = assertThrows(DataSyncJobException.class, () ->
                jobConfigurationController.getDataSyncJobs(-1, 10, null, null));
        assertEquals("Offset must be greater than or equal to 0.", ex.getMessage());
    }

    @Test
    void shouldThrowException_WhenLimitIsZero() {
        DataSyncJobException ex = assertThrows(DataSyncJobException.class, () ->
                jobConfigurationController.getDataSyncJobs(0, 0, null, null));
        assertEquals("Limit must be greater than 0.", ex.getMessage());
    }

    @Test
    void shouldUseDefaultValues_WhenOffsetAndLimitAreNull() throws DataSyncJobException {
        ApiCollection<DatasyncJobConfiguration> mockCollection = mock(ApiCollection.class);
        ResponseEntity<?> mockResponse = ResponseEntity.ok(mockCollection);
        when(dataSyncJobConfigService.searchJobConfiguration(0, 200, null, null))
                .thenReturn((ResponseEntity) mockResponse);

        ResponseEntity<?> response = jobConfigurationController.getDataSyncJobs(null, null, null, null);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockCollection, response.getBody());
        verify(dataSyncJobConfigService, times(1)).searchJobConfiguration(0, 200, null, null);
    }
}