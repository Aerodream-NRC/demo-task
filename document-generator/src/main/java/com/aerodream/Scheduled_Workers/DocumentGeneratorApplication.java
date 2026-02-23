package com.aerodream.Scheduled_Workers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
public class DocumentGeneratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocumentGeneratorApplication.class, args);
	}

}
