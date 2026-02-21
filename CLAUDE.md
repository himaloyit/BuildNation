# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**BuildNation** is a Political Party Management System (PPMS) built as a Spring Boot microservices platform. It's designed for political parties and government/NGO organizations.

**Tech Stack:** Java 17, Spring Boot 3.4.5, Spring Cloud 2024.0.1, PostgreSQL, Redis, Kafka, Netflix Eureka, MapStruct, Lombok

## Build & Run Commands

All commands are run from the repo root (`BuildNation/`) using the Maven wrapper in each service directory.

```bash
# Build a specific service
cd member-management && ./mvnw clean package

# Run a specific service
cd member-management && ./mvnw spring-boot:run

# Run all tests for a service
cd member-management && ./mvnw test

# Run a single test class
cd member-management && ./mvnw test -Dtest=MemberServiceTest

# Build Docker image (from service directory, after mvn package)
cd member-management && docker build -t member-management .
```

The root `pom.xml` is a parent POM (`<packaging>pom</packaging>`). Each microservice is a self-contained Maven module with its own `pom.xml` inheriting from `spring-boot-starter-parent`.

## Architecture

### Microservice Modules (defined in root pom.xml)
Only `member-management` has implementation. Planned modules:
- `eureka-service-discovery` – Service registry
- `gateway-service-config` – API Gateway (Spring Cloud Gateway)
- `security-access-control` – JWT auth + RBAC
- `member-management` – Member CRUD (the reference implementation)
- `constituency-region-management`, `event-campaign-management`, `volunteer-management`, `donations-fundraising`, `internal-communication-media`, `task-workflow-management`, `election-management`, `analytics-reporting`, `policy-manifesto-management`, `grievance-feedback-management`, `build-nation-web-portal`, `notification`

### Package Structure (pattern for all services)
```
com.himaloyit.buildnation.<module-abbreviation>
  controllers/
    rest/          # @RestController classes
    advice/        # @ControllerAdvice (GlobalResponseEntityException)
  domain/
    entities/      # JPA @Entity classes
    dto/           # Data Transfer Objects
    model/         # Request/response models (CreateXxxRequest, UpdateXxxRequest, ApiResponse)
    mapper/        # MapStruct mapper interfaces (prefix I)
    repositories/
      iRepositories/ # JPA repository interfaces (prefix I)
  services/
    iServices/     # Service interfaces (prefix I)
    impl/          # Service implementations
  util/
    exceptions/    # Custom exception classes
```

### Key Conventions

**Interface naming:** All interfaces are prefixed with `I` (e.g., `IMemberService`, `IMemberRepository`, `IMemberMapper`).

**API response wrapper:** All REST endpoints return `ApiResponse<T>` with fields: `timestamp`, `status`, `message`, `data`. Use static factory methods `ApiResponse.success(message, data)` and `ApiResponse.error(status, message)`.

**REST URL pattern:** `/api/v1/{resource}` (e.g., `/api/v1/members`)

**Entity IDs:** Always `UUID`, generated via `@GeneratedValue(strategy = GenerationType.AUTO)`.

**Mapping:** Use MapStruct (`IMemberMapper`) for entity↔DTO conversion. The mapper is a Spring-managed bean via `@Mapper(componentModel = "spring")`. For manual conversion in services, use a `toDTO()` helper method.

**Validation:** Bean Validation annotations on request models (`@NotBlank`, `@Email`, etc.); activate with `@Valid` on controller method parameters.

**Entity timestamps:** `createdAt` and `updatedAt` are `LocalDate` fields set manually in the service layer (not via JPA lifecycle callbacks).

### Member Entity Fields (reference for new entities)
`id` (UUID), `fullName`, `email` (unique), `phone`, `dob`, `gender`, `address`, `nationalId` (unique), `constituencyId` (UUID FK ref), `role`, `position`, `status` (`"ACTIVE"`/etc.), `createdAt`, `updatedAt`

## Infrastructure Dependencies
Each service expects:
- **PostgreSQL** – primary database
- **Redis** – caching
- **Kafka** – async messaging
- **Eureka** – service discovery (register as client via `spring-cloud-starter-netflix-eureka-client`)

Connection config goes in `src/main/resources/application.properties` (currently only `spring.application.name` is set; DB/Kafka/Eureka config must be added per environment).
