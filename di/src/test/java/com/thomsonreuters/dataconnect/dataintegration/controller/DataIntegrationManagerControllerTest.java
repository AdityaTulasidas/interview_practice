package com.thomsonreuters.dataconnect.dataintegration.controller;

import com.thomsonreuters.dataconnect.dataintegration.configuration.RegionConfig;
import com.thomsonreuters.dataconnect.dataintegration.constant.Constants;
import com.thomsonreuters.dataconnect.dataintegration.controllers.DataIntegrationManagerController;
import com.thomsonreuters.dataconnect.dataintegration.exceptionhandler.DataSyncJobException;
import com.thomsonreuters.dataconnect.dataintegration.model.pojo.OptionalRequestBody;
import com.thomsonreuters.dataconnect.dataintegration.model.pojo.RequestModel;
import com.thomsonreuters.dataconnect.dataintegration.services.DataSyncService;
import com.thomsonreuters.dataconnect.dataintegration.utils.CommonUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class DataIntegrationManagerControllerTest {

    @InjectMocks
    private DataIntegrationManagerController dataIntegrationManagerController;

    @Mock
    private DataSyncService dataSyncService;

    @Mock
    private RegionConfig regionConfig;

    @Mock
    private com.thomsonreuters.dataconnect.common.logging.LogClient logClient;

    @Mock
    private com.thomsonreuters.dataconnect.dataintegration.repository.ActivityLogRepository activityLogRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldPublishData_WhenRequestBodyIsValid() throws Exception {
        UUID jobId = UUID.randomUUID();
        OptionalRequestBody optionalRequestBody = new OptionalRequestBody();
        String operationType = "CREATE";
        BindingResult bindingResult = org.mockito.Mockito.mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(dataSyncService.publishDataSyncJob(any(UUID.class), any(OptionalRequestBody.class), anyString()))
                .thenReturn(jobId.toString());

        ResponseEntity<Object> response = dataIntegrationManagerController.publishDataSyncJob(jobId, optionalRequestBody, operationType, bindingResult);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(CommonUtils.generateResponse(jobId.toString(), Constants.JOB_EXECUTION_SUCCESS), response.getBody());
    }

    @Test
    public void shouldThrowBadRequest_WhenOperationTypeIsInvalid() throws Exception {
        UUID jobId = UUID.randomUUID();
        OptionalRequestBody optionalRequestBody = new OptionalRequestBody();
        String operationType = "";
        BindingResult bindingResult = org.mockito.Mockito.mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(dataSyncService.publishDataSyncJob(any(UUID.class), any(OptionalRequestBody.class), anyString()))
                .thenThrow(new DataSyncJobException(Constants.INVALID_OPERATION_TYPE, Constants.BAD_REQUEST));

        try {
            dataIntegrationManagerController.publishDataSyncJob(jobId, optionalRequestBody, operationType, bindingResult);
        } catch (DataSyncJobException e) {
            assertEquals(Constants.INVALID_OPERATION_TYPE, e.getMessage());
            assertEquals(Constants.BAD_REQUEST, e.getCode());
        }
    }

    @Test
    public void shouldThrowInternalServerError_WhenPublishFails() throws Exception {
        UUID jobId = UUID.randomUUID();
        OptionalRequestBody optionalRequestBody = new OptionalRequestBody();
        String operationType = "CREATE";
        BindingResult bindingResult = org.mockito.Mockito.mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(dataSyncService.publishDataSyncJob(any(UUID.class), any(OptionalRequestBody.class), anyString()))
                .thenThrow(new RuntimeException(Constants.ERROR_RETRIEVING_DATA_SOURCE_ENTRIES));

        try {
            dataIntegrationManagerController.publishDataSyncJob(jobId, optionalRequestBody, operationType, bindingResult);
        } catch (RuntimeException e) {
            assertEquals(Constants.ERROR_RETRIEVING_DATA_SOURCE_ENTRIES, e.getMessage());
        }
    }

    @Test
    public void shouldPublishDataSync_WhenRequestModelIsValid() throws Exception {
        String jobId = UUID.randomUUID().toString();
        RequestModel requestModel = new RequestModel();

        when(dataSyncService.publishDataSyncJob(any(RequestModel.class)))
                .thenReturn(jobId);

        ResponseEntity<Object> response = dataIntegrationManagerController.publishDataSync(requestModel);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(CommonUtils.generateResponse(jobId, Constants.JOB_EXECUTION_SUCCESS), response.getBody());
    }

    @Test
    public void shouldThrowBadRequest_WhenRequestModelIsInvalid() throws Exception {
        RequestModel requestModel = new RequestModel();

        when(dataSyncService.publishDataSyncJob(any(RequestModel.class)))
                .thenThrow(new DataSyncJobException(Constants.VALIDATION_ERROR, Constants.BAD_REQUEST));

        try {
            dataIntegrationManagerController.publishDataSync(requestModel);
        } catch (DataSyncJobException e) {
            assertEquals(Constants.VALIDATION_ERROR, e.getMessage());
            assertEquals(Constants.BAD_REQUEST, e.getCode());
        }
    }

    @Test
    public void shouldThrowInternalServerError_WhenPublishDataSyncFails() throws Exception {
        RequestModel requestModel = new RequestModel();

        when(dataSyncService.publishDataSyncJob(any(RequestModel.class)))
                .thenThrow(new RuntimeException(Constants.ERROR_RETRIEVING_DATA_SOURCE_ENTRIES));

        try {
            dataIntegrationManagerController.publishDataSync(requestModel);
        } catch (RuntimeException e) {
            assertEquals(Constants.ERROR_RETRIEVING_DATA_SOURCE_ENTRIES, e.getMessage());
        }
    }

    @Test
    public void shouldValidateDataSyncJob_WhenRequestModelIsValid() throws Exception {
        RequestModel requestModel = new RequestModel();
        when(dataSyncService.validateRequestModelAndJob(any(RequestModel.class), any()))
                .thenReturn(true);

        ResponseEntity<Boolean> response = dataIntegrationManagerController.validateDataSyncJob(requestModel);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody());
    }

    @Test
    public void shouldReturnBadRequest_WhenValidationFails() throws Exception {
        RequestModel requestModel = new RequestModel();
        when(dataSyncService.validateRequestModelAndJob(any(RequestModel.class), any()))
                .thenThrow(new DataSyncJobException(Constants.VALIDATION_ERROR, Constants.OK));

        ResponseEntity<Boolean> response = dataIntegrationManagerController.validateDataSyncJob(requestModel);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(false, response.getBody());
    }
}