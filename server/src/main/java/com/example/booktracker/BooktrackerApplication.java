package com.example.booktracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.wavefront.WavefrontProperties.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BooktrackerApplication {
	public static void main(String[] args) {
		System.out.println("Database URL: " + System.getenv("spring_db_url"));
		SpringApplication.run(Application.class, args);
	}
}
