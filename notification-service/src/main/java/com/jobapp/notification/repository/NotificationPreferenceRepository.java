package com.jobapp.notification.repository;

import com.jobapp.notification.model.NotificationPreference;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface NotificationPreferenceRepository extends MongoRepository<NotificationPreference, String> {
    
    Optional<NotificationPreference> findByUserId(String userId);
    
    Optional<NotificationPreference> findByUserEmail(String userEmail);
    
    List<NotificationPreference> findByEmailEnabledTrue();
    
    List<NotificationPreference> findByEmailFrequency(NotificationPreference.NotificationFrequency frequency);
    
    boolean existsByUserId(String userId);
    
    boolean existsByUserEmail(String userEmail);
    
    void deleteByUserId(String userId);
}