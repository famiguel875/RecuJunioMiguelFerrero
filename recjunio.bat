@echo off
REM Execute the Java application with parameters

REM Check if the Java executable is available
java -version >nul 2>&1
if not %errorlevel%==0 (
    echo Java is not installed or not configured in the system PATH.
    pause
    exit /b
)

REM Run the jar file with all parameters passed to this script
java -jar recjunio.jar %*
if %errorlevel% neq 0 (
    echo The application exited with an error.
    pause
    exit /b
)