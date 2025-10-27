package com.thomsonreuters.dataconnect.dataintegration.model.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
public class DataSyncRequest {
    private List<UUID> objIds;
    private DataCollectionObject dataCollectionObject;

}