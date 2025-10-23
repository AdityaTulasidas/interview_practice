package com.thomsonreuters.dataconnect.executionengine.utils;

import com.thomsonreuters.dataconnect.executionengine.constant.DBConstants;
import com.thomsonreuters.dataconnect.executionengine.dto.MetaObjectAttributeDTO;
import com.thomsonreuters.dataconnect.executionengine.dto.MetaObjectDTO;
import com.thomsonreuters.dataconnect.executionengine.exceptionhandler.DataSyncJobException;
import com.thomsonreuters.dataconnect.executionengine.model.pojo.PrimaryKeyInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class SQLBuilder {

    private SQLBuilder() {
        // Prevent instantiation
    }
    public static String getSelectStatement(MetaObjectDTO model, LocalDateTime lastExecuted) throws DataSyncJobException {
        StringBuilder select = new StringBuilder(DBConstants.SELECT_FROM);
        select.append(model.getSchema()).append(".").append(model.getDbTable());

        String timestampCol = getTimestampColumnWithFallback(model);
        if (timestampCol != null) {
            select.append(" WHERE ").append(timestampCol).append(" >= ?").append("::timestamp without time zone");
        }
        return select.toString();
    }

     public static boolean isTimestamp(MetaObjectAttributeDTO col) {
        return col.getDataType().toString().equalsIgnoreCase("timestamp") || col.getDataType().toString().equalsIgnoreCase("datetime");
    }

    public static String getUpdatedAt(MetaObjectDTO metaObjectDTO) throws DataSyncJobException {
        if (metaObjectDTO == null || metaObjectDTO.getAttributes() == null) {
            return null;
        }

        // Define normalized keywords to match exactly
        Set<String> updateKeywords = Set.of(
                "updateat", "updatedat", "update_at", "updated_at",
                "update-at", "updated-at", "update at", "updated at"
        );

        // First: check dbColumn
        for (MetaObjectAttributeDTO attr : metaObjectDTO.getAttributes()) {
            if (!isTimestamp(attr)) continue;

            String dbColumn = attr.getDbColumn();
            if (dbColumn == null) continue;

            String normalized = dbColumn.toLowerCase().replaceAll("[\\s_\\-]", "");

            if (updateKeywords.contains(normalized) ||
                    normalized.contains("updateat") ||
                    normalized.contains("updatedat")) {
                return dbColumn;
            }
        }

        // Then: check displayName
        for (MetaObjectAttributeDTO attr : metaObjectDTO.getAttributes()) {
            if (!isTimestamp(attr)) continue;

            String displayName = attr.getDisplayName();
            if (displayName == null) continue;

            String normalized = displayName.toLowerCase().replaceAll("[\\s_\\-]", "");

            if (updateKeywords.contains(normalized) ||
                    normalized.contains("updateat") ||
                    normalized.contains("updatedat")) {
                return attr.getDbColumn();
            }
        }

        return null;
    }


    public static String getCreatedAt(MetaObjectDTO metaObjectDTO) throws DataSyncJobException {
        if (metaObjectDTO == null || metaObjectDTO.getAttributes() == null) {
            return null;
        }

        // Define normalized keywords to match exactly
        Set<String> createKeywords = Set.of(
                "createat", "createdat", "create_at", "created_at",
                "create-at", "created-at", "create at", "created at"
        );

        // First: check dbColumn
        for (MetaObjectAttributeDTO attr : metaObjectDTO.getAttributes()) {
            if (!isTimestamp(attr)) continue;

            String dbColumn = attr.getDbColumn();
            if (dbColumn == null) continue;

            String normalized = dbColumn.toLowerCase().replaceAll("[\\s_\\-]", "");

            if (createKeywords.contains(normalized) ||
                    normalized.contains("createat") ||
                    normalized.contains("createdat")) {
                return dbColumn;
            }
        }

        // Then: check displayName
        for (MetaObjectAttributeDTO attr : metaObjectDTO.getAttributes()) {
            if (!isTimestamp(attr)) continue;

            String displayName = attr.getDisplayName();
            if (displayName == null) continue;

            String normalized = displayName.toLowerCase().replaceAll("[\\s_\\-]", "");

            if (createKeywords.contains(normalized) ||
                    normalized.contains("createat") ||
                    normalized.contains("createdat")) {
                return attr.getDbColumn(); // Return dbColumn even if match is in displayName
            }
        }

        return null;
    }

    public static String getInsertStatement(MetaObjectDTO model) {
        // insert statement logic
        StringBuilder insert = new StringBuilder(DBConstants.INSERT_INTO);
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

    public static String getSelectStatementForSpecificIds(MetaObjectDTO metaObj, LocalDateTime completeTime, List<?> objIds, PrimaryKeyInfo pkInfo) throws DataSyncJobException {
        StringBuilder selectQuery = getSelectFromTable(DBConstants.SELECT_FROM, metaObj);

        List<String> orderByCols = getColumnNamesByOrderByOrUpdatedAt(metaObj);
        if (shouldAddWhereClause(completeTime, objIds)) {
            appendObjectIdsCondition(selectQuery, objIds, pkInfo);
        }
        appendOrderBy(selectQuery, orderByCols);
        log.debug("Constructed SELECT query: {}", selectQuery.toString());
        return selectQuery.toString();
    }

    public static String getSelectStatementForSpecificIds(MetaObjectDTO metaObj, LocalDateTime completeTime, List<?> objIds, PrimaryKeyInfo primaryKeyInfo, LocalDateTime acceptedTimeMinfewSec, String tableType) throws DataSyncJobException {
        StringBuilder selectQuery = getSelectFromTable(DBConstants.SELECT_FROM, metaObj);
        List<String> orderByCols = getColumnNamesByOrderByOrUpdatedAt(metaObj);
        if(tableType.equalsIgnoreCase("lookup")){
            appendOrderBy(selectQuery, orderByCols);
            return selectQuery.toString();
        }
        String timestampCol = getTimestampColumnWithFallback(metaObj);

        boolean isThereWhere = isWhereClauseRequired(timestampCol, objIds);
        if (isThereWhere) {
            appendObjectIdsCondition(selectQuery, objIds, primaryKeyInfo);
//            if (timestampCol != null && acceptedTimeMinfewSec != null) {
//                selectQuery.append(" AND ").append(timestampCol).append(" >= ?");
//            }
//
//        }else {
//            if (timestampCol != null && acceptedTimeMinfewSec != null) {
//                selectQuery.append(" WHERE ").append(timestampCol).append(" >= ?");
//            }
        }
        appendOrderBy(selectQuery, orderByCols);
        log.debug("Constructed SELECT query: {}", selectQuery.toString());
        return selectQuery.toString();
    }

    public static StringBuilder getSelectFromTable(String selectFrom, MetaObjectDTO metaObj) {
        StringBuilder select = new StringBuilder(selectFrom);
        select.append(metaObj.getSchema()).append(".").append(metaObj.getDbTable());
        return select;
    }

    private static void appendOrderBy(StringBuilder select, List<String> orderByCols) {
        if (CollectionUtils.isNotEmpty(orderByCols)) {
            select.append(DBConstants.ORDER_BY)
                    .append(String.join(", ", orderByCols));
        }
    }

    private static boolean shouldAddWhereClause(LocalDateTime completeTime, List<?> objIds) {
        return completeTime != null || (objIds != null && !objIds.isEmpty());
    }

    private static boolean isWhereClauseRequired(String timestampCol, List<?> objIds) {
        return timestampCol != null || (objIds != null && !objIds.isEmpty());
    }

    private static void appendCompleteTimeCondition(StringBuilder select,String updatedAt, LocalDateTime completeTime) {
        if (completeTime != null) {
            select.append(updatedAt).append(" > ?");
        }
    }

    private static void appendAndIfNeeded(StringBuilder select, LocalDateTime completeTime, List<?> objIds) {
        if (completeTime != null && (objIds != null && !objIds.isEmpty())) {
            select.append(DBConstants.AND);
        }
    }

    private static void appendObjectIdsCondition(StringBuilder select, List<?> objIds, PrimaryKeyInfo pkInfo) {
        if (objIds != null && !objIds.isEmpty()) {
            select.append(DBConstants.WHERE );
            if(pkInfo.isComposite()){
                List<Map<String, List<String>>> compositeObjIds = new ArrayList<>();
                if(objIds.get(0) instanceof Map){
                    // Composite PK logic
                    compositeObjIds = (List<Map<String, List<String>>>) objIds;
                }
                List<String> pkNames = extractAllColNamesFromCompositePK(pkInfo.getCompositePKColNameAndType());
                select.append("(").append(String.join(", ", pkNames)).append(") IN (");
                List<String> listOfValues = compositeObjIds.get(0)
                        .entrySet()
                        .iterator()
                        .next()
                        .getValue();
                int pkSize = pkNames.size();
                int tupleCount = listOfValues.size(); // Each value represents a tuple
                for (int i = 0; i < tupleCount; i++) {
                    select.append("(");
                    for (int j = 0; j < pkSize; j++) {
                        select.append("?");
                        if (j < pkSize - 1) {
                            select.append(", ");
                        }
                    }
                    select.append(")");
                    if (i < tupleCount - 1) {
                        select.append(", ");
                    }
                }
                select.append(")");
            } else {
                // Single primary key
                select.append(pkInfo.getPkName()).append(" IN (");
                for (int i = 0; i < objIds.size(); i++) {
                    select.append("?");
                    if (i < objIds.size() - 1) {
                        select.append(", ");
                    }
                }
                select.append(")");
            }
        }
    }


    public static String createUpdateQuery(String schemaAppendedTableName, MetaObjectDTO metaObjectDTO, List<String> primaryColumns) {
        if (metaObjectDTO == null || metaObjectDTO.getAttributes().isEmpty()) {
            throw new IllegalArgumentException("MetaModelDTO or its attributes cannot be null or empty");
        }

        StringBuilder query = new StringBuilder();
        query.append(DBConstants.UPDATE).append(schemaAppendedTableName).append(DBConstants.SET);
        StringJoiner setClause = new StringJoiner(", ");
        for (MetaObjectAttributeDTO attribute : metaObjectDTO.getAttributes()) {
            String column = attribute.getDbColumn();
            setClause.add(column + " = ?");
        }

        query.append(setClause.toString());
        query.append(DBConstants.WHERE);
        StringJoiner whereClause = new StringJoiner(DBConstants.AND);
        for (String primaryColumn : primaryColumns) {
            whereClause.add(primaryColumn + " = ?");
        }

        query.append(whereClause.toString());
        return query.toString();
    }

    public static String buildSelectQueryForDuplicateCheck(MetaObjectDTO metaObjectDTO, String primaryKeyColumn) {
        StringBuilder select = new StringBuilder("SELECT 1 FROM ");
        select.append(metaObjectDTO.getSchema()).append(".").append(metaObjectDTO.getDbTable());
        select.append(" WHERE ").append(primaryKeyColumn).append("= ?");
        return select.toString();
    }

    public static String selectCreatedAtUpdatedAtAndId(MetaObjectDTO metaObjectDTO, List<ConcurrentHashMap<String,Object>> dataObjects,List<String> ids) throws DataSyncJobException {
        StringBuilder select = new StringBuilder("SELECT ");
        //select.append(" ").append(getCreatedAt(metaObjectDTO)).append(", ").append(getUpdatedAt(metaObjectDTO));
        if(ids!= null && !ids.isEmpty()) {
            StringJoiner joiner = new StringJoiner(", ");
            ids.forEach(joiner::add);
            select.append(joiner.toString());
        }
        select.append(" FROM ").append(metaObjectDTO.getSchema()).append(".").append(metaObjectDTO.getDbTable());
        int size = dataObjects.size();
        int idSize = ids.size(); int count=0;
        if(ids!= null && !ids.isEmpty() && size > 0) {
            select.append(" WHERE ");
            for (int i = 0; i < size; i++) {
                for(String id:ids) {
                    select.append(id).append(" IN (");
                    select.append("'").append(dataObjects.get(i).get(id)).append("')");
                    if(count++ < idSize - 1) {
                        select.append(" AND ");
                    }
                    if (i < size - 1) {
                        select.append(", ");
                    }
                }
            }

            //select.append(")");
        }
        return select.toString();
    }

    public static List<String> getPrimaryKeyColumns(MetaObjectDTO metaObjectDTO) {
        String primaryKeyColumns = null;
        List<String> primaryKeyCols = new ArrayList<>();
        for (MetaObjectAttributeDTO attribute : metaObjectDTO.getAttributes()) {
            if (attribute.isPrimary()) {
                primaryKeyColumns = attribute.getDbColumn();
                primaryKeyCols.add(attribute.getDbColumn());
            }
        }
        return primaryKeyCols;
    }

    public static String selectSpecificColumnsFromTable(MetaObjectDTO parentMetaObject, String keyColName,String specificColumn) {

        StringBuilder select = new StringBuilder("SELECT ");
        select.append(specificColumn);
        select.append(" FROM ");
        select.append(parentMetaObject.getSchema()).append(".").append(parentMetaObject.getDbTable());
        select.append(" WHERE ").append(keyColName).append(" = ?");

        return select.toString();
    }

    /**
     * Returns the column names from metaObj attributes in the order specified by order_by.
     * If no attribute has a positive order_by, returns only the updated_at column name.
     */
    public static List<String> getColumnNamesByOrderByOrUpdatedAt(MetaObjectDTO metaObj) throws DataSyncJobException {
        if (metaObj == null || metaObj.getAttributes() == null || metaObj.getAttributes().isEmpty()) {
            return Collections.emptyList();
        }
        List<MetaObjectAttributeDTO> orderedAttributes = metaObj.getAttributes().stream()
                .filter(attr -> attr.getOrderBy() != null && attr.getOrderBy() > 0)
                .sorted(Comparator.comparingInt(MetaObjectAttributeDTO::getOrderBy))
                .collect(Collectors.toList());
        if (!orderedAttributes.isEmpty()) {
            return orderedAttributes.stream()
                    .map(MetaObjectAttributeDTO::getDbColumn)
                    .collect(Collectors.toList());
        } else {
            String updatedAt = getTimestampColumnWithFallback(metaObj);
            if (updatedAt != null) {
                return Collections.singletonList(updatedAt);
            } else {
                return Collections.emptyList();
            }
        }
    }

    public static List<String> extractAllColNamesFromCompositePK(List<Map<String, Object>> compositePKColNameAndType) {
        Set<String> colNameSet = new LinkedHashSet<>();
        for (Map<String, Object> map : compositePKColNameAndType) {
            if (map.containsKey("COL_NAME")) {
                Object colName = map.get("COL_NAME");
                if (colName != null) {
                    colNameSet.add(colName.toString());
                }
            }
        }
        return new ArrayList<>(colNameSet);
    }

    public static String getTimestampColumnWithFallback(MetaObjectDTO metaObj) throws DataSyncJobException {
        String updatedAt = getUpdatedAt(metaObj);
        String createdAt = getCreatedAt(metaObj);

        // If both columns exist, use COALESCE to handle null values in data
        if (updatedAt != null && createdAt != null) {
            return "COALESCE(" + updatedAt + ", " + createdAt + ")";
        }

        // If only updated_at exists, use it
        if (updatedAt != null) {
            return updatedAt;
        }

        // If only created_at exists, use it
        if (createdAt != null) {
            log.warn("No updated_at column found for table {}.{}, using created_at column instead. Update sync will may happen. Only insert sync will work",
                    metaObj.getSchema(), metaObj.getDbTable());
            return createdAt;
        }

        return null;
    }

    public static String selectCreatedAtUpdatedAtAndIds(MetaObjectDTO metaObjectDTO, List<ConcurrentHashMap<String,Object>> dataObjects,List<String> ids) throws DataSyncJobException {

        StringBuilder select = new StringBuilder("SELECT ");
        if (ids != null && !ids.isEmpty()) {
            StringJoiner joiner = new StringJoiner(", ");
            ids.forEach(joiner::add);
            select.append(joiner.toString());
        }
        select.append(" FROM ").append(metaObjectDTO.getSchema()).append(".").append(metaObjectDTO.getDbTable());

        if (ids != null && !ids.isEmpty() && dataObjects != null && !dataObjects.isEmpty()) {
            select.append(" WHERE ");
            select.append("(").append(String.join(", ", ids)).append(") IN (");

            // Construct tuples for the composite keys
            StringJoiner tuples = new StringJoiner(", ");
            for (ConcurrentHashMap<String, Object> dataObject : dataObjects) {
                StringJoiner tuple = new StringJoiner(", ", "(", ")");
                for (String id : ids) {
                    Object value = dataObject.get(id);
                    tuple.add(value instanceof String ? "'" + value + "'" : value.toString());
                }
                tuples.add(tuple.toString());
            }
            select.append(tuples.toString()).append(")");
        }

        return select.toString();
    }


}
