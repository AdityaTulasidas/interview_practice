package com.thomsonreuters.dataconnect.dataintegration.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomsonreuters.dataconnect.dataintegration.configuration.ModelMapperConfig;
import com.thomsonreuters.dataconnect.dataintegration.constant.Constants;
import com.thomsonreuters.dataconnect.dataintegration.dto.*;
import com.thomsonreuters.dataconnect.dataintegration.exceptionhandler.DataSyncJobException;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.*;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.*;
import com.thomsonreuters.dataconnect.dataintegration.model.pojo.RequestBodyDataUnitList;
import com.thomsonreuters.dataconnect.dataintegration.model.pojo.RequestModel;
import com.thomsonreuters.dataconnect.dataintegration.repository.*;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.DataSyncActivityConfig;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.TargetRegion;
import com.thomsonreuters.dataconnect.dataintegration.utils.EnumUtils;
import com.thomsonreuters.dep.api.spring.ApiCriteria;
import com.thomsonreuters.dep.api.spring.ApiSupport;
import com.thomsonreuters.dep.api.spring.response.ApiCollection;
import com.thomsonreuters.dep.api.spring.response.ApiCollectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.thomsonreuters.dataconnect.dataintegration.model.pojo.OAuthTokenResponse;
import com.thomsonreuters.dataconnect.dataintegration.model.pojo.uge.CustomerTenant;
import com.thomsonreuters.dataconnect.dataintegration.model.pojo.uge.RegionalTenant;

import static com.thomsonreuters.dataconnect.dataintegration.constant.Constants.*;

@Service
@Slf4j
public class DataSyncJobConfigService {

    private final RestTemplate restTemplate;

    private final DatasyncJobConfigRepository datasyncJobConfigRepository;

    private final ModelMapperConfig modelMapperConfig;

    private final MetaObjectRepository metaObjectRepository;

    private final RegionalJobConfigRepository regionalJobConfigRepository;

    private final ApiSupport apiSupport;

    private final ApiCollectionFactory apiCollection;

    private final DataSyncService dataSyncService;

    private final DataSyncTransformationRepository dataSyncTransformationRepository;
    private final DatasyncActivityRepository datasyncActivityRepository;
    private final TransformationRepository transformationRepository;
    private final TransformParamRepository transformParamRepository;
    private final TargetRegionRepository targetRegionRepository;
    private final OnesourceRegionRepository onesourceRegionRepository;
    private final MetaObjectRelationRepository metaObjectRelationRepository;

    // Added for customer tenant validation
    private final CustomerService customerService;
    private final OAuthService oAuthService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public DataSyncJobConfigService(RestTemplate restTemplate,
                                    DatasyncJobConfigRepository datasyncJobConfigRepository,
                                    ModelMapperConfig modelMapperConfig,
                                    MetaObjectRepository metaObjectRepository,
                                    RegionalJobConfigRepository regionalJobConfigRepository,
                                    ApiSupport apiSupport,
                                    ApiCollectionFactory apiCollection,
                                    DataSyncService dataSyncService,
                                    DataSyncTransformationRepository dataSyncTransformationRepository,
                                    DatasyncActivityRepository datasyncActivityRepository,
                                    TransformationRepository transformationRepository,
                                    TransformParamRepository transformParamRepository,
                                    TargetRegionRepository targetRegionRepository,
                                    OnesourceRegionRepository onesourceRegionRepository,
                                    MetaObjectRelationRepository metaObjectRelationRepository,
                                    CustomerService customerService,
                                    OAuthService oAuthService) {
        this.restTemplate = restTemplate;
        this.datasyncJobConfigRepository = datasyncJobConfigRepository;
        this.modelMapperConfig = modelMapperConfig;
        this.metaObjectRepository = metaObjectRepository;
        this.regionalJobConfigRepository = regionalJobConfigRepository;
        this.apiSupport = apiSupport;
        this.apiCollection = apiCollection;
        this.dataSyncService = dataSyncService;
        this.dataSyncTransformationRepository = dataSyncTransformationRepository;
        this.datasyncActivityRepository = datasyncActivityRepository;
        this.transformationRepository = transformationRepository;
        this.transformParamRepository = transformParamRepository;
        this.targetRegionRepository = targetRegionRepository;
        this.onesourceRegionRepository = onesourceRegionRepository;
        this.metaObjectRelationRepository = metaObjectRelationRepository;
        this.customerService = customerService;
        this.oAuthService = oAuthService;
    }

    public String createDataSyncJob(DatasyncJobConfigurationRequestDTO request) throws DataSyncJobException, JsonProcessingException {
        validateRequest(request);

        MetaObject metaObject = metaObjectRepository.findBySystemName(request.getMetaObjectSysName())
                .orElseThrow(() -> new DataSyncJobException("MetaObject not found for Meta Object System Name:" + request.getMetaObjectSysName(), Constants.DATASYNC_JOB_BAD_REQUEST));

        // New pre-creation validation: meta object must be a parent (no parent relation) and unused
        if (metaObjectRelationRepository.existsByChildObject_Id(metaObject.getId())) {
            throw new DataSyncJobException("Job creation is not allowed for child meta objects.", BAD_REQUEST);
        }

        //validate customer curated and TR curated data
        validateCuratedData(request);
        if (request.getTransformations() != null)
            validateTransformationContext(request.getTransformations(),request);

        if (request.getActivities() != null && !request.getActivities().isEmpty()) {
            validateActivities(request.getActivities(), request.getExecType());
        }

        String systemName = generateDataSyncJobSystemName(request);

        if (jobExistsBySystemName(systemName)) {
            throw new DataSyncJobException("Job already exists for the given meta object.", CONFLICT);
        }
        request.setSystemName(systemName);

        // Set default description if not provided
        if (request.getDescription() == null || request.getDescription().isEmpty()) {
            request.setDescription(systemName);
        }
        String domain = extractOneSourceDomainFromMetaObject(request.getMetaObjectSysName());
        request.setOnesourceDomain(domain);

        //create onesource job configurations records
        UUID datasyncJobId = createNewJob(request);
        //create regional job configurations records
        createRegionalJobRecords(request, datasyncJobId);

        // Validate and save activities
        if (request.getActivities() != null && !request.getActivities().isEmpty()) {
            createDataSyncActivity(request, systemName);
        }

        if( request.getTransformations() != null && !request.getTransformations().isEmpty()) {
            for (Transformations transformationContextDTO : request.getTransformations()) {
                createDataSyncTransform(transformationContextDTO, request.getSystemName());

            }
        }

        // Persist per-target region rows (unique per job_name + region)
        persistTargetRegions(systemName, request.getTargets());
        validateAndSyncJobConfigSyncAcrossRegions(ONESOURCE_DATASYNC_SYS_NAME,request.getSystemName(),"CREATE");
        return datasyncJobId.toString();
    }

    private void createDataSyncActivity(DatasyncJobConfigurationRequestDTO request,String datasyncJobSysName) {
        if (request.getActivities() != null && !request.getActivities().isEmpty()) {
            List<DataSyncActivityConfig> activities = request.getActivities().stream().map(dto -> {
                DataSyncActivityConfig entity = new DataSyncActivityConfig();
                entity.setActivitySysName(dto.getSysName());
                entity.setActivityId(dto.getActivityId());
                entity.setExecType(dto.getExecType());
                entity.setExecSeq(dto.getExecSeq());
                entity.setEventType(dto.getEventType());
                entity.setDatasyncJobSysName(datasyncJobSysName);
                entity.setActivityType(dto.getActivityType());
                entity.setCreatedBy(Constants.SYSTEM);
                entity.setCreatedAt(LocalDateTime.now());
                entity.setUpdatedBy(Constants.SYSTEM);
                entity.setUpdatedAt(LocalDateTime.now());
                return entity;
            }).collect(Collectors.toList());
            datasyncActivityRepository.saveAll(activities);
        }
    }
    public void validateTransformationContext(List<Transformations> transformationContext, DatasyncJobConfigurationRequestDTO requestDTO) throws DataSyncJobException {
        if (transformationContext != null && !transformationContext.isEmpty()) {
            Map<String, Set<String>> funcNameToFieldNamesMap = new HashMap<>();
            for (Transformations context : transformationContext) {
                if (context.getSeq() == 0 || context.getType() == null || context.getFuncName() == null) {
                    throw new DataSyncJobException("Invalid transformation context", BAD_REQUEST);
                }
                if( !EnumUtils.isValidEnum(TransformType.class, context.getType().name())) {
                    throw new DataSyncJobException("Invalid transformation type", BAD_REQUEST);
                }
                Optional<TransformationFunction> transformationFunction=transformationRepository.findBySystemName(context.getFuncName());
                if (transformationFunction.isEmpty()) {
                    throw new DataSyncJobException("Transformation function not found for funcName: " + context.getFuncName(), BAD_REQUEST);
                }
                validateParamsMatchWithRepo(context);
                validateDuplicateTransformation(context,funcNameToFieldNamesMap);

            }
        }
    }

    private void validateParamsMatchWithRepo(Transformations context) throws DataSyncJobException {
        if (context.getParams() != null && !context.getParams().isEmpty()) {
            // Fetch parameters from the repository
            List<TransformationFunctionParam> repoParams = transformParamRepository.findByTransformFuncId(context.getFuncName());

            // Validate that all input params match the repo params
            for (TransformParams inputParam : context.getParams()) {
                boolean matchFound = repoParams.stream()
                        .anyMatch(repoParam -> repoParam.getSystemName().equals(inputParam.getName()));
                if (!matchFound) {
                    throw new DataSyncJobException("Parameter mismatch for funcName: " + context.getFuncName() +
                            ", param: " + inputParam.getName() + " with value: " + inputParam.getValue(), BAD_REQUEST);
                }
            }

        }
    }

    private void validateDuplicateTransformation(Transformations context, Map<String, Set<String>> funcNameToFieldNamesMap) throws DataSyncJobException{
        if (context.getParams() != null && !context.getParams().isEmpty()) {
            // Fetch parameters from the repository
            List<TransformationFunctionParam> repoParams = transformParamRepository.findByTransformFuncId(context.getFuncName());

            // Only check for duplicates within the same region
            String region = context.getRegion();
            String funcName = context.getFuncName();
            String regionKey = funcName + "::" + (region == null ? "" : region);
            funcNameToFieldNamesMap.putIfAbsent(regionKey, new HashSet<>());
            Set<String> fieldNames = funcNameToFieldNamesMap.get(regionKey);

            for (TransformParams param : context.getParams()) {
                if ("field_name".equals(param.getName())) {
                    if (!fieldNames.add(param.getValue())) {
                        throw new DataSyncJobException("Duplicate transformation detected for funcName: "
                                + funcName + ", region: " + region + ", field_name: " + param.getValue(), BAD_REQUEST);
                    }
                }
            }
        }
    }


    private void createDataSyncTransform(Transformations context, String datasyncJobSysName) throws JsonProcessingException {
        DataSyncTransformation dataSyncTransformation = new DataSyncTransformation();
        dataSyncTransformation.setExecutionSeq(context.getSeq());
        ObjectMapper objectMapper = new ObjectMapper();
        dataSyncTransformation.setFunctionParams(objectMapper.writeValueAsString(context.getParams()));
        dataSyncTransformation.setDatasyncJobSysName(datasyncJobSysName);
        dataSyncTransformation.setRegion(context.getRegion());
        dataSyncTransformation.setTransformFuncSysName(context.getFuncName());
        dataSyncTransformation.setType(context.getType().name());
        dataSyncTransformation.setExecLeg(context.getExecLeg());
        dataSyncTransformation.setCreatedBy(Constants.SYSTEM);
        dataSyncTransformation.setCreatedAt(LocalDateTime.now());
        dataSyncTransformation.setUpdatedBy(Constants.SYSTEM);
        dataSyncTransformation.setUpdatedAt(LocalDateTime.now());
        dataSyncTransformationRepository.save(dataSyncTransformation);
    }

    public String extractOneSourceDomainFromMetaObject(String metaObjectSysName) throws DataSyncJobException {
        return metaObjectRepository.findBySystemName(metaObjectSysName)
                .map(MetaObject::getOneSourceDomain)
                .orElseThrow(() -> new DataSyncJobException("Invalid metaObjectSysName", Constants.DATASYNC_JOB_BAD_REQUEST));
    }

    private void validateAndSyncJobConfigSyncAcrossRegions(String metaObject, String sysName, String operationType) throws DataSyncJobException {
        // Trigger sync across regions for meta object related tables
            RequestBodyDataUnitList requestDataUnitList = new RequestBodyDataUnitList();
            requestDataUnitList.setObjIds(Collections.singletonList(sysName));
            RequestModel requestModel = new RequestModel();

            requestModel.setMetaObjectName(metaObject);
            requestModel.setExecType(ExecType.REAL_TIME);
            requestModel.setCustomerTenantSysName(CUSTOMER_TENANT_SYS_NAME);
            requestModel.setRequestDataUnitList(requestDataUnitList);
            requestModel.setOperationType(OperationType.valueOf(operationType));
            requestModel.setSourceTenantId("");
            dataSyncService.publishDataSyncJob(requestModel);
            log.info("Sync across regions triggered for jobId: {}", sysName);


    }

    public void validateRequest(DatasyncJobConfigurationRequestDTO request) throws DataSyncJobException {
        if (!isValidMetaObjectSystemName(request.getMetaObjectSysName())) {
            throw new DataSyncJobException("Invalid meta object system name", Constants.DATASYNC_JOB_BAD_REQUEST);
        }
        if (request.getJobType() == null || !isValidJobType(request.getJobType())) {
            throw new DataSyncJobException("Invalid job type", Constants.DATASYNC_JOB_BAD_REQUEST);
        }
        if (request.getSource() == null || !isValidRegion(request.getSource().getRegion())) {
            throw new DataSyncJobException("Invalid source region",Constants.DATASYNC_JOB_BAD_REQUEST);
        }
        if (request.getTargets() == null || request.getTargets().isEmpty()) {
            throw new DataSyncJobException("Targets list is required and cannot be empty", Constants.DATASYNC_JOB_BAD_REQUEST);
        }
        if(request.getJobType() == JobType.CUSTOMER) {
            // regional_tenant_id (top-level) mandatory
            if (StringUtils.isBlank(request.getSource().getRegionalTenantId())) {
                throw new DataSyncJobException("Source Regional Tenant ID is mandatory for CUSTOMER job type", Constants.DATASYNC_JOB_BAD_REQUEST);
            }
            // customerTenantId now optional for CUSTOMER
            if (StringUtils.isBlank(request.getCustomerTenantSysName())) {
                throw new DataSyncJobException("Client Tenant System Name is mandatory for CUSTOMER job type", Constants.DATASYNC_JOB_BAD_REQUEST);
            }
            // For CUSTOMER targets, region and region_tenant mandatory
            for (TargetRegionDTO target : request.getTargets()) {
                if(!isValidRegion(target.getRegion())){
                    throw new DataSyncJobException("Invalid target region: " + target.getRegion(), Constants.DATASYNC_JOB_BAD_REQUEST);
                }
                if ( StringUtils.isBlank(target.getRegionalTenantId())) {
                    throw new DataSyncJobException(" Region_tenant are mandatory for CUSTOMER job type", Constants.DATASYNC_JOB_BAD_REQUEST);
                }
            }
        } else if (request.getJobType() == JobType.PLATFORM) {
            // PLATFORM: customer-related fields must NOT be populated
            if (StringUtils.isNotBlank(request.getCustomerTenantSysName())) {
                throw new DataSyncJobException("customer_tenant_sys_name must not be populated for PLATFORM job type", Constants.DATASYNC_JOB_BAD_REQUEST);
            }
            
            if (request.getSource() != null && StringUtils.isNotBlank(request.getSource().getRegionalTenantId())) {
                throw new DataSyncJobException("source.regional_tenant must not be populated for PLATFORM job type", Constants.DATASYNC_JOB_BAD_REQUEST);
            }
            
            // Validate target regions and ensure regional_tenant is not populated
            for (TargetRegionDTO target : request.getTargets()) {
                if (StringUtils.isBlank(target.getRegion())) {
                    throw new DataSyncJobException("Target region is required and cannot be blank", Constants.DATASYNC_JOB_BAD_REQUEST);
                }
                if(!isValidRegion(target.getRegion())){
                    throw new DataSyncJobException("Invalid target region: " + target.getRegion(), Constants.DATASYNC_JOB_BAD_REQUEST);
                }
                if (StringUtils.isNotBlank(target.getRegionalTenantId())) {
                    throw new DataSyncJobException("target.regional_tenant must not be populated for PLATFORM job type", Constants.DATASYNC_JOB_BAD_REQUEST);
                }
            }
            
            // Set fields to null after validation to ensure consistency
            request.setCustomerTenantSysName(null);
            if (request.getSource() != null) {
                request.getSource().setRegionalTenantId(null);
            }
            for (TargetRegionDTO target : request.getTargets()) {
                target.setRegionalTenantId(null);
            }
        } else {
            throw new DataSyncJobException("Invalid job type", Constants.DATASYNC_JOB_BAD_REQUEST);
        }
        if (Boolean.TRUE.equals(request.getIsUgeCustomer()) && StringUtils.isNotBlank(request.getCustomerTenantSysName())) {
            validateCustomerTenantConstraints(request);
        }
        if (!isValidExecType(request.getExecType())) {
            throw new DataSyncJobException("Invalid execution type", Constants.DATASYNC_JOB_BAD_REQUEST);
        }
    }

    public void validateUpdateRequestFields(DatasyncJobConfigurationRequestDTO request) throws DataSyncJobException {
        if (request.getJobType() == JobType.CUSTOMER) {
            if (request.getSource() == null || StringUtils.isBlank(request.getSource().getRegionalTenantId())) {
                throw new DataSyncJobException("Source Regional Tenant ID is mandatory for CUSTOMER job type", Constants.DATASYNC_JOB_BAD_REQUEST);
            }
            if (request.getTargets() != null && !request.getTargets().isEmpty()) {
                for (TargetRegionDTO target : request.getTargets()) {
                    if (StringUtils.isBlank(target.getRegionalTenantId())) {
                        throw new DataSyncJobException("Target Regional tenant Id is mandatory for CUSTOMER job type", Constants.DATASYNC_JOB_BAD_REQUEST);
                    }
                }
            }
        } else if (request.getJobType() == JobType.PLATFORM) {
            // PLATFORM: customer-related fields must NOT be populated during updates
            if (StringUtils.isNotBlank(request.getCustomerTenantSysName())) {
                throw new DataSyncJobException("customer_tenant_sys_name must not be populated for PLATFORM job type", Constants.DATASYNC_JOB_BAD_REQUEST);
            }
            
            if (request.getSource() != null && StringUtils.isNotBlank(request.getSource().getRegionalTenantId())) {
                throw new DataSyncJobException("source.regional_tenant must not be populated for PLATFORM job type", Constants.DATASYNC_JOB_BAD_REQUEST);
            }
            
            // Validate target regions and ensure regional_tenant is not populated  
            if (request.getTargets() != null && !request.getTargets().isEmpty()) {
                for (TargetRegionDTO target : request.getTargets()) {
                    if (StringUtils.isNotBlank(target.getRegionalTenantId())) {
                        throw new DataSyncJobException("target.regional_tenant must not be populated for PLATFORM job type", Constants.DATASYNC_JOB_BAD_REQUEST);
                    }
                }
            }
            
            // Set fields to null after validation to ensure consistency
            request.setCustomerTenantSysName(null);
            if (request.getSource() != null) {
                request.getSource().setRegionalTenantId(null);
            }
            if (request.getTargets() != null) {
                for (TargetRegionDTO target : request.getTargets()) {
                    target.setRegionalTenantId(null);
                }
            }
        }
    }

    private boolean isValidMetaObjectSystemName(String metaObjectSysName) {
        return metaObjectRepository.existsBySystemName(metaObjectSysName);
    }

    private boolean isValidJobType(JobType jobType) {
        return EnumUtils.isValidEnum(JobType.class, jobType.name());
    }

    private boolean isValidRegion(String region) {
        return onesourceRegionRepository.findBySystemName(region).isPresent();
    }

    private boolean isValidExecType(List<ExecType> execType) {
        if( execType==null || execType.isEmpty()) {
            return false;
        }
        return execType.stream().allMatch(type ->
                EnumUtils.isValidEnum(ExecType.class, type.name()));
    }


    private String generateRegionalJobSystemName(DatasyncJobConfigurationRequestDTO request,
                                                 String region, String regionalTenantId) throws DataSyncJobException {
        String metaObjectName = request.getMetaObjectSysName();
        String execType = request.getExecType().stream().map(Enum::name).collect(Collectors.joining("_"));
        if( execType.equalsIgnoreCase("REAL_TIME")) {
            execType = "RT";
        } else if (execType.equalsIgnoreCase("BATCH")) {
            execType = "BT";
        } else {
            throw new DataSyncJobException("Invalid execution type for PLATFORM job type", Constants.DATASYNC_JOB_BAD_REQUEST);
        }
        if (request.getJobType() == JobType.PLATFORM) {
            //example: platform.meta_object_sys_name.region.execution_type
            return String.format("platform.%s.%s.%s", metaObjectName, region.toLowerCase(), execType.toLowerCase());
        } else if (request.getJobType() == JobType.CUSTOMER) {
            //example: customer_tenant_sys_name.meta_object_sys_name.region.region_tenant_id.execution_type
            return String.format("%s.%s.%s.%s.%s", request.getCustomerTenantSysName(), metaObjectName,
                    region.toLowerCase(), regionalTenantId.toLowerCase(), execType.toLowerCase());
        } else {
            throw new DataSyncJobException("Job type is not supported to generate job name", Constants.DATASYNC_JOB_BAD_REQUEST);
        }
    }

    private String generateDataSyncJobSystemName(DatasyncJobConfigurationRequestDTO request) throws DataSyncJobException {
        String metaObjectName = request.getMetaObjectSysName();
        String execType = request.getExecType().stream().map(Enum::name).collect(Collectors.joining("_"));
        if( execType.equalsIgnoreCase("REAL_TIME")) {
            execType = "RT";
        } else if (execType.equalsIgnoreCase("BATCH")) {
            execType = "BT";
        } else {
            throw new DataSyncJobException("Invalid execution type for PLATFORM job type", Constants.DATASYNC_JOB_BAD_REQUEST);
        }
        if (request.getJobType() == JobType.PLATFORM) {
            //example: platform.meta_object_sys_name.execution_type
            return String.format("platform.%s.%s", metaObjectName, execType.toLowerCase());
        } else if (request.getJobType() == JobType.CUSTOMER) {
            //example: customer_tenant_sys_name.meta_object_sys_name.execution_type
            return String.format("%s.%s.%s", request.getCustomerTenantSysName(), metaObjectName, execType.toLowerCase());
        } else {
            throw new DataSyncJobException("Job type is not supported to generate job name", Constants.DATASYNC_JOB_BAD_REQUEST);
        }
    }

    public boolean jobExistsBySystemName(String systemName) {
        return datasyncJobConfigRepository.existsBySystemName(systemName);
    }

    public UUID createNewJob(DatasyncJobConfigurationRequestDTO request) throws DataSyncJobException {
        try {
            DatasyncJobConfiguration newJob =modelMapperConfig.modelMapper().map(request, DatasyncJobConfiguration.class);
            newJob.setExecType(request.getExecType().stream().map(Enum::name).collect(Collectors.joining(",")));
            newJob.setCreatedBy(Constants.SYSTEM);
            newJob.setSourceRegion(request.getSource().getRegion());
            newJob.setSourceTenantId(request.getSource().getRegionalTenantId());
            newJob.setCreatedAt(LocalDateTime.now());
            newJob.setUpdatedBy(Constants.SYSTEM);
            newJob.setUpdatedAt(LocalDateTime.now());
            newJob.setMetaObjectSysName(request.getMetaObjectSysName());
            newJob.setCustomerTenantSysName(request.getCustomerTenantSysName());
            datasyncJobConfigRepository.save(newJob);
            return newJob.getId();
        } catch (Exception e) {
            throw new DataSyncJobException("Failed to create new job", Constants.INTERNAL_SERVER_ERROR);
        }
    }

    public void createRegionalJobRecords(DatasyncJobConfigurationRequestDTO request, UUID datasyncJobId) throws DataSyncJobException {
        // Create a record for the source region
        if (request.getSource() != null && StringUtils.isNotBlank(request.getSource().getRegion())) {
            createRegionalJobRecord(request, datasyncJobId, request.getSource().getRegion(), null, ExecutionLeg.SOURCE);
        }

        // Preferred path: new targets list (each element => TARGET record)
        if (request.getTargets() != null && !request.getTargets().isEmpty()) {
            for (TargetRegionDTO target : request.getTargets()) {
                if (target.getRegion() != null) {
                    createRegionalJobRecord(request, datasyncJobId, target.getRegion(), target.getRegionalTenantId(), ExecutionLeg.TARGET);
                }
            }
        }
    }

    public void createRegionalJobRecord(DatasyncJobConfigurationRequestDTO request, UUID datasyncJobId, String targetRegion, String targetTenantId, ExecutionLeg execLeg) throws DataSyncJobException {
        try {
            RegionalJobConfiguration regionalJob = new RegionalJobConfiguration();
            regionalJob.setType(request.getJobType());

            if(execLeg == ExecutionLeg.SOURCE && request.getSource() != null && StringUtils.isNotBlank(request.getSource().getRegionalTenantId())) {
                regionalJob.setSourceTenantId(request.getSource().getRegionalTenantId());
            }
            else{
                regionalJob.setSourceTenantId(null);
            }

            if (execLeg == ExecutionLeg.SOURCE) {
                regionalJob.setTargetRegion(request.getTargets().stream().map(TargetRegionDTO::getRegion).collect(Collectors.joining(",")));
            }
            else
                regionalJob.setTargetRegion(targetRegion);

            regionalJob.setDatasyncJobId(datasyncJobId);
            regionalJob.setActive(true);
            regionalJob.setInAdaptorId(getInputAdaptorId(request.getExecType(), execLeg));
            regionalJob.setOutAdaptorId(getOutputAdaptorId(request.getExecType(), execLeg));
            regionalJob.setSourceRegion(request.getSource().getRegion());

            // Per requirements: for TARGET records use per-target region_tenant as target_tenant_id
            if (execLeg == ExecutionLeg.TARGET) {
                regionalJob.setTargetTenantId(targetTenantId);
            } else {
                regionalJob.setTargetTenantId(null);
            }

            regionalJob.setCreatedBy(Constants.SYSTEM);
            regionalJob.setCreatedAt(LocalDateTime.now());
            regionalJob.setUpdatedBy(Constants.SYSTEM);
            regionalJob.setUpdatedAt(LocalDateTime.now());
            regionalJob.setExecLeg(execLeg);
            regionalJob.setDatasyncJobSysName(request.getSystemName());

            regionalJob.setOnesourceDomain(request.getOnesourceDomain());
            regionalJob.setMetaObjectSysName(request.getMetaObjectSysName());
            regionalJob.setCustomerTenantSysName(request.getCustomerTenantSysName());

            // Group transformations by region and execLeg
            Map<String, List<Transformations>> sourceRegionTransformations = new HashMap<>();
            Map<String, List<Transformations>> targetRegionTransformations = new HashMap<>();
            if (request.getTransformations() != null) {
                for (Transformations context : request.getTransformations()) {
                    if (context.getRegion() != null && context.getExecLeg() != null) {
                        if (ExecutionLeg.SOURCE.toString().equalsIgnoreCase(context.getExecLeg())) {
                            sourceRegionTransformations.computeIfAbsent(context.getRegion(), k -> new ArrayList<>()).add(context);
                        } else if (ExecutionLeg.TARGET.toString().equalsIgnoreCase(context.getExecLeg())) {
                            targetRegionTransformations.computeIfAbsent(context.getRegion(), k -> new ArrayList<>()).add(context);
                        }
                    }
                }
            }
            // For SOURCE, use transformations for the source region
            if (execLeg == ExecutionLeg.SOURCE) {
                List<Transformations> sourceTransforms = sourceRegionTransformations.getOrDefault(request.getSource().getRegion(), new ArrayList<>());
                regionalJob.setTransformContext(sourceTransforms);
            } else if (execLeg == ExecutionLeg.TARGET) {
                // For TARGET, use transformations for the target region
                List<Transformations> targetTransforms = targetRegionTransformations.getOrDefault(targetRegion, new ArrayList<>());
                regionalJob.setTransformContext(targetTransforms);
            }

            // Save the regional job configuration
            regionalJobConfigRepository.save(regionalJob);
        } catch (Exception e) {
            throw new DataSyncJobException("Failed to create regional job record", Constants.INTERNAL_SERVER_ERROR);
        }
    }

    public DatasyncJobConfigurationRequestDTO getJobConfigurationDetails(UUID jobId) throws DataSyncJobException {
        // get job details from database
        Optional<DatasyncJobConfiguration> dataSyncJobConfiguration=datasyncJobConfigRepository.findById(jobId);

        if(dataSyncJobConfiguration.isPresent()){
            Set<RegionalJobConfiguration> regionalJobConfiguration=regionalJobConfigRepository.findByDatasyncJobIdAndIsActive(dataSyncJobConfiguration.get().getId(), true);
            List<Transformations> allTransformations = regionalJobConfiguration.stream()
                    .map(RegionalJobConfiguration::getTransformContext)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            // Assign to DTO
            DatasyncJobConfigurationRequestDTO dto = convertToDto(dataSyncJobConfiguration.get());
            dto.setTransformations(allTransformations);
            //  Populate targets from RegionalJobConfiguration
            List<TargetRegionDTO> targets = regionalJobConfiguration.stream()
                    .filter(rjc -> rjc.getExecLeg() == ExecutionLeg.TARGET)
                    .map(rjc -> {
                        TargetRegionDTO target = new TargetRegionDTO();
                        target.setRegion(rjc.getTargetRegion());
                        target.setRegionalTenantId(rjc.getTargetTenantId());
                        return target;
                    })
                    .collect(Collectors.toList());
            dto.setTargets(targets);
            List<DataSyncActivityConfig> activities = datasyncActivityRepository.findByDatasyncJobSysName(dataSyncJobConfiguration.get().getSystemName());
            List<ActivityDTO> activityDTOs = activities.stream()
                    .map(activity -> {
                        ActivityDTO activityDTO = new ActivityDTO();
                        activityDTO.setSysName(activity.getActivitySysName());
                        activityDTO.setActivityId(activity.getActivityId());
                        activityDTO.setExecType(activity.getExecType());
                        activityDTO.setExecSeq(activity.getExecSeq());
                        activityDTO.setEventType(activity.getEventType());
                        activityDTO.setActivityType(activity.getActivityType());
                        return activityDTO;
                    })
                    .collect(Collectors.toList());
            dto.setActivities(activityDTOs);
            return dto;
        }
        throw new DataSyncJobException("DataSync Job not found", Constants.NOT_FOUND);
    }

    private DatasyncJobConfigurationRequestDTO convertToDto(DatasyncJobConfiguration job) {
        // Convert the job entity to DTO using ModelMapper
        DatasyncJobConfigurationRequestDTO dto = modelMapperConfig.modelMapper().map(job, DatasyncJobConfigurationRequestDTO.class);
        // Manually map the source field (nested object)
        SourceRegionDTO source = new SourceRegionDTO();
        source.setRegion(job.getSourceRegion());
        source.setRegionalTenantId(job.getSourceTenantId());
        dto.setSource(source);
        if (job.getExecType() != null && !job.getExecType().isEmpty()) {
            List<ExecType> execTypeList = Arrays.stream(job.getExecType().split(","))
                    .map(ExecType::valueOf)
                    .collect(Collectors.toList());
            dto.setExecType(execTypeList);
        }
        return dto;
    }


    public String updateDataSyncJob(DatasyncJobConfigurationUpdateRequestDTO updateRequest) throws DataSyncJobException, JsonProcessingException {
        // performing the Validation of request
        Optional<DatasyncJobConfiguration> datasyncJobConfig = datasyncJobConfigRepository.findById(updateRequest.getId());
        String sourceRegion = null;
        if (datasyncJobConfig.isPresent()) {
            sourceRegion = datasyncJobConfig.get().getSourceRegion();
        } else {
            throw new DataSyncJobException("DataSync Job not found", Constants.NOT_FOUND);
        }
        DatasyncJobConfiguration existingJob = datasyncJobConfig.get();
        DatasyncJobConfigurationRequestDTO request = modelMapperConfig.modelMapper().map(updateRequest, DatasyncJobConfigurationRequestDTO.class);
        request.setId(updateRequest.getId());
        SourceRegionDTO sourceRegionDTO = new SourceRegionDTO();
        sourceRegionDTO.setRegion(sourceRegion);
        sourceRegionDTO.setRegionalTenantId(updateRequest.getSource().getRegionalTenantId());
        request.setSource(sourceRegionDTO);
        request.setSystemName(existingJob.getSystemName());
        request.setIsUgeCustomer(existingJob.getIsUgeCustomer());

        String metaObjectSysName = existingJob.getMetaObjectSysName();
        if (metaObjectSysName == null) {
            throw new DataSyncJobException("Meta Object System Name is missing in existing job", Constants.DATASYNC_JOB_BAD_REQUEST);
        }

        // Fetch job type from database
        JobType jobType = existingJob.getJobType();
        //  Extract onesourceDomain using metaObjectId
        String domain = extractOneSourceDomainFromMetaObject(metaObjectSysName);
        request.setMetaObjectSysName(metaObjectSysName);
        request.setOnesourceDomain(domain);
        // Ensure job type is set before validation so conditional rules apply
        request.setJobType(jobType);

        validateUpdateRequestFields(request);
        if (Boolean.TRUE.equals(request.getIsUgeCustomer()) && StringUtils.isNotBlank(request.getCustomerTenantSysName())) {
            validateCustomerTenantConstraints(request);
        }
        if (request.getTransformations() != null)
            validateTransformationContext(request.getTransformations(),request);
        
        Optional<DatasyncJobConfiguration> dataSyncJobConfig = datasyncJobConfigRepository.findById(request.getId());
        if(dataSyncJobConfig.isPresent()){
            DatasyncJobConfiguration datasyncJobConfiguration = dataSyncJobConfig.get();
            datasyncJobConfiguration.setDescription(StringUtils.trimToNull(request.getDescription()));
            if (datasyncJobConfiguration.getDescription() == null || datasyncJobConfiguration.getDescription().isEmpty()) {
                datasyncJobConfiguration.setDescription(datasyncJobConfiguration.getSystemName());
            }
            datasyncJobConfiguration.setSourceTenantId(StringUtils.trimToNull(request.getSource().getRegionalTenantId()));
            datasyncJobConfiguration.setIsUgeCustomer(request.getIsUgeCustomer());
            datasyncJobConfiguration.setUpdatedBy(Constants.SYSTEM);
            datasyncJobConfiguration.setUpdatedAt(LocalDateTime.now());
            datasyncJobConfigRepository.save(datasyncJobConfiguration);

            // update the meta object details and other details
            request.setMetaObjectSysName(datasyncJobConfiguration.getMetaObjectSysName());
            request.setJobType(datasyncJobConfiguration.getJobType());
            request.getSource().setRegion(datasyncJobConfiguration.getSourceRegion());
            request.setCustomerTenantSysName(datasyncJobConfiguration.getCustomerTenantSysName());
            request.setSystemName(datasyncJobConfiguration.getSystemName());
            request.setExecType(Arrays.stream(datasyncJobConfiguration.getExecType().split(","))
                    .map(s -> ExecType.valueOf(s.trim()))
                    .collect(Collectors.toList()));


            if (request.getActivities()!=null && !request.getActivities().isEmpty()) {
                validateActivities(request.getActivities(), request.getExecType());
            }
            // Save activities in datasync_activities table (replace existing)
            updateDataSyncActivties(request,dataSyncJobConfig.get());

            List<RegionalJobConfiguration> regionalJobConfiguration = new ArrayList<>(regionalJobConfigRepository.findByDatasyncJobIdAndIsActive(existingJob.getId(), true));

            // update the regional job details
            Set<RegionalJobConfiguration> regionalJobs = regionalJobConfigRepository.findByDatasyncJobIdAndIsActive(datasyncJobConfiguration.getId(), true);
            List<TargetRegionDTO> existingRegions =new ArrayList<>();
            if(regionalJobs!=null && !regionalJobs.isEmpty()){
                existingRegions = regionalJobs.stream()
                        .filter(job -> job.getExecLeg() == ExecutionLeg.TARGET)
                        .map(job -> {
                            TargetRegionDTO targetRegionDTO = new TargetRegionDTO();
                            targetRegionDTO.setRegion(job.getTargetRegion());
                            targetRegionDTO.setRegionalTenantId(job.getTargetTenantId());
                            return targetRegionDTO;
                        })
                        .toList();
            }
            updateRegionalJobs(
                    request.getId(),
                    request,
                    extractTargetRegionsFromRegionalJobs(regionalJobConfiguration),
                    request.getTargets()
            );
            if(request.getTransformations()!=null && !request.getTransformations().isEmpty()) { // update the transformation context
                updateDataSyncTransformation(request.getSystemName(), request.getTransformations());
            }

            // Synchronize target_regions table with latest targets
            synchronizeTargetRegions(existingJob.getSystemName(), request.getTargets());

            validateAndSyncJobConfigSyncAcrossRegions(ONESOURCE_DATASYNC_SYS_NAME, request.getSystemName(), "UPDATE");
            return request.getId().toString();
        }

        throw new DataSyncJobException("Job not found", Constants.NOT_FOUND);
    }


    private List<TargetRegionDTO> extractTargetRegionsFromRegionalJobs(List<RegionalJobConfiguration> regionalJobs) {
        // Only consider TARGET regional job rows. The SOURCE row stores a comma-separated list
        // of target regions in its targetRegion field which should NOT be treated as a single region.
        if (regionalJobs == null) return Collections.emptyList();
        return regionalJobs.stream()
                .filter(r -> r.getExecLeg() == ExecutionLeg.TARGET)
                .map(r -> {
                    TargetRegionDTO dto = new TargetRegionDTO();
                    // For TARGET rows execRegion is the single region we want.
                    dto.setRegion(r.getTargetRegion());
                    dto.setRegionalTenantId(r.getTargetTenantId());
                    return dto;
                })
                .collect(Collectors.toList());
    }


    private void updateDataSyncActivties(DatasyncJobConfigurationRequestDTO request, DatasyncJobConfiguration datasyncJobConfiguration) {
        List<DataSyncActivityConfig> activityConfig = datasyncActivityRepository.findByDatasyncJobSysName(datasyncJobConfiguration.getSystemName());

        DataSyncActivityConfig configs = null;
        if(request.getActivities()!=null && !request.getActivities().isEmpty()) {
            for (ActivityDTO activityDTO : request.getActivities()) {
                configs = activityConfig.stream().filter(activity -> {
                    return activity.getActivitySysName().equals(activityDTO.getSysName());
                }).findFirst().orElse(null);

                if (configs != null) {
                    configs.setExecType(activityDTO.getExecType());
                    configs.setExecSeq(activityDTO.getExecSeq());
                    configs.setEventType(activityDTO.getEventType());
                    configs.setActivityType(activityDTO.getActivityType());
                    configs.setUpdatedBy(Constants.SYSTEM);
                    configs.setUpdatedAt(LocalDateTime.now());
                    datasyncActivityRepository.save(configs);
                } else {
                    // Create new activities
                    createDataSyncActivity(request, datasyncJobConfiguration.getSystemName());
                }
            }
        }
    }


    private void updateDataSyncTransformation(String datasyncJobSysName, List<Transformations> transformationContext) throws DataSyncJobException, JsonProcessingException {
        // Delete all existing DataSyncTransformation records for this job
        List<DataSyncTransformation> dataSyncTransformations = dataSyncTransformationRepository.findByDatasyncJobSysName(datasyncJobSysName);
        if (!dataSyncTransformations.isEmpty()) {
            dataSyncTransformationRepository.deleteAll(dataSyncTransformations);
        }

        if (transformationContext != null && !transformationContext.isEmpty()) {
            for (Transformations context : transformationContext) {
                createDataSyncTransform(context, datasyncJobSysName);
            }
        }
    }


    public void updateRegionalJobs(UUID jobId, DatasyncJobConfigurationRequestDTO request, List<TargetRegionDTO> existingTargets, List<TargetRegionDTO> newTargets) throws DataSyncJobException {
        /**
         * Updates regionTenantId based on job uniqueness and business rules.
         *
         * Regional job uniqueness is determined by the combination of (jobId + execRegion),
         * so only the region is used as the key for processing.
         *
         * Key Business Rules:
         * - For TARGET rows:
         *     - Do NOT update regionTenantId; it must remain null.
         * - For SOURCE rows:
         *     - Update regionTenantId as needed based on propagation logic.
         *
         * This distinction between TARGET and SOURCE rows is critical to ensure
         * correct propagation of tenant/client updates.
         */
        List<TargetRegionDTO> effectiveNewTargets = (newTargets == null || newTargets.isEmpty()) ? existingTargets : newTargets;
        // Build existing & new region sets (region alone is unique)
        Set<String> existingRegions = existingTargets == null ? Collections.emptySet() :
                existingTargets.stream()
                        .map(TargetRegionDTO::getRegion)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

        Set<String> newRegions = effectiveNewTargets == null ? Collections.emptySet() :
                effectiveNewTargets.stream()
                        .map(TargetRegionDTO::getRegion)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

        // Deactivate regions removed from the list
        for (String regionName : existingRegions) {
            if (!newRegions.contains(regionName)) {
                try {
                    deactivateRegionalJob(jobId, regionName);
                } catch (IllegalArgumentException ex) {
                    log.warn("Skipping deactivation for invalid region token '{}'", regionName);
                }
            }
        }

        // Collapse new targets list to one entry per region (last occurrence wins for tenant id)
        Map<String, String> regionToTenant = new LinkedHashMap<>();
        if (effectiveNewTargets != null) {
            for (TargetRegionDTO t : effectiveNewTargets) {
                if (t.getRegion() != null) {
                    regionToTenant.put(t.getRegion(), t.getRegionalTenantId()); // may be null
                }
            }
        }

        // Upsert / reactivate target rows (always evaluate to propagate customerTenantId/clientId changes)
        for (Map.Entry<String, String> entry : regionToTenant.entrySet()) {
            String regionName = entry.getKey();
            String regionTenant = entry.getValue();
            String regionEnum;
            try {
                regionEnum = regionName;
            } catch (IllegalArgumentException ex) {
                log.warn("Skipping invalid region '{}'", regionName);
                continue;
            }

            Optional<RegionalJobConfiguration> existingRegionalJob =
                    regionalJobConfigRepository.findByDatasyncJobIdAndExecLegAndTargetRegion(jobId, ExecutionLeg.TARGET, regionEnum);

            if (existingRegionalJob.isPresent()) {
                RegionalJobConfiguration rj = existingRegionalJob.get();
                boolean tenantChanged = !Objects.equals(rj.getTargetTenantId(), regionTenant);
                boolean customerTenantChanged = !Objects.equals(rj.getCustomerTenantSysName(), request.getCustomerTenantSysName());
                if (!rj.isActive() || tenantChanged || customerTenantChanged) {
                    updateRegionalJob(request, rj, regionTenant);
                } else {
                    // Only overwrite transform context
                    if (request.getTransformations() != null && !request.getTransformations().isEmpty()) {
                        String targetRegion = rj.getTargetRegion();
                        String execLeg = rj.getExecLeg().name();
                        List<Transformations> filteredTransforms = request.getTransformations().stream()
                                .filter(t -> t.getRegion() != null && t.getExecLeg() != null)
                                .filter(t -> t.getRegion().equals(targetRegion) && t.getExecLeg().equalsIgnoreCase(execLeg))
                                .collect(Collectors.toList());
                        rj.setTransformContext(filteredTransforms);
                        regionalJobConfigRepository.save(rj);
                    }
                }
            } else {
                // brand new target
                createRegionalJobRecord(request, jobId, regionEnum, regionTenant, ExecutionLeg.TARGET);
            }
        }

        // Update SOURCE row (adaptor ids, tenant/customer/client changes, reactivation) and keep legacy CSV of targets in sync
        try {
            Optional<RegionalJobConfiguration> sourceRowOpt =
                    regionalJobConfigRepository.findByDatasyncJobIdAndExecLeg(jobId, ExecutionLeg.SOURCE);
            if (sourceRowOpt.isPresent()) {
                RegionalJobConfiguration src = sourceRowOpt.get();

                // Rebuild comma-delimited target list for backward compatibility
                String csvTargets = (effectiveNewTargets == null || effectiveNewTargets.isEmpty())
                        ? ""
                        : effectiveNewTargets.stream()
                        .map(TargetRegionDTO::getRegion)
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining(","));

                boolean regionTenantChanged = !Objects.equals(src.getSourceTenantId(), request.getSource().getRegionalTenantId());
                boolean customerTenantChanged = !Objects.equals(src.getCustomerTenantSysName(), request.getCustomerTenantSysName());
                boolean targetCsvChanged = !Objects.equals(src.getTargetRegion(), csvTargets);
                boolean wasInactive = !src.isActive();

                if (regionTenantChanged || customerTenantChanged || targetCsvChanged || wasInactive) {
                    src.setActive(true);
                    src.setInAdaptorId(getInputAdaptorId(request.getExecType(), src.getExecLeg()));
                    src.setOutAdaptorId(getOutputAdaptorId(request.getExecType(), src.getExecLeg()));
                    src.setSourceTenantId(request.getSource().getRegionalTenantId());
                    src.setCustomerTenantSysName(request.getCustomerTenantSysName());
                    src.setTargetRegion(csvTargets);
                    src.setUpdatedBy(Constants.SYSTEM);
                    src.setUpdatedAt(LocalDateTime.now());
                    regionalJobConfigRepository.save(src);
                }
            }
        } catch (Exception ex) {
            log.warn("Failed to update SOURCE row (non-fatal): {}", ex.getMessage());
        }
    }

    public void deactivateRegionalJob(UUID jobId, String existingRegion) {
        Optional<RegionalJobConfiguration> regionalJob = regionalJobConfigRepository.findByDatasyncJobIdAndExecLegAndTargetRegion(jobId, ExecutionLeg.TARGET, existingRegion);
        if (regionalJob.isPresent()) {
            regionalJob.get().setActive(false);
            regionalJob.get().setUpdatedBy(Constants.SYSTEM);
            regionalJob.get().setUpdatedAt(LocalDateTime.now());
            regionalJobConfigRepository.save(regionalJob.get());
        }
    }

    private void updateRegionalJob(DatasyncJobConfigurationRequestDTO request,
                                   RegionalJobConfiguration regionalJob,
                                   String targetTenantId) {
        regionalJob.setActive(true);
        regionalJob.setInAdaptorId(getInputAdaptorId(request.getExecType(), regionalJob.getExecLeg()));
        regionalJob.setOutAdaptorId(getOutputAdaptorId(request.getExecType(), regionalJob.getExecLeg()));

        // SOURCE row keeps regional tenant id, TARGET rows keep target tenant id
        if (regionalJob.getExecLeg() == ExecutionLeg.SOURCE) {
            regionalJob.setSourceTenantId(request.getSource().getRegionalTenantId());
        } else {
            regionalJob.setSourceTenantId(null);
            if (targetTenantId != null && !targetTenantId.isBlank()) {
                regionalJob.setTargetTenantId(targetTenantId);
            }
        }

        regionalJob.setCustomerTenantSysName(request.getCustomerTenantSysName());

        // Only overwrite transform context for given execLeg when new context provided
        if (request.getTransformations() != null && !request.getTransformations().isEmpty()) {
            String region = regionalJob.getExecLeg() == ExecutionLeg.SOURCE ? regionalJob.getSourceRegion() : regionalJob.getTargetRegion();
            String execLegStr = regionalJob.getExecLeg().toString();
            List<Transformations> filteredTransforms = request.getTransformations().stream()
                    .filter(t -> t.getRegion() != null && t.getExecLeg() != null)
                    .filter(t -> t.getRegion().equals(region) && t.getExecLeg().equalsIgnoreCase(execLegStr))
                    .collect(Collectors.toList());
            regionalJob.setTransformContext(filteredTransforms);
        }


        regionalJob.setUpdatedBy(Constants.SYSTEM);
        regionalJob.setUpdatedAt(LocalDateTime.now());
        // Save the regional job configuration
        regionalJobConfigRepository.save(regionalJob);
    }

    private String getInputAdaptorId(List<ExecType> execType, ExecutionLeg execLeg) {
        if (execType == null || execType.isEmpty()) {
            return null;
        }
        switch (execLeg) {
            case SOURCE://either case input adaptor is database
                if (execType.contains(ExecType.REAL_TIME)|| execType.contains(ExecType.BATCH)) {
                    return AdaptorType.DATABASEADAPTOR.name();
                }
                break;
            case TARGET:
                if (execType.contains(ExecType.REAL_TIME)) {
                    return AdaptorType.STREAMADAPTOR.name();
                } else if (execType.contains(ExecType.BATCH)) {
                    return AdaptorType.FILEADAPTOR.name();
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid job type: " + execLeg);
        }
        return AdaptorType.DATABASEADAPTOR.name();
    }

    private String getOutputAdaptorId(List<ExecType> execType, ExecutionLeg execLeg) {
        if (execType == null || execType.isEmpty()) {
            return null;
        }
        switch (execLeg) {
            case SOURCE:
                if (execType.contains(ExecType.REAL_TIME)) {
                    return AdaptorType.STREAMADAPTOR.name();
                } else if (execType.contains(ExecType.BATCH)) {
                    return AdaptorType.FILEADAPTOR.name();
                }
                break;
            case TARGET://either case output adaptor is database
                if (execType.contains(ExecType.REAL_TIME) || execType.contains(ExecType.BATCH)) {
                    return AdaptorType.DATABASEADAPTOR.name();
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid job type: " + execLeg);
        }
        return AdaptorType.DATABASEADAPTOR.name();
    }


    public ResponseEntity<?> searchJobConfiguration(
            int page, int size, String sort, String filter)
            throws DataSyncJobException {
        try {

            ApiCriteria<DatasyncJobConfiguration> criteria = apiSupport.getCriteriaHolder(DatasyncJobConfiguration.class);
            final Specification<DatasyncJobConfiguration> spec = criteria.getSpecification();
            Page<DatasyncJobConfiguration> datasyncJobConfigurationPage= datasyncJobConfigRepository.findAll(spec, criteria.getPageable());


            ApiCollection<DatasyncJobConfigurationRequestDTO> responseList = apiCollection.from(datasyncJobConfigurationPage).mapItems(entity -> {
                // Use convertToDto to ensure source is mapped correctly
                DatasyncJobConfigurationRequestDTO dto = convertToDto(entity);
                if (entity.getExecType() != null && !entity.getExecType().isEmpty()) {
                    List<ExecType> execTypeList = Arrays.stream(entity.getExecType().split(","))
                            .map(ExecType::valueOf)
                            .collect(Collectors.toList());
                    dto.setExecType(execTypeList);
                }

                Set<RegionalJobConfiguration> regionalJobConfiguration = regionalJobConfigRepository.findByDatasyncJobIdAndIsActive(dto.getId(), true);
                List<TargetRegionDTO> targets = regionalJobConfiguration.stream()
                        .filter(rjc -> rjc.getExecLeg() == ExecutionLeg.TARGET)
                        .map(rjc -> {
                            TargetRegionDTO target = new TargetRegionDTO();
                            target.setRegion(rjc.getTargetRegion());
                            target.setRegionalTenantId(rjc.getTargetTenantId());
                            return target;
                        })
                        .collect(Collectors.toList());
                dto.setTargets(targets);
                log.info("Regional Job Configuration size: ", regionalJobConfiguration,dto.getId());
                List<Transformations> allTransformFuncConfigs = regionalJobConfiguration.stream()
                        .filter(regionalJob -> regionalJob.getTransformContext() != null)
                        .flatMap(regionalJob -> regionalJob.getTransformContext().stream())
                        .collect(Collectors.toList());

                // Assign to DTO
                dto.setTransformations(allTransformFuncConfigs);

                // Fetch and set activities
                List<DataSyncActivityConfig> activities = datasyncActivityRepository.findByDatasyncJobSysName(entity.getSystemName());
                List<ActivityDTO> activityDTOs = activities.stream()
                        .map(activity -> {
                            ActivityDTO activityDTO = new ActivityDTO();
                            activityDTO.setSysName(activity.getActivitySysName());
                            activityDTO.setActivityId(activity.getActivityId());
                            activityDTO.setExecType(activity.getExecType());
                            activityDTO.setExecSeq(activity.getExecSeq());
                            activityDTO.setEventType(activity.getEventType());
                            activityDTO.setActivityType(activity.getActivityType());
                            return activityDTO;
                        })
                        .collect(Collectors.toList());
                dto.setActivities(activityDTOs);

                return dto;
            });


            return ResponseEntity.status(HttpStatus.OK).body(responseList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new DataSyncJobException("Failed to fetch MetaObjects", "INTERNAL_SERVER_ERROR"));
        }
    }

    /**
     * Insert rows for new regions, update tenant id if region already exists.
     * Audit fields removed (target_regions table has no audit columns).
     */
    private void persistTargetRegions(String datasyncJobSysName, List<TargetRegionDTO> targets) {
        if (targets == null || targets.isEmpty()) return;
        Map<String, TargetRegion> existing = targetRegionRepository.findByDatasyncJobSysName(datasyncJobSysName)
                .stream().collect(Collectors.toMap(TargetRegion::getTargetRegion, tr -> tr));
        for (TargetRegionDTO dto : targets) {
            if (dto.getRegion() == null) continue;
            String region = dto.getRegion().trim();
            if (region.isEmpty()) continue;
            TargetRegion entity = existing.get(region);
            if (entity == null) {
                entity = new TargetRegion();
                entity.setTargetRegion(region);
                entity.setDatasyncJobSysName(datasyncJobSysName);
                entity.setUpdatedBy(Constants.SYSTEM);
                entity.setCreatedBy(Constants.SYSTEM);
                entity.setCreatedAt(LocalDateTime.now());
            }
            // Always update tenant id (latest wins)
            entity.setRegionTenantId(dto.getRegionalTenantId());
            entity.setUpdatedAt(LocalDateTime.now());
            targetRegionRepository.save(entity);
        }
    }

    /**
     * Ensure target_regions matches provided target list:
     * - Delete regions removed from list
     * - Insert new / update existing tenant ids
     */
    private void synchronizeTargetRegions(String jobName, List<TargetRegionDTO> newTargets) {
        List<TargetRegion> existingList = targetRegionRepository.findByDatasyncJobSysName(jobName);
        Set<String> newRegions = newTargets == null ? Collections.emptySet() :
                newTargets.stream()
                        .filter(t -> t.getRegion() != null)
                        .map(t -> t.getRegion().trim())
                        .filter(r -> !r.isEmpty())
                        .collect(Collectors.toSet());

        // Delete removed
        for (TargetRegion tr : existingList) {
            if (!newRegions.contains(tr.getTargetRegion())) {
                targetRegionRepository.delete(tr);
            }
        }
        // Insert / update
        persistTargetRegions(jobName, newTargets);
    }

    public void validateCuratedData(DatasyncJobConfigurationRequestDTO request) throws DataSyncJobException {
        // For job_type as customer the customer_tenant_sys_name, source_tenant_id and target_tenant_id (to have atleast one) is mandatory
        if (request.getJobType() == JobType.CUSTOMER) {
            if (StringUtils.isBlank(request.getCustomerTenantSysName())) {
                throw new DataSyncJobException("Customer Tenant System Name is mandatory for CUSTOMER job type", Constants.BAD_REQUEST);
            }

            if (request.getSource() == null || StringUtils.isBlank(request.getSource().getRegion())
                    || StringUtils.isBlank(request.getSource().getRegionalTenantId())) {
                throw new DataSyncJobException("Source region Tenant ID is mandatory for CUSTOMER job type", Constants.BAD_REQUEST);
            }

            if (request.getTargets() == null || request.getTargets().isEmpty()) {
                throw new DataSyncJobException("At least one target region is required for CUSTOMER job type", Constants.BAD_REQUEST);
            } else {
                for (TargetRegionDTO target : request.getTargets()) {
                    if (StringUtils.isBlank(target.getRegion()) || StringUtils.isBlank(target.getRegionalTenantId())) {
                        throw new DataSyncJobException("Each target region must have a valid region name and tenant id for CUSTOMER job type", Constants.BAD_REQUEST);
                    }
                }
            }
        } else if (request.getJobType() == JobType.PLATFORM) {
            // PLATFORM: keep customerTenantId if provided; null out customerId and clientId only
            request.setCustomerTenantSysName(null);
        } else {
            throw new DataSyncJobException("Invalid job type", Constants.DATASYNC_JOB_BAD_REQUEST);
        }
    }

    /**
     * Refactored: Validate DataSync request against customer tenant + regional tenant mapping.
     * Logic:
     * 1. Source region must match customerTenant.homeRegion (direct field).
     * 2. Apply region mapping: AMER→DEV2, EMEA→LAB.
     * 3. For each regional_tenant:
     *    - If isHomeRegion is true, validate regional_tenant_id from request matches tenant_code.
     *    - If isHomeRegion is false, validate each target's region/tenant using mapped region and tenant_code.
     */
    private void validateCustomerTenantConstraints(DatasyncJobConfigurationRequestDTO request) throws DataSyncJobException {
        CustomerTenant tenant = fetchCustomerTenant(request.getCustomerTenantSysName());
        if (tenant == null) {
            throw new DataSyncJobException("Customer tenant not found for system name: " + request.getCustomerTenantSysName(), BAD_REQUEST);
        }
        if (StringUtils.isBlank(tenant.getHomeRegion())) {
            throw new DataSyncJobException("Customer tenant missing home_region", BAD_REQUEST);
        }
        if (StringUtils.isBlank(request.getSource().getRegion())) {
            throw new DataSyncJobException("Source region is required for UGE customer validation", BAD_REQUEST);
        }
        // Region mapping
        Map<String, String> regionMap = new HashMap<>();
        regionMap.put("AMER", "DEV2");
        regionMap.put("EMEA", "LAB");

        // Apply region mapping for homeRegion and sourceRegion comparison
        String mappedHomeRegion = regionMap.getOrDefault(tenant.getHomeRegion().toUpperCase(Locale.ROOT), tenant.getHomeRegion());
        String mappedSourceRegion = regionMap.getOrDefault(request.getSource().getRegion().toUpperCase(Locale.ROOT), request.getSource().getRegion());
        if (!mappedHomeRegion.equalsIgnoreCase(mappedSourceRegion)) {
            throw new DataSyncJobException("Source region must match customer tenant homeRegion (after mapping): " + tenant.getHomeRegion(), BAD_REQUEST);
        }

        // Validate regional_tenants
        if (tenant.getRegionalTenants() == null || tenant.getRegionalTenants().isEmpty()) {
            throw new DataSyncJobException("Customer tenant missing regional_tenants", BAD_REQUEST);
        }

        for (RegionalTenant rt : tenant.getRegionalTenants()) {
            boolean isHome = Boolean.TRUE.equals(rt.getHomeRegionFlag());
            String mappedRegion = regionMap.getOrDefault(request.getSource().getRegion().toUpperCase(Locale.ROOT), request.getSource().getRegion());
            if (isHome) {
                // Validate home region tenant_code
                if (!Objects.equals(rt.getTenantCode(), request.getSource().getRegionalTenantId())) {
                    throw new DataSyncJobException("regional_tenant_id does not match home region tenant tenant_code", BAD_REQUEST);
                }
            } else {
                // Validate each target against mapped region and tenant_code
                if (request.getTargets() != null) {
                    boolean foundTarget = false;
                    for (TargetRegionDTO target : request.getTargets()) {
                        String targetMappedRegion = regionMap.getOrDefault(target.getRegion().toUpperCase(Locale.ROOT), target.getRegion());
                        if (targetMappedRegion.equalsIgnoreCase(rt.getRegion())) {
                            foundTarget = true;
                            if (!Objects.equals(rt.getTenantCode(), target.getRegionalTenantId())) {
                                throw new DataSyncJobException("Target region tenant code mismatch for region: " + target.getRegion(), BAD_REQUEST);
                            }
                        }
                    }
                    if (!foundTarget) {
                        throw new DataSyncJobException("Target region not present in customer tenant mapping: " + rt.getRegion(), BAD_REQUEST);
                    }
                }
            }
        }
    }

    private CustomerTenant fetchCustomerTenant(String systemName) throws DataSyncJobException {
        if (StringUtils.isBlank(systemName)) {
            return null;
        }
        try {
            OAuthTokenResponse token = oAuthService.getOAuthToken();
            if (StringUtils.isBlank(token.getAccessToken())) {
                throw new DataSyncJobException("Empty OAuth token response", BAD_REQUEST);
            }
            ResponseEntity<String> resp = customerService.getCustomerTenantsBySystemName(token.getAccessToken(), systemName);
            if (!resp.getStatusCode().is2xxSuccessful()) {
                throw new DataSyncJobException("Failed fetching customer tenant: HTTP " + resp.getStatusCode(), BAD_REQUEST);
            }
            return objectMapper.readValue(resp.getBody(), CustomerTenant.class);
        } catch (com.thomsonreuters.dataconnect.dataintegration.exceptionhandler.OAuthTokenRetrievalException e) {
            throw new DataSyncJobException("Failed to acquire OAuth token: " + e.getMessage(), BAD_REQUEST);
        } catch (DataSyncJobException e) {
            throw e;
        } catch (Exception e) {
            throw new DataSyncJobException("Error parsing customer tenant response: " + e.getMessage(), BAD_REQUEST);
        }
    }

    private void validateActivities(List<ActivityDTO> activities, List<ExecType> allowedExecTypes) throws DataSyncJobException {
        if (activities == null) {
            return;
        }
        Set<String> execTypeNames = allowedExecTypes == null ? Collections.emptySet() :
                allowedExecTypes.stream().map(Enum::name).collect(Collectors.toSet());
        for (ActivityDTO a : activities) {
            if (a.getSysName() == null || a.getSysName().trim().isEmpty()) {
                throw new DataSyncJobException("Activity sys_name cannot be blank", BAD_REQUEST);
            }
            if (a.getActivityId() == null || a.getActivityId() <= 0) {
                throw new DataSyncJobException("Activity activity_id must be > 0", BAD_REQUEST);
            }
            if (a.getExecSeq() == null || a.getExecSeq() <= 0) {
                throw new DataSyncJobException("Activity exec_seq must be > 0", BAD_REQUEST);
            }
            if (a.getExecType() == null || a.getExecType().trim().isEmpty()) {
                throw new DataSyncJobException("Activity exec_type is required", BAD_REQUEST);
            }
            if (!execTypeNames.isEmpty()) {
                String actExecTypeUpper = a.getExecType().toUpperCase(Locale.ROOT);
                if (!actExecTypeUpper.equals("SOURCE") && !actExecTypeUpper.equals("TARGET")
                        && !execTypeNames.contains(actExecTypeUpper)) {
                    throw new DataSyncJobException("Activity exec_type must be one of job exec_type list or SOURCE/TARGET", BAD_REQUEST);
                }
            }
            if (a.getEventType() == null || a.getEventType().trim().isEmpty()) {
                throw new DataSyncJobException("Activity event_type is required", BAD_REQUEST);
            }
            if (a.getActivityType() == null || a.getActivityType().trim().isEmpty()) {
                throw new DataSyncJobException("Activity activity_type is required", BAD_REQUEST);
            }
        }
    }

    public MetaObject getMetaObjectDetails(String metaObjectSysName) throws DataSyncJobException {
        return metaObjectRepository.findBySystemName(metaObjectSysName)
                .orElseThrow(() -> new DataSyncJobException("Meta Object not found", Constants.DATASYNC_JOB_BAD_REQUEST));
    }
}
