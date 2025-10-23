package com.thomsonreuters.dataconnect.executionengine.configuration;

import com.thomsonreuters.dataconnect.executionengine.model.entity.JobExecutionLog;
import com.thomsonreuters.dep.api.jpa.mapping.JPAFieldMapper;
import com.thomsonreuters.dep.api.spring.configuration.JPAFieldMapperConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class FieldMapperConfig {

    @Bean
    public JPAFieldMapperConfiguration jpaFieldMapperConfiguration() {
        JPAFieldMapperConfiguration configuration = new JPAFieldMapperConfiguration();
        configuration.addAllMappings(

                JobExecutionLog.class

        );
        log.info("Bean 'JPAFieldMapperConfiguration' loaded.");
        return configuration;

    }
}