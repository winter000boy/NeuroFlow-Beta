package com.jobapp.user.service;

import com.jobapp.user.dto.FileUploadResponse;
import com.jobapp.user.exception.FileUploadException;
import com.jobapp.user.exception.InvalidFileTypeException;
import com.jobapp.user.exception.FileSizeExceededException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FileUploadService
 * Requirements: 1.4, 3.4, 9.4
 */
@ExtendWith(MockitoExtension.class)
class FileUploadServiceTest {
    
    @InjectMocks
    private FileUploadService fileUploadService;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(fileUploadService, "uploadDir", tempDir.toString());
        ReflectionTestUtils.setField(fileUploadService, "baseUrl", "http://localhost:8082");
    }
    
    @Test
    void uploadResume_ValidPdfFile_ReturnsSuccess() {
        // Given
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "resume.pdf",
            "application/pdf",
            "PDF content".getBytes()
        );
        String candidateId = "candidate123";
        
        // When
        FileUploadResponse response = fileUploadService.uploadResume(file, candidateId);
        
        // Then
        assertNotNull(response);
        assertTrue(response.getFileName().contains("resume_candidate123"));
        assertTrue(response.getFileName().endsWith(".pdf"));
        assertTrue(response.getFileUrl().contains("/files/resumes/"));
        assertEquals("application/pdf", response.getFileType());
        assertEquals(11, response.getFileSize()); // "PDF content".length()
        assertEquals("Resume uploaded successfully", response.getMessage());
    }
    
    @Test
    void uploadResume_ValidDocFile_ReturnsSuccess() {
        // Given
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "resume.doc",
            "application/msword",
            "DOC content".getBytes()
        );
        String candidateId = "candidate123";
        
        // When
        FileUploadResponse response = fileUploadService.uploadResume(file, candidateId);
        
        // Then
        assertNotNull(response);
        assertTrue(response.getFileName().endsWith(".doc"));
        assertEquals("application/msword", response.getFileType());
        assertEquals("Resume uploaded successfully", response.getMessage());
    }
    
    @Test
    void uploadResume_ValidDocxFile_ReturnsSuccess() {
        // Given
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "resume.docx",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "DOCX content".getBytes()
        );
        String candidateId = "candidate123";
        
        // When
        FileUploadResponse response = fileUploadService.uploadResume(file, candidateId);
        
        // Then
        assertNotNull(response);
        assertTrue(response.getFileName().endsWith(".docx"));
        assertEquals("application/vnd.openxmlformats-officedocument.wordprocessingml.document", response.getFileType());
        assertEquals("Resume uploaded successfully", response.getMessage());
    }
    
    @Test
    void uploadResume_InvalidFileType_ThrowsException() {
        // Given
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "resume.txt",
            "text/plain",
            "Text content".getBytes()
        );
        String candidateId = "candidate123";
        
        // When & Then
        InvalidFileTypeException exception = assertThrows(
            InvalidFileTypeException.class,
            () -> fileUploadService.uploadResume(file, candidateId)
        );
        
        assertTrue(exception.getMessage().contains("Invalid file type for resume"));
    }
    
    @Test
    void uploadResume_FileSizeExceeded_ThrowsException() {
        // Given
        byte[] largeContent = new byte[6 * 1024 * 1024]; // 6MB (exceeds 5MB limit)
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "resume.pdf",
            "application/pdf",
            largeContent
        );
        String candidateId = "candidate123";
        
        // When & Then
        FileSizeExceededException exception = assertThrows(
            FileSizeExceededException.class,
            () -> fileUploadService.uploadResume(file, candidateId)
        );
        
        assertTrue(exception.getMessage().contains("File size exceeds maximum allowed size"));
    }
    
    @Test
    void uploadResume_EmptyFile_ThrowsException() {
        // Given
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "resume.pdf",
            "application/pdf",
            new byte[0]
        );
        String candidateId = "candidate123";
        
        // When & Then
        FileUploadException exception = assertThrows(
            FileUploadException.class,
            () -> fileUploadService.uploadResume(file, candidateId)
        );
        
        assertEquals("File is empty or null", exception.getMessage());
    }
    
    @Test
    void uploadLogo_ValidJpegFile_ReturnsSuccess() {
        // Given
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "logo.jpg",
            "image/jpeg",
            "JPEG content".getBytes()
        );
        String employerId = "employer123";
        
        // When
        FileUploadResponse response = fileUploadService.uploadLogo(file, employerId);
        
        // Then
        assertNotNull(response);
        assertTrue(response.getFileName().contains("logo_employer123"));
        assertTrue(response.getFileName().endsWith(".jpg"));
        assertTrue(response.getFileUrl().contains("/files/logos/"));
        assertEquals("image/jpeg", response.getFileType());
        assertEquals("Logo uploaded successfully", response.getMessage());
    }
    
    @Test
    void uploadLogo_ValidPngFile_ReturnsSuccess() {
        // Given
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "logo.png",
            "image/png",
            "PNG content".getBytes()
        );
        String employerId = "employer123";
        
        // When
        FileUploadResponse response = fileUploadService.uploadLogo(file, employerId);
        
        // Then
        assertNotNull(response);
        assertTrue(response.getFileName().endsWith(".png"));
        assertEquals("image/png", response.getFileType());
    }
    
    @Test
    void uploadLogo_InvalidFileType_ThrowsException() {
        // Given
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "logo.pdf",
            "application/pdf",
            "PDF content".getBytes()
        );
        String employerId = "employer123";
        
        // When & Then
        InvalidFileTypeException exception = assertThrows(
            InvalidFileTypeException.class,
            () -> fileUploadService.uploadLogo(file, employerId)
        );
        
        assertTrue(exception.getMessage().contains("Invalid file type for logo"));
    }
    
    @Test
    void uploadLogo_FileSizeExceeded_ThrowsException() {
        // Given
        byte[] largeContent = new byte[3 * 1024 * 1024]; // 3MB (exceeds 2MB limit)
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "logo.jpg",
            "image/jpeg",
            largeContent
        );
        String employerId = "employer123";
        
        // When & Then
        FileSizeExceededException exception = assertThrows(
            FileSizeExceededException.class,
            () -> fileUploadService.uploadLogo(file, employerId)
        );
        
        assertTrue(exception.getMessage().contains("File size exceeds maximum allowed size"));
    }
    
    @Test
    void validateFileForUpload_ValidResumeFile_NoException() {
        // Given
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "resume.pdf",
            "application/pdf",
            "PDF content".getBytes()
        );
        
        // When & Then
        assertDoesNotThrow(() -> fileUploadService.validateFileForUpload(file, "resume"));
    }
    
    @Test
    void validateFileForUpload_ValidLogoFile_NoException() {
        // Given
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "logo.jpg",
            "image/jpeg",
            "JPEG content".getBytes()
        );
        
        // When & Then
        assertDoesNotThrow(() -> fileUploadService.validateFileForUpload(file, "logo"));
    }
    
    @Test
    void validateFileForUpload_InvalidCategory_ThrowsException() {
        // Given
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "file.txt",
            "text/plain",
            "Text content".getBytes()
        );
        
        // When & Then
        InvalidFileTypeException exception = assertThrows(
            InvalidFileTypeException.class,
            () -> fileUploadService.validateFileForUpload(file, "invalid")
        );
        
        assertTrue(exception.getMessage().contains("Invalid file category"));
    }
    
    @Test
    void uploadResume_SuspiciousFilename_ThrowsException() {
        // Given
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "resume.exe",
            "application/pdf",
            "PDF content".getBytes()
        );
        String candidateId = "candidate123";
        
        // When & Then
        InvalidFileTypeException exception = assertThrows(
            InvalidFileTypeException.class,
            () -> fileUploadService.uploadResume(file, candidateId)
        );
        
        assertTrue(exception.getMessage().contains("Invalid file extension 'exe' for resume"));
    }
    
    @Test
    void uploadResume_PathTraversalAttempt_ThrowsException() {
        // Given
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "../resume.pdf",
            "application/pdf",
            "PDF content".getBytes()
        );
        String candidateId = "candidate123";
        
        // When & Then
        FileUploadException exception = assertThrows(
            FileUploadException.class,
            () -> fileUploadService.uploadResume(file, candidateId)
        );
        
        assertTrue(exception.getMessage().contains("File name contains suspicious content"));
    }
    
    @Test
    void uploadResume_SuspiciousContentInValidExtension_ThrowsException() {
        // Given - file with valid extension but suspicious content in name
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "resume.exe.pdf",
            "application/pdf",
            "PDF content".getBytes()
        );
        String candidateId = "candidate123";
        
        // When & Then
        FileUploadException exception = assertThrows(
            FileUploadException.class,
            () -> fileUploadService.uploadResume(file, candidateId)
        );
        
        assertTrue(exception.getMessage().contains("File name contains suspicious content"));
    }
    
    @Test
    void deleteFile_ExistingFile_ReturnsTrue() throws IOException {
        // Given
        Path testFile = tempDir.resolve("resumes").resolve("test-file.pdf");
        Files.createDirectories(testFile.getParent());
        Files.createFile(testFile);
        
        String fileUrl = "http://localhost:8082/files/resumes/test-file.pdf";
        
        // When
        boolean result = fileUploadService.deleteFile(fileUrl);
        
        // Then
        assertTrue(result);
        assertFalse(Files.exists(testFile));
    }
    
    @Test
    void deleteFile_NonExistentFile_ReturnsFalse() {
        // Given
        String fileUrl = "http://localhost:8082/files/resumes/nonexistent-file.pdf";
        
        // When
        boolean result = fileUploadService.deleteFile(fileUrl);
        
        // Then
        assertFalse(result);
    }
    
    @Test
    void uploadResume_NullFile_ThrowsException() {
        // Given
        String candidateId = "candidate123";
        
        // When & Then
        FileUploadException exception = assertThrows(
            FileUploadException.class,
            () -> fileUploadService.uploadResume(null, candidateId)
        );
        
        assertEquals("File is empty or null", exception.getMessage());
    }
    
    @Test
    void uploadResume_NoFileExtension_ThrowsException() {
        // Given
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "resume",
            "application/pdf",
            "PDF content".getBytes()
        );
        String candidateId = "candidate123";
        
        // When & Then
        InvalidFileTypeException exception = assertThrows(
            InvalidFileTypeException.class,
            () -> fileUploadService.uploadResume(file, candidateId)
        );
        
        assertTrue(exception.getMessage().contains("Invalid file extension"));
    }
}