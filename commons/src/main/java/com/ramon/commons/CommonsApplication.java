package com.ramon.commons;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
//wiwin
@EnableFeignClients
public class CommonsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommonsApplication.class, args);
	}

}
