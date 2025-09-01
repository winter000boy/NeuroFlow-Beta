package com.jobapp.job.documentation;

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
                .andExpect(jsonPath("$.info.title").value("Job Application Platform - Job Service API"))
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
    public void testApiDocsContainsJobEndpoints() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/jobs']").exists())
                .andExpect(jsonPath("$.paths['/api/jobs/search']").exists())
                .andExpect(jsonPath("$.paths['/api/jobs/{jobId}']").exists())
                .andExpect(jsonPath("$.paths['/api/jobs/employer']").exists())
                .andExpect(jsonPath("$.paths['/api/jobs/featured']").exists())
                .andExpect(jsonPath("$.paths['/api/jobs/recent']").exists())
                .andExpect(jsonPath("$.paths['/api/jobs/popular']").exists());
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
    public void testApiDocsContainsSearchParameters() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/jobs/search'].get.parameters").isArray())
                .andExpect(jsonPath("$.paths['/api/jobs/search'].get.parameters[?(@.name == 'search')].description").exists())
                .andExpect(jsonPath("$.paths['/api/jobs/search'].get.parameters[?(@.name == 'location')].description").exists())
                .andExpect(jsonPath("$.paths['/api/jobs/search'].get.parameters[?(@.name == 'jobType')].description").exists());
    }

    @Test
    public void testApiDocsContainsExampleResponses() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/jobs'].post.responses['201'].content['application/json'].examples").exists())
                .andExpect(jsonPath("$.paths['/api/jobs/search'].get.responses['200'].content['application/json'].examples").exists());
    }

    @Test
    public void testApiDocsContainsPublicEndpoints() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                // Public endpoints should not have security requirements
                .andExpect(jsonPath("$.paths['/api/jobs/search'].get.security").doesNotExist())
                .andExpect(jsonPath("$.paths['/api/jobs/{jobId}'].get.security").doesNotExist())
                .andExpect(jsonPath("$.paths['/api/jobs/featured'].get.security").doesNotExist());
    }
}