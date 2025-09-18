package com.aditya.ETLExecutionEngine.util;

import com.aditya.ETLExecutionEngine.model.dto.MetaObjectAttributeDTO;
import com.aditya.ETLExecutionEngine.model.dto.MetaObjectDTO;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.utils.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Slf4j
public class SQLBuilder {

    private SQLBuilder() {
        // Prevent instantiation
    }

     public static boolean isTimestamp(MetaObjectAttributeDTO col) {
        return col.getDataType().toString().equalsIgnoreCase("timestamp") || col.getDataType().toString().equalsIgnoreCase("datetime");
    }

    public static String getUpdatedAt(MetaObjectDTO metaObjectDTO) throws DataSyncJobException {
        List<MetaObjectAttributeDTO> metaObjectAttributeDTOS = metaObjectDTO.getAttributes().stream().filter(SQLBuilder::isTimestamp).filter(m -> m.getDisplayName().contains("update") || m.getDisplayName().equalsIgnoreCase("update_at")).collect(Collectors.toList());
        if(metaObjectAttributeDTOS.isEmpty() || metaObjectAttributeDTOS.get(0) == null || metaObjectAttributeDTOS.get(0).getDbColumnName() == null){
            log.info("No attribute with display name containing 'updated_at' found in MetaObjectDTO: {}", metaObjectDTO.getTableName());
            return null;
        }
        return metaObjectAttributeDTOS.get(0).getDbColumnName();
    }

    public static String getCreatedAt(MetaObjectDTO metaObjectDTO) throws DataSyncJobException {
        List<MetaObjectAttributeDTO> metaObjectAttributeDTOS = metaObjectDTO.getAttributes().stream().filter(SQLBuilder::isTimestamp).filter(m -> m.getDisplayName().contains("create") || m.getDisplayName().equalsIgnoreCase("create_at")).collect(Collectors.toList());
        if (metaObjectAttributeDTOS.isEmpty() || metaObjectAttributeDTOS.get(0) == null || metaObjectAttributeDTOS.get(0).getDbColumnName() == null)
            return null;
        return metaObjectAttributeDTOS.get(0).getDbColumnName();
    }

    public static String getInsertStatement(MetaObjectDTO model) {
        // insert statement logic
        StringBuilder insert = new StringBuilder(DBConstants.INSERT_INTO);
        insert.append(model.getTableName());
        insert.append(" (");
        Set<MetaObjectAttributeDTO> setAttributes = model.getAttributes();
        Map<String, MetaObjectAttributeDTO> attributes = DataProcessor.convertSetToMap(setAttributes);

        attributes.forEach((key, value) -> insert.append(value.getDbColumnName()).append(", "));
        insert.delete(insert.length() - 2, insert.length());
        insert.append(") VALUES (");
        attributes.forEach((key, value) -> insert.append("?, "));
        insert.delete(insert.length() - 2, insert.length());
        insert.append(")");
        return insert.toString();
    }

    public static String getSelectStatementForSpecificIds(MetaObjectDTO metaObj, LocalDateTime completeTime, List<?> objIds, String primaryKeyColumnName) throws DataSyncJobException {
        StringBuilder selectQuery = getSelectFromTable(DBConstants.SELECT_FROM, metaObj);

        String updatedAt= getUpdatedAt(metaObj); //can enable this once timestamp issue fixed

        List<String> orderByCols = getColumnNamesByOrderByOrUpdatedAt(metaObj);
        if (shouldAddWhereClause(completeTime, objIds)) {
            appendObjectIdsCondition(selectQuery, objIds, primaryKeyColumnName);
        }
        appendOrderBy(selectQuery, orderByCols);
        log.debug("Constructed SELECT query: {}", selectQuery.toString());
        return selectQuery.toString();
    }

    public static StringBuilder getSelectFromTable(String selectFrom, MetaObjectDTO metaObj) {
        StringBuilder select = new StringBuilder(selectFrom);
        select.append(metaObj.getTableName());
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
    private static void appendObjectIdsCondition(StringBuilder select, List<?> objIds,String primaryKeyColumnName) {
        if (objIds != null && !objIds.isEmpty()) {
            select.append("Select * from " );
            select.append(primaryKeyColumnName).append(" IN (");
            for (int i = 0; i < objIds.size(); i++) {
                select.append("?");
                if (i < objIds.size() - 1) {
                    select.append(", ");
                }
            }
            select.append(")");
        }


    }
    public static String createUpdateQuery(String tableName, MetaObjectDTO metaObjectDTO, List<String> primaryColumns) {
        if (metaObjectDTO == null || metaObjectDTO.getAttributes().isEmpty()) {
            throw new IllegalArgumentException("MetaModelDTO or its attributes cannot be null or empty");
        }

        StringBuilder query = new StringBuilder();
        query.append(DBConstants.UPDATE).append(tableName).append(DBConstants.SET);
        StringJoiner setClause = new StringJoiner(", ");
        for (MetaObjectAttributeDTO attribute : metaObjectDTO.getAttributes()) {
            String column = attribute.getDbColumnName();
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
                    .map(MetaObjectAttributeDTO::getDbColumnName)
                    .collect(Collectors.toList());
        } else {
            String updatedAt = getUpdatedAt(metaObj);
            if (updatedAt != null) {
                return Collections.singletonList(updatedAt);
            } else {
                return Collections.emptyList();
            }
        }
    }

    public static String getPrimaryKeyColumns(MetaObjectDTO metaObjectDTO) {
        String primaryKeyColumns = null;
        for (MetaObjectAttributeDTO attribute : metaObjectDTO.getAttributes()) {
            if (attribute.isPrimary()) {
                primaryKeyColumns = attribute.getDbColumnName();
            }
        }
        return primaryKeyColumns;
    }

    public static String selectSpecificColumnsFromTable(MetaObjectDTO parentMetaObject, String keyColName,String specificColumn) {

        StringBuilder select = new StringBuilder("SELECT ");
        select.append(specificColumn);
        select.append(" FROM ");
        select.append(parentMetaObject.getTableName());
        select.append(" WHERE ").append(keyColName).append(" = ?");

        return select.toString();
    }


    public static String getUpdateStatement(MetaObjectDTO model) {
        // Update statement logic
        StringBuilder update = new StringBuilder("UPDATE ");
        update.append(model.getTableName());
        update.append(" SET ");
        Set<MetaObjectAttributeDTO> setAttributes = model.getAttributes();
        Map<String, MetaObjectAttributeDTO> attributes = DataProcessor.convertSetToMap(setAttributes);

        attributes.forEach((key, value) -> update.append(value.getDbColumnName()).append(" = ?, "));
        update.delete(update.length() - 2, update.length());
        update.append(" WHERE id = ?");
        return update.toString();
    }

    public static String createInsertQuery(String tableName, ConcurrentMap<String, Object> dataObject) {
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
        query.append("INSERT INTO ").append(tableName).append(" ")
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
        query.append("UPDATE ").append(metaObjectDTO.getTableName()).append(" SET ");

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
                attributes.put(attribute.getDbColumnName(), attribute);
            }
        }
        return attributes;
    }

}