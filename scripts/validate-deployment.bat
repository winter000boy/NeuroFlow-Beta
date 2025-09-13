@echo off
REM Job Application Platform Deployment Validation Script for Windows

echo 🔍 Validating Job Application Platform Deployment

REM Function to check if a service is responding
:check_service
set service_name=%1
set url=%2
echo Checking %service_name%...

curl -s -o nul -w "%%{http_code}" "%url%" | findstr "200" >nul
if %errorlevel% equ 0 (
    echo ✅ %service_name% is responding
    exit /b 0
) else (
    echo ❌ %service_name% is not responding
    exit /b 1
)

REM Main validation
echo Starting validation...

REM Check if Docker Compose is running
docker-compose ps | findstr "Up" >nul
if %errorlevel% neq 0 (
    echo ❌ No services are running. Please start the application first.
    exit /b 1
)

REM Check databases
echo Checking MongoDB connectivity...
docker-compose exec -T mongodb mongosh --eval "db.adminCommand('ping')" >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ MongoDB is accessible
) else (
    echo ❌ MongoDB is not accessible
)

echo Checking Redis connectivity...
docker-compose exec -T redis redis-cli ping | findstr "PONG" >nul
if %errorlevel% equ 0 (
    echo ✅ Redis is accessible
) else (
    echo ❌ Redis is not accessible
)

REM Wait for services to be ready
echo Waiting for services to be ready...
timeout /t 10 /nobreak >nul

REM Check services
echo Checking Auth Service...
curl -s -o nul -w "%%{http_code}" "http://localhost:8081/actuator/health" | findstr "200" >nul
if %errorlevel% equ 0 (
    echo ✅ Auth Service is responding
) else (
    echo ❌ Auth Service is not responding
)

echo Checking User Service...
curl -s -o nul -w "%%{http_code}" "http://localhost:8082/actuator/health" | findstr "200" >nul
if %errorlevel% equ 0 (
    echo ✅ User Service is responding
) else (
    echo ❌ User Service is not responding
)

echo Checking Job Service...
curl -s -o nul -w "%%{http_code}" "http://localhost:8083/actuator/health" | findstr "200" >nul
if %errorlevel% equ 0 (
    echo ✅ Job Service is responding
) else (
    echo ❌ Job Service is not responding
)

echo Checking Application Service...
curl -s -o nul -w "%%{http_code}" "http://localhost:8084/actuator/health" | findstr "200" >nul
if %errorlevel% equ 0 (
    echo ✅ Application Service is responding
) else (
    echo ❌ Application Service is not responding
)

echo Checking Notification Service...
curl -s -o nul -w "%%{http_code}" "http://localhost:8085/actuator/health" | findstr "200" >nul
if %errorlevel% equ 0 (
    echo ✅ Notification Service is responding
) else (
    echo ❌ Notification Service is not responding
)

echo Checking Frontend...
curl -s -o nul -w "%%{http_code}" "http://localhost:3000/api/health" | findstr "200" >nul
if %errorlevel% equ 0 (
    echo ✅ Frontend is responding
) else (
    echo ❌ Frontend is not responding
)

echo.
echo 📊 Validation completed!
echo 🌐 Application URLs:
echo Frontend: http://localhost:3000
echo API Documentation: http://localhost:8081/swagger-ui.html

echo 🎉 Deployment validation finished!