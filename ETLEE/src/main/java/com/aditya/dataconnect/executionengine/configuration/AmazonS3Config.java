
package com.aditya.dataconnect.executionengine.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.aditya.dataconnect.executionengine.constant.Constants.API_CALL_ATTEMPT_TIMEOUT;
import static com.aditya.dataconnect.executionengine.constant.Constants.API_CALL_TIMEOUT;

@Configuration
public class AmazonS3Config {

    private final S3Config s3Config;

    public AmazonS3Config(S3Config s3Config) {
        this.s3Config = s3Config;
    }

    @Bean
    public S3Config getS3ConfigProperties(){
        return s3Config;
    }

    @Bean
    public S3Client sourceS3Client() {
        return S3Client.builder()
                .region(Region.of(s3Config.getRegion()))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Bean
    public Map<String, S3Client> targetS3Clients() {
        Map<String, S3Client> targetClients = new HashMap<>();


        ClientOverrideConfiguration overrideConfig = ClientOverrideConfiguration.builder()
                .apiCallTimeout(Duration.ofMinutes(API_CALL_TIMEOUT))        // Total allowed time for a request (including retries)
                .apiCallAttemptTimeout(Duration.ofMinutes(API_CALL_ATTEMPT_TIMEOUT))  // Timeout for each individual attempt
                .retryPolicy(RetryMode.STANDARD)
                .build();

        s3Config.getTargetRegion().forEach((regionKey, targetRegion) -> {
            S3Client client = S3Client.builder()
                    .region(Region.of(targetRegion.getRegionName()))
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .overrideConfiguration(overrideConfig)
                    .build();
            targetClients.put(targetRegion.getRegionKey(), client);
        });
        return targetClients;
    }
}