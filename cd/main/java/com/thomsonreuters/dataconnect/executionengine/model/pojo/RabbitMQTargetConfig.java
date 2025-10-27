package com.thomsonreuters.dataconnect.executionengine.model.pojo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RabbitMQTargetConfig {
    private String regionKey;
    private String host;
    private int port;
    private String username;
    private String password;
    private SslConfig ssl;
    private QueueName queue;
    private Exchange exchange;
    private Routing routing;
    private int concurrentConsumers;
    private int maxConcurrentConsumers;

}