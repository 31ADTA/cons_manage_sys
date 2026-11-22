# Construction Management System

A multi-tiered construction management platform featuring a Java 21 Spring Boot backend, a containerized infrastructure setup, and an Expo/React Native mobile client.

---

## Repository Structure

```
.
├── backend/          # Java 21 Spring Boot application (Maven)
├── infra/            # Containerized infrastructure (PostgreSQL 16 Docker Compose)
├── mobile/           # React Native / Expo mobile application
├── .env.example      # Environment variables template
├── .gitignore        # Global git exclusions
├── PROJECT_RULES.md  # Core project conventions and architectural rules
└── README.md         # Project documentation and setup guide
```

---

## Prerequisites

- **Java**: OpenJDK 21 LTS (e.g., Eclipse Temurin 21)
- **Maven**: 3.9+ (or included Maven wrapper `mvnw`)
- **Docker**: Docker Engine & Docker Compose (v2+)
- **Node.js**: v18+ & npm/yarn (for mobile app development)

---

## Quick Start Guide

### 1. Environment Setup

Copy `.env.example` to `.env`:

```bash
cp .env.example .env
```

### 2. Start PostgreSQL 16 Database

Launch the isolated PostgreSQL 16 database container from `infra/`:

```bash
docker compose -f infra/docker-compose.yml up -d
```

Verify that the container is healthy:

```bash
docker compose -f infra/docker-compose.yml ps
```

To stop the database:

```bash
docker compose -f infra/docker-compose.yml down
```

### 3. Run the Backend Application

Navigate to the `backend/` directory:

```bash
cd backend
```

Ensure `JAVA_HOME` points to Java 21 (e.g. on macOS):
```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH
```

Build the application:

```bash
./mvnw clean package
# Or with global maven:
# mvn clean package
```

Run the application:

```bash
./mvnw spring-boot:run
# Or with global maven:
# mvn spring-boot:run
```

The server starts on port `8080` by default.

---

## API Endpoints & Documentation

- **Health Check**:
  - `GET http://localhost:8080/api/health`
  - Returns service status, timestamp, and database connectivity.
- **Swagger UI (OpenAPI 3.0)**:
  - `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON Spec**:
  - `http://localhost:8080/v3/api-docs`

---

## Architecture & Conventions

Please review [PROJECT_RULES.md](PROJECT_RULES.md) for engineering standards, database migration policies (Flyway), coding conventions, and architectural boundary constraints.
