package com.jobapp.user.documentation;

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
                .andExpect(jsonPath("$.info.title").value("Job Application Platform - User Service API"))
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
    public void testApiDocsContainsUserEndpoints() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/candidates']").exists())
                .andExpect(jsonPath("$.paths['/api/employers']").exists())
                .andExpect(jsonPath("$.paths['/api/files/upload/resume']").exists())
                .andExpect(jsonPath("$.paths['/api/files/upload/logo']").exists())
                .andExpect(jsonPath("$.paths['/api/admin/users']").exists());
    }

    @Test
    public void testApiDocsContainsSecuritySchemes() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.securitySchemes['Bearer Authentication']").exists())
                .andExpect(jsonPath("$.components.securitySchemes['Bearer Authentication'].type").value("http"))
                .andExpect(jsonPath("$.components.securitySchemes['Bearer Authentication'].scheme").value("bearer"));
    }

    @Test
    public void testApiDocsContainsFileUploadEndpoints() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/files/upload/resume'].post").exists())
                .andExpect(jsonPath("$.paths['/api/files/upload/logo'].post").exists())
                .andExpect(jsonPath("$.paths['/api/files/upload/resume'].post.requestBody.content['multipart/form-data']").exists());
    }

    @Test
    public void testApiDocsContainsGroupConfigurations() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tags").exists());
    }
}