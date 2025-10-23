package com.aditya.dataconnect.executionengine.configuration;

import com.thomsonreuters.dataconnect.common.logging.LogClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DCLogClientConfig {

    @org.springframework.beans.factory.annotation.Value("${spring.rabbitmq.connections.activity-log.exchange.name}")
    private String exchangeName;

    @org.springframework.beans.factory.annotation.Value("${spring.rabbitmq.connections.activity-log.queues.routing.key.name}")
    private String routingKey;

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("logRabbitTemplate")
    private org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate;

    @Bean
    public LogClient dcLogClient() {
        return new LogClient(rabbitTemplate, exchangeName, routingKey);
    }
}
