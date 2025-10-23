package com.thomsonreuters.dataconnect.executionengine.configuration;

import com.thomsonreuters.dataconnect.executionengine.model.pojo.RabbitMQTargetConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "spring.rabbitmq.connections.data-sync")
@Getter
@Setter
public class RabbitMQTargetPropsConfig {
    private List<RabbitMQTargetConfig> dataStreamTargets;
    private List<RabbitMQTargetConfig> fileTargets;
}