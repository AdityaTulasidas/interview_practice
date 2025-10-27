package com.thomsonreuters.dataconnect.executionengine.configuration;

import com.thomsonreuters.dataconnect.executionengine.dto.DatasyncJobConfigurationRequestDTO;
import com.thomsonreuters.dataconnect.executionengine.model.entity.DatasyncJobConfiguration;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}