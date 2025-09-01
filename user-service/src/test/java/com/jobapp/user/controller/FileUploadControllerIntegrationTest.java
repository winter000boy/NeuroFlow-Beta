package com.jobapp.user.controller;

import com.jobapp.user.model.Candidate;
import com.jobapp.user.model.Employer;
import com.jobapp.user.repository.CandidateRepository;
import com.jobapp.user.repository.EmployerRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.file.Path;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for FileUploadController
 * Requirements: 1.4, 3.4, 9.4
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@TestPropertySource(locations = "classpath:application-test.yml")
public class FileUploadControllerIntegrationTest {
    
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    private CandidateRepository candidateRepository;
    
    @Autowired
    private EmployerRepository employerRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private MockMvc mockMvc;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        candidateRepository.deleteAll();
        employerRepository.deleteAll();
        
        // Set temp directory for file uploads in tests
        System.setProperty("app.file.upload.dir", tempDir.toString());
    }
    
    @Test
    void uploadResume_ValidPdfFile_ReturnsSuccess() throws Exception {
        // Given
        Candidate candidate = new Candidate(
            "john.doe@example.com",
            passwordEncoder.encode("password123"),
            "John Doe",
            "+1234567890",
            "Computer Science",
            2022
        );
        Candidate savedCandidate = candidateRepository.save(candidate);
        
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "resume.pdf",
            "application/pdf",
            "PDF content for testing".getBytes()
        );
        
        // When & Then
        mockMvc.perform(multipart("/api/files/upload/resume/{candidateId}", savedCandidate.getId())
                .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName", containsString("resume_" + savedCandidate.getId())))
                .andExpect(jsonPath("$.fileName", endsWith(".pdf")))
                .andExpect(jsonPath("$.fileUrl", containsString("/files/resumes/")))
                .andExpect(jsonPath("$.fileType", is("application/pdf")))
                .andExpect(jsonPath("$.fileSize", is(24))) // "PDF content for testing".length()
                .andExpect(jsonPath("$.message", is("Resume uploaded successfully")));
    }
    
    @Test
    void uploadResume_ValidDocFile_ReturnsSuccess() throws Exception {
        // Given
        Candidate candidate = new Candidate(
            "john.doe@example.com",
            passwordEncoder.encode("password123"),
            "John Doe",
            "+1234567890",
            "Computer Science",
            2022
        );
        Candidate savedCandidate = candidateRepository.save(candidate);
        
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "resume.doc",
            "application/msword",
            "DOC content for testing".getBytes()
        );
        
        // When & Then
        mockMvc.perform(multipart("/api/files/upload/resume/{candidateId}", savedCandidate.getId())
                .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName", endsWith(".doc")))
                .andExpect(jsonPath("$.fileType", is("application/msword")))
                .andExpect(jsonPath("$.message", is("Resume uploaded successfully")));
    }
    
    @Test
    void uploadResume_InvalidFileType_ReturnsBadRequest() throws Exception {
        // Given
        Candidate candidate = new Candidate(
            "john.doe@example.com",
            passwordEncoder.encode("password123"),
            "John Doe",
            "+1234567890",
            "Computer Science",
            2022
        );
        Candidate savedCandidate = candidateRepository.save(candidate);
        
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "resume.txt",
            "text/plain",
            "Text content".getBytes()
        );
        
        // When & Then
        mockMvc.perform(multipart("/api/files/upload/resume/{candidateId}", savedCandidate.getId())
                .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("INVALID_FILE_TYPE")))
                .andExpect(jsonPath("$.message", containsString("Invalid file type for resume")));
    }
    
    @Test
    void uploadResume_FileSizeExceeded_ReturnsPayloadTooLarge() throws Exception {
        // Given
        Candidate candidate = new Candidate(
            "john.doe@example.com",
            passwordEncoder.encode("password123"),
            "John Doe",
            "+1234567890",
            "Computer Science",
            2022
        );
        Candidate savedCandidate = candidateRepository.save(candidate);
        
        // Create a file larger than 5MB
        byte[] largeContent = new byte[6 * 1024 * 1024]; // 6MB
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "resume.pdf",
            "application/pdf",
            largeContent
        );
        
        // When & Then
        mockMvc.perform(multipart("/api/files/upload/resume/{candidateId}", savedCandidate.getId())
                .file(file))
                .andExpect(status().isPayloadTooLarge())
                .andExpect(jsonPath("$.code", is("FILE_SIZE_EXCEEDED")))
                .andExpect(jsonPath("$.message", containsString("File size exceeds maximum allowed size")));
    }
    
    @Test
    void uploadLogo_ValidJpegFile_ReturnsSuccess() throws Exception {
        // Given
        Employer employer = new Employer(
            "hr@techcorp.com",
            passwordEncoder.encode("password123"),
            "Tech Corp",
            "https://techcorp.com",
            "Leading technology company"
        );
        Employer savedEmployer = employerRepository.save(employer);
        
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "logo.jpg",
            "image/jpeg",
            "JPEG content for testing".getBytes()
        );
        
        // When & Then
        mockMvc.perform(multipart("/api/files/upload/logo/{employerId}", savedEmployer.getId())
                .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName", containsString("logo_" + savedEmployer.getId())))
                .andExpect(jsonPath("$.fileName", endsWith(".jpg")))
                .andExpect(jsonPath("$.fileUrl", containsString("/files/logos/")))
                .andExpect(jsonPath("$.fileType", is("image/jpeg")))
                .andExpect(jsonPath("$.message", is("Logo uploaded successfully")));
    }
    
    @Test
    void uploadLogo_ValidPngFile_ReturnsSuccess() throws Exception {
        // Given
        Employer employer = new Employer(
            "hr@techcorp.com",
            passwordEncoder.encode("password123"),
            "Tech Corp",
            "https://techcorp.com",
            "Leading technology company"
        );
        Employer savedEmployer = employerRepository.save(employer);
        
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "logo.png",
            "image/png",
            "PNG content for testing".getBytes()
        );
        
        // When & Then
        mockMvc.perform(multipart("/api/files/upload/logo/{employerId}", savedEmployer.getId())
                .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName", endsWith(".png")))
                .andExpect(jsonPath("$.fileType", is("image/png")))
                .andExpect(jsonPath("$.message", is("Logo uploaded successfully")));
    }
    
    @Test
    void uploadLogo_InvalidFileType_ReturnsBadRequest() throws Exception {
        // Given
        Employer employer = new Employer(
            "hr@techcorp.com",
            passwordEncoder.encode("password123"),
            "Tech Corp",
            "https://techcorp.com",
            "Leading technology company"
        );
        Employer savedEmployer = employerRepository.save(employer);
        
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "logo.pdf",
            "application/pdf",
            "PDF content".getBytes()
        );
        
        // When & Then
        mockMvc.perform(multipart("/api/files/upload/logo/{employerId}", savedEmployer.getId())
                .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("INVALID_FILE_TYPE")))
                .andExpect(jsonPath("$.message", containsString("Invalid file type for logo")));
    }
    
    @Test
    void uploadLogo_FileSizeExceeded_ReturnsPayloadTooLarge() throws Exception {
        // Given
        Employer employer = new Employer(
            "hr@techcorp.com",
            passwordEncoder.encode("password123"),
            "Tech Corp",
            "https://techcorp.com",
            "Leading technology company"
        );
        Employer savedEmployer = employerRepository.save(employer);
        
        // Create a file larger than 2MB
        byte[] largeContent = new byte[3 * 1024 * 1024]; // 3MB
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "logo.jpg",
            "image/jpeg",
            largeContent
        );
        
        // When & Then
        mockMvc.perform(multipart("/api/files/upload/logo/{employerId}", savedEmployer.getId())
                .file(file))
                .andExpect(status().isPayloadTooLarge())
                .andExpect(jsonPath("$.code", is("FILE_SIZE_EXCEEDED")))
                .andExpect(jsonPath("$.message", containsString("File size exceeds maximum allowed size")));
    }
    
    @Test
    void validateFile_ValidResumeFile_ReturnsSuccess() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "resume.pdf",
            "application/pdf",
            "PDF content".getBytes()
        );
        
        // When & Then
        mockMvc.perform(multipart("/api/files/validate")
                .file(file)
                .param("category", "resume"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("File is valid for upload")));
    }
    
    @Test
    void validateFile_ValidLogoFile_ReturnsSuccess() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "logo.jpg",
            "image/jpeg",
            "JPEG content".getBytes()
        );
        
        // When & Then
        mockMvc.perform(multipart("/api/files/validate")
                .file(file)
                .param("category", "logo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("File is valid for upload")));
    }
    
    @Test
    void validateFile_InvalidCategory_ReturnsBadRequest() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "file.txt",
            "text/plain",
            "Text content".getBytes()
        );
        
        // When & Then
        mockMvc.perform(multipart("/api/files/validate")
                .file(file)
                .param("category", "invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("INVALID_FILE_TYPE")))
                .andExpect(jsonPath("$.message", containsString("Invalid file category")));
    }
    
    @Test
    void getUploadLimits_ReturnsLimitsAndAllowedTypes() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/files/limits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resume.allowedTypes", hasSize(3)))
                .andExpect(jsonPath("$.resume.allowedTypes", hasItem("application/pdf")))
                .andExpect(jsonPath("$.resume.allowedTypes", hasItem("application/msword")))
                .andExpect(jsonPath("$.resume.allowedExtensions", hasItem("pdf")))
                .andExpect(jsonPath("$.resume.allowedExtensions", hasItem("doc")))
                .andExpect(jsonPath("$.resume.allowedExtensions", hasItem("docx")))
                .andExpect(jsonPath("$.resume.maxSizeBytes", is(5242880))) // 5MB
                .andExpect(jsonPath("$.resume.maxSizeDisplay", is("5MB")))
                .andExpect(jsonPath("$.logo.allowedTypes", hasSize(5)))
                .andExpect(jsonPath("$.logo.allowedTypes", hasItem("image/jpeg")))
                .andExpect(jsonPath("$.logo.allowedTypes", hasItem("image/png")))
                .andExpect(jsonPath("$.logo.allowedExtensions", hasItem("jpg")))
                .andExpect(jsonPath("$.logo.allowedExtensions", hasItem("png")))
                .andExpect(jsonPath("$.logo.maxSizeBytes", is(2097152))) // 2MB
                .andExpect(jsonPath("$.logo.maxSizeDisplay", is("2MB")));
    }
}