package com.thomsonreuters.dataconnect.executionengine.configuration;

import com.thomsonreuters.dataconnect.common.executioncontext.GlobalContext;
import com.thomsonreuters.dataconnect.common.executioncontext.MessagingContext;
import com.thomsonreuters.dataconnect.executionengine.model.pojo.SslConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GlobalContextConfig {
    @Value("${app.region}")
    private String hostRegion;

    @Value("${app.provider}")
    private String cloudProvider;

    @Autowired
    MessagingContext messagingContext;

    @Bean
    public GlobalContext globalContext(){
        GlobalContext globalCtx = GlobalContext.getInstance();
        globalCtx.setValue(GlobalContext.HOST_REGION, hostRegion);
        globalCtx.setValue(GlobalContext.CLOUD_PROVIDER, cloudProvider);
        globalCtx.setValue(GlobalContext.MESSAGING_CONTEXT, messagingContext);
        return globalCtx;
    }
}
