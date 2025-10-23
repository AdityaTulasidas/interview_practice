package com.aditya.dataconnect.executionengine.model.pojo;

import com.aditya.dataconnect.executionengine.dto.MetaObjectAttributeDTO;
import com.aditya.dataconnect.executionengine.dto.MetaObjectDTO;
import com.aditya.dataconnect.executionengine.exceptionhandler.CustomError;
import com.aditya.dataconnect.executionengine.model.entity.enums.DataType;
import com.aditya.dataconnect.executionengine.model.entity.enums.ErrorConstant;
import com.aditya.dataconnect.executionengine.utils.DataProcessor;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class DataObject {

    private Map<String, Object> values; // store attributes of a DataObject represented by MetaObject
    private Map<String, DataCollectionObject> childObjects; // store child objects
    public MetaObjectDTO metaModel;

    DataObject(int fieldCnt, int childCnt) {
        this.values = new HashMap<>(fieldCnt);
        this.childObjects = new HashMap<>(childCnt);
    }

    public DataObject() {
        this(10, 5);
    }

    public DataObject(MetaObjectDTO model) {
        this();
        this.metaModel = model;
    }

    public static DataObject createObject(MetaObjectDTO model, ResultSet result) throws SQLException {
        DataObject retVal = new DataObject(model);
        Set<MetaObjectAttributeDTO> setAttributes = model.getAttributes();
        Map<String, MetaObjectAttributeDTO> attributes = DataProcessor.convertSetToMap(setAttributes);

        for (Map.Entry<String, MetaObjectAttributeDTO> attribute : attributes.entrySet()) {
            DataType type = attribute.getValue().getDataType();
            MetaObjectAttributeDTO metaObjectAttributeDTO= attribute.getValue();
            if(metaObjectAttributeDTO==null){
                log.error("Error while creating meta object from result set");
                throw new CustomError(ErrorConstant.JOB_READ_ERROR.getMessage(), ErrorConstant.JOB_READ_ERROR.getCode());
            }
            try {
                switch (type) {
                    case STRING, UUID:
                        String strVal = result.getString(metaObjectAttributeDTO.getDbColumn());
                        retVal.setValue(metaObjectAttributeDTO.getDbColumn(), strVal);
                        break;
                    case INTEGER:
                        int intVal = result.getInt(metaObjectAttributeDTO.getDbColumn());
                        retVal.setInteger(metaObjectAttributeDTO.getDbColumn(), intVal);
                        break;
                    case LONG:
                        double dblVal = result.getDouble(metaObjectAttributeDTO.getDbColumn());
                        retVal.setValue(metaObjectAttributeDTO.getDbColumn(), dblVal);
                        break;
                    case DATETIME:
                        LocalDateTime dateTimeVal = result.getObject(metaObjectAttributeDTO.getDbColumn(), LocalDateTime.class);
                        retVal.setDateTime(metaObjectAttributeDTO.getDbColumn(), dateTimeVal);
                        break;
                }
            } catch (Exception e) {
                Map<String, Object> response = new LinkedHashMap<>();
                CustomError error = new CustomError(ErrorConstant.JOB_WRITE_ERROR.getMessage(), ErrorConstant.JOB_WRITE_ERROR.getCode());
                response.put("error", error);
                log.error("Error while creating meta object from result set", e);
            }
        }
        return retVal;
    }

    public <T> T getValue(String name) {
        return (T) this.values.get(name);
    }

    public String getString(String name) {
        return (String) this.values.get(name);
    }

    public LocalDateTime getDateTime(String name) {
        return (LocalDateTime) this.values.get(name);
    }
    public LocalDate getDate(String name) {
        return LocalDate.parse(this.values.get(name).toString());
    }

    public int getInteger(String name) {
        return (Integer) this.values.get(name);
    }

    public DataCollectionObject getChildObjects(String modelId) {
        return this.childObjects.get(modelId);
    }

    public void addChildObject(String modelId, DataObject object) {
        DataCollectionObject val = this.childObjects.get(modelId);
        if (val == null) {
            val = new DataCollectionObject();
            this.childObjects.put(modelId, val);
        }
        val.addDataObject(object);
    }

    public <T> void setValue(String name, T val) {

        this.values.put(name, val);
    }

    public void setDateTime(String name, LocalDateTime val) {

        this.values.put(name, val);
    }

    public void setInteger(String name, int val) {

        this.values.put(name, val);
    }

    public Short getShort(String name) {
        Object value = this.values.get(name);
        if (value == null) return null;
        if (value instanceof Number) {
            return ((Number) value).shortValue();
        }
        return Short.parseShort(value.toString());
    }

    public Long getLong(String name) {
        Object value = this.values.get(name);
        if (value == null) return null;
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.parseLong(value.toString());
    }

    public java.math.BigDecimal getBigDecimal(String name) {
        Object value = this.values.get(name);
        if (value == null) return null;
        if (value instanceof java.math.BigDecimal) {
            return (java.math.BigDecimal) value;
        }
        return new java.math.BigDecimal(value.toString());
    }
}