# Troubleshooting Guide

## Common Setup Issues

### 1. Docker Issues

#### Problem: Docker containers fail to start
**Symptoms:**
- Services exit immediately
- Port binding errors
- Network connectivity issues

**Solutions:**
```bash
# Check if ports are already in use
netstat -tulpn | grep :8081
netstat -tulpn | grep :3000

# Stop conflicting services
sudo systemctl stop apache2  # If using port 80
sudo systemctl stop nginx    # If using port 80

# Clean Docker environment
docker-compose down -v
docker system prune -f
docker-compose up -d
```

#### Problem: MongoDB connection refused
**Symptoms:**
- `Connection refused` errors in service logs
- Services can't connect to database

**Solutions:**
```bash
# Check MongoDB container status
docker ps | grep mongo

# Check MongoDB logs
docker logs mongodb

# Restart MongoDB with proper configuration
docker-compose down
docker-compose up -d mongodb
docker-compose logs mongodb

# Wait for MongoDB to be ready, then start other services
docker-compose up -d
```

### 2. Java/Spring Boot Issues

#### Problem: Java version compatibility
**Symptoms:**
- `UnsupportedClassVersionError`
- Build failures with version errors

**Solutions:**
```bash
# Check Java version
java -version
javac -version

# Install Java 17 (Ubuntu/Debian)
sudo apt update
sudo apt install openjdk-17-jdk

# Set JAVA_HOME
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> ~/.bashrc
```

#### Problem: Maven build failures
**Symptoms:**
- Dependency resolution errors
- Test failures during build

**Solutions:**
```bash
# Clean and rebuild
mvn clean install -DskipTests

# Update dependencies
mvn dependency:resolve

# Clear Maven cache
rm -rf ~/.m2/repository
mvn clean install

# Run with debug output
mvn clean install -X
```

#### Problem: Application fails to start
**Symptoms:**
- Port already in use errors
- Bean creation failures
- Configuration errors

**Solutions:**
```bash
# Check application logs
tail -f auth-service/logs/application.log

# Verify configuration
cat auth-service/src/main/resources/application.yml

# Check if port is available
lsof -i :8081

# Kill process using the port
kill -9 $(lsof -t -i:8081)
```

### 3. Frontend Issues

#### Problem: Node.js/npm issues
**Symptoms:**
- Package installation failures
- Build errors
- Runtime errors

**Solutions:**
```bash
# Clear npm cache
npm cache clean --force

# Delete node_modules and reinstall
rm -rf node_modules package-lock.json
npm install

# Use specific Node version
nvm install 18
nvm use 18

# Check for conflicting global packages
npm list -g --depth=0
```

#### Problem: Environment variable issues
**Symptoms:**
- API calls failing
- Configuration not loading
- Build-time errors

**Solutions:**
```bash
# Verify environment file exists
ls -la frontend/.env.local

# Check environment variables are loaded
npm run dev -- --debug

# Validate environment file format
cat frontend/.env.local
# Ensure no spaces around = signs
# Ensure no quotes unless needed
```

#### Problem: Build failures
**Symptoms:**
- TypeScript compilation errors
- Missing dependencies
- Asset loading issues

**Solutions:**
```bash
# Clear Next.js cache
rm -rf frontend/.next

# Rebuild with verbose output
npm run build -- --debug

# Check TypeScript configuration
npx tsc --noEmit

# Verify all dependencies are installed
npm audit
npm audit fix
```

### 4. Database Issues

#### Problem: MongoDB connection issues
**Symptoms:**
- Authentication failures
- Connection timeouts
- Database not found errors

**Solutions:**
```bash
# Test MongoDB connection
mongo mongodb://admin:password@localhost:27017/jobapp

# Check MongoDB status
docker exec -it mongodb mongo --eval "db.adminCommand('ismaster')"

# Verify database exists
docker exec -it mongodb mongo -u admin -p password --eval "show dbs"

# Create database if missing
docker exec -it mongodb mongo -u admin -p password --eval "use jobapp; db.createCollection('test')"
```

#### Problem: Data seeding issues
**Symptoms:**
- Empty database after setup
- Seeder script failures
- Duplicate key errors

**Solutions:**
```bash
# Run seeder manually
mvn test -Dtest=TestDataSeeder

# Clear database and reseed
docker exec -it mongodb mongo -u admin -p password --eval "use jobapp; db.dropDatabase()"
mvn test -Dtest=TestDataSeeder

# Check seeded data
docker exec -it mongodb mongo -u admin -p password --eval "use jobapp; db.candidates.count()"
```

### 5. Authentication Issues

#### Problem: JWT token issues
**Symptoms:**
- 401 Unauthorized errors
- Token validation failures
- Login not working

**Solutions:**
```bash
# Verify JWT secret is set
grep JWT_SECRET .env

# Check token format
echo "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." | base64 -d

# Test authentication endpoint
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password"}'

# Check auth service logs
docker logs auth-service
```

#### Problem: CORS issues
**Symptoms:**
- Browser console CORS errors
- API calls blocked by browser
- Preflight request failures

**Solutions:**
```bash
# Verify CORS configuration in backend
grep -r "CrossOrigin\|CORS" auth-service/src/

# Check frontend API URL configuration
grep NEXT_PUBLIC_API_URL frontend/.env.local

# Test API directly (bypassing CORS)
curl -X GET http://localhost:8081/api/auth/health
```

### 6. File Upload Issues

#### Problem: File upload failures
**Symptoms:**
- 413 Request Entity Too Large
- File not found errors
- Upload timeouts

**Solutions:**
```bash
# Check file size limits
grep -r "multipart.max-file-size" */src/main/resources/

# Verify cloud storage configuration
grep -r "AWS_\|GCS_" .env

# Test file upload endpoint
curl -X POST http://localhost:8082/api/users/upload/resume \
  -H "Authorization: Bearer {token}" \
  -F "file=@test-resume.pdf"

# Check storage service logs
docker logs user-service | grep -i upload
```

### 7. Performance Issues

#### Problem: Slow API responses
**Symptoms:**
- High response times
- Timeout errors
- Poor user experience

**Solutions:**
```bash
# Check database indexes
docker exec -it mongodb mongo -u admin -p password --eval "use jobapp; db.jobs.getIndexes()"

# Monitor database queries
docker exec -it mongodb mongo -u admin -p password --eval "db.setProfilingLevel(2)"

# Check Redis cache
docker exec -it redis redis-cli info memory

# Monitor service metrics
curl http://localhost:8081/actuator/metrics
```

#### Problem: High memory usage
**Symptoms:**
- Out of memory errors
- Slow performance
- Container restarts

**Solutions:**
```bash
# Check memory usage
docker stats

# Increase JVM heap size
export JAVA_OPTS="-Xmx2g -Xms1g"

# Monitor garbage collection
jstat -gc $(pgrep java)

# Check for memory leaks
jmap -histo $(pgrep java) | head -20
```

### 8. Email Service Issues

#### Problem: Email notifications not working
**Symptoms:**
- Registration emails not sent
- Application status emails missing
- SMTP connection errors

**Solutions:**
```bash
# Verify email configuration
grep -r "EMAIL_\|SMTP" .env

# Test SMTP connection
telnet smtp.gmail.com 587

# Check notification service logs
docker logs notification-service | grep -i email

# Test email endpoint
curl -X POST http://localhost:8085/api/notifications/test-email \
  -H "Authorization: Bearer {token}"
```

### 9. Deployment Issues

#### Problem: Production deployment failures
**Symptoms:**
- Services not starting in production
- Environment variable issues
- Network connectivity problems

**Solutions:**
```bash
# Check production environment file
cat .env.prod.example

# Verify all required variables are set
docker-compose -f docker-compose.prod.yml config

# Check service health in production
curl https://api.yourapp.com/auth/actuator/health

# Monitor deployment logs
docker-compose -f docker-compose.prod.yml logs -f
```

#### Problem: SSL/HTTPS issues
**Symptoms:**
- Certificate errors
- Mixed content warnings
- HTTPS redirect loops

**Solutions:**
```bash
# Check SSL certificate
openssl s_client -connect yourapp.com:443

# Verify reverse proxy configuration
nginx -t
systemctl reload nginx

# Check HTTPS redirect configuration
curl -I http://yourapp.com
```

## Debugging Tools

### 1. Health Check Endpoints
```bash
# Check all service health
for port in 8081 8082 8083 8084 8085; do
  echo "Checking port $port:"
  curl -s http://localhost:$port/actuator/health | jq .
done
```

### 2. Log Analysis
```bash
# Tail all service logs
docker-compose logs -f

# Search for errors
docker-compose logs | grep -i error

# Filter by service
docker-compose logs auth-service | tail -50
```

### 3. Database Debugging
```bash
# Connect to MongoDB
docker exec -it mongodb mongo -u admin -p password

# Check collections
use jobapp
show collections
db.candidates.find().limit(5)
db.jobs.find({isActive: true}).count()
```

### 4. Network Debugging
```bash
# Check container networking
docker network ls
docker network inspect jobapp_default

# Test inter-service communication
docker exec -it auth-service curl http://user-service:8082/actuator/health
```

## Performance Monitoring

### 1. Application Metrics
```bash
# JVM metrics
curl http://localhost:8081/actuator/metrics/jvm.memory.used

# HTTP metrics
curl http://localhost:8081/actuator/metrics/http.server.requests

# Database connection pool
curl http://localhost:8081/actuator/metrics/hikaricp.connections.active
```

### 2. Database Performance
```bash
# MongoDB slow queries
db.setProfilingLevel(2, { slowms: 100 })
db.system.profile.find().sort({ts: -1}).limit(5)

# Index usage
db.jobs.find({location: "San Francisco"}).explain("executionStats")
```

### 3. Frontend Performance
```bash
# Bundle analysis
cd frontend
npm run analyze

# Lighthouse audit
npx lighthouse http://localhost:3000 --output html --output-path ./lighthouse-report.html
```

## Getting Help

### 1. Log Collection
When reporting issues, collect these logs:
```bash
# Create debug bundle
mkdir debug-logs
docker-compose logs > debug-logs/docker-compose.log
cp .env debug-logs/env-file
cp frontend/.env.local debug-logs/frontend-env
tar -czf debug-bundle.tar.gz debug-logs/
```

### 2. System Information
```bash
# System info
uname -a
docker --version
docker-compose --version
java -version
node --version
npm --version
```

### 3. Configuration Check
```bash
# Validate configuration
docker-compose config
mvn validate
npm run lint
```

## Preventive Measures

### 1. Regular Maintenance
```bash
# Weekly cleanup
docker system prune -f
npm audit fix
mvn dependency:resolve-sources

# Update dependencies
npm update
mvn versions:display-dependency-updates
```

### 2. Monitoring Setup
```bash
# Set up log rotation
sudo logrotate -f /etc/logrotate.conf

# Monitor disk space
df -h
du -sh docker/volumes/*

# Set up alerts for service health
curl -f http://localhost:8081/actuator/health || echo "Auth service down!"
```

### 3. Backup Procedures
```bash
# Backup MongoDB
docker exec mongodb mongodump --out /backup --authenticationDatabase admin -u admin -p password

# Backup uploaded files
aws s3 sync s3://your-bucket ./backup/files/

# Backup configuration
cp -r .kiro/settings ./backup/config/
```