package com.example.portal.config;

import com.example.portal.service.MemberDetailsService;
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http, MemberDetailsService memberDetailsService) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Allow access to static resources, home, register, login, and validation APIs
                .requestMatchers("/", "/css/**", "/images/**", "/js/**", "/main.html").permitAll()
                .requestMatchers("/register", "/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/members/check-username", "/api/members/check-email").permitAll()
                // Require authentication for writing posts before allowing general board access
                .requestMatchers("/board/write").authenticated()
                // Allow read access to the board for everyone
                .requestMatchers(HttpMethod.GET, "/board/**").permitAll()
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .formLogin(formLogin -> formLogin
                .loginPage("/login") // Custom login page URL
                .usernameParameter("email") // Use email as the username field
                .passwordParameter("password")
                .defaultSuccessUrl("/", true) // Redirect to home on success
                .failureUrl("/login?error=true") // Redirect on failure
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .csrf(csrf -> csrf.disable()) // Keep CSRF disabled for simplicity
            .userDetailsService(memberDetailsService); // Set our custom user details service

        return http.build();
    }
}
