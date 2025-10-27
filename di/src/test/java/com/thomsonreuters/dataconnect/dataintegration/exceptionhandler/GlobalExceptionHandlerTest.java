package com.thomsonreuters.dataconnect.dataintegration.exceptionhandler;//package com.thomsonreuters.dataconnect.dataintegration.exceptionhandler;

import com.thomsonreuters.dataconnect.common.logging.LogClient;
import com.thomsonreuters.dataconnect.dataintegration.exceptionhandler.DataSyncJobException;
import com.thomsonreuters.dataconnect.dataintegration.exceptionhandler.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import com.thomsonreuters.dataconnect.dataintegration.configuration.RegionConfig;
import static org.mockito.Mockito.when;
import com.thomsonreuters.dataconnect.common.logging.LogClient;

public class GlobalExceptionHandlerTest {

    @Test
    void shouldReturnInternalServerErrorResponse_WhenDataSyncJobExceptionOccurs() {
        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();
        RegionConfig regionConfig = mock(RegionConfig.class);
        when(regionConfig.getRegion()).thenReturn("TEST_REGION");
        exceptionHandler.setRegionConfig(regionConfig);
        LogClient dcLogClient = mock(LogClient.class);
        try {
            java.lang.reflect.Field logField = exceptionHandler.getClass().getDeclaredField("logClient");
            logField.setAccessible(true);
            logField.set(exceptionHandler, dcLogClient);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        com.thomsonreuters.dataconnect.dataintegration.repository.ActivityLogRepository activityLogRepository =
            mock(com.thomsonreuters.dataconnect.dataintegration.repository.ActivityLogRepository.class);
        try {
            java.lang.reflect.Field repoField = exceptionHandler.getClass().getDeclaredField("activityLogRepository");
            repoField.setAccessible(true);
            repoField.set(exceptionHandler, activityLogRepository);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        DataSyncJobException exception = new DataSyncJobException("Test exception message", "INTERNAL_SERVER_ERROR");
        WebRequest request = mock(WebRequest.class);

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleCustomError(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        //assertEquals("Test exception message", response.getBody());
    }

    @Test
    void shouldReturnBadRequestResponse_WhenGenericExceptionOccurs() {
        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();
        RegionConfig regionConfig = mock(RegionConfig.class);
        when(regionConfig.getRegion()).thenReturn("TEST_REGION");
        exceptionHandler.setRegionConfig(regionConfig);
        LogClient logClient = mock(LogClient.class);
        try {
            java.lang.reflect.Field logField = exceptionHandler.getClass().getDeclaredField("logClient");
            logField.setAccessible(true);
            logField.set(exceptionHandler, logClient);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        com.thomsonreuters.dataconnect.dataintegration.repository.ActivityLogRepository activityLogRepository =
            mock(com.thomsonreuters.dataconnect.dataintegration.repository.ActivityLogRepository.class);
        try {
            java.lang.reflect.Field repoField = exceptionHandler.getClass().getDeclaredField("activityLogRepository");
            repoField.setAccessible(true);
            repoField.set(exceptionHandler, activityLogRepository);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Exception exception = new Exception("Test exception message");
        WebRequest request = mock(WebRequest.class);

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleGenericError(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //assertEquals("Test exception message", response.getBody());
    }
}
