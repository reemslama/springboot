package com.example.dection_anomalie.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/v1/**",
                    "/api/nutritionnists/**",   
                    "/api/nutritionists",
                    "/api/nutritionists/all",
                    "/api/nutritionists/delete/**",
                    "/api/nutritionists/view/**",
                    "/api/nutritionists/update/**",
                    "/api/nutritionists/login",
                    "/api/nutritionists/reset-password/**",
                    "/api/detection/upload"
                ).permitAll()
                .requestMatchers("/api/nutritionnistes/all", "/api/nutritionnistes/update/**", "/api/nutritionnistes/delete/**")
                    .hasRole("ADMIN")
                .requestMatchers("/api/nutritionnistes/view/**")
                    .hasRole("NUTRITIONISTE")
                .anyRequest().authenticated()
            )
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(request -> {
                var corsConfig = new CorsConfiguration();
                corsConfig.addAllowedOrigin("http://localhost:4200");
                corsConfig.addAllowedOrigin("http://192.168.1.101");
                corsConfig.addAllowedMethod("GET");
                corsConfig.addAllowedMethod("POST");
                corsConfig.addAllowedMethod("PUT");
                corsConfig.addAllowedMethod("DELETE");
                corsConfig.addAllowedHeader("Content-Type");
                corsConfig.addAllowedHeader("Authorization");
                return corsConfig;
            }));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
