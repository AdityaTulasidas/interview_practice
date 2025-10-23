package com.thomsonreuters.dataconnect.executionengine.services.datastream.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ListenerStatusMonitor {

    @Autowired
    private RabbitListenerEndpointRegistry registry;

//    @Scheduled(fixedRate = 10000) // every 10 seconds
//    public void checkListenerStatus() {
//        registry.getListenerContainers().forEach(container -> {
//            String id = container.getMessageListener().toString();
//            boolean isRunning = container.isRunning();
//            log.info("Listener ID: " + id + " | Running: " + isRunning);
//        });
//    }
}
