package com.jobapp.notification.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "notification_queue")
public class NotificationQueue {
    
    @Id
    private String id;
    
    @Indexed
    private String notificationId;
    
    @Indexed
    private QueueStatus status;
    
    @Indexed
    private int priority; // 1 = highest, 5 = lowest
    
    @Indexed
    private LocalDateTime scheduledAt;
    
    private LocalDateTime processedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Retry mechanism
    private int retryCount;
    private int maxRetries;
    private LocalDateTime nextRetryAt;
    
    // Queue metadata
    private String queueName;
    private Map<String, Object> metadata;
    
    // Error tracking
    private String lastError;
    private String processingNode; // Which server/instance is processing this
    
    public enum QueueStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED,
        CANCELLED,
        RETRY_SCHEDULED
    }
    
    public NotificationQueue() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = QueueStatus.PENDING;
        this.priority = 3; // Default priority
        this.retryCount = 0;
        this.maxRetries = 3;
        this.scheduledAt = LocalDateTime.now();
    }
    
    public NotificationQueue(String notificationId) {
        this();
        this.notificationId = notificationId;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getNotificationId() {
        return notificationId;
    }
    
    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }
    
    public QueueStatus getStatus() {
        return status;
    }
    
    public void setStatus(QueueStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }
    
    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }
    
    public LocalDateTime getProcessedAt() {
        return processedAt;
    }
    
    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public int getRetryCount() {
        return retryCount;
    }
    
    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
    
    public int getMaxRetries() {
        return maxRetries;
    }
    
    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }
    
    public LocalDateTime getNextRetryAt() {
        return nextRetryAt;
    }
    
    public void setNextRetryAt(LocalDateTime nextRetryAt) {
        this.nextRetryAt = nextRetryAt;
    }
    
    public String getQueueName() {
        return queueName;
    }
    
    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    public String getLastError() {
        return lastError;
    }
    
    public void setLastError(String lastError) {
        this.lastError = lastError;
    }
    
    public String getProcessingNode() {
        return processingNode;
    }
    
    public void setProcessingNode(String processingNode) {
        this.processingNode = processingNode;
    }
    
    // Helper methods
    public void incrementRetryCount() {
        this.retryCount++;
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean canRetry() {
        return retryCount < maxRetries;
    }
    
    public void markAsProcessing(String processingNode) {
        this.status = QueueStatus.PROCESSING;
        this.processingNode = processingNode;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void markAsCompleted() {
        this.status = QueueStatus.COMPLETED;
        this.processedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public void markAsFailed(String error) {
        this.status = QueueStatus.FAILED;
        this.lastError = error;
        this.processedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public void scheduleRetry(LocalDateTime nextRetryTime) {
        this.status = QueueStatus.RETRY_SCHEDULED;
        this.nextRetryAt = nextRetryTime;
        this.incrementRetryCount();
    }
}