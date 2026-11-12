package com.ramon.pacientes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.ramon.pacientes", "com.ramon.commons"})
public class PacientesApplication {

	public static void main(String[] args) {
		SpringApplication.run(PacientesApplication.class, args);
	}

}
