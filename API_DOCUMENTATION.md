# Job Application Platform - API Documentation

This document provides comprehensive information about the API documentation setup for the Job Application Platform microservices.

## Overview

The Job Application Platform consists of 5 microservices, each with comprehensive OpenAPI/Swagger documentation:

1. **Auth Service** (Port 8081) - Authentication and authorization
2. **User Service** (Port 8082) - User profile management
3. **Job Service** (Port 8083) - Job posting and search
4. **Application Service** (Port 8084) - Job application management
5. **Notification Service** (Port 8085) - Email notifications

## Accessing API Documentation

### Swagger UI Endpoints

Each microservice provides interactive Swagger UI documentation:

- **Auth Service**: http://localhost:8081/swagger-ui.html
- **User Service**: http://localhost:8082/swagger-ui.html
- **Job Service**: http://localhost:8083/swagger-ui.html
- **Application Service**: http://localhost:8084/swagger-ui.html
- **Notification Service**: http://localhost:8085/swagger-ui.html

### OpenAPI JSON Endpoints

Raw OpenAPI specifications are available at:

- **Auth Service**: http://localhost:8081/api-docs
- **User Service**: http://localhost:8082/api-docs
- **Job Service**: http://localhost:8083/api-docs
- **Application Service**: http://localhost:8084/api-docs
- **Notification Service**: http://localhost:8085/api-docs

## API Documentation Features

### 1. Comprehensive Endpoint Documentation

Each API endpoint includes:
- **Operation Summary**: Brief description of the endpoint
- **Detailed Description**: Comprehensive explanation of functionality
- **Parameters**: All query parameters, path variables, and request bodies
- **Request Examples**: Sample JSON payloads
- **Response Examples**: Sample response structures
- **Error Responses**: All possible error codes and messages
- **Security Requirements**: Authentication and authorization details

### 2. Security Documentation

All services implement JWT-based authentication with:
- **Bearer Token Authentication**: JWT tokens for API access
- **Role-Based Access Control**: CANDIDATE, EMPLOYER, ADMIN roles
- **Security Schemes**: Properly documented in OpenAPI specification
- **Protected Endpoints**: Clear indication of authentication requirements

### 3. Interactive Testing

Swagger UI provides:
- **Try It Out**: Test endpoints directly from the documentation
- **Request Builder**: Interactive form to build API requests
- **Response Viewer**: Real-time response display
- **Authentication**: Built-in JWT token management

## Service-Specific Documentation

### Auth Service API

**Base URL**: http://localhost:8081/api/auth

**Key Endpoints**:
- `POST /login` - User authentication
- `POST /register` - User registration
- `POST /refresh` - Token refresh
- `GET /check-email` - Email availability check

**Features**:
- JWT token generation and validation
- Role-based user registration
- Password encryption with BCrypt
- Email validation

### User Service API

**Base URL**: http://localhost:8082/api

**Key Endpoints**:
- `GET /candidates/{id}` - Get candidate profile
- `PUT /candidates/{id}` - Update candidate profile
- `GET /employers/{id}` - Get employer profile
- `PUT /employers/{id}` - Update employer profile
- `POST /files/upload/resume` - Upload resume
- `POST /files/upload/logo` - Upload company logo

**Features**:
- Profile management for candidates and employers
- File upload with cloud storage integration
- Admin user management
- Social links validation

### Job Service API

**Base URL**: http://localhost:8083/api/jobs

**Key Endpoints**:
- `POST /` - Create job posting (Employer only)
- `GET /search` - Search jobs (Public)
- `GET /{jobId}` - Get job details (Public)
- `GET /employer` - Get employer's jobs (Employer only)
- `PUT /{jobId}/activate` - Activate job (Employer only)

**Features**:
- Full-text search with MongoDB
- Advanced filtering (location, job type, salary)
- Redis caching for performance
- Public and protected endpoints
- Job expiration handling

### Application Service API

**Base URL**: http://localhost:8084/api/applications

**Key Endpoints**:
- `POST /` - Submit job application (Candidate only)
- `GET /candidate/my-applications` - Get candidate's applications
- `GET /employer/my-applications` - Get employer's applications
- `PUT /{applicationId}/status` - Update application status (Employer only)
- `GET /candidate/statistics` - Get application statistics

**Features**:
- Application status tracking
- Duplicate application prevention
- Role-based access control
- Application analytics
- Status change notifications

### Notification Service API

**Base URL**: http://localhost:8085/api/notifications

**Key Endpoints**:
- `POST /email/send` - Send immediate email
- `POST /email/send-async` - Queue email for sending
- `POST /email/schedule` - Schedule email
- `GET /preferences` - Get notification preferences
- `PUT /preferences` - Update notification preferences

**Features**:
- Email template system
- Asynchronous email processing
- Email scheduling
- Notification preferences
- Retry mechanisms for failed emails

## API Groups and Organization

Each service organizes endpoints into logical groups:

### Auth Service Groups
- **Authentication**: Login, registration, token management
- **Actuator**: Health checks and monitoring

### User Service Groups
- **Candidates**: Candidate profile management
- **Employers**: Employer profile management
- **File Upload**: Resume and logo upload
- **Admin**: User administration

### Job Service Groups
- **Job Management**: CRUD operations for jobs
- **Job Search**: Public search and filtering
- **Company**: Company-specific endpoints

### Application Service Groups
- **Applications**: Application management
- **Analytics**: Application statistics

### Notification Service Groups
- **Email**: Email sending and management
- **Preferences**: Notification preferences
- **Queue**: Email queue management

## Authentication in Swagger UI

To test protected endpoints in Swagger UI:

1. **Get JWT Token**: Use the `/api/auth/login` endpoint to get a token
2. **Authorize**: Click the "Authorize" button in Swagger UI
3. **Enter Token**: Paste the JWT token in the format: `Bearer <your-token>`
4. **Test Endpoints**: All protected endpoints will now include the authorization header

## Example API Calls

### Authentication
```bash
# Login
curl -X POST "http://localhost:8081/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

### Job Search
```bash
# Search jobs
curl -X GET "http://localhost:8083/api/jobs/search?search=software&location=San Francisco&page=0&size=10"
```

### Submit Application
```bash
# Submit job application (requires authentication)
curl -X POST "http://localhost:8084/api/applications" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your-jwt-token>" \
  -d '{
    "jobId": "job123",
    "coverLetter": "I am interested in this position..."
  }'
```

## Testing API Documentation

Each service includes comprehensive tests to ensure API documentation accuracy:

- **Endpoint Availability**: Tests verify all documented endpoints exist
- **Schema Validation**: Ensures request/response schemas match documentation
- **Security Configuration**: Validates authentication requirements
- **Example Accuracy**: Tests example requests and responses
- **Parameter Validation**: Verifies all parameters are properly documented

Run documentation tests:
```bash
# Test all services
mvn test -Dtest=ApiDocumentationTest

# Test specific service
cd auth-service
mvn test -Dtest=ApiDocumentationTest
```

## Configuration

### Swagger Configuration

Each service includes comprehensive Swagger configuration in `application.yml`:

```yaml
springdoc:
  api-docs:
    path: /api-docs
    enabled: true
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
    try-it-out-enabled: true
    operations-sorter: method
    tags-sorter: alpha
    display-request-duration: true
  show-actuator: true
  group-configs:
    - group: 'main-api'
      display-name: 'Main API'
      paths-to-match: '/api/**'
```

### OpenAPI Configuration

Each service has a dedicated `OpenApiConfig` class with:
- Service-specific metadata
- Security scheme definitions
- Server information
- Contact and license details

## Best Practices

### 1. Documentation Standards
- **Clear Descriptions**: Every endpoint has comprehensive descriptions
- **Example Values**: All parameters include example values
- **Error Documentation**: All possible error responses are documented
- **Security Clarity**: Authentication requirements are clearly marked

### 2. API Design
- **RESTful Conventions**: Consistent use of HTTP methods and status codes
- **Resource Naming**: Clear, hierarchical resource naming
- **Pagination**: Consistent pagination across all list endpoints
- **Filtering**: Standardized query parameter naming

### 3. Security
- **JWT Authentication**: Consistent token-based authentication
- **Role-Based Access**: Clear role requirements for each endpoint
- **Input Validation**: Comprehensive request validation
- **Error Handling**: Secure error messages without sensitive information

## Troubleshooting

### Common Issues

1. **Swagger UI Not Loading**
   - Verify service is running on correct port
   - Check application.yml configuration
   - Ensure springdoc dependency is included

2. **Authentication Errors**
   - Verify JWT token format: `Bearer <token>`
   - Check token expiration
   - Ensure correct role permissions

3. **API Documentation Missing**
   - Verify controller annotations are present
   - Check OpenAPI configuration
   - Ensure endpoints are properly mapped

### Support

For API documentation issues:
1. Check service logs for errors
2. Verify OpenAPI configuration
3. Test endpoints directly with curl or Postman
4. Review controller annotations and documentation

## Future Enhancements

Planned improvements to API documentation:
- **API Versioning**: Support for multiple API versions
- **Rate Limiting**: Documentation of rate limits
- **Webhooks**: Documentation for webhook endpoints
- **SDK Generation**: Auto-generated client SDKs
- **Postman Collections**: Exportable Postman collections