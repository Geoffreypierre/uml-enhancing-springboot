#!/bin/bash

# Script to run the Spring Boot Maven project
# Navigate to the project root directory
cd "$(dirname "$0")/.." || exit 1

echo "=========================================="
echo "Running UML Enhancement Spring Boot App"
echo "=========================================="

# Check if Maven wrapper exists
if [ -f "./mvnw" ]; then
    echo "Using Maven wrapper..."
    chmod +x ./mvnw
    ./mvnw spring-boot:run
else
    echo "Using system Maven..."
    mvn spring-boot:run
fi
