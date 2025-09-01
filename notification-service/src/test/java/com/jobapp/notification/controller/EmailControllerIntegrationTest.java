package com.jobapp.notification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobapp.notification.dto.EmailRequest;
import com.jobapp.notification.model.EmailNotification;
import com.jobapp.notification.repository.EmailNotificationRepository;
import com.jobapp.notification.repository.EmailTemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@TestPropertySource(properties = {
    "spring.data.mongodb.uri=mongodb://localhost:27017/jobapp_notifications_test",
    "spring.mail.host=localhost",
    "spring.mail.port=1025"
})
class EmailControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private EmailNotificationRepository notificationRepository;
    
    @Autowired
    private EmailTemplateRepository templateRepository;
    
    @MockBean
    private JavaMailSender mailSender; // Mock to avoid actual email sending
    
    @BeforeEach
    void setUp() {
        // Clean up test data
        notificationRepository.deleteAll();
    }
    
    @Test
    void sendEmail_ValidRequest_ReturnsOk() throws Exception {
        // Given
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setRecipientEmail("test@example.com");
        emailRequest.setRecipientName("Test User");
        emailRequest.setTemplateName("registration-confirmation");
        emailRequest.setType(EmailNotification.NotificationType.REGISTRATION_CONFIRMATION);
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", "Test User");
        variables.put("email", "test@example.com");
        variables.put("role", "CANDIDATE");
        emailRequest.setTemplateVariables(variables);
        
        // When & Then
        mockMvc.perform(post("/api/notifications/email/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipientEmail").value("test@example.com"))
                .andExpect(jsonPath("$.recipientName").value("Test User"))
                .andExpect(jsonPath("$.type").value("REGISTRATION_CONFIRMATION"));
    }
    
    @Test
    void sendEmail_InvalidEmail_ReturnsBadRequest() throws Exception {
        // Given
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setRecipientEmail("invalid-email");
        emailRequest.setTemplateName("registration-confirmation");
        emailRequest.setType(EmailNotification.NotificationType.REGISTRATION_CONFIRMATION);
        
        // When & Then
        mockMvc.perform(post("/api/notifications/email/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void sendEmail_MissingRequiredFields_ReturnsBadRequest() throws Exception {
        // Given
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setRecipientEmail("test@example.com");
        // Missing templateName and type
        
        // When & Then
        mockMvc.perform(post("/api/notifications/email/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void sendEmailAsync_ValidRequest_ReturnsAccepted() throws Exception {
        // Given
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setRecipientEmail("test@example.com");
        emailRequest.setRecipientName("Test User");
        emailRequest.setTemplateName("registration-confirmation");
        emailRequest.setType(EmailNotification.NotificationType.REGISTRATION_CONFIRMATION);
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", "Test User");
        emailRequest.setTemplateVariables(variables);
        
        // When & Then
        mockMvc.perform(post("/api/notifications/email/send-async")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailRequest)))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Email queued for sending"));
    }
    
    @Test
    void scheduleEmail_ValidRequest_ReturnsOk() throws Exception {
        // Given
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setRecipientEmail("test@example.com");
        emailRequest.setRecipientName("Test User");
        emailRequest.setTemplateName("registration-confirmation");
        emailRequest.setType(EmailNotification.NotificationType.REGISTRATION_CONFIRMATION);
        
        // When & Then
        mockMvc.perform(post("/api/notifications/email/schedule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipientEmail").value("test@example.com"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }
    
    @Test
    void getNotificationsByRecipient_ValidEmail_ReturnsNotifications() throws Exception {
        // Given
        String recipientEmail = "test@example.com";
        
        // Create test notification
        EmailNotification notification = new EmailNotification();
        notification.setRecipientEmail(recipientEmail);
        notification.setRecipientName("Test User");
        notification.setTemplateName("test-template");
        notification.setType(EmailNotification.NotificationType.REGISTRATION_CONFIRMATION);
        notification.setStatus(EmailNotification.NotificationStatus.SENT);
        notificationRepository.save(notification);
        
        // When & Then
        mockMvc.perform(get("/api/notifications/email/recipient/{email}", recipientEmail)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].recipientEmail").value(recipientEmail));
    }
    
    @Test
    void getNotificationsByStatus_ValidStatus_ReturnsNotifications() throws Exception {
        // Given
        EmailNotification.NotificationStatus status = EmailNotification.NotificationStatus.SENT;
        
        // Create test notification
        EmailNotification notification = new EmailNotification();
        notification.setRecipientEmail("test@example.com");
        notification.setTemplateName("test-template");
        notification.setType(EmailNotification.NotificationType.REGISTRATION_CONFIRMATION);
        notification.setStatus(status);
        notificationRepository.save(notification);
        
        // When & Then
        mockMvc.perform(get("/api/notifications/email/status/{status}", status)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].status").value(status.toString()));
    }
    
    @Test
    void getNotification_ExistingId_ReturnsNotification() throws Exception {
        // Given
        EmailNotification notification = new EmailNotification();
        notification.setRecipientEmail("test@example.com");
        notification.setTemplateName("test-template");
        notification.setType(EmailNotification.NotificationType.REGISTRATION_CONFIRMATION);
        notification = notificationRepository.save(notification);
        
        // When & Then
        mockMvc.perform(get("/api/notifications/email/{notificationId}", notification.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(notification.getId()))
                .andExpect(jsonPath("$.recipientEmail").value("test@example.com"));
    }
    
    @Test
    void getNotification_NonExistentId_ReturnsNotFound() throws Exception {
        // Given
        String nonExistentId = "nonexistent123";
        
        // When & Then
        mockMvc.perform(get("/api/notifications/email/{notificationId}", nonExistentId))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void cancelNotification_PendingNotification_ReturnsOk() throws Exception {
        // Given
        EmailNotification notification = new EmailNotification();
        notification.setRecipientEmail("test@example.com");
        notification.setTemplateName("test-template");
        notification.setType(EmailNotification.NotificationType.REGISTRATION_CONFIRMATION);
        notification.setStatus(EmailNotification.NotificationStatus.PENDING);
        notification = notificationRepository.save(notification);
        
        // When & Then
        mockMvc.perform(put("/api/notifications/email/{notificationId}/cancel", notification.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(notification.getId()))
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }
    
    @Test
    void cancelNotification_SentNotification_ReturnsBadRequest() throws Exception {
        // Given
        EmailNotification notification = new EmailNotification();
        notification.setRecipientEmail("test@example.com");
        notification.setTemplateName("test-template");
        notification.setType(EmailNotification.NotificationType.REGISTRATION_CONFIRMATION);
        notification.setStatus(EmailNotification.NotificationStatus.SENT);
        notification = notificationRepository.save(notification);
        
        // When & Then
        mockMvc.perform(put("/api/notifications/email/{notificationId}/cancel", notification.getId()))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void processScheduledEmails_ValidRequest_ReturnsOk() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/notifications/email/process-scheduled"))
                .andExpect(status().isOk())
                .andExpect(content().string("Scheduled emails processed"));
    }
    
    @Test
    void retryFailedEmails_ValidRequest_ReturnsOk() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/notifications/email/retry-failed"))
                .andExpect(status().isOk())
                .andExpect(content().string("Failed emails retry initiated"));
    }
}