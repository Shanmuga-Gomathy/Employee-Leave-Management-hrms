package com.example.hrms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/*
 This class is used to create and configure users for authentication.

 In this project:
 - We are creating one manager user.
 - The user details are stored in memory (not in database).
 - Password is encrypted using BCrypt.
*/

@Configuration
public class UserConfig {

    /*
     This method creates a password encoder.
     BCrypt is used to encrypt passwords securely.
    */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     This method creates an in-memory user.
     Example:
     Username  : manager
     Password  : manager123 (encrypted)
     Role      : MANAGER
    */
    @Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {

        // Manager User
        UserDetails manager = User.withUsername("manager")
                .password(passwordEncoder.encode("manager123"))
                .roles("MANAGER")
                .build();

        // Employee User
        UserDetails employee = User.withUsername("employee")
                .password(passwordEncoder.encode("employee123"))
                .roles("EMPLOYEE")
                .build();

        return new InMemoryUserDetailsManager(manager, employee);
    }
}