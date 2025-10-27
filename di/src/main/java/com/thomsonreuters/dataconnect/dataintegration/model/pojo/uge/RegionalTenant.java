package com.thomsonreuters.dataconnect.dataintegration.model.pojo.uge;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a regional tenant entry inside a CustomerTenant response.
 * Sample JSON element:
 * {
 *   "id":"01995179-39b9-71db-96b7-b1094504a2b9",
 *   "tenant_code":"W4X",
 *   "customer_tenant_id":"01995179-3790-73a2-850a-5a558d48216d",
 *   "region":"DEV2",
 *   "tenant_name":"tewsst",
 *   "created_by":"piotr.niedzialek.ea0",
 *   "created_at":"2025-09-16T07:42:03.193857Z",
 *   "is_home_region":true
 * }
 *
 * Dates are kept as raw String because formats may vary or evolve.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegionalTenant {

    @JsonProperty("id")
    private String id;

    @JsonProperty("tenant_code")
    private String tenantCode;

    @JsonProperty("customer_tenant_id")
    private String customerTenantId;

    @JsonProperty("region")
    private String region;

    @JsonProperty("tenant_name")
    private String tenantName;

    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("is_home_region")
    private Boolean homeRegionFlag;

    // Explicit getters to satisfy annotation processing limitations in some test compile contexts
    public String getId() {
        return id;
    }
    public String getTenantCode() {
        return tenantCode;
    }
    public String getCustomerTenantId() {
        return customerTenantId;
    }
    public String getRegion() {
        return region;
    }
    public String getTenantName() {
        return tenantName;
    }
    public String getCreatedBy() {
        return createdBy;
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public Boolean getHomeRegionFlag() {
        return homeRegionFlag;
    }
}
