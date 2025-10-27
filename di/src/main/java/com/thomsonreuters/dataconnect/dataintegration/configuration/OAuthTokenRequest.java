package com.thomsonreuters.dataconnect.dataintegration.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "management.oauth.token-request")
public class OAuthTokenRequest {
    private String client_id;
    private String grant_type;
    private String client_secret;
    private String audience;
}
