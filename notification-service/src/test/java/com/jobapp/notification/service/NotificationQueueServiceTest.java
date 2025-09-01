package com.jobapp.notification.service;

import com.jobapp.notification.model.NotificationQueue;
import com.jobapp.notification.repository.NotificationQueueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationQueueServiceTest {
    
    @Mock
    private NotificationQueueRepository queueRepository;
    
    @InjectMocks
    private NotificationQueueService queueService;
    
    private NotificationQueue queueItem;
    
    @BeforeEach
    void setUp() {
        queueItem = new NotificationQueue();
        queueItem.setId("queue123");
        queueItem.setNotificationId("notification123");
        queueItem.setStatus(NotificationQueue.QueueStatus.PENDING);
        queueItem.setPriority(3);
        queueItem.setScheduledAt(LocalDateTime.now());
    }
    
    @Test
    void enqueue_ValidNotification_CreatesQueueItem() {
        // Given
        when(queueRepository.save(any(NotificationQueue.class))).thenReturn(queueItem);
        
        // When
        NotificationQueue result = queueService.enqueue("notification123", 2, LocalDateTime.now().plusMinutes(5));
        
        // Then
        assertNotNull(result);
        assertEquals("notification123", result.getNotificationId());
        
        verify(queueRepository).save(any(NotificationQueue.class));
    }
    
    @Test
    void enqueue_DefaultParameters_CreatesWithDefaults() {
        // Given
        when(queueRepository.save(any(NotificationQueue.class))).thenReturn(queueItem);
        
        // When
        NotificationQueue result = queueService.enqueue("notification123");
        
        // Then
        assertNotNull(result);
        assertEquals("notification123", result.getNotificationId());
        
        verify(queueRepository).save(any(NotificationQueue.class));
    }
    
    @Test
    void getNextBatch_PendingItems_ReturnsBatch() {
        // Given
        List<NotificationQueue> pendingItems = Arrays.asList(queueItem);
        when(queueRepository.findByStatusAndScheduledAtBeforeOrderByPriorityAscScheduledAtAsc(
                eq(NotificationQueue.QueueStatus.PENDING), any(LocalDateTime.class)))
                .thenReturn(pendingItems);
        
        // When
        List<NotificationQueue> result = queueService.getNextBatch(10);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("notification123", result.get(0).getNotificationId());
        
        verify(queueRepository).findByStatusAndScheduledAtBeforeOrderByPriorityAscScheduledAtAsc(
                eq(NotificationQueue.QueueStatus.PENDING), any(LocalDateTime.class));
    }
    
    @Test
    void claimForProcessing_PendingItem_ClaimsSuccessfully() {
        // Given
        when(queueRepository.findById("queue123")).thenReturn(Optional.of(queueItem));
        when(queueRepository.save(any(NotificationQueue.class))).thenReturn(queueItem);
        
        // When
        Optional<NotificationQueue> result = queueService.claimForProcessing("queue123");
        
        // Then
        assertTrue(result.isPresent());
        assertEquals("queue123", result.get().getId());
        
        verify(queueRepository).findById("queue123");
        verify(queueRepository).save(queueItem);
    }
    
    @Test
    void claimForProcessing_NonExistentItem_ReturnsEmpty() {
        // Given
        when(queueRepository.findById("nonexistent")).thenReturn(Optional.empty());
        
        // When
        Optional<NotificationQueue> result = queueService.claimForProcessing("nonexistent");
        
        // Then
        assertFalse(result.isPresent());
        
        verify(queueRepository).findById("nonexistent");
        verify(queueRepository, never()).save(any(NotificationQueue.class));
    }
    
    @Test
    void claimForProcessing_AlreadyProcessing_ReturnsEmpty() {
        // Given
        queueItem.setStatus(NotificationQueue.QueueStatus.PROCESSING);
        when(queueRepository.findById("queue123")).thenReturn(Optional.of(queueItem));
        
        // When
        Optional<NotificationQueue> result = queueService.claimForProcessing("queue123");
        
        // Then
        assertFalse(result.isPresent());
        
        verify(queueRepository).findById("queue123");
        verify(queueRepository, never()).save(any(NotificationQueue.class));
    }
    
    @Test
    void markAsCompleted_ValidItem_MarksCompleted() {
        // Given
        when(queueRepository.findById("queue123")).thenReturn(Optional.of(queueItem));
        when(queueRepository.save(any(NotificationQueue.class))).thenReturn(queueItem);
        
        // When
        queueService.markAsCompleted("queue123");
        
        // Then
        verify(queueRepository).findById("queue123");
        verify(queueRepository).save(queueItem);
    }
    
    @Test
    void markAsCompleted_NonExistentItem_ThrowsException() {
        // Given
        when(queueRepository.findById("nonexistent")).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> queueService.markAsCompleted("nonexistent"));
        
        verify(queueRepository).findById("nonexistent");
        verify(queueRepository, never()).save(any(NotificationQueue.class));
    }
    
    @Test
    void markAsFailed_CanRetry_SchedulesRetry() {
        // Given
        queueItem.setRetryCount(1);
        queueItem.setMaxRetries(3);
        when(queueRepository.findById("queue123")).thenReturn(Optional.of(queueItem));
        when(queueRepository.save(any(NotificationQueue.class))).thenReturn(queueItem);
        
        // When
        queueService.markAsFailed("queue123", "Test error");
        
        // Then
        verify(queueRepository).findById("queue123");
        verify(queueRepository).save(queueItem);
    }
    
    @Test
    void markAsFailed_MaxRetriesReached_MarksPermanentlyFailed() {
        // Given
        queueItem.setRetryCount(3);
        queueItem.setMaxRetries(3);
        when(queueRepository.findById("queue123")).thenReturn(Optional.of(queueItem));
        when(queueRepository.save(any(NotificationQueue.class))).thenReturn(queueItem);
        
        // When
        queueService.markAsFailed("queue123", "Test error");
        
        // Then
        verify(queueRepository).findById("queue123");
        verify(queueRepository).save(queueItem);
    }
    
    @Test
    void getRetryableItems_HasRetryableItems_ReturnsItems() {
        // Given
        List<NotificationQueue> retryableItems = Arrays.asList(queueItem);
        when(queueRepository.findRetryableItems(
                eq(NotificationQueue.QueueStatus.RETRY_SCHEDULED), eq(3), any(LocalDateTime.class)))
                .thenReturn(retryableItems);
        
        // When
        List<NotificationQueue> result = queueService.getRetryableItems();
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        
        verify(queueRepository).findRetryableItems(
                eq(NotificationQueue.QueueStatus.RETRY_SCHEDULED), eq(3), any(LocalDateTime.class));
    }
    
    @Test
    void processRetryableItems_HasRetryableItems_ProcessesItems() {
        // Given
        List<NotificationQueue> retryableItems = Arrays.asList(queueItem);
        when(queueRepository.findRetryableItems(
                eq(NotificationQueue.QueueStatus.RETRY_SCHEDULED), eq(3), any(LocalDateTime.class)))
                .thenReturn(retryableItems);
        when(queueRepository.save(any(NotificationQueue.class))).thenReturn(queueItem);
        
        // When
        queueService.processRetryableItems();
        
        // Then
        verify(queueRepository).findRetryableItems(
                eq(NotificationQueue.QueueStatus.RETRY_SCHEDULED), eq(3), any(LocalDateTime.class));
        verify(queueRepository).save(queueItem);
    }
    
    @Test
    void handleStuckItems_HasStuckItems_HandlesItems() {
        // Given
        queueItem.setStatus(NotificationQueue.QueueStatus.PROCESSING);
        queueItem.setRetryCount(1);
        List<NotificationQueue> stuckItems = Arrays.asList(queueItem);
        when(queueRepository.findStuckProcessingItems(any(LocalDateTime.class))).thenReturn(stuckItems);
        when(queueRepository.save(any(NotificationQueue.class))).thenReturn(queueItem);
        
        // When
        queueService.handleStuckItems();
        
        // Then
        verify(queueRepository).findStuckProcessingItems(any(LocalDateTime.class));
        verify(queueRepository).save(queueItem);
    }
    
    @Test
    void getQueueStats_ValidCall_ReturnsStats() {
        // Given
        when(queueRepository.countByStatus(NotificationQueue.QueueStatus.PENDING)).thenReturn(5L);
        when(queueRepository.countByStatus(NotificationQueue.QueueStatus.PROCESSING)).thenReturn(2L);
        when(queueRepository.countByStatus(NotificationQueue.QueueStatus.COMPLETED)).thenReturn(100L);
        when(queueRepository.countByStatus(NotificationQueue.QueueStatus.FAILED)).thenReturn(3L);
        when(queueRepository.countByStatus(NotificationQueue.QueueStatus.RETRY_SCHEDULED)).thenReturn(1L);
        
        // When
        NotificationQueueService.QueueStats stats = queueService.getQueueStats();
        
        // Then
        assertNotNull(stats);
        assertEquals(5L, stats.getPendingCount());
        assertEquals(2L, stats.getProcessingCount());
        assertEquals(100L, stats.getCompletedCount());
        assertEquals(3L, stats.getFailedCount());
        assertEquals(1L, stats.getRetryScheduledCount());
        assertEquals(111L, stats.getTotalCount());
        
        verify(queueRepository, times(5)).countByStatus(any(NotificationQueue.QueueStatus.class));
    }
    
    @Test
    void getByNotificationId_ExistingNotification_ReturnsQueueItem() {
        // Given
        when(queueRepository.findByNotificationId("notification123")).thenReturn(Optional.of(queueItem));
        
        // When
        Optional<NotificationQueue> result = queueService.getByNotificationId("notification123");
        
        // Then
        assertTrue(result.isPresent());
        assertEquals("notification123", result.get().getNotificationId());
        
        verify(queueRepository).findByNotificationId("notification123");
    }
    
    @Test
    void cleanupOldItems_ValidCall_CleansUpItems() {
        // When
        queueService.cleanupOldItems(30);
        
        // Then
        verify(queueRepository, times(2)).deleteByStatusAndProcessedAtBefore(
                any(NotificationQueue.QueueStatus.class), any(LocalDateTime.class));
    }
}