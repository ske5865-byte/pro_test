package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                    // Permit all users to access the home, signup, and login pages
                    .requestMatchers("/", "/signup", "/login", "/h2-console/**").permitAll()
                    // Permit read access to posts for everyone
                    .requestMatchers(HttpMethod.GET, "/posts", "/posts/{id}").permitAll()
                    // All other requests (including creating, updating, deleting posts) require authentication
                    .anyRequest().authenticated()
            )
            .formLogin(formLogin ->
                formLogin
                    // Specify the custom login page
                    .loginPage("/login")
                    // Redirect to the home page on successful login
                    .defaultSuccessUrl("/", true)
                    .permitAll()
            )
            .logout(logout ->
                logout
                    // Redirect to home on logout
                    .logoutSuccessUrl("/")
            )
            .csrf(csrf -> csrf.disable()) // Disabling CSRF for H2 console
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable())); // Disabling frame options for H2 console
        return http.build();
    }
}
