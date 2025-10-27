package com.thomsonreuters.dataconnect.dataintegration.controllers;

import com.thomsonreuters.dataconnect.dataintegration.model.pojo.OAuthTokenResponse;
import com.thomsonreuters.dataconnect.dataintegration.services.CustomerService;
import com.thomsonreuters.dataconnect.dataintegration.services.OAuthService;
import com.thomsonreuters.dataconnect.dataintegration.exceptionhandler.OAuthTokenRetrievalException;
import com.thomsonreuters.dataconnect.dataintegration.exceptionhandler.NoUgeDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomsonreuters.dataconnect.dataintegration.dto.CustomerTenantDTO;
import com.thomsonreuters.dataconnect.dataintegration.dto.CustomerTenantsResponseDTO;
import com.thomsonreuters.dataconnect.dataintegration.dto.MetaDTO;

import java.util.Collections;
import java.util.UUID;

@RestController
public class CustomerController {

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private OAuthService oAuthService;

    @Autowired
    private CustomerService customerService;

    @GetMapping("/customer-tenants/{system-name}")
    public ResponseEntity<Object> getCustomerTenantsBySystemName(@PathVariable("system-name") String systemName) throws NoUgeDataException {
        try {
            OAuthTokenResponse tokenResponse = oAuthService.getOAuthToken();
            ResponseEntity<String> customerResponse = customerService.getCustomerTenantsBySystemName(tokenResponse.getAccessToken(), systemName);

            ObjectMapper objectMapper = new ObjectMapper();
            CustomerTenantDTO tenant = objectMapper.readValue(customerResponse.getBody(), CustomerTenantDTO.class);

            // Example meta, you may want to set count, limit, offset dynamically
            MetaDTO meta = new MetaDTO(1, 1, 0);

            CustomerTenantsResponseDTO responseDTO = new CustomerTenantsResponseDTO(
                    Collections.singletonList(tenant),
                    meta
            );
            return ResponseEntity.ok(responseDTO);
        } catch (NoUgeDataException e) {
            throw e; // Let GlobalExceptionHandler handle this
        } catch (Exception e) {
            log.error("Error fetching customer tenant data by system name: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch customer tenant data");
        }
    }

    @PostMapping("/oauth/token/test")
    public ResponseEntity<Object> getOAuthToken() {
        try {
            OAuthTokenResponse tokenResponse = oAuthService.getOAuthToken();
            return ResponseEntity.ok(tokenResponse);
        } catch (Exception e) {
            log.error("Error fetching OAuth token or customer data: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch OAuth token or customer data");
        }
    }

}
