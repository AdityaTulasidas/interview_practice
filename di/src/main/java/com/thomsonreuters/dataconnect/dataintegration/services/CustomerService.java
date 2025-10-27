package com.thomsonreuters.dataconnect.dataintegration.services;

import com.thomsonreuters.dataconnect.dataintegration.constant.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.thomsonreuters.dataconnect.dataintegration.exceptionhandler.NoUgeDataException;

import java.util.UUID;

@Service
public class CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String acceptHeader;
    private final String accountIdHeader;
    private final String universalIdHeader;

    public CustomerService(
            RestTemplate restTemplate,
            // Use management.customer.* hierarchy defined in application.yml; retain backward fallback to old keys
            @Value("${management.customer.api.url}") String apiUrl,
            @Value("${management.customer.api.headers.accept:application/json}") String acceptHeader,
            @Value("${management.customer.api.headers.accountId:default-account-id}") String accountIdHeader,
            @Value("${management.customer.api.headers.universalId:default-universal-id}") String universalIdHeader
    ) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.acceptHeader = acceptHeader;
        this.accountIdHeader = accountIdHeader;
        this.universalIdHeader = universalIdHeader;
    }

    private HttpHeaders buildHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("accept", acceptHeader);
        headers.set("X-Lonestar-AccountId", accountIdHeader);
        headers.set("X-Lonestar-UniversalId", universalIdHeader);
        headers.setBearerAuth(accessToken);
        return headers;
    }

    private ResponseEntity<String> callApi(String endpoint, String accessToken) {
        HttpHeaders headers = buildHeaders(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try {
            return restTemplate.exchange(apiUrl.concat(endpoint), HttpMethod.GET, entity, String.class);
        } catch (Exception ex) {
            log.error("Failed to fetch data from {}: {}", endpoint, ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch data");
        }
    }


    public ResponseEntity<String> getCustomerTenantsBySystemName(String accessToken, String systemName) {
        ResponseEntity<String> response = callApi(Constants.CUSTOMER_TENANT_API_PATH + systemName, accessToken);
        if (!response.getStatusCode().is2xxSuccessful() ||
            response.getBody() == null ||
            response.getBody().trim().isEmpty()) {
            throw new NoUgeDataException("no data available in UGE for this system-name/customer_tenant_id");
        }
        return response;
    }
}
