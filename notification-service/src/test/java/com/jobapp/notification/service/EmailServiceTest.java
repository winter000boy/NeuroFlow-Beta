package com.jobapp.notification.service;

import com.jobapp.notification.dto.EmailRequest;
import com.jobapp.notification.dto.EmailResponse;
import com.jobapp.notification.model.EmailNotification;
import com.jobapp.notification.model.EmailTemplate;
import com.jobapp.notification.repository.EmailNotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {
    
    @Mock
    private JavaMailSender mailSender;
    
    @Mock
    private EmailNotificationRepository notificationRepository;
    
    @Mock
    private EmailTemplateService templateService;
    
    @Mock
    private MimeMessage mimeMessage;
    
    @InjectMocks
    private EmailService emailService;
    
    private EmailRequest emailRequest;
    private EmailTemplate emailTemplate;
    private EmailNotification emailNotification;
    
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "fromEmail", "noreply@jobapp.com");
        
        // Setup test data
        emailRequest = new EmailRequest();
        emailRequest.setRecipientEmail("test@example.com");
        emailRequest.setRecipientName("Test User");
        emailRequest.setTemplateName("registration-confirmation");
        emailRequest.setType(EmailNotification.NotificationType.REGISTRATION_CONFIRMATION);
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", "Test User");
        variables.put("email", "test@example.com");
        emailRequest.setTemplateVariables(variables);
        
        emailTemplate = new EmailTemplate();
        emailTemplate.setName("registration-confirmation");
        emailTemplate.setSubject("Welcome to JobApp - ${name}");
        emailTemplate.setHtmlContent("<html><body>Welcome ${name}!</body></html>");
        emailTemplate.setTextContent("Welcome ${name}!");
        
        emailNotification = new EmailNotification();
        emailNotification.setId("notification123");
        emailNotification.setRecipientEmail("test@example.com");
        emailNotification.setRecipientName("Test User");
        emailNotification.setTemplateName("registration-confirmation");
        emailNotification.setType(EmailNotification.NotificationType.REGISTRATION_CONFIRMATION);
        emailNotification.setTemplateVariables(variables);
        emailNotification.setStatus(EmailNotification.NotificationStatus.PENDING);
    }
    
    @Test
    void sendEmail_Success_ReturnsEmailResponse() {
        // Given
        when(notificationRepository.save(any(EmailNotification.class))).thenReturn(emailNotification);
        when(templateService.findByName("registration-confirmation")).thenReturn(Optional.of(emailTemplate));
        when(templateService.processTemplate(eq("registration-confirmation"), any())).thenReturn("<html><body>Welcome Test User!</body></html>");
        when(templateService.processSubject(eq("Welcome to JobApp - ${name}"), any())).thenReturn("Welcome to JobApp - Test User");
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        // When
        EmailResponse response = emailService.sendEmail(emailRequest);
        
        // Then
        assertNotNull(response);
        assertEquals("notification123", response.getId());
        assertEquals("test@example.com", response.getRecipientEmail());
        assertEquals("Test User", response.getRecipientName());
        
        verify(notificationRepository, times(2)).save(any(EmailNotification.class));
        verify(mailSender).send(mimeMessage);
        verify(templateService).processTemplate(eq("registration-confirmation"), any());
    }
    
    @Test
    void sendEmail_TemplateNotFound_ThrowsException() {
        // Given
        when(notificationRepository.save(any(EmailNotification.class))).thenReturn(emailNotification);
        when(templateService.findByName("registration-confirmation")).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(RuntimeException.class, () -> emailService.sendEmail(emailRequest));
        
        verify(notificationRepository, atLeastOnce()).save(any(EmailNotification.class));
        verify(mailSender, never()).send(any(MimeMessage.class));
    }
    
    @Test
    void scheduleEmail_ValidRequest_ReturnsScheduledNotification() {
        // Given
        LocalDateTime scheduledTime = LocalDateTime.now().plusHours(1);
        emailRequest.setScheduledAt(scheduledTime);
        
        EmailNotification scheduledNotification = new EmailNotification();
        scheduledNotification.setId("scheduled123");
        scheduledNotification.setRecipientEmail("test@example.com");
        scheduledNotification.setScheduledAt(scheduledTime);
        scheduledNotification.setStatus(EmailNotification.NotificationStatus.PENDING);
        
        when(notificationRepository.save(any(EmailNotification.class))).thenReturn(scheduledNotification);
        
        // When
        EmailResponse response = emailService.scheduleEmail(emailRequest);
        
        // Then
        assertNotNull(response);
        assertEquals("scheduled123", response.getId());
        assertEquals("test@example.com", response.getRecipientEmail());
        assertEquals(EmailNotification.NotificationStatus.PENDING, response.getStatus());
        
        verify(notificationRepository).save(any(EmailNotification.class));
        verify(mailSender, never()).send(any(MimeMessage.class));
    }
    
    @Test
    void cancelNotification_PendingNotification_CancelsSuccessfully() {
        // Given
        String notificationId = "notification123";
        emailNotification.setStatus(EmailNotification.NotificationStatus.PENDING);
        
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(emailNotification));
        when(notificationRepository.save(any(EmailNotification.class))).thenReturn(emailNotification);
        
        // When
        EmailResponse response = emailService.cancelNotification(notificationId);
        
        // Then
        assertNotNull(response);
        assertEquals(EmailNotification.NotificationStatus.CANCELLED, response.getStatus());
        
        verify(notificationRepository).findById(notificationId);
        verify(notificationRepository).save(emailNotification);
    }
    
    @Test
    void cancelNotification_SentNotification_ThrowsException() {
        // Given
        String notificationId = "notification123";
        emailNotification.setStatus(EmailNotification.NotificationStatus.SENT);
        
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(emailNotification));
        
        // When & Then
        assertThrows(IllegalStateException.class, () -> emailService.cancelNotification(notificationId));
        
        verify(notificationRepository).findById(notificationId);
        verify(notificationRepository, never()).save(any(EmailNotification.class));
    }
    
    @Test
    void cancelNotification_NotificationNotFound_ThrowsException() {
        // Given
        String notificationId = "nonexistent";
        
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> emailService.cancelNotification(notificationId));
        
        verify(notificationRepository).findById(notificationId);
        verify(notificationRepository, never()).save(any(EmailNotification.class));
    }
    
    @Test
    void getNotificationById_ExistingNotification_ReturnsNotification() {
        // Given
        String notificationId = "notification123";
        
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(emailNotification));
        
        // When
        Optional<EmailResponse> response = emailService.getNotificationById(notificationId);
        
        // Then
        assertTrue(response.isPresent());
        assertEquals("notification123", response.get().getId());
        assertEquals("test@example.com", response.get().getRecipientEmail());
        
        verify(notificationRepository).findById(notificationId);
    }
    
    @Test
    void getNotificationById_NonExistentNotification_ReturnsEmpty() {
        // Given
        String notificationId = "nonexistent";
        
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());
        
        // When
        Optional<EmailResponse> response = emailService.getNotificationById(notificationId);
        
        // Then
        assertFalse(response.isPresent());
        
        verify(notificationRepository).findById(notificationId);
    }
}