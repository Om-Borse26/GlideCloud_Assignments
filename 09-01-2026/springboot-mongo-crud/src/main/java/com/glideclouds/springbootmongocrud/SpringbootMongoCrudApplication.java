package com.glideclouds.springbootmongocrud;

import com.glideclouds.springbootmongocrud.config.EnvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringbootMongoCrudApplication {

	public static void main(String[] args) {
		// Load .env file BEFORE Spring Boot starts
		EnvLoader.loadEnv();

		SpringApplication.run(SpringbootMongoCrudApplication.class, args);
	}
}
