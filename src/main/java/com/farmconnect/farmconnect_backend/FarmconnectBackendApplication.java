package com.farmconnect.farmconnect_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.farmconnect.farmconnectbackend.model")
@EnableJpaRepositories("com.farmconnect.farmconnectbackend.repository")
public class FarmconnectBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FarmconnectBackendApplication.class, args);
	}

	//helloooooo 
}
