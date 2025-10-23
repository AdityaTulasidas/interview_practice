package com.thomsonreuters.dataconnect.executionengine.services;

import com.thomsonreuters.dataconnect.executionengine.model.entity.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestTemplate;

@Service
public class DataSourceApiService {

    private final RestTemplate restTemplate;

    @Autowired
    public DataSourceApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public DataSource getFilteredDataSource(String baseUrl, String regionalTenantId, String domain, String onesourceRegion, String domainObjectSysName) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/datasources/filter")
                .queryParam("regionalTenantId", regionalTenantId)
                .queryParam("domain", domain)
                .queryParam("onesourceRegion", onesourceRegion)
                .queryParam("domainObjectSysName", domainObjectSysName);

        return restTemplate.getForObject(builder.toUriString(), DataSource.class);
    }
}