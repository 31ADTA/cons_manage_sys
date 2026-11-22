# Construction Management System — Project Rules & Engineering Guidelines

This document defines the architectural standards, technical stack specifications, coding conventions, and project boundaries. **All future development tasks MUST read, adhere to, and maintain compliance with these rules.**

---

## 1. Directory Structure & Boundaries

The project root consists strictly of three core operational directories:

```
.
├── backend/          # Java 21 Spring Boot application
├── infra/            # Container orchestration and infrastructure definitions
├── mobile/           # React Native / Expo mobile application
├── .env.example      # Environment variables reference template
├── .gitignore        # Global git exclusion rules
├── PROJECT_RULES.md  # Architectural guidelines (this document)
└── README.md         # Setup and run documentation
```

### Boundary Constraints:
- **Do NOT create new top-level directories.** All code, configurations, or scripts must reside within `backend/`, `infra/`, or `mobile/`.
- **Prohibited Modules (Until Explicitly Requested):**
  - **NO Redis** (or any distributed cache cluster).
  - **NO S3 / Object Storage** (AWS S3, MinIO, Google Cloud Storage, etc.).
  - **NO WebSockets** (STOMP, socket.io, raw WebSockets).
  - **NO AI / LLM Integrations** (no OpenAI, LangChain, Spring AI, Anthropic SDKs).

---

## 2. Technology Stack

### Backend
- **Language**: Java 21 LTS (utilizing records, sealed classes, pattern matching).
- **Framework**: Spring Boot 3.3.x.
- **Build Tool**: Maven (with `mvnw` wrapper support).
- **Persistence**: Spring Data JPA & Hibernate.
- **Database Driver**: PostgreSQL JDBC driver (`org.postgresql:postgresql`).
- **Schema Migration**: Flyway (`flyway-core`, `flyway-database-postgresql`).
- **Validation**: Jakarta Bean Validation (`spring-boot-starter-validation`).
- **API Documentation**: Springdoc OpenAPI 2.x (Swagger UI at `/swagger-ui.html`, spec at `/v3/api-docs`).
- **Testing**: JUnit 5, Mockito, Spring Boot Starter Test (MockMvc).

### Infrastructure
- **PostgreSQL 16**: Managed via `infra/docker-compose.yml` (`postgres:16-alpine`).
- **Ports**: PostgreSQL mapped to host `5432`.

### Mobile
- **Framework**: React Native with Expo (to be scaffolded/expanded in later tasks).

---

## 3. Architecture & Design Patterns

### 3.1 Layered Backend Architecture
Backend code in `backend/src/main/java/com/construction/management/` must strictly adhere to the layered pattern:

```
com.construction.management/
├── config/         # Spring beans, security, OpenAPI configuration
├── controller/     # REST controllers handling HTTP requests and responses
├── dto/            # Request and Response transfer objects (Java 21 records)
├── entity/         # JPA database entities
├── exception/      # Custom exceptions and @RestControllerAdvice handler
├── repository/     # Spring Data JPA repositories
└── service/        # Business logic interfaces and implementations
```

### 3.2 DTOs & Validation
- **Entities must NEVER be exposed directly in API requests or responses.**
- Always map between JPA entities and DTOs.
- Represent DTOs using Java 21 `record` types wherever immutability applies.
- Enforce validation at the controller boundary using `@Valid` with Jakarta annotations (`@NotBlank`, `@NotNull`, `@Size`, `@Email`, etc.).

### 3.3 Database Migrations (Flyway)
- **All schema evolution must be driven exclusively through Flyway migrations.**
- Migration scripts reside in `backend/src/main/resources/db/migration/`.
- Naming convention: `V<Version>__<Description>.sql` (e.g., `V1__init.sql`, `V2__create_users_table.sql`).
- Hibernate's `ddl-auto` must remain set to `validate` in runtime environments to prevent uncontrolled schema drift.

### 3.4 REST API & OpenAPI Conventions
- All REST endpoints must be prefixed with `/api/` (e.g., `/api/health`, `/api/projects`).
- Use plural nouns for resource paths (`/api/contractors`, `/api/tasks`).
- Standard HTTP status codes:
  - `200 OK`: Successful retrieval or update.
  - `201 Created`: Successful resource creation (include `Location` header when applicable).
  - `204 No Content`: Successful deletion.
  - `400 Bad Request`: Validation failure or malformed payload.
  - `404 Not Found`: Target resource does not exist.
  - `409 Conflict`: Business rule violation or unique constraint collision.
  - `500 Internal Server Error`: Unhandled application errors.
- Every controller class must include `@Tag` and each endpoint must include `@Operation` and `@ApiResponse` documentation annotations.

---

## 4. Environment & Security Conventions

- **Never commit `.env` or secret credentials.** All `.env*` files are excluded by `.gitignore`, except `.env.example`.
- Keep `.env.example` up-to-date with any new environment variables required by the system.
- Application configuration in `application.yml` must use sensible fallback defaults for local development.

---

## 5. Development & Testing Commands

| Task | Command |
|---|---|
| **Start Database** | `docker compose -f infra/docker-compose.yml up -d` |
| **Stop Database** | `docker compose -f infra/docker-compose.yml down` |
| **Build Backend** | `cd backend && ./mvnw clean package` |
| **Run Backend** | `cd backend && ./mvnw spring-boot:run` |
| **Run Tests** | `cd backend && ./mvnw test` |
| **Access Swagger UI** | `http://localhost:8080/swagger-ui.html` |
| **Access OpenAPI JSON** | `http://localhost:8080/v3/api-docs` |
| **Health Check** | `http://localhost:8080/api/health` |
