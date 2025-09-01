package com.jobapp.notification.service;

import com.jobapp.notification.dto.NotificationPreferenceRequest;
import com.jobapp.notification.dto.NotificationPreferenceResponse;
import com.jobapp.notification.model.NotificationPreference;
import com.jobapp.notification.repository.NotificationPreferenceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationPreferenceService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationPreferenceService.class);
    
    @Autowired
    private NotificationPreferenceRepository preferenceRepository;
    
    public NotificationPreferenceResponse createOrUpdatePreferences(NotificationPreferenceRequest request) {
        NotificationPreference preference = preferenceRepository.findByUserId(request.getUserId())
                .orElse(new NotificationPreference());
        
        // Update preference fields
        preference.setUserId(request.getUserId());
        preference.setUserEmail(request.getUserEmail());
        preference.setUserName(request.getUserName());
        preference.setEmailEnabled(request.isEmailEnabled());
        preference.setRegistrationConfirmationEnabled(request.isRegistrationConfirmationEnabled());
        preference.setApplicationStatusUpdateEnabled(request.isApplicationStatusUpdateEnabled());
        preference.setJobApplicationReceivedEnabled(request.isJobApplicationReceivedEnabled());
        preference.setEmployerApprovalEnabled(request.isEmployerApprovalEnabled());
        preference.setSystemNotificationEnabled(request.isSystemNotificationEnabled());
        preference.setEmailFrequency(request.getEmailFrequency());
        preference.setQuietHoursStart(request.getQuietHoursStart());
        preference.setQuietHoursEnd(request.getQuietHoursEnd());
        preference.setTimezone(request.getTimezone());
        preference.setCustomPreferences(request.getCustomPreferences());
        preference.updateTimestamp();
        
        preference = preferenceRepository.save(preference);
        logger.info("Updated notification preferences for user: {}", request.getUserId());
        
        return convertToResponse(preference);
    }
    
    public Optional<NotificationPreferenceResponse> getPreferencesByUserId(String userId) {
        return preferenceRepository.findByUserId(userId)
                .map(this::convertToResponse);
    }
    
    public Optional<NotificationPreferenceResponse> getPreferencesByEmail(String email) {
        return preferenceRepository.findByUserEmail(email)
                .map(this::convertToResponse);
    }
    
    public NotificationPreferenceResponse createDefaultPreferences(String userId, String userEmail, String userName) {
        if (preferenceRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("Preferences already exist for user: " + userId);
        }
        
        NotificationPreference preference = new NotificationPreference(userId, userEmail);
        preference.setUserName(userName);
        
        preference = preferenceRepository.save(preference);
        logger.info("Created default notification preferences for user: {}", userId);
        
        return convertToResponse(preference);
    }
    
    public NotificationPreferenceResponse updateSpecificPreference(String userId, String preferenceKey, boolean enabled) {
        NotificationPreference preference = preferenceRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Preferences not found for user: " + userId));
        
        // Update specific preference
        switch (preferenceKey.toLowerCase()) {
            case "email":
                preference.setEmailEnabled(enabled);
                break;
            case "registration_confirmation":
                preference.setRegistrationConfirmationEnabled(enabled);
                break;
            case "application_status_update":
                preference.setApplicationStatusUpdateEnabled(enabled);
                break;
            case "job_application_received":
                preference.setJobApplicationReceivedEnabled(enabled);
                break;
            case "employer_approval":
                preference.setEmployerApprovalEnabled(enabled);
                break;
            case "system_notification":
                preference.setSystemNotificationEnabled(enabled);
                break;
            default:
                // Handle custom preferences
                if (preference.getCustomPreferences() != null) {
                    preference.getCustomPreferences().put(preferenceKey, enabled);
                }
                break;
        }
        
        preference.updateTimestamp();
        preference = preferenceRepository.save(preference);
        
        logger.info("Updated preference '{}' to {} for user: {}", preferenceKey, enabled, userId);
        
        return convertToResponse(preference);
    }
    
    public void deletePreferences(String userId) {
        if (!preferenceRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("Preferences not found for user: " + userId);
        }
        
        preferenceRepository.deleteByUserId(userId);
        logger.info("Deleted notification preferences for user: {}", userId);
    }
    
    public List<NotificationPreferenceResponse> getAllEnabledEmailPreferences() {
        List<NotificationPreference> preferences = preferenceRepository.findByEmailEnabledTrue();
        return preferences.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<NotificationPreferenceResponse> getPreferencesByFrequency(NotificationPreference.NotificationFrequency frequency) {
        List<NotificationPreference> preferences = preferenceRepository.findByEmailFrequency(frequency);
        return preferences.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public boolean shouldSendNotification(String userId, com.jobapp.notification.model.EmailNotification.NotificationType type) {
        Optional<NotificationPreference> preferenceOpt = preferenceRepository.findByUserId(userId);
        
        if (preferenceOpt.isEmpty()) {
            // If no preferences exist, default to sending notifications
            return true;
        }
        
        NotificationPreference preference = preferenceOpt.get();
        
        // Check if email notifications are globally disabled
        if (!preference.isEmailEnabled()) {
            return false;
        }
        
        // Check specific notification type
        return preference.isNotificationTypeEnabled(type);
    }
    
    public boolean isInQuietHours(String userId) {
        Optional<NotificationPreference> preferenceOpt = preferenceRepository.findByUserId(userId);
        
        if (preferenceOpt.isEmpty()) {
            return false; // No quiet hours if no preferences
        }
        
        NotificationPreference preference = preferenceOpt.get();
        
        // For simplicity, using system time. In production, you'd convert to user's timezone
        int currentHour = java.time.LocalTime.now().getHour();
        
        int start = preference.getQuietHoursStart();
        int end = preference.getQuietHoursEnd();
        
        // Handle cases where quiet hours span midnight
        if (start <= end) {
            return currentHour >= start && currentHour < end;
        } else {
            return currentHour >= start || currentHour < end;
        }
    }
    
    private NotificationPreferenceResponse convertToResponse(NotificationPreference preference) {
        NotificationPreferenceResponse response = new NotificationPreferenceResponse();
        response.setId(preference.getId());
        response.setUserId(preference.getUserId());
        response.setUserEmail(preference.getUserEmail());
        response.setUserName(preference.getUserName());
        response.setEmailEnabled(preference.isEmailEnabled());
        response.setRegistrationConfirmationEnabled(preference.isRegistrationConfirmationEnabled());
        response.setApplicationStatusUpdateEnabled(preference.isApplicationStatusUpdateEnabled());
        response.setJobApplicationReceivedEnabled(preference.isJobApplicationReceivedEnabled());
        response.setEmployerApprovalEnabled(preference.isEmployerApprovalEnabled());
        response.setSystemNotificationEnabled(preference.isSystemNotificationEnabled());
        response.setEmailFrequency(preference.getEmailFrequency());
        response.setQuietHoursStart(preference.getQuietHoursStart());
        response.setQuietHoursEnd(preference.getQuietHoursEnd());
        response.setTimezone(preference.getTimezone());
        response.setCustomPreferences(preference.getCustomPreferences());
        response.setCreatedAt(preference.getCreatedAt());
        response.setUpdatedAt(preference.getUpdatedAt());
        return response;
    }
}