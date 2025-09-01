package com.jobapp.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobapp.application.dto.CreateApplicationRequest;
import com.jobapp.application.dto.UpdateApplicationStatusRequest;
import com.jobapp.application.model.Application;
import com.jobapp.application.model.ApplicationStatus;
import com.jobapp.application.repository.ApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ApplicationController
 * Requirements: 2.3, 2.4, 2.5, 4.1, 4.2, 4.3
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
class ApplicationControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private ApplicationRepository applicationRepository;
    
    @MockBean
    private RestTemplate restTemplate;
    
    private Application testApplication;
    private CreateApplicationRequest createRequest;
    private UpdateApplicationStatusRequest updateRequest;
    
    @BeforeEach
    void setUp() {
        testApplication = new Application("candidate123", "job123", "employer123");
        testApplication.setId("app123");
        testApplication.setCoverLetter("Test cover letter");
        testApplication.setResumeUrl("http://example.com/resume.pdf");
        
        createRequest = new CreateApplicationRequest();
        createRequest.setJobId("job123");
        createRequest.setCoverLetter("Test cover letter");
        createRequest.setResumeUrl("http://example.com/resume.pdf");
        
        updateRequest = new UpdateApplicationStatusRequest();
        updateRequest.setStatus(ApplicationStatus.IN_REVIEW);
        updateRequest.setNotes("Application looks promising");
    }
    
    @Test
    @WithMockUser(username = "candidate123", roles = {"CANDIDATE"})
    void createApplication_ValidRequest_ReturnsCreated() throws Exception {
        // Mock external service calls
        when(restTemplate.getForObject(anyString(), eq(Object.class))).thenReturn(new Object());
        when(applicationRepository.existsByCandidateIdAndJobId(anyString(), anyString())).thenReturn(false);
        when(applicationRepository.save(any(Application.class))).thenReturn(testApplication);
        
        mockMvc.perform(post("/api/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("app123"))
                .andExpect(jsonPath("$.candidateId").value("candidate123"))
                .andExpect(jsonPath("$.jobId").value("job123"))
                .andExpect(jsonPath("$.status").value("APPLIED"))
                .andExpect(jsonPath("$.coverLetter").value("Test cover letter"));
    }
    
    @Test
    @WithMockUser(username = "candidate123", roles = {"CANDIDATE"})
    void createApplication_DuplicateApplication_ReturnsConflict() throws Exception {
        when(applicationRepository.existsByCandidateIdAndJobId(anyString(), anyString())).thenReturn(true);
        
        mockMvc.perform(post("/api/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_APPLICATION"));
    }
    
    @Test
    @WithMockUser(username = "candidate123", roles = {"CANDIDATE"})
    void createApplication_InvalidRequest_ReturnsBadRequest() throws Exception {
        createRequest.setJobId(null); // Invalid request
        
        mockMvc.perform(post("/api/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }
    
    @Test
    @WithMockUser(username = "candidate123", roles = {"CANDIDATE"})
    void getApplication_ValidId_ReturnsApplication() throws Exception {
        when(applicationRepository.findById("app123")).thenReturn(Optional.of(testApplication));
        
        mockMvc.perform(get("/api/applications/app123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("app123"))
                .andExpect(jsonPath("$.candidateId").value("candidate123"))
                .andExpect(jsonPath("$.jobId").value("job123"));
    }
    
    @Test
    @WithMockUser(username = "candidate123", roles = {"CANDIDATE"})
    void getApplication_NotFound_ReturnsNotFound() throws Exception {
        when(applicationRepository.findById("nonexistent")).thenReturn(Optional.empty());
        
        mockMvc.perform(get("/api/applications/nonexistent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }
    
    @Test
    @WithMockUser(username = "candidate123", roles = {"CANDIDATE"})
    void getCandidateApplications_ValidRequest_ReturnsPagedApplications() throws Exception {
        List<Application> applications = Arrays.asList(testApplication);
        Page<Application> page = new PageImpl<>(applications);
        
        when(applicationRepository.findByCandidateId(eq("candidate123"), any(Pageable.class)))
                .thenReturn(page);
        
        mockMvc.perform(get("/api/applications/candidate/my-applications")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value("app123"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }
    
    @Test
    @WithMockUser(username = "candidate123", roles = {"CANDIDATE"})
    void getCandidateApplications_WithStatusFilter_ReturnsFilteredApplications() throws Exception {
        List<Application> applications = Arrays.asList(testApplication);
        Page<Application> page = new PageImpl<>(applications);
        
        when(applicationRepository.findByCandidateIdAndStatus(eq("candidate123"), 
                eq(ApplicationStatus.APPLIED), any(Pageable.class)))
                .thenReturn(page);
        
        mockMvc.perform(get("/api/applications/candidate/my-applications")
                .param("page", "0")
                .param("size", "10")
                .param("status", "APPLIED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].status").value("APPLIED"));
    }
    
    @Test
    @WithMockUser(username = "employer123", roles = {"EMPLOYER"})
    void getJobApplications_ValidRequest_ReturnsPagedApplications() throws Exception {
        List<Application> applications = Arrays.asList(testApplication);
        Page<Application> page = new PageImpl<>(applications);
        
        // Mock job ownership validation
        when(restTemplate.getForObject(anyString(), eq(Object.class))).thenReturn(new Object());
        when(applicationRepository.findByJobId(eq("job123"), any(Pageable.class)))
                .thenReturn(page);
        
        mockMvc.perform(get("/api/applications/job/job123")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].jobId").value("job123"));
    }
    
    @Test
    @WithMockUser(username = "employer123", roles = {"EMPLOYER"})
    void getEmployerApplications_ValidRequest_ReturnsPagedApplications() throws Exception {
        List<Application> applications = Arrays.asList(testApplication);
        Page<Application> page = new PageImpl<>(applications);
        
        when(applicationRepository.findByEmployerId(eq("employer123"), any(Pageable.class)))
                .thenReturn(page);
        
        mockMvc.perform(get("/api/applications/employer/my-applications")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].employerId").value("employer123"));
    }
    
    @Test
    @WithMockUser(username = "employer123", roles = {"EMPLOYER"})
    void updateApplicationStatus_ValidRequest_ReturnsUpdatedApplication() throws Exception {
        Application updatedApplication = new Application("candidate123", "job123", "employer123");
        updatedApplication.setId("app123");
        updatedApplication.setStatus(ApplicationStatus.IN_REVIEW);
        updatedApplication.setNotes("Application looks promising");
        
        when(applicationRepository.findById("app123")).thenReturn(Optional.of(testApplication));
        when(applicationRepository.save(any(Application.class))).thenReturn(updatedApplication);
        
        mockMvc.perform(put("/api/applications/app123/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("app123"))
                .andExpect(jsonPath("$.status").value("IN_REVIEW"))
                .andExpect(jsonPath("$.notes").value("Application looks promising"));
    }
    
    @Test
    @WithMockUser(username = "employer123", roles = {"EMPLOYER"})
    void updateApplicationStatus_InvalidTransition_ReturnsBadRequest() throws Exception {
        testApplication.setStatus(ApplicationStatus.HIRED); // Final status
        when(applicationRepository.findById("app123")).thenReturn(Optional.of(testApplication));
        
        mockMvc.perform(put("/api/applications/app123/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_STATUS_TRANSITION"));
    }
    
    @Test
    @WithMockUser(username = "candidate123", roles = {"CANDIDATE"})
    void withdrawApplication_ValidRequest_ReturnsUpdatedApplication() throws Exception {
        Application withdrawnApplication = new Application("candidate123", "job123", "employer123");
        withdrawnApplication.setId("app123");
        withdrawnApplication.setStatus(ApplicationStatus.WITHDRAWN);
        
        when(applicationRepository.findById("app123")).thenReturn(Optional.of(testApplication));
        when(applicationRepository.save(any(Application.class))).thenReturn(withdrawnApplication);
        
        mockMvc.perform(put("/api/applications/app123/withdraw"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("app123"))
                .andExpect(jsonPath("$.status").value("WITHDRAWN"));
    }
    
    @Test
    @WithMockUser(username = "candidate123", roles = {"CANDIDATE"})
    void withdrawApplication_FinalStatus_ReturnsBadRequest() throws Exception {
        testApplication.setStatus(ApplicationStatus.HIRED); // Final status
        when(applicationRepository.findById("app123")).thenReturn(Optional.of(testApplication));
        
        mockMvc.perform(put("/api/applications/app123/withdraw"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_STATUS_TRANSITION"));
    }
    
    @Test
    @WithMockUser(username = "candidate123", roles = {"CANDIDATE"})
    void getCandidateStatistics_ValidRequest_ReturnsStatistics() throws Exception {
        when(applicationRepository.countByCandidateId("candidate123")).thenReturn(5L);
        when(applicationRepository.countByCandidateIdAndStatus("candidate123", ApplicationStatus.APPLIED)).thenReturn(2L);
        when(applicationRepository.countByCandidateIdAndStatus("candidate123", ApplicationStatus.IN_REVIEW)).thenReturn(1L);
        when(applicationRepository.countByCandidateIdAndStatus("candidate123", ApplicationStatus.HIRED)).thenReturn(1L);
        when(applicationRepository.countByCandidateIdAndStatus("candidate123", ApplicationStatus.REJECTED)).thenReturn(1L);
        
        mockMvc.perform(get("/api/applications/candidate/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalApplications").value(5))
                .andExpect(jsonPath("$.appliedCount").value(2))
                .andExpect(jsonPath("$.inReviewCount").value(1))
                .andExpect(jsonPath("$.hiredCount").value(1))
                .andExpect(jsonPath("$.rejectedCount").value(1));
    }
    
    @Test
    @WithMockUser(username = "employer123", roles = {"EMPLOYER"})
    void getEmployerStatistics_ValidRequest_ReturnsStatistics() throws Exception {
        when(applicationRepository.countByEmployerId("employer123")).thenReturn(10L);
        when(applicationRepository.countByEmployerIdAndStatus("employer123", ApplicationStatus.APPLIED)).thenReturn(4L);
        when(applicationRepository.countByEmployerIdAndStatus("employer123", ApplicationStatus.IN_REVIEW)).thenReturn(3L);
        when(applicationRepository.countByEmployerIdAndStatus("employer123", ApplicationStatus.HIRED)).thenReturn(2L);
        when(applicationRepository.countByEmployerIdAndStatus("employer123", ApplicationStatus.REJECTED)).thenReturn(1L);
        
        mockMvc.perform(get("/api/applications/employer/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalApplications").value(10))
                .andExpect(jsonPath("$.appliedCount").value(4))
                .andExpect(jsonPath("$.inReviewCount").value(3))
                .andExpect(jsonPath("$.hiredCount").value(2))
                .andExpect(jsonPath("$.rejectedCount").value(1));
    }
    
    @Test
    void createApplication_Unauthorized_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @WithMockUser(username = "employer123", roles = {"EMPLOYER"})
    void createApplication_WrongRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(post("/api/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());
    }
}