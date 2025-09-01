package com.jobapp.notification.service;

import com.jobapp.notification.model.EmailTemplate;
import com.jobapp.notification.repository.EmailTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class EmailTemplateService {
    
    @Autowired
    private EmailTemplateRepository templateRepository;
    
    @Autowired
    private TemplateEngine templateEngine;
    
    public Optional<EmailTemplate> findByName(String templateName) {
        return templateRepository.findByNameAndIsActive(templateName, true);
    }
    
    public EmailTemplate createTemplate(EmailTemplate template) {
        if (templateRepository.existsByName(template.getName())) {
            throw new IllegalArgumentException("Template with name '" + template.getName() + "' already exists");
        }
        return templateRepository.save(template);
    }
    
    public EmailTemplate updateTemplate(String templateName, EmailTemplate updatedTemplate) {
        EmailTemplate existingTemplate = templateRepository.findByName(templateName)
                .orElseThrow(() -> new IllegalArgumentException("Template not found: " + templateName));
        
        existingTemplate.setSubject(updatedTemplate.getSubject());
        existingTemplate.setHtmlContent(updatedTemplate.getHtmlContent());
        existingTemplate.setTextContent(updatedTemplate.getTextContent());
        existingTemplate.setDefaultVariables(updatedTemplate.getDefaultVariables());
        existingTemplate.setActive(updatedTemplate.isActive());
        existingTemplate.setUpdatedAt(LocalDateTime.now());
        
        return templateRepository.save(existingTemplate);
    }
    
    public String processTemplate(String templateName, Map<String, Object> variables) {
        EmailTemplate template = findByName(templateName)
                .orElseThrow(() -> new IllegalArgumentException("Template not found: " + templateName));
        
        Context context = new Context();
        
        // Add default variables if they exist
        if (template.getDefaultVariables() != null) {
            template.getDefaultVariables().forEach(context::setVariable);
        }
        
        // Add provided variables (these will override defaults)
        if (variables != null) {
            variables.forEach(context::setVariable);
        }
        
        // Process the HTML content using Thymeleaf
        return templateEngine.process(templateName, context);
    }
    
    public String processSubject(String subject, Map<String, Object> variables) {
        if (subject == null || variables == null) {
            return subject;
        }
        
        String processedSubject = subject;
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String placeholder = "${" + entry.getKey() + "}";
            if (processedSubject.contains(placeholder)) {
                processedSubject = processedSubject.replace(placeholder, String.valueOf(entry.getValue()));
            }
        }
        
        return processedSubject;
    }
    
    public void initializeDefaultTemplates() {
        // Registration confirmation template
        if (!templateRepository.existsByName("registration-confirmation")) {
            EmailTemplate registrationTemplate = new EmailTemplate(
                "registration-confirmation",
                "Welcome to JobApp - Please confirm your registration",
                getRegistrationConfirmationHtml(),
                getRegistrationConfirmationText()
            );
            templateRepository.save(registrationTemplate);
        }
        
        // Application status update template
        if (!templateRepository.existsByName("application-status-update")) {
            EmailTemplate statusUpdateTemplate = new EmailTemplate(
                "application-status-update",
                "Application Status Update - ${jobTitle}",
                getApplicationStatusUpdateHtml(),
                getApplicationStatusUpdateText()
            );
            templateRepository.save(statusUpdateTemplate);
        }
        
        // Job application received template
        if (!templateRepository.existsByName("job-application-received")) {
            EmailTemplate applicationReceivedTemplate = new EmailTemplate(
                "job-application-received",
                "New Application Received - ${jobTitle}",
                getJobApplicationReceivedHtml(),
                getJobApplicationReceivedText()
            );
            templateRepository.save(applicationReceivedTemplate);
        }
    }
    
    private String getRegistrationConfirmationHtml() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Welcome to JobApp</title>
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h1 style="color: #2563eb;">Welcome to JobApp!</h1>
                    <p>Dear <span th:text="${name}">User</span>,</p>
                    <p>Thank you for registering with JobApp. Your account has been successfully created.</p>
                    <div style="background-color: #f3f4f6; padding: 15px; border-radius: 5px; margin: 20px 0;">
                        <p><strong>Account Details:</strong></p>
                        <p>Email: <span th:text="${email}">user@example.com</span></p>
                        <p>Role: <span th:text="${role}">Candidate</span></p>
                    </div>
                    <p>You can now log in to your account and start exploring job opportunities.</p>
                    <p>Best regards,<br>The JobApp Team</p>
                </div>
            </body>
            </html>
            """;
    }
    
    private String getRegistrationConfirmationText() {
        return """
            Welcome to JobApp!
            
            Dear ${name},
            
            Thank you for registering with JobApp. Your account has been successfully created.
            
            Account Details:
            Email: ${email}
            Role: ${role}
            
            You can now log in to your account and start exploring job opportunities.
            
            Best regards,
            The JobApp Team
            """;
    }
    
    private String getApplicationStatusUpdateHtml() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Application Status Update</title>
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h1 style="color: #2563eb;">Application Status Update</h1>
                    <p>Dear <span th:text="${candidateName}">Candidate</span>,</p>
                    <p>We have an update regarding your application for the position:</p>
                    <div style="background-color: #f3f4f6; padding: 15px; border-radius: 5px; margin: 20px 0;">
                        <p><strong>Job Title:</strong> <span th:text="${jobTitle}">Software Engineer</span></p>
                        <p><strong>Company:</strong> <span th:text="${companyName}">Tech Corp</span></p>
                        <p><strong>New Status:</strong> <span th:text="${status}" style="font-weight: bold; color: #059669;">In Review</span></p>
                    </div>
                    <div th:if="${status == 'HIRED'}" style="background-color: #d1fae5; padding: 15px; border-radius: 5px; margin: 20px 0;">
                        <p style="color: #059669; font-weight: bold;">Congratulations! You have been selected for this position.</p>
                    </div>
                    <p th:if="${notes}" th:text="${notes}">Additional notes from the employer.</p>
                    <p>You can view more details by logging into your JobApp account.</p>
                    <p>Best regards,<br>The JobApp Team</p>
                </div>
            </body>
            </html>
            """;
    }
    
    private String getApplicationStatusUpdateText() {
        return """
            Application Status Update
            
            Dear ${candidateName},
            
            We have an update regarding your application for the position:
            
            Job Title: ${jobTitle}
            Company: ${companyName}
            New Status: ${status}
            
            ${notes}
            
            You can view more details by logging into your JobApp account.
            
            Best regards,
            The JobApp Team
            """;
    }
    
    private String getJobApplicationReceivedHtml() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>New Job Application Received</title>
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h1 style="color: #2563eb;">New Job Application Received</h1>
                    <p>Dear <span th:text="${employerName}">Employer</span>,</p>
                    <p>You have received a new application for your job posting:</p>
                    <div style="background-color: #f3f4f6; padding: 15px; border-radius: 5px; margin: 20px 0;">
                        <p><strong>Job Title:</strong> <span th:text="${jobTitle}">Software Engineer</span></p>
                        <p><strong>Candidate:</strong> <span th:text="${candidateName}">John Doe</span></p>
                        <p><strong>Applied On:</strong> <span th:text="${appliedDate}">2024-01-15</span></p>
                    </div>
                    <p>You can review the candidate's profile and resume by logging into your JobApp employer dashboard.</p>
                    <p>Best regards,<br>The JobApp Team</p>
                </div>
            </body>
            </html>
            """;
    }
    
    private String getJobApplicationReceivedText() {
        return """
            New Job Application Received
            
            Dear ${employerName},
            
            You have received a new application for your job posting:
            
            Job Title: ${jobTitle}
            Candidate: ${candidateName}
            Applied On: ${appliedDate}
            
            You can review the candidate's profile and resume by logging into your JobApp employer dashboard.
            
            Best regards,
            The JobApp Team
            """;
    }
}