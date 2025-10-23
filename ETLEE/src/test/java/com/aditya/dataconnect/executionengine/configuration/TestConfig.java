package com.aditya.dataconnect.executionengine.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@TestConfiguration
public class TestConfig {

    // Local environment configuration
    @Bean(name = "dataIntegrationExecuteApiUrl")
    @Profile("local")
    public String dataIntegrationExecuteApiUrlLocal() {
        return "http://localhost:8082/data-connect/data-integration/jobs/execute/";
    }

    @Bean(name = "jobStatusApiUrl")
    @Profile("local")
    public String jobStatusApiUrlLocal() {
        return "http://localhost:8080/data-connect/execution-engine/data-connect/jobs/execution-logs/";
    }

    // Dev environment configuration
    @Bean(name = "dataIntegrationExecuteApiUrl")
    @Profile("dev")
    public String dataIntegrationExecuteApiUrlDev() {
        return "http://internal-a209259-dev-data26ac2fd16bc924b4-933279134.us-east-1.elb.amazonaws.com/data-connect/data-integration/jobs/execute/";
    }

    @Bean(name = "jobStatusApiUrl")
    @Profile("dev")
    public String jobStatusApiUrlDev() {
        return " http://internal-a209259-dev-data26ac2fd16bc924b4-933279134.us-east-1.elb.amazonaws.com/data-connect/execution-engine/data-connect/jobs/execution-logs/";
    }
}
