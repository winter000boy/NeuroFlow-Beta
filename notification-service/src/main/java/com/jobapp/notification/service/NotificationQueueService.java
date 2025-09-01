package com.jobapp.notification.service;

import com.jobapp.notification.model.NotificationQueue;
import com.jobapp.notification.repository.NotificationQueueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationQueueService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationQueueService.class);
    
    @Autowired
    private NotificationQueueRepository queueRepository;
    
    private final String processingNodeId = generateNodeId();
    
    public NotificationQueue enqueue(String notificationId, int priority, LocalDateTime scheduledAt) {
        NotificationQueue queueItem = new NotificationQueue(notificationId);
        queueItem.setPriority(priority);
        queueItem.setScheduledAt(scheduledAt != null ? scheduledAt : LocalDateTime.now());
        queueItem.setQueueName("email-notifications");
        
        queueItem = queueRepository.save(queueItem);
        logger.info("Enqueued notification {} with priority {} scheduled for {}", 
                   notificationId, priority, queueItem.getScheduledAt());
        
        return queueItem;
    }
    
    public NotificationQueue enqueue(String notificationId) {
        return enqueue(notificationId, 3, null); // Default priority and immediate scheduling
    }
    
    public List<NotificationQueue> getNextBatch(int batchSize) {
        List<NotificationQueue> pendingItems = queueRepository
                .findByStatusAndScheduledAtBeforeOrderByPriorityAscScheduledAtAsc(
                    NotificationQueue.QueueStatus.PENDING, 
                    LocalDateTime.now()
                );
        
        // Limit to batch size
        return pendingItems.stream()
                .limit(batchSize)
                .toList();
    }
    
    public Optional<NotificationQueue> claimForProcessing(String queueItemId) {
        Optional<NotificationQueue> queueItemOpt = queueRepository.findById(queueItemId);
        
        if (queueItemOpt.isEmpty()) {
            return Optional.empty();
        }
        
        NotificationQueue queueItem = queueItemOpt.get();
        
        // Only claim if it's still pending
        if (queueItem.getStatus() != NotificationQueue.QueueStatus.PENDING) {
            return Optional.empty();
        }
        
        queueItem.markAsProcessing(processingNodeId);
        queueItem = queueRepository.save(queueItem);
        
        logger.info("Claimed queue item {} for processing by node {}", queueItemId, processingNodeId);
        
        return Optional.of(queueItem);
    }
    
    public void markAsCompleted(String queueItemId) {
        NotificationQueue queueItem = queueRepository.findById(queueItemId)
                .orElseThrow(() -> new IllegalArgumentException("Queue item not found: " + queueItemId));
        
        queueItem.markAsCompleted();
        queueRepository.save(queueItem);
        
        logger.info("Marked queue item {} as completed", queueItemId);
    }
    
    public void markAsFailed(String queueItemId, String error) {
        NotificationQueue queueItem = queueRepository.findById(queueItemId)
                .orElseThrow(() -> new IllegalArgumentException("Queue item not found: " + queueItemId));
        
        if (queueItem.canRetry()) {
            // Schedule for retry with exponential backoff
            LocalDateTime nextRetry = LocalDateTime.now().plusMinutes((long) Math.pow(2, queueItem.getRetryCount()));
            queueItem.scheduleRetry(nextRetry);
            logger.info("Scheduled queue item {} for retry at {}", queueItemId, nextRetry);
        } else {
            queueItem.markAsFailed(error);
            logger.error("Queue item {} failed permanently after {} retries: {}", 
                        queueItemId, queueItem.getRetryCount(), error);
        }
        
        queueRepository.save(queueItem);
    }
    
    public List<NotificationQueue> getRetryableItems() {
        return queueRepository.findRetryableItems(
            NotificationQueue.QueueStatus.RETRY_SCHEDULED, 
            3, 
            LocalDateTime.now()
        );
    }
    
    public void processRetryableItems() {
        List<NotificationQueue> retryableItems = getRetryableItems();
        
        logger.info("Found {} items ready for retry", retryableItems.size());
        
        for (NotificationQueue item : retryableItems) {
            item.setStatus(NotificationQueue.QueueStatus.PENDING);
            item.setScheduledAt(LocalDateTime.now());
            queueRepository.save(item);
            
            logger.info("Reset queue item {} to pending for retry", item.getId());
        }
    }
    
    public void handleStuckItems() {
        // Items that have been processing for more than 10 minutes are considered stuck
        LocalDateTime stuckThreshold = LocalDateTime.now().minusMinutes(10);
        List<NotificationQueue> stuckItems = queueRepository.findStuckProcessingItems(stuckThreshold);
        
        logger.info("Found {} stuck processing items", stuckItems.size());
        
        for (NotificationQueue item : stuckItems) {
            if (item.canRetry()) {
                LocalDateTime nextRetry = LocalDateTime.now().plusMinutes(5); // Retry in 5 minutes
                item.scheduleRetry(nextRetry);
                logger.warn("Reset stuck item {} for retry", item.getId());
            } else {
                item.markAsFailed("Processing timeout - item was stuck");
                logger.error("Marked stuck item {} as permanently failed", item.getId());
            }
            
            queueRepository.save(item);
        }
    }
    
    public void cleanupOldItems(int daysToKeep) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        
        // Delete completed items older than cutoff
        queueRepository.deleteByStatusAndProcessedAtBefore(
            NotificationQueue.QueueStatus.COMPLETED, 
            cutoffDate
        );
        
        // Delete failed items older than cutoff
        queueRepository.deleteByStatusAndProcessedAtBefore(
            NotificationQueue.QueueStatus.FAILED, 
            cutoffDate
        );
        
        logger.info("Cleaned up queue items older than {}", cutoffDate);
    }
    
    // Query methods
    public Optional<NotificationQueue> getByNotificationId(String notificationId) {
        return queueRepository.findByNotificationId(notificationId);
    }
    
    public Page<NotificationQueue> getByStatus(NotificationQueue.QueueStatus status, Pageable pageable) {
        return queueRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
    }
    
    public long getQueueSize(NotificationQueue.QueueStatus status) {
        return queueRepository.countByStatus(status);
    }
    
    public QueueStats getQueueStats() {
        QueueStats stats = new QueueStats();
        stats.setPendingCount(queueRepository.countByStatus(NotificationQueue.QueueStatus.PENDING));
        stats.setProcessingCount(queueRepository.countByStatus(NotificationQueue.QueueStatus.PROCESSING));
        stats.setCompletedCount(queueRepository.countByStatus(NotificationQueue.QueueStatus.COMPLETED));
        stats.setFailedCount(queueRepository.countByStatus(NotificationQueue.QueueStatus.FAILED));
        stats.setRetryScheduledCount(queueRepository.countByStatus(NotificationQueue.QueueStatus.RETRY_SCHEDULED));
        return stats;
    }
    
    private String generateNodeId() {
        return "node-" + System.currentTimeMillis() + "-" + Thread.currentThread().getId();
    }
    
    // Inner class for queue statistics
    public static class QueueStats {
        private long pendingCount;
        private long processingCount;
        private long completedCount;
        private long failedCount;
        private long retryScheduledCount;
        
        // Getters and Setters
        public long getPendingCount() {
            return pendingCount;
        }
        
        public void setPendingCount(long pendingCount) {
            this.pendingCount = pendingCount;
        }
        
        public long getProcessingCount() {
            return processingCount;
        }
        
        public void setProcessingCount(long processingCount) {
            this.processingCount = processingCount;
        }
        
        public long getCompletedCount() {
            return completedCount;
        }
        
        public void setCompletedCount(long completedCount) {
            this.completedCount = completedCount;
        }
        
        public long getFailedCount() {
            return failedCount;
        }
        
        public void setFailedCount(long failedCount) {
            this.failedCount = failedCount;
        }
        
        public long getRetryScheduledCount() {
            return retryScheduledCount;
        }
        
        public void setRetryScheduledCount(long retryScheduledCount) {
            this.retryScheduledCount = retryScheduledCount;
        }
        
        public long getTotalCount() {
            return pendingCount + processingCount + completedCount + failedCount + retryScheduledCount;
        }
    }
}