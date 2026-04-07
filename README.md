# kra-api

Backend del proyecto KRA. API REST construida con Spring Boot 3.x (Java 17), arquitectura DDD y persistencia en DynamoDB vía AWS SDK v2.

## Requisitos previos

- Java 17+
- Maven (el wrapper `./mvnw` está incluido, no hace falta instalarlo)
- Docker Desktop — solo para los tests de integración con Testcontainers
- AWS CLI configurado — solo para ejecutar contra AWS real

## Compilar

```bash
./mvnw compile
```

## Ejecutar

**Contra AWS real:**

```bash
./mvnw spring-boot:run
```

Requiere credenciales AWS activas (`aws configure` o variables de entorno). La API arranca en `http://localhost:8080`.

**Contra DynamoDB Local (sin AWS):**

1. Levanta DynamoDB Local con Docker:
   ```bash
   docker run -p 8000:8000 amazon/dynamodb-local:latest
   ```

2. Añade en `src/main/resources/application.properties`:
   ```properties
   aws.dynamodb.endpoint-override=http://localhost:8000
   ```

3. Ejecuta normalmente:
   ```bash
   ./mvnw spring-boot:run
   ```

## Tests

**Unitarios** (rápidos, sin Docker ni AWS):
```bash
./mvnw test -Dtest="ProjectTest,ProjectIdTest"
```

**Contexto Spring** (carga el ApplicationContext):
```bash
./mvnw test -Dtest="KraApiApplicationTests"
```

**Integración con DynamoDB Local** (requiere Docker Desktop activo):
```bash
./mvnw test -Dtest="DynamoDbProjectRepositoryIT"
```

**Suite completa:**
```bash
./mvnw test
```

## Empaquetar

```bash
./mvnw package -DskipTests
java -jar target/kra-api-0.0.1-SNAPSHOT.jar
```

## Configuración

| Propiedad | Default | Descripción |
|-----------|---------|-------------|
| `aws.region` | `eu-west-1` | Región AWS |
| `aws.dynamodb.table-name` | `kra-table` | Nombre de la tabla DynamoDB |
| `aws.dynamodb.endpoint-override` | _(vacío)_ | URL para DynamoDB Local; vacío = AWS real |
| `server.port` | `8080` | Puerto del servidor |

## Arquitectura

Sigue DDD pragmático con tres capas:

```
com.kra.api
├── domain/
│   ├── model/          # Project, ProjectId — sin dependencias de framework
│   └── repository/     # ProjectRepository (interfaz/puerto)
├── application/        # ProjectService (casos de uso)
└── infrastructure/
    ├── config/         # DynamoDbConfig — beans Spring
    └── repository/     # DynamoDbProjectRepository, ProjectDynamoDbItem
```

La capa `domain` no importa nada de Spring ni de AWS SDK. Todo el detalle de infraestructura queda en `infrastructure/`.
