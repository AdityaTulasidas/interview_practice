package com.thomsonreuters.dataconnect.dataintegration.model.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RequestBodyDataUnitObject {
    @JsonProperty("data_collection_object")
    private DataCollectionObject dataCollectionObject;

}