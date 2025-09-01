#!/bin/bash

# Job Application Platform Build Script

set -e

echo "🚀 Building Job Application Platform..."

# Function to build individual service
build_service() {
    local service=$1
    echo "📦 Building $service..."
    cd $service
    mvn clean package -DskipTests
    cd ..
    echo "✅ $service built successfully"
}

# Function to run tests for individual service
test_service() {
    local service=$1
    echo "🧪 Testing $service..."
    cd $service
    mvn test
    cd ..
    echo "✅ $service tests passed"
}

# Parse command line arguments
case "$1" in
    "build")
        echo "Building all services..."
        mvn clean install -DskipTests
        ;;
    "test")
        echo "Running tests for all services..."
        mvn test
        ;;
    "docker")
        echo "Building Docker images..."
        docker-compose build
        ;;
    "start")
        echo "Starting all services with Docker Compose..."
        docker-compose up -d
        ;;
    "stop")
        echo "Stopping all services..."
        docker-compose down
        ;;
    "logs")
        echo "Showing logs..."
        docker-compose logs -f
        ;;
    "clean")
        echo "Cleaning build artifacts..."
        mvn clean
        docker-compose down -v
        docker system prune -f
        ;;
    *)
        echo "Usage: $0 {build|test|docker|start|stop|logs|clean}"
        echo ""
        echo "Commands:"
        echo "  build  - Build all services"
        echo "  test   - Run tests for all services"
        echo "  docker - Build Docker images"
        echo "  start  - Start all services with Docker Compose"
        echo "  stop   - Stop all services"
        echo "  logs   - Show service logs"
        echo "  clean  - Clean build artifacts and Docker volumes"
        exit 1
        ;;
esac

echo "🎉 Operation completed successfully!"