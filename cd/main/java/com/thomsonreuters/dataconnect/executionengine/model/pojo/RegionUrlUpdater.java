package com.thomsonreuters.dataconnect.executionengine.model.pojo;

import com.thomsonreuters.dataconnect.executionengine.configuration.DataIntegrationRegionConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;


@Component
public class RegionUrlUpdater {

    private final DataIntegrationRegionConfig dataIntegrationRegionConfig;

    public RegionUrlUpdater(DataIntegrationRegionConfig dataIntegrationRegionConfig) {
        this.dataIntegrationRegionConfig = dataIntegrationRegionConfig;
    }

    @PostConstruct
    public void updateRegionUrls() {
        String amerBaseUrl = System.getenv("AMER_BASE_URL");
        if (amerBaseUrl != null && amerBaseUrl.toLowerCase().contains("qa")) {
            dataIntegrationRegionConfig.getRegionUrls().put("EMEA", "http://a209259-data-connect-qa-euw2.3300.aws-int.thomsonreuters.com/data-connect/data-integration/job-execution-log");
        }

    }
}