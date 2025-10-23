package com.aditya.dataconnect.executionengine.services;

import com.aditya.dataconnect.executionengine.dto.MetaRelationMetaModelDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class MetaModelClient {

    @Value("${META_REGISTRY_API_URL}")
    private String metaRegistryUrl;

    @Autowired
    private RestTemplate restTemplate;

    public MetaRelationMetaModelDTO getMetaRelationMetaModel(UUID metaObjectId) {
        String url = metaRegistryUrl +"/meta-object-relations/meta-model/"+ metaObjectId;
        try {
            return restTemplate.getForObject(url, MetaRelationMetaModelDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch MetaRelationMetaModelDTO for MetaObjectId: " + metaObjectId, e);
        }
    }
}