package com.thomsonreuters.dataconnect.executionengine.service.datastream;//package com.thomsonreuters.dataconnect.executionengine.service.datastream;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.Mockito.mock;
//
//import com.thomsonreuters.dataconnect.executionengine.configuration.RabbitMQConfig;
//import com.thomsonreuters.dataconnect.executionengine.configuration.ReceiveRabbitMQConfig;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import org.springframework.amqp.core.Queue;
//import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
//import org.springframework.amqp.rabbit.connection.ConnectionFactory;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
//import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.security.KeyManagementException;
//import java.security.NoSuchAlgorithmException;
//
//@ExtendWith(MockitoExtension.class)
//class RabbitMQConfigTest {
//    @InjectMocks
//    private RabbitMQConfig rabbitMQConfig;
//
//    @InjectMocks
//    private ReceiveRabbitMQConfig receiveRabbitMQConfig;
//
//    @Mock
//    private ConnectionFactory connectionFactory;
//
//    @Mock
//    private RabbitTemplate rabbitTemplate;
//
//    @Mock
//    SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        // Inject the value for the queue name
//        ReflectionTestUtils.setField(rabbitMQConfig, "sourceQueueName", "sourceQueue");
//        ReflectionTestUtils.setField(rabbitMQConfig, "targetQueueName", "targetQueue");
//        ReflectionTestUtils.setField(receiveRabbitMQConfig, "receiveQueueName", "receiveQueue");
//    }
//
//    @Test
//    void shouldValidateSourceQueue_WhenQueueNameIsCorrect() {
//        Queue queue = rabbitMQConfig.sourceQueue();
//        assertNotNull(queue);
//        assertEquals("sourceQueue", queue.getName());
//    }
//
//    @Test
//    void shouldValidateTargetQueue_WhenQueueNameIsCorrect() {
//        Queue queue = rabbitMQConfig.targetQueue();
//        assertNotNull(queue);
//        assertEquals("targetQueue", queue.getName());
//    }
//
//    @Test
//    void shouldValidateReceiveQueue_WhenQueueNameIsCorrect() {
//        Queue queue = receiveRabbitMQConfig.receiveQueue();
//        assertNotNull(queue);
//        assertEquals("receiveQueue", queue.getName());
//    }
//
//    @Test
//    void shouldValidateRabbitTemplate_WhenNotNull() throws NoSuchAlgorithmException, KeyManagementException {
//        RabbitTemplate createdRabbitTemplate = rabbitMQConfig.sourceRabbitTemplate();
//        assertNotNull(createdRabbitTemplate);
//    }
//
//    @Test
//    void shouldValidateContainerFactory_WhenNotNull() throws Exception {
//        SimpleRabbitListenerContainerFactoryConfigurer configurer = new SimpleRabbitListenerContainerFactoryConfigurer(new RabbitProperties());
//        simpleRabbitListenerContainerFactory = mock(SimpleRabbitListenerContainerFactory.class);
//        configurer.configure(simpleRabbitListenerContainerFactory, connectionFactory);
//        SimpleRabbitListenerContainerFactory factory = rabbitMQConfig.rabbitListenerContainerFactory(configurer);
//        assertNotNull(factory);
//    }
//}
