package com.example.booktracker.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests) -> requests
            .anyRequest().permitAll() // Allow all requests without authentication
        ).csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        // The default strength is 10 if not specified
        int strength = 10;
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(strength);
        System.out.println("Configuring BCryptPasswordEncoder");
        System.out.println("Encoding strength: " + strength);

        // Let's test the encoder to make sure it's working
        String testPassword = "testPassword123";
        String encoded = encoder.encode(testPassword);
        System.out.println("Test encode result: " + encoded);
        System.out.println("Test password matches: " + encoder.matches(testPassword, encoded));

        return encoder;
    }

}
