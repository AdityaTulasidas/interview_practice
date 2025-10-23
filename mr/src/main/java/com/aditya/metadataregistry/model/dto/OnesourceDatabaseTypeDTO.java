package com.thomsonreuters.metadataregistry.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thomsonreuters.metadataregistry.model.entity.OnesourceDatabaseType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OnesourceDatabaseTypeDTO {
    @JsonProperty("db_type")
    private String dbType;
    @JsonProperty("jdbc_driver")
    private String jdbcDriver;
    @JsonProperty("default_port")
    private Integer defaultPort;
    @JsonProperty("jdbc_template")
    private String jdbcTemplate;

    public OnesourceDatabaseTypeDTO() {}



    public OnesourceDatabaseTypeDTO(OnesourceDatabaseType entity) {
        this.dbType = entity.getDbType();
        this.jdbcDriver = entity.getJdbcDriver();
        this.defaultPort = entity.getDefaultPort();
        this.jdbcTemplate = entity.getJdbcTemplate();
    }
}

