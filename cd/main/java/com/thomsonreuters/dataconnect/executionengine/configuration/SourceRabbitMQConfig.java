package com.thomsonreuters.dataconnect.executionengine.configuration;

import lombok.Getter;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import static com.thomsonreuters.dataconnect.executionengine.constant.Constants.SOURCE_RABBITMQ_CONNECTION_NAME;
import static com.thomsonreuters.dataconnect.executionengine.utils.RabbitMQConnectionName.generateConnectionName;

@EnableRabbit
@Configuration
@Getter
public class SourceRabbitMQConfig {

    @Value("${spring.rabbitmq.connections.data-sync.source.source.queue.name}")
    private String sourceQueueName;

    @Value("${spring.rabbitmq.connections.data-sync.source.job.queue.name}")
    private String jobQueueName;

    @Value("${spring.rabbitmq.connections.data-sync.source.host}")
    private String rabbitmqHost;

    @Value("${spring.rabbitmq.connections.data-sync.source.port}")
    private int rabbitmqPort;

    @Value("${spring.rabbitmq.connections.data-sync.source.username}")
    private String rabbitmqUsername;

    @Value("${spring.rabbitmq.connections.data-sync.source.password}")
    private String rabbitmqPassword;

    @Value("${spring.rabbitmq.connections.data-sync.source.concurrentConsumers}")
    private int concurrentConsumers;

    @Value("${spring.rabbitmq.connections.data-sync.source.maxConcurrentConsumers}")
    private int maxConcurrentConsumers;

    @Value("${spring.rabbitmq.connections.data-sync.source.prefetchCount}")
    private int prefetchCount;

    @Value("${spring.rabbitmq.connections.data-sync.source.source.exchange.name}")
    private String sourceListenerExchange;

    @Value("${spring.rabbitmq.connections.data-sync.source.job.exchange.name}")
    private String sourceTaskExchange;

    @Value("${spring.rabbitmq.connections.data-sync.source.source.routing.key.name}")
    private String sourceListenerRoutingKey;
    @Value("${spring.rabbitmq.connections.data-sync.source.job.routing.key.name}")
    private String jobRoutingKey;



    @Bean
    public DirectExchange dataSyncExchange() {
        return new DirectExchange(sourceTaskExchange);
    }

    @Bean
    public DirectExchange dataTaskExchange() {
        return new DirectExchange(sourceListenerExchange);
    }

    @Bean
    public Binding dataTaskQueueBinding() {
        return BindingBuilder.bind(jobQueue()).to(dataSyncExchange()).with(jobRoutingKey);

    }

    @Bean
    public Binding dataSyncBinding() {
        return BindingBuilder.bind(sourceQueue()).to(dataTaskExchange()).with(sourceListenerRoutingKey);


    }



    @Bean
    public Queue sourceQueue() {
        return new Queue(sourceQueueName, true);

    }

    @Bean
    public Queue jobQueue() {
        return new Queue(jobQueueName, true);

    }


    @Bean
    public static Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    @Primary
    public RabbitTemplate sourceRabbitTemplate() throws NoSuchAlgorithmException, KeyManagementException {
        RabbitTemplate template = new RabbitTemplate(sourceCachingConnectionFactory());
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }


    @Bean(name = "rabbitJobListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory rabbitJobListenerContainerFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer) throws NoSuchAlgorithmException, KeyManagementException{
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(sourceCachingConnectionFactory());
        final Jackson2JsonMessageConverter messageConverter = new Jackson2JsonMessageConverter();
        factory.setMessageConverter(messageConverter);
        factory.setConcurrentConsumers(concurrentConsumers);
        factory.setMaxConcurrentConsumers(maxConcurrentConsumers);
        factory.setPrefetchCount(prefetchCount);
        configurer.configure(factory, sourceCachingConnectionFactory());
        BackOff recoveryBackOff = new FixedBackOff(5000, 3);
        factory.setRecoveryBackOff(recoveryBackOff);

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(maxConcurrentConsumers);
        executor.setMaxPoolSize(maxConcurrentConsumers);
        executor.setThreadNamePrefix("JobListenerConsumer-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();

        factory.setTaskExecutor(executor);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL); // only if you're using manual ack
        return factory;
    }

    @Bean(name = "rabbitDataStreamListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory rabbitDataStreamListenerContainerFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer) throws NoSuchAlgorithmException, KeyManagementException{
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(sourceCachingConnectionFactory());
        final Jackson2JsonMessageConverter messageConverter = new Jackson2JsonMessageConverter();
        factory.setMessageConverter(messageConverter);
        factory.setConcurrentConsumers(concurrentConsumers);
        factory.setMaxConcurrentConsumers(maxConcurrentConsumers);
        factory.setPrefetchCount(prefetchCount);
        configurer.configure(factory, sourceCachingConnectionFactory());
        BackOff recoveryBackOff = new FixedBackOff(5000, 3);
        factory.setRecoveryBackOff(recoveryBackOff);

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(maxConcurrentConsumers);
        executor.setMaxPoolSize(maxConcurrentConsumers);
        executor.setThreadNamePrefix("DataStreamListenerConsumer-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();

        factory.setTaskExecutor(executor);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL); // only if you're using manual ack
        return factory;
    }

    @Bean(name = "rabbitFileListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory rabbitFileListenerContainerFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer) throws NoSuchAlgorithmException, KeyManagementException{
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(sourceCachingConnectionFactory());
        final Jackson2JsonMessageConverter messageConverter = new Jackson2JsonMessageConverter();
        factory.setMessageConverter(messageConverter);
        factory.setConcurrentConsumers(concurrentConsumers);
        factory.setMaxConcurrentConsumers(maxConcurrentConsumers);
        factory.setPrefetchCount(prefetchCount);
        configurer.configure(factory, sourceCachingConnectionFactory());
        BackOff recoveryBackOff = new FixedBackOff(5000, 3);
        factory.setRecoveryBackOff(recoveryBackOff);

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(maxConcurrentConsumers);
        executor.setMaxPoolSize(maxConcurrentConsumers);
        executor.setThreadNamePrefix("FileListenerConsumer-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();

        factory.setTaskExecutor(executor);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL); // only if you're using manual ack
        return factory;
    }

    @Bean
    public CachingConnectionFactory sourceCachingConnectionFactory() throws NoSuchAlgorithmException, KeyManagementException {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitmqHost);
        connectionFactory.setPort(rabbitmqPort);
        connectionFactory.setUsername(rabbitmqUsername);
        connectionFactory.setPassword(rabbitmqPassword);
        connectionFactory.getRabbitConnectionFactory().useSslProtocol();
        connectionFactory.setConnectionNameStrategy(
                f -> {
                    return generateConnectionName(SOURCE_RABBITMQ_CONNECTION_NAME);
                });
        return connectionFactory;
    }

    // Add a RabbitTemplate for the "first" connection (for logging)
    @Bean(name = "logRabbitTemplate")
    public RabbitTemplate logRabbitTemplate(
            @Value("${spring.rabbitmq.connections.activity-log.host}") String host,
            @Value("${spring.rabbitmq.connections.activity-log.port}") int port,
            @Value("${spring.rabbitmq.connections.activity-log.username}") String username,
            @Value("${spring.rabbitmq.connections.activity-log.password}") String password
    ) throws NoSuchAlgorithmException, KeyManagementException {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.getRabbitConnectionFactory().useSslProtocol();
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

}