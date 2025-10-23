package com.aditya.dataconnect.executionengine.utils;

import com.aditya.dataconnect.executionengine.model.entity.*;
import com.aditya.dataconnect.executionengine.repository.*;
import com.thomsonreuters.dataconnect.common.exception.ExecutionContextException;
import com.thomsonreuters.dataconnect.common.executioncontext.*;
import com.aditya.dataconnect.executionengine.adapters.impl.DataStreamAdapter;
import com.aditya.dataconnect.executionengine.configuration.TransitHubConfiguration;
import com.aditya.dataconnect.executionengine.exceptionhandler.DataSyncJobException;
import com.thomsonreuters.dataconnect.executionengine.model.entity.*;
import com.aditya.dataconnect.executionengine.model.entity.enums.ErrorConstant;
import com.aditya.dataconnect.executionengine.model.entity.enums.ExecutionLeg;
import com.aditya.dataconnect.executionengine.model.pojo.DataUnit;
import com.aditya.dataconnect.executionengine.model.pojo.DatasyncMessage;
import com.aditya.dataconnect.executionengine.model.pojo.Header;
import com.thomsonreuters.dataconnect.executionengine.repository.*;
import com.aditya.dataconnect.executionengine.services.awsservices.AwsSsmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static com.aditya.dataconnect.executionengine.constant.Constants.ACCEPTED_AT;
import static com.aditya.dataconnect.executionengine.constant.Constants.EXEC_TYPE;

@Component
@Slf4j
public class ExecutionContextUtil {

    private final JobExecutionLogRepository jobExecutionLogRepository;

    private final RegionalJobConfigRepository regionalJobConfigRepository;

    private final GlobalContext globalContext;

    private final DataSyncActivityRepository dataSyncActivityRepository;

    private final DatasyncJobConfigRepository datasyncJobConfigRepository;

    private final MetaObjectRepository metaObjectRepository;

    private final TransitHubConfigRepository transitHubConfigRepository;

    private final TransitHubConfiguration transitHubConfiguration;

    //TODO: Move schema id to DataSyncActivityContext / DataSyncActivityConfig table once the placing of schema id is decided
    private static final String CHART_OF_ACCOUNTS_SCHEMA_ID = "chart-of-accounts-created-2023-Feb-20";
    private static final String EXEC_LEG_TARGET = "TARGET";

    @Autowired
    private AwsSsmService awsSsmService;

    @Autowired
    public ExecutionContextUtil(JobExecutionLogRepository jobExecutionLogRepository, RegionalJobConfigRepository regionalJobConfigRepository, GlobalContext globalContext, DataSyncActivityRepository dataSyncActivityRepository, DatasyncJobConfigRepository datasyncJobConfigRepository, MetaObjectRepository metaObjectRepository, TransitHubConfigRepository transitHubConfigRepository, TransitHubConfiguration transitHubConfiguration) {
        this.jobExecutionLogRepository = jobExecutionLogRepository;
        this.regionalJobConfigRepository = regionalJobConfigRepository;
        this.globalContext = globalContext;
        this.dataSyncActivityRepository = dataSyncActivityRepository;
        this.datasyncJobConfigRepository = datasyncJobConfigRepository;
        this.metaObjectRepository = metaObjectRepository;
        this.transitHubConfigRepository = transitHubConfigRepository;
        this.transitHubConfiguration = transitHubConfiguration;
    }

    public ExecutionContext buildExecutionContext(DatasyncMessage datasyncMessage) throws DataSyncJobException, ExecutionContextException {
        ExecutionContext executionContext = new ExecutionContext();
        Header header = datasyncMessage.getHeader();
        validateHeader(header);
        DataUnit dataUnit = datasyncMessage.getData();
        //TODO: update JobName to DataSyncJobName once JobName column is renamed
        String region = globalContext.getValue(GlobalContext.HOST_REGION);
        log.info("Inside Region: {}", region);
         log.info("HEADER JobName - {}",header.getJobName());
        RegionalJobConfiguration regionalJobConfig= null;
         //TODO : get executionlec is source or target based on that get source region or target region
        if(ExecutionLeg.SOURCE.name().equals(header.getExecLeg().toString())) {
            log.info("Fetching source region config for job: {} in region: {}", header.getJobName(), region);
            log.info("Fetching source region config for execution leg {}", header.getExecLeg());
            regionalJobConfig = regionalJobConfigRepository.findByDatasyncJobSysNameAndSourceRegionAndExecLegAndIsActive(header.getJobName(), region, header.getExecLeg(), true)
                    .orElseThrow(() -> new DataSyncJobException(ErrorConstant.JOB_NOT_FOUND.getMessage(), ErrorConstant.JOB_NOT_FOUND.getCode()));
        }else if((ExecutionLeg.TARGET.name().equals(header.getExecLeg().toString()))){
            regionalJobConfig = regionalJobConfigRepository.findByDatasyncJobSysNameAndTargetRegionAndExecLegAndIsActive(header.getJobName(), region,header.getExecLeg(), true)
                    .orElseThrow(() -> new DataSyncJobException(ErrorConstant.JOB_NOT_FOUND.getMessage(), ErrorConstant.JOB_NOT_FOUND.getCode()));
        }else {
             log.error("Invalid exec leg value: {}", header.getExecLeg());
             throw new DataSyncJobException(ErrorConstant.INVALID_EXEC_LEG.getMessage(), ErrorConstant.INVALID_EXEC_LEG.getCode());
        }
        //TODO: fetch job execution Log based on job execution id and regional job exection id once regional job exection id is implemented
        log.info("got host region configerations");
        Optional<JobExecutionLog> jobExecutionLogOpt = jobExecutionLogRepository.findById(header.getRegionalExecId());
        JobExecutionLog jobExecutionLog = jobExecutionLogOpt.orElse(null);
        executionContext.setContextByName(ExecutionContext.REGIONAL_JOB_CONTEXT,
                getRegionalJobContext(regionalJobConfig, jobExecutionLog, header));
        log.info("got host regional configerations");
        executionContext.setContextByName(ExecutionContext.GLOBAL_CONTEXT, globalContext);
        log.info("got host global configerations");

        AdapterContext inputAdapterContext =getAdaptorContext(regionalJobConfig.getInAdaptorId());
        log.info("got input adapter configurations");
        //TODO: Add DATA_UNIT to AdapterContext of DC Common Lib and change DataStreamAdapter.DATA_UNIT to AdapterContext.DATA_UNIT
        inputAdapterContext.setValue(DataStreamAdapter.DATA_UNIT, dataUnit);
        executionContext.setContextByName(ExecutionContext.IN_ADAPTER_CONTEXT, inputAdapterContext);
        log.info("got output adapter configurations");
        executionContext.setContextByName(ExecutionContext.OUT_ADAPTER_CONTEXT, getAdaptorContext(regionalJobConfig.getOutAdaptorId()));
        executionContext.setContextByName(ExecutionContext.TRANSFORMATION_CONTEXT, getRegionalTransformationContext(regionalJobConfig));
        log.info("got transformation configurations");
        executionContext.setContextByName(ExecutionContext.DATA_SYNC_ACTIVITY_CONTEXT,getDataSyncActivityContext(header,regionalJobConfig));
        log.info("Received data sync activity configurations");
        log.info("ExecutionContext built successfully for job: {}", header.getJobName());
        return executionContext;
    }

    private DataSyncActivityContext getDataSyncActivityContext(Header header,RegionalJobConfiguration regionConfig) throws DataSyncJobException {
        Optional<DatasyncJobConfiguration> datasyncJobConfiguration=datasyncJobConfigRepository.findById(header.getJobId());
        if(datasyncJobConfiguration.isEmpty()) {
            log.error("DataSyncJobConfiguration not found for jobId: {}", header.getJobId());
            throw new DataSyncJobException(ErrorConstant.JOB_NOT_FOUND.getMessage(), ErrorConstant.JOB_NOT_FOUND.getCode());
        }
        MetaObject metaObject=metaObjectRepository.findMetaObjectBySystemName(datasyncJobConfiguration.get().getMetaObjectSysName());
        if(metaObject == null) {
            log.error("MetaObject not found for id: {}", datasyncJobConfiguration.get().getMetaObjectSysName());
            throw new DataSyncJobException("Meta_object not found", "NOT_FOUND");
        }
        TransitHubConfig transitHubConfig=transitHubConfigRepository.findByMetaObjectSysName(metaObject.getSystemName())
                .orElseGet(() -> {
                    log.warn("TransitHubConfig not set for this meta object assuming disabled: {}", metaObject.getSystemName());
                    return null; // Ensures compatibility with the method signature
                });
        DataSyncActivityContext dataSyncActivityContext = new DataSyncActivityContext();
        TransitHubContext transitHubContext = new TransitHubContext();
        List<DataSyncActivityConfig> dataSyncActivityConfig= new ArrayList<>();
        if(regionConfig.getDatasyncJobSysName().endsWith(".rt")) {
            dataSyncActivityConfig = dataSyncActivityRepository.findByDatasyncJobSysNameAndExecType(datasyncJobConfiguration.get().getSystemName(), String.valueOf(regionConfig.getExecLeg()));
        }
            dataSyncActivityContext.setAttributes(new HashMap<>());
            dataSyncActivityContext.setValue(DataSyncActivityContext.DATA_SYNC_ACTIVITY_CONTEXT, dataSyncActivityConfig);
            transitHubContext.setAttributes(new HashMap<>());

        if(transitHubConfig!=null) {
            String ssmPrivateKey = null;
            String ssmSubKey = null;
            try {
                ssmPrivateKey = awsSsmService.getSSMValue(transitHubConfig.getPrivateKey());
                ssmSubKey = awsSsmService.getSSMValue(transitHubConfig.getSubscriptionKey());
            } catch (Exception e) {
                log.error("Failed to fetch SSM parameter(s): privateKey={}, subscriptionKey={}", transitHubConfig.getPrivateKey(), transitHubConfig.getSubscriptionKey(), e);
            }
            transitHubContext.setValue(TransitHubContext.SUBSCRIPTION_KEY, ssmSubKey);
            transitHubContext.setValue(TransitHubContext.ISSUER_URL, transitHubConfig.getIssuer());
            transitHubContext.setValue(TransitHubContext.PUBLISHER_ID, transitHubConfig.getPublisherId());
            transitHubContext.setValue(TransitHubContext.SCHEMA_ID_CREATE, CHART_OF_ACCOUNTS_SCHEMA_ID);
            transitHubContext.setValue(TransitHubContext.PRIVATE_KEY, ssmPrivateKey);
            transitHubContext.setValue(TransitHubContext.SERVICE_URL, transitHubConfiguration.getServiceUrl());
        }
        globalContext.setValue(GlobalContext.TRANSITHUB_CONTEXT, transitHubContext);

        return dataSyncActivityContext;
    }

    private RegionalJobContext getRegionalJobContext(

            RegionalJobConfiguration regionalJobConfig,
            JobExecutionLog jobExecutionLog,
            Header header
    ) throws DataSyncJobException {
        // Build the regional job context with the necessary information
        RegionalJobContext regionalJobContext = new RegionalJobContext();
        regionalJobContext.setAttributes(new HashMap<>());

        // DatasyncJobConfiguration values
        regionalJobContext.setValue(RegionalJobContext.JOB_ID, regionalJobConfig.getDatasyncJobId());
        regionalJobContext.setValue(RegionalJobContext.JOB_NAME, regionalJobConfig.getDatasyncJobSysName());
        // JobExecutionLog values
        if (jobExecutionLog != null) {
            regionalJobContext.setValue(RegionalJobContext.EXEC_ID, jobExecutionLog.getJobExecutionId());
            //TODO: Once regional job execution id is implemented, set the regional job execution id
            regionalJobContext.setValue(RegionalJobContext.REGIONAL_JOB_EXEC_ID, jobExecutionLog.getId());
            regionalJobContext.setValue(RegionalJobContext.REGIONAL_JOB_EXEC_STATUS, jobExecutionLog.getStatus());
            regionalJobContext.setValue(RegionalJobContext.LAST_COMPLETED_AT, jobExecutionLog.getWhenCompleted());
            regionalJobContext.setValue((ACCEPTED_AT), jobExecutionLog.getWhenAccepted());

        }
        regionalJobContext.setValue(RegionalJobContext.REGIONAL_JOB_NAME, regionalJobConfig.getDatasyncJobSysName());
        // Source or Target info
        regionalJobContext.setValue(RegionalJobContext.EXEC_LEG, regionalJobConfig.getExecLeg());
        regionalJobContext.setValue(RegionalJobContext.CUSTOMER_TENANT_ID, regionalJobConfig.getCustomerTenantSysName());
        regionalJobContext.setValue(RegionalJobContext.SOURCE_REGION, regionalJobConfig.getSourceRegion());
        regionalJobContext.setValue(RegionalJobContext.TARGET_REGIONS, regionalJobConfig.getTargetRegion());
        regionalJobContext.setValue(RegionalJobContext.CLIENT_ID, regionalJobConfig.getClientId());
        // RegionalJobConfiguration values
        regionalJobContext.setValue(RegionalJobContext.ONESOURCE_DOMAIN, regionalJobConfig.getOnesourceDomain());
        regionalJobContext.setValue(RegionalJobContext.META_OBJECT_SYS_NAME, header.getMetaObjectSysName());
        if (regionalJobConfig.getExecLeg() == ExecutionLeg.TARGET) {
            regionalJobContext.setValue(
                    RegionalJobContext.REGIONAL_TENANT_ID,
                    regionalJobConfig.getTargetTenantId()
            );
        } else if (regionalJobConfig.getExecLeg() == ExecutionLeg.SOURCE) {
            regionalJobContext.setValue(
                    RegionalJobContext.REGIONAL_TENANT_ID,
                    regionalJobConfig.getSourceTenantId()
            );
        } else {
            log.error("Invalid RegionType value: {}", regionalJobConfig.getExecLeg());
        }
        regionalJobContext.setValue(EXEC_TYPE, header.getExecType());
        return regionalJobContext;
    }

    private ExecutionContextBase getRegionalTransformationContext(RegionalJobConfiguration regionConfig) {
        TransformationContext transformationContext = new TransformationContext();
        transformationContext.setAttributes(new HashMap<>());
        transformationContext.setValue(TransformationContext.TRANSFORMATION_CONTEXT, regionConfig.getTransformContext());
        return transformationContext;
    }

    private AdapterContext getAdaptorContext(String adapterId) {
        AdapterContext adapterContext = new AdapterContext();
        adapterContext.setAttributes(new HashMap<>());
        adapterContext.setValue(AdapterContext.ADOPTER_ID, adapterId);
        return adapterContext;

    }

    public void validateHeader(Header header) throws DataSyncJobException {
        if (header == null || header.getJobExecId() == null) {
            throw new DataSyncJobException(ErrorConstant.JOB_ID_ERROR.getMessage(), ErrorConstant.JOB_ID_ERROR.getCode());
        }
    }

}
