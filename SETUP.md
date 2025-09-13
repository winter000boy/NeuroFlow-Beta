# Job Application Platform - Setup Guide

## Prerequisites

Before setting up the Job Application Platform, ensure you have the following installed:

- **Java 17+** - Required for Spring Boot microservices
- **Node.js 18+** - Required for React/Next.js frontend
- **Docker & Docker Compose** - For containerized deployment
- **MongoDB 6.0+** - Database (can be run via Docker)
- **Redis 7+** - Caching layer (can be run via Docker)
- **Git** - Version control

## Quick Start with Docker

The fastest way to get the platform running is using Docker Compose:

### 1. Clone the Repository
```bash
git clone <repository-url>
cd job-application-platform
```

### 2. Environment Configuration
```bash
# Copy environment files
cp .env.example .env
cp frontend/.env.example frontend/.env.local

# Edit the .env files with your configuration
# Key variables to configure:
# - JWT_SECRET
# - MONGODB_URI
# - EMAIL_SERVICE credentials
# - CLOUD_STORAGE credentials
```

### 3. Start All Services
```bash
# Development environment
docker-compose up -d

# Production environment
docker-compose -f docker-compose.prod.yml up -d
```

### 4. Verify Installation
```bash
# Check service health
curl http://localhost:8081/actuator/health  # Auth Service
curl http://localhost:8082/actuator/health  # User Service
curl http://localhost:8083/actuator/health  # Job Service
curl http://localhost:8084/actuator/health  # Application Service
curl http://localhost:8085/actuator/health  # Notification Service

# Check frontend
curl http://localhost:3000
```

## Manual Setup (Development)

### Backend Services Setup

#### 1. Database Setup
```bash
# Start MongoDB
docker run -d --name mongodb -p 27017:27017 \
  -e MONGO_INITDB_ROOT_USERNAME=admin \
  -e MONGO_INITDB_ROOT_PASSWORD=password \
  mongo:6.0

# Start Redis
docker run -d --name redis -p 6379:6379 redis:7-alpine
```

#### 2. Build and Run Services
```bash
# Build all services
mvn clean install

# Run each service (in separate terminals)
cd auth-service && mvn spring-boot:run
cd user-service && mvn spring-boot:run
cd job-service && mvn spring-boot:run
cd application-service && mvn spring-boot:run
cd notification-service && mvn spring-boot:run
```

### Frontend Setup

#### 1. Install Dependencies
```bash
cd frontend
npm install
```

#### 2. Configure Environment
```bash
# Copy and edit environment file
cp .env.example .env.local

# Required variables:
NEXT_PUBLIC_API_URL=http://localhost:8080
NEXT_PUBLIC_APP_URL=http://localhost:3000
```

#### 3. Run Development Server
```bash
npm run dev
```

## Configuration Details

### Environment Variables

#### Backend Services (.env)
```env
# Database
MONGODB_URI=mongodb://admin:password@localhost:27017/jobapp
REDIS_URL=redis://localhost:6379

# Security
JWT_SECRET=your-super-secret-jwt-key-here
JWT_EXPIRATION=86400000

# Email Service
EMAIL_HOST=smtp.gmail.com
EMAIL_PORT=587
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=your-app-password

# Cloud Storage (AWS S3)
AWS_ACCESS_KEY_ID=your-access-key
AWS_SECRET_ACCESS_KEY=your-secret-key
AWS_REGION=us-east-1
AWS_S3_BUCKET=your-bucket-name

# Or Google Cloud Storage
GCS_PROJECT_ID=your-project-id
GCS_BUCKET_NAME=your-bucket-name
GOOGLE_APPLICATION_CREDENTIALS=path/to/service-account.json
```

#### Frontend (.env.local)
```env
NEXT_PUBLIC_API_URL=http://localhost:8080
NEXT_PUBLIC_APP_URL=http://localhost:3000
NEXT_PUBLIC_GOOGLE_ANALYTICS_ID=GA_MEASUREMENT_ID
```

### Database Initialization

The platform includes automatic database seeding for development:

```bash
# Run test data seeder
mvn test -Dtest=TestDataSeeder
```

This creates:
- Sample candidates, employers, and admin users
- Test job postings
- Sample applications
- Default configuration data

## Service Ports

| Service | Port | Health Check |
|---------|------|--------------|
| Auth Service | 8081 | /actuator/health |
| User Service | 8082 | /actuator/health |
| Job Service | 8083 | /actuator/health |
| Application Service | 8084 | /actuator/health |
| Notification Service | 8085 | /actuator/health |
| Frontend | 3000 | / |
| MongoDB | 27017 | - |
| Redis | 6379 | - |

## Testing the Setup

### 1. Backend API Tests
```bash
# Run all backend tests
mvn test

# Run specific service tests
cd auth-service && mvn test
cd user-service && mvn test
```

### 2. Frontend Tests
```bash
cd frontend

# Unit tests
npm run test

# E2E tests
npm run test:e2e

# Test coverage
npm run test:coverage
```

### 3. Integration Tests
```bash
# Run deployment validation
./scripts/validate-deployment.sh  # Linux/Mac
./scripts/validate-deployment.bat  # Windows
```

## Next Steps

After successful setup:

1. **Access the Application**: Navigate to http://localhost:3000
2. **Admin Access**: Use seeded admin credentials to access admin panel
3. **API Documentation**: Visit http://localhost:8081/swagger-ui.html for API docs
4. **Monitoring**: Check service health at respective /actuator/health endpoints

## Common Issues

See [TROUBLESHOOTING.md](./TROUBLESHOOTING.md) for common setup issues and solutions.