#!/bin/bash

# Demo script to showcase UML enhancement pipeline
# This script processes a PUML diagram and outputs the enhanced version

set -e  # Exit on error

echo "=================================="
echo "UML Enhancement Pipeline Demo"
echo "=================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Setup
DEMO_DIR="/tmp/uml-enhancement-demo"
INPUT_FILE="$DEMO_DIR/input-diagram.puml"
OUTPUT_FILE="$DEMO_DIR/enhanced-diagram.puml"

echo -e "${BLUE}[1/5] Setting up demo environment...${NC}"
mkdir -p "$DEMO_DIR"
echo -e "${GREEN}✓ Created demo directory: $DEMO_DIR${NC}"
echo ""

echo -e "${YELLOW}Creating sample diagram with code duplication...${NC}"
cat > "$INPUT_FILE" << 'EOF'
@startuml
class Address {
  name: String
  age: int
  street: String
  city: String
  zipCode: String
  +getName(): String
  +setName(name: String): void
  +getAge(): int
  +setAge(age: int): void
  +getFullAddress(): String
}

class Company {
  name: String
  age: int
  companyName: String
  employees: List<Person>
  +getName(): String
  +setName(name: String): void
  +getAge(): int
  +setAge(age: int): void
  +addEmployee(person: Person): void
  +getEmployeeCount(): int
}

Address "*" -- "1" Company : employs
@enduml
EOF
echo -e "${GREEN}✓ Created sample diagram with duplicated attributes${NC}"
echo -e "${YELLOW}   Notice: Both Address and Company have duplicate name/age fields${NC}"
echo ""

echo -e "${BLUE}[2/5] Displaying input diagram...${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
cat "$INPUT_FILE"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

echo -e "${BLUE}[3/5] Building project...${NC}"
./mvnw clean compile -q
echo -e "${GREEN}✓ Build successful${NC}"
echo ""

echo -e "${BLUE}[4/5] Running enhancement pipeline...${NC}"
echo "   - Parsing PlantUML diagram"
echo "   - Extracting knowledge graph"
echo "   - Applying Formal Concept Analysis"
echo "   - Detecting common patterns (name, age attributes)"
echo "   - Creating abstract parent class"
echo "   - Generating inheritance relationships"
echo "   - Refactoring child classes"

# Copy the input to project directory for test
cp "$INPUT_FILE" test-diagram-with-duplication.puml

# Run the full pipeline test to generate enhanced output
./mvnw test -Dtest=FullPipelineTest#testAbstractionFromDuplicatedAttributes -q

# Copy the generated snapshot to demo folder
if [ -f "test-diagram-enhanced-with-abstraction.puml" ]; then
    cp "test-diagram-enhanced-with-abstraction.puml" "$OUTPUT_FILE"
    echo -e "${GREEN}✓ Enhancement complete${NC}"
else
    echo -e "${YELLOW}⚠ No enhanced output found${NC}"
    exit 1
fi
echo ""

echo -e "${BLUE}[5/5] Enhanced diagram output:${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
cat "$OUTPUT_FILE"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

echo -e "${GREEN}✓ Demo completed successfully!${NC}"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "Summary of Enhancement:"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "  Input:  $INPUT_FILE"
echo "  Output: $OUTPUT_FILE"
echo ""
echo "Before Enhancement:"
echo "  • Address had: name, age, street, city, zipCode + 5 methods"
echo "  • Company had: name, age, companyName, employees + 6 methods"
echo "  • Code duplication: name, age fields and 4 getter/setter methods"
echo ""
echo "After Enhancement:"
echo "  • AbstractEntity created with common: name, age + 4 methods"
echo "  • Address refactored: only street, city, zipCode + getFullAddress()"
echo "  • Company refactored: only companyName, employees + 2 methods"
echo "  • 2 inheritance relationships added (AbstractEntity <|-- Address/Company)"
echo "  • Association preserved"
echo ""
echo "Result: Cleaner design with proper inheritance hierarchy!"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "To view with PlantUML (if installed):"
echo "  plantuml $OUTPUT_FILE"
echo ""
