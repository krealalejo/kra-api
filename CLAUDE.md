# CLAUDE.md — kra-api

## Interaction Rules

- **Language:** All code, comments, variable names, and documentation files must be written in English.
- **Git Strategy:** Trunk Based Development. Each GSD phase executes on a dedicated branch `feat/phase-NN-<slug>` branched from `main`. Never commit directly to `main`.
- **Commits:** Conventional Commits — `feat:`, `fix:`, `chore:`, `docs:`, `refactor:`, `test:`.
- **Atomic:** Split commits at the file or resource level — one new file = one commit, one file extension = separate commit. Do not bundle multiple files or concerns into a single commit.

## API-Specific Rules

- **DDD layering:** The `domain` package must never import Spring or AWS SDK. Repository interfaces live in `domain.repository`; implementations live in `infrastructure.persistence`.
- **Tests:** Use `@WebMvcTest` for controller slice tests, plain JUnit 5 for domain unit tests, Testcontainers for repository integration tests. No mixing of test categories.
- **Coverage:** JaCoCo branch minimum 80% enforced by `mvn verify`. Do not skip or lower the threshold.
- **Security:** All write endpoints (`POST`, `PUT`, `DELETE`) require an authenticated Cognito JWT via Spring Security OAuth2 resource server. Public reads and `POST /contact` use `permitAll()`.
- **No hardcoded credentials:** Use default AWS credential chain (EC2 IAM role in prod, `AWS_PROFILE` locally). Never commit `.env` or access keys.
- **Exception mapping:** Use `GlobalExceptionHandler` (`@ControllerAdvice`) for all error responses. Return structured JSON `{error, message}` — no raw stack traces in API responses.

## Repository Context

Spring Boot 3.5 / Java 21 REST API with DDD-style layering:
- **Domain layer** (`com.kra.api.domain`): Project, BlogPost, Lead aggregates + value objects + repository port interfaces — pure Java, zero framework coupling
- **Application layer** (`com.kra.api.application`): ProjectService, BlogPostService, ContactService — orchestrates domain logic
- **Infrastructure layer** (`com.kra.api.infrastructure`):
  - `persistence/`: DynamoDB Enhanced Client adapters implementing domain ports
  - `web/`: Spring MVC controllers + DTOs + GlobalExceptionHandler
  - `github/`: GitHubWebClient BFF for portfolio data
  - `config/`: SecurityConfig (OAuth2 resource server), DynamoDbConfig, WebCorsConfiguration

See [kra-docs-architecture](https://github.com/krealalejo/kra-docs-architecture) for the full system C4 diagrams.

## Current Phase: 21 — C4 Final & Launch

All features complete. This phase adds README badges, this CLAUDE.md, and C4 architecture diagrams only. No production code changes.
