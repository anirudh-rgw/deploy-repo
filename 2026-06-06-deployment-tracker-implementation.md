# Deployment Tracker Service Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a Spring Boot REST API service for tracking and querying deployment events with in-memory H2 database, seeded mock data, and comprehensive tests.

**Architecture:** Layered architecture with Controller/Service/Repository pattern. REST endpoints for listing (with filters) and retrieving deployments. Global exception handling for consistent error responses. DataSeeder component populates database on startup.

**Tech Stack:** Spring Boot 3.3.x, Java 21, Spring Data JPA, H2 Database, Lombok, JUnit 5, MockMvc

---

## File Structure Overview

**Core Application:**
- `pom.xml` - Maven configuration with dependencies
- `src/main/java/com/deploytracker/DeploymentTrackerApplication.java` - Main Spring Boot application
- `src/main/resources/application.yml` - Application configuration

**Model Layer:**
- `src/main/java/com/deploytracker/model/Deployment.java` - JPA entity

**Repository Layer:**
- `src/main/java/com/deploytracker/repository/DeploymentRepository.java` - Spring Data JPA repository

**Service Layer:**
- `src/main/java/com/deploytracker/service/DeploymentService.java` - Business logic and filtering

**Controller Layer:**
- `src/main/java/com/deploytracker/controller/DeploymentController.java` - REST endpoints

**DTOs:**
- `src/main/java/com/deploytracker/dto/DeploymentResponse.java` - API response wrapper
- `src/main/java/com/deploytracker/dto/ErrorResponse.java` - Error response format

**Exception Handling:**
- `src/main/java/com/deploytracker/exception/DeploymentNotFoundException.java` - Custom exception
- `src/main/java/com/deploytracker/exception/GlobalExceptionHandler.java` - Exception handler

**Configuration:**
- `src/main/java/com/deploytracker/config/DataSeeder.java` - Seed mock data on startup

**Tests:**
- `src/test/java/com/deploytracker/DeploymentControllerTest.java` - Integration tests

**Documentation:**
- `README.md` - Setup and usage instructions

---

## Task 1: Maven Project Setup

**Files:**
- Create: `pom.xml`

- [ ] **Step 1: Create pom.xml with Spring Boot parent and dependencies**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.0</version>
        <relativePath/>
    </parent>

    <groupId>com.deploytracker</groupId>
    <artifactId>deployment-tracker</artifactId>
    <version>1.0.0</version>
    <name>Deployment Tracker Service</name>
    <description>REST API for tracking deployment events</description>

    <properties>
        <java.version>21</java.version>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
    </properties>

    <dependencies>
        <!-- Spring Boot Web Starter -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Spring Boot Data JPA -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <!-- H2 Database -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- Spring Boot Actuator for health checks -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <!-- Spring Boot Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 2: Verify Maven configuration**

Run: `mvn validate`

Expected: `BUILD SUCCESS`

- [ ] **Step 3: Commit Maven setup**

```bash
git add pom.xml
git commit -m "feat: add Maven configuration with Spring Boot dependencies"
```

---

## Task 2: Application Configuration

**Files:**
- Create: `src/main/resources/application.yml`

- [ ] **Step 1: Create application.yml with database and server configuration**

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

management:
  endpoints:
    web:
      exposure:
        include: health
  endpoint:
    health:
      show-details: always
```

- [ ] **Step 2: Create directory structure**

Run: `mkdir -p src/main/resources src/main/java/com/deploytracker src/test/java/com/deploytracker`

Expected: Directories created

- [ ] **Step 3: Commit configuration**

```bash
git add src/main/resources/application.yml
git commit -m "feat: add application configuration for H2 database and server"
```

---

## Task 3: Deployment Entity Model

**Files:**
- Create: `src/main/java/com/deploytracker/model/Deployment.java`

- [ ] **Step 1: Create Deployment entity with JPA annotations**

```java
package com.deploytracker.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "deployments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Deployment {
    
    @Id
    private String id;
    
    private String service;
    
    private String status;
    
    private Integer duration;
    
    private LocalDateTime timestamp;
    
    private String commitSha;
}
```

- [ ] **Step 2: Create directory if needed**

Run: `mkdir -p src/main/java/com/deploytracker/model`

Expected: Directory created

- [ ] **Step 3: Commit entity model**

```bash
git add src/main/java/com/deploytracker/model/Deployment.java
git commit -m "feat: add Deployment JPA entity model"
```

---

## Task 4: Repository Layer

**Files:**
- Create: `src/main/java/com/deploytracker/repository/DeploymentRepository.java`

- [ ] **Step 1: Create Spring Data JPA repository interface**

```java
package com.deploytracker.repository;

import com.deploytracker.model.Deployment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeploymentRepository extends JpaRepository<Deployment, String> {
    
    List<Deployment> findByService(String service);
    
    List<Deployment> findByStatus(String status);
    
    List<Deployment> findByServiceAndStatus(String service, String status);
}
```

- [ ] **Step 2: Create directory if needed**

Run: `mkdir -p src/main/java/com/deploytracker/repository`

Expected: Directory created

- [ ] **Step 3: Commit repository**

```bash
git add src/main/java/com/deploytracker/repository/DeploymentRepository.java
git commit -m "feat: add DeploymentRepository with filter methods"
```

---

## Task 5: Response DTOs

**Files:**
- Create: `src/main/java/com/deploytracker/dto/DeploymentResponse.java`
- Create: `src/main/java/com/deploytracker/dto/ErrorResponse.java`

- [ ] **Step 1: Create DeploymentResponse DTO**

```java
package com.deploytracker.dto;

import com.deploytracker.model.Deployment;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DeploymentResponse {
    private List<Deployment> data;
    private int count;
}
```

- [ ] **Step 2: Create ErrorResponse DTO**

```java
package com.deploytracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String error;
    private int status;
    private LocalDateTime timestamp;
    private String path;
}
```

- [ ] **Step 3: Create directory if needed**

Run: `mkdir -p src/main/java/com/deploytracker/dto`

Expected: Directory created

- [ ] **Step 4: Commit DTOs**

```bash
git add src/main/java/com/deploytracker/dto/
git commit -m "feat: add response DTOs for API responses and errors"
```

---

## Task 6: Custom Exception

**Files:**
- Create: `src/main/java/com/deploytracker/exception/DeploymentNotFoundException.java`

- [ ] **Step 1: Create custom exception class**

```java
package com.deploytracker.exception;

public class DeploymentNotFoundException extends RuntimeException {
    
    public DeploymentNotFoundException(String id) {
        super("Deployment not found: " + id);
    }
}
```

- [ ] **Step 2: Create directory if needed**

Run: `mkdir -p src/main/java/com/deploytracker/exception`

Expected: Directory created

- [ ] **Step 3: Commit exception**

```bash
git add src/main/java/com/deploytracker/exception/DeploymentNotFoundException.java
git commit -m "feat: add DeploymentNotFoundException custom exception"
```

---

## Task 7: Global Exception Handler

**Files:**
- Create: `src/main/java/com/deploytracker/exception/GlobalExceptionHandler.java`

- [ ] **Step 1: Create global exception handler with @ControllerAdvice**

```java
package com.deploytracker.exception;

import com.deploytracker.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(DeploymentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDeploymentNotFound(
            DeploymentNotFoundException ex,
            WebRequest request) {
        
        ErrorResponse error = new ErrorResponse(
            ex.getMessage(),
            HttpStatus.NOT_FOUND.value(),
            LocalDateTime.now(),
            request.getDescription(false).replace("uri=", "")
        );
        
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            WebRequest request) {
        
        ErrorResponse error = new ErrorResponse(
            "Internal server error",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            LocalDateTime.now(),
            request.getDescription(false).replace("uri=", "")
        );
        
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```

- [ ] **Step 2: Commit exception handler**

```bash
git add src/main/java/com/deploytracker/exception/GlobalExceptionHandler.java
git commit -m "feat: add global exception handler for consistent error responses"
```

---

## Task 8: Service Layer

**Files:**
- Create: `src/main/java/com/deploytracker/service/DeploymentService.java`

- [ ] **Step 1: Create DeploymentService with business logic**

```java
package com.deploytracker.service;

import com.deploytracker.exception.DeploymentNotFoundException;
import com.deploytracker.model.Deployment;
import com.deploytracker.repository.DeploymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeploymentService {
    
    private final DeploymentRepository repository;
    
    public DeploymentService(DeploymentRepository repository) {
        this.repository = repository;
    }
    
    public List<Deployment> getAllDeployments() {
        return repository.findAll();
    }
    
    public Deployment getDeploymentById(String id) {
        return repository.findById(id)
            .orElseThrow(() -> new DeploymentNotFoundException(id));
    }
    
    public List<Deployment> getDeploymentsByFilters(String service, String status) {
        if (service != null && status != null) {
            return repository.findByServiceAndStatus(service, status);
        } else if (service != null) {
            return repository.findByService(service);
        } else if (status != null) {
            return repository.findByStatus(status);
        } else {
            return repository.findAll();
        }
    }
}
```

- [ ] **Step 2: Create directory if needed**

Run: `mkdir -p src/main/java/com/deploytracker/service`

Expected: Directory created

- [ ] **Step 3: Commit service layer**

```bash
git add src/main/java/com/deploytracker/service/DeploymentService.java
git commit -m "feat: add DeploymentService with filtering logic"
```

---

## Task 9: Controller Layer

**Files:**
- Create: `src/main/java/com/deploytracker/controller/DeploymentController.java`

- [ ] **Step 1: Create REST controller with two endpoints**

```java
package com.deploytracker.controller;

import com.deploytracker.dto.DeploymentResponse;
import com.deploytracker.model.Deployment;
import com.deploytracker.service.DeploymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/deployments")
public class DeploymentController {
    
    private final DeploymentService service;
    
    public DeploymentController(DeploymentService service) {
        this.service = service;
    }
    
    @GetMapping
    public ResponseEntity<DeploymentResponse> getDeployments(
            @RequestParam(required = false) String service,
            @RequestParam(required = false) String status) {
        
        List<Deployment> deployments = this.service.getDeploymentsByFilters(service, status);
        DeploymentResponse response = new DeploymentResponse(deployments, deployments.size());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Deployment> getDeploymentById(@PathVariable String id) {
        Deployment deployment = service.getDeploymentById(id);
        return ResponseEntity.ok(deployment);
    }
}
```

- [ ] **Step 2: Create directory if needed**

Run: `mkdir -p src/main/java/com/deploytracker/controller`

Expected: Directory created

- [ ] **Step 3: Commit controller**

```bash
git add src/main/java/com/deploytracker/controller/DeploymentController.java
git commit -m "feat: add REST controller with list and get endpoints"
```

---

## Task 10: Data Seeder

**Files:**
- Create: `src/main/java/com/deploytracker/config/DataSeeder.java`

- [ ] **Step 1: Create DataSeeder component with @PostConstruct**

```java
package com.deploytracker.config;

import com.deploytracker.model.Deployment;
import com.deploytracker.repository.DeploymentRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class DataSeeder {
    
    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);
    
    private final DeploymentRepository repository;
    private final Random random = new Random();
    
    private static final String[] SERVICES = {
        "billing-api",
        "user-service",
        "payment-gateway",
        "notification-service",
        "analytics-engine",
        "auth-service"
    };
    
    private static final String[] STATUSES = {"success", "failed", "in_progress"};
    private static final double[] STATUS_WEIGHTS = {0.6, 0.3, 0.1};
    
    public DataSeeder(DeploymentRepository repository) {
        this.repository = repository;
    }
    
    @PostConstruct
    public void seedData() {
        logger.info("Seeding database with deployment data...");
        
        List<Deployment> deployments = new ArrayList<>();
        int deploymentCount = 32;
        
        for (int i = 1; i <= deploymentCount; i++) {
            String id = String.format("deploy_%03d", i);
            String service = SERVICES[(i - 1) % SERVICES.length];
            String status = getWeightedRandomStatus();
            Integer duration = 30 + random.nextInt(571);
            LocalDateTime timestamp = LocalDateTime.now().minusDays(random.nextInt(30))
                .minusHours(random.nextInt(24))
                .minusMinutes(random.nextInt(60));
            String commitSha = generateRandomCommitSha();
            
            deployments.add(new Deployment(id, service, status, duration, timestamp, commitSha));
        }
        
        repository.saveAll(deployments);
        
        long successCount = deployments.stream().filter(d -> "success".equals(d.getStatus())).count();
        long failedCount = deployments.stream().filter(d -> "failed".equals(d.getStatus())).count();
        long inProgressCount = deployments.stream().filter(d -> "in_progress".equals(d.getStatus())).count();
        
        logger.info("Seeding complete: {} deployments", deploymentCount);
        logger.info("Services: {}, Success: {}, Failed: {}, In Progress: {}", 
            SERVICES.length, successCount, failedCount, inProgressCount);
    }
    
    private String getWeightedRandomStatus() {
        double value = random.nextDouble();
        double cumulative = 0.0;
        
        for (int i = 0; i < STATUSES.length; i++) {
            cumulative += STATUS_WEIGHTS[i];
            if (value <= cumulative) {
                return STATUSES[i];
            }
        }
        
        return STATUSES[0];
    }
    
    private String generateRandomCommitSha() {
        int length = 6 + random.nextInt(3);
        StringBuilder sha = new StringBuilder();
        String hexChars = "0123456789abcdef";
        
        for (int i = 0; i < length; i++) {
            sha.append(hexChars.charAt(random.nextInt(hexChars.length())));
        }
        
        return sha.toString();
    }
}
```

- [ ] **Step 2: Create directory if needed**

Run: `mkdir -p src/main/java/com/deploytracker/config`

Expected: Directory created

- [ ] **Step 3: Commit data seeder**

```bash
git add src/main/java/com/deploytracker/config/DataSeeder.java
git commit -m "feat: add DataSeeder to populate database with 32 mock deployments"
```

---

## Task 11: Main Application Class

**Files:**
- Create: `src/main/java/com/deploytracker/DeploymentTrackerApplication.java`

- [ ] **Step 1: Create Spring Boot main application class**

```java
package com.deploytracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DeploymentTrackerApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(DeploymentTrackerApplication.class, args);
    }
}
```

- [ ] **Step 2: Commit main application**

```bash
git add src/main/java/com/deploytracker/DeploymentTrackerApplication.java
git commit -m "feat: add main Spring Boot application class"
```

- [ ] **Step 3: Verify application compiles**

Run: `mvn clean compile`

Expected: `BUILD SUCCESS`

- [ ] **Step 4: Test application startup**

Run: `mvn spring-boot:run` (let it run for 10 seconds, then Ctrl+C)

Expected output should include:
```
Seeding database with deployment data...
Seeding complete: 32 deployments
Services: 6, Success: XX, Failed: XX, In Progress: XX
Started DeploymentTrackerApplication in X.XXX seconds
```

- [ ] **Step 5: Commit verification note**

```bash
git commit --allow-empty -m "verify: application starts successfully with seeded data"
```

---

## Task 12: Integration Tests

**Files:**
- Create: `src/test/java/com/deploytracker/DeploymentControllerTest.java`

- [ ] **Step 1: Create comprehensive integration tests**

```java
package com.deploytracker;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DeploymentControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void testGetAllDeployments() throws Exception {
        mockMvc.perform(get("/deployments"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data", hasSize(greaterThan(30))))
            .andExpect(jsonPath("$.count", greaterThan(30)));
    }
    
    @Test
    void testGetDeploymentsByService() throws Exception {
        mockMvc.perform(get("/deployments")
                .param("service", "billing-api"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[*].service", everyItem(is("billing-api"))))
            .andExpect(jsonPath("$.count", greaterThan(0)));
    }
    
    @Test
    void testGetDeploymentsByStatus() throws Exception {
        mockMvc.perform(get("/deployments")
                .param("status", "failed"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[*].status", everyItem(is("failed"))))
            .andExpect(jsonPath("$.count", greaterThan(0)));
    }
    
    @Test
    void testGetDeploymentsByServiceAndStatus() throws Exception {
        mockMvc.perform(get("/deployments")
                .param("service", "billing-api")
                .param("status", "success"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[*].service", everyItem(is("billing-api"))))
            .andExpect(jsonPath("$.data[*].status", everyItem(is("success"))));
    }
    
    @Test
    void testGetDeploymentById() throws Exception {
        mockMvc.perform(get("/deployments/deploy_001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("deploy_001"))
            .andExpect(jsonPath("$.service").exists())
            .andExpect(jsonPath("$.status").exists())
            .andExpect(jsonPath("$.duration").exists())
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.commitSha").exists());
    }
    
    @Test
    void testGetDeploymentByIdNotFound() throws Exception {
        mockMvc.perform(get("/deployments/invalid_id"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value(containsString("Deployment not found")))
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.path").value("/deployments/invalid_id"));
    }
    
    @Test
    void testResponseStructure() throws Exception {
        mockMvc.perform(get("/deployments"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[0].id").exists())
            .andExpect(jsonPath("$.data[0].service").exists())
            .andExpect(jsonPath("$.data[0].status").exists())
            .andExpect(jsonPath("$.data[0].duration").isNumber())
            .andExpect(jsonPath("$.data[0].timestamp").exists())
            .andExpect(jsonPath("$.data[0].commitSha").exists())
            .andExpect(jsonPath("$.count").isNumber());
    }
}
```

- [ ] **Step 2: Create directory if needed**

Run: `mkdir -p src/test/java/com/deploytracker`

Expected: Directory created

- [ ] **Step 3: Run tests**

Run: `mvn test`

Expected: All 7 tests should PASS

- [ ] **Step 4: Commit tests**

```bash
git add src/test/java/com/deploytracker/DeploymentControllerTest.java
git commit -m "test: add comprehensive integration tests for all endpoints"
```

---

## Task 13: README Documentation

**Files:**
- Modify: `README.md`

- [ ] **Step 1: Write comprehensive README**

```markdown
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
```

- [ ] **Step 2: Commit README**

```bash
git add README.md
git commit -m "docs: add comprehensive README with setup and API documentation"
```

- [ ] **Step 3: Final verification - run full build**

Run: `mvn clean install`

Expected: 
- BUILD SUCCESS
- All tests pass
- JAR file created in `target/` directory

- [ ] **Step 4: Manual API testing**

Start the application:
```bash
mvn spring-boot:run
```

Test each endpoint in a new terminal:
```bash
curl http://localhost:8080/deployments
curl http://localhost:8080/deployments?service=billing-api
curl http://localhost:8080/deployments?status=failed
curl http://localhost:8080/deployments/deploy_001
curl http://localhost:8080/deployments/invalid_id
curl http://localhost:8080/actuator/health
```

Verify:
- All endpoints return expected responses
- Error responses have correct structure
- Health check returns UP status
- Seeding logs appear on startup

- [ ] **Step 5: Final commit**

```bash
git commit --allow-empty -m "verify: all endpoints tested and working correctly"
```

---

## Plan Complete

All tasks completed. The deployment tracker service is ready with:
- ✅ Spring Boot application with Java 21
- ✅ Two REST endpoints (list with filters, get by id)
- ✅ In-memory H2 database
- ✅ 32 mock deployment events seeded on startup
- ✅ Global exception handling
- ✅ Comprehensive integration tests
- ✅ Complete README documentation
- ✅ Runs successfully with `mvn spring-boot:run`
