package com.thomsonreuters.metadataregistry.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "onesource_database_type")
public class OnesourceDatabaseType {

    @Id
    @Column(name = "db_type", nullable = false)
    @NotBlank(message = "Database type cannot be blank")
    @Schema(description = "Database type identifier (e.g., POSTGRESQL, MYSQL)")
    private String dbType;

    @Column(name = "jdbc_driver", nullable = false)
    @NotBlank(message = "JDBC driver cannot be blank")
    @Schema(description = "JDBC driver class name for the database type")
    private String jdbcDriver;

    @Column(name = "default_port")
    @Schema(description = "Default port number for the database type")
    private Integer defaultPort;

    @Column(name= "jdbc_template")
    private String jdbcTemplate;

}
