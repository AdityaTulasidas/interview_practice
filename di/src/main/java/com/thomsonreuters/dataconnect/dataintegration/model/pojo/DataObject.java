package com.thomsonreuters.dataconnect.dataintegration.model.pojo;

import com.thomsonreuters.dataconnect.dataintegration.dto.MetaObjectAttributeDTO;
import com.thomsonreuters.dataconnect.dataintegration.dto.MetaObjectDTO;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.DataType;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class DataObject {

    private Map<String, Object> values;
    private Map<String, DataCollectionObject> childObjects;
    private MetaObjectDTO model;

    DataObject(int fieldCnt, int childCnt) {
        this.values = new HashMap<>(fieldCnt);
        this.childObjects = new HashMap<>(childCnt);
    }

    public DataObject() {
        this(10, 5);
    }

    public DataObject(MetaObjectDTO model) {
        this();
        this.model = model;
    }

    public static DataObject createObject(MetaObjectDTO model, ResultSet result) throws SQLException {
        DataObject retVal = new DataObject(model);
        Map<String,MetaObjectAttributeDTO> attributes = model.getAttributes();
        for (Map.Entry<String, MetaObjectAttributeDTO> attribute : attributes.entrySet()) {
            DataType type = attribute.getValue().getDataType();
            MetaObjectAttributeDTO metaObjectAttributeDTO= attribute.getValue();
            if(metaObjectAttributeDTO==null){
                log.error("Error while creating meta object from result set");
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
                    case DATETIME:
                        LocalDateTime dateTimeVal = result.getObject(metaObjectAttributeDTO.getDbColumn(), LocalDateTime.class);
                        retVal.setDateTime(metaObjectAttributeDTO.getDbColumn(), dateTimeVal);
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported data type: " + type);
                }
            } catch (Exception e) {
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

    public int getInteger(String name) {
        return (Integer) this.values.get(name);
    }

    public DataCollectionObject getChildObjects(String modelId) {
        return this.childObjects.get(modelId);
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
}