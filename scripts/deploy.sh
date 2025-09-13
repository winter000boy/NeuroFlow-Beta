#!/bin/bash

# Job Application Platform Deployment Script

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
ENVIRONMENT=${1:-development}
COMPOSE_FILE="docker-compose.yml"
ENV_FILE=".env"

if [ "$ENVIRONMENT" = "production" ]; then
    COMPOSE_FILE="docker-compose.prod.yml"
    ENV_FILE=".env.prod"
fi

echo -e "${BLUE}🚀 Deploying Job Application Platform - Environment: $ENVIRONMENT${NC}"

# Function to check if required files exist
check_prerequisites() {
    echo -e "${YELLOW}📋 Checking prerequisites...${NC}"
    
    if [ ! -f "$COMPOSE_FILE" ]; then
        echo -e "${RED}❌ Docker compose file $COMPOSE_FILE not found${NC}"
        exit 1
    fi
    
    if [ ! -f "$ENV_FILE" ]; then
        echo -e "${RED}❌ Environment file $ENV_FILE not found${NC}"
        echo -e "${YELLOW}💡 Please copy .env.example to $ENV_FILE and configure it${NC}"
        exit 1
    fi
    
    # Check if Docker is running
    if ! docker info > /dev/null 2>&1; then
        echo -e "${RED}❌ Docker is not running${NC}"
        exit 1
    fi
    
    # Check if Docker Compose is available
    if ! command -v docker-compose > /dev/null 2>&1; then
        echo -e "${RED}❌ Docker Compose is not installed${NC}"
        exit 1
    fi
    
    echo -e "${GREEN}✅ Prerequisites check passed${NC}"
}

# Function to build services
build_services() {
    echo -e "${YELLOW}🔨 Building services...${NC}"
    
    # Build backend services
    echo -e "${BLUE}Building backend services...${NC}"
    mvn clean package -DskipTests
    
    # Build Docker images
    echo -e "${BLUE}Building Docker images...${NC}"
    docker-compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" build
    
    echo -e "${GREEN}✅ Services built successfully${NC}"
}

# Function to start services
start_services() {
    echo -e "${YELLOW}🚀 Starting services...${NC}"
    
    # Create logs directory
    mkdir -p logs/{auth-service,user-service,job-service,application-service,notification-service,frontend,mongodb,redis}
    
    # Start services
    docker-compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" up -d
    
    echo -e "${GREEN}✅ Services started successfully${NC}"
}

# Function to check service health
check_health() {
    echo -e "${YELLOW}🏥 Checking service health...${NC}"
    
    # Wait for services to start
    sleep 30
    
    services=("auth-service" "user-service" "job-service" "application-service" "notification-service")
    
    for service in "${services[@]}"; do
        echo -e "${BLUE}Checking $service health...${NC}"
        
        # Try to get health status
        for i in {1..10}; do
            if docker-compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" exec -T "$service" curl -f http://localhost:808$((i+0))/actuator/health > /dev/null 2>&1; then
                echo -e "${GREEN}✅ $service is healthy${NC}"
                break
            else
                if [ $i -eq 10 ]; then
                    echo -e "${RED}❌ $service health check failed${NC}"
                else
                    echo -e "${YELLOW}⏳ Waiting for $service to be ready... (attempt $i/10)${NC}"
                    sleep 10
                fi
            fi
        done
    done
}

# Function to show service status
show_status() {
    echo -e "${YELLOW}📊 Service Status:${NC}"
    docker-compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" ps
    
    echo -e "\n${YELLOW}📋 Service URLs:${NC}"
    echo -e "${BLUE}Frontend:${NC} http://localhost:3000"
    echo -e "${BLUE}Auth Service:${NC} http://localhost:8081"
    echo -e "${BLUE}User Service:${NC} http://localhost:8082"
    echo -e "${BLUE}Job Service:${NC} http://localhost:8083"
    echo -e "${BLUE}Application Service:${NC} http://localhost:8084"
    echo -e "${BLUE}Notification Service:${NC} http://localhost:8085"
    
    if [ "$ENVIRONMENT" = "production" ]; then
        echo -e "${BLUE}Prometheus:${NC} http://localhost:9090"
        echo -e "${BLUE}Grafana:${NC} http://localhost:3001"
    fi
}

# Function to show logs
show_logs() {
    echo -e "${YELLOW}📝 Showing logs...${NC}"
    docker-compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" logs -f
}

# Function to stop services
stop_services() {
    echo -e "${YELLOW}🛑 Stopping services...${NC}"
    docker-compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" down
    echo -e "${GREEN}✅ Services stopped${NC}"
}

# Function to clean up
cleanup() {
    echo -e "${YELLOW}🧹 Cleaning up...${NC}"
    docker-compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" down -v
    docker system prune -f
    echo -e "${GREEN}✅ Cleanup completed${NC}"
}

# Main deployment logic
case "${2:-deploy}" in
    "deploy")
        check_prerequisites
        build_services
        start_services
        check_health
        show_status
        ;;
    "start")
        check_prerequisites
        start_services
        show_status
        ;;
    "stop")
        stop_services
        ;;
    "restart")
        stop_services
        start_services
        show_status
        ;;
    "status")
        show_status
        ;;
    "logs")
        show_logs
        ;;
    "health")
        check_health
        ;;
    "cleanup")
        cleanup
        ;;
    *)
        echo "Usage: $0 [environment] [command]"
        echo ""
        echo "Environments:"
        echo "  development (default)"
        echo "  production"
        echo ""
        echo "Commands:"
        echo "  deploy    - Full deployment (build, start, health check)"
        echo "  start     - Start services"
        echo "  stop      - Stop services"
        echo "  restart   - Restart services"
        echo "  status    - Show service status"
        echo "  logs      - Show service logs"
        echo "  health    - Check service health"
        echo "  cleanup   - Stop services and clean up volumes"
        echo ""
        echo "Examples:"
        echo "  $0 development deploy"
        echo "  $0 production start"
        echo "  $0 development logs"
        exit 1
        ;;
esac

echo -e "${GREEN}🎉 Operation completed successfully!${NC}"