package com.thomsonreuters.dataconnect.executionengine.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public String hostRegion(@Value("${app.region}") String hostRegion) {
        return hostRegion;
    }

    @Bean
    public String cloudProvider(@Value("${app.provider}") String cloudProvider) {
        return cloudProvider;
    }
}
