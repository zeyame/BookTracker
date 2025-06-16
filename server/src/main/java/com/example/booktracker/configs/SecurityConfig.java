package com.example.booktracker.configs;

import com.example.booktracker.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthFilter) throws Exception {
        http.cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests((requests) -> requests
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // allow preflight requests
                .requestMatchers("/api/user/books/**").authenticated()      // only these endpoints require jwt authentication
                .anyRequest().permitAll()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

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
