package com.thomsonreuters.dataconnect.executionengine.datasyncactivity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thomsonreuters.dataconnect.common.executioncontext.DataSyncActivityContext;
import com.thomsonreuters.dataconnect.common.executioncontext.ExecutionContext;
import com.thomsonreuters.dataconnect.executionengine.data.DataSetCollection;
import com.thomsonreuters.dataconnect.executionengine.exceptionhandler.DataSyncJobException;
import com.thomsonreuters.dataconnect.executionengine.model.entity.DataSyncActivityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DataSyncActivityEngineTest {

    private DataSyncActivityEngine activityEngine;

    @Mock
    private ExecutionContext executionContext;

    @Mock
    private DataSyncActivityContext dataSyncActivityContext;

    @Mock
    private DataSetCollection dataSetCollection;

    @Mock
    private DataSyncActivity dataSyncActivity;

    @Mock
    private DataSyncActivityFactory dataSyncActivityFactory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        activityEngine = new DataSyncActivityEngine();
        activityEngine.setDataSyncActivityFactory(dataSyncActivityFactory);
        dataSetCollection.setDataSets(List.of());// Inject the mock factory
    }

    @Test
    void shouldInitializeActivities_whenContextIsProvided() throws DataSyncJobException {
        List<DataSyncActivityConfig> activities = new ArrayList<>();
        DataSyncActivityConfig activityConfig = new DataSyncActivityConfig();
        activityConfig.setActivitySysName("TestActivity");
        activities.add(activityConfig);

        // Mock the context and factory behavior
        when(executionContext.getContextByName(ExecutionContext.DATA_SYNC_ACTIVITY_CONTEXT)).thenReturn(dataSyncActivityContext);
        when(dataSyncActivityContext.getValue(DataSyncActivityContext.DATA_SYNC_ACTIVITY_CONTEXT)).thenReturn(activities);
        when(dataSyncActivityFactory.getDataSyncActivity("TestActivity")).thenReturn(dataSyncActivity);

        // Call the initialize method
        activityEngine.initialize(executionContext);

        // Verify that the activity was initialized
        verify(dataSyncActivity, times(1)).initialize(executionContext);
    }


    @Test
    void shouldExecuteAllActivities_whenActivitiesPresent() throws DataSyncJobException, JsonProcessingException {
        activityEngine.addActivity(dataSyncActivity);

        // Mock the intermediate and final DataSetCollection
        DataSetCollection intermediateDataSet = mock(DataSetCollection.class);
        DataSetCollection finalDataSet = mock(DataSetCollection.class);

        // Mock the behavior of the dataSyncActivity
        when(dataSyncActivity.execute(dataSetCollection, executionContext)).thenReturn(intermediateDataSet);
        when(dataSyncActivity.execute(intermediateDataSet, executionContext)).thenReturn(finalDataSet);

        // Call the execute method
        DataSetCollection result = activityEngine.execute(dataSetCollection, executionContext);

        // Assert and verify
        assertNotNull(result); // Ensure the result is not null
        verify(dataSyncActivity, times(1)).validate(dataSetCollection);
        verify(dataSyncActivity, times(1)).execute(dataSetCollection, executionContext);
    }

    @Test
    void shouldReturnInputDataSet_whenNoActivitiesArePresent() throws DataSyncJobException, JsonProcessingException {
        DataSetCollection result = activityEngine.execute(dataSetCollection, executionContext);

        assertEquals(dataSetCollection, result);
    }

    @Test
    void shouldCleanupAllActivities_whenCleanupIsCalled() {
        activityEngine.addActivity(dataSyncActivity);

        activityEngine.cleanup();

        verify(dataSyncActivity, times(1)).cleanup();
    }
}