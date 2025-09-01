@echo off
REM Job Application Platform Build Script for Windows

echo 🚀 Building Job Application Platform...

if "%1"=="build" (
    echo Building all services...
    mvn clean install -DskipTests
    goto :end
)

if "%1"=="test" (
    echo Running tests for all services...
    mvn test
    goto :end
)

if "%1"=="docker" (
    echo Building Docker images...
    docker-compose build
    goto :end
)

if "%1"=="start" (
    echo Starting all services with Docker Compose...
    docker-compose up -d
    goto :end
)

if "%1"=="stop" (
    echo Stopping all services...
    docker-compose down
    goto :end
)

if "%1"=="logs" (
    echo Showing logs...
    docker-compose logs -f
    goto :end
)

if "%1"=="clean" (
    echo Cleaning build artifacts...
    mvn clean
    docker-compose down -v
    docker system prune -f
    goto :end
)

echo Usage: %0 {build^|test^|docker^|start^|stop^|logs^|clean}
echo.
echo Commands:
echo   build  - Build all services
echo   test   - Run tests for all services
echo   docker - Build Docker images
echo   start  - Start all services with Docker Compose
echo   stop   - Stop all services
echo   logs   - Show service logs
echo   clean  - Clean build artifacts and Docker volumes
exit /b 1

:end
echo 🎉 Operation completed successfully!