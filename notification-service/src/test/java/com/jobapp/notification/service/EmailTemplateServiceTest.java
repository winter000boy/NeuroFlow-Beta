package com.jobapp.notification.service;

import com.jobapp.notification.model.EmailTemplate;
import com.jobapp.notification.repository.EmailTemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailTemplateServiceTest {
    
    @Mock
    private EmailTemplateRepository templateRepository;
    
    @Mock
    private TemplateEngine templateEngine;
    
    @InjectMocks
    private EmailTemplateService templateService;
    
    private EmailTemplate emailTemplate;
    
    @BeforeEach
    void setUp() {
        emailTemplate = new EmailTemplate();
        emailTemplate.setId("template123");
        emailTemplate.setName("test-template");
        emailTemplate.setSubject("Test Subject - ${name}");
        emailTemplate.setHtmlContent("<html><body>Hello ${name}!</body></html>");
        emailTemplate.setTextContent("Hello ${name}!");
        emailTemplate.setActive(true);
        
        Map<String, String> defaultVars = new HashMap<>();
        defaultVars.put("appName", "JobApp");
        emailTemplate.setDefaultVariables(defaultVars);
    }
    
    @Test
    void findByName_ExistingActiveTemplate_ReturnsTemplate() {
        // Given
        String templateName = "test-template";
        when(templateRepository.findByNameAndIsActive(templateName, true)).thenReturn(Optional.of(emailTemplate));
        
        // When
        Optional<EmailTemplate> result = templateService.findByName(templateName);
        
        // Then
        assertTrue(result.isPresent());
        assertEquals("test-template", result.get().getName());
        assertEquals("Test Subject - ${name}", result.get().getSubject());
        
        verify(templateRepository).findByNameAndIsActive(templateName, true);
    }
    
    @Test
    void findByName_NonExistentTemplate_ReturnsEmpty() {
        // Given
        String templateName = "nonexistent-template";
        when(templateRepository.findByNameAndIsActive(templateName, true)).thenReturn(Optional.empty());
        
        // When
        Optional<EmailTemplate> result = templateService.findByName(templateName);
        
        // Then
        assertFalse(result.isPresent());
        
        verify(templateRepository).findByNameAndIsActive(templateName, true);
    }
    
    @Test
    void createTemplate_NewTemplate_CreatesSuccessfully() {
        // Given
        EmailTemplate newTemplate = new EmailTemplate();
        newTemplate.setName("new-template");
        newTemplate.setSubject("New Template");
        
        when(templateRepository.existsByName("new-template")).thenReturn(false);
        when(templateRepository.save(newTemplate)).thenReturn(newTemplate);
        
        // When
        EmailTemplate result = templateService.createTemplate(newTemplate);
        
        // Then
        assertNotNull(result);
        assertEquals("new-template", result.getName());
        
        verify(templateRepository).existsByName("new-template");
        verify(templateRepository).save(newTemplate);
    }
    
    @Test
    void createTemplate_ExistingTemplateName_ThrowsException() {
        // Given
        EmailTemplate duplicateTemplate = new EmailTemplate();
        duplicateTemplate.setName("existing-template");
        
        when(templateRepository.existsByName("existing-template")).thenReturn(true);
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> templateService.createTemplate(duplicateTemplate));
        
        verify(templateRepository).existsByName("existing-template");
        verify(templateRepository, never()).save(any(EmailTemplate.class));
    }
    
    @Test
    void processTemplate_ValidTemplateAndVariables_ReturnsProcessedContent() {
        // Given
        String templateName = "test-template";
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", "John Doe");
        
        when(templateRepository.findByNameAndIsActive(templateName, true)).thenReturn(Optional.of(emailTemplate));
        when(templateEngine.process(eq(templateName), any(Context.class))).thenReturn("<html><body>Hello John Doe!</body></html>");
        
        // When
        String result = templateService.processTemplate(templateName, variables);
        
        // Then
        assertEquals("<html><body>Hello John Doe!</body></html>", result);
        
        verify(templateRepository).findByNameAndIsActive(templateName, true);
        verify(templateEngine).process(eq(templateName), any(Context.class));
    }
    
    @Test
    void processTemplate_TemplateNotFound_ThrowsException() {
        // Given
        String templateName = "nonexistent-template";
        Map<String, Object> variables = new HashMap<>();
        
        when(templateRepository.findByNameAndIsActive(templateName, true)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> templateService.processTemplate(templateName, variables));
        
        verify(templateRepository).findByNameAndIsActive(templateName, true);
        verify(templateEngine, never()).process(anyString(), any(Context.class));
    }
    
    @Test
    void processSubject_WithVariables_ReplacesPlaceholders() {
        // Given
        String subject = "Welcome to ${appName}, ${name}!";
        Map<String, Object> variables = new HashMap<>();
        variables.put("appName", "JobApp");
        variables.put("name", "John Doe");
        
        // When
        String result = templateService.processSubject(subject, variables);
        
        // Then
        assertEquals("Welcome to JobApp, John Doe!", result);
    }
    
    @Test
    void processSubject_NoVariables_ReturnsOriginalSubject() {
        // Given
        String subject = "Static Subject";
        
        // When
        String result = templateService.processSubject(subject, null);
        
        // Then
        assertEquals("Static Subject", result);
    }
    
    @Test
    void processSubject_NullSubject_ReturnsNull() {
        // Given
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", "John Doe");
        
        // When
        String result = templateService.processSubject(null, variables);
        
        // Then
        assertNull(result);
    }
    
    @Test
    void updateTemplate_ExistingTemplate_UpdatesSuccessfully() {
        // Given
        String templateName = "test-template";
        EmailTemplate updatedTemplate = new EmailTemplate();
        updatedTemplate.setSubject("Updated Subject");
        updatedTemplate.setHtmlContent("Updated HTML");
        updatedTemplate.setTextContent("Updated Text");
        updatedTemplate.setActive(false);
        
        when(templateRepository.findByName(templateName)).thenReturn(Optional.of(emailTemplate));
        when(templateRepository.save(any(EmailTemplate.class))).thenReturn(emailTemplate);
        
        // When
        EmailTemplate result = templateService.updateTemplate(templateName, updatedTemplate);
        
        // Then
        assertNotNull(result);
        
        verify(templateRepository).findByName(templateName);
        verify(templateRepository).save(emailTemplate);
    }
    
    @Test
    void updateTemplate_NonExistentTemplate_ThrowsException() {
        // Given
        String templateName = "nonexistent-template";
        EmailTemplate updatedTemplate = new EmailTemplate();
        
        when(templateRepository.findByName(templateName)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> templateService.updateTemplate(templateName, updatedTemplate));
        
        verify(templateRepository).findByName(templateName);
        verify(templateRepository, never()).save(any(EmailTemplate.class));
    }
    
    @Test
    void initializeDefaultTemplates_NoExistingTemplates_CreatesDefaultTemplates() {
        // Given
        when(templateRepository.existsByName("registration-confirmation")).thenReturn(false);
        when(templateRepository.existsByName("application-status-update")).thenReturn(false);
        when(templateRepository.existsByName("job-application-received")).thenReturn(false);
        
        // When
        templateService.initializeDefaultTemplates();
        
        // Then
        verify(templateRepository, times(3)).save(any(EmailTemplate.class));
        verify(templateRepository).existsByName("registration-confirmation");
        verify(templateRepository).existsByName("application-status-update");
        verify(templateRepository).existsByName("job-application-received");
    }
    
    @Test
    void initializeDefaultTemplates_ExistingTemplates_DoesNotCreateDuplicates() {
        // Given
        when(templateRepository.existsByName("registration-confirmation")).thenReturn(true);
        when(templateRepository.existsByName("application-status-update")).thenReturn(true);
        when(templateRepository.existsByName("job-application-received")).thenReturn(true);
        
        // When
        templateService.initializeDefaultTemplates();
        
        // Then
        verify(templateRepository, never()).save(any(EmailTemplate.class));
        verify(templateRepository).existsByName("registration-confirmation");
        verify(templateRepository).existsByName("application-status-update");
        verify(templateRepository).existsByName("job-application-received");
    }
}