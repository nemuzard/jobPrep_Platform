# JobPrep Backend

This is the Spring Boot backend for the JobPrep platform. It provides REST APIs for user accounts, notes, collections, questions, comments, resume matching, and learning recommendations.

## Tech Stack

- Java 17
- Spring Boot 3.3
- Maven
- MyBatis
- MySQL
- Redis
- RabbitMQ
- PostgreSQL with pgvector

## Prerequisites

Install these before running the backend locally:

- Java 17
- Docker and Docker Compose

The project includes the Maven wrapper, so a separate Maven install is not required.

## Run Locally

From the repository root, start the local services:

```bash
docker compose up -d mysql redis rabbitmq pgvector
```

Then start the backend:

```bash
cd backend
./mvnw spring-boot:run
```

The backend runs on:

```text
http://localhost:8080
```

Health check:

```text
GET http://localhost:8080/api/health
```

Expected response:

```text
OK
```

## Local Service Defaults

The default development configuration uses:

- MySQL: `localhost:3306`
- Redis: `localhost:6380`
- RabbitMQ: `localhost:5672`
- RabbitMQ Management UI: `http://localhost:15672`
- pgvector PostgreSQL: `localhost:5433`

Default local usernames and passwords are defined in the root `docker-compose.yml`.

## Configuration

Main configuration files:

- `src/main/resources/application.yaml`
- `src/main/resources/application-dev.yaml`

Useful environment variables:

- `MYSQL_USER`
- `MYSQL_PASSWORD`
- `REDIS_HOST`
- `REDIS_PORT`
- `RABBITMQ_HOST`
- `RABBITMQ_PORT`
- `RABBITMQ_USERNAME`
- `RABBITMQ_PASSWORD`
- `JWT_SECRET`
- `MAIL_USERNAME`
- `MAIL_PASSWORD`
- `AI_PROVIDER`
- `OLLAMA_BASE_URL`
- `OLLAMA_MODEL`
- `RAG_VECTOR_JDBC_URL`
- `RAG_VECTOR_USERNAME`
- `RAG_VECTOR_PASSWORD`

## Common Commands

Run tests:

```bash
./mvnw test
```

Build the application:

```bash
./mvnw package
```

Build without tests:

```bash
./mvnw -DskipTests package
```

Build the Docker image:

```bash
docker build -t jobprep-backend .
```

## Project Structure

```text
src/main/java/com/jobprep/jobprep_platform
├── config        # Spring and feature configuration
├── controller    # REST controllers
├── mapper        # MyBatis mapper interfaces
├── model         # DTOs, entities, enums, and view objects
├── service       # Business logic
├── task          # Scheduled and async tasks
└── utils         # Shared utility code
```

SQL mapper XML files are in:

```text
src/main/resources/mapper
```
