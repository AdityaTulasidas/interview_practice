package com.thomsonreuters.metadataregistry.service;

import com.thomsonreuters.metadataregistry.exceptionhandler.MetaDataRegistryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Service
public class DBAdaptorService {


    private final DataSource postgresqlDataSource;

    @Autowired
    public DBAdaptorService(DataSource postgresqlDataSource) {
        this.postgresqlDataSource = postgresqlDataSource;
    }

    public String getPostgreSQLConnection() {
        try (Connection connection = postgresqlDataSource.getConnection()) {
            return "Connection to PostgreSQL is successful";
        } catch (SQLException e) {
            throw new MetaDataRegistryException("Failed to connect to PostgreSQL", "INTERNAL_SERVER_ERROR");
        }
    }
}
