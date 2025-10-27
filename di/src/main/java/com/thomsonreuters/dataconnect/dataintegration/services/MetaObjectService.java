package com.thomsonreuters.dataconnect.dataintegration.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class MetaObjectService {

    @Autowired
    private RestTemplate restTemplate;


    @Value("${META_REGISTRY_API_URL}")
    private String metaRegistryUrl;

    public void incrementUsageCount(UUID metaObjectId, int incrementValue, boolean relative) {
        // Normalize metaRegistryUrl to avoid trailing slash
        String baseUrl = metaRegistryUrl != null && metaRegistryUrl.endsWith("/")
                ? metaRegistryUrl.substring(0, metaRegistryUrl.length() - 1)
                : metaRegistryUrl;
        String url = baseUrl + "/meta-objects/usage/" + metaObjectId.toString();
        Map<String, Object> payload = new HashMap<>();
        payload.put("value", incrementValue); // increment by 1
        payload.put("relative", relative);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.PATCH,
                request,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        if (response.getStatusCode() != HttpStatus.OK) {
            log.error("Failed to increment usage count for metaObjectId {}", metaObjectId);
        } else {
            log.info("Successfully incremented usage count for metaObjectId {}", metaObjectId);
        }

    }
}
