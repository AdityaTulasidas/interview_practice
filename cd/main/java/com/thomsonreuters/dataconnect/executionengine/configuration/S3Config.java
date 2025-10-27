package com.thomsonreuters.dataconnect.executionengine.configuration;

import com.thomsonreuters.dataconnect.executionengine.model.pojo.TargetRegion;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "aws.s3")
public class S3Config {

    private String region;
    private String sourceBucketName;
    private int pollingInterval;
    private String inputFolder;
    private Map<String, TargetRegion> targetRegion;

    // Getters and Setters

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getSourceBucketName() {
        return sourceBucketName;
    }

    public void setSourceBucketName(String sourceBucketName) {
        this.sourceBucketName = sourceBucketName;
    }

    public int getPollingInterval() {
        return pollingInterval;
    }

    public void setPollingInterval(int pollingInterval) {
        this.pollingInterval = pollingInterval;
    }

    public String getInputFolder() {
        return inputFolder;
    }

    public void setInputFolder(String inputFolder) {
        this.inputFolder = inputFolder;
    }

    public Map<String, TargetRegion> getTargetRegion() {
        return targetRegion;
    }

    public void setTargetRegion(Map<String, TargetRegion> targetRegion) {
        this.targetRegion = targetRegion;
    }

}