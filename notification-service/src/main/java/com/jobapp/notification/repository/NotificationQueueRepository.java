package com.jobapp.notification.repository;

import com.jobapp.notification.model.NotificationQueue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationQueueRepository extends MongoRepository<NotificationQueue, String> {
    
    Optional<NotificationQueue> findByNotificationId(String notificationId);
    
    List<NotificationQueue> findByStatusOrderByPriorityAscScheduledAtAsc(NotificationQueue.QueueStatus status);
    
    List<NotificationQueue> findByStatusAndScheduledAtBeforeOrderByPriorityAscScheduledAtAsc(
            NotificationQueue.QueueStatus status, LocalDateTime scheduledAt);
    
    List<NotificationQueue> findByStatusAndNextRetryAtBeforeOrderByPriorityAscNextRetryAtAsc(
            NotificationQueue.QueueStatus status, LocalDateTime nextRetryAt);
    
    Page<NotificationQueue> findByStatusOrderByCreatedAtDesc(NotificationQueue.QueueStatus status, Pageable pageable);
    
    List<NotificationQueue> findByQueueNameAndStatusOrderByPriorityAscScheduledAtAsc(
            String queueName, NotificationQueue.QueueStatus status);
    
    @Query("{ 'status': ?0, 'retryCount': { $lt: ?1 }, 'nextRetryAt': { $lte: ?2 } }")
    List<NotificationQueue> findRetryableItems(NotificationQueue.QueueStatus status, int maxRetries, LocalDateTime now);
    
    @Query("{ 'status': 'PROCESSING', 'updatedAt': { $lt: ?0 } }")
    List<NotificationQueue> findStuckProcessingItems(LocalDateTime stuckThreshold);
    
    long countByStatus(NotificationQueue.QueueStatus status);
    
    long countByStatusAndCreatedAtBetween(NotificationQueue.QueueStatus status, LocalDateTime startDate, LocalDateTime endDate);
    
    void deleteByStatusAndProcessedAtBefore(NotificationQueue.QueueStatus status, LocalDateTime cutoffDate);
}