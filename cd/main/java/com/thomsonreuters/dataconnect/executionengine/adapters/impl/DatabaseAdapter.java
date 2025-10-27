package com.thomsonreuters.dataconnect.executionengine.adapters.impl;


import com.thomsonreuters.dataconnect.common.executioncontext.AdapterContext;
import com.thomsonreuters.dataconnect.common.executioncontext.ExecutionContext;
import com.thomsonreuters.dataconnect.common.executioncontext.GlobalContext;
import com.thomsonreuters.dataconnect.common.executioncontext.RegionalJobContext;
import com.thomsonreuters.dataconnect.executionengine.adapters.CommonAdaptersUtil;
import com.thomsonreuters.dataconnect.executionengine.adapters.IntegrationAdapter;
import com.thomsonreuters.dataconnect.executionengine.data.DataRow;
import com.thomsonreuters.dataconnect.executionengine.data.DataSet;
import com.thomsonreuters.dataconnect.executionengine.data.DataSetCollection;
import com.thomsonreuters.dataconnect.executionengine.dto.MetaObjectAttributeDTO;
import com.thomsonreuters.dataconnect.executionengine.dto.MetaObjectDTO;
import com.thomsonreuters.dataconnect.executionengine.dto.MetaRelationMetaModelDTO;
import com.thomsonreuters.dataconnect.executionengine.dto.MetaRelationModelDTO;
import com.thomsonreuters.dataconnect.executionengine.exceptionhandler.CustomError;
import com.thomsonreuters.dataconnect.executionengine.exceptionhandler.DataSyncJobException;
import com.thomsonreuters.dataconnect.executionengine.model.entity.DataSource;
import com.thomsonreuters.dataconnect.executionengine.model.entity.OnesourceDatabaseType;
import com.thomsonreuters.dataconnect.executionengine.model.entity.enums.DataType;
import com.thomsonreuters.dataconnect.executionengine.model.pojo.DataUnit;
import com.thomsonreuters.dataconnect.executionengine.model.pojo.DataUnitList;
import com.thomsonreuters.dataconnect.executionengine.model.pojo.PrimaryKeyInfo;
import com.thomsonreuters.dataconnect.executionengine.repository.DataSourceCatalogRepository;
import com.thomsonreuters.dataconnect.executionengine.repository.MetaObjectRelationRepository;
import com.thomsonreuters.dataconnect.executionengine.repository.MetaObjectRepository;
import com.thomsonreuters.dataconnect.executionengine.repository.OnesourceDatabaseTypeRepository;
import com.thomsonreuters.dataconnect.executionengine.services.DataSourceApiService;
import com.thomsonreuters.dataconnect.executionengine.services.MetaModelClient;
import com.thomsonreuters.dataconnect.executionengine.services.awsservices.AwsSsmService;
import com.thomsonreuters.dataconnect.executionengine.utils.ConnectionFactory;
import com.thomsonreuters.dataconnect.executionengine.utils.DataProcessor;
import com.thomsonreuters.dataconnect.executionengine.utils.SQLBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.thomsonreuters.dataconnect.executionengine.adapters.CommonAdaptersUtil.sanitizeAndValidateObjIds;
import static com.thomsonreuters.dataconnect.executionengine.constant.Constants.*;
import static com.thomsonreuters.dataconnect.executionengine.model.entity.enums.ErrorConstant.*;

@Service
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
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

    @Autowired
    private DataSourceApiService dataSourceApiService;

    @Value("${META_REGISTRY_API_URL}")
    private String metaModelServiceUrl;
    @Value("${DELTA_NEW_RECORD_THRESHOLD_IN_SECS}")
    private int recordBefore;

    @Override
    public void validate() {
        // Default implementation: No action required
    }

    @Getter
    public List<DataSetCollection> transitHubDataSetCollection;

    @Override
    public DataSetCollection readData(ExecutionContext ctx) throws DataSyncJobException {

        DataUnitList dataUnitList = null;
        int hierarchyIndex = 0;
        AdapterContext adapterContext = (AdapterContext) ctx.getContextByName(ExecutionContext.IN_ADAPTER_CONTEXT);
        DataUnit data = adapterContext.getValue(DATA_UNIT);
        List<?> objIds = null;
        if (data != null && data.getContent() != null && (data.getContent() instanceof DataUnitList)) {
            dataUnitList = (DataUnitList) data.getContent();
        }
        List<DataSet> dataSets = new ArrayList<>();
        try (Connection connection = getConnection(ctx)) {
            if (dataUnitList != null && dataUnitList.getObjIds() != null && !dataUnitList.getObjIds().isEmpty()) {
                objIds = sanitizeAndValidateObjIds(dataUnitList.getObjIds());
            } else {
                log.warn("DataUnitList is null or empty, proceeding with no object IDs");
            }
            regionalJobCtx = (RegionalJobContext) ctx.getContextByName(ExecutionContext.REGIONAL_JOB_CONTEXT);
            UUID metaObjectId = metaObjectRepository.findMetaObjectBySystemName(regionalJobCtx.getValue(RegionalJobContext.META_OBJECT_SYS_NAME)).getId();
            MetaRelationMetaModelDTO metaRelationMetaModelDTO = commonAdaptersUtil.fetchMetaRelation(metaObjectId);

            if (metaRelationMetaModelDTO == null) {
                log.error("MetaRelationMetaModelDTO is null for MetaObject ID: {}", metaObjectId);
                throw new DataSyncJobException("MetaRelationMetaModelDTO not found for the given MetaObject ID", NOT_FOUND);
            }
            MetaObjectDTO parentMetaObject = metaRelationMetaModelDTO.getParentObject();
            if (parentMetaObject == null) {
                log.warn("Parent MetaObject is null for MetaObject ID: {}. Skipping data read process.", metaObjectId);
                return new DataSetCollection();
            }

            if (parentMetaObject.getId() == null) {
                log.warn("Parent MetaObject ID is null. Skipping data read process.");
                return new DataSetCollection();
            }

            Set<MetaObjectAttributeDTO> setAttributes = parentMetaObject.getAttributes();
            //Handle composite keys
            if (setAttributes == null || setAttributes.isEmpty()) {
                log.warn("Parent MetaObject attributes are null or empty. Skipping data read process.");
                return new DataSetCollection();
            }

            // Validate child object relations
            Set<MetaRelationModelDTO> childObjectRelations = metaRelationMetaModelDTO.getChildObjectRelations();
            if (childObjectRelations == null) {
                log.warn("Child object relations are null. Processing without child relations.");
            } else {
                // Check if any child relations have null critical properties
                boolean hasNullCriticalProperties = childObjectRelations.stream().anyMatch(relation ->
                        relation == null ||
                                relation.getChildObject() == null ||
                                relation.getChildObject().getId() == null ||
                                relation.getRelationType() == null ||
                                (REFERENCE.equalsIgnoreCase(relation.getRelationType()) &&
                                        (relation.getChildObjRelCol() == null || relation.getParentObjRelCol() == null))
                );

                if (hasNullCriticalProperties) {
                    log.warn("Some child relations have null critical properties. Processing will continue but may skip affected relations.");
                }
            }
            PrimaryKeyInfo primaryKeyInfo = new PrimaryKeyInfo();
            int primaryCount = 0;
            for (MetaObjectAttributeDTO attr : setAttributes) {
                if (attr.isPrimary()) {
                    primaryCount++;
                }
            }
            if ((dataUnitList != null && dataUnitList.getObjIds() != null && !dataUnitList.getObjIds().isEmpty() && dataUnitList.getObjIds().get(0) instanceof Map) || primaryCount > 1) {
                primaryKeyInfo.setCompositePKColNameAndType(getCompositePKColNameAndType(setAttributes, null));
                primaryKeyInfo.setComposite(true);
            } else {
                Map<String, Object> pkNameAndType = getColNameAndType(setAttributes, null);
                primaryKeyInfo.setPkDataType(getDataType(pkNameAndType, parentMetaObject));
                primaryKeyInfo.setPkName(getColName(pkNameAndType, parentMetaObject));
                primaryKeyInfo.setComposite(false);


                if (pkNameAndType.isEmpty()) {
                    log.warn("Primary key name and type mapping is null or empty. Skipping data read process.");
                    return new DataSetCollection();
                }

                DataType pkdataType = getDataType(pkNameAndType, parentMetaObject);
                String pkName = getColName(pkNameAndType, parentMetaObject);

                if (pkName.trim().isEmpty()) {
                    log.warn("Primary key name is null or empty. Skipping data read process.");
                    return new DataSetCollection();
                }

                if (pkdataType == null) {
                    log.warn("Primary key data type is null. Skipping data read process.");
                    return new DataSetCollection();
                }
            }
            processMetaObjectHierarchy(parentMetaObject, metaRelationMetaModelDTO, hierarchyIndex, objIds, dataSets, connection, primaryKeyInfo, regionalJobCtx, null);
            log.info("Reading data from the database");
        } catch (DataSyncJobException e) {
            log.error("Error reading data from the database", e);
            throw new DataSyncJobException("Error reading data from the database", INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("Unexpected error occurred while reading data from the database", e);
            throw new DataSyncJobException("Error reading data from the database", INTERNAL_SERVER_ERROR);
        }
        // Return a DataSetCollection containing all datasets
        DataSetCollection dataSetCollection = new DataSetCollection();
        dataSetCollection.setDataSets(dataSets);
        return dataSetCollection;
    }

    private PrimaryKeyInfo getPrimaryKeyInfo(MetaObjectDTO parentMetaObject, List<?> objectIds,String ColName) throws SQLException, DataSyncJobException {
        Set<MetaObjectAttributeDTO> setAttributes = parentMetaObject.getAttributes();

        PrimaryKeyInfo primaryKeyInfo = new PrimaryKeyInfo();
        int primaryCount = 0;
        for (MetaObjectAttributeDTO attr : setAttributes) {
            if (attr.isPrimary()) {
                primaryCount++;
            }
        }
        if (objectIds instanceof Map || primaryCount > 1) {
            primaryKeyInfo.setCompositePKColNameAndType(getCompositePKColNameAndType(setAttributes, null));
            primaryKeyInfo.setComposite(true);
        } else {
            Map<String, Object> pkNameAndType = getColNameAndType(setAttributes, ColName);
            primaryKeyInfo.setPkDataType(getDataType(pkNameAndType, parentMetaObject));
            primaryKeyInfo.setPkName(getColName(pkNameAndType, parentMetaObject));
            primaryKeyInfo.setComposite(false);
        }
        return primaryKeyInfo;
    }

    private List<String> getObjectIdsForReferenceMetaObject(MetaObjectDTO parentMetaObject, List<?> objIds, Connection connection, String keyColName, DataType specificColDataType, String specificColumn) throws SQLException, DataSyncJobException {
        List<String> objectIds = new ArrayList<>();

        // Check for null values in critical parameters and skip processing if any are null
        if (parentMetaObject == null) {
            log.warn("Skipping getObjectIdsForReferenceMetaObject due to null parentMetaObject");
            return objectIds; // Return empty list
        }

        if (objIds == null || objIds.isEmpty()) {
            log.warn("Skipping getObjectIdsForReferenceMetaObject due to null or empty objIds");
            return objectIds; // Return empty list
        }


        if (keyColName == null || keyColName.trim().isEmpty()) {
            log.warn("Skipping getObjectIdsForReferenceMetaObject due to null or empty keyColName");
            return objectIds; // Return empty list
        }

        if (specificColDataType == null) {
            log.warn("Skipping getObjectIdsForReferenceMetaObject due to null specificColDataType");
            return objectIds; // Return empty list
        }

        if (specificColumn == null || specificColumn.trim().isEmpty()) {
            log.warn("Skipping getObjectIdsForReferenceMetaObject due to null or empty specificColumn");
            return objectIds; // Return empty list
        }

        try {
            String selectQuery = SQLBuilder.selectSpecificColumnsFromTable(parentMetaObject, keyColName, specificColumn, objIds.size());
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            geExecutableStatement(objIds, specificColDataType, preparedStatement, 1);
            //compositePrimaryKey change
            String tenantId = (regionalJobCtx.getValue(RegionalJobContext.REGIONAL_TENANT_ID) == null) ? null : regionalJobCtx.getValue(RegionalJobContext.REGIONAL_TENANT_ID).toString();
            String clientId = (regionalJobCtx.getValue(RegionalJobContext.CLIENT_ID) == null) ? null : regionalJobCtx.getValue(RegionalJobContext.CLIENT_ID).toString();
            setTenantIdAndClientId(tenantId, connection);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Object value = DataSet.getValue(resultSet, specificColDataType, specificColumn);
                if (!"null".equalsIgnoreCase(value.toString().trim())) {
                    objectIds.add(DataSet.getValue(resultSet, specificColDataType, specificColumn).toString());
                }
            }
        } catch (Exception e) {
            throw new DataSyncJobException("Error fetching object IDs for reference MetaObject", INTERNAL_SERVER_ERROR);
        }
        return objectIds;
    }

    private int getHierarchyIndexAndSetDataset(MetaObjectDTO parentMetaObject, Set<MetaRelationModelDTO> childRelations, int hierarchyIndex, List<?> objIds, Connection connection, List<DataSet> dataSets, PrimaryKeyInfo primaryKeyInfo, RegionalJobContext regionalJobCtx) throws DataSyncJobException, SQLException {
        if (childRelations != null && !childRelations.isEmpty()) {
            for (MetaRelationModelDTO childRelation : childRelations) {
                MetaObjectDTO childMetaObject = childRelation.getChildObject();
                String relationType = childRelation.getRelationType();
                if (childMetaObject != null && LOOKUP.equalsIgnoreCase(relationType)) {
                    hierarchyIndex = getLookupData(hierarchyIndex, connection, dataSets, null, childRelation, childMetaObject, regionalJobCtx);
                }
            }
            for (MetaRelationModelDTO childRelation : childRelations) {
                MetaObjectDTO childMetaObject = childRelation.getChildObject();
                String relationType = childRelation.getRelationType();
                if (childMetaObject != null && REFERENCE.equalsIgnoreCase(relationType)) {
                    hierarchyIndex = getReferenceData(parentMetaObject, hierarchyIndex, objIds, connection, dataSets, primaryKeyInfo, childRelation, childMetaObject, regionalJobCtx);
                }
            }
        }
        return hierarchyIndex;
    }

    private int getLookupData(int hierarchyIndex, Connection connection, List<DataSet> dataSets, String pkName, MetaRelationModelDTO childRelation, MetaObjectDTO childMetaObject, RegionalJobContext regionalJobCtx) throws SQLException, DataSyncJobException {
        String tableType = "Lookup";
        if (childMetaObject != null) {
            hierarchyIndex = getHierarchyIndex(hierarchyIndex, null, connection, null, dataSets, childMetaObject, regionalJobCtx, tableType);
        }
        log.info("Lookup relation type processed for child MetaObject: {} with Parent Key Column Name: {}", childMetaObject.getSystemName(), pkName);

        return hierarchyIndex;
    }

    private int getReferenceData(MetaObjectDTO parentMetaObject, int hierarchyIndex, List<?> objIds, Connection connection, List<DataSet> dataSets, PrimaryKeyInfo primaryKeyInfo, MetaRelationModelDTO childRelation, MetaObjectDTO childMetaObject, RegionalJobContext regionalJobCtx) throws SQLException, DataSyncJobException {

        // Check for null values in critical parameters and skip processing if any are null
        if (parentMetaObject == null) {
            log.warn("Skipping getReferenceData due to null parentMetaObject");
            return hierarchyIndex;
        }

        if (objIds == null || objIds.isEmpty()) {
            log.warn("Skipping getReferenceData due to null or empty objIds");
            return hierarchyIndex;
        }
        String pkName = primaryKeyInfo.getPkName();
        if (pkName == null || pkName.trim().isEmpty()) {
            log.warn("Skipping getReferenceData due to null or empty pkName");
            return hierarchyIndex;
        }

        if (childRelation == null) {
            log.warn("Skipping getReferenceData due to null childRelation");
            return hierarchyIndex;
        }

        if (childMetaObject == null) {
            log.warn("Skipping getReferenceData due to null childMetaObject");
            return hierarchyIndex;
        }
        // Check for null values in child relation columns
        String childObjRelCol = childRelation.getChildObjRelCol();
        String parentObjRelCol = childRelation.getParentObjRelCol();

        if (childObjRelCol == null || childObjRelCol.trim().isEmpty()) {
            log.warn("Skipping getReferenceData due to null or empty childObjRelCol");
            return hierarchyIndex;
        }

        if (parentObjRelCol == null || parentObjRelCol.trim().isEmpty()) {
            log.warn("Skipping getReferenceData due to null or empty parentObjRelCol");
            return hierarchyIndex;
        }

        // Check for null child object ID
        if (childMetaObject.getId() == null) {
            log.warn("Skipping getReferenceData due to null child object ID");
            return hierarchyIndex;
        }

        Set<MetaObjectAttributeDTO> childsetAttributes = childMetaObject.getAttributes();
        Map<String, Object> childcolumnNameAndType = getColNameAndType(childsetAttributes, childObjRelCol);
        DataType childKeyColdataType = getDataType(childcolumnNameAndType, childMetaObject);
        String childKeyColName = getColName(childcolumnNameAndType, childMetaObject);
        List<?> refPKIds = getObjectIdsForReferenceMetaObject(parentMetaObject, objIds, connection, pkName, childKeyColdataType, parentObjRelCol);
        //select parent_ref_col from parent_table where parent_pk_col in (objIds)
        if (refPKIds != null && !refPKIds.isEmpty()) {
            PrimaryKeyInfo refPkinfo = getPrimaryKeyInfo(childMetaObject,refPKIds,childKeyColName);
            hierarchyIndex = getHierarchyIndex(hierarchyIndex, refPkinfo, connection, refPKIds, dataSets, childMetaObject, regionalJobCtx, "reference");
            //select child_rel_col from ref_table where child_rel_col in (refPKIds)
            log.info("Reference PK IDs fetched for child MetaObject: {} with Parent Object Relation Column: {} and Parent Key Column Name: {}", childMetaObject.getSystemName(), parentObjRelCol, pkName);
        } else {
            log.info("No Reference PK IDs found for child MetaObject: {} with Parent Object Relation Column: {} and Parent Key Column Name: {}", childMetaObject.getSystemName(), parentObjRelCol, pkName);
        }

        return hierarchyIndex;
    }

    private int getHierarchyIndex(int hierarchyIndex, PrimaryKeyInfo primaryKeyInfo, Connection connection, List<?> objIds, List<DataSet> dataSets, MetaObjectDTO childMetaObject, RegionalJobContext regionalJobCtx, String tableType) throws DataSyncJobException, SQLException {
        if (childMetaObject != null) {
            DataSet dataSet = readDataForSpecificIds(childMetaObject, hierarchyIndex, primaryKeyInfo, objIds, connection, regionalJobCtx, tableType, null);
            dataSets.add(dataSet);
            hierarchyIndex++;
        }
        return hierarchyIndex;
    }

    protected void processMetaObjectHierarchy(
            MetaObjectDTO metaObj,
            MetaRelationMetaModelDTO metaRelationMetaModelDTO,
            int hierarchyIndex,
            List<?> objIds,
            List<DataSet> dataSets, Connection connection, PrimaryKeyInfo pkInfo, RegionalJobContext regionalJobContext, LocalDateTime lastUpdatedTime) throws DataSyncJobException, SQLException {
        //TODO
        Set<MetaRelationModelDTO> childRelations = metaRelationMetaModelDTO.getChildObjectRelations();
        hierarchyIndex = getHierarchyIndexAndSetDataset(metaObj, childRelations, hierarchyIndex, objIds, connection, dataSets, pkInfo, regionalJobContext);
        //Handle composite keys
        if (pkInfo.isComposite() || (objIds != null && !objIds.isEmpty() && objIds.get(0) instanceof Map)) {
            objIds = convertSanitizedObjIdsToOriginal((List<String>) objIds);
        }
        String tableName = regionalJobContext.getValue(RegionalJobContext.META_OBJECT_SYS_NAME).toString();
        String tableType = "child";
        if(tableName.equalsIgnoreCase(metaObj.getDbTable())){
            tableType = "parent";
        }
        String tenantId = (regionalJobCtx.getValue(RegionalJobContext.REGIONAL_TENANT_ID) == null) ? null : regionalJobCtx.getValue(RegionalJobContext.REGIONAL_TENANT_ID).toString();
        String execType = (regionalJobCtx.getValue(EXEC_TYPE) == null) ? null : regionalJobCtx.getValue(EXEC_TYPE).toString();
        if (execType != null) {
            if (execType.equalsIgnoreCase("real_time")) {
                execType = "REAL_TIME";
            } else if (execType.equalsIgnoreCase("batch")) {
                execType = "BATCH";
            }
        }
        // Get latest updated_at timestamp from database instead of using acceptedTime from context
        LocalDateTime latestUpdatedAtTimeMinfewSec = null;
        LocalDateTime latestUpdatedAt = lastUpdatedTime;
        if (execType.equals("REAL_TIME") && objIds != null && !objIds.isEmpty() && tableType.equalsIgnoreCase("parent") ) {
            latestUpdatedAt = getLatestUpdatedAtFromDatabase(metaObj, objIds, pkInfo, connection, tenantId);
            if (latestUpdatedAt != null) {
                latestUpdatedAtTimeMinfewSec = latestUpdatedAt.minusSeconds(recordBefore);
                log.info("Using latest updated_at timestamp from database: {} for table: {}", latestUpdatedAt, metaObj.getDbTable());
            }
        }

        DataSet dataSet = readDataForSpecificIds(metaObj, hierarchyIndex, pkInfo, objIds, connection, regionalJobContext, tableType, latestUpdatedAtTimeMinfewSec);
        dataSets.add(dataSet);
        if (childRelations != null && !childRelations.isEmpty()) {
            for (MetaRelationModelDTO childRelation : childRelations) {
                List<Object> objectIds = getChildPKList(dataSet, metaObj, childRelation.getParentObjRelCol());
                objectIds = objectIds.stream().distinct().collect(Collectors.toList());
                String relationType = childRelation.getRelationType();
                if (!REFERENCE.equalsIgnoreCase(relationType) && !LOOKUP.equalsIgnoreCase(relationType)) {
                    MetaObjectDTO childMetaObject = childRelation.getChildObject();
                    if (childMetaObject != null) {
                        String childObjRelCol = childRelation.getChildObjRelCol();
                        Set<MetaObjectAttributeDTO> childsetAttributes = childMetaObject.getAttributes();
                        //Handle composite keys
                        PrimaryKeyInfo primaryKeyInfo = new PrimaryKeyInfo();
                        Map<String, Object> childcolumnNameAndType = getColNameAndType(childsetAttributes, childObjRelCol);
                        if (childcolumnNameAndType.isEmpty()) {
                            log.warn("Child key column name and type mapping is null or empty for MetaObject: {}", childMetaObject.getSystemName());
                            throw new CustomError("Child key column name and type mapping is null or empty", INTERNAL_SERVER_ERROR);
                        }
                        primaryKeyInfo.setPkDataType(getDataType(childcolumnNameAndType, metaObj));
                        primaryKeyInfo.setPkName(getColName(childcolumnNameAndType, metaObj));
                        primaryKeyInfo.setComposite(false);
                        processMetaObjectHierarchy(
                                childMetaObject,
                                commonAdaptersUtil.fetchMetaRelation(childMetaObject.getId()),
                                hierarchyIndex + 1,
                                objectIds,
                                dataSets, connection, primaryKeyInfo, regionalJobCtx, latestUpdatedAtTimeMinfewSec
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
            log.error("Child key column name is null or empty for MetaObject: {}", childMetaObject.getSystemName());
            throw new CustomError("Child key column name is null or empty", INTERNAL_SERVER_ERROR);
        }
        return childKeyColName;
    }

    private static DataType getDataType(Map<String, Object> childcolumnNameAndType, MetaObjectDTO childMetaObject) {
        DataType childKeyColdataType = childcolumnNameAndType.get("COL_TYPE") != null
                ? DataType.valueOf(childcolumnNameAndType.get("COL_TYPE").toString())
                : null;
        if (childKeyColdataType == null) {
            log.error("Child key column data type is null for MetaObject: {}", childMetaObject.getSystemName());
            throw new CustomError("Child key column data type is null", INTERNAL_SERVER_ERROR);
        }
        return childKeyColdataType;
    }

    private List<Object> getChildPKList(DataSet dataSet, MetaObjectDTO metaObj, String keyColName) throws DataSyncJobException {
        List<Object> objectIds = new ArrayList<>();
        if(dataSet.getDataRows() == null || dataSet.getDataRows().isEmpty())
        {
            return objectIds;
        }
        String columnName = keyColName;
        for (MetaObjectAttributeDTO attribute : metaObj.getAttributes()) {
            // If no keyColName provided, find the primary key
            if (keyColName != null && keyColName.equalsIgnoreCase(attribute.getDbColumn())) {
                columnName = attribute.getDbColumn();
                break;
            }
        }
        if (columnName == null) {
            log.error("Primary key column name is null for MetaObject: {}", metaObj.getSystemName());
            throw new CustomError("Primary key column name is null", INTERNAL_SERVER_ERROR);
        }
        String finalKeyColName = columnName;
        dataSet.getDataRows().forEach(dataRow -> {
            if (dataRow == null || dataRow.getRow() == null || dataRow.getRow().isEmpty()) {
                log.warn("Data row is empty for MetaObject: {}", metaObj.getSystemName());
            } else {
                objectIds.add(dataRow.getRow().get(finalKeyColName).toString());
            }
        });
        return objectIds;
    }

    private Map<String, Object> getColNameAndType(Set<MetaObjectAttributeDTO> setAttributes, String keyColName) throws DataSyncJobException, SQLException {
        Map<String, Object> columnNameAndType = new HashMap<>();
        String resolvedColName = keyColName;
        DataType resolvedColType = null;
        for (MetaObjectAttributeDTO attribute : setAttributes) {
            // If no keyColName provided, find the primary key
            if (resolvedColName == null || resolvedColName.isEmpty()) {
                if (Boolean.TRUE.equals(attribute.isPrimary())) {
                    resolvedColName = attribute.getDbColumn();
                    resolvedColType = attribute.getDataType();
                    columnNameAndType.put("COL_NAME", resolvedColName);
                    columnNameAndType.put("COL_TYPE", resolvedColType);
                    break;
                }
            } else if (attribute.getDbColumn().equals(resolvedColName)) {
                resolvedColType = attribute.getDataType();
                columnNameAndType.put("COL_NAME", resolvedColName);
                columnNameAndType.put("COL_TYPE", resolvedColType);
                break;
            }
        }
        return columnNameAndType;
    }

    private List<Map<String, Object>> getCompositePKColNameAndType(Set<MetaObjectAttributeDTO> setAttributes, String keyColName) throws DataSyncJobException, SQLException {
        List<Map<String, Object>> compositePKList = new ArrayList<>();
        String resolvedColName = keyColName;
        DataType resolvedColType = null;
        for (MetaObjectAttributeDTO attribute : setAttributes) {
            // If no keyColName provided, find the primary key
            if (Boolean.TRUE.equals(attribute.isPrimary())) {
                Map<String, Object> columnNameAndType = new HashMap<>();
                resolvedColName = attribute.getDbColumn();
                resolvedColType = attribute.getDataType();
                columnNameAndType.put("COL_NAME", resolvedColName);
                columnNameAndType.put("COL_TYPE", resolvedColType);
                compositePKList.add(columnNameAndType);
                //break;
            }
        }
        return compositePKList;
    }

    private DataSet readDataForSpecificIds(MetaObjectDTO metaObj, int hierarchyIndex, PrimaryKeyInfo primaryKeyInfo, List<?> objIds, Connection connection, RegionalJobContext regionalJobCtx, String tableType, LocalDateTime lastUpdatedTime) throws DataSyncJobException, SQLException {
        DataSet dataSet = new DataSet();
        String tenantId = (regionalJobCtx.getValue(RegionalJobContext.REGIONAL_TENANT_ID) == null) ? null : regionalJobCtx.getValue(RegionalJobContext.REGIONAL_TENANT_ID).toString();
        String execType = (regionalJobCtx.getValue(EXEC_TYPE) == null) ? null : regionalJobCtx.getValue(EXEC_TYPE).toString();
        if (execType != null) {
            if (execType.equalsIgnoreCase("real_time")) {
                execType = "REAL_TIME";
            } else if (execType.equalsIgnoreCase("batch")) {
                execType = "BATCH";
            }
        }
        if (tableType != null && tableType.equalsIgnoreCase("lookup")) {
            String selectQuery = SQLBuilder.getSelectStatementForSpecificIds(metaObj, null, null, primaryKeyInfo, null, tableType);
            processDataCollection(dataSet, metaObj, selectQuery, objIds, primaryKeyInfo, execType, tenantId, connection, tableType,lastUpdatedTime); //making foreign key as null when primary key is present
        }
        
        Object lastCompletedAtValue = regionalJobCtx.getValue(RegionalJobContext.LAST_COMPLETED_AT);
        LocalDateTime completeTime = (lastCompletedAtValue == null || !(lastCompletedAtValue instanceof String))
                ? null
                : LocalDateTime.parse((String) lastCompletedAtValue);
        dataSet.setMetaObject(metaObj);
        dataSet.setHierarchyIndex(hierarchyIndex);
        

        List<?> objectIds = null;
        if (objIds != null && !objIds.isEmpty()) {
            objectIds = objIds.stream()
                    .filter(obj -> obj != null && !"null".equalsIgnoreCase(obj.toString().trim()))
                    .collect(Collectors.toList());
        }

        if (("REAL_TIME".equalsIgnoreCase(execType) &&  objectIds != null && !objectIds.isEmpty()) || "BATCH".equalsIgnoreCase(execType)) {
            String selectQuery = SQLBuilder.getSelectStatementForSpecificIds(metaObj, completeTime, objectIds, primaryKeyInfo, lastUpdatedTime, tableType);
            processDataCollection(dataSet, metaObj, selectQuery, objIds, primaryKeyInfo, execType, tenantId, connection, tableType, lastUpdatedTime); //making foreign key as null when primary key is present
        }

        return dataSet;
    }

    private void processDataCollection(DataSet dataSet, MetaObjectDTO metaObj, String selectQuery, List<?> objIds, PrimaryKeyInfo pkInfo, String clientId, String tenantId, Connection connection, String tableType,LocalDateTime acceptedTimeMinfewSec) throws DataSyncJobException, SQLException {
        ResultSet resultSet = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            if (tableType != null && tableType.equalsIgnoreCase("lookup")) {
                log.info("Executing Lookup Query: {}", selectQuery);
                resultSet = preparedStatement.executeQuery();
                if (resultSet != null) {
                    dataSet.setDataRows(dataSet.getDataRowsFromResultSet(resultSet, metaObj));
                }
            } else {
                int paramIndex = 1;
                int index = paramIndex;

                // Whitelist validation for tenantId and clientId to prevent SQL injection in SET statements
                // Tenant and client context should be managed outside of SQL. Removed SET statements to eliminate SQL injection risk.
                // The above regex validations ensure tenantId and clientId are safe for SQL usage and prevent SQL injection.
                setTenantIdAndClientId(tenantId, connection);
                // sanitize the completeTime and objIds before using them in the query
                try {
                    if (objIds != null && !objIds.isEmpty() && objIds.get(0) instanceof Map) {
                        index = geExecutableStatementForCompositeKeys(objIds, preparedStatement, paramIndex, pkInfo);
                    } else {
                        index = geExecutableStatement(objIds, pkInfo.getPkDataType(), preparedStatement, paramIndex);
                    }
                    String column  = SQLBuilder.getTimestampColumnWithFallback(metaObj);
                    if(!tableType.equalsIgnoreCase("parent") && column!=null && !column.isEmpty() && acceptedTimeMinfewSec != null){
                        index++;
                        log.info("Setting acceptedTimeMinfewSec at index {}: {}", index, acceptedTimeMinfewSec);

                        preparedStatement.setObject(index, acceptedTimeMinfewSec);
                    }
                    log.info("SELECT query to be executed - "+selectQuery);
                    resultSet = preparedStatement.executeQuery();
                } catch (SQLException e) {
                    log.error("Error executing query", e);
                    throw new CustomError(JOB_READ_ERROR.getMessage(), JOB_READ_ERROR.getCode());
                }
                if (resultSet != null) {
                    //dataSet.setDataRows(dataSet.getDataRowsFromResultSet(resultSet));
                    dataSet.setDataRows(dataSet.getDataRowsFromResultSet(resultSet, metaObj));
                }
                if (resultSet == null) {
                    log.info("No data found for the query: {}", selectQuery);
                }
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

    private static int geExecutableStatement(List<?> objIds, DataType primaryKeyColumnType, PreparedStatement preparedStatement, int paramIndex) throws SQLException {
        int index = paramIndex;
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
                index =paramIndex+i;
            }
        }
        return index;
    }

    private static int geExecutableStatementForCompositeKeys(List<?> objIds, PreparedStatement preparedStatement, int paramIndex, PrimaryKeyInfo pkInfo) throws SQLException {
        log.info("Params: {}", objIds.toString());
        // Extract PK column names and types
        List<Map<String, Object>> compositePKColNameAndType = pkInfo.getCompositePKColNameAndType();
        List<String> pkNames = new ArrayList<>();
        List<DataType> pkTypes = new ArrayList<>();
        for (Map<String, Object> map : compositePKColNameAndType) {
            pkNames.add(map.get("COL_NAME").toString());
            pkTypes.add((DataType) map.get("COL_TYPE"));
        }

        // Build a map of column name to list of values
        Map<String, List<String>> pkValuesMap = new LinkedHashMap<>();
        for (Object obj : objIds) {
            if (obj instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) obj;
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    pkValuesMap.put(entry.getKey().toString(), (List<String>) entry.getValue());
                }
            }
        }

        // Number of tuples to bind
        int tupleCount = pkValuesMap.values().iterator().next().size();

        int paramCounter = paramIndex;
        for (int i = 0; i < tupleCount; i++) {
            for (int j = 0; j < pkNames.size(); j++) {
                String pkName = pkNames.get(j);
                DataType pkType = pkTypes.get(j);
                String value = pkValuesMap.get(pkName).get(i);
                if (pkType == null) {
                    preparedStatement.setObject(paramCounter++, value);
                } else {
                    switch (Objects.requireNonNull(pkType)) {
                        case STRING, TEXT:
                            preparedStatement.setString(paramCounter++, value);
                            break;
                        case INTEGER:
                            preparedStatement.setInt(paramCounter++, Integer.parseInt(value));
                            break;
                        case UUID:
                            try {
                                preparedStatement.setObject(paramCounter++, UUID.fromString(value));
                            } catch (IllegalArgumentException e) {
                                log.error("Invalid UUID format for objId: {}", value, e);
                                throw new CustomError(JOB_READ_ERROR.getMessage(), JOB_READ_ERROR.getCode());
                            }
                            break;
                        case BIGINT:
                            try {
                                preparedStatement.setLong(paramCounter++, Long.parseLong(value));
                            } catch (NumberFormatException e) {
                                log.error("Invalid BIGINT format for objId: {}", value, e);
                                throw new CustomError(JOB_READ_ERROR.getMessage(), JOB_READ_ERROR.getCode());
                            }
                            break;
                        default:
                            throw new IllegalArgumentException("Unsupported id column data type: " + pkType);
                    }
                }
            }
        }
        paramIndex = paramCounter;
        return paramIndex;
    }


    /**
     * Retrieves the latest updated_at timestamp from the database for the given object IDs
     * This method executes a query to get the maximum updated_at value from the specified table
     * using the provided object IDs in descending order.
     */
    private LocalDateTime getLatestUpdatedAtFromDatabase(MetaObjectDTO metaObj, List<?> objIds, PrimaryKeyInfo primaryKeyInfo, Connection connection, String tenantId) throws SQLException, DataSyncJobException {
        if (metaObj == null || objIds == null || objIds.isEmpty()) {
            log.warn("MetaObject or objIds is null/empty, cannot fetch latest updated_at");
            return null;
        }

        String column = SQLBuilder.getTimestampColumnWithFallback(metaObj);
        StringBuilder selectQuery = new StringBuilder();

        selectQuery.append("SELECT MIN(")
                .append(column)
                .append(")) AS latest_timestamp FROM ")
                .append(metaObj.getSchema())
                .append(".")
                .append(metaObj.getDbTable());


        if (primaryKeyInfo.isComposite() && objIds.get(0) instanceof Map) {
            // Handle composite primary keys
            List<Map<String, Object>> compositePKColNameAndType = primaryKeyInfo.getCompositePKColNameAndType();
            List<String> pkNames = SQLBuilder.extractAllColNamesFromCompositePK(compositePKColNameAndType);
            selectQuery.append(" WHERE (").append(String.join(", ", pkNames)).append(") IN (");
            
            @SuppressWarnings("unchecked")
            List<Map<String, List<String>>> compositeObjIds = (List<Map<String, List<String>>>) objIds;
            List<String> listOfValues = compositeObjIds.get(0)
                    .entrySet()
                    .iterator()
                    .next()
                    .getValue();
            int tupleCount = listOfValues.size();
            int pkSize = pkNames.size();
            
            for (int i = 0; i < tupleCount; i++) {
                selectQuery.append("(");
                for (int j = 0; j < pkSize; j++) {
                    selectQuery.append("?");
                    if (j < pkSize - 1) {
                        selectQuery.append(", ");
                    }
                }
                selectQuery.append(")");
                if (i < tupleCount - 1) {
                    selectQuery.append(", ");
                }
            }
            selectQuery.append(")");
        } else {
            // Handle single primary key
            selectQuery.append(" WHERE ").append(primaryKeyInfo.getPkName()).append(" IN (");
            for (int i = 0; i < objIds.size(); i++) {
                selectQuery.append("?");
                if (i < objIds.size() - 1) {
                    selectQuery.append(", ");
                }
            }
            selectQuery.append(")");
        }

        LocalDateTime latestTimestamp = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery.toString())) {
            // Set tenant context
            setTenantIdAndClientId(tenantId, connection);
            
            // Set parameters based on primary key type
            if (primaryKeyInfo.isComposite() && objIds.get(0) instanceof Map) {
                geExecutableStatementForCompositeKeys(objIds, preparedStatement, 1, primaryKeyInfo);
            } else {
                geExecutableStatement(objIds, primaryKeyInfo.getPkDataType(), preparedStatement, 1);
            }
            
            log.info("Executing query to get latest updated_at timestamp: {}", selectQuery.toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Timestamp timestamp = resultSet.getTimestamp("latest_timestamp");
                    if (timestamp != null) {
                        latestTimestamp = timestamp.toLocalDateTime();
                        log.info("Latest updated_at timestamp found: {} for table: {}.{}", 
                                latestTimestamp, metaObj.getSchema(), metaObj.getDbTable());
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Error executing query to get latest updated_at timestamp for table: {}.{}", 
                     metaObj.getSchema(), metaObj.getDbTable(), e);
            throw new DataSyncJobException("Error fetching latest updated_at timestamp", INTERNAL_SERVER_ERROR);
        }
        
        return latestTimestamp;
    }

    private static void setTenantIdAndClientId(String tenantId, Connection connection) throws SQLException {
        //ToDo Remove when multi-client use case is supported
        try (Statement stmt = connection.createStatement()) {
            String sql = "SET dep.client_id = '-1'";
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

    Connection getConnection(ExecutionContext ctx) throws DataSyncJobException {
        Connection connection = null;
        RegionalJobContext regionalJobContext = (RegionalJobContext) ctx.getContextByName(ExecutionContext.REGIONAL_JOB_CONTEXT);
        GlobalContext globalContext = (GlobalContext) ctx.getContextByName(ExecutionContext.GLOBAL_CONTEXT);
        String onesourceDomain = regionalJobContext.getValue(RegionalJobContext.ONESOURCE_DOMAIN).toString();
        String onesourceRegion = globalContext.getValue(GlobalContext.HOST_REGION).toString();
        String regionalTenantId = regionalJobContext.getValue(RegionalJobContext.REGIONAL_TENANT_ID) == null ? null : regionalJobContext.getValue(RegionalJobContext.REGIONAL_TENANT_ID).toString();
        String systemName = regionalJobContext.getValue(RegionalJobContext.META_OBJECT_SYS_NAME) == null ? null : regionalJobContext.getValue(RegionalJobContext.META_OBJECT_SYS_NAME).toString();

        assert systemName != null;
        String domainSysName = systemName.substring(0, systemName.lastIndexOf('.'));
        DataSource dataSource = dataSourceApiService.getFilteredDataSource(metaModelServiceUrl,
                regionalTenantId,
                onesourceDomain,
                onesourceRegion,
                domainSysName);

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
            throw new DataSyncJobException("Error establishing database connection", INTERNAL_SERVER_ERROR);
        }
        return connection;
    }


    @Override
    public void writeData(DataSetCollection data, ExecutionContext ctx) throws DataSyncJobException {
        Connection connection = null;
        transitHubDataSetCollection = new ArrayList<>();
        RegionalJobContext regionalJobCtx = (RegionalJobContext) ctx.getContextByName(ExecutionContext.REGIONAL_JOB_CONTEXT);
        try {
            String tenantId = (regionalJobCtx.getValue(RegionalJobContext.REGIONAL_TENANT_ID) == null) ? null : regionalJobCtx.getValue(RegionalJobContext.REGIONAL_TENANT_ID).toString();
            String clientId = (regionalJobCtx.getValue(RegionalJobContext.CLIENT_ID) == null) ? null : regionalJobCtx.getValue(RegionalJobContext.CLIENT_ID).toString();

            connection = getConnection(ctx);
            if (data != null && data.getDataSets() != null) {
                for (DataSet dataSet : data.getDataSets()) {
                    try {
                        setTenantIdAndClientId(tenantId, connection);
                        // Initialize transformation engine and apply transformations
                        Map<String, List<ConcurrentHashMap<String, Object>>> dataObjects = DataProcessor.divideList(connection, dataSet);
                        executeDataOperations(connection, dataSet.getMetaObject(), dataObjects, ctx);
                        log.info("Data written successfully for MetaObject: {}", dataSet.getMetaObject().getSystemName());
                        buildTransitHubDataSetCollection(dataSet, dataObjects);
                    } catch (SQLException e) {
                        log.error("Error writing data for MetaObject: {}", dataSet.getMetaObject().getSystemName(), e);
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
                setPreparedStatementParameters(preparedStatement, dataObject, metaObjectDTO, INSERT_LIST_KEY, insertQuery);
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
            log.info("Completed executeInsert for metaObject: {}", metaObjectDTO.getDbTable());
        } catch (SQLException e) {
            connection.rollback();
            log.error("Error executing insert for metaObject: {}", metaObjectDTO.getDbTable(), e);
            throw new DataSyncJobException("Failed to execute insert for metaObject: " + metaObjectDTO.getDbTable(), JOB_WRITE_ERROR.getCode());

        }
    }

    private void executeUpdate(Connection connection, MetaObjectDTO metaObjectDTO, List<ConcurrentHashMap<String, Object>> dataObjects) throws SQLException, DataSyncJobException {
        log.info("Executing update for {} records", dataObjects.size());
        List<String> primaryKeyColumns = getPrimaryKeyColumns(metaObjectDTO);
        String schemaAppendedTableName = metaObjectDTO.getSchema() + "." + metaObjectDTO.getDbTable();

        try {
            connection.setAutoCommit(false); // Enable transaction management
            int count = 0;

            String updateQuery = SQLBuilder.createUpdateQuery(schemaAppendedTableName, metaObjectDTO, primaryKeyColumns);
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                for (ConcurrentHashMap<String, Object> dataObject : dataObjects) {
                    if (dataObject == null) {
                        log.warn("Null dataObject encountered in executeUpdate. Skipping entry.");
                        continue;
                    }
                    //TODO move applyTransformation to DataSyncExecution Initialize transformation engine and apply transformations
                    //dataObject = applyTransformation(ctx, dataObject);
                    setPreparedStatementParameters(preparedStatement, dataObject, metaObjectDTO, UPDATE_LIST_KEY, updateQuery);
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
            log.info("Batch update completed successfully for table: {}", schemaAppendedTableName);
        } catch (SQLException e) {
            connection.rollback(); // Rollback transaction in case of error
            log.error("Error executing batch update for table: {}", schemaAppendedTableName, e);
            throw new DataSyncJobException("Error executing batch update", JOB_WRITE_ERROR.getCode());
        }
    }


    private void setPreparedStatementParameters(PreparedStatement preparedStatement, ConcurrentHashMap<String, Object> dataObject, MetaObjectDTO metaObject, String operationType, String updateQuery) throws SQLException {
        Set<MetaObjectAttributeDTO> setAttributes = metaObject.getAttributes();
        Map<String, MetaObjectAttributeDTO> attributes = DataProcessor.convertSetToMap(setAttributes);
        Map<String, Map<Object, DataType>> primaryKeys = new HashMap<>();
        int index = 1;
        Object primaryKeyValue = null;
        DataType primaryKeyDataType = null;
        for (Map.Entry<String, MetaObjectAttributeDTO> entry : attributes.entrySet()) {
            MetaObjectAttributeDTO attribute = entry.getValue();
            Object value = dataObject.get(attribute.getDbColumn());

            if (attribute.isPrimary()) {
                Map<Object, DataType> primaryKeyNamesAndTypes = new HashMap<>();
                primaryKeyValue = value; // Store the primary key value
                primaryKeyDataType = attribute.getDataType();
                primaryKeyNamesAndTypes.put(primaryKeyValue, primaryKeyDataType);
                primaryKeys.put(attribute.getDbColumn(), primaryKeyNamesAndTypes);// Store the primary key data type
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
                    PGobject jsonObject = new PGobject();
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
                    preparedStatement.setNull(index, Types.ARRAY);
                } else {
                    String[] arrayValue;
                    if (value instanceof String[]) {
                        arrayValue = (String[]) value;
                    } else if (value instanceof List) {
                        arrayValue = ((List<?>) value).toArray(new String[0]);

                    } else {
                        throw new SQLException("Unsupported value for TEXT_ARRAY: " + value);
                    }
                    Array sqlArray = preparedStatement.getConnection().createArrayOf("text", arrayValue);
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
        if (UPDATE_LIST_KEY.equals(operationType) && primaryKeys != null && !primaryKeys.isEmpty()) {
            String whereClause = updateQuery.substring(updateQuery.indexOf("WHERE") + 5).trim();

            // Split the WHERE clause to get individual conditions
            String[] conditions = whereClause.split("AND");

            for (String condition : conditions) {
                // Extract the column name (before the '=' sign)
                String colName = condition.split("=")[0].trim();
                for (Map.Entry<String, Map<Object, DataType>> outerEntry : primaryKeys.entrySet()) {
                    if (outerEntry.getKey().equals(colName)) {
                        Map<Object, DataType> innerMap = outerEntry.getValue(); // Inner map

                        for (Map.Entry<Object, DataType> innerEntry : innerMap.entrySet()) {
                            primaryKeyValue = innerEntry.getKey(); // Primary key value
                            primaryKeyDataType = innerEntry.getValue(); // Data type of the primary key

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
                }
                index++;
            }
        }
    }

    private List<String> getPrimaryKeyColumns(MetaObjectDTO metaObjectDTO) {
        List<String> primaryKeyColumns = new ArrayList<>();
        for (MetaObjectAttributeDTO attribute : metaObjectDTO.getAttributes()) {
            if (attribute.isPrimary()) {
                primaryKeyColumns.add(attribute.getDbColumn());
            }
        }
        return primaryKeyColumns;
    }

    private void buildTransitHubDataSetCollection(DataSet dataSet, Map<String, List<ConcurrentHashMap<String, Object>>> dataObjects) {
        if (dataSet == null || dataSet.getMetaObject() == null || dataObjects == null) {
            log.error("Invalid input: DataSet, MetaObject, or dataObjects is null");
            return;
        }

        MetaObjectDTO metaObject = dataSet.getMetaObject();
        List<DataSet> dataSets = new ArrayList<>();
        // Process insertList
        List<ConcurrentHashMap<String, Object>> insertList = dataObjects.get(INSERT_LIST_KEY);
        if (insertList != null && !insertList.isEmpty()) {
            DataSet insertDataSet = new DataSet();
            insertDataSet.setDataSetName(INSERT_DATA_SET);
            insertDataSet.setMetaObject(metaObject);
            insertDataSet.setHierarchyIndex(dataSet.getHierarchyIndex());
            insertDataSet.setDataRows(convertToDataRows(insertList));
            dataSets.add(insertDataSet);
        }
        // Process updateList
        List<ConcurrentHashMap<String, Object>> updateList = dataObjects.get(UPDATE_LIST_KEY);
        if (updateList != null && !updateList.isEmpty()) {
            DataSet updateDataSet = new DataSet();
            updateDataSet.setDataSetName(UPDATE_DATA_SET);
            updateDataSet.setMetaObject(metaObject);
            updateDataSet.setHierarchyIndex(dataSet.getHierarchyIndex());
            updateDataSet.setDataRows(convertToDataRows(updateList));
            dataSets.add(updateDataSet);
        }
        if (!dataSets.isEmpty()) {
            DataSetCollection dataSetCollection = new DataSetCollection();
            dataSetCollection.setDataSets(dataSets);
            transitHubDataSetCollection.add(dataSetCollection);
        }

        log.info("TransitHub DataSetCollection built successfully with {} collections", transitHubDataSetCollection.size());
    }

    private List<DataRow> convertToDataRows(List<ConcurrentHashMap<String, Object>> dataList) {
        return dataList.stream()
                .map(dataObject -> {
                    DataRow dataRow = new DataRow();
                    dataRow.setRow(dataObject);
                    return dataRow;
                })
                .collect(Collectors.toList());
    }


    public static List<Map<String, List<String>>> convertSanitizedObjIdsToOriginal(List<String> sanitizedObjIds) {
        LinkedHashMap<String, List<String>> grouped = new LinkedHashMap<>();
        if (sanitizedObjIds == null) {
            return new ArrayList<>();
        }
        for (String entry : sanitizedObjIds) {
            String[] parts = entry.split(":", 2);
            if (parts.length == 2) {
                grouped.computeIfAbsent(parts[0], k -> new ArrayList<>()).add(parts[1]);
            }
        }
        List<Map<String, List<String>>> originalObjIds = new ArrayList<>();
        for (Map.Entry<String, List<String>> e : grouped.entrySet()) {
            Map<String, List<String>> map = new HashMap<>();
            map.put(e.getKey(), e.getValue());
            originalObjIds.add(map);
        }
        return originalObjIds;
    }

    @Override
    public void cleanUp() {
    }
}
