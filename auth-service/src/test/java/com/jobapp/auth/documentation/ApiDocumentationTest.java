package com.jobapp.auth.documentation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

/**
 * Tests to ensure API documentation endpoints are working correctly
 * Requirements: 8.5
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.yml")
public class ApiDocumentationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Test
    public void testOpenApiDocsEndpoint() {
        String response = restTemplate.getForObject("http://localhost:" + port + "/api-docs", String.class);
        
        // Verify the response contains OpenAPI specification
        assert response != null;
        assert response.contains("\"openapi\"");
        assert response.contains("\"info\"");
        assert response.contains("Job Application Platform - Auth Service API");
        assert response.contains("\"version\":\"1.0.0\"");
    }

    @Test
    public void testSwaggerUIEndpoint() {
        var response = restTemplate.getForEntity("http://localhost:" + port + "/swagger-ui.html", String.class);
        
        // Should redirect to swagger-ui/index.html or return 200 with content
        assert response.getStatusCode().is3xxRedirection() || response.getStatusCode().is2xxSuccessful();
    }

    @Test
    public void testSwaggerUIIndexEndpoint() {
        String response = restTemplate.getForObject("http://localhost:" + port + "/swagger-ui/index.html", String.class);
        
        // Verify it returns HTML content
        assert response != null;
        assert response.contains("<html") || response.contains("<!DOCTYPE html");
    }

    @Test
    public void testApiDocsContainsAuthEndpoints() {
        String response = restTemplate.getForObject("http://localhost:" + port + "/api-docs", String.class);
        
        // Verify auth endpoints are documented
        assert response != null;
        assert response.contains("/api/auth/login");
        assert response.contains("/api/auth/register");
        assert response.contains("/api/auth/refresh");
        assert response.contains("/api/auth/check-email");
    }

    @Test
    public void testApiDocsContainsSecuritySchemes() {
        String response = restTemplate.getForObject("http://localhost:" + port + "/api-docs", String.class);
        
        // Verify security schemes are documented
        assert response != null;
        assert response.contains("Bearer Authentication");
        assert response.contains("\"type\":\"http\"");
        assert response.contains("\"scheme\":\"bearer\"");
    }

    @Test
    public void testApiDocsContainsServerInformation() {
        String response = restTemplate.getForObject("http://localhost:" + port + "/api-docs", String.class);
        
        // Verify server information is documented
        assert response != null;
        assert response.contains("\"servers\"");
        assert response.contains("\"url\"");
        assert response.contains("\"description\"");
    }
}