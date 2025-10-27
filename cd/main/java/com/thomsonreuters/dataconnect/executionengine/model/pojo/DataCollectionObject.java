package com.thomsonreuters.dataconnect.executionengine.model.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.thomsonreuters.dataconnect.executionengine.dto.MetaObjectDTO;
import com.thomsonreuters.dataconnect.executionengine.exceptionhandler.CustomError;
import com.thomsonreuters.dataconnect.executionengine.model.entity.enums.ErrorConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@JsonIgnoreProperties(ignoreUnknown=true)
public class DataCollectionObject {
    private MetaObjectDTO metaModel;
    private ResultSet dbResult;
    private Map<String, DataCollectionObject> childList;

    public MetaObjectDTO getMetaModel() {
        return metaModel;
    }

    public ResultSet getDbResult() {
        return dbResult;
    }

    public String getMetaObjectId() {
        return this.metaModel.getId().toString();
    }

    public boolean hasObjects() {
        try {
            return this.dbResult.isBeforeFirst();
        } catch (Exception e) {
            log.error("Error in DataCollectionObject.hasObjects()", e);
            throw new CustomError(ErrorConstant.JOB_READ_ERROR.getMessage(), ErrorConstant.JOB_READ_ERROR.getCode());
        }
    }

    public DataObject getObject() {
        try {
            return DataObject.createObject(this.metaModel, this.dbResult);
        } catch (SQLException e) {
            log.error("Error in DataCollectionObject.getObject()", e);
            throw new CustomError(ErrorConstant.JOB_WRITE_ERROR.getMessage(), ErrorConstant.JOB_WRITE_ERROR.getCode());
        }
    }




    public void close() {
        try {
            if (this.dbResult != null){
                this.dbResult.close();
        }
            if (this.childList != null) {
                for (DataCollectionObject objCollect : this.childList.values()) {
                    objCollect.close();
                }
            }
        } catch (Exception e) {
            throw new CustomError(ErrorConstant.JOB_WRITE_ERROR.getMessage(), ErrorConstant.JOB_WRITE_ERROR.getCode());

        }
    }

    public void addDataObject(DataObject object) {
        //yet to implement
    }

    public void setChildList(String string, DataCollectionObject dataCollectionObjectChild) {
        if (this.childList == null) {
            this.childList = new HashMap<>();
        }
        this.childList.put(string, dataCollectionObjectChild);
    }

}
