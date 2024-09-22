package com.example.booktracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BooktrackerApplication {
	public static void main(String[] args) {
		SpringApplication.run(BooktrackerApplication.class, args);
	}
}
