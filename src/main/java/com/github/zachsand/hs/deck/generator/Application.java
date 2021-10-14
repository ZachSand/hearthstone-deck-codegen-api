package com.github.zachsand.hs.deck.generator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * The main entry point for starting the Spring Boot application.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class Application {

	public static void main(final String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
