package com.jobapp.job.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration for the job service
 * Requirements: 3.2, 3.3
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers("/api/jobs/search/**").permitAll()
                .requestMatchers("/api/jobs/featured").permitAll()
                .requestMatchers("/api/jobs/recent").permitAll()
                .requestMatchers("/api/jobs/popular").permitAll()
                .requestMatchers("/api/jobs/public/**").permitAll()
                .requestMatchers("/api/jobs/{id}").permitAll() // Public job details
                .requestMatchers("/api/jobs/{id}/details").permitAll() // Public job details with company
                .requestMatchers("/api/jobs/company/{employerId}").permitAll() // Public company jobs
                .requestMatchers("/api/jobs/company/{employerId}/profile").permitAll() // Public company profile
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // Protected endpoints
                .requestMatchers("/api/jobs/employer/**").hasRole("EMPLOYER")
                .requestMatchers("/api/jobs").hasRole("EMPLOYER")
                .requestMatchers("/api/jobs/{id}/activate").hasRole("EMPLOYER")
                .requestMatchers("/api/jobs/{id}/deactivate").hasRole("EMPLOYER")
                .requestMatchers("/api/jobs/expired").hasRole("ADMIN")
                .anyRequest().authenticated()
            );
        
        // Add JWT authentication filter here when implemented
        // http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}