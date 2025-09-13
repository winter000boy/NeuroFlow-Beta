@echo off
REM Job Application Platform Deployment Script for Windows

setlocal enabledelayedexpansion

REM Configuration
set ENVIRONMENT=%1
if "%ENVIRONMENT%"=="" set ENVIRONMENT=development

set COMPOSE_FILE=docker-compose.yml
set ENV_FILE=.env

if "%ENVIRONMENT%"=="production" (
    set COMPOSE_FILE=docker-compose.prod.yml
    set ENV_FILE=.env.prod
)

echo 🚀 Deploying Job Application Platform - Environment: %ENVIRONMENT%

REM Function to check prerequisites
:check_prerequisites
echo 📋 Checking prerequisites...

if not exist "%COMPOSE_FILE%" (
    echo ❌ Docker compose file %COMPOSE_FILE% not found
    exit /b 1
)

if not exist "%ENV_FILE%" (
    echo ❌ Environment file %ENV_FILE% not found
    echo 💡 Please copy .env.example to %ENV_FILE% and configure it
    exit /b 1
)

REM Check if Docker is running
docker info >nul 2>&1
if errorlevel 1 (
    echo ❌ Docker is not running
    exit /b 1
)

REM Check if Docker Compose is available
docker-compose --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Docker Compose is not installed
    exit /b 1
)

echo ✅ Prerequisites check passed
goto :eof

REM Function to build services
:build_services
echo 🔨 Building services...

REM Build backend services
echo Building backend services...
mvn clean package -DskipTests
if errorlevel 1 (
    echo ❌ Backend build failed
    exit /b 1
)

REM Build Docker images
echo Building Docker images...
docker-compose -f "%COMPOSE_FILE%" --env-file "%ENV_FILE%" build
if errorlevel 1 (
    echo ❌ Docker build failed
    exit /b 1
)

echo ✅ Services built successfully
goto :eof

REM Function to start services
:start_services
echo 🚀 Starting services...

REM Create logs directory
if not exist "logs" mkdir logs
if not exist "logs\auth-service" mkdir logs\auth-service
if not exist "logs\user-service" mkdir logs\user-service
if not exist "logs\job-service" mkdir logs\job-service
if not exist "logs\application-service" mkdir logs\application-service
if not exist "logs\notification-service" mkdir logs\notification-service
if not exist "logs\frontend" mkdir logs\frontend
if not exist "logs\mongodb" mkdir logs\mongodb
if not exist "logs\redis" mkdir logs\redis

REM Start services
docker-compose -f "%COMPOSE_FILE%" --env-file "%ENV_FILE%" up -d
if errorlevel 1 (
    echo ❌ Failed to start services
    exit /b 1
)

echo ✅ Services started successfully
goto :eof

REM Function to check service health
:check_health
echo 🏥 Checking service health...

REM Wait for services to start
timeout /t 30 /nobreak >nul

echo Checking service health...
REM Note: Windows doesn't have a direct equivalent to the bash health check
REM This is a simplified version
docker-compose -f "%COMPOSE_FILE%" --env-file "%ENV_FILE%" ps

echo ✅ Health check completed
goto :eof

REM Function to show service status
:show_status
echo 📊 Service Status:
docker-compose -f "%COMPOSE_FILE%" --env-file "%ENV_FILE%" ps

echo.
echo 📋 Service URLs:
echo Frontend: http://localhost:3000
echo Auth Service: http://localhost:8081
echo User Service: http://localhost:8082
echo Job Service: http://localhost:8083
echo Application Service: http://localhost:8084
echo Notification Service: http://localhost:8085

if "%ENVIRONMENT%"=="production" (
    echo Prometheus: http://localhost:9090
    echo Grafana: http://localhost:3001
)
goto :eof

REM Function to show logs
:show_logs
echo 📝 Showing logs...
docker-compose -f "%COMPOSE_FILE%" --env-file "%ENV_FILE%" logs -f
goto :eof

REM Function to stop services
:stop_services
echo 🛑 Stopping services...
docker-compose -f "%COMPOSE_FILE%" --env-file "%ENV_FILE%" down
echo ✅ Services stopped
goto :eof

REM Function to clean up
:cleanup
echo 🧹 Cleaning up...
docker-compose -f "%COMPOSE_FILE%" --env-file "%ENV_FILE%" down -v
docker system prune -f
echo ✅ Cleanup completed
goto :eof

REM Main deployment logic
set COMMAND=%2
if "%COMMAND%"=="" set COMMAND=deploy

if "%COMMAND%"=="deploy" (
    call :check_prerequisites
    if errorlevel 1 exit /b 1
    call :build_services
    if errorlevel 1 exit /b 1
    call :start_services
    if errorlevel 1 exit /b 1
    call :check_health
    call :show_status
) else if "%COMMAND%"=="start" (
    call :check_prerequisites
    if errorlevel 1 exit /b 1
    call :start_services
    if errorlevel 1 exit /b 1
    call :show_status
) else if "%COMMAND%"=="stop" (
    call :stop_services
) else if "%COMMAND%"=="restart" (
    call :stop_services
    call :start_services
    if errorlevel 1 exit /b 1
    call :show_status
) else if "%COMMAND%"=="status" (
    call :show_status
) else if "%COMMAND%"=="logs" (
    call :show_logs
) else if "%COMMAND%"=="health" (
    call :check_health
) else if "%COMMAND%"=="cleanup" (
    call :cleanup
) else (
    echo Usage: %0 [environment] [command]
    echo.
    echo Environments:
    echo   development ^(default^)
    echo   production
    echo.
    echo Commands:
    echo   deploy    - Full deployment ^(build, start, health check^)
    echo   start     - Start services
    echo   stop      - Stop services
    echo   restart   - Restart services
    echo   status    - Show service status
    echo   logs      - Show service logs
    echo   health    - Check service health
    echo   cleanup   - Stop services and clean up volumes
    echo.
    echo Examples:
    echo   %0 development deploy
    echo   %0 production start
    echo   %0 development logs
    exit /b 1
)

echo 🎉 Operation completed successfully!