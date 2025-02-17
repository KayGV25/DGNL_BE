package com.dgnl_backend.project.dgnl_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.dgnl_backend.project.dgnl_backend.schemas")
@EnableJpaRepositories(basePackages = "com.dgnl_backend.project.dgnl_backend.repositories")
public class DgnlBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(DgnlBackendApplication.class, args);
	}

}
