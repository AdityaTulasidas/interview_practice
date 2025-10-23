package com.thomsonreuters.metadataregistry.service;

import com.thomsonreuters.metadataregistry.exceptionhandler.MetaDataRegistryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

 class DBAdaptorServiceTest {

    @InjectMocks
    private DBAdaptorService dbAdaptorService;

    @Mock
    private DataSource postgresqlDataSource;

    @Mock
    private Connection connection;

    @BeforeEach
    public void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(postgresqlDataSource.getConnection()).thenReturn(connection);
    }

    @Test
    void ShouldGetPostgreSQLConnectionError_WhenServerNotAvailable() throws SQLException {
        when(postgresqlDataSource.getConnection()).thenThrow(new SQLException("Error connecting to PostgreSQL server"));

        MetaDataRegistryException exception = assertThrows(MetaDataRegistryException.class, () -> {
            dbAdaptorService.getPostgreSQLConnection();
        });

        assertEquals("Failed to connect to PostgreSQL", exception.getMessage());
        assertEquals("INTERNAL_SERVER_ERROR", exception.getCode());
    }
}