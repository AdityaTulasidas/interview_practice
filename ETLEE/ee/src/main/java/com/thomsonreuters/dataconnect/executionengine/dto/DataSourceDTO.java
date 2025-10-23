package com.thomsonreuters.dataconnect.executionengine.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.thomsonreuters.dataconnect.executionengine.utils.BaseDTO;

import com.thomsonreuters.dataconnect.executionengine.utils.Trimmed;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.ALWAYS)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"id", "system_name", "description", "db_type", "domain", "regional_tenant_id", "customer_tenant_id", "onesource_region", "user_name", "password", "domain_object_sys_name", "database_name", "host", "port"})
public class DataSourceDTO extends BaseDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;


    @JsonProperty("display_name")
    @NotNull
    private String displayName;

    @JsonProperty("system_name")
    private String systemName;


    @JsonProperty("description")
    private String description;


    @JsonProperty("db_type")
    @NotNull
    @Trimmed
    private String dbType;

    @JsonProperty("domain")
    @NotNull
    private String domain;

    @JsonProperty("regional_tenant_id")
    private String regionalTenantId;

    @JsonProperty("customer_tenant_id")
    private String customerTenantId;

    @JsonProperty("onesource_region")
    @NotNull
    private String onesourceRegion;

    @NotBlank
    @NotNull
    @NotEmpty
    @JsonProperty("user_name")
    private String userName;

    @NotBlank
    @NotNull
    @NotEmpty
    @JsonProperty("password")
    private String password;

    @JsonProperty("domain_object_sys_name")
    @NotNull
    private String domainObjectSysName;

    @NotBlank
    @NotNull
    @NotEmpty
    @JsonProperty("host")
    private String host;

    @NotBlank
    @NotNull
    @NotEmpty
    @JsonProperty("database_name")
    private String db;

    @JsonProperty("port")
    private String port;

}