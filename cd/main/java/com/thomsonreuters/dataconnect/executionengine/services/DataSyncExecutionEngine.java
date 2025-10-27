package com.thomsonreuters.dataconnect.executionengine.services;

import com.thomsonreuters.dataconnect.common.executioncontext.ExecutionContext;
import com.thomsonreuters.dataconnect.common.executioncontext.RegionalJobContext;
import com.thomsonreuters.dataconnect.common.executioncontext.TransformationContext;
import com.thomsonreuters.dataconnect.common.logging.LogClient;
import com.thomsonreuters.dataconnect.executionengine.adapters.AdapterFactory;
import com.thomsonreuters.dataconnect.executionengine.adapters.IntegrationAdapter;
import com.thomsonreuters.dataconnect.executionengine.adapters.impl.DatabaseAdapter;
import com.thomsonreuters.dataconnect.executionengine.data.DataSet;
import com.thomsonreuters.dataconnect.executionengine.data.DataSetCollection;
import com.thomsonreuters.dataconnect.executionengine.exceptionhandler.DataSyncJobException;
import com.thomsonreuters.dataconnect.executionengine.model.entity.JobExecutionLog;
import com.thomsonreuters.dataconnect.executionengine.repository.JobExecutionLogRepository;
import com.thomsonreuters.dataconnect.executionengine.transformation.TransformationEngine;
import com.thomsonreuters.dataconnect.executionengine.datasyncactivity.DataSyncActivityEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.thomsonreuters.dataconnect.executionengine.constant.Constants.COMPLETED;
import static com.thomsonreuters.dataconnect.executionengine.constant.Constants.IN_PROGRESS;

@Service
@Slf4j
public class DataSyncExecutionEngine {

    @Autowired
    private AdapterFactory adapterFactory;

    @Autowired
    private LogClient logClient;

    @Autowired
    private JobExecutionLogRepository jobExecutionLogRepository;

    @Autowired
    private DataSyncActivityEngine dataSyncActivityEngine;


    public void executeTask(ExecutionContext ctx) throws DataSyncJobException {
        updateExecutionStatus(ctx, IN_PROGRESS);

        IntegrationAdapter inAdaptor = adapterFactory.getAdapter(ctx, ExecutionContext.IN_ADAPTER_CONTEXT);
        String inAdaptorId = inAdaptor.getClass().getSimpleName();
        log.info("Input Adapter: {}", inAdaptorId);
        TransformationEngine engine = new TransformationEngine();
        engine.initialize(ctx.getContextByName(ExecutionContext.TRANSFORMATION_CONTEXT).getValue(TransformationContext.TRANSFORMATION_CONTEXT));
        IntegrationAdapter outAdaptor = adapterFactory.getAdapter(ctx, ExecutionContext.OUT_ADAPTER_CONTEXT);
        String outAdaptorId = outAdaptor.getClass().getSimpleName();
        log.info("Output Adapter: {}", outAdaptorId);

        DataSetCollection dataSetCollection = inAdaptor.readData(ctx);
        log.info("DataSetCollection read from input adapter: {}", inAdaptorId);
        DataSetCollection transformedDataSetCollection = engine.transform(dataSetCollection, ctx);

        outAdaptor.writeData(transformedDataSetCollection, ctx);

        //Transit hub activity engine
        if(outAdaptor instanceof DatabaseAdapter dbAdapter) {
            List<DataSetCollection> dataSetCollectionList=dbAdapter.getTransitHubDataSetCollection();
            for(DataSetCollection dataSetCollections:dataSetCollectionList){
                executeDataSyncActivities(dataSetCollections, ctx);
                log.info("Transit hub DataSetCollection written to output adapter: {}", outAdaptorId);
            }

        }



        log.info("DataSetCollection written to output adapter: {}", outAdaptorId);
        //TODO: Need to check if we need to clean up inAdaptor and transformationEngine
        //outAdaptor.cleanUp();
        //TransformationEngine.cleanup();
        //inAdaptor.cleanUp();
        updateExecutionStatus(ctx, COMPLETED);

    }

    public void updateExecutionStatus(ExecutionContext ctx, String status) throws DataSyncJobException {

        RegionalJobContext regionalJobContext = (RegionalJobContext) ctx.getContextByName(ExecutionContext.REGIONAL_JOB_CONTEXT);
        UUID regionalJobExecId = regionalJobContext.getValue(RegionalJobContext.REGIONAL_JOB_EXEC_ID);
        if (status.equals(IN_PROGRESS)) {
            updateJobExecutionLog(regionalJobExecId, status);
           // logClient.beginJobExecution(ctx, regionalJobExecId);
        } else if (status.equals(COMPLETED)) {
           updateJobExecutionLog(regionalJobExecId, status);
          //  logClient.jobCompletion(ctx, regionalJobExecId);
        } else {
            throw new DataSyncJobException("Job Status update failed " + status, "INTERNAL_SERVER_ERROR");
        }
    }

    public JobExecutionLog saveJobExecutionLog(UUID jobId,UUID jobExecId, String status) {
        JobExecutionLog jobExecutionLog = new JobExecutionLog();
        jobExecutionLog.setJobId(jobId);
        jobExecutionLog.setJobExecutionId(jobExecId);
        jobExecutionLog.setStatus(status);
        jobExecutionLog.setCreatedBy("system");
        jobExecutionLog.setWhenAccepted(LocalDateTime.now());
        return jobExecutionLogRepository.save(jobExecutionLog);
    }

    public void updateJobExecutionLog(UUID regionalExecId, String status) {
        Optional<JobExecutionLog> jobExecutionLog = jobExecutionLogRepository.findById(regionalExecId);
        if (jobExecutionLog.isPresent()) {
            JobExecutionLog jobExecLog = jobExecutionLog.get();
            jobExecLog.setStatus(status);
            jobExecLog.setWhenCompleted(LocalDateTime.now());
            jobExecutionLogRepository.save(jobExecLog);
        }
    }

    public void executeDataSyncActivities(DataSetCollection transformedDataSetCollection, ExecutionContext ctx) throws DataSyncJobException {
        try {
            for (DataSet dataSet : transformedDataSetCollection.getDataSets()) {
                if (dataSet.getMetaObject() !=null && dataSet.getMetaObject().isEventEnabled() ) {
                    dataSyncActivityEngine.initialize(ctx);
                    dataSyncActivityEngine.execute(transformedDataSetCollection,ctx);
                }
            }
        }catch (Exception e) {
            log.error("Error during activity engine execution: {}", e.getMessage());
            throw new DataSyncJobException("Activity Engine execution failed", "INTERNAL_SERVER_ERROR");
        }
    }


}
