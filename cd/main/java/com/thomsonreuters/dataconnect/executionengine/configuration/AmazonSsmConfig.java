package com.thomsonreuters.dataconnect.executionengine.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;

@Configuration
public class AmazonSsmConfig {

    private final AwsConfig ssmConfigProperties;

    public AmazonSsmConfig(AwsConfig ssmConfigProperties) {
        this.ssmConfigProperties = ssmConfigProperties;
    }

    @Bean
    public SsmClient ssmClient() {
        return SsmClient.builder()
                .region(Region.of(ssmConfigProperties.getRegion()))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}
