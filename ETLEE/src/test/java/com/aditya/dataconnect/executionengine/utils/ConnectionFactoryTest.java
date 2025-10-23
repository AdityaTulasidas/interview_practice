package com.aditya.dataconnect.executionengine.utils;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConnectionFactoryTest {

    @Test
    void shouldThrowSQLException_WhenInvalidCredentials() {
        ConnectionFactory connectionFactory = new ConnectionFactory() {
            @Override
            public Connection getConnection() throws SQLException {
                return DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "invalid", "invalid");
            }
        };
        assertThrows(SQLException.class, connectionFactory::getConnection);
    }

    @Test
    void shouldThrowSQLException_WhenInvalidUrl() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        assertThrows(SQLException.class, () -> connectionFactory.getConnection("jdbc:invalid://localhost:5432/postgres", "postgresql", "postgresql"));
    }

    @Test
    void shouldThrowSQLException_WhenInvalidUser() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        assertThrows(SQLException.class, () -> connectionFactory.getConnection("jdbc:postgresql://localhost:5432/postgres", "invalid", "postgresql"));
    }

    @Test
    void shouldThrowSQLException_WhenInvalidPassword() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
    }
}