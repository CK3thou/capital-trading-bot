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

REM Recompile and run
call !M2_HOME!\bin\mvn clean compile spring-boot:run

endlocal
