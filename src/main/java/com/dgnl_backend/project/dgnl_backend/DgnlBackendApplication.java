package com.dgnl_backend.project.dgnl_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.dgnl_backend.project.dgnl_backend.repositories")
@EntityScan(basePackages = "com.dgnl_backend.project.dgnl_backend.schemas")
@EnableCaching
public class DgnlBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(DgnlBackendApplication.class, args);
	}

}
