package com.thomsonreuters.dataconnect.dataintegration.services;

import com.thomsonreuters.dataconnect.dataintegration.configuration.OAuthTokenRequest;
import com.thomsonreuters.dataconnect.dataintegration.model.pojo.OAuthTokenResponse;
import com.thomsonreuters.dataconnect.dataintegration.exceptionhandler.OAuthTokenRetrievalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class OAuthService {

    private static final Logger log = LoggerFactory.getLogger(OAuthService.class);

    @Value("${management.oauth.url}")
    private String OAUTH_URL;

    @Autowired
    private OAuthTokenRequest request;

    @Autowired
    private RestTemplate restTemplate;

    public OAuthTokenResponse getOAuthToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OAuthTokenRequest> entity = new HttpEntity<>(request, headers);
        try {
            ResponseEntity<OAuthTokenResponse> response = restTemplate.exchange(
                    OAUTH_URL,
                    HttpMethod.POST,
                    entity,
                    OAuthTokenResponse.class
            );
            return response.getBody();
        } catch (HttpStatusCodeException ex) {
            String bodySnippet = safeSnippet(ex.getResponseBodyAsString());
            log.error("OAuth token request failed status={} bodySnippet={}", ex.getStatusCode(), bodySnippet, ex);
            throw new OAuthTokenRetrievalException(
                    "OAuth token HTTP error: " + ex.getStatusCode(),
                    ex.getStatusCode().value(),
                    bodySnippet,
                    ex
            );
        } catch (RestClientException ex) {
            log.error("OAuth token transport error", ex);
            throw new OAuthTokenRetrievalException("OAuth token transport error", ex);
        } catch (Exception ex) {
            log.error("Unexpected error during OAuth token request", ex);
            throw new OAuthTokenRetrievalException("Unexpected error during OAuth token request", ex);
        }
    }

    private String safeSnippet(String body) {
        if (body == null) {
            return "";
        }
        return body.length() > 500 ? body.substring(0, 500) + "...(truncated)" : body;
    }
}
