package com.jobapp.notification.controller;

import com.jobapp.notification.model.NotificationQueue;
import com.jobapp.notification.service.NotificationQueueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications/queue")
@Tag(name = "Notification Queue", description = "Notification queue management endpoints")
public class NotificationQueueController {
    
    @Autowired
    private NotificationQueueService queueService;
    
    @PostMapping("/enqueue")
    @Operation(summary = "Enqueue notification", 
               description = "Add a notification to the processing queue")
    public ResponseEntity<NotificationQueue> enqueueNotification(
            @Parameter(description = "Notification ID") @RequestParam String notificationId,
            @Parameter(description = "Priority (1=highest, 5=lowest)") @RequestParam(defaultValue = "3") int priority,
            @Parameter(description = "Scheduled time") @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime scheduledAt) {
        try {
            NotificationQueue queueItem = queueService.enqueue(notificationId, priority, scheduledAt);
            return ResponseEntity.status(HttpStatus.CREATED).body(queueItem);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/next-batch")
    @Operation(summary = "Get next batch for processing", 
               description = "Retrieve the next batch of notifications ready for processing")
    public ResponseEntity<List<NotificationQueue>> getNextBatch(
            @Parameter(description = "Batch size") @RequestParam(defaultValue = "10") int batchSize) {
        try {
            List<NotificationQueue> batch = queueService.getNextBatch(batchSize);
            return ResponseEntity.ok(batch);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{queueItemId}/claim")
    @Operation(summary = "Claim queue item for processing", 
               description = "Claim a queue item for processing by the current node")
    public ResponseEntity<NotificationQueue> claimForProcessing(
            @Parameter(description = "Queue item ID") @PathVariable String queueItemId) {
        try {
            Optional<NotificationQueue> queueItem = queueService.claimForProcessing(queueItemId);
            return queueItem.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{queueItemId}/complete")
    @Operation(summary = "Mark as completed", 
               description = "Mark a queue item as successfully completed")
    public ResponseEntity<Void> markAsCompleted(
            @Parameter(description = "Queue item ID") @PathVariable String queueItemId) {
        try {
            queueService.markAsCompleted(queueItemId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{queueItemId}/fail")
    @Operation(summary = "Mark as failed", 
               description = "Mark a queue item as failed with error message")
    public ResponseEntity<Void> markAsFailed(
            @Parameter(description = "Queue item ID") @PathVariable String queueItemId,
            @Parameter(description = "Error message") @RequestParam String error) {
        try {
            queueService.markAsFailed(queueItemId, error);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/retryable")
    @Operation(summary = "Get retryable items", 
               description = "Retrieve all items that are ready for retry")
    public ResponseEntity<List<NotificationQueue>> getRetryableItems() {
        try {
            List<NotificationQueue> retryableItems = queueService.getRetryableItems();
            return ResponseEntity.ok(retryableItems);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/process-retries")
    @Operation(summary = "Process retryable items", 
               description = "Process all items that are ready for retry")
    public ResponseEntity<String> processRetryableItems() {
        try {
            queueService.processRetryableItems();
            return ResponseEntity.ok("Retryable items processed");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process retryable items");
        }
    }
    
    @PostMapping("/handle-stuck")
    @Operation(summary = "Handle stuck items", 
               description = "Handle items that have been stuck in processing state")
    public ResponseEntity<String> handleStuckItems() {
        try {
            queueService.handleStuckItems();
            return ResponseEntity.ok("Stuck items handled");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to handle stuck items");
        }
    }
    
    @DeleteMapping("/cleanup")
    @Operation(summary = "Cleanup old items", 
               description = "Clean up old completed and failed queue items")
    public ResponseEntity<String> cleanupOldItems(
            @Parameter(description = "Days to keep") @RequestParam(defaultValue = "30") int daysToKeep) {
        try {
            queueService.cleanupOldItems(daysToKeep);
            return ResponseEntity.ok("Old items cleaned up");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to cleanup old items");
        }
    }
    
    @GetMapping("/notification/{notificationId}")
    @Operation(summary = "Get queue item by notification ID", 
               description = "Retrieve queue item for a specific notification")
    public ResponseEntity<NotificationQueue> getByNotificationId(
            @Parameter(description = "Notification ID") @PathVariable String notificationId) {
        Optional<NotificationQueue> queueItem = queueService.getByNotificationId(notificationId);
        return queueItem.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get items by status", 
               description = "Retrieve queue items with a specific status")
    public ResponseEntity<Page<NotificationQueue>> getByStatus(
            @Parameter(description = "Queue status") @PathVariable NotificationQueue.QueueStatus status,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationQueue> queueItems = queueService.getByStatus(status, pageable);
        return ResponseEntity.ok(queueItems);
    }
    
    @GetMapping("/stats")
    @Operation(summary = "Get queue statistics", 
               description = "Retrieve statistics about the notification queue")
    public ResponseEntity<NotificationQueueService.QueueStats> getQueueStats() {
        try {
            NotificationQueueService.QueueStats stats = queueService.getQueueStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/size/{status}")
    @Operation(summary = "Get queue size by status", 
               description = "Get the number of items in the queue with a specific status")
    public ResponseEntity<Long> getQueueSize(
            @Parameter(description = "Queue status") @PathVariable NotificationQueue.QueueStatus status) {
        try {
            long size = queueService.getQueueSize(status);
            return ResponseEntity.ok(size);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}