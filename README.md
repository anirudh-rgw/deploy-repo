# Deployment Tracker Service

A Spring Boot REST API service for tracking and querying deployment events.

## Overview

This service provides read-only access to deployment event data via REST endpoints. It uses an in-memory H2 database pre-seeded with 30+ mock deployment events across multiple services.

## Requirements

- **Java 21** or higher
- **Maven 3.6+**

## Quick Start

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd deployment-tracker
   ```

2. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

3. **Application starts on** `http://localhost:8080`

The application will automatically seed the database with 32 deployment events on startup.

## API Endpoints

### 1. List Deployments

**GET** `/deployments`

List all deployments with optional filtering.

**Query Parameters:**
- `service` (optional) - Filter by service name
- `status` (optional) - Filter by status

**Example Requests:**
```bash
# Get all deployments
curl http://localhost:8080/deployments

# Filter by service
curl http://localhost:8080/deployments?service=billing-api

# Filter by status
curl http://localhost:8080/deployments?status=failed

# Filter by both
curl http://localhost:8080/deployments?service=billing-api&status=success
```

**Response:**
```json
{
  "data": [
    {
      "id": "deploy_001",
      "service": "billing-api",
      "status": "success",
      "duration": 245,
      "timestamp": "2026-05-15T10:23:45",
      "commitSha": "a3b5c7"
    }
  ],
  "count": 1
}
```

### 2. Get Deployment by ID

**GET** `/deployments/{id}`

Retrieve a single deployment by its ID.

**Example Request:**
```bash
curl http://localhost:8080/deployments/deploy_001
```

**Response:**
```json
{
  "id": "deploy_001",
  "service": "billing-api",
  "status": "success",
  "duration": 245,
  "timestamp": "2026-05-15T10:23:45",
  "commitSha": "a3b5c7"
}
```

**Error Response (404):**
```json
{
  "error": "Deployment not found: invalid_id",
  "status": 404,
  "timestamp": "2026-06-06T12:45:30",
  "path": "/deployments/invalid_id"
}
```

## Health Check

**GET** `/actuator/health`

Check the application health status.

```bash
curl http://localhost:8080/actuator/health
```

## Running Tests

Execute all tests:
```bash
mvn test
```

Run application and tests:
```bash
mvn clean verify
```

## Mock Data

The application seeds the database with:
- **32 deployment events**
- **6 services:** billing-api, user-service, payment-gateway, notification-service, analytics-engine, auth-service
- **3 statuses:** success (~60%), failed (~30%), in_progress (~10%)
- **Timestamps:** Distributed over the last 30 days
- **Durations:** Random between 30-600 seconds

## Technology Stack

- Spring Boot 3.3.0
- Java 21
- Spring Data JPA
- H2 In-Memory Database
- Lombok
- JUnit 5 & MockMvc

## Project Structure

```
src/
├── main/
│   ├── java/com/deploytracker/
│   │   ├── DeploymentTrackerApplication.java
│   │   ├── config/DataSeeder.java
│   │   ├── controller/DeploymentController.java
│   │   ├── dto/
│   │   ├── exception/
│   │   ├── model/Deployment.java
│   │   ├── repository/DeploymentRepository.java
│   │   └── service/DeploymentService.java
│   └── resources/application.yml
└── test/java/com/deploytracker/
    └── DeploymentControllerTest.java
```

## Notes

- Data is stored in-memory and will be lost when the application stops
- The H2 database is recreated on each startup
- No authentication or authorization is required
- All timestamps are in ISO-8601 format