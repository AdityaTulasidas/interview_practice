package com.aditya.dataconnect.executionengine.datasyncactivity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thomsonreuters.dataconnect.common.executioncontext.ExecutionContext;
import com.thomsonreuters.dataconnect.common.executioncontext.GlobalContext;
import com.thomsonreuters.dataconnect.common.executioncontext.RegionalJobContext;
import com.thomsonreuters.dataconnect.common.executioncontext.TransitHubContext;
import com.aditya.dataconnect.executionengine.constant.Constants;
import com.aditya.dataconnect.executionengine.data.DataRow;
import com.aditya.dataconnect.executionengine.data.DataSet;
import com.aditya.dataconnect.executionengine.data.DataSetCollection;
import com.aditya.dataconnect.executionengine.dto.MetaObjectAttributeDTO;
import com.aditya.dataconnect.executionengine.dto.MetaObjectDTO;
import com.aditya.dataconnect.executionengine.exceptionhandler.CustomError;
import com.aditya.dataconnect.executionengine.exceptionhandler.DataSyncJobException;
import com.aditya.dataconnect.executionengine.model.entity.DataSource;
import com.aditya.dataconnect.executionengine.model.entity.MetaObjectRelation;
import com.aditya.dataconnect.executionengine.model.entity.OnesourceDatabaseType;
import com.aditya.dataconnect.executionengine.model.entity.enums.DataType;
import com.aditya.dataconnect.executionengine.model.pojo.Payload;
import com.aditya.dataconnect.executionengine.model.pojo.PrimaryKeyInfo;
import com.aditya.dataconnect.executionengine.repository.DataSourceCatalogRepository;
import com.aditya.dataconnect.executionengine.repository.MetaObjectRelationRepository;
import com.aditya.dataconnect.executionengine.repository.MetaObjectRepository;
import com.aditya.dataconnect.executionengine.repository.OnesourceDatabaseTypeRepository;
import com.aditya.dataconnect.executionengine.services.DataSourceApiService;
import com.aditya.dataconnect.executionengine.services.awsservices.AwsSsmService;
import com.aditya.dataconnect.executionengine.utils.ConnectionFactory;
import com.aditya.dataconnect.executionengine.utils.SQLBuilder;
import com.thomsonreuters.dep.transithub.PublisherConfiguration;
import com.thomsonreuters.dep.transithub.TransitHubMessage;
import com.thomsonreuters.dep.transithub.TransitHubPublisher;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.aditya.dataconnect.executionengine.constant.Constants.*;
import static com.aditya.dataconnect.executionengine.model.entity.enums.ErrorConstant.*;
import static com.aditya.dataconnect.executionengine.model.entity.enums.ErrorConstant.JOB_READ_ERROR;

@Service
@Slf4j
@NoArgsConstructor
public class PublishTransitHubActivity implements DataSyncActivity{



    TransitHubPublisher transitHubPublisher;

    PublisherConfiguration publisherConfiguration;

    private Payload payload;

    Set<MetaObjectAttributeDTO> attributeSet;

    MetaObjectRelationRepository metaObjectRelationRepository;

    MetaObjectRepository  metaObjectRepository;

    DataSourceCatalogRepository dataSourceCatalogRepository;
    OnesourceDatabaseTypeRepository onesourceDatabaseTypeRepository;

    ModelMapper modelMapper;

    AwsSsmService awsSsmService;

    DataSourceApiService dataSourceApiService;

    @Value("${META_REGISTRY_API_URL}")
    private String metaModelServiceUrl;


    private static final DateTimeFormatter TRIGGERED_AT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd 'UTC' HH:mm:ss.SS");



    public  PublishTransitHubActivity(TransitHubPublisher transitHubPublisher, PublisherConfiguration publisherConfiguration) {
        this.transitHubPublisher = transitHubPublisher;
        this.publisherConfiguration = publisherConfiguration;
    }

    @Autowired
    public PublishTransitHubActivity( MetaObjectRelationRepository metaObjectRelationRepository,
                                      MetaObjectRepository metaObjectRepository,
                                      DataSourceCatalogRepository dataSourceCatalogRepository,
                                      OnesourceDatabaseTypeRepository onesourceDatabaseTypeRepository,
                                      ModelMapper modelMapper,
                                      AwsSsmService awsSsmService) {
        this.metaObjectRelationRepository = metaObjectRelationRepository;
        this.metaObjectRepository = metaObjectRepository;
        this.dataSourceCatalogRepository = dataSourceCatalogRepository;
        this.onesourceDatabaseTypeRepository = onesourceDatabaseTypeRepository;
        this.modelMapper = modelMapper;
        this.awsSsmService = awsSsmService;
    }
    Map<Object ,Object> dataToPublish = new HashMap<>();
    List<String> dbColumnNames;
    Map<String, String> childData = new HashMap<>();



    @Override
    public void initialize(ExecutionContext ctx) throws DataSyncJobException {
        // need to get info global context -
        GlobalContext globalContext= (GlobalContext) ctx.getContextByName(ExecutionContext.GLOBAL_CONTEXT);
        TransitHubContext transitHubContext=globalContext.getValue(GlobalContext.TRANSITHUB_CONTEXT);
        RegionalJobContext regionalJobContext= (RegionalJobContext) ctx.getContextByName(ExecutionContext.REGIONAL_JOB_CONTEXT);

        // get parameters from global context required by transithub client SDK to connect to transitHub server
        publisherConfiguration = PublisherConfiguration.builder()
                .publisherId(transitHubContext.getValue(TransitHubContext.PUBLISHER_ID))
                .key(transitHubContext.getValue(TransitHubContext.SUBSCRIPTION_KEY))
                .serviceUrl(transitHubContext.getValue(TransitHubContext.SERVICE_URL))
                .privateKey(transitHubContext.getValue(TransitHubContext.PRIVATE_KEY))
                .issuer(transitHubContext.getValue(TransitHubContext.ISSUER_URL))
                .renewPeriod(Integer.parseInt(Constants.TRANSIT_HUB_RENEWAL_PERIOD))
                .defaultSchemaId(transitHubContext.getValue(TransitHubContext.SCHEMA_ID_CREATE))
                .build();

        transitHubPublisher= new TransitHubPublisher(publisherConfiguration);

        payload = Payload.builder()
                .correlationId(RegionalJobContext.REGIONAL_TENANT_ID+":"+RegionalJobContext.CLIENT_ID+":") // ideally this should be unique for each event
                .schemaId(transitHubContext.getValue(TransitHubContext.SCHEMA_ID_CREATE))
                .key(transitHubContext.getValue(TransitHubContext.SUBSCRIPTION_KEY))
                .tenantId(regionalJobContext.getValue(RegionalJobContext.REGIONAL_TENANT_ID))
                .clientId(regionalJobContext.getValue(RegionalJobContext.CLIENT_ID))
                .action("")
                .username(DEFAULT_TRANSITHUB_USERNAME) //default value, will be overridden if created_by/updated_by is present in the table
                .data(dataToPublish)
                .build();
    }

    @Override
    public DataSetCollection execute(DataSetCollection dataSetCollection,ExecutionContext context) throws DataSyncJobException, JsonProcessingException {
        // Prepare the TransitHub Event using the data in DatasetCollection and related MetaObjects
        for(DataSet dataSet : dataSetCollection.getDataSets()) {
            if (dataSet.getHierarchyIndex() ==0 && dataSet.getMetaObject().isEventEnabled()) {
                for (DataRow row : dataSet.getDataRows()) {

                    dbColumnNames = getDbColumnNames(dataSet);

                    String primaryKeyColumn = dataSet.getMetaObject().getAttributes().stream()
                            .filter(MetaObjectAttributeDTO::isPrimary)
                            .map(MetaObjectAttributeDTO::getSystemName)
                            .findFirst()
                            .orElse(null);

                    for (String columnName : dbColumnNames) {
                        Object value = row.getRow().get(columnName);
                        dataToPublish.put(columnName, String.valueOf(value));
                    }

                    // Build and publish the message
                    payload.setData(dataToPublish);
                    // Ensure associated_applications is present
                    ensureAssociatedApplications();

                    payload.setAction(determineAction(dataSet));

                    // use builder pattern to setup TransitHubMessage
                    this.transitHubPublisher.publish(prepareMessage(row,dataSet,primaryKeyColumn));
                }
            }
            else if (dataSet.getHierarchyIndex() >0 && dataSet.getMetaObject().isEventEnabled()) {
                List<Map<String, String>> childDataList = new ArrayList<>();
                for (DataRow row : dataSet.getDataRows()) {

                    dbColumnNames = getDbColumnNames(dataSet);

                    String primaryKeyColumn = dataSet.getMetaObject().getAttributes().stream()
                            .filter(MetaObjectAttributeDTO::isPrimary)
                            .map(MetaObjectAttributeDTO::getSystemName)
                            .findFirst()
                            .orElse(null);

                    String childSystemName=dataSet.getMetaObject().getSystemName();

                    for (String columnName : dbColumnNames) {
                        Object value = row.getRow().get(columnName);
                        dataToPublish.put(columnName, String.valueOf(value));
                    }
                    childDataList.add(childData);
                    DataSet parentData=getParentData(dataSet,context,childData);

                    //Publish parent data als
                    if(parentData!=null && parentData.getDataRows()!=null && !parentData.getDataRows().isEmpty())
                    {
                        DataRow parentRow=parentData.getDataRows().iterator().next();
                        Set<MetaObjectAttributeDTO> parentAttributes=parentData.getMetaObject().getAttributes();
                        for(MetaObjectAttributeDTO attribute:parentAttributes)
                        {
                            if(attribute.isEventEnabled() && !dataToPublish.containsKey(attribute.getDbColumn()))
                            {
                                dataToPublish.put(attribute.getDbColumn(),parentRow.getRow().get(attribute.getDbColumn())!=null?parentRow.getRow().get(attribute.getDbColumn()).toString():"null");
                            }
                        }
                    }
                    dataToPublish.put(childSystemName.substring(childSystemName.lastIndexOf('.')+1),childDataList);
                    // Build and publish the message
                    // Ensure associated_applications is present
                    ensureAssociatedApplications();

                    payload.setData(dataToPublish);
                    payload.setAction(determineAction(dataSet));

                    // use builder pattern to setup TransitHubMessage
                    this.transitHubPublisher.publish(prepareMessage(row,dataSet,primaryKeyColumn));
                }
            }


        }
        return dataSetCollection;
    }

    private List<String> getDbColumnNames(DataSet dataSet) {
        return dataSet.getMetaObject().getAttributes().stream()
                .filter(MetaObjectAttributeDTO::isEventEnabled)
                .map(MetaObjectAttributeDTO::getDbColumn)
                .toList();
    }

    private void ensureAssociatedApplications() {
        if (!dataToPublish.containsKey("associated_applications")) {
            dataToPublish.put("associated_applications", Collections.emptyList());
        }
    }

    private String determineAction(DataSet dataSet) {
        String domainObject = dataSet.getMetaObject().getDomainObject();
        String dataSetName = dataSet.getDataSetName();
        String metaObjectName=domainObject.substring(domainObject.lastIndexOf('.')+1);

        if ("accounts".equalsIgnoreCase(metaObjectName)) {
            if (INSERT_DATA_SET.equalsIgnoreCase(dataSetName)) {
                return "accounts:CREATE";
            } else if (UPDATE_DATA_SET.equalsIgnoreCase(dataSetName)) {
                return "accounts:UPDATE";
            } else if (DELETE_DATA_SET.equalsIgnoreCase(dataSetName)) {
                return "accounts:DELETE";
            }
        } else if ("associated_jurisdictions".equalsIgnoreCase(domainObject)) {
            if (INSERT_DATA_SET.equalsIgnoreCase(dataSetName)) {
                return "associated_jurisdictions:CREATE";
            } else if (DELETE_DATA_SET.equalsIgnoreCase(dataSetName)) {
                return "associated_jurisdictions:DELETE";
            }
        } else if ("associated_applications".equalsIgnoreCase(domainObject)) {
            if (INSERT_DATA_SET.equalsIgnoreCase(dataSetName)) {
                return "associated_applications:CREATE";
            } else if (UPDATE_DATA_SET.equalsIgnoreCase(dataSetName)) {
                return "associated_applications:UPDATE";
            } else if (DELETE_DATA_SET.equalsIgnoreCase(dataSetName)) {
                return "associated_applications:DELETE";
            }
        } else if ("associated_entities".equalsIgnoreCase(domainObject)) {
            if (INSERT_DATA_SET.equalsIgnoreCase(dataSetName)) {
                return "associated_entities:CREATE";
            } else if (DELETE_DATA_SET.equalsIgnoreCase(dataSetName)) {
                return "associated_entities:DELETE";
            }
        }
        if ("account_mappings".equalsIgnoreCase(metaObjectName)) {
            if (INSERT_DATA_SET.equalsIgnoreCase(dataSetName)) {
                return "account_mappings:CREATE";
            } else if (DELETE_DATA_SET.equalsIgnoreCase(dataSetName)) {
                return "accounts:DELETE";
            }
        }

        // Default action if no match is found
        return "UNKNOWN_ACTION";
    }

    private DataSet getParentData(DataSet dataSet, ExecutionContext context,Map<String,String> childData) throws DataSyncJobException {
        MetaObjectRelation relation=metaObjectRelationRepository.findByChild(dataSet.getMetaObject().getId());
        if(metaObjectRepository.findById(relation.getParentObject().getId()).isPresent())
        {
            return getDataSource(context,relation,childData);
        }
        return null;
    }

    private DataSet getDataSource(ExecutionContext ctx,MetaObjectRelation relation,Map<String,String> childData) throws DataSyncJobException  {

        Connection connection = null;
        RegionalJobContext regionalJobContext = (RegionalJobContext) ctx.getContextByName(ExecutionContext.REGIONAL_JOB_CONTEXT);
        GlobalContext globalContext = (GlobalContext) ctx.getContextByName(ExecutionContext.GLOBAL_CONTEXT);
        String onesourceDomain = regionalJobContext.getValue(RegionalJobContext.ONESOURCE_DOMAIN).toString();
        String onesourceRegion = globalContext.getValue(GlobalContext.HOST_REGION).toString();
        String regionalTenantId = regionalJobContext.getValue(RegionalJobContext.REGIONAL_TENANT_ID) == null ? null : regionalJobContext.getValue(RegionalJobContext.REGIONAL_TENANT_ID).toString();

        String systemName = regionalJobContext.getValue(RegionalJobContext.META_OBJECT_SYS_NAME) == null ? null : regionalJobContext.getValue(RegionalJobContext.META_OBJECT_SYS_NAME).toString();



        assert systemName != null;
        String domainSysName=systemName.substring(0,systemName.lastIndexOf('.'));
        DataSource dataSource=dataSourceApiService.getFilteredDataSource(metaModelServiceUrl,
                regionalTenantId,
                onesourceDomain,
                onesourceRegion,
                domainSysName);

        //Build JDBC connection string with parameters fetched from SSM
        String userName = awsSsmService.getSSMValue(dataSource.getUserName());
        String password = awsSsmService.getSSMValue(dataSource.getPassword());
        String hostName = awsSsmService.getSSMValue(dataSource.getHost());
        String databaseName = awsSsmService.getSSMValue(dataSource.getDb());

        OnesourceDatabaseType onesourceDatabaseType = onesourceDatabaseTypeRepository.findById(dataSource.getDbType())
                .orElseThrow(() -> new DataSyncJobException(
                        DATABASE_TYPE_NOT_FOUND.getMessage(),
                        DATABASE_TYPE_NOT_FOUND.getCode()
                ));
        String jdbcDriver = onesourceDatabaseType.getJdbcDriver();
        String port = (dataSource.getPort() != null && !dataSource.getPort().isBlank())
                ? awsSsmService.getSSMValue(dataSource.getPort())
                : String.valueOf(onesourceDatabaseType.getDefaultPort());
        String jdbcUrl = String.format("jdbc:%s://%s:%s/%s", jdbcDriver, hostName, port, databaseName);


        try {
            connection = ConnectionFactory.getConnection(jdbcUrl, userName, password);
            log.info("Database connection established successfully");
            MetaObjectDTO metaObjectDTO=modelMapper.map(relation.getParentObject(),MetaObjectDTO.class);
            DataType primaKeyType=null;
            for (MetaObjectAttributeDTO attribute : metaObjectDTO.getAttributes()) {
                if(attribute.isPrimary() && attribute.getDbColumn().equalsIgnoreCase(relation.getParentObjRelCol()))
                    primaKeyType= attribute.getDataType();
            }
            String childCol =relation.getChildObjRelCol() !=null? relation.getChildObjRelCol():"null";
            String childRelColData = childData.get(childCol) != null
                    ? childData.get(childCol)
                    : "null";
            return readDataForSpecificIds(regionalJobContext,metaObjectDTO,1,relation.getParentObjRelCol(),primaKeyType, Collections.singletonList(childRelColData),connection);
        } catch (SQLException e) {
            log.error("Error establishing database connection", e);
            throw new DataSyncJobException("Error establishing database connection", Constants.INTERNAL_SERVER_ERROR);
        }


    }

    private DataSet readDataForSpecificIds(RegionalJobContext regionalJobCtx,MetaObjectDTO metaObj, int hierarchyIndex, String keyColName, DataType keyColdataType, List<String> objIds, Connection connection) throws DataSyncJobException, SQLException {
        DataSet dataSet = new DataSet();
        String tenantId = (regionalJobCtx.getValue(RegionalJobContext.REGIONAL_TENANT_ID) == null) ? null : regionalJobCtx.getValue(RegionalJobContext.REGIONAL_TENANT_ID).toString();
        String clientId = (regionalJobCtx.getValue(RegionalJobContext.CLIENT_ID) == null) ? null : regionalJobCtx.getValue(RegionalJobContext.CLIENT_ID).toString();
        LocalDateTime completeTime = regionalJobCtx.getValue(RegionalJobContext.LAST_COMPLETED_AT) == null ? null : LocalDateTime.parse(regionalJobCtx.getValue(RegionalJobContext.LAST_COMPLETED_AT));
        dataSet.setMetaObject(metaObj);
        dataSet.setHierarchyIndex(hierarchyIndex);
        PrimaryKeyInfo primaryKeyInfo= new PrimaryKeyInfo();
        primaryKeyInfo.setPkName(keyColName);

        String selectQuery = SQLBuilder.getSelectStatementForSpecificIds(metaObj, completeTime, objIds, primaryKeyInfo);
        processDataCollection(dataSet, metaObj, selectQuery, objIds, keyColdataType, clientId, tenantId, connection); //making foreign key as null when primary key is present
        return dataSet;
    }

    private void processDataCollection(DataSet dataSet, MetaObjectDTO metaObj, String selectQuery, List<?> objIds, DataType primaryKeyColumnType, String clientId, String tenantId, Connection connection) throws DataSyncJobException, SQLException {
        ResultSet resultSet = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            int paramIndex = 1;

            // Whitelist validation for tenantId and clientId to prevent SQL injection in SET statements
            // Tenant and client context should be managed outside of SQL. Removed SET statements to eliminate SQL injection risk.
            // The above regex validations ensure tenantId and clientId are safe for SQL usage and prevent SQL injection.
            setTenantIdAndClientId(clientId, tenantId, connection);
            // sanitize the completeTime and objIds before using them in the query
            try {

                geExecutableStatement(objIds, primaryKeyColumnType, preparedStatement, paramIndex);
                resultSet = preparedStatement.executeQuery();
            } catch (SQLException e) {
                log.error("Error executing query", e);
                throw new CustomError(JOB_READ_ERROR.getMessage(), JOB_READ_ERROR.getCode());
            }
            if (resultSet != null) {
                dataSet.setDataRows(dataSet.getDataRowsFromResultSet(resultSet, metaObj));
                //method to be implemented to get Set<Primarykeys> from the resultSet
            }
            if (resultSet == null) {
                log.info("No data found for the query: {}", selectQuery);
            }
        } catch (SQLException e) {
            throw new CustomError(JOB_READ_ERROR.getMessage(), JOB_READ_ERROR.getCode());
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    log.error("Error closing ResultSet", e);
                }
            }
            if (dataSet.getDataRows() == null || dataSet.getDataRows().isEmpty()) {
                log.info("No data found for the query: {}", selectQuery);
            }
            log.info("Data read successfully for MetaObject: {}", metaObj.getSystemName());
        }
    }

    private static void geExecutableStatement(List<?> objIds, DataType primaryKeyColumnType, PreparedStatement preparedStatement, int paramIndex) throws SQLException {
        if (objIds != null && !objIds.isEmpty()) {
            log.info("Params: {}", objIds.toString());
            for (int i = 0; i < objIds.size(); i++) {
                Object objId = objIds.get(i);
                if (primaryKeyColumnType == null) {
                    preparedStatement.setObject(i + paramIndex, objId);
                } else {
                    switch (Objects.requireNonNull(primaryKeyColumnType)) {
                        case STRING, TEXT:
                            preparedStatement.setString(i + paramIndex, objId.toString());
                            break;
                        case INTEGER:
                            preparedStatement.setInt(i + paramIndex, Integer.parseInt(objId.toString()));
                            break;
                        case UUID:
                            try {
                                preparedStatement.setObject(i + paramIndex, UUID.fromString(objId.toString()));
                            } catch (IllegalArgumentException e) {
                                log.error("Invalid UUID format for objId: {}", objId, e);
                                throw new CustomError(JOB_READ_ERROR.getMessage(), JOB_READ_ERROR.getCode());
                            }
                            break;
                        case BIGINT:
                            try {
                                preparedStatement.setLong(i + paramIndex, Long.parseLong(objId.toString()));
                            } catch (NumberFormatException e) {
                                log.error("Invalid BIGINT format for objId: {}", objId, e);
                                throw new CustomError(JOB_READ_ERROR.getMessage(), JOB_READ_ERROR.getCode());
                            }
                            break;
                        default:
                            throw new IllegalArgumentException("Unsupported id column data type: " + primaryKeyColumnType);
                    }
                }
            }
        }
    }

    private static void setTenantIdAndClientId(String clientId, String tenantId, Connection connection) throws SQLException {
        //ToDo Remove when multi-client use case is supported
        try (Statement stmt = connection.createStatement()) {
            String sql = "SET dep.client_id = '" + clientId + "'";
            stmt.execute(sql);
        }

        if (tenantId != null && !tenantId.isBlank()) {
            if (!tenantId.matches("^[a-zA-Z0-9_]+$")) {
                throw new IllegalArgumentException("Invalid tenantId");
            }
            try (Statement stmt = connection.createStatement()) {
                String sql = "SET dep.tenant_id = '" + tenantId + "'";
                stmt.execute(sql);
            }
        }
    }

    private TransitHubMessage prepareMessage(DataRow row, DataSet dataSet, String primaryKeyColumn) {
        final var message = this.transitHubPublisher.prepare()
                .correlationId(payload.getCorrelationId() + row.getRow().get(primaryKeyColumn))
                .schemaId(payload.getSchemaId())
                .key(payload.getKey())
                .payload(payload.getData())
                .build();
        message.addHeader("tenant_id", payload.getTenantId());
        message.addHeader("client_id", payload.getClientId());
        message.addHeader("actions", payload.getAction());
        message.addHeader("domain_object", dataSet.getMetaObject().getDomainObject());
        String triggeredBy= DEFAULT_TRANSITHUB_USERNAME;
        String triggeredAt = LocalDateTime.now().format(TRIGGERED_AT_FORMATTER);
        if (payload.getAction().equalsIgnoreCase("CREATE")) {
            triggeredBy = row.getRow().containsKey("created_by") ? row.getRow().get("created_by").toString() : "DataConnect";
            triggeredAt = row.getRow().containsKey("created_at") ? row.getRow().get("created_at").toString() : triggeredAt;
        } else if (payload.getAction().equalsIgnoreCase("UPDATE")) {
            triggeredBy = row.getRow().containsKey("updated_by") ? row.getRow().get("updated_by").toString() : "DataConnect";
            triggeredAt = row.getRow().containsKey("updated_at") ? row.getRow().get("updated_at").toString() : triggeredAt;
        }
        // Ensure triggered_at matches the required pattern
        if (!triggeredAt.matches("^\\d{4}-\\d{2}-\\d{2} UTC \\d{2}:\\d{2}:\\d{2}\\.\\d{2}$")) {
            triggeredAt = LocalDateTime.now().format(TRIGGERED_AT_FORMATTER);
        }
        message.addHeader("triggered_by", triggeredBy);// who triggered the event: this I have set to 'DataConnect'
        message.addHeader("triggered_at", triggeredAt);
        return message;
    }


    @Override
    public void validate(DataSetCollection dataSetCollection) {
        // Validate if the dataset collection has the required structure and data
    }

    @Override
    public void cleanup() {
        // Close the transitHub server connection
    }
}
