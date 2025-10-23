package com.aditya.dataconnect.executionengine.entity;

import com.aditya.dataconnect.executionengine.model.entity.DataSyncActivityConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataSyncActivityConfigTest {

    @Test
    void shouldSetAndGetFields_whenEntityIsInitialized() {
        DataSyncActivityConfig activity = new DataSyncActivityConfig();
        activity.setId(1);
        activity.setActivitySysName("TestActivity");
        activity.setActivityId(100);
        activity.setDatasyncJobSysName("TestJob");
        activity.setExecType("SYNC");
        activity.setExecSeq(1);
        activity.setEventType("CREATE");
        activity.setActivityType("POST_SYNC");

        assertEquals(1, activity.getId());
        assertEquals("TestActivity", activity.getActivitySysName());
        assertEquals(100, activity.getActivityId());
        assertEquals("TestJob", activity.getDatasyncJobSysName());
        assertEquals("SYNC", activity.getExecType());
        assertEquals(1, activity.getExecSeq());
        assertEquals("CREATE", activity.getEventType());
        assertEquals("POST_SYNC", activity.getActivityType());
    }
}