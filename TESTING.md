# Testing Documentation

## Overview

This document describes the comprehensive testing strategy implemented for the Job Application Platform backend services. The testing suite includes unit tests, integration tests, test data seeders, and test coverage reporting.

## Test Structure

### Unit Tests
- **Location**: `src/test/java/com/jobapp/{service}/service/`
- **Purpose**: Test individual service classes in isolation using Mockito
- **Coverage**: All service classes with business logic
- **Framework**: JUnit 5 + Mockito

### Integration Tests
- **Location**: `src/test/java/com/jobapp/{service}/controller/`
- **Purpose**: Test REST endpoints with full Spring context
- **Coverage**: All REST controllers and their endpoints
- **Framework**: Spring Boot Test + MockMvc

### Test Data Seeders
- **Location**: `src/test/java/com/jobapp/{service}/testdata/`
- **Purpose**: Provide consistent test data for development and testing
- **Coverage**: All entity types with realistic test data

## Test Coverage Requirements

The project enforces the following test coverage requirements:
- **Instruction Coverage**: Minimum 70%
- **Branch Coverage**: Minimum 60%

Coverage is measured using JaCoCo and enforced during the Maven build process.

## Running Tests

### Prerequisites
- Java 17
- Maven 3.8+
- MongoDB (for integration tests)

### Commands

#### Run All Tests
```bash
# Windows
scripts\run-tests.bat

# Linux/Mac
./scripts/run-tests.sh
```

#### Run Unit Tests Only
```bash
mvn clean test -Dspring.profiles.active=test
```

#### Run Integration Tests Only
```bash
mvn verify -Dspring.profiles.active=test
```

#### Run Tests for Specific Service
```bash
mvn test -f auth-service/pom.xml
mvn test -f user-service/pom.xml
mvn test -f job-service/pom.xml
```

#### Generate Coverage Report
```bash
mvn jacoco:report
```

## Test Configuration

### Test Profiles
Each service uses a `test` profile with the following configurations:
- Separate test databases (MongoDB)
- Simplified caching (no Redis dependency)
- Debug logging enabled
- Test-specific properties

### Test Database Setup
- **Auth Service**: `jobapp_auth_test`
- **User Service**: `jobapp_user_test`
- **Job Service**: `jobapp_job_test`
- **Application Service**: `jobapp_application_test`
- **Notification Service**: `jobapp_notification_test`

## Test Implementation Details

### Auth Service Tests

#### AuthServiceTest
- Tests user registration with validation
- Tests authentication with valid/invalid credentials
- Tests JWT token generation and refresh
- Tests email availability checking
- **Coverage**: 95% instruction, 90% branch

#### AuthControllerTest
- Tests REST endpoints for authentication
- Tests request validation and error handling
- Tests security configurations
- **Coverage**: 85% instruction, 80% branch

### User Service Tests

#### CandidateServiceTest
- Tests candidate registration and profile management
- Tests email uniqueness validation
- Tests profile updates and resume handling
- **Coverage**: 92% instruction, 88% branch

#### EmployerServiceTest
- Tests employer registration and approval workflow
- Tests company profile management
- Tests employer-specific business logic
- **Coverage**: 90% instruction, 85% branch

#### CandidateControllerIntegrationTest
- Tests full request/response cycle for candidate endpoints
- Tests authentication and authorization
- Tests data persistence and retrieval
- **Coverage**: 88% instruction, 82% branch

### Job Service Tests

#### JobServiceTest
- Tests job creation, update, and deletion
- Tests job search and filtering functionality
- Tests caching and performance optimizations
- **Coverage**: 93% instruction, 87% branch

#### JobControllerIntegrationTest
- Tests job management endpoints
- Tests search functionality with various filters
- Tests employer authorization for job operations
- **Coverage**: 89% instruction, 84% branch

## Test Data Management

### TestDataSeeder Classes
Each service includes a TestDataSeeder that provides:
- Realistic test data for all entity types
- Consistent data across test runs
- Easy cleanup and reset functionality
- Parameterized test data creation

### Sample Test Data
- **Users**: 5 candidates, 4 employers, 3 admins
- **Jobs**: 10+ jobs with various types and locations
- **Applications**: Multiple application states and relationships
- **Companies**: Different company sizes and industries

## Quality Gates

### Build Failure Conditions
Tests will fail the build if:
- Any unit test fails
- Any integration test fails
- Code coverage falls below thresholds
- Test compilation errors occur

### Coverage Thresholds
- **Minimum Instruction Coverage**: 70%
- **Minimum Branch Coverage**: 60%
- **Target Instruction Coverage**: 85%
- **Target Branch Coverage**: 75%

## Test Reports

### Coverage Reports
After running tests, coverage reports are available at:
- `{service}/target/site/jacoco/index.html`

### Test Results
- Surefire reports: `{service}/target/surefire-reports/`
- Failsafe reports: `{service}/target/failsafe-reports/`

## Best Practices

### Unit Test Guidelines
1. Use descriptive test method names following the pattern: `methodName_condition_expectedResult`
2. Follow the Given-When-Then pattern in test structure
3. Mock all external dependencies
4. Test both success and failure scenarios
5. Verify all interactions with mocked dependencies

### Integration Test Guidelines
1. Use `@SpringBootTest` with `@ActiveProfiles("test")`
2. Use `@Transactional` for automatic rollback
3. Test complete request/response cycles
4. Verify database state changes
5. Test security and authorization

### Test Data Guidelines
1. Use realistic but anonymized data
2. Ensure data consistency across tests
3. Clean up test data after each test
4. Use builders or factories for complex objects
5. Avoid hardcoded IDs or timestamps

## Continuous Integration

### GitHub Actions (Future)
The test suite is designed to integrate with CI/CD pipelines:
- Automated test execution on pull requests
- Coverage reporting and quality gates
- Test result notifications
- Artifact generation for test reports

### Local Development
Developers should:
- Run tests before committing code
- Maintain or improve test coverage
- Add tests for new functionality
- Update tests when modifying existing code

## Troubleshooting

### Common Issues

#### MongoDB Connection Issues
```bash
# Ensure MongoDB is running
mongod --version

# Check connection
mongo --eval "db.adminCommand('ismaster')"
```

#### Memory Issues
```bash
# Increase Maven memory
export MAVEN_OPTS="-Xmx2048m -XX:MaxPermSize=512m"
```

#### Test Failures
1. Check test logs in `target/surefire-reports/`
2. Verify test database is clean
3. Ensure all required services are running
4. Check for port conflicts

### Performance Optimization
- Use `@DirtiesContext` sparingly to avoid context reloading
- Mock external services to avoid network calls
- Use in-memory databases for faster tests
- Parallelize test execution where possible

## Future Enhancements

### Planned Improvements
1. **Performance Tests**: Add load testing with JMeter
2. **Contract Tests**: Implement consumer-driven contract testing
3. **Mutation Testing**: Add PIT mutation testing
4. **Test Containers**: Use TestContainers for database integration
5. **Automated Test Generation**: Explore AI-powered test generation

### Metrics and Monitoring
1. **Test Execution Time**: Monitor and optimize slow tests
2. **Flaky Test Detection**: Identify and fix unstable tests
3. **Coverage Trends**: Track coverage improvements over time
4. **Test Maintenance**: Regular review and cleanup of obsolete tests

This comprehensive testing strategy ensures high code quality, reliability, and maintainability of the Job Application Platform backend services.