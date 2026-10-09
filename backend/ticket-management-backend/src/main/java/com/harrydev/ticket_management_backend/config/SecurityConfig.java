package com.harrydev.ticket_management_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Tắt CSRF vì đây là REST API
                .authorizeHttpRequests(auth -> auth
                        // Các endpoint public không cần token
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/api/users").permitAll()

                        // Tất cả các request còn lại đều cần phải có token hợp lệ
                        .anyRequest().authenticated())
                // Kích hoạt OAuth2 Resource Server dùng JWT
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }
}
