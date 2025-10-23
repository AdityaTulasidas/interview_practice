package com.thomsonreuters.dataconnect.executionengine.datasyncactivity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataSyncActivityFactory {

    @Autowired
    private PublishTransitHubActivity publishTransitHubActivity;

    private final static  String PUBLISH_TRANSIT_HUB_EVENT= "dsync.transit-hub.publish-event";
    public  DataSyncActivity getDataSyncActivity(String name) {
        DataSyncActivity  dataSyncActivity =null;

        if(name.equalsIgnoreCase(DataSyncActivityFactory.PUBLISH_TRANSIT_HUB_EVENT)){
            dataSyncActivity = publishTransitHubActivity;

        }
        return dataSyncActivity;
    }

}
