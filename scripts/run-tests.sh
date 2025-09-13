#!/bin/bash

echo "Starting comprehensive test execution for Job Application Platform..."
echo

echo "========================================"
echo "Running Unit Tests"
echo "========================================"
mvn clean test -Dspring.profiles.active=test

if [ $? -ne 0 ]; then
    echo "Unit tests failed!"
    exit 1
fi

echo
echo "========================================"
echo "Running Integration Tests"
echo "========================================"
mvn verify -Dspring.profiles.active=test

if [ $? -ne 0 ]; then
    echo "Integration tests failed!"
    exit 1
fi

echo
echo "========================================"
echo "Generating Test Coverage Report"
echo "========================================"
mvn jacoco:report

echo
echo "========================================"
echo "Test Coverage Summary"
echo "========================================"
echo "Coverage reports generated in:"
echo "- auth-service/target/site/jacoco/index.html"
echo "- user-service/target/site/jacoco/index.html"
echo "- job-service/target/site/jacoco/index.html"
echo "- application-service/target/site/jacoco/index.html"
echo "- notification-service/target/site/jacoco/index.html"

echo
echo "========================================"
echo "All tests completed successfully!"
echo "========================================"