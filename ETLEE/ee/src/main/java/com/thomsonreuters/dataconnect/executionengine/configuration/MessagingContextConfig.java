package com.thomsonreuters.dataconnect.executionengine.configuration;

import com.thomsonreuters.dataconnect.common.executioncontext.MessagingContext;
import com.thomsonreuters.dataconnect.executionengine.model.pojo.SslConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingContextConfig {

    @Value("${spring.rabbitmq.connections.data-sync.source.host}")
    private String host;

    @Value("${spring.rabbitmq.connections.data-sync.source.port}")
    private int port;

    @Value("${spring.rabbitmq.connections.data-sync.source.username}")
    private String username;

    @Value("${spring.rabbitmq.connections.data-sync.source.password}")
    private String password;

    @Value("${spring.rabbitmq.connections.data-sync.source.ssl.enabled}")
    private boolean sslEnabled;

    @Value("${spring.rabbitmq.connections.data-sync.source.ssl.algorithm}")
    private String sslAlgorithm;

    @Value("${spring.rabbitmq.connections.data-sync.source.source.exchange.name}")
    private String exchange;

    @Bean
    public MessagingContext messagingContext(){

        MessagingContext msgCtx = new MessagingContext();
        msgCtx.setValue(MessagingContext.HOST, host);
        //msgCtx.setValue(MessagingContext.PORT, port);
        msgCtx.setValue(MessagingContext.USERNAME, username);
        msgCtx.setValue(MessagingContext.PASSWORD, password);
        SslConfig sslConfig = new SslConfig();
        sslConfig.setEnabled(sslEnabled);
        sslConfig.setAlgorithm(sslAlgorithm);
        msgCtx.setValue(MessagingContext.SSL, sslConfig);
        msgCtx.setValue(MessagingContext.EXCHANGE, exchange);
        return msgCtx;
    }
}
