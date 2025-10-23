package com.thomsonreuters.dataconnect.executionengine.services.awsservices;

import com.thomsonreuters.dataconnect.executionengine.constant.Constants;
import com.thomsonreuters.dataconnect.executionengine.exceptionhandler.DataSyncJobException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;

@Service
@Slf4j
public class AwsSsmService {

    @Autowired
    private SsmClient ssmClient;

    public String getSSMValue(String key) throws DataSyncJobException {
        try {
            GetParameterRequest request = GetParameterRequest.builder()
                    .name(key)
                    .withDecryption(true)
                    .build();

            GetParameterResponse response = ssmClient.getParameter(request);
            return response.parameter().value();
        } catch (software.amazon.awssdk.services.ssm.model.SsmException e) {
            log.error("Failed to retrieve SSM parameter '{}': {}", key, e.getMessage(), e);
            throw new DataSyncJobException("Failed to retrieve SSM parameter : " + key, Constants.NOT_FOUND);
        }
    }

}
