package com.thomsonreuters.metadataregistry;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.thomsonreuters.metadataregistry")
public class MetadataRegistryApplication {

	public static void main(String[] args) {
		SpringApplication.run(MetadataRegistryApplication.class, args);

	}
	
}