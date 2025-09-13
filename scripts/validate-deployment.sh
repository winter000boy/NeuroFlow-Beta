#!/bin/bash

# Job Application Platform Deployment Validation Script

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🔍 Validating Job Application Platform Deployment${NC}"

# Function to check if a service is responding
check_service() {
    local service_name=$1
    local url=$2
    local expected_status=${3:-200}
    
    echo -e "${YELLOW}Checking $service_name...${NC}"
    
    if curl -s -o /dev/null -w "%{http_code}" "$url" | grep -q "$expected_status"; then
        echo -e "${GREEN}✅ $service_name is responding${NC}"
        return 0
    else
        echo -e "${RED}❌ $service_name is not responding${NC}"
        return 1
    fi
}

# Function to check database connectivity
check_database() {
    echo -e "${YELLOW}Checking MongoDB connectivity...${NC}"
    
    if docker-compose exec -T mongodb mongosh --eval "db.adminCommand('ping')" > /dev/null 2>&1; then
        echo -e "${GREEN}✅ MongoDB is accessible${NC}"
        return 0
    else
        echo -e "${RED}❌ MongoDB is not accessible${NC}"
        return 1
    fi
}

# Function to check Redis connectivity
check_redis() {
    echo -e "${YELLOW}Checking Redis connectivity...${NC}"
    
    if docker-compose exec -T redis redis-cli ping | grep -q "PONG"; then
        echo -e "${GREEN}✅ Redis is accessible${NC}"
        return 0
    else
        echo -e "${RED}❌ Redis is not accessible${NC}"
        return 1
    fi
}

# Main validation
echo -e "${BLUE}Starting validation...${NC}"

# Check if Docker Compose is running
if ! docker-compose ps | grep -q "Up"; then
    echo -e "${RED}❌ No services are running. Please start the application first.${NC}"
    exit 1
fi

# Check databases
check_database
check_redis

# Wait a moment for services to be fully ready
echo -e "${YELLOW}Waiting for services to be ready...${NC}"
sleep 10

# Check microservices
services_passed=0
total_services=5

if check_service "Auth Service" "http://localhost:8081/actuator/health"; then
    ((services_passed++))
fi

if check_service "User Service" "http://localhost:8082/actuator/health"; then
    ((services_passed++))
fi

if check_service "Job Service" "http://localhost:8083/actuator/health"; then
    ((services_passed++))
fi

if check_service "Application Service" "http://localhost:8084/actuator/health"; then
    ((services_passed++))
fi

if check_service "Notification Service" "http://localhost:8085/actuator/health"; then
    ((services_passed++))
fi

# Check frontend
if check_service "Frontend" "http://localhost:3000/api/health"; then
    echo -e "${GREEN}✅ Frontend is responding${NC}"
else
    echo -e "${RED}❌ Frontend is not responding${NC}"
fi

# Summary
echo -e "\n${BLUE}📊 Validation Summary:${NC}"
echo -e "Services passed: $services_passed/$total_services"

if [ $services_passed -eq $total_services ]; then
    echo -e "${GREEN}🎉 All services are healthy and ready!${NC}"
    echo -e "\n${BLUE}🌐 Application URLs:${NC}"
    echo -e "Frontend: ${GREEN}http://localhost:3000${NC}"
    echo -e "API Documentation: ${GREEN}http://localhost:8081/swagger-ui.html${NC}"
    exit 0
else
    echo -e "${RED}⚠️  Some services are not healthy. Please check the logs.${NC}"
    echo -e "Run: ${YELLOW}docker-compose logs [service-name]${NC} to debug"
    exit 1
fi