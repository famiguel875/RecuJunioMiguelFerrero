@echo off
REM Compile the project and create an UberJar for the current OS
call ./gradlew packageUberJarForCurrentOS

REM Check if the compilation was successful
if not exist "./build/compose/jars/recjunio-windows-x64-1.0.0.jar" (
    echo Compilation failed, jar file not found.
    pause
    exit /b
)

REM Move and rename the jar file to the working directory
move /Y build\compose\jars\recjunio-windows-x64-1.0.0.jar .\recjunio.jar
if %errorlevel% neq 0 (
    echo Failed to move and rename the jar file.
    pause
    exit /b
)

echo Successfully moved and renamed the jar file.
pause