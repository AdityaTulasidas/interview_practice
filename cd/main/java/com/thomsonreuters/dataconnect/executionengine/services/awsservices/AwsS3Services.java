package com.thomsonreuters.dataconnect.executionengine.services.awsservices;

import com.thomsonreuters.dataconnect.executionengine.configuration.RegionConfig;
import com.thomsonreuters.dataconnect.executionengine.exceptionhandler.DataSyncJobException;
import com.thomsonreuters.dataconnect.executionengine.configuration.S3Config;
import com.thomsonreuters.dataconnect.executionengine.model.pojo.TargetRegion;
import com.thomsonreuters.dataconnect.executionengine.repository.ActivityLogRepository;
import com.thomsonreuters.dataconnect.executionengine.repository.JobExecutionLogRepository;
//import com.thomsonreuters.dataconnect.executionengine.services.fileadaptor.FileNameQueuePublishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Service
@Slf4j
public class AwsS3Services {

    private final S3Client sourceS3Client;

    private S3Client destinationS3Client;
    private S3Config s3Config;

    private String sourcebucketName;

    private Map<String, S3Client> targetClients;

    private String destinationBucketName;

    private static final String ARCHIVE_PREFIX = "archive/";

    @Autowired
    private RegionConfig regionConfig;
    @Autowired
    private JobExecutionLogRepository jobExecutionLogRepository;
    @Autowired
    private ActivityLogRepository activityLogRepository;

    public AwsS3Services(@Qualifier("sourceS3Client") S3Client sourceS3Client, @Qualifier("targetS3Clients") Map<String, S3Client> targetClients, @Qualifier("getS3ConfigProperties") S3Config s3Config
                                                                    ) throws DataSyncJobException {
        this.sourceS3Client = sourceS3Client;
        this.targetClients = targetClients;
        this.destinationS3Client = null;
        this.sourcebucketName = s3Config.getSourceBucketName();
        this.s3Config = s3Config;
    }


    private void moveToArchive(String sourceKey, String fileName) {
        String archiveKey = ARCHIVE_PREFIX + fileName;
        try {
            // Copy to archive
            CopyObjectRequest copyReq = CopyObjectRequest.builder()
                    .sourceBucket(sourcebucketName)
                    .sourceKey(sourceKey)
                    .destinationBucket(sourcebucketName)
                    .destinationKey(archiveKey)
                    .build();
            sourceS3Client.copyObject(copyReq);

            // Delete original
            DeleteObjectRequest delReq = DeleteObjectRequest.builder()
                    .bucket(sourcebucketName)
                    .key(sourceKey)
                    .build();
            sourceS3Client.deleteObject(delReq);

            log.info("Moved file '{}' to archive '{}'", sourceKey, archiveKey);
        } catch (Exception e) {
            log.error("Failed to move file '{}' to archive: {}", sourceKey, e.getMessage(), e);
        }
    }

    /**
     * Transfers a file from one S3 bucket to another.
     *
     * @param sourceBucket      Source S3 bucket name
     * @param sourceKey         Source S3 object key (file path)
     * @param destinationBucket Destination S3 bucket name
     * @param destinationKey    Destination S3 object key (file path)
     * @throws DataSyncJobException if transfer fails
     */
    public void transferFileBetweenBuckets(String sourceBucket, String sourceKey, String destinationBucket, String destinationKey) throws DataSyncJobException {
        try {
            if (sourceBucket == null || sourceBucket.isEmpty() ||
                    sourceKey == null || sourceKey.isEmpty() ||
                    destinationBucket == null || destinationBucket.isEmpty() ||
                    destinationKey == null || destinationKey.isEmpty()) {
                throw new DataSyncJobException("Source and destination bucket/key cannot be null or empty", "INVALID_REQUEST");
            }
            // Check if the source file exists
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(sourceBucket)
                    .key(sourceKey)
                    .build();
            sourceS3Client.headObject(headObjectRequest);

            // Get the object from the source bucket
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(sourceBucket)
                    .key(sourceKey)
                    .build();

            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                sourceS3Client.getObject(getObjectRequest, software.amazon.awssdk.core.sync.ResponseTransformer.toOutputStream(outputStream));
                byte[] fileBytes = outputStream.toByteArray();

                // Put the object into the destination bucket
                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(destinationBucket)
                        .key(destinationKey)
                        .build();

                destinationS3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromBytes(fileBytes));
                log.info("File '{}' transferred from bucket '{}' to bucket '{}' as '{}'.", sourceKey, sourceBucket, destinationBucket, destinationKey);
            }
        } catch (NoSuchKeyException e) {
            log.error("Source file '{}' not found in bucket '{}'. Error: {}", sourceKey, sourceBucket, e.getMessage(), e);
            throw new DataSyncJobException("Source file " + sourceKey + " not found in AWS S3 bucket: " + sourceBucket, "NOT_FOUND");
        } catch (DataSyncJobException e) {
            log.error("Failed to transfer file from '{}' to '{}'. Error: {}", sourceBucket, destinationBucket, e.getMessage(), e);
            throw e;
        } catch (S3Exception | IOException e) {
            log.error("Failed to transfer file from '{}' to '{}'. Error: {}", sourceBucket, destinationBucket, e.getMessage(), e);
            throw new DataSyncJobException("Failed to transfer file between buckets. Please check the bucket names and keys.", "INTERNAL_SERVER_ERROR");
        }
    }

    /**
     * Transfers multiple files from one S3 bucket to another.
     *
     * @param sourceBucket      Source S3 bucket name
     * @param sourceKeys        List of source S3 object keys (file paths)
     * @param destinationBucket Destination S3 bucket name
     * @param destinationKeys   List of destination S3 object keys (file paths)
     * @throws DataSyncJobException if any transfer fails
     */
    public void transferMultipleFilesBetweenBuckets(
            String sourceBucket,
            java.util.List<String> sourceKeys,
            String destinationBucket,
            java.util.List<String> destinationKeys
    ) throws DataSyncJobException {
        if (sourceBucket == null || sourceBucket.isEmpty() ||
                destinationBucket == null || destinationBucket.isEmpty() ||
                sourceKeys == null || destinationKeys == null ||
                sourceKeys.isEmpty() || destinationKeys.isEmpty() ||
                sourceKeys.size() != destinationKeys.size()) {
            throw new DataSyncJobException("Invalid input: buckets and key lists must be non-null, non-empty, and lists must be of equal size", "INVALID_REQUEST");
        }
        log.info("Starting transfer of {} files from bucket '{}' to bucket '{}'.", sourceKeys.size(), sourceBucket, destinationBucket);
        for (int i = 0; i < sourceKeys.size(); i++) {
            String srcKey = sourceKeys.get(i);
            String destKey = destinationKeys.get(i);
            try {
                transferFileBetweenBuckets(sourceBucket, srcKey, destinationBucket, destKey);
                log.info("Transferred file '{}' to '{}'.", srcKey, destKey);
            } catch (DataSyncJobException e) {
                log.error("Failed to transfer file '{}' to '{}'. Error: {}", srcKey, destKey, e.getMessage(), e);
                throw e;
            }
        }
        log.info("All files transferred successfully from bucket '{}' to bucket '{}'.", sourceBucket, destinationBucket);
    }

    public void verifyS3Connection() throws DataSyncJobException {
        try {
            ListBucketsResponse bucketsResponse = sourceS3Client.listBuckets();
            int statusCode = bucketsResponse.sdkHttpResponse().statusCode();
            if (statusCode == 200) {
                log.info("Successfully connected to S3. Buckets: {}", bucketsResponse.buckets());
            } else {
                log.error("Failed to connect to S3. HTTP Status Code: {}", statusCode);
                throw new DataSyncJobException("Error connecting to S3. HTTP Status Code: " + statusCode, "INTERNAL_SERVER_ERROR");
            }
        } catch (S3Exception e) {
            log.error("Failed to connect to S3. Error: {}", e.getMessage(), e);
            throw new DataSyncJobException("Error connecting to S3", "INTERNAL_SERVER_ERROR");
        }
    }

    public byte[] retrieveFileContentsAsBytes(String fileName) throws DataSyncJobException {
        try {
            if (fileName == null || "null".equalsIgnoreCase(fileName) || fileName.isEmpty()) {
                throw new DataSyncJobException("File name cannot be null or empty", "INVALID_REQUEST");
            }
            // Check if the file exists in S3
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(sourcebucketName)
                    .key(fileName)
                    .build();


            // Create a GetObjectRequest
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(sourcebucketName)
                    .key(fileName)
                    .build();

            // Retrieve the file from S3 and read its contents into a byte array
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                sourceS3Client.getObject(getObjectRequest, software.amazon.awssdk.core.sync.ResponseTransformer.toOutputStream(outputStream));
                log.info("File '{}' retrieved successfully from bucket '{}'.", fileName, sourcebucketName);
                return outputStream.toByteArray();
            }
        } catch (NoSuchKeyException e) {
            log.error("File '{}' not found in bucket '{}'. Error: {}", fileName, sourcebucketName, e.getMessage(), e);
            throw new DataSyncJobException("File " + fileName + " not found in AWS S3 bucket: " + sourcebucketName, "NOT_FOUND");
        } catch (DataSyncJobException e) {
            log.error("Failed to retrieve file '{}' from bucket '{}'. Error: {}", fileName, destinationBucketName, e.getMessage(), e);
            throw new DataSyncJobException(e.getMessage(), "INTERNAL_SERVER_ERROR");
        } catch (S3Exception | IOException e) {
            log.error("Failed to retrieve file '{}' from bucket '{}'. Error: {}", fileName, destinationBucketName, e.getMessage(), e);
            throw new DataSyncJobException("Failed to retrieve file. Please check the file name", "INTERNAL_SERVER_ERROR");
        }

    }

    public void writeToS3(String filePath, String targetRegions, InputStream inputStream) throws DataSyncJobException {
        try {
            String[] inpTargets = targetRegions.split(",");
            // Buffer the input stream into a byte array ONCE
            byte[] fileBytes = inputStream.readAllBytes();
            for (String target : inpTargets) {
                target = target.trim();
                S3Client targetS3Client = targetClients.get(target);

                if (targetS3Client == null) {
                    log.error("No S3 client found for target region: {}", target);
                    throw new DataSyncJobException("No S3 client found for target region: " + target + "filePath: " + filePath , "INVALID_TARGET");
                }
                // Create the "directory" in S3
                String targetRegionBucketName = getDestinationBucketName(target);
                // Upload the file to the directory
                PutObjectRequest fileRequest = PutObjectRequest.builder()
                        .bucket(targetRegionBucketName)
                        .key(filePath)
                        .build();

                PutObjectResponse response = targetS3Client.putObject(fileRequest, RequestBody.fromBytes(fileBytes));
                if (response.sdkHttpResponse().isSuccessful()) {
                    log.info("File '{}' uploaded successfully to bucket '{}' with key '{}'.", filePath, targetRegionBucketName, filePath);
                } else {
                    log.error("Failed to upload file '{}' to bucket '{}'. Response: {}", filePath, targetRegionBucketName, response.sdkHttpResponse().statusCode());
                }
            }
        } catch (Exception e) {
            log.error("Failed to upload file '{}' to bucket '{}'. Error: {}", filePath, sourcebucketName, e.getMessage(), e);
            throw new DataSyncJobException("Failed to upload file", "INTERNAL_SERVER_ERROR");
        }
    }

    private String getDestinationBucketName(String target) throws DataSyncJobException {
        return s3Config.getTargetRegion().values().stream()
                .filter(targetRegion -> targetRegion.getRegionKey().equals(target))
                .map(TargetRegion::getBucketName)
                .findFirst()
                .orElseThrow(() -> new DataSyncJobException("No matching regionKey found for target: " + target, "INVALID_TARGET"));
    }

}
