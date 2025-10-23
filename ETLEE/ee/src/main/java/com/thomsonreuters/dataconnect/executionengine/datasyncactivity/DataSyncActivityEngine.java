package com.thomsonreuters.dataconnect.executionengine.datasyncactivity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thomsonreuters.dataconnect.common.executioncontext.DataSyncActivityContext;
import com.thomsonreuters.dataconnect.common.executioncontext.ExecutionContext;

import com.thomsonreuters.dataconnect.executionengine.data.DataSetCollection;
import com.thomsonreuters.dataconnect.executionengine.exceptionhandler.DataSyncJobException;
import com.thomsonreuters.dataconnect.executionengine.model.entity.DataSyncActivityConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DataSyncActivityEngine {
    private final List<DataSyncActivity> activities = new ArrayList<>();
    private ExecutionContext context;
    DataSyncActivityContext dataSyncActivityContext;

    @Autowired
    @Setter
    private DataSyncActivityFactory dataSyncActivityFactory;
    private List<DataSyncActivityConfig> dataSyncActivities= new ArrayList<>();


    public void addActivity(DataSyncActivity activity) {
        if(activity!=null)
            activities.add(activity);
    }

    public void initialize(ExecutionContext ctx) throws DataSyncJobException {
        this.context = ctx;
        if(ctx!=null)
            dataSyncActivityContext=((DataSyncActivityContext) ctx.getContextByName(ExecutionContext.DATA_SYNC_ACTIVITY_CONTEXT));

        if(dataSyncActivityContext !=null) {
            dataSyncActivities=dataSyncActivityContext.getValue(DataSyncActivityContext.DATA_SYNC_ACTIVITY_CONTEXT);
            if(dataSyncActivities!=null && !dataSyncActivities.isEmpty()) {
                for (DataSyncActivityConfig activity : dataSyncActivities) {
                    DataSyncActivity syncActivity = dataSyncActivityFactory.getDataSyncActivity(activity.getActivitySysName());
                    addActivity(syncActivity);
                    if(syncActivity!=null)
                        syncActivity.initialize(ctx);
                }
            }
    }
        else {
            log.warn("No Data Sync Activities found to initialize.");
        }
    }

    public DataSetCollection execute(DataSetCollection input,ExecutionContext context) throws DataSyncJobException, JsonProcessingException {
        DataSetCollection currentDataSet = input;
        if(!activities.isEmpty()) {
            for (DataSyncActivity activity : activities) {
                activity.validate(currentDataSet);
                currentDataSet = activity.execute(currentDataSet,context);
            }
        }
        else {
            log.info("No Activities found to execute, returning input data set.");
        }
        return currentDataSet;
    }

    public void cleanup() {
        for (DataSyncActivity activity : activities) {
            activity.cleanup();
        }
    }
}

