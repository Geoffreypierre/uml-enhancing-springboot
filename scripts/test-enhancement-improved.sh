#!/bin/bash

echo "=========================================="
echo "Testing Improved UML Enhancement"
echo "=========================================="
echo ""
echo "Changes implemented:"
echo "1. Filtered out relationship concepts (_to_, _NULL)"
echo "2. Increased threshold to 0.3 (was 0.1)"
echo "3. Added clustering for natural groupings"
echo "4. Improved LLM prompts with domain context"
echo "5. Added god class detection (>50% common members)"
echo ""

# Start the Spring Boot application in the background
echo "Starting Spring Boot application..."
./mvnw spring-boot:run > test-improved.log 2>&1 &
PID=$!

# Wait for the application to start
echo "Waiting for application to start..."
sleep 10

# Make the API request with threshold 0.3
echo ""
echo "Making API request with threshold=0.3..."
curl -X POST http://localhost:8080/api/uml/enhance \
  -H "Content-Type: application/json" \
  -d "{
    \"threshold\": 0.3,
    \"pumlContent\": \"$(cat test-diagram-missing-abstractions.puml | sed 's/"/\\"/g' | tr -d '\n')\"
  }" \
  -o output/enhanced-diagram-improved.puml

echo ""
echo "Waiting for processing to complete..."
sleep 145

# Shutdown the application
echo ""
echo "Shutting down application..."
kill $PID
wait $PID 2>/dev/null

echo ""
echo "=========================================="
echo "Enhancement complete!"
echo "=========================================="
echo ""
echo "Results:"
echo "  - Output: output/enhanced-diagram-improved.puml"
echo "  - Logs: test-improved.log"
echo ""
echo "To view the enhanced diagram:"
echo "  cat output/enhanced-diagram-improved.puml"
echo ""
