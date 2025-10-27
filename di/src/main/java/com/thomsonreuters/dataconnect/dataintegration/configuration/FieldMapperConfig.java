package com.thomsonreuters.dataconnect.dataintegration.configuration;

import com.thomsonreuters.dataconnect.dataintegration.model.entity.DatasyncJobConfiguration;
import com.thomsonreuters.dataconnect.dataintegration.model.entity.JobExecutionLog;
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

                DatasyncJobConfiguration.class

        );
        log.info("Bean 'JPAFieldMapperConfiguration' loaded.");
        return configuration;

    }
}