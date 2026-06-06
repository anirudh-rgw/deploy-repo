# Deployment Tracker Service - Design Document

**Date:** 2026-06-06  
**Status:** Approved

## Overview

A Spring Boot REST API service for tracking and querying deployment events. The service provides read-only access to deployment data via two REST endpoints with filtering capabilities.

## Technology Stack

- **Java 21** - Latest LTS with modern language features
- **Spring Boot 3.3.x** - Web framework and dependency management
- **Spring Data JPA** - Data access layer
- **H2 Database** - In-memory database (no persistence)
- **Lombok** - Reduce boilerplate code
- **JUnit 5 + MockMvc** - Testing framework

## Project Structure

```
deployment-tracker/
├── pom.xml
├── README.md
├── src/
│   ├── main/
│   │   ├── java/com/deploytracker/
│   │   │   ├── DeploymentTrackerApplication.java
│   │   │   ├── controller/
│   │   │   │   └── DeploymentController.java
│   │   │   ├── service/
│   │   │   │   └── DeploymentService.java
│   │   │   ├── repository/
│   │   │   │   └── DeploymentRepository.java
│   │   │   ├── model/
│   │   │   │   └── Deployment.java
│   │   │   ├── dto/
│   │   │   │   ├── DeploymentResponse.java
│   │   │   │   └── ErrorResponse.java
│   │   │   ├── exception/
│   │   │   │   ├── DeploymentNotFoundException.java
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   └── config/
│   │   │       └── DataSeeder.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/com/deploytracker/
│           └── DeploymentControllerTest.java
```

## Architecture

**Layered Architecture Pattern:**

1. **Controller Layer** - REST endpoints, request validation, HTTP concerns
2. **Service Layer** - Business logic, filtering operations
3. **Repository Layer** - Spring Data JPA for database access
4. **Model/Entity** - JPA entities with H2 in-memory database
5. **Exception Handling** - Global exception handler for consistent error responses

## Data Model

### Deployment Entity

```java
@Entity
public class Deployment {
    @Id
    private String id;              // e.g., "deploy_123"
    private String service;         // e.g., "billing-api"
    private String status;          // "success", "failed", "in_progress"
    private Integer duration;       // Duration in seconds (30-600)
    private LocalDateTime timestamp;
    private String commitSha;       // e.g., "abc123def456"
}
```

**Field Specifications:**
- `id`: Unique identifier (deploy_001, deploy_002, etc.)
- `service`: One of 6 services (billing-api, user-service, payment-gateway, notification-service, analytics-engine, auth-service)
- `status`: One of 3 statuses (success ~60%, failed ~30%, in_progress ~10%)
- `duration`: Random integer between 30-600 seconds
- `timestamp`: ISO-8601 formatted, spread over last 30 days
- `commitSha`: Random hexadecimal string (6-8 chars)

## API Endpoints

### 1. GET /deployments

List all deployments with optional filtering.

**Query Parameters:**
- `service` (optional) - Filter by service name (exact match)
- `status` (optional) - Filter by status (exact match)
- Multiple filters use AND logic

**Response (200 OK):**
```json
{
  "data": [
    {
      "id": "deploy_123",
      "service": "billing-api",
      "status": "failed",
      "duration": 320,
      "timestamp": "2025-04-28T14:32:00Z",
      "commitSha": "abc123"
    }
  ],
  "count": 1
}
```

**Error Response (400 Bad Request):**
```json
{
  "error": "Invalid filter value",
  "status": 400,
  "timestamp": "2026-06-06T12:45:30Z",
  "path": "/deployments"
}
```

### 2. GET /deployments/:id

Retrieve a single deployment by ID.

**Path Parameters:**
- `id` - Deployment ID

**Response (200 OK):**
```json
{
  "id": "deploy_123",
  "service": "billing-api",
  "status": "failed",
  "duration": 320,
  "timestamp": "2025-04-28T14:32:00Z",
  "commitSha": "abc123"
}
```

**Error Response (404 Not Found):**
```json
{
  "error": "Deployment not found",
  "status": 404,
  "timestamp": "2026-06-06T12:45:30Z",
  "path": "/deployments/invalid_id"
}
```

## Data Seeding

### Strategy

A `DataSeeder` component runs on application startup using `@PostConstruct` to populate the in-memory database.

### Seeding Specifications

- **Total Events:** 30-35 deployments
- **Services (6):** billing-api, user-service, payment-gateway, notification-service, analytics-engine, auth-service
- **Status Distribution:** success (~60%), failed (~30%), in_progress (~10%)
- **Events per Service:** 5-6 deployments each
- **Timestamp Range:** Evenly distributed over last 30 days
- **Duration Range:** Random 30-600 seconds
- **ID Pattern:** deploy_001, deploy_002, etc.
- **Commit SHAs:** Random 6-8 character hex strings

### Startup Validation

Log messages on successful seeding:
```
Seeding database with 32 deployments...
Services: 6, Success: 19, Failed: 10, In Progress: 3
Application started successfully on port 8080
```

## Configuration

### application.yml

```yaml
spring:
  application:
    name: deployment-tracker
  datasource:
    url: jdbc:h2:mem:deploydb
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: false
  h2:
    console:
      enabled: false

server:
  port: 8080
```

### Database

- **Type:** H2 in-memory database
- **Lifecycle:** Data exists only during application runtime
- **Schema:** Auto-created from JPA entities on startup
- **No persistence:** Data lost on application restart

## Testing & Sanity Checks

### Integration Tests

```java
@SpringBootTest
@AutoConfigureMockMvc
class DeploymentControllerTest {
    // Test cases:
    // 1. GET /deployments returns 200 and all deployments
    // 2. GET /deployments?service=billing-api filters correctly
    // 3. GET /deployments?status=failed filters correctly
    // 4. GET /deployments?service=X&status=Y applies both filters
    // 5. GET /deployments/:id returns 200 with correct deployment
    // 6. GET /deployments/invalid_id returns 404 with error response
    // 7. Response JSON structure matches specification
}
```

### Sanity Checks

1. **Startup Validation:**
   - Verify 30+ events seeded successfully
   - Ensure all 6 services are represented
   - Confirm all 3 statuses exist in dataset

2. **API Validation:**
   - All timestamps in ISO-8601 format
   - All required fields present in responses
   - Proper HTTP status codes (200, 404, 400)
   - Consistent error response shapes

3. **Health Check:**
   - Spring Boot Actuator `/actuator/health` endpoint
   - Returns service status and database connectivity

### Test Coverage

- **Controller Layer:** All endpoints with success and error cases
- **Service Layer:** All filter combinations
- **Repository Layer:** Basic CRUD operations

## Runtime Requirements

### Quick Start Goals

- ✅ **Zero setup time** - No database installation or configuration needed
- ✅ **Single command** - `mvn spring-boot:run` starts everything
- ✅ **Under 2 minutes** - From clone to running API (including Maven downloads)
- ✅ **Instant feedback** - Startup logs show seeded data statistics
- ✅ **Works first try** - No environment variables or external dependencies

### Maven Dependencies

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## README Structure

The README will include:

1. **Project Description** - Brief overview
2. **Requirements** - Java 21, Maven 3.6+
3. **Quick Start** - Clone and run command
4. **API Endpoints** - List with examples
5. **Example Requests** - curl commands for testing
6. **Running Tests** - `mvn test` command

## Success Criteria

The implementation will be considered successful when:

1. ✅ Application starts on first try with `mvn spring-boot:run`
2. ✅ 30+ deployment events seeded automatically on startup
3. ✅ Both API endpoints work correctly with proper filtering
4. ✅ Error handling returns consistent error responses
5. ✅ All tests pass
6. ✅ Response formats match specification exactly
7. ✅ README allows running service locally in under 2 minutes
8. ✅ Code follows production-quality organization patterns

## Non-Requirements

- Persistence beyond application runtime
- Authentication/Authorization
- CRUD operations (POST/PUT/DELETE)
- External configuration
- Docker containerization
- Production deployment configuration
