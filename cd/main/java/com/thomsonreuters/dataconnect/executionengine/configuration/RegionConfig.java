package com.thomsonreuters.dataconnect.executionengine.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RegionConfig {

    @Value("${app.region}")
    private String region;

    public String getRegion() {
        return region;
    }
}
