# Job Application Platform

A full-stack microservices-based job application platform built with Spring Boot, MongoDB, and React.

## Architecture

The platform consists of 5 microservices:

- **Auth Service** (Port 8081) - Authentication and authorization
- **User Service** (Port 8082) - User profile management
- **Job Service** (Port 8083) - Job posting and search
- **Application Service** (Port 8084) - Job application management
- **Notification Service** (Port 8085) - Email notifications

## Prerequisites

- Java 17+
- Maven 3.6+
- Docker and Docker Compose
- MongoDB 6.0+
- Redis 7+

## Quick Start

### Using Docker Compose (Recommended)

1. Clone the repository
2. Copy environment variables:
   ```bash
   cp .env.example .env
   ```
3. Update the `.env` file with your configuration
4. Build and start all services:
   ```bash
   docker-compose up --build
   ```

### Manual Setup

1. Start MongoDB and Redis:
   ```bash
   docker run -d --name mongodb -p 27017:27017 -e MONGO_INITDB_ROOT_USERNAME=admin -e MONGO_INITDB_ROOT_PASSWORD=password123 mongo:6.0
   docker run -d --name redis -p 6379:6379 redis:7-alpine
   ```

2. Build all services:
   ```bash
   mvn clean install
   ```

3. Start each service:
   ```bash
   # Terminal 1 - Auth Service
   cd auth-service && mvn spring-boot:run
   
   # Terminal 2 - User Service
   cd user-service && mvn spring-boot:run
   
   # Terminal 3 - Job Service
   cd job-service && mvn spring-boot:run
   
   # Terminal 4 - Application Service
   cd application-service && mvn spring-boot:run
   
   # Terminal 5 - Notification Service
   cd notification-service && mvn spring-boot:run
   ```

## API Documentation

Once the services are running, you can access the Swagger UI for each service:

- Auth Service: http://localhost:8081/swagger-ui.html
- User Service: http://localhost:8082/swagger-ui.html
- Job Service: http://localhost:8083/swagger-ui.html
- Application Service: http://localhost:8084/swagger-ui.html
- Notification Service: http://localhost:8085/swagger-ui.html

## Health Checks

Each service provides health check endpoints:

- Auth Service: http://localhost:8081/actuator/health
- User Service: http://localhost:8082/actuator/health
- Job Service: http://localhost:8083/actuator/health
- Application Service: http://localhost:8084/actuator/health
- Notification Service: http://localhost:8085/actuator/health

## Configuration

### Environment Variables

Key environment variables that need to be configured:

- `MONGODB_URI` - MongoDB connection string
- `JWT_SECRET` - Secret key for JWT token generation
- `AWS_ACCESS_KEY`, `AWS_SECRET_KEY` - AWS credentials for S3 file storage
- `SMTP_HOST`, `SMTP_USERNAME`, `SMTP_PASSWORD` - Email configuration

### Database Setup

The MongoDB initialization script (`scripts/mongo-init.js`) automatically creates:
- Required databases for each service
- Necessary indexes for optimal performance
- Unique constraints for data integrity

## Development

### Building Individual Services

```bash
# Build specific service
cd auth-service
mvn clean package

# Run tests
mvn test

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Logs

Application logs are stored in the `logs/` directory for each service when running with Docker Compose.

## Next Steps

After setting up the infrastructure:

1. Implement JWT authentication service (Task 2.1)
2. Create data models and repositories (Task 3)
3. Build user registration endpoints (Task 4)
4. Develop job posting functionality (Task 5)

## Support

For issues and questions, please refer to the project documentation or create an issue in the repository.