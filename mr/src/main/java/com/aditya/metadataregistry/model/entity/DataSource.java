package com.thomsonreuters.metadataregistry.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thomsonreuters.dep.api.jpa.mapping.annotations.ApiClass;
import com.thomsonreuters.dep.api.jpa.mapping.annotations.ApiField;

import com.thomsonreuters.metadataregistry.model.entity.enums.SystemName;
import com.thomsonreuters.metadataregistry.utils.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@ApiClass(DataSource.class)
@Entity
@Getter
@Setter
@Table(name = "onesource_data_source", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"regional_tenant_id", "domain", "onesource_region", "domain_object_sys_name"}, name = "regional_tenant_domain_region_meta_unique")
})
public class DataSource extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ApiField("displayName")
    @Schema(description = "Display name of the data source")
    @Column(name = "display_name")
    @JsonProperty("display_name")
    @NotNull
    private String displayName;

    @ApiField("systemName")
    @Schema(description = "System name of the data source")
    @Column(name = "system_name")
    @JsonProperty("system_name")
    @NotNull
    private String systemName;

    private String description;

    @Schema(description = "Database vendor for the data source")
    @JsonProperty("db_type")
    @NotNull
    private String dbType;

    @ApiField("domain")
    @Schema(description = "Domain type for the data source")
    @NotBlank
    @NotNull
    @NotEmpty
    @Column(name = "domain")
    @JsonProperty("domain")
    @NotNull
    private String domain;

    @ApiField("regionalTenantId")
    @Column(name ="regional_tenant_id")
    @JsonProperty("regional_tenant_id")
    private String regionalTenantId;

    @ApiField("customerTenantId")
    @Column(name = "customer_tenant_id")
    @JsonProperty("customer_tenant_id")
    private String customerTenantId;

    @ApiField("onesourceRegion")
    @NotNull
    @Column(name = "onesource_region")
    @JsonProperty("onesource_region")
    @Schema(description = "Onesource region for the data source")
    private String onesourceRegion;

    @JsonProperty("user_name")
    @Column(name = "user_name")
    @NotNull
    private String userName;


    @Schema(description = "Domain Object system name for the data source")
    @JsonProperty("domain_object_sys_name")
    @Column(name = "domain_object_sys_name")
    private String domainObjectSysName;

    @Column(name = "password")
    @NotNull
    private String password;

    @Column(name = "host")
    @NotNull
    private String host;

    @JsonProperty("database_name")
    @Column(name="db")
    @NotNull
    private String db;

    @Column(name = "port")
    private String port;

    @PrePersist
    protected void onCreate() {
        this.setCreatedAt(LocalDateTime.now());
    }

    @PreUpdate
    private void preUpdate() {
        this.setUpdatedAt(LocalDateTime.now());
    }


}