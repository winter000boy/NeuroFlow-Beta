package com.jobapp.notification.controller;

import com.jobapp.notification.dto.EmailRequest;
import com.jobapp.notification.dto.EmailResponse;
import com.jobapp.notification.model.EmailNotification;
import com.jobapp.notification.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications/email")
@Tag(name = "Email Notifications", description = "Email notification management endpoints with template support, scheduling, and retry mechanisms")
@SecurityRequirement(name = "Bearer Authentication")
public class EmailController {
    
    @Autowired
    private EmailService emailService;
    
    @PostMapping("/send")
    @Operation(
            summary = "Send email immediately", 
            description = "Send an email notification immediately using a predefined template. Supports registration confirmations, application status updates, and custom notifications."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Email sent successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EmailResponse.class),
                            examples = @ExampleObject(
                                    name = "Email sent",
                                    value = """
                                    {
                                        "id": "email123",
                                        "recipient": "user@example.com",
                                        "subject": "Application Status Update",
                                        "templateName": "application-status-update",
                                        "status": "SENT",
                                        "sentAt": "2024-01-15T10:30:00Z"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid email request data"),
            @ApiResponse(responseCode = "500", description = "Email service error")
    })
    public ResponseEntity<EmailResponse> sendEmail(
            @Parameter(description = "Email request with template and recipient details", required = true)
            @Valid @RequestBody EmailRequest emailRequest) {
        try {
            EmailResponse response = emailService.sendEmail(emailRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/send-async")
    @Operation(summary = "Send email asynchronously", description = "Queue an email to be sent asynchronously")
    public ResponseEntity<String> sendEmailAsync(@Valid @RequestBody EmailRequest emailRequest) {
        try {
            emailService.sendEmailAsync(emailRequest);
            return ResponseEntity.accepted().body("Email queued for sending");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to queue email");
        }
    }
    
    @PostMapping("/schedule")
    @Operation(summary = "Schedule email", description = "Schedule an email to be sent at a specific time")
    public ResponseEntity<EmailResponse> scheduleEmail(@Valid @RequestBody EmailRequest emailRequest) {
        try {
            EmailResponse response = emailService.scheduleEmail(emailRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{notificationId}")
    @Operation(summary = "Get notification by ID", description = "Retrieve a specific email notification by its ID")
    public ResponseEntity<EmailResponse> getNotification(
            @Parameter(description = "Notification ID") @PathVariable String notificationId) {
        Optional<EmailResponse> notification = emailService.getNotificationById(notificationId);
        return notification.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/recipient/{email}")
    @Operation(summary = "Get notifications by recipient", description = "Get all notifications for a specific email address")
    public ResponseEntity<Page<EmailResponse>> getNotificationsByRecipient(
            @Parameter(description = "Recipient email address") @PathVariable String email,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<EmailResponse> notifications = emailService.getNotificationsByRecipient(email, pageable);
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get notifications by user ID", description = "Get all notifications for a specific user")
    public ResponseEntity<List<EmailResponse>> getNotificationsByUser(
            @Parameter(description = "User ID") @PathVariable String userId) {
        List<EmailResponse> notifications = emailService.getNotificationsByUser(userId);
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get notifications by status", description = "Get all notifications with a specific status")
    public ResponseEntity<Page<EmailResponse>> getNotificationsByStatus(
            @Parameter(description = "Notification status") @PathVariable EmailNotification.NotificationStatus status,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<EmailResponse> notifications = emailService.getNotificationsByStatus(status, pageable);
        return ResponseEntity.ok(notifications);
    }
    
    @PutMapping("/{notificationId}/cancel")
    @Operation(summary = "Cancel notification", description = "Cancel a pending or scheduled email notification")
    public ResponseEntity<EmailResponse> cancelNotification(
            @Parameter(description = "Notification ID") @PathVariable String notificationId) {
        try {
            EmailResponse response = emailService.cancelNotification(notificationId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/process-scheduled")
    @Operation(summary = "Process scheduled emails", description = "Manually trigger processing of scheduled emails")
    public ResponseEntity<String> processScheduledEmails() {
        try {
            emailService.processScheduledEmails();
            return ResponseEntity.ok("Scheduled emails processed");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process scheduled emails");
        }
    }
    
    @PostMapping("/retry-failed")
    @Operation(summary = "Retry failed emails", description = "Manually trigger retry of failed email notifications")
    public ResponseEntity<String> retryFailedEmails() {
        try {
            emailService.retryFailedEmails();
            return ResponseEntity.ok("Failed emails retry initiated");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retry failed emails");
        }
    }
}