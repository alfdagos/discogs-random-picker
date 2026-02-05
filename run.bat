@echo off
REM Discogs Random Picker - Windows Launch Script
REM Version 2.0

setlocal

REM Set the JAR file path
set JAR_FILE=target\discogs-random-picker-1.0-SNAPSHOT.jar

REM Check if JAR exists
if not exist "%JAR_FILE%" (
    echo Building project...
    call mvn clean package
    if errorlevel 1 (
        echo Build failed!
        exit /b 1
    )
)

REM Run the application with all arguments
java -jar "%JAR_FILE%" %*

endlocal
