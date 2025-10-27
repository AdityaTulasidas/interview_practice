package com.thomsonreuters.dataconnect.dataintegration.configuration;

import com.thomsonreuters.dataconnect.dataintegration.model.entity.enums.*;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {

        ModelMapper modelMapper = new ModelMapper();


        // Add a converter to trim string values
        Converter<String, String> stringTrimmer = new Converter<String, String>() {
            @Override
            public String convert(MappingContext<String, String> context) {
                return context.getSource() == null ? null : context.getSource().trim();
            }
        };



        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.addConverter(stringTrimmer);



        modelMapper.createTypeMap(String.class, ExecType.class).setConverter(createEnumConverter(ExecType.class));
        modelMapper.createTypeMap(String.class, JobType.class).setConverter(createEnumConverter(JobType.class));



        return modelMapper;
    }


    private <E extends Enum<E>> Converter<String, E> createEnumConverter(Class<E> enumType) {
        return new Converter<String, E>() {
            @Override
            public E convert(MappingContext<String, E> context) {
                String source = context.getSource();
                return source == null ? null : Enum.valueOf(enumType, source.trim());
            }
        };
    }
}