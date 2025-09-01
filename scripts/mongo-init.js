// MongoDB initialization script for Job Application Platform

// Create databases for each microservice
db = db.getSiblingDB('jobapp_auth');
db.createCollection('candidates');
db.createCollection('employers');
db.createCollection('admins');

// Create indexes for auth database
db.candidates.createIndex({ "email": 1 }, { unique: true });
db.employers.createIndex({ "email": 1 }, { unique: true });
db.admins.createIndex({ "email": 1 }, { unique: true });

db = db.getSiblingDB('jobapp_users');
db.createCollection('candidate_profiles');
db.createCollection('employer_profiles');

db = db.getSiblingDB('jobapp_jobs');
db.createCollection('jobs');

// Create text search indexes for jobs
db.jobs.createIndex({ 
  "title": "text", 
  "description": "text" 
});

// Create compound indexes for efficient queries
db.jobs.createIndex({ "employerId": 1, "isActive": 1 });
db.jobs.createIndex({ "location": 1, "jobType": 1, "isActive": 1 });

db = db.getSiblingDB('jobapp_applications');
db.createCollection('applications');

// Create indexes for applications
db.applications.createIndex({ "candidateId": 1, "status": 1 });
db.applications.createIndex({ "jobId": 1, "status": 1 });
db.applications.createIndex({ "employerId": 1, "status": 1 });
db.applications.createIndex({ "candidateId": 1, "jobId": 1 }, { unique: true });

db = db.getSiblingDB('jobapp_notifications');
db.createCollection('notifications');
db.createCollection('email_templates');

print('MongoDB databases and collections initialized successfully!');