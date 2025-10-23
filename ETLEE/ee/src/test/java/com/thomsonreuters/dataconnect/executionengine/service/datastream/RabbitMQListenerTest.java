package com.thomsonreuters.dataconnect.executionengine.service.datastream;

import com.rabbitmq.client.Channel;
import com.thomsonreuters.dataconnect.executionengine.listeners.JobListener;
import com.thomsonreuters.dataconnect.executionengine.model.pojo.DatasyncMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.core.Message;

import static org.junit.jupiter.api.Assertions.*;

class RabbitMQListenerTest {

    @InjectMocks
    private JobListener jobListener;

    @Mock
    private Channel channel;

    @Mock
    private Message message;

    @Mock
    private DatasyncMessage dataSyncMessage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testListenAndConsume_NullMessage() {
        // Call the method with null message and assert exception
        assertThrows(NullPointerException.class, () ->
                jobListener.listenAndConsume(null, message, channel)
        );
    }
}