package com.thomsonreuters.dataconnect.dataintegration.model.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OAuthTokenResponse {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("expires_in")
    private int expiresIn;

    // Explicit getter added to avoid reliance on Lombok annotation processing issues
    public String getAccessToken() {
        return accessToken;
    }
    // Explicit setter for test compatibility
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
