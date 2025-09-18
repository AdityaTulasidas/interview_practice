package com.aditya.ETLExecutionEngine.data;

import com.aditya.ETLExecutionEngine.model.enums.DataType;
import com.aditya.ETLExecutionEngine.util.DataTypeProcessor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Slf4j
public class DataSet {
    private MetaObjectDTO metaObject;
    private List<DataRow> dataRows;
    private int hierarchyIndex;

    public List<DataRow> getDataRowsFromResultSet(ResultSet resultSet) {
        List<DataRow> dataRows = new ArrayList<>();
        if (resultSet == null) {
            return dataRows;
        }
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                DataRow dataRow = new DataRow();
                ConcurrentHashMap<String, Object> rowMap = new ConcurrentHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    if (columnName == null || columnName.isEmpty()) {
                        columnName = metaData.getColumnName(i);
                    }
                    Object value = resultSet.getObject(i);
                    rowMap.put(columnName, value);
                }
                dataRow.setRow(rowMap);
                dataRows.add(dataRow);
            }
        } catch (Exception e) {
            log.error("Error processing ResultSet: {}", e.getMessage(), e);
        }
        return dataRows;
    }

    public List<DataRow> getDataRowsFromResultSet(ResultSet resultSet, MetaObjectDTO model) throws DataSyncJobException {
        List<DataRow> dataRows = new ArrayList<>();
        if (resultSet == null) {
            return dataRows;
        }
        try {
            while (resultSet.next()) {
                DataRow dataRow = new DataRow();
                ConcurrentHashMap<String, Object> rowMap = createObjectMaps(model, resultSet);
                dataRow.setRow(rowMap);
                dataRows.add(dataRow);
            }
        } catch (SQLException e) {
            log.error("Error processing ResultSet: {}", e.getMessage(), e);
            throw new DataSyncJobException(ErrorConstant.JOB_READ_ERROR.getMessage(), ErrorConstant.JOB_READ_ERROR.getCode());
        }
        return dataRows;
    }

    public static ConcurrentHashMap<String, Object> createObjectMaps(MetaObjectDTO model, ResultSet result) throws SQLException, DataSyncJobException {
        ConcurrentHashMapWithNullValues retVal = new ConcurrentHashMapWithNullValues();


        Map<String, MetaObjectAttributeDTO> attributes = DataProcessor.convertSetToMap(model.getAttributes());

        for (Map.Entry<String, MetaObjectAttributeDTO> attribute : attributes.entrySet()) {
            DataType type = attribute.getValue().getDataType();
            String columnName = attribute.getValue().getDbColumnName();
            retVal.put(columnName, getValue(result, type, columnName));

        }
        return retVal.getMap();
    }

    public static Object getValue(ResultSet result, DataType type, String columnName) throws DataSyncJobException {
        try {
            Object value = DataTypeProcessor.processDataType(type, result, columnName);
            if( value == null) {
                value = "null"; // Handle null values for string types
            }
            return value;
        } catch (Exception e) {
            log.error("Error while processing column: {}", columnName, e);
            throw new DataSyncJobException(ErrorConstant.JOB_WRITE_ERROR.getMessage(), ErrorConstant.JOB_WRITE_ERROR.getCode());
        }
    }
}
 