package com.thomsonreuters.dataconnect.executionengine.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomsonreuters.dataconnect.executionengine.data.DataRow;
import com.thomsonreuters.dataconnect.executionengine.data.DataSet;
import com.thomsonreuters.dataconnect.executionengine.dto.DatasyncJobConfigurationRequestDTO;
import com.thomsonreuters.dataconnect.executionengine.dto.MetaObjectAttributeDTO;
import com.thomsonreuters.dataconnect.executionengine.dto.MetaObjectDTO;
import com.thomsonreuters.dataconnect.executionengine.exceptionhandler.DataSyncJobException;
import com.thomsonreuters.dataconnect.executionengine.model.entity.DatasyncJobConfiguration;
import com.thomsonreuters.dataconnect.executionengine.model.entity.enums.AdapterType;
import com.thomsonreuters.dataconnect.executionengine.model.entity.enums.DataType;
import com.thomsonreuters.dataconnect.executionengine.model.entity.enums.ErrorConstant;
import com.thomsonreuters.dataconnect.executionengine.repository.DatasyncJobConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static com.thomsonreuters.dataconnect.executionengine.constant.Constants.BAD_REQUEST;
import static com.thomsonreuters.dataconnect.executionengine.constant.Constants.INSERT_LIST_KEY;
import static com.thomsonreuters.dataconnect.executionengine.constant.Constants.UPDATE_LIST_KEY;
import static com.thomsonreuters.dataconnect.executionengine.model.entity.enums.ErrorConstant.JOB_ID_ERROR;
import static com.thomsonreuters.dataconnect.executionengine.utils.SQLBuilder.getPrimaryKeyColumns;

@Component
@Slf4j
public class DataProcessor {

    @Autowired
    private DatasyncJobConfigRepository datasyncJobConfigRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ObjectMapper objectMapper;
    private DataProcessor() {
        // Private constructor to prevent instantiation
    }

    public static Map<String, List<ConcurrentHashMap<String, Object>>> divideList(Connection connection, DataSet dataset) throws DataSyncJobException {
        MetaObjectDTO metaObject = dataset.getMetaObject();
        log.info("Dividing list into insert and update lists {}", metaObject.getDbTable());
        //String id = getPrimaryKeyColumns(metaObject);
        List<String> ids= getPrimaryKeyColumns(metaObject);
        List<ConcurrentHashMap<String, Object>> insertList = new ArrayList<>();
        List<ConcurrentHashMap<String, Object>> updateList = new ArrayList<>();
        // Convert DataRow list to List<ConcurrentHashMap<String, Object>>
        List<ConcurrentHashMap<String, Object>> sourcedataObjects = new ArrayList<>();
        List<DataRow> dataRows = dataset.getDataRows();
        if (dataRows != null) {
            for (DataRow row : dataRows) {
                if (row != null && row.getRow() != null) {
                    sourcedataObjects.add(row.getRow());
                }
            }
        }
        if(!sourcedataObjects.isEmpty()) {
            String query = SQLBuilder.selectCreatedAtUpdatedAtAndIds(metaObject, sourcedataObjects, ids);
            Set<MetaObjectAttributeDTO> metaObjectAttributeDTOS = metaObject.getAttributes().stream()
                    .filter(a -> ids.contains(a.getDbColumn()))
                    .collect(Collectors.toSet());
            Map<String, MetaObjectAttributeDTO> attributes = DataProcessor.convertSetToMap(metaObjectAttributeDTOS);

            List<ConcurrentHashMap<String, Object>> targetDataObject = new ArrayList<>();
            try {
                ResultSet resultSet = connection.createStatement().executeQuery(query);
                while (resultSet.next()) {
                    targetDataObject.add(putRetValue(attributes, resultSet));
                }
            } catch (SQLException e) {
                log.error("Error while dividing list", e);
                throw new DataSyncJobException(ErrorConstant.JOB_READ_ERROR.getMessage(), ErrorConstant.JOB_READ_ERROR.getCode());
            }
            Map<Object, ConcurrentHashMap<String, Object>> targetDataMap = targetDataObject.stream()
                    .flatMap(record -> ids.stream()
                            .map(id -> Map.entry(
                                    record.get(id) != null ? record.get(id).toString() : "null",
                                    record
                            ))
                    )
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (existing, replacement) -> existing // Handle duplicate keys by keeping the first entry
                    ));
            sourcedataObjects.forEach(sourceRecord -> {
                boolean existsInTarget = ids.stream()
                        .allMatch(id -> targetDataMap.containsKey(sourceRecord.get(id).toString()));

                if (!existsInTarget) {
                    insertList.add(sourceRecord);
                } else {
                    updateList.add(sourceRecord);
                }
            });
        }

        Map<String, List<ConcurrentHashMap<String, Object>>> result = new HashMap<>();
        result.put(INSERT_LIST_KEY, insertList);
        result.put(UPDATE_LIST_KEY, updateList);
        log.info("Divided list into insert and update lists successfully {}", metaObject.getDbTable());
        return result;
    }

    public DatasyncJobConfigurationRequestDTO getJobConfiguration(UUID id) throws DataSyncJobException {
        DatasyncJobConfiguration job = datasyncJobConfigRepository.findById(id)
                .orElseThrow(() -> new DataSyncJobException(JOB_ID_ERROR.getMessage(), JOB_ID_ERROR.getCode()));
        return convertToDTO(job);
    }

    private DatasyncJobConfigurationRequestDTO convertToDTO(DatasyncJobConfiguration job) {
        return modelMapper.map(job, DatasyncJobConfigurationRequestDTO.class);
    }
    public static Map<String, List<ConcurrentHashMap<String, Object>>> divideList(List<ConcurrentHashMap<String, Object>> dataObject, LocalDateTime lastJobcompletedDate,String createdAt, String updatedAt) {
//        List<ConcurrentHashMap<String, Object>> insertList = dataObject.stream()
//                .filter(map -> map.get(createdAt).equals(map.get(updatedAt)) && LocalDateTime.parse(map.get(createdAt).toString()).isAfter(lastJobcompletedDate))
//                .toList();
        List<ConcurrentHashMap<String, Object>> insertList = new ArrayList<>();
        List<ConcurrentHashMap<String, Object>> updateList = new ArrayList<>();
        Map<String, List<ConcurrentHashMap<String, Object>>> result = new HashMap<>();


        if( lastJobcompletedDate == null) {
            insertList = dataObject.stream().toList();
            result.put("insertList", insertList);
        }
        else {
            insertList = dataObject.stream()
                    .filter(map -> {
                        Object createdAtValue = map.get(createdAt);
                        Object updatedAtValue = map.get(updatedAt);

                        // Debugging and null checks
                        if (createdAtValue == null || updatedAtValue == null) {
                            log.info("Key not found or value is null: createdAt=" + createdAtValue + ", updatedAt=" + updatedAtValue);
                            return false;
                        }

                        try {
                            LocalDateTime createdAtDateTime = LocalDateTime.parse(createdAtValue.toString());
                            LocalDateTime updatedAtDateTime = LocalDateTime.parse(updatedAtValue.toString());

                            return createdAtDateTime.equals(updatedAtDateTime) || createdAtDateTime.isAfter(lastJobcompletedDate);

                        } catch (Exception e) {
                            log.info("Error parsing dates: " + e.getMessage());

                            return false;
                        }
                    })
                    .toList();

            updateList = dataObject.stream()
                    .filter(map -> LocalDateTime.parse(map.get(createdAt).toString()).isBefore(LocalDateTime.parse(map.get(updatedAt).toString())) || LocalDateTime.parse(map.get(updatedAt).toString()).isAfter(lastJobcompletedDate))
                    .collect(Collectors.toList());
            result.put("insertList", insertList);
            result.put("updateList", updateList);
        }
        return result;
    }

    public static Map<String,List<String>> processData(List<ConcurrentHashMap<String, Object>> dataObject, MetaObjectDTO metaObjectDTO, LocalDateTime lastJobcompletedDate) throws DataSyncJobException {

        String createdAt = SQLBuilder.getCreatedAt(metaObjectDTO);
        String updatedAt = SQLBuilder.getUpdatedAt(metaObjectDTO);
        Map<String, List<ConcurrentHashMap<String, Object>>> dividedLists = divideList(dataObject, lastJobcompletedDate, createdAt, updatedAt);

        List<ConcurrentHashMap<String, Object>> insertList = dividedLists.get("insertList");
        List<ConcurrentHashMap<String, Object>> updateList = dividedLists.get("updateList");
        List<String> insertQuery =null;
        List<String> updateQuery =null;
        Map<String,List<String>> result = new HashMap<>();
        // Send to SQL Builder
        for (ConcurrentHashMap<String, Object> stringObjectConcurrentHashMap : insertList) {
            String query = sendToInsertSQLBuilder(stringObjectConcurrentHashMap,metaObjectDTO);
            insertQuery.add(query);
        }
        for (ConcurrentHashMap<String, Object> stringObjectConcurrentHashMap : updateList) {
            String query = sendToUpdateSQLBuilder(stringObjectConcurrentHashMap,metaObjectDTO);
            updateQuery.add(query);
        }
        result.put("insertQuery", insertQuery);
        result.put("updateQuery", updateQuery);
        return result;
    }

    public static String sendToInsertSQLBuilder(ConcurrentMap<String, Object> map, MetaObjectDTO metaObjectDTO) {
        // Insert statement logic
        String schemaAppendedtableName = null;
        if (metaObjectDTO != null) {
            schemaAppendedtableName = metaObjectDTO.getSchema()+"."+metaObjectDTO.getDbTable(); // Assuming MetaObjectDTO has a method getTableName()
        }
        // Assuming MetaObjectDTO is defined elsewhere
        return createInsertQuery(schemaAppendedtableName,map);
    }

    public static String sendToUpdateSQLBuilder(ConcurrentMap<String, Object> map,MetaObjectDTO metaObjectDTO) {
        // Update statement logic
        // Assuming MetaObjectDTO is defined elsewhere
        return createUpdateQuery(metaObjectDTO ,map,"id"); // Assuming 'id' is the primary key column for the table, replace with actual primary key column if different
    }

    public static String getInsertStatement(MetaObjectDTO model) {
        // Insert statement logic
        StringBuilder insert = new StringBuilder("INSERT INTO ");
        insert.append(model.getSchema()).append(".").append(model.getDbTable());
        insert.append(" (");
        Set<MetaObjectAttributeDTO> setAttributes = model.getAttributes();
        Map<String, MetaObjectAttributeDTO> attributes = DataProcessor.convertSetToMap(setAttributes);
        attributes.forEach((key, value) -> insert.append(value.getDbColumn()).append(", "));
        insert.delete(insert.length() - 2, insert.length());
        insert.append(") VALUES (");
        attributes.forEach((key, value) -> insert.append("?, "));
        insert.delete(insert.length() - 2, insert.length());
        insert.append(")");
        return insert.toString();
    }

    public static String getUpdateStatement(MetaObjectDTO model) {
        // Update statement logic
        StringBuilder update = new StringBuilder("UPDATE ");
        update.append(model.getSchema()).append(".").append(model.getDbTable());
        update.append(" SET ");
        Set<MetaObjectAttributeDTO> setAttributes = model.getAttributes();
        Map<String, MetaObjectAttributeDTO> attributes = DataProcessor.convertSetToMap(setAttributes);

        attributes.forEach((key, value) -> update.append(value.getDbColumn()).append(" = ?, "));
        update.delete(update.length() - 2, update.length());
        update.append(" WHERE id = ?");
        return update.toString();
    }

    public static String createInsertQuery(String schemaAppendedTableName, ConcurrentMap<String, Object> dataObject) {
        if (dataObject == null || dataObject.isEmpty()) {
            throw new IllegalArgumentException("Data object cannot be null or empty");
        }

        StringJoiner columns = new StringJoiner(", ", "(", ")");
        StringJoiner placeholders = new StringJoiner(", ", "(", ")");
        for (String column : dataObject.keySet()) {
            columns.add(column);
            placeholders.add("?");
        }

        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO ").append(schemaAppendedTableName).append(" ")
                .append(columns.toString()).append(" VALUES ")
                .append(placeholders.toString());

        return query.toString();
    }

    public static String createUpdateQuery(MetaObjectDTO metaObjectDTO, ConcurrentMap<String, Object> dataObject, String idColumn) {
        if (dataObject == null || dataObject.isEmpty()) {
            throw new IllegalArgumentException("Data object cannot be null or empty");
        }
        metaObjectDTO.getAttributes()
                .stream()
                .filter(entry -> entry == null)
                .forEach(entry -> {
                    throw new IllegalArgumentException("MetaObjectDTO contains null attributes, cannot build update query");
                });

        StringBuilder query = new StringBuilder();
        query.append("UPDATE ").append(metaObjectDTO.getSchema()).append(".").append(metaObjectDTO.getDbTable()).append(" SET ");

        StringJoiner setClause = new StringJoiner(", ");
        for (String column : dataObject.keySet()) {
            setClause.add(column + " = ?");
        }

        query.append(setClause.toString());
        query.append(" WHERE ").append(idColumn).append(" = ?");

        return query.toString();
    }

    public static void sendToUpdateSQLBuilder(ConcurrentMap<String, Object> stringObjectConcurrentHashMap) {
        //yet to implement
    }

    public static Map<String, MetaObjectAttributeDTO> convertSetToMap(Set<MetaObjectAttributeDTO> setAttributes){
        Map<String, MetaObjectAttributeDTO> attributes = new LinkedHashMap<>();
        for (MetaObjectAttributeDTO attribute : setAttributes) {
            if (attribute != null) {
                attributes.put(attribute.getDbColumn(), attribute);
            }
        }
        return attributes;
    }
    public static DataStreamObject dataCollectionToStreamConverter(Header header, DataCollectionObject dataCollectionObject) throws DataSyncJobException,SQLException {
        ResultSet resultSet = dataCollectionObject.getDbResult();
        DataStreamObject dataStreamObject = new DataStreamObject();
        List<ConcurrentHashMap<String, Object>> resultSetListParent = new ArrayList<>();
        List<ConcurrentHashMap<String, Object>> resultSetListChild = new ArrayList<>();
        if (resultSet != null && resultSet.next()) {
            do {
                resultSetListParent.add(createObjectMaps(dataCollectionObject.getMetaModel(), resultSet)); // Create the initial object map from the ResultSet
            } while (resultSet.next());
        }
        List<DataCollectionObject> childList = (dataCollectionObject.getChildList() != null && !dataCollectionObject.getChildList().isEmpty())
                ? new ArrayList<>(dataCollectionObject.getChildList().values())
                : new ArrayList<>();
        MetaObjectDTO childMetaObjectDTO = null;
        ResultSet childResultSet = null;
        if (!childList.isEmpty()) {
            childMetaObjectDTO = childList.get(0).getMetaModel();
            childResultSet = childList.get(0).getDbResult();
            dataStreamObject.setChildMetaModel(childList.get(0).getMetaModel());
        }
        if (childResultSet != null && childResultSet.next() && childMetaObjectDTO != null) {
            do {
                resultSetListChild.add(createObjectMaps(childMetaObjectDTO, childResultSet)); // Create the initial object map from the ResultSet
            } while (childResultSet.next());
        }
        dataStreamObject.setParentMetaModel(dataCollectionObject.getMetaModel());
        dataStreamObject.setParentDataObject(resultSetListParent); // Set the parent data object list
        dataStreamObject.setChildDataObject(resultSetListChild); // Set the child data object list
        DatasyncMessage datasyncMessage = new DatasyncMessage();
        DataUnit dataUnit =new DataUnit();
        dataUnit.setContent(dataStreamObject);
        datasyncMessage.setHeader(header);
        datasyncMessage.setData(dataUnit);
        return dataStreamObject;
    }

    public static ConcurrentHashMap<String, Object> createObjectMap(MetaObjectDTO model, ResultSet result) throws SQLException, DataSyncJobException {
        ConcurrentHashMap<String, Object> retVal = new ConcurrentHashMap<>();
        Set<MetaObjectAttributeDTO> setAttributes = model.getAttributes();
        Map<String, MetaObjectAttributeDTO> attributes = DataProcessor.convertSetToMap(setAttributes);

        for (Map.Entry<String, MetaObjectAttributeDTO> attribute : attributes.entrySet()) {
            DataType type = attribute.getValue().getDataType();
            MetaObjectAttributeDTO metaObjectAttributeDTO = attribute.getValue();
            if (metaObjectAttributeDTO == null) {
                log.error("Error while creating meta object from result set");
                throw new DataSyncJobException(ErrorConstant.JOB_READ_ERROR.getMessage(), ErrorConstant.JOB_READ_ERROR.getCode());
            }
            try {
                switch (type) {
                    // Grouping string-like types together as they represent textual data
                    // and are handled similarly when retrieving values from the ResultSet./**/                    case STRING, UUID,CHAR,VARCHAR2,NVARCHAR2,NCHAR:
                    case STRING, UUID,CHAR,VARCHAR2,NVARCHAR2,NCHAR:
                        String strVal = result.getString(metaObjectAttributeDTO.getDbColumn());
                        retVal.put(metaObjectAttributeDTO.getDbColumn(), strVal);
                        break;
                    case INTEGER:
                        int intVal = result.getInt(metaObjectAttributeDTO.getDbColumn());
                        retVal.put(metaObjectAttributeDTO.getDbColumn(), intVal);
                        break;
                    case LONG:
                        double dblVal = result.getLong(metaObjectAttributeDTO.getDbColumn());
                        retVal.put(metaObjectAttributeDTO.getDbColumn(), dblVal);
                        break;
                    case BIGINT:
                        long bigIntVal = result.getLong(metaObjectAttributeDTO.getDbColumn());
                        retVal.put(metaObjectAttributeDTO.getDbColumn(), bigIntVal);
                        break;
                    case SMALLINT:
                        short smallIntVal = result.getShort(metaObjectAttributeDTO.getDbColumn());
                        retVal.put(metaObjectAttributeDTO.getDbColumn(), smallIntVal);
                        break;
                    case DATETIME,TIMESTAMP:
                        LocalDateTime dateTimeVal = result.getObject(metaObjectAttributeDTO.getDbColumn(), LocalDateTime.class);
                        retVal.put(metaObjectAttributeDTO.getDbColumn(), dateTimeVal);
                        break;
                    case DATE:
                        LocalDate dateVal = result.getObject(metaObjectAttributeDTO.getDbColumn(), LocalDate.class);
                        retVal.put(metaObjectAttributeDTO.getDbColumn(), dateVal);
                        break;
                    case ARRAY:
                        Object arrayVal = result.getArray(metaObjectAttributeDTO.getDbColumn());
                        retVal.put(metaObjectAttributeDTO.getDbColumn(), arrayVal != null ? ((java.sql.Array) arrayVal).getArray() : null);
                        break;
                    case JSONB:
                        Object jsonbVal = result.getObject(metaObjectAttributeDTO.getDbColumn());
                        retVal.put(metaObjectAttributeDTO.getDbColumn(), jsonbVal != null ? jsonbVal.toString() : null);
                        break;
                    case NUMERIC:
                        java.math.BigDecimal numericVal = result.getBigDecimal(metaObjectAttributeDTO.getDbColumn());
                        retVal.put(metaObjectAttributeDTO.getDbColumn(), numericVal);
                        break;


                }
            } catch (Exception e) {
                log.error("Error while creating meta object from result set", e);
                throw new DataSyncJobException(ErrorConstant.JOB_WRITE_ERROR.getMessage(), ErrorConstant.JOB_WRITE_ERROR.getCode());
            }
        }
        return retVal;
    }

    public static List<LinkedHashMap<String, Object>> populateDataObject(MetaObjectDTO MetaObjectDTO, List<ConcurrentHashMap<String,Object>> dataObjects) throws DataSyncJobException {
        Set<MetaObjectAttributeDTO> sortedMetaAttributes = MetaObjectDTO.getAttributes();
        List<MetaObjectAttributeDTO> sortedMetaAttributeBySeq = sortMetaObjectsBySeqNum(sortedMetaAttributes);
        List<LinkedHashMap<String, Object>> dataObjectsList = new ArrayList<>();
        // Iterate over sorted meta objects and fields[]
        if(dataObjects==null && dataObjects.isEmpty()){
            throw new DataSyncJobException("No data to sync", BAD_REQUEST);
        }
        dataObjects.forEach(dataObject -> {
            LinkedHashMap<String, Object> dataObjectMap = new LinkedHashMap<>();
            for (int i = 0; i < sortedMetaAttributeBySeq.size(); i++) {
                MetaObjectAttributeDTO metaAttribute = sortedMetaAttributeBySeq.get(i);
                //String fieldValue = (String) dataObject.get(metaAttribute.getDbColumn());
                Object fieldValueObj = dataObject.get(metaAttribute.getDbColumn());
                Object fieldValue = (fieldValueObj instanceof LocalDateTime)
                        ? fieldValueObj.toString()
                        :  fieldValueObj;
                dataObjectMap.put(metaAttribute.getDbColumn(), fieldValue);
            }
            dataObjectsList.add(dataObjectMap);
        });
        return dataObjectsList;
    }

    public static List<MetaObjectAttributeDTO> sortMetaObjectsBySeqNum(Set<MetaObjectAttributeDTO> metaObjects) {
        // Convert the Set to a List and sort by seqNum
        return metaObjects.stream().sorted(Comparator.comparingInt(MetaObjectAttributeDTO::getSeqNum)).collect(Collectors.toList());
    }
//
//    public static Map<String, List<ConcurrentHashMap<String, Object>>> divideList(Connection connection, List<ConcurrentHashMap<String, Object>> sourcedataObjects, MetaObjectDTO metaObjectDTO) throws DataSyncJobException {
//        log.info("Dividing list into insert and update lists");
//        String id = getPrimaryKeyColumns(metaObjectDTO);
//        List<ConcurrentHashMap<String, Object>> insertList = new ArrayList<>();
//        List<ConcurrentHashMap<String, Object>> updateList = new ArrayList<>();
//        String query = SQLBuilder.selectCreatedAtUpdatedAtAndId(metaObjectDTO, sourcedataObjects,id);
//        Set<MetaObjectAttributeDTO> metaObjectAttributeDTOS = metaObjectDTO.getAttributes().stream().filter(a ->  a.getDbColumn().equals(id))
//                .collect(Collectors.toSet());
//        Map<String, MetaObjectAttributeDTO> attributes = DataProcessor.convertSetToMap(metaObjectAttributeDTOS);
//
//        List<ConcurrentHashMap<String, Object>> targetDataObject = new ArrayList<>();
//        try {
//            ResultSet resultSet = connection.createStatement().executeQuery(query);
//            while (resultSet.next()) {
//                targetDataObject.add(putRetValue(attributes, resultSet));
//            }
//        } catch (SQLException e) {
//            log.error("Error while dividing list", e);
//            throw new DataSyncJobException(ErrorConstant.JOB_READ_ERROR.getMessage(), ErrorConstant.JOB_READ_ERROR.getCode());
//        }
//
//        Map<Object, ConcurrentHashMap<String, Object>> targetDataMap = targetDataObject.stream()
//                .collect(Collectors.toMap(record -> record.get(id).toString(), record -> record));
//
//        sourcedataObjects.stream()
//                .forEach(sourceRecord -> {
//                    if (!targetDataMap.containsKey(sourceRecord.get(id).toString())) {
//                        // Step 2: Add to insertList
//                        insertList.add(sourceRecord);
//                    } else {// Step 3: Add to updateList
//                        updateList.add(sourceRecord);
//                    }
//                });
//
//        Map<String, List<ConcurrentHashMap<String, Object>>> result = new HashMap<>();
//        result.put(INSERT_LIST_KEY, insertList);
//        result.put(UPDATE_LIST_KEY, updateList);
//        log.info("Divided list into insert and update lists successfully");
//        return result;
//    }

    public static ConcurrentHashMap<String, Object> putRetValue(Map<String, MetaObjectAttributeDTO> attributes, ResultSet result) throws DataSyncJobException {
        ConcurrentHashMap<String, Object> retVal = new ConcurrentHashMap<String, Object>();
        for (Map.Entry<String, MetaObjectAttributeDTO> attribute : attributes.entrySet()) {
            DataType type = attribute.getValue().getDataType();
            String columnName = attribute.getValue().getDbColumn();
            try {
                Object value = DataTypeProcessor.processDataType(type, result, columnName);
                retVal.put(columnName, value);
            } catch (Exception e) {
                log.error("Error while processing column: {}", columnName, e);
                throw new DataSyncJobException(ErrorConstant.JOB_WRITE_ERROR.getMessage(), ErrorConstant.JOB_WRITE_ERROR.getCode());
            }
        }
        return retVal;
    }

    public static ConcurrentHashMap<String, Object> createObjectMaps(MetaObjectDTO model, ResultSet result) throws SQLException, DataSyncJobException {
        ConcurrentHashMapWithNullValues retVal = new ConcurrentHashMapWithNullValues();


        Map<String, MetaObjectAttributeDTO> attributes = DataProcessor.convertSetToMap(model.getAttributes());

        for (Map.Entry<String, MetaObjectAttributeDTO> attribute : attributes.entrySet()) {
            DataType type = attribute.getValue().getDataType();
            String columnName = attribute.getValue().getDbColumn();
            try {
                Object value = DataTypeProcessor.processDataType(type, result, columnName);
                if( value == null) {
                    value = "null"; // Handle null values for string types
                }
                retVal.put(columnName, value);
            } catch (Exception e) {
                log.error("Error while processing column: {}", columnName, e);
                throw new DataSyncJobException(ErrorConstant.JOB_WRITE_ERROR.getMessage(), ErrorConstant.JOB_WRITE_ERROR.getCode());
            }
        }
        return retVal.getMap();
    }

    public static AdapterType getAdapter(String adaptorId) {
        if (adaptorId == null || adaptorId.isEmpty()) {
            return null;
        }
        //String aId = adaptorId.toLowerCase();
        if (adaptorId.contains("DATABASEADAPTOR")) {
            return AdapterType.DATABASEADAPTOR;
        } else if (adaptorId.contains("FILEADAPTOR")) {
            return AdapterType.FILEADAPTOR;
        } else if (adaptorId.contains("STREAMADAPTOR")) {
            return AdapterType.STREAMADAPTOR;
        } else {
            return null;
        }
    }

    /**
     * Creates a list of insert SQL queries for each DataRow in the given DataSet.
     * Each query is generated using the metaObject and the row data.
     * @param dataSet the DataSet containing metaObject and dataRows
     * @return List of insert SQL queries
     */
    public static String createInsertQueriesFromDataSet(DataSet dataSet) {
        String query = null;
        if (dataSet == null || dataSet.getMetaObject() == null || dataSet.getDataRows() == null) {
            return query;
        }
        String schemaAppendedTableName = dataSet.getMetaObject().getSchema()+"." + dataSet.getMetaObject().getDbTable();
        for (DataRow dataRow : dataSet.getDataRows()) {
            if (dataRow != null && dataRow.getRow() != null && !dataRow.getRow().isEmpty()) {
                 query = createInsertQuery(schemaAppendedTableName, dataRow.getRow());
            }
        }
        return query;
    }

}
