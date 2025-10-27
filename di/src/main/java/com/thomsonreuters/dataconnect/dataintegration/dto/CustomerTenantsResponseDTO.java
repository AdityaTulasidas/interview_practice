// DTO for /customer-tenants/{system-name} JSON response

package com.thomsonreuters.dataconnect.dataintegration.dto;

import java.util.List;

public class CustomerTenantsResponseDTO {
    private List<CustomerTenantDTO> items;
    private MetaDTO _meta;

    public CustomerTenantsResponseDTO() {}

    public CustomerTenantsResponseDTO(List<CustomerTenantDTO> items, MetaDTO _meta) {
        this.items = items;
        this._meta = _meta;
    }

    public List<CustomerTenantDTO> getItems() {
        return items;
    }

    public void setItems(List<CustomerTenantDTO> items) {
        this.items = items;
    }

    public MetaDTO get_meta() {
        return _meta;
    }

    public void set_meta(MetaDTO _meta) {
        this._meta = _meta;
    }
}
