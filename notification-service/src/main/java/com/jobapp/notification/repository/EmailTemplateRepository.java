package com.jobapp.notification.repository;

import com.jobapp.notification.model.EmailTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface EmailTemplateRepository extends MongoRepository<EmailTemplate, String> {
    
    Optional<EmailTemplate> findByNameAndIsActive(String name, boolean isActive);
    
    Optional<EmailTemplate> findByName(String name);
    
    List<EmailTemplate> findByIsActive(boolean isActive);
    
    boolean existsByName(String name);
}