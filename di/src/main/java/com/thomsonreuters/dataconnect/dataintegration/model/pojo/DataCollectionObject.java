package com.thomsonreuters.dataconnect.dataintegration.model.pojo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.thomsonreuters.dataconnect.dataintegration.dto.MetaObjectDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class DataCollectionObject {
    @JsonProperty("meta_model")
    private MetaObjectDTO metaModel;
    @JsonProperty("child_list")
    private Map<String, DataCollectionObject> childList;

    @JsonCreator
    public DataCollectionObject(MetaObjectDTO metaModel, Map<String, DataCollectionObject> childList) {
        this.metaModel = metaModel;
        this.childList = childList;
    }

    @JsonProperty("meta_object_id")
    public String getMetaObjectId() {
        return this.metaModel.getId().toString();
    }


    public void setChildList(String string, DataCollectionObject dataCollectionObjectChild) {
        if (this.childList == null) {
            this.childList = new HashMap<>();
        }
        this.childList.put(string, dataCollectionObjectChild);
    }
}