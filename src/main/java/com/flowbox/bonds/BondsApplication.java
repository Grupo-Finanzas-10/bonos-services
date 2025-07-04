package com.flowbox.bonds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BondsApplication {

	public static void main(String[] args) {
		SpringApplication.run(BondsApplication.class, args);

		System.out.println("\n----------------------------------------------------------");
		System.out.println("Swagger UI:         http://localhost:8080/swagger-ui/index.html");
		System.out.println("Swagger UI:         http://localhost:8080/v3/api-docs");
		System.out.println("Swagger UI:         http://localhost:8080/swagger-ui.html");
		System.out.println("----------------------------------------------------------\n");
	}
}
