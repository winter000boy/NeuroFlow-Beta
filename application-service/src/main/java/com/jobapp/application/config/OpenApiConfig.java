package com.jobapp.application.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI configuration for Application Service
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8084}")
    private String serverPort;

    @Bean
    public OpenAPI applicationServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Job Application Platform - Application Service API")
                        .description("Job application management microservice for the Job Application Platform. " +
                                "This service handles job applications, status tracking, and application analytics.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Job Application Platform Team")
                                .email("support@jobapp.com")
                                .url("https://jobapp.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Development server"),
                        new Server()
                                .url("https://api.jobapp.com/applications")
                                .description("Production server")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token for authentication")));
    }
}