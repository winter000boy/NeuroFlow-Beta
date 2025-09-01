package com.jobapp.notification.service;

import com.jobapp.notification.dto.EmailRequest;
import com.jobapp.notification.dto.EmailResponse;
import com.jobapp.notification.model.EmailNotification;
import com.jobapp.notification.model.EmailTemplate;
import com.jobapp.notification.repository.EmailNotificationRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private EmailNotificationRepository notificationRepository;
    
    @Autowired
    private EmailTemplateService templateService;
    
    @Autowired
    private NotificationPreferenceService preferenceService;
    
    @Autowired
    private NotificationQueueService queueService;
    
    @Value("${email.from}")
    private String fromEmail;
    
    public EmailResponse sendEmail(EmailRequest emailRequest) {
        try {
            // Check user preferences if userId is provided
            if (emailRequest.getUserId() != null) {
                if (!preferenceService.shouldSendNotification(emailRequest.getUserId(), emailRequest.getType())) {
                    logger.info("Notification blocked by user preferences for user: {}", emailRequest.getUserId());
                    throw new RuntimeException("Notification blocked by user preferences");
                }
                
                if (preferenceService.isInQuietHours(emailRequest.getUserId())) {
                    logger.info("User {} is in quiet hours, scheduling for later", emailRequest.getUserId());
                    return scheduleEmail(emailRequest);
                }
            }
            
            // Create notification record
            EmailNotification notification = createNotificationFromRequest(emailRequest);
            notification = notificationRepository.save(notification);
            
            // Add to queue for processing
            queueService.enqueue(notification.getId());
            
            // Process template and send email
            processAndSendEmail(notification);
            
            return convertToResponse(notification);
        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}", emailRequest.getRecipientEmail(), e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
    
    @Async
    public CompletableFuture<Void> sendEmailAsync(EmailRequest emailRequest) {
        try {
            sendEmail(emailRequest);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            logger.error("Async email sending failed: {}", e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }
    
    public EmailResponse scheduleEmail(EmailRequest emailRequest) {
        EmailNotification notification = createNotificationFromRequest(emailRequest);
        
        if (emailRequest.getScheduledAt() != null) {
            notification.setScheduledAt(emailRequest.getScheduledAt());
        }
        
        notification = notificationRepository.save(notification);
        logger.info("Email scheduled for {} at {}", notification.getRecipientEmail(), notification.getScheduledAt());
        
        return convertToResponse(notification);
    }
    
    public void processScheduledEmails() {
        List<EmailNotification> pendingNotifications = notificationRepository
                .findByStatusAndScheduledAtBefore(EmailNotification.NotificationStatus.PENDING, LocalDateTime.now());
        
        logger.info("Processing {} scheduled emails", pendingNotifications.size());
        
        for (EmailNotification notification : pendingNotifications) {
            try {
                processAndSendEmail(notification);
            } catch (Exception e) {
                logger.error("Failed to process scheduled email {}: {}", notification.getId(), e.getMessage(), e);
                handleEmailFailure(notification, e.getMessage());
            }
        }
    }
    
    public void retryFailedEmails() {
        List<EmailNotification> failedNotifications = notificationRepository
                .findPendingNotificationsForRetry(
                    EmailNotification.NotificationStatus.FAILED, 
                    3, 
                    LocalDateTime.now()
                );
        
        logger.info("Retrying {} failed emails", failedNotifications.size());
        
        for (EmailNotification notification : failedNotifications) {
            try {
                notification.setRetryCount(notification.getRetryCount() + 1);
                processAndSendEmail(notification);
            } catch (Exception e) {
                logger.error("Retry failed for email {}: {}", notification.getId(), e.getMessage(), e);
                handleEmailFailure(notification, e.getMessage());
            }
        }
    }
    
    private void processAndSendEmail(EmailNotification notification) throws MessagingException {
        try {
            // Get template
            Optional<EmailTemplate> templateOpt = templateService.findByName(notification.getTemplateName());
            if (templateOpt.isEmpty()) {
                throw new IllegalArgumentException("Template not found: " + notification.getTemplateName());
            }
            
            EmailTemplate template = templateOpt.get();
            
            // Process template content
            String htmlContent = templateService.processTemplate(notification.getTemplateName(), notification.getTemplateVariables());
            String subject = templateService.processSubject(template.getSubject(), notification.getTemplateVariables());
            
            // Update notification with processed content
            notification.setHtmlContent(htmlContent);
            notification.setSubject(subject);
            notification.setTextContent(template.getTextContent()); // Could also process this with variables
            
            // Send email
            sendMimeMessage(notification);
            
            // Update status
            notification.setStatus(EmailNotification.NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
            
            logger.info("Email sent successfully to {} with subject: {}", notification.getRecipientEmail(), subject);
            
        } catch (Exception e) {
            handleEmailFailure(notification, e.getMessage());
            throw e;
        }
    }
    
    private void sendMimeMessage(EmailNotification notification) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom(fromEmail);
        helper.setTo(notification.getRecipientEmail());
        helper.setSubject(notification.getSubject());
        
        // Set HTML content
        if (notification.getHtmlContent() != null) {
            helper.setText(notification.getTextContent(), notification.getHtmlContent());
        } else {
            helper.setText(notification.getTextContent());
        }
        
        mailSender.send(message);
    }
    
    private void handleEmailFailure(EmailNotification notification, String errorMessage) {
        notification.setStatus(EmailNotification.NotificationStatus.FAILED);
        notification.setErrorMessage(errorMessage);
        
        if (notification.getRetryCount() >= notification.getMaxRetries()) {
            logger.error("Email {} exceeded max retries. Marking as permanently failed.", notification.getId());
        } else {
            // Schedule retry with exponential backoff
            LocalDateTime nextRetry = LocalDateTime.now().plusMinutes((long) Math.pow(2, notification.getRetryCount()));
            notification.setScheduledAt(nextRetry);
            logger.warn("Email {} failed. Scheduled for retry at {}", notification.getId(), nextRetry);
        }
        
        notificationRepository.save(notification);
    }
    
    private EmailNotification createNotificationFromRequest(EmailRequest request) {
        EmailNotification notification = new EmailNotification();
        notification.setRecipientEmail(request.getRecipientEmail());
        notification.setRecipientName(request.getRecipientName());
        notification.setTemplateName(request.getTemplateName());
        notification.setType(request.getType());
        notification.setTemplateVariables(request.getTemplateVariables());
        notification.setUserId(request.getUserId());
        notification.setJobId(request.getJobId());
        notification.setApplicationId(request.getApplicationId());
        
        if (request.getScheduledAt() != null) {
            notification.setScheduledAt(request.getScheduledAt());
        }
        
        return notification;
    }
    
    private EmailResponse convertToResponse(EmailNotification notification) {
        EmailResponse response = new EmailResponse();
        response.setId(notification.getId());
        response.setRecipientEmail(notification.getRecipientEmail());
        response.setRecipientName(notification.getRecipientName());
        response.setSubject(notification.getSubject());
        response.setStatus(notification.getStatus());
        response.setType(notification.getType());
        response.setErrorMessage(notification.getErrorMessage());
        response.setRetryCount(notification.getRetryCount());
        response.setScheduledAt(notification.getScheduledAt());
        response.setSentAt(notification.getSentAt());
        response.setCreatedAt(notification.getCreatedAt());
        return response;
    }
    
    // Query methods
    public Page<EmailResponse> getNotificationsByRecipient(String recipientEmail, Pageable pageable) {
        Page<EmailNotification> notifications = notificationRepository
                .findByRecipientEmailOrderByCreatedAtDesc(recipientEmail, pageable);
        return notifications.map(this::convertToResponse);
    }
    
    public Page<EmailResponse> getNotificationsByStatus(EmailNotification.NotificationStatus status, Pageable pageable) {
        Page<EmailNotification> notifications = notificationRepository
                .findByStatusOrderByCreatedAtDesc(status, pageable);
        return notifications.map(this::convertToResponse);
    }
    
    public List<EmailResponse> getNotificationsByUser(String userId) {
        List<EmailNotification> notifications = notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream().map(this::convertToResponse).toList();
    }
    
    public Optional<EmailResponse> getNotificationById(String notificationId) {
        return notificationRepository.findById(notificationId)
                .map(this::convertToResponse);
    }
    
    public EmailResponse cancelNotification(String notificationId) {
        EmailNotification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + notificationId));
        
        if (notification.getStatus() == EmailNotification.NotificationStatus.SENT) {
            throw new IllegalStateException("Cannot cancel already sent notification");
        }
        
        notification.setStatus(EmailNotification.NotificationStatus.CANCELLED);
        notification = notificationRepository.save(notification);
        
        return convertToResponse(notification);
    }
}