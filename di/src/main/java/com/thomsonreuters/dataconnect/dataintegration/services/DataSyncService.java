package com.thomsonreuters.dataconnect.dataintegration.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomsonreuters.dataconnect.common.logging.LogClient;
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
import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.ExecutionLeg;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.JobStatus;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.OperationType;
import com.thomsonreuters.dataconnect.dataintegration.model.pojo.*;
import com.thomsonreuters.dataconnect.dataintegration.repository.DatasyncJobConfigRepository;
import com.thomsonreuters.dataconnect.dataintegration.repository.JobExecutionLogRepository;
import com.thomsonreuters.dataconnect.dataintegration.repository.MetaObjectRepository;
import com.thomsonreuters.dataconnect.dataintegration.repository.MetaObjectRelationRepository;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.MetaObjectRelation;
import com.thomsonreuters.dataconnect.dataintegration.utils.EnumUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class DataSyncService {

    private final DatasyncJobConfigRepository datasyncJobConfigRepository;

    private final JobExecutionLogRepository jobExecutionLogRepository;

    private final MetaObjectRepository metaObjectRepository;

    private final RabbitTemplate rabbitTemplate;

    private final String rabbitmqExchange;

    private final RabbitMQConfig rabbitMQConfig;

    private final String sourceSenderRoutingKey;


    @Autowired
    private LogClient dcLogClient;

    private final RegionConfig regionConfig;

    private final ObjectMapper objectMapper;

    private final RestTemplate restTemplate;

    LocalDateTime localDateTime = null;

    private final DataIntegrationRegionConfig dataIntegrationRegionConfig;

    private final ModelMapperConfig modelMapperConfig;
    private final MetaObjectRelationRepository metaObjectRelationRepository;


    @Autowired
    public DataSyncService(DatasyncJobConfigRepository datasyncJobConfigRepository, JobExecutionLogRepository jobExecutionLogRepository, MetaObjectRepository metaObjectRepository, @Qualifier("dataSyncRabbitTemplate") RabbitTemplate rabbitTemplate, RabbitMQConfig rabbitMQConfig,RegionConfig regionConfig,ObjectMapper objectMapper, RestTemplate restTemplate,DataIntegrationRegionConfig dataIntegrationRegionConfig, ModelMapperConfig modelMapperConfig, MetaObjectRelationRepository metaObjectRelationRepository) {
        this.datasyncJobConfigRepository = datasyncJobConfigRepository;
        this.jobExecutionLogRepository = jobExecutionLogRepository;
        this.metaObjectRepository = metaObjectRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitMQConfig = rabbitMQConfig;
        this.rabbitmqExchange = rabbitMQConfig.getSourceListenerExchange();
        this.sourceSenderRoutingKey = rabbitMQConfig.getSourceSenderRoutingKey();
        this.regionConfig = regionConfig;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
        this.dataIntegrationRegionConfig = dataIntegrationRegionConfig;
        this.modelMapperConfig = modelMapperConfig;
        this.metaObjectRelationRepository = metaObjectRelationRepository;
    }


    public String publishDataSyncJob(RequestModel requestModel) throws DataSyncJobException {
        if (requestModel == null) {
            throw new DataSyncJobException("RequestModel cannot be null", Constants.BAD_REQUEST);
        }
        if (requestModel.getOperationType() == null) {
            throw new DataSyncJobException("OperationType cannot be null", Constants.BAD_REQUEST);
        }
        if (requestModel.getExecType() == null) {
            throw new DataSyncJobException("ExecType cannot be null", Constants.BAD_REQUEST);
        }

        // Preserve original meta object identifiers from request (even if we fall back to an ancestor job)
        String originalMetaObjectId = requestModel.getMetaObjectId();
        String originalMetaObjectName = requestModel.getMetaObjectName();

        OptionalRequestBody optionalRequestBody = new OptionalRequestBody();
        optionalRequestBody.setRequestDataUnitList(requestModel.getRequestDataUnitList());

        DatasyncJobConfiguration jobConfiguration = null;

        // Preferred path: use provided meta_object_id directly (if valid UUID)
        UUID providedMetaId = null;
        String providedMetaObjectName = null;
        if (requestModel.getMetaObjectName() != null && !requestModel.getMetaObjectName().isBlank()) {
            try {
                providedMetaObjectName = requestModel.getMetaObjectName();
            } catch (IllegalArgumentException ex) {
                // Invalid UUID -> fall back to legacy name/domain resolution
                providedMetaId = null;
            }
        }

        if (providedMetaObjectName != null && !providedMetaObjectName.isBlank()) {
            // Iterative ancestor fallback:
            // Try provided ID; if no job, walk up parent chain checking each ancestor
            jobConfiguration = findJobWithAncestorFallback(providedMetaId, providedMetaObjectName, requestModel);
        } else {
            throw new DataSyncJobException("meta_object_name must be provided", Constants.BAD_REQUEST);
        }

        UUID jobId = jobConfiguration.getId();
        return publishDataSyncJob(jobId, optionalRequestBody, requestModel.getOperationType().toString(), originalMetaObjectId, originalMetaObjectName);
    }

    public String publishDataSyncJob(UUID jobId, OptionalRequestBody optionalRequestBody, String operationType, String originalMetaObjectId, String originalMetaObjectName) throws DataSyncJobException {
        if (operationType == null || operationType.trim().isEmpty() || !isValidOperationType(operationType)) {
            throw new DataSyncJobException(Constants.INVALID_OPERATION_TYPE, Constants.BAD_REQUEST);
        }
        Optional<DatasyncJobConfiguration> datasyncJobConfigurationOpt = datasyncJobConfigRepository.findDatasyncJobConfigurationById(jobId);

        if (datasyncJobConfigurationOpt.isEmpty()) {
            throw new DataSyncJobException(Constants.DATASYNC_JOB_NOT_FOUND, Constants.NOT_FOUND);
        } else {
            // check if job is already running to prevent duplicate execution
            DatasyncJobConfiguration jobConfiguration = datasyncJobConfigurationOpt.get();

            Optional<MetaObject> metaObject = metaObjectRepository.findBySystemName(jobConfiguration.getMetaObjectSysName());
            if(metaObject.isEmpty()) {
                throw new DataSyncJobException(Constants.METAOBJECT_JOB_NOT_FOUND, Constants.NOT_FOUND);
            }

            // Perform validation for object ids if the sync is real time
            String execType = jobConfiguration.getExecType();
            if (ExecType.REAL_TIME.name().equalsIgnoreCase(execType)) {
                validateObjectIds(optionalRequestBody);
            } else if (ExecType.BATCH.name().equalsIgnoreCase(execType)) {
                // For batch jobs, request body must be null or empty
                if (optionalRequestBody != null && optionalRequestBody.getRequestDataUnitList() != null
                        && optionalRequestBody.getRequestDataUnitList().getObjIds() != null
                        && !optionalRequestBody.getRequestDataUnitList().getObjIds().isEmpty()) {
                    throw new DataSyncJobException(Constants.OBJECT_IDS_SHOULD_BE_EMPTY_FOR_BATCH_SYNC, Constants.BAD_REQUEST);
                }
            }

            // Persist the job execution log in the source region
            JobExecutionLog jobExecutionLogEntry = saveJobExecutionLog(jobId);
            DatasyncMessage datasyncMessage = createDataSyncMessage(jobConfiguration, jobExecutionLogEntry, optionalRequestBody, originalMetaObjectId, originalMetaObjectName);
            //publish message to rabbitmq
            rabbitTemplate.convertAndSend(rabbitmqExchange,sourceSenderRoutingKey, datasyncMessage);
            return jobExecutionLogEntry.getId().toString();
        }
    }

    private static void validateObjectIds(OptionalRequestBody optionalRequestBody) throws DataSyncJobException {
        if (optionalRequestBody == null
                || optionalRequestBody.getRequestDataUnitList() == null
                || optionalRequestBody.getRequestDataUnitList().getObjIds() == null
                || optionalRequestBody.getRequestDataUnitList().getObjIds().isEmpty()) {
            throw new DataSyncJobException(Constants.OBJECT_ID_REQUIRED, Constants.BAD_REQUEST);
        }
        List<?> objIds = optionalRequestBody.getRequestDataUnitList().getObjIds();

        Object firstObj = objIds.get(0);
        // Single PK: List of IDs (not a map)
        if (!(firstObj instanceof Map<?,?>)) {
            for (Object id : objIds) {
                if (id == null || (id instanceof String && ((String) id).isEmpty())) {
                    throw new DataSyncJobException(Constants.OBJECT_ID_REQUIRED, Constants.BAD_REQUEST);
                }
            }
            return;
        }

        // Composite PK: List of Maps
        // Determine if column-oriented (each map has single key) or row-oriented (each map has multiple keys)
        boolean columnOriented = objIds.stream().allMatch(obj -> obj instanceof Map && ((Map<?, ?>) obj).size() == 1);
        if (columnOriented) {
            if (objIds.size() < 2) {
                throw new DataSyncJobException(Constants.ATLEAST_TWO_COLUMN_REQUIRED, Constants.BAD_REQUEST);
            }

            // Check for duplicate keys in column-oriented maps
            Set<Object> keys = new HashSet<>();
            for (Object obj : objIds) {
                Map<?, ?> map = (Map<?, ?>) obj;
                Object key = map.keySet().iterator().next();
                if (!keys.add(key)) {
                    throw new DataSyncJobException(Constants.DUPLICATE_KEY_FOUND + key, Constants.BAD_REQUEST);
                }
            }

            Integer expectedSize = null;
            for (Object obj : objIds) {
                Map<?, ?> map = (Map<?, ?>) obj;
                Object value = map.values().iterator().next();
                int size = (value instanceof List) ? ((List<?>) value).size() : (value instanceof Object[]) ? ((Object[]) value).length : -1;
                if (size < 1) {
                    throw new DataSyncJobException(Constants.OBJECT_ID_REQUIRED, Constants.BAD_REQUEST);
                }
                if (expectedSize == null) {
                    expectedSize = size;
                } else if (!expectedSize.equals(size)) {
                    throw new DataSyncJobException(Constants.ALL_COLUMNS_MUST_HAVE_SAME_NUMBER_OF_IDS, Constants.BAD_REQUEST);
                }
            }
        } else {
            // Row-oriented: each map has multiple keys
            throw new DataSyncJobException(Constants.MAP_WITH_SINGLE_KEY_REQUIRED, Constants.BAD_REQUEST);
        }
    }

    // Backward compatibility overload for existing controller calls (without original meta object fields)
    /**
     * Deprecated overload kept for backward compatibility with existing callers that do not
     * pass original meta object identifiers. Prefer
     * {@link #publishDataSyncJob(UUID, OptionalRequestBody, String, String, String)}
     * which preserves the originating meta object id and name for downstream auditing.
     *
     * Will be removed after all callers are migrated.
     * @deprecated Use the 5-parameter variant with original meta object identifiers.
     */
    @Deprecated
    public String publishDataSyncJob(UUID jobId, OptionalRequestBody optionalRequestBody, String operationType) throws DataSyncJobException {
        return publishDataSyncJob(jobId, optionalRequestBody, operationType, null, null);
    }

    public JobExecutionLog saveJobExecutionLog(UUID jobId) {
        JobExecutionLog jobExecutionLog = new JobExecutionLog();
        jobExecutionLog.setJobId(jobId);
        jobExecutionLog.setStatus(String.valueOf(JobStatus.ACCEPTED));
        jobExecutionLog.setCreatedBy("system");
        jobExecutionLog.setWhenAccepted(LocalDateTime.now());
        return jobExecutionLogRepository.save(jobExecutionLog);
    }

    private DatasyncMessage createDataSyncMessage(DatasyncJobConfiguration jobConfiguration, JobExecutionLog jobExecutionLog, OptionalRequestBody optionalRequestBody, String originalMetaObjectId, String originalMetaObjectName) {
        DatasyncMessage datasyncMessage = new DatasyncMessage();
        DataUnit dataUnit1 = new DataUnit();
        Header header = new Header();
        header.setJobId(jobExecutionLog.getJobId());
        header.setRegionalExecId(jobExecutionLog.getId());
        header.setJobName(jobConfiguration.getSystemName());
        header.setJobExecId(jobExecutionLog.getJobExecutionId());
        header.setSource(jobConfiguration.getSourceRegion());
        header.setMetaObjectSysName(originalMetaObjectName);
        header.setExecLeg(ExecutionLeg.SOURCE);
        header.setExecType(jobConfiguration.getExecType());
        header.setMetaObjectId(originalMetaObjectId);
        if (optionalRequestBody.getRequestDataUnitList() != null) {
            dataUnit1.setContent(new DataUnitList(optionalRequestBody.getRequestDataUnitList().getObjIds()));
        }
        datasyncMessage.setHeader(header);
        datasyncMessage.setData(dataUnit1);
        return datasyncMessage;
    }

    public boolean isValidOperationType(String operationType) {
        return EnumUtils.isValidEnum(OperationType.class, operationType);
    }

    /**
     * Walk upward through parent relations until a job configuration is found.
     * Checks each level (meta object itself, parent, grandparent, etc.).
     * Throws NOT_FOUND if no ancestor in the chain has a job.
     */
    private DatasyncJobConfiguration findJobWithAncestorFallback(UUID startingMetaId, String metaObjectName, RequestModel requestModel) throws DataSyncJobException {
        String currentMetaObjectName = metaObjectName;
        MetaObject metaobj = metaObjectRepository.findBySystemName(currentMetaObjectName).orElseThrow(
                () -> new DataSyncJobException("Meta object not found for name: " + metaObjectName, Constants.NOT_FOUND)
        );
        UUID current = metaobj.getId();
        while (true) {
            Optional<DatasyncJobConfiguration> jobOpt = findDataSyncJob(requestModel, currentMetaObjectName);

            if (jobOpt.isPresent()) {
                return jobOpt.get();
            }
            Optional<MetaObjectRelation> rel = metaObjectRelationRepository.findByChildObject_Id(current);
            if (rel.isEmpty()) {
                // No parent and no job at this level
                throw new DataSyncJobException("Job details not found for meta object ID (including parents): " + startingMetaId, Constants.NOT_FOUND);
            }
            MetaObjectRelation relation = rel.get();
            if (relation.getParentObject() == null || relation.getParentObject().getId() == null) {
                throw new DataSyncJobException("Job details not found for meta object ID (including parents): " + startingMetaId, Constants.NOT_FOUND);
            }
            current = relation.getParentObject().getId();
            Optional<MetaObject> parentMeta = metaObjectRepository.findById(current);
            if (parentMeta.isEmpty()) {
                throw new DataSyncJobException("Job details not found for meta object ID (including parents): " + startingMetaId, Constants.NOT_FOUND);
            }
            currentMetaObjectName = parentMeta.get().getSystemName();
        }
    }

    public void checkIfJobIsRunning(UUID jobId) throws DataSyncJobException {
        Optional<JobExecutionLog> latestJobExecutionLog = jobExecutionLogRepository.findTopByJobIdOrderByWhenAcceptedDesc(jobId);
        if (latestJobExecutionLog.isPresent()) {
            String status = latestJobExecutionLog.get().getStatus();
            if (JobStatus.ACCEPTED.name().equals(status) || JobStatus.IN_PROGRESS.name().equals(status)) {
                throw new DataSyncJobException(String.format(Constants.DUPLICATE_JOB_RUNNING, jobId, status), Constants.BAD_REQUEST);
            }
        }
    }


    public boolean validateRequestModelAndJob(RequestModel requestModel, DatasyncJobConfiguration datasyncJobConfiguration) throws DataSyncJobException {
        if (requestModel == null || requestModel.getMetaObjectName() == null
                || requestModel.getExecType() == null) {
            return false;
        }
        
        try {
            // Get MetaObject by system name to obtain UUID for findJobWithAncestorFallback
            Optional<MetaObject> metaObjectOpt = metaObjectRepository.findBySystemName(requestModel.getMetaObjectName());
            if (metaObjectOpt.isEmpty()) {
                return false;
            }
            
            UUID metaObjectId = metaObjectOpt.get().getId();
            DatasyncJobConfiguration jobConfig = findJobWithAncestorFallback(metaObjectId, requestModel.getMetaObjectName(), requestModel);
            return jobConfig.getId() != null;
        } catch (DataSyncJobException e) {
            // If findJobWithAncestorFallback throws exception (job not found), return false to maintain existing behavior
            return false;
        }
    }

    public String childDataSyncJob(ChildRequestModel requestModel) throws DataSyncJobException {
        return "Success";
    }

    public Optional<DatasyncJobConfiguration> findDataSyncJob(RequestModel requestModel, String metaObjectName) {
        if (StringUtils.isNotBlank(requestModel.getCustomerTenantSysName())
                && StringUtils.isNotBlank(requestModel.getSourceTenantId())) {
            return datasyncJobConfigRepository.findByCustomerTenantSysNameAndSourceTenantIdAndMetaObjectSysNameAndExecType(
                    requestModel.getCustomerTenantSysName(),
                    requestModel.getSourceTenantId(),
                    metaObjectName,
                    requestModel.getExecType().name());
        } else if (StringUtils.isNotBlank(requestModel.getCustomerTenantSysName())) {
            return datasyncJobConfigRepository.findByCustomerTenantSysNameAndMetaObjectSysNameAndExecType(
                    requestModel.getCustomerTenantSysName(),
                    metaObjectName,
                    requestModel.getExecType().name());
        } else if (StringUtils.isNotBlank(requestModel.getSourceTenantId())) {
            return datasyncJobConfigRepository.findBySourceTenantIdAndMetaObjectSysNameAndExecType(
                    requestModel.getSourceTenantId(),
                    metaObjectName,
                    requestModel.getExecType().name());
        } else {
            return datasyncJobConfigRepository.findByMetaObjectSysNameAndExecType(metaObjectName,
                    requestModel.getExecType().name());
        }
    }
}
