package com.jobapp.notification.controller;

import com.jobapp.notification.dto.NotificationPreferenceRequest;
import com.jobapp.notification.dto.NotificationPreferenceResponse;
import com.jobapp.notification.model.NotificationPreference.NotificationFrequency;
import com.jobapp.notification.service.NotificationPreferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications/preferences")
@Tag(name = "Notification Preferences", description = "Notification preference management endpoints")
public class NotificationPreferenceController {
    
    @Autowired
    private NotificationPreferenceService preferenceService;
    
    @PostMapping
    @Operation(summary = "Create or update notification preferences", 
               description = "Create new notification preferences or update existing ones for a user")
    public ResponseEntity<NotificationPreferenceResponse> createOrUpdatePreferences(
            @Valid @RequestBody NotificationPreferenceRequest request) {
        try {
            NotificationPreferenceResponse response = preferenceService.createOrUpdatePreferences(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get preferences by user ID", 
               description = "Retrieve notification preferences for a specific user")
    public ResponseEntity<NotificationPreferenceResponse> getPreferencesByUserId(
            @Parameter(description = "User ID") @PathVariable String userId) {
        Optional<NotificationPreferenceResponse> preferences = preferenceService.getPreferencesByUserId(userId);
        return preferences.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/email/{email}")
    @Operation(summary = "Get preferences by email", 
               description = "Retrieve notification preferences for a specific email address")
    public ResponseEntity<NotificationPreferenceResponse> getPreferencesByEmail(
            @Parameter(description = "Email address") @PathVariable String email) {
        Optional<NotificationPreferenceResponse> preferences = preferenceService.getPreferencesByEmail(email);
        return preferences.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/default")
    @Operation(summary = "Create default preferences", 
               description = "Create default notification preferences for a new user")
    public ResponseEntity<NotificationPreferenceResponse> createDefaultPreferences(
            @Parameter(description = "User ID") @RequestParam String userId,
            @Parameter(description = "User email") @RequestParam String userEmail,
            @Parameter(description = "User name") @RequestParam(required = false) String userName) {
        try {
            NotificationPreferenceResponse response = preferenceService.createDefaultPreferences(userId, userEmail, userName);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/user/{userId}/preference/{preferenceKey}")
    @Operation(summary = "Update specific preference", 
               description = "Update a specific notification preference for a user")
    public ResponseEntity<NotificationPreferenceResponse> updateSpecificPreference(
            @Parameter(description = "User ID") @PathVariable String userId,
            @Parameter(description = "Preference key") @PathVariable String preferenceKey,
            @Parameter(description = "Enable/disable preference") @RequestParam boolean enabled) {
        try {
            NotificationPreferenceResponse response = preferenceService.updateSpecificPreference(userId, preferenceKey, enabled);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/user/{userId}")
    @Operation(summary = "Delete preferences", 
               description = "Delete all notification preferences for a user")
    public ResponseEntity<Void> deletePreferences(
            @Parameter(description = "User ID") @PathVariable String userId) {
        try {
            preferenceService.deletePreferences(userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/enabled")
    @Operation(summary = "Get all enabled email preferences", 
               description = "Retrieve all users who have email notifications enabled")
    public ResponseEntity<List<NotificationPreferenceResponse>> getAllEnabledEmailPreferences() {
        try {
            List<NotificationPreferenceResponse> preferences = preferenceService.getAllEnabledEmailPreferences();
            return ResponseEntity.ok(preferences);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/frequency/{frequency}")
    @Operation(summary = "Get preferences by frequency", 
               description = "Retrieve all users with a specific notification frequency setting")
    public ResponseEntity<List<NotificationPreferenceResponse>> getPreferencesByFrequency(
            @Parameter(description = "Notification frequency") @PathVariable NotificationFrequency frequency) {
        try {
            List<NotificationPreferenceResponse> preferences = preferenceService.getPreferencesByFrequency(frequency);
            return ResponseEntity.ok(preferences);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/user/{userId}/check/{notificationType}")
    @Operation(summary = "Check if notification should be sent", 
               description = "Check if a specific notification type should be sent to a user")
    public ResponseEntity<Boolean> shouldSendNotification(
            @Parameter(description = "User ID") @PathVariable String userId,
            @Parameter(description = "Notification type") @PathVariable String notificationType) {
        try {
            // Convert string to enum
            com.jobapp.notification.model.EmailNotification.NotificationType type = 
                com.jobapp.notification.model.EmailNotification.NotificationType.valueOf(notificationType.toUpperCase());
            
            boolean shouldSend = preferenceService.shouldSendNotification(userId, type);
            return ResponseEntity.ok(shouldSend);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/user/{userId}/quiet-hours")
    @Operation(summary = "Check if user is in quiet hours", 
               description = "Check if the current time is within the user's quiet hours")
    public ResponseEntity<Boolean> isInQuietHours(
            @Parameter(description = "User ID") @PathVariable String userId) {
        try {
            boolean inQuietHours = preferenceService.isInQuietHours(userId);
            return ResponseEntity.ok(inQuietHours);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}