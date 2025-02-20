package com.dgnl_backend.project.dgnl_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class DgnlBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(DgnlBackendApplication.class, args);
	}

}
