package com.thomsonreuters.dataconnect.dataintegration.model.pojo;

import lombok.Data;

@Data
public class DataUnitObject implements DataUnitContent {
    private DataCollectionObject datacollectionobject;

    public DataUnitObject(DataCollectionObject datacollectionobject) {
        this.datacollectionobject = datacollectionobject;
    }
    public DataCollectionObject getDatacollectionobject() {
        return datacollectionobject;
    }

    public void setDatacollectionobject(DataCollectionObject datacollectionobject) {
        this.datacollectionobject = datacollectionobject;
    }
}
