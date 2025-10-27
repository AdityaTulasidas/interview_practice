package com.thomsonreuters.dataconnect.executionengine.configuration;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;




@Configuration
@Getter
public class TransitHubConfiguration {


     @Value("${transithub.service-url}")
     String serviceUrl;


}
