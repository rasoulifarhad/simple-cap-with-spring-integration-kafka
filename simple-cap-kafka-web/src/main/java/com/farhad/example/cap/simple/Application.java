package com.farhad.example.cap.simple;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;

@SpringBootApplication
@EnableIntegration
@IntegrationComponentScan(basePackages = "com.farhad.example.cap.gateway")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
