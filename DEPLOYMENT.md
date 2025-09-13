# Job Application Platform - Deployment Guide

This guide provides comprehensive instructions for deploying the Job Application Platform in different environments.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Environment Setup](#environment-setup)
3. [Local Development](#local-development)
4. [Docker Deployment](#docker-deployment)
5. [Production Deployment](#production-deployment)
6. [Kubernetes Deployment](#kubernetes-deployment)
7. [Monitoring and Logging](#monitoring-and-logging)
8. [Troubleshooting](#troubleshooting)

## Prerequisites

### System Requirements

- **Docker**: Version 20.10 or higher
- **Docker Compose**: Version 2.0 or higher
- **Java**: JDK 17 or higher (for local development)
- **Node.js**: Version 18 or higher (for frontend development)
- **Maven**: Version 3.8 or higher (for backend builds)

### External Services

- **MongoDB**: Version 6.0 or higher
- **Redis**: Version 7.0 or higher
- **AWS S3**: For file storage (production)
- **SMTP Server**: For email notifications

## Environment Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd job-application-platform
```

### 2. Environment Configuration

Copy the appropriate environment file and configure it:

**For Development:**
```bash
cp .env.example .env
```

**For Production:**
```bash
cp .env.prod.example .env.prod
```

### 3. Configure Environment Variables

Edit the environment file with your specific values:

```bash
# Database Configuration
MONGODB_ROOT_USERNAME=admin
MONGODB_ROOT_PASSWORD=your_secure_password
MONGODB_DATABASE=jobapp

# JWT Configuration
JWT_SECRET=your_very_secure_jwt_secret

# AWS Configuration (Production)
AWS_ACCESS_KEY_ID=your_aws_access_key
AWS_SECRET_ACCESS_KEY=your_aws_secret_key
S3_BUCKET_NAME=your-s3-bucket

# Email Configuration
SMTP_HOST=smtp.gmail.com
SMTP_USERNAME=your_email@gmail.com
SMTP_PASSWORD=your_app_password
```

## Local Development

### 1. Start Infrastructure Services

```bash
# Start MongoDB and Redis
docker-compose up -d mongodb redis
```

### 2. Build and Run Backend Services

```bash
# Build all services
mvn clean install

# Run individual services
cd auth-service && mvn spring-boot:run &
cd user-service && mvn spring-boot:run &
cd job-service && mvn spring-boot:run &
cd application-service && mvn spring-boot:run &
cd notification-service && mvn spring-boot:run &
```

### 3. Run Frontend

```bash
cd frontend
npm install
npm run dev
```

## Docker Deployment

### Development Environment

```bash
# Build and start all services
./scripts/deploy.sh development deploy

# Or use the build script
./build.sh docker
./build.sh start
```

### Production Environment

```bash
# Build and start production services
./scripts/deploy.sh production deploy
```

### Available Commands

```bash
# Start services
./scripts/deploy.sh [environment] start

# Stop services
./scripts/deploy.sh [environment] stop

# View logs
./scripts/deploy.sh [environment] logs

# Check service health
./scripts/deploy.sh [environment] health

# Clean up
./scripts/deploy.sh [environment] cleanup
```

## Production Deployment

### 1. Server Setup

**Minimum Requirements:**
- CPU: 4 cores
- RAM: 8GB
- Storage: 50GB SSD
- OS: Ubuntu 20.04 LTS or CentOS 8

### 2. Install Dependencies

```bash
# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

### 3. Deploy Application

```bash
# Clone repository
git clone <repository-url>
cd job-application-platform

# Configure production environment
cp .env.prod.example .env.prod
# Edit .env.prod with production values

# Deploy
./scripts/deploy.sh production deploy
```

### 4. SSL/TLS Setup

Use a reverse proxy like Nginx with Let's Encrypt:

```nginx
server {
    listen 80;
    server_name yourdomain.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name yourdomain.com;

    ssl_certificate /etc/letsencrypt/live/yourdomain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/yourdomain.com/privkey.pem;

    location / {
        proxy_pass http://localhost:3000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

## Kubernetes Deployment

### 1. Prerequisites

- Kubernetes cluster (v1.24+)
- kubectl configured
- Helm (optional, for package management)

### 2. Deploy to Kubernetes

```bash
# Create namespace
kubectl apply -f k8s/namespace.yml

# Deploy database services
kubectl apply -f k8s/mongodb.yml
kubectl apply -f k8s/redis.yml

# Deploy application services
kubectl apply -f k8s/auth-service.yml
# Apply other service configurations...

# Check deployment status
kubectl get pods -n jobapp
kubectl get services -n jobapp
```

### 3. Ingress Configuration

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: jobapp-ingress
  namespace: jobapp
  annotations:
    kubernetes.io/ingress.class: nginx
    cert-manager.io/cluster-issuer: letsencrypt-prod
spec:
  tls:
  - hosts:
    - yourdomain.com
    secretName: jobapp-tls
  rules:
  - host: yourdomain.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: frontend-service
            port:
              number: 3000
      - path: /api
        pathType: Prefix
        backend:
          service:
            name: api-gateway-service
            port:
              number: 8080
```

## Monitoring and Logging

### 1. Prometheus and Grafana

The production Docker Compose includes Prometheus and Grafana:

- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3001 (admin/password from env)

### 2. Log Aggregation

Logs are stored in the `./logs` directory and can be forwarded to centralized logging systems:

```bash
# View service logs
docker-compose logs -f auth-service

# View all logs
docker-compose logs -f
```

### 3. Health Checks

All services include health check endpoints:

```bash
# Check service health
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
# ... for other services
```

## Troubleshooting

### Common Issues

#### 1. Services Not Starting

**Problem**: Services fail to start or crash immediately.

**Solutions**:
- Check environment variables in `.env` file
- Verify MongoDB and Redis are running
- Check service logs: `docker-compose logs [service-name]`
- Ensure ports are not already in use

#### 2. Database Connection Issues

**Problem**: Services cannot connect to MongoDB.

**Solutions**:
- Verify MongoDB is running: `docker-compose ps mongodb`
- Check MongoDB logs: `docker-compose logs mongodb`
- Verify connection string in environment variables
- Ensure MongoDB user has proper permissions

#### 3. Frontend Build Issues

**Problem**: Frontend fails to build or start.

**Solutions**:
- Clear node_modules: `rm -rf frontend/node_modules && npm install`
- Check Node.js version: `node --version` (should be 18+)
- Verify environment variables in `frontend/.env.local`

#### 4. Memory Issues

**Problem**: Services running out of memory.

**Solutions**:
- Increase Docker memory limits
- Adjust JVM heap size in Dockerfiles
- Monitor resource usage: `docker stats`

### Debugging Commands

```bash
# Check container status
docker-compose ps

# View container logs
docker-compose logs -f [service-name]

# Execute commands in container
docker-compose exec [service-name] bash

# Check network connectivity
docker-compose exec [service-name] ping [other-service]

# Monitor resource usage
docker stats

# Check disk usage
docker system df
```

### Performance Optimization

#### 1. Database Optimization

```javascript
// MongoDB indexes for better performance
db.jobs.createIndex({ "title": "text", "description": "text" });
db.jobs.createIndex({ "location": 1, "jobType": 1, "isActive": 1 });
db.applications.createIndex({ "candidateId": 1, "status": 1 });
```

#### 2. Caching Strategy

- Redis is used for caching frequently accessed job data
- Frontend implements service worker for offline caching
- CDN can be used for static assets in production

#### 3. Load Balancing

For high-traffic scenarios, consider:
- Multiple instances of each service
- Load balancer (Nginx, HAProxy)
- Database read replicas
- CDN for static content

## Security Considerations

### 1. Environment Variables

- Never commit `.env` files to version control
- Use strong passwords and secrets
- Rotate JWT secrets regularly

### 2. Network Security

- Use Docker networks to isolate services
- Implement proper firewall rules
- Use HTTPS in production

### 3. Database Security

- Use strong MongoDB authentication
- Enable MongoDB access control
- Regular security updates

## Backup and Recovery

### 1. Database Backup

```bash
# MongoDB backup
docker-compose exec mongodb mongodump --uri="mongodb://admin:password@localhost:27017/jobapp?authSource=admin" --out=/backup

# Copy backup from container
docker cp $(docker-compose ps -q mongodb):/backup ./backup
```

### 2. File Storage Backup

- S3 buckets should have versioning enabled
- Regular backup of uploaded files
- Cross-region replication for critical data

## Support

For additional support:
1. Check the application logs
2. Review this deployment guide
3. Consult the API documentation
4. Contact the development team

---

**Last Updated**: December 2024
**Version**: 1.0.0