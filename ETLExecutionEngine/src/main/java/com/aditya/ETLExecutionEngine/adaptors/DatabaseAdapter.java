package com.aditya.ETLExecutionEngine.adaptors;

import com.aditya.ETLExecutionEngine.context.AdapterContext;
import com.aditya.ETLExecutionEngine.context.ExecutionContext;
import com.aditya.ETLExecutionEngine.context.GlobalContext;
import com.aditya.ETLExecutionEngine.context.RegionalJobContext;
import com.aditya.ETLExecutionEngine.data.DataSet;
import com.aditya.ETLExecutionEngine.data.DataSetCollection;
import com.aditya.ETLExecutionEngine.data.DataUnit;
import com.aditya.ETLExecutionEngine.data.DataUnitList;
import com.aditya.ETLExecutionEngine.exception.CustomError;
import com.aditya.ETLExecutionEngine.exception.DataSyncJobException;
import com.aditya.ETLExecutionEngine.model.dto.MetaObjectAttributeDTO;
import com.aditya.ETLExecutionEngine.model.dto.MetaObjectDTO;
import com.aditya.ETLExecutionEngine.model.dto.MetaRelationMetaModelDTO;
import com.aditya.ETLExecutionEngine.model.dto.MetaRelationModelDTO;
import com.aditya.ETLExecutionEngine.model.enums.DataType;
import com.aditya.ETLExecutionEngine.model.enums.OnesourceDomain;
import com.aditya.ETLExecutionEngine.model.enums.OnesourceRegion;
import com.aditya.ETLExecutionEngine.util.ConnectionFactory;
import com.aditya.ETLExecutionEngine.util.Constants;
import com.aditya.ETLExecutionEngine.util.SQLBuilder;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.aditya.ETLExecutionEngine.util.CommonAdaptersUtil.sanitizeAndValidateObjIds;
import static com.aditya.ETLExecutionEngine.util.Constants.*;

@Slf4j
public class DatabaseAdapter implements IntegrationAdapter {

    @Autowired
    protected MetaObjectRelationRepository metaObjectRelationRepository;

    @Autowired
    protected MetaObjectRepository metaObjectRepository;

    @Autowired
    protected ModelMapper modelMapper;

    @Autowired
    protected MetaModelClient metaModelClient;

    @Autowired
    CommonAdaptersUtil commonAdaptersUtil;

    @Autowired
    protected DataSourceCatalogRepository dataSourceCatalogRepository;

    protected ExecutionContext ctx;
    public static final String DATA_UNIT = "data_unit";
    private RegionalJobContext regionalJobCtx;

    @Autowired
    private AwsSsmService awsSsmService;

    @Autowired
    private OnesourceDatabaseTypeRepository onesourceDatabaseTypeRepository;

    @Override
    public void initialize(ExecutionContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void validate() {
        // Default implementation: No action required
    }

    @Override
    public DataSetCollection readData() throws DataSyncJobException {
        DataUnitList dataUnitList = null;
        int hierarchyIndex = 0;
        AdapterContext adapterContext = (AdapterContext) ctx.getContextByName(ExecutionContext.IN_ADAPTER_CONTEXT);
        DataUnit data = adapterContext.getValue(DATA_UNIT);
        List<String> objIds = null;
        if (data != null && data.getContent() != null && (data.getContent() instanceof DataUnitList)) {
            dataUnitList = (DataUnitList) data.getContent();
        }
        List<DataSet> dataSets = new ArrayList<>();
        try (Connection connection = getConnection()) {
            if (dataUnitList != null && dataUnitList.getObjIds() != null) {
                objIds = sanitizeAndValidateObjIds(dataUnitList.getObjIds());
            } else {
                log.warn("DataUnitList is null or empty, proceeding with no object IDs");
            }
            regionalJobCtx = (RegionalJobContext) ctx.getContextByName(ExecutionContext.REGIONAL_JOB_CONTEXT);
            UUID metaObjectId = UUID.fromString(regionalJobCtx.getValue(RegionalJobContext.META_OBJECT_ID).toString());
            MetaRelationMetaModelDTO metaRelationMetaModelDTO = commonAdaptersUtil.fetchMetaRelation(metaObjectId);

            if (metaRelationMetaModelDTO == null) {
                log.error("MetaRelationMetaModelDTO is null for MetaObject ID: {}", metaObjectId);
                throw new DataSyncJobException("MetaRelationMetaModelDTO not found for the given MetaObject ID", Constants.NOT_FOUND);
            }
            MetaObjectDTO parentMetaObject = metaRelationMetaModelDTO.getParentObject();
            Set<MetaObjectAttributeDTO> setAttributes = parentMetaObject.getAttributes();
            Map<String, Object> pkNameAndType = geColNameAndType(setAttributes, null);
            DataType pkdataType = getDataType(pkNameAndType, parentMetaObject);
            String pkName = getColName(pkNameAndType, parentMetaObject);
            processMetaObjectHierarchy(parentMetaObject, metaRelationMetaModelDTO, hierarchyIndex, pkName, pkdataType, objIds, dataSets, connection);
            log.info("Reading data from the database");
        } catch (DataSyncJobException e) {
            log.error("Error reading data from the database", e);
            throw new DataSyncJobException("Error reading data from the database", Constants.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("Unexpected error occurred while reading data from the database", e);
            throw new DataSyncJobException("Error reading data from the database", Constants.INTERNAL_SERVER_ERROR);
        }
        // Return a DataSetCollection containing all datasets
        DataSetCollection dataSetCollection = new DataSetCollection();
        dataSetCollection.setDataSets(dataSets);
        return dataSetCollection;
    }

    private List<String> getObjectIdsForReferenceMetaObject(MetaObjectDTO parentMetaObject, List<String> objIds, Connection connection, String keyColName, DataType specificColDataType, String specificColumn) throws SQLException, DataSyncJobException {
        List<String> objectIds = new ArrayList<>();
        try {
            String selectQuery = SQLBuilder.selectSpecificColumnsFromTable(parentMetaObject, keyColName, specificColumn);
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            geExecutableStatement(objIds, specificColDataType, preparedStatement, 1);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                objectIds.add(DataSet.getValue(resultSet, specificColDataType, specificColumn).toString());
            }
        } catch (Exception e) {
            throw new DataSyncJobException("Error fetching object IDs for reference MetaObject", Constants.INTERNAL_SERVER_ERROR);
        }
        return objectIds;
    }

    private int getHierarchyIndexAndSetDataset(MetaObjectDTO parentMetaObject, Set<MetaRelationModelDTO> childRelations, int hierarchyIndex, List<String> objIds, Connection connection, List<DataSet> dataSets, String pkName) throws DataSyncJobException, SQLException {
        if (childRelations != null && !childRelations.isEmpty()) {
            for (MetaRelationModelDTO childRelation : childRelations) {
                MetaObjectDTO childMetaObject = childRelation.getChildObject();
                String relationType = childRelation.getRelationType();
                if (childMetaObject != null && LOOKUP.equalsIgnoreCase(relationType)) {
                    hierarchyIndex = getLookupData(hierarchyIndex, connection, dataSets, pkName, childRelation, childMetaObject);
                }
            }
            for (MetaRelationModelDTO childRelation : childRelations) {
                MetaObjectDTO childMetaObject = childRelation.getChildObject();
                String relationType = childRelation.getRelationType();
                if (childMetaObject != null && REFERENCE.equalsIgnoreCase(relationType)) {
                    hierarchyIndex = getReferenceData(parentMetaObject, hierarchyIndex, objIds, connection, dataSets, pkName, childRelation, childMetaObject);
                }
            }
        }
        return hierarchyIndex;
    }

    private int getLookupData(int hierarchyIndex, Connection connection, List<DataSet> dataSets, String pkName, MetaRelationModelDTO childRelation, MetaObjectDTO childMetaObject) throws SQLException, DataSyncJobException {
        if (childMetaObject != null) {
            hierarchyIndex = getHierarchyIndex(hierarchyIndex, null, null, connection, null, dataSets, childMetaObject);
        }
        log.info("Lookup relation type processed for child MetaObject: {} with Parent Key Column Name: {}", childMetaObject.getName(), pkName);

        return hierarchyIndex;
    }

    private int getReferenceData(MetaObjectDTO parentMetaObject, int hierarchyIndex, List<String> objIds, Connection connection, List<DataSet> dataSets, String pkName, MetaRelationModelDTO childRelation, MetaObjectDTO childMetaObject) throws SQLException, DataSyncJobException {

        String childObjRelCol = childRelation.getChildObjRelCol();
        String parentObjRelCol = childRelation.getParentObjRelCol();
        Set<MetaObjectAttributeDTO> childsetAttributes = childMetaObject.getAttributes();
        Map<String, Object> childcolumnNameAndType = geColNameAndType(childsetAttributes, childObjRelCol);
        DataType childKeyColdataType = getDataType(childcolumnNameAndType, childMetaObject);
        String childKeyColName = getColName(childcolumnNameAndType, childMetaObject);
        List<String> refPKIds = getObjectIdsForReferenceMetaObject(parentMetaObject, objIds, connection, pkName, childKeyColdataType, parentObjRelCol);
        if (refPKIds != null && !refPKIds.isEmpty()) {
            hierarchyIndex = getHierarchyIndex(hierarchyIndex, childKeyColName, childKeyColdataType, connection, refPKIds, dataSets, childMetaObject);
            log.info("Reference PK IDs fetched for child MetaObject: {} with Parent Object Relation Column: {} and Parent Key Column Name: {}", childMetaObject.getName(), parentObjRelCol, pkName);
        } else {
            log.info("No Reference PK IDs found for child MetaObject: {} with Parent Object Relation Column: {} and Parent Key Column Name: {}", childMetaObject.getName(), parentObjRelCol, pkName);
        }

        return hierarchyIndex;
    }

    private int getHierarchyIndex(int hierarchyIndex, String keyColName, DataType keyColDataType, Connection connection, List<String> objIds, List<DataSet> dataSets, MetaObjectDTO childMetaObject) throws DataSyncJobException, SQLException {
        if (childMetaObject != null) {
            DataSet dataSet = readDataForSpecificIds(childMetaObject, hierarchyIndex, keyColName, keyColDataType, objIds, connection);
            dataSets.add(dataSet);
            hierarchyIndex++;
        }
        return hierarchyIndex;
    }

    protected void processMetaObjectHierarchy(
            MetaObjectDTO metaObj,
            MetaRelationMetaModelDTO metaRelationMetaModelDTO,
            int hierarchyIndex,
            String keyColName,
            DataType keyColdataType,
            List<String> objIds,
            List<DataSet> dataSets, Connection connection) throws DataSyncJobException, SQLException {
        Set<MetaRelationModelDTO> childRelations = metaRelationMetaModelDTO.getChildObjectRelations();
        hierarchyIndex = getHierarchyIndexAndSetDataset(metaObj, childRelations, hierarchyIndex, objIds, connection, dataSets, keyColName);
        DataSet dataSet = readDataForSpecificIds(metaObj, hierarchyIndex, keyColName, keyColdataType, objIds, connection);
        dataSets.add(dataSet);
        List<String> objectIds = getPKList(dataSet, metaObj);
        if (childRelations != null && !childRelations.isEmpty() && objectIds != null && !objectIds.isEmpty()) {
            for (MetaRelationModelDTO childRelation : childRelations) {
                String relationType = childRelation.getRelationType();
                if (!"REFERENCE".equalsIgnoreCase(relationType) && !"LOOKUP".equalsIgnoreCase(relationType)) {
                    MetaObjectDTO childMetaObject = childRelation.getChildObject();
                    if (childMetaObject != null) {
                        String childObjRelCol = childRelation.getChildObjRelCol();
                        Set<MetaObjectAttributeDTO> childsetAttributes = childMetaObject.getAttributes();
                        Map<String, Object> childcolumnNameAndType = geColNameAndType(childsetAttributes, childObjRelCol);
                        DataType childKeyColdataType = getDataType(childcolumnNameAndType, childMetaObject);
                        String childKeyColName = getColName(childcolumnNameAndType, childMetaObject);
                        processMetaObjectHierarchy(
                                childMetaObject,
                                commonAdaptersUtil.fetchMetaRelation(childMetaObject.getId()),
                                hierarchyIndex + 1,
                                childKeyColName,
                                childKeyColdataType,
                                objectIds,
                                dataSets, connection
                        );
                    }
                }
            }
        }
    }

    private static String getColName(Map<String, Object> childcolumnNameAndType, MetaObjectDTO childMetaObject) {
        String childKeyColName = childcolumnNameAndType.get("COL_NAME") != null
                ? childcolumnNameAndType.get("COL_NAME").toString()
                : null;
        if (childKeyColName == null || childKeyColName.isEmpty()) {
            log.error("Child key column name is null or empty for MetaObject: {}", childMetaObject.getName());
            throw new CustomError("Child key column name is null or empty", Constants.INTERNAL_SERVER_ERROR);
        }
        return childKeyColName;
    }

    private static DataType getDataType(Map<String, Object> childcolumnNameAndType, MetaObjectDTO childMetaObject) {
        DataType childKeyColdataType = childcolumnNameAndType.get("COL_TYPE") != null
                ? DataType.valueOf(childcolumnNameAndType.get("COL_TYPE").toString())
                : null;
        if (childKeyColdataType == null) {
            log.error("Child key column data type is null for MetaObject: {}", childMetaObject.getName());
            throw new CustomError("Child key column data type is null", Constants.INTERNAL_SERVER_ERROR);
        }
        return childKeyColdataType;
    }

    private List<String> getPKList(DataSet dataSet, MetaObjectDTO metaObj) {
        List<String> objectIds = new ArrayList<>();
        String columnName = null;
        for (MetaObjectAttributeDTO attribute : metaObj.getAttributes()) {
            // If no keyColName provided, find the primary key
            if (Boolean.TRUE.equals(attribute.isPrimary())) {
                columnName = attribute.getDbColumnName();
                break;
            }
        }
        if (columnName == null) {
            log.error("Primary key column name is null for MetaObject: {}", metaObj.getName());
            throw new CustomError("Primary key column name is null", Constants.INTERNAL_SERVER_ERROR);
        }
        String finalKeyColName = columnName;
        dataSet.getDataRows().forEach(dataRow -> {
            if (dataRow.getRow() == null || dataRow.getRow().isEmpty()) {
                log.warn("Data row is empty for MetaObject: {}", metaObj.getName());
            } else {
                objectIds.add(dataRow.getRow().get(finalKeyColName).toString());
            }
        });
        return objectIds;
    }

    private Map<String, Object> geColNameAndType(Set<MetaObjectAttributeDTO> setAttributes, String keyColName) throws DataSyncJobException, SQLException {
        Map<String, Object> columnNameAndType = new HashMap<>();
        String resolvedColName = keyColName;
        DataType resolvedColType = null;
        for (MetaObjectAttributeDTO attribute : setAttributes) {
            // If no keyColName provided, find the primary key
            if (resolvedColName == null || resolvedColName.isEmpty()) {
                if (Boolean.TRUE.equals(attribute.isPrimary())) {
                    resolvedColName = attribute.getDbColumnName();
                    resolvedColType = attribute.getDataType();
                    columnNameAndType.put("COL_NAME", resolvedColName);
                    columnNameAndType.put("COL_TYPE", resolvedColType);
                    break;
                }
            } else if (attribute.getDbColumnName().equals(resolvedColName)) {
                resolvedColType = attribute.getDataType();
                columnNameAndType.put("COL_NAME", resolvedColName);
                columnNameAndType.put("COL_TYPE", resolvedColType);
                break;
            }
        }
        return columnNameAndType;
    }

    private DataSet readDataForSpecificIds(MetaObjectDTO metaObj, int hierarchyIndex, String keyColName, DataType keyColdataType, List<String> objIds, Connection connection) throws DataSyncJobException, SQLException {
        DataSet dataSet = new DataSet();
        String tenantId = (regionalJobCtx.getValue(RegionalJobContext.REGIONAL_TENANT_ID) == null) ? null : regionalJobCtx.getValue(RegionalJobContext.REGIONAL_TENANT_ID).toString();
        String clientId = (regionalJobCtx.getValue(RegionalJobContext.CLIENT_ID) == null) ? null : regionalJobCtx.getValue(RegionalJobContext.CLIENT_ID).toString();
        LocalDateTime completeTime = regionalJobCtx.getValue(RegionalJobContext.LAST_COMPLETED_AT) == null ? null : LocalDateTime.parse(regionalJobCtx.getValue(RegionalJobContext.LAST_COMPLETED_AT));
        dataSet.setMetaObject(metaObj);
        dataSet.setHierarchyIndex(hierarchyIndex);

        String selectQuery = SQLBuilder.getSelectStatementForSpecificIds(metaObj, completeTime, objIds, keyColName);
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
                //dataSet.setDataRows(dataSet.getDataRowsFromResultSet(resultSet));
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
            log.info("Data read successfully for MetaObject: {}", metaObj.getName());
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
        if (clientId != null && !clientId.isBlank()) {
            if (!clientId.matches("^[a-zA-Z0-9_-]+$")) {
                throw new IllegalArgumentException("Invalid clientId");
            }
            try (Statement stmt = connection.createStatement()) {
                String sql = "SET dep.client_id = '" + clientId + "'";
                stmt.execute(sql);
            }
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

    Connection getConnection() throws DataSyncJobException {
        Connection connection = null;
        RegionalJobContext regionalJobContext = (RegionalJobContext) ctx.getContextByName(ExecutionContext.REGIONAL_JOB_CONTEXT);
        GlobalContext globalContext = (GlobalContext) ctx.getContextByName(ExecutionContext.GLOBAL_CONTEXT);
        OnesourceDomain onesourceDomain = OnesourceDomain.valueOf(regionalJobContext.getValue(RegionalJobContext.ONESOURCE_DOMAIN).toString());
        OnesourceRegion onesourceRegion = OnesourceRegion.valueOf(globalContext.getValue(GlobalContext.HOST_REGION).toString());
        // String regionalTenantId = regionalJobContext.getValue(RegionalJobContext.REGIONAL_TENANT_ID) == null ? null : regionalJobContext.getValue(RegionalJobContext.REGIONAL_TENANT_ID).toString();
        String systemName = regionalJobContext.getValue(RegionalJobContext.META_OBJECT_SYS_NAME) == null ? null : regionalJobContext.getValue(RegionalJobContext.META_OBJECT_SYS_NAME).toString();

        DataSource dataSource = dataSourceCatalogRepository.findByOnesourceDomainAndOnesourceRegionAndMetaObjectSysName(onesourceDomain, onesourceRegion, systemName)
                .orElseThrow(() -> new DataSyncJobException(DATASOURCE_NOT_FOUND.getMessage(), DATASOURCE_NOT_FOUND.getCode()));

        // Build JDBC connection string with parameters fetched from SSM
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
        } catch (SQLException e) {
            log.error("Error establishing database connection", e);
            throw new DataSyncJobException("Error establishing database connection", Constants.INTERNAL_SERVER_ERROR);
        }
        return connection;
    }

    @Override
    public void writeData(DataSetCollection data) throws DataSyncJobException {
        Connection connection = null;
        regionalJobCtx = (RegionalJobContext) ctx.getContextByName(ExecutionContext.REGIONAL_JOB_CONTEXT);
        try {
            String tenantId = (regionalJobCtx.getValue(RegionalJobContext.REGIONAL_TENANT_ID) == null) ? null : regionalJobCtx.getValue(RegionalJobContext.REGIONAL_TENANT_ID).toString();
            String clientId = (regionalJobCtx.getValue(RegionalJobContext.CLIENT_ID) == null) ? null : regionalJobCtx.getValue(RegionalJobContext.CLIENT_ID).toString();

            connection = getConnection();
            if (data != null && data.getDataSets() != null) {
                for (DataSet dataSet : data.getDataSets()) {
                    try {
                        setTenantIdAndClientId(clientId, tenantId, connection);
                        // Initialize transformation engine and apply transformations
                        Map<String, List<ConcurrentHashMap<String, Object>>> dataObjects = DataProcessor.divideList(connection, dataSet);
                        executeDataOperations(connection, dataSet.getMetaObject(), dataObjects, ctx);
                        log.info("Data written successfully for MetaObject: {}", dataSet.getMetaObject().getName());
                    } catch (SQLException e) {
                        log.error("Error writing data for MetaObject: {}", dataSet.getMetaObject().getName(), e);
                        throw new DataSyncJobException(JOB_READ_ERROR.getMessage(), JOB_READ_ERROR.getCode());
                    }
                }
            } else {
                log.warn("No data to write");
            }
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error("Error closing database connection", e);
                }
            }
        }

    }

    private void executeDataOperations(Connection connection, MetaObjectDTO metaObjectDTO, Map<String, List<ConcurrentHashMap<String, Object>>> dataObjects, ExecutionContext ctx) throws DataSyncJobException, SQLException {
        List<ConcurrentHashMap<String, Object>> insertList = dataObjects.get(INSERT_LIST_KEY);
        if (insertList != null && !insertList.isEmpty()) {
            executeInsert(connection, metaObjectDTO, insertList, ctx);
        }
        if (dataObjects.containsKey(UPDATE_LIST_KEY)) {
            List<ConcurrentHashMap<String, Object>> updateList = dataObjects.get(UPDATE_LIST_KEY);
            if (updateList != null && !updateList.isEmpty()) {
                executeUpdate(connection, metaObjectDTO, updateList);
            }
        }
    }

    private void executeInsert(Connection connection, MetaObjectDTO metaObjectDTO, List<ConcurrentHashMap<String, Object>> dataObjects, ExecutionContext ctx) throws SQLException, DataSyncJobException {
        log.info("In DatabaseAdaptorService executeInsert - start");
        String insertQuery = SQLBuilder.getInsertStatement(metaObjectDTO);
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            connection.setAutoCommit(false);
            int count = 0;
            for (ConcurrentHashMap<String, Object> dataObject : dataObjects) {
                if (dataObject == null) {
                    log.warn("Null dataObject encountered in executeInsert. Skipping entry.");
                    continue;
                }
                //TODO move applyTransformation to DataSyncExecution Initialize transformation engine and apply transformations
                // dataObject = applyTransformation(this.ctx, dataObject);
                setPreparedStatementParameters(preparedStatement, dataObject, metaObjectDTO, INSERT_LIST_KEY);
                preparedStatement.addBatch();
                count++;
                // Execute every BATCH_SIZE inserts
                if (count % BATCH_SIZE == 0) {
                    preparedStatement.executeBatch();
                    preparedStatement.clearBatch();
                }
            }
            // Execute remaining batch if any
            if (count % BATCH_SIZE != 0) {
                preparedStatement.executeBatch();
                preparedStatement.clearBatch();
            }
            connection.commit(); // Commit if all successful
            log.info("Completed executeInsert for metaObject: {}", metaObjectDTO.getTableName());
        } catch (SQLException e) {
            connection.rollback();
            log.error("Error executing insert for metaObject: {}", metaObjectDTO.getTableName(), e);
            throw new DataSyncJobException("Failed to execute insert for metaObject: " + metaObjectDTO.getTableName(), JOB_WRITE_ERROR.getCode());

        }
    }
    private void executeUpdate(Connection connection, MetaObjectDTO metaObjectDTO, List<ConcurrentHashMap<String, Object>> dataObjects) throws SQLException, DataSyncJobException {
        log.info("Executing update for {} records", dataObjects.size());
        List<String> primaryKeyColumns = getPrimaryKeyColumns(metaObjectDTO);
        String tableName = metaObjectDTO.getTableName();

        try {
            connection.setAutoCommit(false); // Enable transaction management
            int count = 0;

            String updateQuery = SQLBuilder.createUpdateQuery(tableName, metaObjectDTO, primaryKeyColumns);
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                for (ConcurrentHashMap<String, Object> dataObject : dataObjects) {
                    if (dataObject == null) {
                        log.warn("Null dataObject encountered in executeUpdate. Skipping entry.");
                        continue;
                    }
                    //TODO move applyTransformation to DataSyncExecution Initialize transformation engine and apply transformations
                    //dataObject = applyTransformation(ctx, dataObject);
                    setPreparedStatementParameters(preparedStatement, dataObject, metaObjectDTO, UPDATE_LIST_KEY);
                    preparedStatement.addBatch();
                    log.info("Prepared statement for update: {}", preparedStatement.toString());
                    count++;

                    // Execute batch when the batch size is reached
                    if (count % BATCH_SIZE == 0) {
                        log.info("Executing batch update for {} records", BATCH_SIZE);
                        preparedStatement.executeBatch();
                        preparedStatement.clearBatch();
                    }
                }

                // Execute remaining batch if any
                if (count % BATCH_SIZE != 0) {
                    log.info("Executing final batch update for {} records", count % BATCH_SIZE);
                    preparedStatement.executeBatch();
                    preparedStatement.clearBatch();
                }
            }

            connection.commit(); // Commit transaction
            log.info("Batch update completed successfully for table: {}", tableName);
        } catch (SQLException e) {
            connection.rollback(); // Rollback transaction in case of error
            log.error("Error executing batch update for table: {}", tableName, e);
            throw new DataSyncJobException("Error executing batch update", JOB_WRITE_ERROR.getCode());
        }
    }

    private void setPreparedStatementParameters(PreparedStatement preparedStatement, ConcurrentHashMap<String, Object> dataObject, MetaObjectDTO metaObject, String operationType) throws SQLException {
        Set<MetaObjectAttributeDTO> setAttributes = metaObject.getAttributes();
        Map<String, MetaObjectAttributeDTO> attributes = DataProcessor.convertSetToMap(setAttributes);

        int index = 1;
        Object primaryKeyValue = null;
        DataType primaryKeyDataType = null;
        for (Map.Entry<String, MetaObjectAttributeDTO> entry : attributes.entrySet()) {
            MetaObjectAttributeDTO attribute = entry.getValue();
            Object value = dataObject.get(attribute.getDbColumnName());

            if (attribute.isPrimary()) {
                primaryKeyValue = value; // Store the primary key value
                primaryKeyDataType = attribute.getDataType(); // Store the primary key data type
            }
            // Handle CHAR, STRING, and TEXT as similar types for consistency in primary key and general attribute handling.
            if (attribute.getDataType() == DataType.STRING ||
                    attribute.getDataType() == DataType.TEXT ||
                    attribute.getDataType() == DataType.CHAR) {

                if (value == null || "null".equalsIgnoreCase(value.toString())) {
                    preparedStatement.setNull(index, Types.VARCHAR);
                } else {
                    preparedStatement.setString(index, value.toString());
                }
            } else if (attribute.getDataType() == DataType.JSONB) {
                if (value == null || "null".equalsIgnoreCase(value.toString())) {
                    preparedStatement.setNull(index, Types.OTHER);
                } else {
                    org.postgresql.util.PGobject jsonObject = new org.postgresql.util.PGobject();
                    jsonObject.setType("jsonb");
                    jsonObject.setValue(value.toString());
                    preparedStatement.setObject(index, jsonObject);
                }
            } else if (attribute.getDataType() == DataType.UUID) {
                if (value == null || value.equals("null")) {
                    preparedStatement.setObject(index, null);
                } else {
                    preparedStatement.setObject(index, UUID.fromString(value.toString()));
                }
            } else if (attribute.getDataType() == DataType.INTEGER) {
                if (value == null || value.equals("null")) {
                    preparedStatement.setObject(index, null);
                } else {
                    preparedStatement.setInt(index, Integer.parseInt(value.toString()));
                }
            } else if (attribute.getDataType() == DataType.LONG) {
                if (value == null || value.equals("null")) {
                    preparedStatement.setObject(index, null);
                } else {
                    preparedStatement.setDouble(index, Long.parseLong(value.toString()));
                }
            } else if (attribute.getDataType() == DataType.DATETIME || attribute.getDataType() == DataType.TIMESTAMP) {
                if (value == null || value.equals("null")) {
                    preparedStatement.setObject(index, null);
                } else {
                    preparedStatement.setObject(index, LocalDateTime.parse(value.toString()));
                }
            } else if (attribute.getDataType() == DataType.BOOLEAN) {
                if (value == null || value.equals("null")) {
                    preparedStatement.setObject(index, null);
                } else {
                    preparedStatement.setBoolean(index, Boolean.parseBoolean(value.toString()));
                }
            } else if (attribute.getDataType() == DataType.DATE) {
                if (value == null || value.equals("null")) {
                    preparedStatement.setObject(index, null);
                } else {
                    preparedStatement.setObject(index, LocalDate.parse(value.toString()));
                }

            } else if (attribute.getDataType() == DataType.TEXT_ARRAY) {
                if (value == null || value.equals("null")) {
                    preparedStatement.setNull(index, java.sql.Types.ARRAY);
                } else {
                    String[] arrayValue;
                    if (value instanceof String[]) {
                        arrayValue = (String[]) value;
                    } else if (value instanceof java.util.List) {
                        arrayValue = ((java.util.List<?>) value).toArray(new String[0]);

                    } else {
                        throw new SQLException("Unsupported value for TEXT_ARRAY: " + value);
                    }
                    java.sql.Array sqlArray = preparedStatement.getConnection().createArrayOf("text", arrayValue);
                    preparedStatement.setArray(index, sqlArray);
                }
            } else if (attribute.getDataType() == DataType.BIGINT) {
                if (value == null || value.equals("null")) {
                    preparedStatement.setObject(index, null);
                } else {
                    preparedStatement.setLong(index, Long.parseLong(value.toString()));
                }
            } else {
                if (value == null || value.equals("null")) {
                    preparedStatement.setObject(index, null);
                } else {
                    preparedStatement.setObject(index, value);
                }
            }

            index++;
        }
        // If the query is an update query, set the WHERE clause for the primary key
        if (UPDATE_LIST_KEY.equals(operationType) && primaryKeyValue != null && primaryKeyDataType != null) {
            switch (primaryKeyDataType) {
                // Handle CHAR, STRING, and TEXT as similar types for consistency in primary key.
                case STRING, TEXT, CHAR:
                    preparedStatement.setString(index, primaryKeyValue.toString());
                    break;
                case INTEGER:
                    preparedStatement.setInt(index, Integer.parseInt(primaryKeyValue.toString()));
                    break;
                case UUID:
                    preparedStatement.setObject(index, UUID.fromString(primaryKeyValue.toString()));
                    break;
                case BIGINT:
                    try {
                        preparedStatement.setLong(index, Long.parseLong(primaryKeyValue.toString()));
                    } catch (NumberFormatException e) {
                        log.error("Invalid BIGINT format for objId: {}", primaryKeyValue, e);
                        throw new CustomError(JOB_READ_ERROR.getMessage(), JOB_READ_ERROR.getCode());
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported primary key data type: " + primaryKeyDataType);
            }
        }
    }

    private List<String> getPrimaryKeyColumns(MetaObjectDTO metaObjectDTO) {
        List<String> primaryKeyColumns = new ArrayList<>();
        for (MetaObjectAttributeDTO attribute : metaObjectDTO.getAttributes()) {
            if (attribute.isPrimary()) {
                primaryKeyColumns.add(attribute.getDbColumnName());
            }
        }
        return primaryKeyColumns;
    }


    @Override
    public void cleanUp() {
        ctx = null;
    }
}

