package com.thomsonreuters.dataconnect.executionengine.utils;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.thomsonreuters.dataconnect.executionengine.constant.Constants.SERVICE_NAME;

@Slf4j
public class RabbitMQConnectionName {

    public static String generateConnectionName(String connectionType) {
        String hostName;
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            hostName= "unknownHost";
        }
        String connectionName = hostName + "-" + connectionType + "-" + SERVICE_NAME;
        log.info("connectionName : {}", connectionName);
        return connectionName;
    }
}
