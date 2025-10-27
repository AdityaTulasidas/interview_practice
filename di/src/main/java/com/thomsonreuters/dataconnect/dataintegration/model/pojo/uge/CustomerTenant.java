package com.thomsonreuters.dataconnect.dataintegration.model.pojo.uge;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a customer tenant with regional tenant mappings.
 * Sample JSON:
 * {
 *   "id":"01995179-3790-73a2-850a-5a558d48216d",
 *   "customer_tenant_name":"Murphy LLC TA.CT 6aG9c",
 *   "customer_id":"01995179-36d7-7440-bfe1-891b7d436fc8",
 *   "domain":"sunny-certification.com",
 *   "system_name":"rJIFSjTKtV",
 *   "sap_account_number":"BDDSFeN62i",
 *   "created_by":"piotr.niedzialek.ea0",
 *   "created_at":"09/16/2025 07:42:02",
 *   "updated_by":null,
 *   "updated_at":null,
 *   "regional_tenants":[ { ... see RegionalTenant } ],
 *   "home_region":"DEV2"
 * }
 *
 * Dates retained as raw String due to mixed formats (e.g. '09/16/2025 07:42:02' vs ISO8601 in regional tenants).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerTenant {

    @JsonProperty("id")
    private String id;

    @JsonProperty("customer_tenant_name")
    private String customerTenantName;

    @JsonProperty("customer_id")
    private String customerId;

    @JsonProperty("domain")
    private String domain;

    @JsonProperty("system_name")
    private String systemName;

    @JsonProperty("sap_account_number")
    private String sapAccountNumber;

    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_by")
    private String updatedBy;

    @JsonProperty("updated_at")
    private String updatedAt;

    @JsonProperty("regional_tenants")
    private List<RegionalTenant> regionalTenants;

    @JsonProperty("home_region")
    private String homeRegion;

    /**
     * Convenience: return the RegionalTenant marked as home region (is_home_region = true)
     */
    public RegionalTenant getHomeRegionalTenant() {
        if (regionalTenants == null) return null;
        return regionalTenants.stream()
                .filter(r -> Boolean.TRUE.equals(r.getHomeRegionFlag()))
                .findFirst()
                .orElse(null);
    }
}
