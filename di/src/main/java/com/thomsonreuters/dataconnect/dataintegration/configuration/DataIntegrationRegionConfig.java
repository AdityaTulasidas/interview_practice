package com.thomsonreuters.dataconnect.dataintegration.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "dataconnect.data-integration")
public class DataIntegrationRegionConfig {

    private Map<String, String> regionUrls;
}
