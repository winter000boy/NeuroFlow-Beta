package com.jobapp.notification.documentation;

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
                .andExpect(jsonPath("$.info.title").value("Job Application Platform - Notification Service API"))
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
    public void testApiDocsContainsNotificationEndpoints() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/notifications/email/send']").exists())
                .andExpect(jsonPath("$.paths['/api/notifications/email/send-async']").exists())
                .andExpect(jsonPath("$.paths['/api/notifications/email/schedule']").exists())
                .andExpect(jsonPath("$.paths['/api/notifications/preferences']").exists())
                .andExpect(jsonPath("$.paths['/api/notifications/queue']").exists());
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
    public void testApiDocsContainsEmailTemplateInfo() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/notifications/email/send'].post.requestBody.content['application/json'].schema").exists())
                .andExpect(jsonPath("$.paths['/api/notifications/email/send'].post.responses['200'].content['application/json'].examples").exists());
    }
}