package com.aditya.dataconnect.executionengine.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.aditya.dataconnect.executionengine.model.entity.enums.ExecType;
import com.aditya.dataconnect.executionengine.model.entity.enums.JobType;
import com.aditya.dataconnect.executionengine.transformation.Transformations;
import com.aditya.dataconnect.executionengine.utils.BaseDTO;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static java.util.Comparator.comparingInt;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Service
public class DatasyncJobConfigurationRequestDTO extends BaseDTO {


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @JsonProperty("description")
    private String description;

    @NotNull
    @JsonProperty("meta_object_sys_name")
    private String metaObjectSysName;

    @NotNull
    @JsonProperty("job_type")
    private JobType jobType;

    @JsonProperty("customer_tenant_sys_name")
    private String customerTenantSysName;

    @NotNull
    @JsonProperty("source")
    private SourceRegionDTO source;

    @NotNull
    @NotEmpty
    @JsonProperty("targets")
    private List<TargetRegionDTO> targets;

    @NotNull
    @JsonProperty("exec_type")
    private List<ExecType> execType;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "system_name", access = JsonProperty.Access.READ_ONLY)
    private String systemName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "onesource_domain", access = JsonProperty.Access.READ_ONLY)
    private String onesourceDomain;

    @JsonProperty("transformations")
    private List<Transformations> transformations;

    @JsonProperty("activities")
    private List<ActivityDTO> activities;

    // Custom setter to sort transformations by seq
    public void setTransformations(List<Transformations> transformations) {
        if (transformations != null) {
            transformations.sort(comparingInt(Transformations::getSeq));
        }
        this.transformations = transformations;
    }

    @JsonProperty("is_uge_customer")
    private Boolean isUgeCustomer;
}
