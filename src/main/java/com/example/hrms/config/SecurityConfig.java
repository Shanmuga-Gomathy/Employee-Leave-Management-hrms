package com.example.hrms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/*
 This class is used to configure security for the HRMS project.

 It defines:
 - Which APIs need login
 - Which APIs are public
 - The type of authentication used

 In this project:
 - Manager APIs require authentication
 - Swagger and H2 console are public
 - Other APIs are allowed without login
*/
@Configuration
public class SecurityConfig {

    /*
    This method sets the security rules
    and builds the SecurityFilterChain.
   */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        // Public URLs
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/api-docs/**",
                                "/h2-console/**"
                        ).permitAll()

                        // Employee APIs - accessible by EMPLOYEE and MANAGER
                        .requestMatchers("/employee-Api/v1/**")
                        .hasAnyRole("EMPLOYEE", "MANAGER")

                        // Leave APIs - only EMPLOYEE
                        .requestMatchers("/leave-request-api/v1/**")
                        .hasRole("EMPLOYEE")

                        // Manager APIs - only MANAGER
                        .requestMatchers("/manager-api/v1/**")
                        .hasRole("MANAGER")

                        // All other requests denied
                        .anyRequest().denyAll()
                )

                .httpBasic(Customizer.withDefaults())

                // Needed for H2 Console
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable())
                );

        return http.build();
    }
}
