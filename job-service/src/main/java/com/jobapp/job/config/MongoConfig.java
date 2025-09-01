package com.jobapp.job.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.data.domain.Sort;

import com.jobapp.job.model.Job;

import jakarta.annotation.PostConstruct;

/**
 * MongoDB configuration for setting up indexes in job-service
 * Requirements: 2.1, 2.2, 4.1, 4.2 - Text search indexes and optimization
 */
@Configuration
public class MongoConfig {
    
    private final MongoTemplate mongoTemplate;
    
    public MongoConfig(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    
    /**
     * Set up MongoDB indexes for Job collection
     */
    @PostConstruct
    public void setupIndexes() {
        setupJobIndexes();
    }
    
    private void setupJobIndexes() {
        IndexOperations indexOps = mongoTemplate.indexOps(Job.class);
        
        // Text search index on title and description with weights
        TextIndexDefinition textIndex = new TextIndexDefinition.TextIndexDefinitionBuilder()
                .onField("title", 2.0f) // Higher weight for title
                .onField("description", 1.0f)
                .build();
        indexOps.ensureIndex(textIndex);
        
        // Index on employer_id for employer-specific queries
        IndexDefinition employerIndex = new Index()
                .on("employer_id", Sort.Direction.ASC);
        indexOps.ensureIndex(employerIndex);
        
        // Index on is_active for filtering active jobs
        IndexDefinition activeIndex = new Index()
                .on("is_active", Sort.Direction.ASC);
        indexOps.ensureIndex(activeIndex);
        
        // Compound index on employer_id and is_active
        IndexDefinition employerActiveIndex = new Index()
                .on("employer_id", Sort.Direction.ASC)
                .on("is_active", Sort.Direction.ASC);
        indexOps.ensureIndex(employerActiveIndex);
        
        // Index on location for location-based searches
        IndexDefinition locationIndex = new Index()
                .on("location", Sort.Direction.ASC);
        indexOps.ensureIndex(locationIndex);
        
        // Index on job_type for job type filtering
        IndexDefinition jobTypeIndex = new Index()
                .on("job_type", Sort.Direction.ASC);
        indexOps.ensureIndex(jobTypeIndex);
        
        // Index on experience_level for experience filtering
        IndexDefinition experienceIndex = new Index()
                .on("experience_level", Sort.Direction.ASC);
        indexOps.ensureIndex(experienceIndex);
        
        // Index on created_at for date-based queries and sorting
        IndexDefinition createdAtIndex = new Index()
                .on("created_at", Sort.Direction.DESC);
        indexOps.ensureIndex(createdAtIndex);
        
        // Index on updated_at for tracking recent updates
        IndexDefinition updatedAtIndex = new Index()
                .on("updated_at", Sort.Direction.DESC);
        indexOps.ensureIndex(updatedAtIndex);
        
        // Index on expires_at for expiration queries
        IndexDefinition expiresAtIndex = new Index()
                .on("expires_at", Sort.Direction.ASC)
                .sparse();
        indexOps.ensureIndex(expiresAtIndex);
        
        // Index on application_deadline for deadline queries
        IndexDefinition deadlineIndex = new Index()
                .on("application_deadline", Sort.Direction.ASC)
                .sparse();
        indexOps.ensureIndex(deadlineIndex);
        
        // Compound index for active jobs with location and job type
        IndexDefinition activeLocationTypeIndex = new Index()
                .on("is_active", Sort.Direction.ASC)
                .on("location", Sort.Direction.ASC)
                .on("job_type", Sort.Direction.ASC);
        indexOps.ensureIndex(activeLocationTypeIndex);
        
        // Index on salary.min for salary range queries
        IndexDefinition salaryMinIndex = new Index()
                .on("salary.min", Sort.Direction.ASC)
                .sparse();
        indexOps.ensureIndex(salaryMinIndex);
        
        // Index on salary.max for salary range queries
        IndexDefinition salaryMaxIndex = new Index()
                .on("salary.max", Sort.Direction.ASC)
                .sparse();
        indexOps.ensureIndex(salaryMaxIndex);
        
        // Index on is_featured for featured jobs
        IndexDefinition featuredIndex = new Index()
                .on("is_featured", Sort.Direction.ASC);
        indexOps.ensureIndex(featuredIndex);
        
        // Compound index for featured active jobs
        IndexDefinition featuredActiveIndex = new Index()
                .on("is_featured", Sort.Direction.ASC)
                .on("is_active", Sort.Direction.ASC)
                .on("created_at", Sort.Direction.DESC);
        indexOps.ensureIndex(featuredActiveIndex);
        
        // Index on application_count for popular jobs
        IndexDefinition applicationCountIndex = new Index()
                .on("application_count", Sort.Direction.DESC);
        indexOps.ensureIndex(applicationCountIndex);
        
        // Index on view_count for trending jobs
        IndexDefinition viewCountIndex = new Index()
                .on("view_count", Sort.Direction.DESC);
        indexOps.ensureIndex(viewCountIndex);
        
        // Index on required_skills for skill-based searches
        IndexDefinition skillsIndex = new Index()
                .on("required_skills", Sort.Direction.ASC);
        indexOps.ensureIndex(skillsIndex);
        
        // Compound index for comprehensive job search
        IndexDefinition searchIndex = new Index()
                .on("is_active", Sort.Direction.ASC)
                .on("location", Sort.Direction.ASC)
                .on("job_type", Sort.Direction.ASC)
                .on("experience_level", Sort.Direction.ASC)
                .on("created_at", Sort.Direction.DESC);
        indexOps.ensureIndex(searchIndex);
    }
}