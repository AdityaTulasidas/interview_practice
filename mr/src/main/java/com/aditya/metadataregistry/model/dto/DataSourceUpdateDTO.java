package com.thomsonreuters.metadataregistry.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.thomsonreuters.metadataregistry.utils.BaseDTO;
import com.thomsonreuters.metadataregistry.utils.Trimmed;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
@JsonInclude(JsonInclude.Include.ALWAYS)
@JsonIgnoreProperties(ignoreUnknown = false)
@JsonPropertyOrder({
        "id", "system_name", "description", "db_type", "domain", "regional_tenant_id",
        "customer_tenant_id", "onesource_region", "user_name", "password",
        "domain_object_sys_name", "database_name", "host", "port"
})
public class DataSourceUpdateDTO extends BaseDTO {



    @JsonProperty("display_name")
    @NotNull
    private String displayName;



    @JsonProperty("description")
    private String description;




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

}