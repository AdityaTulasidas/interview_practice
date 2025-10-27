package com.thomsonreuters.dataconnect.dataintegration.model.pojo;

import com.thomsonreuters.dataconnect.dataintegration.configuration.DataIntegrationRegionConfig;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class RegionUrlUpdater {

    private final DataIntegrationRegionConfig dataIntegrationRegionConfig;

    public RegionUrlUpdater(DataIntegrationRegionConfig dataIntegrationRegionConfig) {
        this.dataIntegrationRegionConfig = dataIntegrationRegionConfig;
    }

    @PostConstruct
    public void updateRegionUrls() {
        String amerBaseUrl = System.getenv("AMER_BASE_URL");
        log.info(amerBaseUrl);
        if (amerBaseUrl != null && amerBaseUrl.toLowerCase().contains("qa")) {
            dataIntegrationRegionConfig.getRegionUrls().put("EMEA", "http://a209259-data-connect-qa-euw2.3300.aws-int.thomsonreuters.com/data-connect/data-integration/job-execution-log");
        }

    }
}