# Job Application Platform

A comprehensive full-stack microservices-based job application platform that connects job seekers with employers. Built with Spring Boot microservices, MongoDB, Redis, and React/Next.js frontend.

## 🚀 Quick Start

Get the platform running in minutes:

```bash
# Clone and setup
git clone https://github.com/winter000boy/NeuroFlow-Beta.git
cd job-application-platform
cp .env.example .env

# Start with Docker (recommended)
docker-compose up -d

# Verify services are running
curl http://localhost:3000  # Frontend
curl http://localhost:8081/actuator/health  # Backend services
```

**🎯 Access the Platform:**
- **Frontend**: http://localhost:3000
- **API Documentation**: http://localhost:8081/swagger-ui.html
- **Admin Panel**: http://localhost:3000/admin

## 📚 Documentation

### 📖 Setup and Installation
- **[Setup Guide](./SETUP.md)** - Complete installation instructions
- **[Deployment Guide](./DEPLOYMENT.md)** - Production deployment
- **[Testing Guide](./TESTING.md)** - Running tests and quality assurance

### 🔧 Development and Integration
- **[API Integration Guide](./API_INTEGRATION_GUIDE.md)** - REST API usage and examples
- **[Troubleshooting Guide](./TROUBLESHOOTING.md)** - Common issues and solutions
- **[API Documentation](./API_DOCUMENTATION.md)** - Detailed API reference

### 👥 User Guides
- **[Candidate Manual](./USER_MANUAL_CANDIDATE.md)** - Guide for job seekers
- **[Employer Manual](./USER_MANUAL_EMPLOYER.md)** - Guide for hiring managers
- **[Admin Manual](./USER_MANUAL_ADMIN.md)** - Platform administration guide
- **[FAQ](./FAQ.md)** - Frequently asked questions

## 🏗️ Architecture

### Microservices Overview
| Service | Port | Purpose |
|---------|------|---------|
| **Auth Service** | 8081 | Authentication, JWT tokens, user roles |
| **User Service** | 8082 | Profile management, file uploads |
| **Job Service** | 8083 | Job posting, search, filtering |
| **Application Service** | 8084 | Application tracking, status management |
| **Notification Service** | 8085 | Email notifications, messaging |
| **Frontend** | 3000 | React/Next.js user interface |

### Technology Stack
- **Backend**: Spring Boot 3.x, Java 17
- **Frontend**: React 18, Next.js 14, TypeScript
- **Database**: MongoDB 6.0+ with Redis caching
- **Authentication**: JWT with role-based access control
- **File Storage**: AWS S3 / Google Cloud Storage
- **Containerization**: Docker & Docker Compose
- **Monitoring**: Spring Actuator, Prometheus ready

## 🎯 Features

### For Job Seekers (Candidates)
- ✅ Profile creation with resume upload
- ✅ Advanced job search with filters
- ✅ One-click job applications
- ✅ Application status tracking
- ✅ Email notifications for updates
- ✅ Social media integration (LinkedIn, Portfolio)

### For Employers
- ✅ Company profile management
- ✅ Job posting with rich descriptions
- ✅ Applicant management and review
- ✅ Application status updates
- ✅ Candidate communication tools
- ✅ Hiring analytics and reporting

### For Administrators
- ✅ User management and approval
- ✅ Content moderation
- ✅ Platform analytics dashboard
- ✅ System monitoring and health checks
- ✅ Configuration management

## 🛠️ Development

### Prerequisites
- **Java 17+** - Backend services
- **Node.js 18+** - Frontend development
- **Docker & Docker Compose** - Containerization
- **MongoDB 6.0+** - Primary database
- **Redis 7+** - Caching layer

### Local Development Setup

1. **Backend Services**
   ```bash
   # Build all services
   mvn clean install
   
   # Run individual service
   cd auth-service && mvn spring-boot:run
   ```

2. **Frontend Development**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

3. **Database Setup**
   ```bash
   # Start databases
   docker-compose up -d mongodb redis
   
   # Initialize with test data
   mvn test -Dtest=TestDataSeeder
   ```

### Testing
```bash
# Backend tests
mvn test

# Frontend tests
cd frontend && npm test

# E2E tests
cd frontend && npm run test:e2e

# Integration tests
./scripts/run-tests.sh
```

## 🔧 Configuration

### Environment Variables
Key configuration variables (see [SETUP.md](./SETUP.md) for complete list):

```env
# Database
MONGODB_URI=mongodb://admin:password@localhost:27017/jobapp
REDIS_URL=redis://localhost:6379

# Security
JWT_SECRET=your-super-secret-jwt-key
JWT_EXPIRATION=86400000

# Email Service
EMAIL_HOST=smtp.gmail.com
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=your-app-password

# Cloud Storage
AWS_ACCESS_KEY_ID=your-access-key
AWS_SECRET_ACCESS_KEY=your-secret-key
AWS_S3_BUCKET=your-bucket-name
```

### Service Health Monitoring
Monitor service health at these endpoints:
- Auth: http://localhost:8081/actuator/health
- User: http://localhost:8082/actuator/health
- Job: http://localhost:8083/actuator/health
- Application: http://localhost:8084/actuator/health
- Notification: http://localhost:8085/actuator/health

## 🚀 Deployment

### Production Deployment
```bash
# Production build
docker-compose -f docker-compose.prod.yml up -d

# Kubernetes deployment
kubectl apply -f k8s/

# Validate deployment
./scripts/validate-deployment.sh
```

### Monitoring and Logging
- **Health Checks**: Spring Actuator endpoints
- **Metrics**: Prometheus-compatible metrics
- **Logging**: Structured JSON logging
- **Alerts**: Configurable health and performance alerts

## 📊 API Reference

### Authentication
```bash
# Login
POST /api/auth/login
{
  "email": "user@example.com",
  "password": "password"
}

# Register candidate
POST /api/auth/register/candidate
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password",
  "degree": "Computer Science",
  "graduationYear": 2022
}
```

### Job Search
```bash
# Search jobs
GET /api/jobs?search=software&location=San Francisco&jobType=FULL_TIME

# Get job details
GET /api/jobs/{jobId}

# Apply for job
POST /api/applications
{
  "jobId": "job123",
  "coverLetter": "I am interested..."
}
```

**📖 Complete API documentation**: [API_INTEGRATION_GUIDE.md](./API_INTEGRATION_GUIDE.md)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📞 Support

### Getting Help
- **📖 Documentation**: Check the guides above for detailed instructions
- **🐛 Issues**: Report bugs via GitHub issues
- **💬 Discussions**: Join community discussions
- **📧 Email**: Contact support for urgent issues

### Troubleshooting
Common issues and solutions are documented in [TROUBLESHOOTING.md](./TROUBLESHOOTING.md)

### Status and Updates
- **System Status**: Check service health endpoints
- **Release Notes**: See GitHub releases for updates
- **Roadmap**: View planned features and improvements

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

**🎉 Ready to get started?** Follow the [Setup Guide](./SETUP.md) for detailed installation instructions!
