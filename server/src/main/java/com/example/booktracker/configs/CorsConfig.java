package com.example.booktracker.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        System.out.println("CorsConfig is being applied");      // debugging line
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")  // Restrict to your frontend domain
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)  // Allow credentials (cookies, Authorization headers, etc.)
                .maxAge(3600);
    }
}