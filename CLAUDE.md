# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local   # port 8080 (override with SERVER_PORT)
mvn test                                                # unit + slice tests
mvn verify                                              # tests + JaCoCo report + 80% branch coverage gate
mvn test -Dtest="BlogPostServiceTest,ProjectTest"       # run specific test classes
mvn package -DskipTests                                 # build runnable JAR
```

Health check: `GET http://localhost:8080/actuator/health`

JaCoCo excludes from coverage measurement: `KraApiApplication`, `infrastructure.config`, `infrastructure.repository`, `infrastructure.github`.

## Required env vars

Copy `.env.example` → `.env`. The dotenv-java library loads it only when a key is **not** already set in the OS environment.

| Variable                            | Notes                                                                                 |
| ----------------------------------- | ------------------------------------------------------------------------------------- |
| `AWS_DYNAMODB_TABLE_NAME`           | Required at startup                                                                   |
| `AWS_S3_BUCKET_NAME`                | Required at startup                                                                   |
| `COGNITO_ISSUER_URI`                | Required for JWT validation (`https://cognito-idp.eu-west-1.amazonaws.com/<pool-id>`) |
| `GITHUB_TOKEN`                      | Fine-scoped PAT for portfolio BFF                                                     |
| `GITHUB_PORTFOLIO_USER`             | GitHub username whose repos are listed                                                |
| `MEMCACHED_HOST` / `MEMCACHED_PORT` | Defaults: `localhost:11211`                                                           |

`AWS_DYNAMODB_ENDPOINT_OVERRIDE` is in `.env.example` but the current `DynamoDbConfig` builder does **not** read it — extend `DynamoDbConfig` if you need LocalStack.

## Architecture

### DDD layering

```
infrastructure.web          @RestController, DTOs, RateLimitInterceptor
application                 @Service — orchestrates use cases, owns NotFound exceptions
domain.model                Pure Java aggregates — zero Spring/AWS imports
domain.repository           Port interfaces (save, findById, findAll, delete)
infrastructure.repository   DynamoDB adapters implementing domain ports
infrastructure.github       GitHubPortfolioClient (Spring WebClient)
infrastructure.cache        Memcached integration (xmemcached, custom CacheManager)
infrastructure.config       DynamoDB/S3/Security/CORS/GitHub configuration beans
infrastructure.cloudwatch   CloudWatchMetricsService
```

**The domain layer must never import Spring or AWS SDK.** Security is an infrastructure concern exclusively — no Spring Security imports in domain or application.

### DynamoDB access pattern

All repositories extend `AbstractDynamoDbRepository<DomainType, DynamoDbItem>`. The base class owns:

- `buildKey(id)` → `PK = <pkPrefix><id>`, `SK = "METADATA"`
- `findAllByGsi1(typeValue, ...)` → queries the `GSI1` index by `GSI1PK` (e.g. `TYPE#POST`, `TYPE#LEAD`)

Each entity has a matching `*DynamoDbItem` annotated class that maps to the table schema. Domain objects are converted by `fromDomain()` / `toDomain()` lambdas passed to the base class methods.

### Security

`SecurityConfig` is STATELESS. All `POST`/`PUT`/`DELETE` endpoints require a valid Cognito JWT (RS256, validated against Cognito JWKS). Public: `GET /posts/**`, `GET /projects/**`, `GET /portfolio/**`, `GET /activity/**`, `GET /cv/**`, `GET /config/**`, `GET /images/**`, `GET /skills/**`, `POST /contact`, `GET /actuator/health`.

`CustomAuthenticationEntryPoint` returns `{"error":"UNAUTHORIZED"}` on 401. `CustomAccessDeniedHandler` returns `{"error":"FORBIDDEN"}` on 403.

### Rate limiting

`RateLimitInterceptor` (Bucket4j in-process token bucket) is registered in `WebMvcConfig`. Per-IP, applied to public write-adjacent endpoints.

### Controller test pattern

All `@WebMvcTest` controller tests extend `AbstractControllerTest`, which imports `SecurityConfig`, `CustomAuthenticationEntryPoint`, `CustomAccessDeniedHandler`, and `GlobalExceptionHandler` — ensuring security and error handling are exercised in slice tests. Use `@WithMockUser` or `@WithAnonymousUser` to control auth context.

## Domain entities

| Entity            | PK prefix      | GSI1PK            | Notes                                              |
| ----------------- | -------------- | ----------------- | -------------------------------------------------- |
| `Project`         | `PROJECT#`     | —                 | Has `ProjectId` value object                       |
| `BlogPost`        | `POST#`        | `TYPE#POST`       | `BlogSlug` value object, supports `Reference` list |
| `Lead`            | `LEAD#`        | `TYPE#LEAD`       | Contact form submission                            |
| `Experience`      | `EXPERIENCE#`  | `TYPE#EXPERIENCE` | CV timeline, extends `TimelineEntity`              |
| `Education`       | `EDUCATION#`   | `TYPE#EDUCATION`  | CV timeline, extends `TimelineEntity`              |
| `SkillCategory`   | `SKILL#`       | `TYPE#SKILL`      | CV skills                                          |
| `ActivityCard`    | `ACTIVITY#`    | `TYPE#ACTIVITY`   | Home page activity feed                            |
| `AppConfig`       | `CONFIG#`      | —                 | Profile/site config (singleton-ish)                |
| `ProjectMetadata` | `PROJECTMETA#` | —                 | Extra metadata per project                         |
