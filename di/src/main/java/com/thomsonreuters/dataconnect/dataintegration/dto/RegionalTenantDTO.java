package com.thomsonreuters.dataconnect.dataintegration.dto;

public class RegionalTenantDTO {
    private String id;
    private String tenant_code;
    private String customer_tenant_id;
    private String region;
    private String tenant_name;
    private String created_by;
    private String created_at;
    private boolean is_home_region;

    public RegionalTenantDTO() {}

    // Getters and setters for all fields

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenant_code() {
        return tenant_code;
    }

    public void setTenant_code(String tenant_code) {
        this.tenant_code = tenant_code;
    }

    public String getCustomer_tenant_id() {
        return customer_tenant_id;
    }

    public void setCustomer_tenant_id(String customer_tenant_id) {
        this.customer_tenant_id = customer_tenant_id;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getTenant_name() {
        return tenant_name;
    }

    public void setTenant_name(String tenant_name) {
        this.tenant_name = tenant_name;
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

    public boolean isIs_home_region() {
        return is_home_region;
    }

    public void setIs_home_region(boolean is_home_region) {
        this.is_home_region = is_home_region;
    }
}
