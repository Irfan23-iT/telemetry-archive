# CorsaData-Analytics

## Overview
A high-performance Java-based telemetry system designed to ingest, process, and visualize real-time racing data from Assetto Corsa for advanced driver performance analysis.

## Key Features
* Real-Time Monitoring: Continuous ingestion and display of telemetry data.
* Data Visualization: Clean, structured interface for tracking system metrics and performance indicators.
* Scalable Architecture: Built on standard Java frameworks to ensure maintainability and efficient data processing.

## Technology Stack
* Core Language: Java
* Framework: Spring Boot
* Build Automation: Maven
* IDE Support: Configured for NetBeans, IntelliJ IDEA, STS, and VS Code

## Installation and Setup Instructions

### 1. Clone the Repository
Execute the following command to pull the source code to your local machine:

    git clone https://github.com/yourusername/telemetry-dashboard.git

### 2. Navigate to the Directory

    cd telemetry-dashboard

### 3. Build the Project
Resolve all Maven dependencies and compile the project using the Maven wrapper:

    ./mvnw clean install

### 4. Execute the Application
Initialize the server and launch the application:

    ./mvnw spring-boot:run

## System Access
Once the application server has successfully initialized, the telemetry dashboard can be accessed via a standard web browser at:
http://localhost:8080

## Snapshot 
http://localhost:3000/dashboard/snapshot/gGiniBQxKu9gXROvg6kvxBOsUBKjv4hH?orgId=1&from=2026-04-06T08:26:04.469Z&to=2026-04-06T08:31:04.469Z&timezone=browser&refresh=5s

## Project Context
Author: Mohamad Irfan Danish Bin Azani

Description: Comprehensive telemetry dashboard developed for system monitoring.
