package com.aditya.dataconnect.executionengine.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.aditya.dataconnect.executionengine.model.pojo.RabbitMQTargetConfig;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static com.aditya.dataconnect.executionengine.constant.Constants.TARGET_RABBITMQ_CONNECTION_NAME;

@Configuration
public class RabbitMQTargetsConfig {
    @Autowired
    RabbitMQTargetPropsConfig rabbitMQTargetPropsConfig;

    @Bean
    public static Jackson2JsonMessageConverter targetsJsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();

        // Register JavaTimeModule to handle LocalDateTime
        objectMapper.registerModule(new JavaTimeModule());

        // Disable timestamps (otherwise it serializes as array)
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Optional: Set default date format if needed
        objectMapper.setDateFormat(new StdDateFormat());

        return new Jackson2JsonMessageConverter(objectMapper);
    }

    /* ToDo Remove the below beans and create a single bean for RabbitMQ connection factory and RabbitTemplate
            using GlobalContext once there is a solution of shovel plugin */
    @Bean
    public Map<String, CachingConnectionFactory> rabbitMQConnectionFactories() throws NoSuchAlgorithmException, KeyManagementException {
        Map<String, CachingConnectionFactory> factories = new HashMap<>();
        for (RabbitMQTargetConfig config : rabbitMQTargetPropsConfig.getDataStreamTargets()) {
            CachingConnectionFactory factory = new CachingConnectionFactory(config.getHost(), config.getPort());
            factory.setUsername(config.getUsername());
            factory.setPassword(config.getPassword());
            if (config.getSsl() != null && config.getSsl().isEnabled()) {
                factory.getRabbitConnectionFactory().useSslProtocol(config.getSsl().getAlgorithm());
            }
            factory.setConnectionNameStrategy(conn -> config.getRegionKey() + TARGET_RABBITMQ_CONNECTION_NAME);
            factories.put(config.getRegionKey(), factory);
        }
        return factories;
    }

    @Bean(name = "targetsRabbitMQTemplates")
    public Map<String, RabbitTemplate> targetsRabbitMQTemplates() throws NoSuchAlgorithmException, KeyManagementException {
        Map<String, RabbitTemplate> templates = new HashMap<>();
        Map<String, CachingConnectionFactory> rabbitMQConnectionFactories = rabbitMQConnectionFactories();
        for (Map.Entry<String, CachingConnectionFactory> entry : rabbitMQConnectionFactories.entrySet()) {
            RabbitTemplate template = new RabbitTemplate(entry.getValue());
            template.setMessageConverter(targetsJsonMessageConverter());
            templates.put(entry.getKey(), template);
        }
        return templates;
    }

    @Bean(name = "targetsRabbitMQExchanges")
    public Map<String, String> targetsRabbitMQExchanges() {
        Map<String, String> exchanges = new HashMap<>();
        for (RabbitMQTargetConfig config : rabbitMQTargetPropsConfig.getDataStreamTargets()) {
            if (config.getExchange() != null && config.getExchange().getName() != null) {
                exchanges.put(config.getRegionKey(), config.getExchange().getName());
            }
        }
        return exchanges;
    }

    @Bean(name = "targetsRabbitMQRoutingKeys")
    public Map<String, String> targetsRabbitMQRoutingKeys() {
        Map<String, String> routings = new HashMap<>();
        for (RabbitMQTargetConfig config : rabbitMQTargetPropsConfig.getDataStreamTargets()) {
            if (config.getRouting() != null && config.getRouting().getKey() != null) {
                routings.put(config.getRegionKey(), config.getRouting().getKey().getName());
            }
        }
        return routings;
    }

    /* ToDo Remove the below beans and create a single bean for RabbitMQ connection factory and RabbitTemplate
            using GlobalContext once there is a solution of shovel plugin */
    @Bean
    public Map<String, CachingConnectionFactory> fileRabbitMQConnectionFactories() throws NoSuchAlgorithmException, KeyManagementException {
        Map<String, CachingConnectionFactory> factories = new HashMap<>();
        for (RabbitMQTargetConfig config : rabbitMQTargetPropsConfig.getFileTargets()) {
            CachingConnectionFactory factory = new CachingConnectionFactory(config.getHost(), config.getPort());
            factory.setUsername(config.getUsername());
            factory.setPassword(config.getPassword());
            if (config.getSsl() != null && config.getSsl().isEnabled()) {
                factory.getRabbitConnectionFactory().useSslProtocol(config.getSsl().getAlgorithm());
            }
            factory.setConnectionNameStrategy(conn -> config.getRegionKey() + TARGET_RABBITMQ_CONNECTION_NAME);
            factories.put(config.getRegionKey(), factory);
        }
        return factories;
    }

    @Bean(name = "fileTargetsRabbitMQTemplates")
    public Map<String, RabbitTemplate> fileTargetsRabbitMQTemplates() throws NoSuchAlgorithmException, KeyManagementException {
        Map<String, RabbitTemplate> templates = new HashMap<>();
        Map<String, CachingConnectionFactory> rabbitMQConnectionFactories = fileRabbitMQConnectionFactories();
        for (Map.Entry<String, CachingConnectionFactory> entry : rabbitMQConnectionFactories.entrySet()) {
            RabbitTemplate template = new RabbitTemplate(entry.getValue());
            template.setMessageConverter(targetsJsonMessageConverter());
            templates.put(entry.getKey(), template);
        }
        return templates;
    }

    @Bean(name = "fileTargetsRabbitMQExchanges")
    public Map<String, String> fileTargetsRabbitMQExchanges() {
        Map<String, String> exchanges = new HashMap<>();
        for (RabbitMQTargetConfig config : rabbitMQTargetPropsConfig.getFileTargets()) {
            if (config.getExchange() != null && config.getExchange().getName() != null) {
                exchanges.put(config.getRegionKey(), config.getExchange().getName());
            }
        }
        return exchanges;
    }

    @Bean(name = "fileTargetsRabbitMQRoutingKeys")
    public Map<String, String> fileTargetsRabbitMQRoutingKeys() {
        Map<String, String> routings = new HashMap<>();
        for (RabbitMQTargetConfig config : rabbitMQTargetPropsConfig.getFileTargets()) {
            if (config.getRouting() != null && config.getRouting().getKey() != null) {
                routings.put(config.getRegionKey(), config.getRouting().getKey().getName());
            }
        }
        return routings;
    }

}
