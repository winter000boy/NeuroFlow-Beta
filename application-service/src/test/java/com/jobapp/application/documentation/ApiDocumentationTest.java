package com.jobapp.application.documentation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests to ensure API documentation endpoints are working correctly
 */
@SpringBootTest
@AutoConfigureWebMvc
@TestPropertySource(locations = "classpath:application-test.yml")
public class ApiDocumentationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testOpenApiDocsEndpoint() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.openapi").exists())
                .andExpect(jsonPath("$.info").exists())
                .andExpect(jsonPath("$.info.title").value("Job Application Platform - Application Service API"))
                .andExpect(jsonPath("$.info.version").value("1.0.0"));
    }

    @Test
    public void testSwaggerUIEndpoint() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testSwaggerUIIndexEndpoint() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html"));
    }

    @Test
    public void testApiDocsContainsApplicationEndpoints() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/applications']").exists())
                .andExpect(jsonPath("$.paths['/api/applications/{applicationId}']").exists())
                .andExpect(jsonPath("$.paths['/api/applications/candidate/my-applications']").exists())
                .andExpect(jsonPath("$.paths['/api/applications/employer/my-applications']").exists())
                .andExpect(jsonPath("$.paths['/api/applications/{applicationId}/status']").exists());
    }

    @Test
    public void testApiDocsContainsSecuritySchemes() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.securitySchemes['Bearer Authentication']").exists())
                .andExpect(jsonPath("$.components.securitySchemes['Bearer Authentication'].type").value("http"))
                .andExpect(jsonPath("$.components.securitySchemes['Bearer Authentication'].scheme").value("bearer"))
                .andExpect(jsonPath("$.components.securitySchemes['Bearer Authentication'].bearerFormat").value("JWT"));
    }

    @Test
    public void testApiDocsContainsServerInformation() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.servers").isArray())
                .andExpect(jsonPath("$.servers[0].url").exists())
                .andExpect(jsonPath("$.servers[0].description").exists());
    }

    @Test
    public void testApiDocsContainsTagInformation() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tags").exists());
    }

    @Test
    public void testApiDocsContainsParameterDescriptions() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/applications/{applicationId}'].get.parameters[0].description").exists())
                .andExpect(jsonPath("$.paths['/api/applications/candidate/my-applications'].get.parameters[0].description").exists());
    }

    @Test
    public void testApiDocsContainsRequestBodySchemas() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/applications'].post.requestBody.content['application/json'].schema").exists())
                .andExpect(jsonPath("$.paths['/api/applications/{applicationId}/status'].put.requestBody.content['application/json'].schema").exists());
    }

    @Test
    public void testApiDocsContainsSecurityRequirements() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.security").exists());
    }
}