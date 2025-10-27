package com.thomsonreuters.dataconnect.dataintegration.configuration;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import org.springframework.amqp.core.*;

import static com.thomsonreuters.dataconnect.dataintegration.utils.RabbitMQConnectionName.generateConnectionName;

@Getter
@Configuration
@Slf4j
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.connections.second.queue.name}")
    private String queueName;

    @Value("${spring.rabbitmq.connections.second.host}")
    private String rabbitmqHost;

    @Value("${spring.rabbitmq.connections.second.port}")
    private int rabbitmqPort;

    @Value("${spring.rabbitmq.connections.second.username}")
    private String rabbitmqUsername;

    @Value("${spring.rabbitmq.connections.second.password}")
    private String rabbitmqPassword;

    @Value("${spring.rabbitmq.connections.second.exchange.name}")
    private String sourceListenerExchange;

    @Value("${spring.rabbitmq.connections.second.queue.routing.key.name}")
    private String sourceSenderRoutingKey;


    @Bean
    public Queue queue() {
        return new Queue(queueName, true);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(sourceListenerExchange);
    }

    @Bean
    public Binding sourceSenderQueueBinding() {
        return BindingBuilder.bind(queue()).to(exchange()).with(sourceSenderRoutingKey);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean(name = "dataSyncRabbitTemplate")
    public RabbitTemplate dataSyncRabbitTemplate() throws NoSuchAlgorithmException, KeyManagementException {
        RabbitTemplate template = new RabbitTemplate(sourceCachingConnectionFactory());
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    // Add a RabbitTemplate for the "first" connection (for logging)
    @Bean(name = "logRabbitTemplate")
    public RabbitTemplate logRabbitTemplate(
            @Value("${spring.rabbitmq.connections.first.host}") String host,
            @Value("${spring.rabbitmq.connections.first.port}") int port,
            @Value("${spring.rabbitmq.connections.first.username}") String username,
            @Value("${spring.rabbitmq.connections.first.password}") String password
    ) throws NoSuchAlgorithmException, KeyManagementException {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
     //   connectionFactory.getRabbitConnectionFactory().useSslProtocol();
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    public CachingConnectionFactory sourceCachingConnectionFactory() throws NoSuchAlgorithmException, KeyManagementException {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitmqHost);
        connectionFactory.setPort(rabbitmqPort);
        connectionFactory.setUsername(rabbitmqUsername);
        connectionFactory.setPassword(rabbitmqPassword);
        log.info("Connecting to RabbitMQ at host: {}, port: {}",
                connectionFactory.getHost(), connectionFactory.getPort());
     //   connectionFactory.getRabbitConnectionFactory().useSslProtocol();
        connectionFactory.setConnectionNameStrategy(
                f -> {
                    return generateConnectionName(" sourceRabbitMQConnection");
                });
        return connectionFactory;
    }

}