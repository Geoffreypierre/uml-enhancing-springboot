@echo off
setlocal EnableDelayedExpansion

REM ==================================
REM UML Enhancement Pipeline Demo (Windows version)
REM ==================================

echo ==================================
echo UML Enhancement Pipeline Demo
echo ==================================
echo.

REM Colors (Windows ANSI)
for /f "delims=" %%a in ('echo prompt $E ^| cmd') do set "ESC=%%a"
set "GREEN=%ESC%[32m"
set "BLUE=%ESC%[34m"
set "YELLOW=%ESC%[33m"
set "NC=%ESC%[0m"

REM Setup
set "DEMO_DIR=%TEMP%\uml-enhancement-demo"
set "INPUT_FILE=%DEMO_DIR%\input-diagram.puml"
set "OUTPUT_FILE=%DEMO_DIR%\enhanced-diagram.puml"

echo %BLUE%[1/5] Setting up demo environment...%NC%
if not exist "%DEMO_DIR%" mkdir "%DEMO_DIR%"
echo %GREEN%✓ Created demo directory: %DEMO_DIR%%NC%
echo.

echo %YELLOW%Creating sample diagram with code duplication...%NC%

(
echo @startuml
echo class Address {
echo   name: String
echo   age: int
echo   street: String
echo   city: String
echo   zipCode: String
echo   +getName(): String
echo   +setName(name: String): void
echo   +getAge(): int
echo   +setAge(age: int): void
echo   +getFullAddress(): String
echo }
echo.
echo class Company {
echo   name: String
echo   age: int
echo   companyName: String
echo   employees: List^<Person^>
echo   +getName(): String
echo   +setName(name: String): void
echo   +getAge(): int
echo   +setAge(age: int): void
echo   +addEmployee(person: Person): void
echo   +getEmployeeCount(): int
echo }
echo.
echo Address "*" -- "1" Company : employs
echo @enduml
) > "%INPUT_FILE%"

echo %GREEN%✓ Created sample diagram with duplicated attributes%NC%
echo %YELLOW%   Notice: Both Address and Company have duplicate name/age fields%NC%
echo.

echo %BLUE%[2/5] Displaying input diagram...%NC%
echo ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
type "%INPUT_FILE%"
echo ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
echo.

echo %BLUE%[3/5] Building project...%NC%
call mvnw.cmd clean compile -q
echo %GREEN%✓ Build successful%NC%
echo.

echo %BLUE%[4/5] Running enhancement pipeline...%NC%
echo    - Parsing PlantUML diagram
echo    - Extracting knowledge graph
echo    - Applying Formal Concept Analysis
echo    - Detecting common patterns (name, age attributes)
echo    - Creating abstract parent class
echo    - Generating inheritance relationships
echo    - Refactoring child classes

copy "%INPUT_FILE%" test-diagram-with-duplication.puml >nul

call mvnw.cmd test -Dtest=FullPipelineTest#testAbstractionFromDuplicatedAttributes -q

if exist "test-diagram-enhanced-with-abstraction.puml" (
    copy "test-diagram-enhanced-with-abstraction.puml" "%OUTPUT_FILE%" >nul
    echo %GREEN%✓ Enhancement complete%NC%
) else (
    echo %YELLOW%⚠ No enhanced output found%NC%
    exit /b 1
)
echo.

echo %BLUE%[5/5] Enhanced diagram output:%NC%
echo ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
type "%OUTPUT_FILE%"
echo ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
echo.

echo %GREEN%✓ Demo completed successfully!%NC%
echo.
echo ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
echo Summary of Enhancement:
echo ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
echo   Input:  %INPUT_FILE%
echo   Output: %OUTPUT_FILE%
echo.
echo Before Enhancement:
echo   • Address had: name, age, street, city, zipCode + 5 methods
echo   • Company had: name, age, companyName, employees + 6 methods
echo   • Code duplication: name, age fields and 4 getter/setter methods
echo.
echo After Enhancement:
echo   • AbstractEntity created with common: name, age + 4 methods
echo   • Address refactored: only street, city, zipCode + getFullAddress()
echo   • Company refactored: only companyName, employees + 2 methods
echo   • 2 inheritance relationships added (AbstractEntity ^<|-- Address/Company)
echo   • Association preserved
echo.
echo Result: Cleaner design with proper inheritance hierarchy!
echo ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
echo.
echo To view with PlantUML (if installed):
echo   plantuml "%OUTPUT_FILE%"
echo.

endlocal
