package com.thomsonreuters.dataconnect.executionengine.utils;

import com.thomsonreuters.dataconnect.executionengine.constant.Constants;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class ConnectionFactory {
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(Constants.CONNECTION_URL, Constants.CONNECTION_USER , Constants.CONNECTION_USER);
    }

    public static Connection getConnection(String url, String user, String password) throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
