# kra-api

Backend for the KRA project. REST API built with Spring Boot 3.x (Java 21), DDD architecture, and persistence in DynamoDB via AWS SDK v2.

## Prerequisites

- Java 21+
- Maven
- Docker Desktop — only for integration tests with Testcontainers
- Configured AWS CLI — only for running against real AWS

## Compile

```bash
mvn compile
```

## Run

**Against real AWS:**

```bash
mvn spring-boot:run
```

Requires active AWS credentials (`aws configure` or environment variables). The API starts at `http://localhost:8080`.

**Against DynamoDB Local (without AWS):**

1. Start DynamoDB Local with Docker:
   ```bash
   docker run -p 8000:8000 amazon/dynamodb-local:latest
   ```

2. Add the following to `src/main/resources/application.properties`:
   ```properties
   aws.dynamodb.endpoint-override=http://localhost:8000
   ```

3. Run normally:
   ```bash
   mvn spring-boot:run
   ```

## Tests

**Unit tests** (fast, no Docker or AWS):
```bash
mvn test -Dtest="ProjectTest,ProjectIdTest"
```

**Spring Context** (loads the ApplicationContext):
```bash
mvn test -Dtest="KraApiApplicationTests"
```

**DynamoDB Local Integration** (requires Docker Desktop active):
```bash
mvn test -Dtest="DynamoDbProjectRepositoryIT"
```

**Full suite:**
```bash
mvn test
```

## Package

```bash
mvn package -DskipTests
java -jar target/kra-api-0.0.1-SNAPSHOT.jar
```

## Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `aws.region` | `eu-west-1` | AWS Region |
| `aws.dynamodb.table-name` | `kra-table` | DynamoDB table name |
| `aws.dynamodb.endpoint-override` | _(empty)_ | URL for DynamoDB Local; empty = real AWS |
| `server.port` | `8080` | Server port |

## Architecture

Follows pragmatic DDD with three layers:

```
com.kra.api
├── domain/
│   ├── model/          # Project, ProjectId — no framework dependencies
│   └── repository/     # ProjectRepository (interface/port)
├── application/        # ProjectService (use cases)
└── infrastructure/
    ├── config/         # DynamoDbConfig — Spring beans
    └── repository/     # DynamoDbProjectRepository, ProjectDynamoDbItem
```

The `domain` layer does not import anything from Spring or the AWS SDK. All infrastructure details reside in `infrastructure/`.
