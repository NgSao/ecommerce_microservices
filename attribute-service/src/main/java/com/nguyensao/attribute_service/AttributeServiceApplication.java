package com.nguyensao.attribute_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(exclude = {
		org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@EnableFeignClients
public class AttributeServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(AttributeServiceApplication.class, args);
	}

}
