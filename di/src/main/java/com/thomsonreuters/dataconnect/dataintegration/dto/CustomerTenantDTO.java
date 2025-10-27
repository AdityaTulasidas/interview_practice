package com.thomsonreuters.dataconnect.dataintegration.dto;

import java.util.List;

public class CustomerTenantDTO {
    private String id;
    private String customer_tenant_name;
    private String customer_id;
    private String domain;
    private String system_name;
    private String sap_account_number;
    private String created_by;
    private String created_at;
    private String updated_by;
    private String updated_at;
    private List<RegionalTenantDTO> regional_tenants;
    private String home_region;

    public CustomerTenantDTO() {}

    // Getters and setters for all fields

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomer_tenant_name() {
        return customer_tenant_name;
    }

    public void setCustomer_tenant_name(String customer_tenant_name) {
        this.customer_tenant_name = customer_tenant_name;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getSystem_name() {
        return system_name;
    }

    public void setSystem_name(String system_name) {
        this.system_name = system_name;
    }

    public String getSap_account_number() {
        return sap_account_number;
    }

    public void setSap_account_number(String sap_account_number) {
        this.sap_account_number = sap_account_number;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_by() {
        return updated_by;
    }

    public void setUpdated_by(String updated_by) {
        this.updated_by = updated_by;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public List<RegionalTenantDTO> getRegional_tenants() {
        return regional_tenants;
    }

    public void setRegional_tenants(List<RegionalTenantDTO> regional_tenants) {
        this.regional_tenants = regional_tenants;
    }

    public String getHome_region() {
        return home_region;
    }

    public void setHome_region(String home_region) {
        this.home_region = home_region;
    }
}
