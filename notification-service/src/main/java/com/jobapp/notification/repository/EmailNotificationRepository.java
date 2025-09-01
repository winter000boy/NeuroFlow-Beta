package com.jobapp.notification.repository;

import com.jobapp.notification.model.EmailNotification;
import com.jobapp.notification.model.EmailNotification.NotificationStatus;
import com.jobapp.notification.model.EmailNotification.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmailNotificationRepository extends MongoRepository<EmailNotification, String> {
    
    Page<EmailNotification> findByRecipientEmailOrderByCreatedAtDesc(String recipientEmail, Pageable pageable);
    
    List<EmailNotification> findByStatusAndScheduledAtBefore(NotificationStatus status, LocalDateTime scheduledAt);
    
    List<EmailNotification> findByStatusAndRetryCountLessThan(NotificationStatus status, int maxRetries);
    
    Page<EmailNotification> findByStatusOrderByCreatedAtDesc(NotificationStatus status, Pageable pageable);
    
    Page<EmailNotification> findByTypeOrderByCreatedAtDesc(NotificationType type, Pageable pageable);
    
    List<EmailNotification> findByUserIdOrderByCreatedAtDesc(String userId);
    
    List<EmailNotification> findByJobIdOrderByCreatedAtDesc(String jobId);
    
    List<EmailNotification> findByApplicationIdOrderByCreatedAtDesc(String applicationId);
    
    @Query("{ 'status': ?0, 'retryCount': { $lt: ?1 }, 'scheduledAt': { $lte: ?2 } }")
    List<EmailNotification> findPendingNotificationsForRetry(NotificationStatus status, int maxRetries, LocalDateTime now);
    
    long countByStatusAndCreatedAtBetween(NotificationStatus status, LocalDateTime startDate, LocalDateTime endDate);
    
    long countByTypeAndCreatedAtBetween(NotificationType type, LocalDateTime startDate, LocalDateTime endDate);
}