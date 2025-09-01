package com.jobapp.notification.service;

import com.jobapp.notification.dto.NotificationPreferenceRequest;
import com.jobapp.notification.dto.NotificationPreferenceResponse;
import com.jobapp.notification.model.EmailNotification;
import com.jobapp.notification.model.NotificationPreference;
import com.jobapp.notification.repository.NotificationPreferenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationPreferenceServiceTest {
    
    @Mock
    private NotificationPreferenceRepository preferenceRepository;
    
    @InjectMocks
    private NotificationPreferenceService preferenceService;
    
    private NotificationPreferenceRequest request;
    private NotificationPreference preference;
    
    @BeforeEach
    void setUp() {
        request = new NotificationPreferenceRequest();
        request.setUserId("user123");
        request.setUserEmail("user@example.com");
        request.setUserName("Test User");
        request.setEmailEnabled(true);
        request.setApplicationStatusUpdateEnabled(true);
        
        preference = new NotificationPreference();
        preference.setId("pref123");
        preference.setUserId("user123");
        preference.setUserEmail("user@example.com");
        preference.setUserName("Test User");
        preference.setEmailEnabled(true);
        preference.setApplicationStatusUpdateEnabled(true);
    }
    
    @Test
    void createOrUpdatePreferences_NewUser_CreatesPreferences() {
        // Given
        when(preferenceRepository.findByUserId("user123")).thenReturn(Optional.empty());
        when(preferenceRepository.save(any(NotificationPreference.class))).thenReturn(preference);
        
        // When
        NotificationPreferenceResponse response = preferenceService.createOrUpdatePreferences(request);
        
        // Then
        assertNotNull(response);
        assertEquals("user123", response.getUserId());
        assertEquals("user@example.com", response.getUserEmail());
        assertTrue(response.isEmailEnabled());
        
        verify(preferenceRepository).findByUserId("user123");
        verify(preferenceRepository).save(any(NotificationPreference.class));
    }
    
    @Test
    void createOrUpdatePreferences_ExistingUser_UpdatesPreferences() {
        // Given
        when(preferenceRepository.findByUserId("user123")).thenReturn(Optional.of(preference));
        when(preferenceRepository.save(any(NotificationPreference.class))).thenReturn(preference);
        
        // When
        NotificationPreferenceResponse response = preferenceService.createOrUpdatePreferences(request);
        
        // Then
        assertNotNull(response);
        assertEquals("user123", response.getUserId());
        
        verify(preferenceRepository).findByUserId("user123");
        verify(preferenceRepository).save(preference);
    }
    
    @Test
    void getPreferencesByUserId_ExistingUser_ReturnsPreferences() {
        // Given
        when(preferenceRepository.findByUserId("user123")).thenReturn(Optional.of(preference));
        
        // When
        Optional<NotificationPreferenceResponse> response = preferenceService.getPreferencesByUserId("user123");
        
        // Then
        assertTrue(response.isPresent());
        assertEquals("user123", response.get().getUserId());
        
        verify(preferenceRepository).findByUserId("user123");
    }
    
    @Test
    void getPreferencesByUserId_NonExistentUser_ReturnsEmpty() {
        // Given
        when(preferenceRepository.findByUserId("nonexistent")).thenReturn(Optional.empty());
        
        // When
        Optional<NotificationPreferenceResponse> response = preferenceService.getPreferencesByUserId("nonexistent");
        
        // Then
        assertFalse(response.isPresent());
        
        verify(preferenceRepository).findByUserId("nonexistent");
    }
    
    @Test
    void createDefaultPreferences_NewUser_CreatesWithDefaults() {
        // Given
        when(preferenceRepository.existsByUserId("user123")).thenReturn(false);
        when(preferenceRepository.save(any(NotificationPreference.class))).thenReturn(preference);
        
        // When
        NotificationPreferenceResponse response = preferenceService.createDefaultPreferences("user123", "user@example.com", "Test User");
        
        // Then
        assertNotNull(response);
        assertEquals("user123", response.getUserId());
        assertEquals("user@example.com", response.getUserEmail());
        
        verify(preferenceRepository).existsByUserId("user123");
        verify(preferenceRepository).save(any(NotificationPreference.class));
    }
    
    @Test
    void createDefaultPreferences_ExistingUser_ThrowsException() {
        // Given
        when(preferenceRepository.existsByUserId("user123")).thenReturn(true);
        
        // When & Then
        assertThrows(IllegalArgumentException.class, 
                () -> preferenceService.createDefaultPreferences("user123", "user@example.com", "Test User"));
        
        verify(preferenceRepository).existsByUserId("user123");
        verify(preferenceRepository, never()).save(any(NotificationPreference.class));
    }
    
    @Test
    void updateSpecificPreference_ValidPreference_UpdatesSuccessfully() {
        // Given
        when(preferenceRepository.findByUserId("user123")).thenReturn(Optional.of(preference));
        when(preferenceRepository.save(any(NotificationPreference.class))).thenReturn(preference);
        
        // When
        NotificationPreferenceResponse response = preferenceService.updateSpecificPreference("user123", "email", false);
        
        // Then
        assertNotNull(response);
        
        verify(preferenceRepository).findByUserId("user123");
        verify(preferenceRepository).save(preference);
    }
    
    @Test
    void updateSpecificPreference_NonExistentUser_ThrowsException() {
        // Given
        when(preferenceRepository.findByUserId("nonexistent")).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(IllegalArgumentException.class, 
                () -> preferenceService.updateSpecificPreference("nonexistent", "email", false));
        
        verify(preferenceRepository).findByUserId("nonexistent");
        verify(preferenceRepository, never()).save(any(NotificationPreference.class));
    }
    
    @Test
    void shouldSendNotification_NoPreferences_ReturnsTrue() {
        // Given
        when(preferenceRepository.findByUserId("user123")).thenReturn(Optional.empty());
        
        // When
        boolean shouldSend = preferenceService.shouldSendNotification("user123", EmailNotification.NotificationType.APPLICATION_STATUS_UPDATE);
        
        // Then
        assertTrue(shouldSend);
        
        verify(preferenceRepository).findByUserId("user123");
    }
    
    @Test
    void shouldSendNotification_EmailDisabled_ReturnsFalse() {
        // Given
        preference.setEmailEnabled(false);
        when(preferenceRepository.findByUserId("user123")).thenReturn(Optional.of(preference));
        
        // When
        boolean shouldSend = preferenceService.shouldSendNotification("user123", EmailNotification.NotificationType.APPLICATION_STATUS_UPDATE);
        
        // Then
        assertFalse(shouldSend);
        
        verify(preferenceRepository).findByUserId("user123");
    }
    
    @Test
    void shouldSendNotification_SpecificTypeDisabled_ReturnsFalse() {
        // Given
        preference.setApplicationStatusUpdateEnabled(false);
        when(preferenceRepository.findByUserId("user123")).thenReturn(Optional.of(preference));
        
        // When
        boolean shouldSend = preferenceService.shouldSendNotification("user123", EmailNotification.NotificationType.APPLICATION_STATUS_UPDATE);
        
        // Then
        assertFalse(shouldSend);
        
        verify(preferenceRepository).findByUserId("user123");
    }
    
    @Test
    void shouldSendNotification_SpecificTypeEnabled_ReturnsTrue() {
        // Given
        preference.setApplicationStatusUpdateEnabled(true);
        when(preferenceRepository.findByUserId("user123")).thenReturn(Optional.of(preference));
        
        // When
        boolean shouldSend = preferenceService.shouldSendNotification("user123", EmailNotification.NotificationType.APPLICATION_STATUS_UPDATE);
        
        // Then
        assertTrue(shouldSend);
        
        verify(preferenceRepository).findByUserId("user123");
    }
    
    @Test
    void deletePreferences_ExistingUser_DeletesSuccessfully() {
        // Given
        when(preferenceRepository.existsByUserId("user123")).thenReturn(true);
        
        // When
        preferenceService.deletePreferences("user123");
        
        // Then
        verify(preferenceRepository).existsByUserId("user123");
        verify(preferenceRepository).deleteByUserId("user123");
    }
    
    @Test
    void deletePreferences_NonExistentUser_ThrowsException() {
        // Given
        when(preferenceRepository.existsByUserId("nonexistent")).thenReturn(false);
        
        // When & Then
        assertThrows(IllegalArgumentException.class, 
                () -> preferenceService.deletePreferences("nonexistent"));
        
        verify(preferenceRepository).existsByUserId("nonexistent");
        verify(preferenceRepository, never()).deleteByUserId(anyString());
    }
}