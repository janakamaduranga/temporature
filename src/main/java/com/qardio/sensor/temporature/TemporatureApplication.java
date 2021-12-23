package com.qardio.sensor.temporature;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Temperature application",
		version = "1.0",
		description = "qardio temperature APIs v1.0"))
public class TemporatureApplication {

	public static void main(String[] args) {
		SpringApplication.run(TemporatureApplication.class, args);
	}

}
