package com.jobapp.notification.scheduler;

import com.jobapp.notification.service.EmailService;
import com.jobapp.notification.service.NotificationQueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EmailScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailScheduler.class);
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private NotificationQueueService queueService;
    
    @Scheduled(fixedRate = 60000) // Run every minute
    public void processScheduledEmails() {
        logger.debug("Processing scheduled emails...");
        try {
            emailService.processScheduledEmails();
        } catch (Exception e) {
            logger.error("Error processing scheduled emails: {}", e.getMessage(), e);
        }
    }
    
    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    public void retryFailedEmails() {
        logger.debug("Retrying failed emails...");
        try {
            emailService.retryFailedEmails();
        } catch (Exception e) {
            logger.error("Error retrying failed emails: {}", e.getMessage(), e);
        }
    }
    
    @Scheduled(fixedRate = 120000) // Run every 2 minutes
    public void processQueueRetries() {
        logger.debug("Processing queue retries...");
        try {
            queueService.processRetryableItems();
        } catch (Exception e) {
            logger.error("Error processing queue retries: {}", e.getMessage(), e);
        }
    }
    
    @Scheduled(fixedRate = 600000) // Run every 10 minutes
    public void handleStuckQueueItems() {
        logger.debug("Handling stuck queue items...");
        try {
            queueService.handleStuckItems();
        } catch (Exception e) {
            logger.error("Error handling stuck queue items: {}", e.getMessage(), e);
        }
    }
    
    @Scheduled(cron = "0 0 2 * * ?") // Run daily at 2 AM
    public void cleanupOldQueueItems() {
        logger.info("Cleaning up old queue items...");
        try {
            queueService.cleanupOldItems(30); // Keep 30 days
        } catch (Exception e) {
            logger.error("Error cleaning up old queue items: {}", e.getMessage(), e);
        }
    }
}