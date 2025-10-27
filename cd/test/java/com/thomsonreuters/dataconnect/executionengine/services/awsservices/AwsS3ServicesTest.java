package com.thomsonreuters.dataconnect.executionengine.services.awsservices;

import com.thomsonreuters.dataconnect.executionengine.exceptionhandler.DataSyncJobException;
import com.thomsonreuters.dataconnect.executionengine.configuration.S3Config;
import com.thomsonreuters.dataconnect.executionengine.model.pojo.TargetRegion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AwsS3ServicesTest {

    @Mock
    private S3Client mockSourceS3Client;

    @Mock
    private S3Client mockDestinationS3Client;

    @Mock
    private S3Config mockS3Config;

    private AwsS3Services awsS3Services;

    @BeforeEach
    void setUp() throws DataSyncJobException {
        MockitoAnnotations.openMocks(this);
        Map<String, S3Client> mockTargetClients = Map.of("EMEA", mockDestinationS3Client);
        when(mockS3Config.getSourceBucketName()).thenReturn("sourceBucket");
        when(mockS3Config.getTargetRegion()).thenReturn(Map.of("EMEA", mock(TargetRegion.class)));
        awsS3Services = new AwsS3Services(mockSourceS3Client, mockTargetClients, mockS3Config);
    }


    @Test
    void shouldThrowExceptionWhenInvalidParametersProvidedToTransferFileBetweenBuckets() {
        assertThrows(DataSyncJobException.class, () -> awsS3Services.transferFileBetweenBuckets(null, null, null, null));
    }

    @Test
    void shouldVerifyS3ConnectionSuccessfullyWhenValidResponseReceived() throws DataSyncJobException {
        ListBucketsResponse mockResponse = mock(ListBucketsResponse.class);
        when(mockResponse.sdkHttpResponse()).thenReturn(mock(SdkHttpResponse.class));
        when(mockResponse.sdkHttpResponse().statusCode()).thenReturn(200);
        when(mockSourceS3Client.listBuckets()).thenReturn(mockResponse);

        awsS3Services.verifyS3Connection();

        verify(mockSourceS3Client).listBuckets();
    }

    @Test
    void shouldThrowExceptionWhenS3ConnectionFails() {
        when(mockSourceS3Client.listBuckets()).thenThrow(S3Exception.builder().message("Connection error").build());

        assertThrows(DataSyncJobException.class, () -> awsS3Services.verifyS3Connection());
    }


}
