package com.thomsonreuters.metadataregistry.configuration;

import com.thomsonreuters.metadataregistry.model.dto.MetaObjectDTO;
import com.thomsonreuters.metadataregistry.model.dto.MetaObjectDetailsDTO;
import com.thomsonreuters.metadataregistry.model.entity.MetaObject;
import com.thomsonreuters.metadataregistry.model.entity.enums.DataType;
import com.thomsonreuters.metadataregistry.model.entity.enums.DatabaseVendor;

import com.thomsonreuters.metadataregistry.utils.TrimmedEnumConverter;
import org.hibernate.collection.spi.PersistentSet;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();


        //Custom converter for PersistentSet to Set
        // Custom converter for PersistentSet to Set
        Converter<PersistentSet<Object>, Set<?>> persistentSetConverter = context ->
                new HashSet<>(context.getSource());

        modelMapper.addConverter(persistentSetConverter);


        // Global String Trimmer
        modelMapper.addConverter(ctx -> {
            if (ctx.getSource() == null) return null;
            return ctx.getSource().trim();
        }, String.class, String.class);

        Converter<String, String> stringTrimmer = ctx -> {
            if (ctx.getSource() == null) return null;
            return ctx.getSource().trim();
        };

        // Add converter for all String types
        modelMapper.addConverter(stringTrimmer, String.class, String.class);

        // Trimmed String -> Enum converters
        modelMapper.addConverter(new TrimmedEnumConverter<>(DatabaseVendor.class), String.class, DatabaseVendor.class);
        modelMapper.addConverter(new TrimmedEnumConverter<>(DataType.class), String.class, DataType.class);


        modelMapper.addMappings(new PropertyMap<MetaObject, MetaObjectDTO>() {
            @Override
            protected void configure() {
                map().setSystemName(source.getSystemName());
            }
        });



        return modelMapper;
    }


}
