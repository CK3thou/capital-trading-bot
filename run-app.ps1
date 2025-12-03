#!/usr/bin/env pwsh

# Set Java Home
$env:JAVA_HOME = "C:\Program Files\Java\jdk-24"
$env:PATH = $env:PATH + ";C:\Users\justthatuser\.maven\maven-3.9.11\bin"

# Change to project directory
Set-Location "c:\Users\justthatuser\Documents\GitHub\capital-trading-bot"

Write-Host "============================================"
Write-Host "Starting Trading Bot Application..."
Write-Host "============================================"
Write-Host "JAVA_HOME: $($env:JAVA_HOME)"
Write-Host "Maven: C:\Users\justthatuser\.maven\maven-3.9.11\bin\mvn"
Write-Host ""
Write-Host "Building and starting application..."
Write-Host "Access at: http://localhost:8080"
Write-Host "============================================"
Write-Host ""

# Run Maven spring-boot:run
& "C:\Users\justthatuser\.maven\maven-3.9.11\bin\mvn" spring-boot:run
