package com.thomsonreuters.dataconnect.dataintegration.configuration;

import com.thomsonreuters.dataconnect.common.logging.LogClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DCLogClientConfig {

    @org.springframework.beans.factory.annotation.Value("${spring.rabbitmq.connections.first.exchange.name}")
    private String exchangeName;

    @org.springframework.beans.factory.annotation.Value("${spring.rabbitmq.connections.first.queues.routing.key.name}")
    private String routingKey;

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("logRabbitTemplate")
    private org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate;

    @Bean
    public LogClient dcLogClient() {
        return new LogClient(rabbitTemplate, exchangeName, routingKey);
    }
}
