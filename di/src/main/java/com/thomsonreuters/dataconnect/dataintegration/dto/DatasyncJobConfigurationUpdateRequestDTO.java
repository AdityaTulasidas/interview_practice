package com.thomsonreuters.dataconnect.dataintegration.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.ExecType;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.JobType;

import com.thomsonreuters.dataconnect.dataintegration.utils.BaseDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DatasyncJobConfigurationUpdateRequestDTO extends BaseDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(defaultValue = "id", access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "system_name", access = JsonProperty.Access.READ_ONLY)
    private String systemName;

    @JsonProperty("description")
    private String description;

    @JsonProperty(defaultValue = "meta_object_sys_name", access = JsonProperty.Access.READ_ONLY)
    private String metaObjectSysName;

    @JsonProperty(defaultValue = "job_type", access = JsonProperty.Access.READ_ONLY)
    private JobType jobType;

    @JsonProperty(defaultValue = "source")
    private SourceRegionalTenantDTO source;

    @JsonProperty(defaultValue = "customer_tenant_sys_name", access = JsonProperty.Access.READ_ONLY)
    private String customerTenantSysName;

    // Optional on update: null or empty => no change to existing targets
    @JsonProperty("targets")
    private List<TargetRegionDTO> targets;

    @JsonProperty(value="onesource_domain" , access = JsonProperty.Access.READ_ONLY)
    private String onesourceDomain;

    @JsonProperty(defaultValue = "exec_type", access = JsonProperty.Access.READ_ONLY)
    private List<ExecType> execType;

    @JsonProperty("transformations")
    private List<Transformations> transformations;

    @JsonProperty("activities")
    private List<ActivityDTO> activities;

    @JsonProperty(value="is_uge_customer",access = JsonProperty.Access.READ_ONLY )
    private Boolean isUgeCustomer;
}
