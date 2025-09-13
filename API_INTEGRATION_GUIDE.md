# API Integration Guide

## Overview

The Job Application Platform provides RESTful APIs across multiple microservices. This guide covers authentication, API usage patterns, and integration examples.

## Base URLs

| Service | Development | Production |
|---------|-------------|------------|
| Auth Service | http://localhost:8081 | https://api.yourapp.com/auth |
| User Service | http://localhost:8082 | https://api.yourapp.com/users |
| Job Service | http://localhost:8083 | https://api.yourapp.com/jobs |
| Application Service | http://localhost:8084 | https://api.yourapp.com/applications |
| Notification Service | http://localhost:8085 | https://api.yourapp.com/notifications |

## Authentication

### 1. User Registration

#### Candidate Registration
```bash
curl -X POST http://localhost:8081/api/auth/register/candidate \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john.doe@example.com",
    "password": "SecurePassword123!",
    "phone": "+1234567890",
    "degree": "Computer Science",
    "graduationYear": 2022
  }'
```

#### Employer Registration
```bash
curl -X POST http://localhost:8081/api/auth/register/employer \
  -H "Content-Type: application/json" \
  -d '{
    "companyName": "Tech Corp",
    "email": "hr@techcorp.com",
    "password": "SecurePassword123!",
    "website": "https://techcorp.com",
    "description": "Leading technology company"
  }'
```

### 2. User Login
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "SecurePassword123!"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": "64a1b2c3d4e5f6789012345",
  "email": "john.doe@example.com",
  "roles": ["ROLE_CANDIDATE"],
  "expiresIn": 86400
}
```

### 3. Token Refresh
```bash
curl -X POST http://localhost:8081/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "your-refresh-token-here"
  }'
```

## API Usage Patterns

### Authentication Headers
All protected endpoints require the JWT token in the Authorization header:

```bash
curl -X GET http://localhost:8082/api/users/profile \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### Error Handling
All APIs return consistent error responses:

```json
{
  "code": "VALIDATION_ERROR",
  "message": "Email is already registered",
  "timestamp": "2024-01-15T10:30:00Z",
  "details": {
    "field": "email",
    "rejectedValue": "john.doe@example.com"
  }
}
```

## User Management APIs

### 1. Get User Profile
```bash
curl -X GET http://localhost:8082/api/users/profile \
  -H "Authorization: Bearer {token}"
```

### 2. Update Profile
```bash
curl -X PUT http://localhost:8082/api/users/profile \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Smith",
    "phone": "+1234567891",
    "linkedinProfile": "https://linkedin.com/in/johnsmith",
    "portfolioUrl": "https://johnsmith.dev"
  }'
```

### 3. Upload Resume (Candidates)
```bash
curl -X POST http://localhost:8082/api/users/upload/resume \
  -H "Authorization: Bearer {token}" \
  -F "file=@/path/to/resume.pdf"
```

### 4. Upload Company Logo (Employers)
```bash
curl -X POST http://localhost:8082/api/users/upload/logo \
  -H "Authorization: Bearer {token}" \
  -F "file=@/path/to/logo.png"
```

## Job Management APIs

### 1. Search Jobs (Public)
```bash
# Basic search
curl -X GET "http://localhost:8083/api/jobs?page=0&size=10"

# Advanced search with filters
curl -X GET "http://localhost:8083/api/jobs?search=software%20engineer&location=San%20Francisco&jobType=FULL_TIME&minSalary=80000&maxSalary=120000&page=0&size=10"
```

**Response:**
```json
{
  "content": [
    {
      "id": "64a1b2c3d4e5f6789012345",
      "title": "Senior Software Engineer",
      "company": "Tech Corp",
      "location": "San Francisco, CA",
      "jobType": "FULL_TIME",
      "salary": {
        "min": 100000,
        "max": 150000,
        "currency": "USD"
      },
      "postedDate": "2024-01-15T10:30:00Z",
      "isActive": true
    }
  ],
  "totalElements": 25,
  "totalPages": 3,
  "size": 10,
  "number": 0
}
```

### 2. Get Job Details
```bash
curl -X GET http://localhost:8083/api/jobs/64a1b2c3d4e5f6789012345
```

### 3. Create Job (Employers)
```bash
curl -X POST http://localhost:8083/api/jobs \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Frontend Developer",
    "description": "We are looking for a skilled Frontend Developer...",
    "location": "Remote",
    "jobType": "FULL_TIME",
    "salary": {
      "min": 70000,
      "max": 90000,
      "currency": "USD"
    },
    "requirements": ["React", "TypeScript", "3+ years experience"]
  }'
```

### 4. Update Job Status
```bash
curl -X PATCH http://localhost:8083/api/jobs/64a1b2c3d4e5f6789012345/status \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "isActive": false
  }'
```

### 5. Get Employer Jobs
```bash
curl -X GET http://localhost:8083/api/jobs/employer/my-jobs \
  -H "Authorization: Bearer {token}"
```

## Application Management APIs

### 1. Apply for Job (Candidates)
```bash
curl -X POST http://localhost:8084/api/applications \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "jobId": "64a1b2c3d4e5f6789012345",
    "coverLetter": "I am very interested in this position..."
  }'
```

### 2. Get Candidate Applications
```bash
curl -X GET http://localhost:8084/api/applications/my-applications \
  -H "Authorization: Bearer {token}"
```

### 3. Get Job Applicants (Employers)
```bash
curl -X GET http://localhost:8084/api/applications/job/64a1b2c3d4e5f6789012345/applicants \
  -H "Authorization: Bearer {token}"
```

### 4. Update Application Status (Employers)
```bash
curl -X PATCH http://localhost:8084/api/applications/64a1b2c3d4e5f6789012345/status \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "IN_REVIEW",
    "notes": "Candidate looks promising, scheduling interview"
  }'
```

## Admin APIs

### 1. Get All Users
```bash
curl -X GET http://localhost:8082/api/admin/users \
  -H "Authorization: Bearer {admin-token}"
```

### 2. Approve Employer
```bash
curl -X PATCH http://localhost:8082/api/admin/employers/64a1b2c3d4e5f6789012345/approve \
  -H "Authorization: Bearer {admin-token}"
```

### 3. Get Platform Analytics
```bash
curl -X GET http://localhost:8082/api/admin/analytics \
  -H "Authorization: Bearer {admin-token}"
```

## Notification APIs

### 1. Get Notifications
```bash
curl -X GET http://localhost:8085/api/notifications \
  -H "Authorization: Bearer {token}"
```

### 2. Mark Notification as Read
```bash
curl -X PATCH http://localhost:8085/api/notifications/64a1b2c3d4e5f6789012345/read \
  -H "Authorization: Bearer {token}"
```

### 3. Update Notification Preferences
```bash
curl -X PUT http://localhost:8085/api/notifications/preferences \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "emailNotifications": true,
    "applicationUpdates": true,
    "jobRecommendations": false
  }'
```

## JavaScript/TypeScript Integration

### API Client Setup
```typescript
// api-client.ts
import axios from 'axios';

const apiClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080',
  timeout: 10000,
});

// Request interceptor to add auth token
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('authToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Response interceptor for error handling
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('authToken');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default apiClient;
```

### Service Examples
```typescript
// auth.service.ts
export class AuthService {
  static async login(email: string, password: string) {
    const response = await apiClient.post('/api/auth/login', {
      email,
      password,
    });
    
    const { token } = response.data;
    localStorage.setItem('authToken', token);
    return response.data;
  }

  static async register(userData: RegisterRequest) {
    return apiClient.post('/api/auth/register/candidate', userData);
  }

  static logout() {
    localStorage.removeItem('authToken');
  }
}

// job.service.ts
export class JobService {
  static async searchJobs(params: JobSearchParams) {
    const response = await apiClient.get('/api/jobs', { params });
    return response.data;
  }

  static async getJobDetails(jobId: string) {
    const response = await apiClient.get(`/api/jobs/${jobId}`);
    return response.data;
  }

  static async applyForJob(jobId: string, coverLetter?: string) {
    return apiClient.post('/api/applications', {
      jobId,
      coverLetter,
    });
  }
}
```

## Rate Limiting

The API implements rate limiting to prevent abuse:

- **Authentication endpoints**: 5 requests per minute per IP
- **Search endpoints**: 100 requests per minute per user
- **File upload endpoints**: 10 requests per minute per user
- **General endpoints**: 1000 requests per hour per user

Rate limit headers are included in responses:
```
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 95
X-RateLimit-Reset: 1642694400
```

## Pagination

List endpoints support pagination with consistent parameters:

```bash
curl -X GET "http://localhost:8083/api/jobs?page=0&size=20&sort=createdAt,desc"
```

Response includes pagination metadata:
```json
{
  "content": [...],
  "totalElements": 150,
  "totalPages": 8,
  "size": 20,
  "number": 0,
  "first": true,
  "last": false
}
```

## WebSocket Integration (Real-time Notifications)

### Connect to WebSocket
```javascript
const socket = new WebSocket('ws://localhost:8085/ws/notifications');

socket.onopen = () => {
  // Send authentication
  socket.send(JSON.stringify({
    type: 'auth',
    token: localStorage.getItem('authToken')
  }));
};

socket.onmessage = (event) => {
  const notification = JSON.parse(event.data);
  // Handle real-time notification
  console.log('New notification:', notification);
};
```

## Testing APIs

### Postman Collection
Import the provided Postman collection (`postman/Job-Application-Platform.json`) for easy API testing.

### cURL Scripts
Use the provided test scripts:
```bash
# Test all endpoints
./scripts/test-api-endpoints.sh

# Test specific service
./scripts/test-auth-service.sh
```

## API Versioning

APIs are versioned using URL path versioning:
- Current version: `/api/v1/`
- Future versions: `/api/v2/`

Backward compatibility is maintained for at least one major version.

## Support

For API integration support:
- Check the [Troubleshooting Guide](./TROUBLESHOOTING.md)
- Review [API Documentation](http://localhost:8081/swagger-ui.html)
- Contact the development team