package com.jewoos.securityapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class SecurityApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityApiApplication.class, args);
	}

}
