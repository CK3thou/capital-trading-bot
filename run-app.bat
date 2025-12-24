@echo off
setlocal enableextensions enabledelayedexpansion

REM Set explicitly for this process
set JAVA_HOME=C:\Program Files\Java\jdk-24
set JAVA_TOOL_OPTIONS=-Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8

REM Add Maven to PATH
set M2_HOME=C:\Users\justthatuser\.maven\maven-3.9.11
set PATH=!M2_HOME!\bin;!JAVA_HOME!\bin;%PATH%

REM Change to project directory
cd /d "c:\Users\justthatuser\Documents\GitHub\capital-trading-bot"

REM Show startup info
echo.
echo ============================================
echo Trading Bot Application Startup
echo ============================================
echo JAVA_HOME: !JAVA_HOME!
echo M2_HOME: !M2_HOME!
echo.
echo Starting application on http://localhost:8080
echo Press Ctrl+C to stop
echo ============================================
echo.

REM Build and run
call !M2_HOME!\bin\mvn spring-boot:run

endlocal

