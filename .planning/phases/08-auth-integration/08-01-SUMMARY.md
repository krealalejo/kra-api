---
phase: 08-auth-integration
plan: "01"
subsystem: security
tags: [spring-security, oauth2, jwt, cognito, authentication]
dependency_graph:
  requires:
    - Phase 4 Cognito User Pool (eu-west-1_sDg68GJKt)
    - Phase 7 ProjectController endpoints
  provides:
    - JWT authentication enforcement on POST/PUT/DELETE /projects
    - Custom 401/403 JSON error responses matching GlobalExceptionHandler format
    - SecurityFilterChain bean wired with Cognito issuer-uri
  affects:
    - ProjectControllerTest (added @Import, .with(jwt()), 5 new auth tests)
    - application.properties (Cognito issuer-uri added)
    - pom.xml (Spring Security + OAuth2 Resource Server dependencies)
tech_stack:
  added:
    - spring-boot-starter-security (3.5.0 BOM managed)
    - spring-boot-starter-oauth2-resource-server (3.5.0 BOM managed)
    - spring-security-test (test scope, 3.5.0 BOM managed)
  patterns:
    - OAuth2 Resource Server with JWT (stateless, no sessions, CSRF disabled)
    - Custom AuthenticationEntryPoint and AccessDeniedHandler for uniform error format
    - @Import(SecurityConfig + handlers) in @WebMvcTest for security slice testing
key_files:
  created:
    - src/main/java/com/kra/api/infrastructure/config/SecurityConfig.java
    - src/main/java/com/kra/api/infrastructure/security/CustomAuthenticationEntryPoint.java
    - src/main/java/com/kra/api/infrastructure/security/CustomAccessDeniedHandler.java
  modified:
    - pom.xml
    - src/main/resources/application.properties
    - src/test/java/com/kra/api/infrastructure/web/ProjectControllerTest.java
decisions:
  - CSRF disabled in SecurityConfig — stateless JWT REST API does not require CSRF protection; Bearer token authentication is not susceptible to CSRF attacks
  - Session management set to STATELESS — JWT resource server must not create server-side sessions
  - @Import includes both SecurityConfig and handler classes — @WebMvcTest does not component-scan outside the web layer, so @Component handlers must be explicitly imported
metrics:
  duration_seconds: 395
  completed_date: "2026-04-08"
  tasks_completed: 4
  files_changed: 6
---

# Phase 8 Plan 01: Spring Security OAuth2 JWT Auth Integration Summary

**One-liner:** JWT auth enforcement via Spring Security OAuth2 Resource Server backed by AWS Cognito (eu-west-1_sDg68GJKt), with custom 401/403 JSON handlers matching GlobalExceptionHandler format.

## Tasks Completed

| Task | Name | Commit | Files |
|------|------|--------|-------|
| 0 | Add @Disabled auth test stubs (Wave 0 scaffold) | 69f7b77 | ProjectControllerTest.java |
| 1 | Add Spring Security + OAuth2 dependencies to pom.xml | b79757e | pom.xml |
| 2 | Create SecurityConfig.java, add Cognito issuer-uri, activate auth tests | 7860879 | SecurityConfig.java, application.properties, ProjectControllerTest.java |
| 3 | Create CustomAuthenticationEntryPoint + CustomAccessDeniedHandler | 7860879 | CustomAuthenticationEntryPoint.java, CustomAccessDeniedHandler.java |

Note: Tasks 2 and 3 were committed together since SecurityConfig references the handler classes — they form a single atomic compilation unit.

## Verification Results

```
Tests run: 40, Failures: 0, Errors: 0, Skipped: 0 — BUILD SUCCESS
```

All 16 ProjectControllerTest tests pass including the 5 new auth tests:
- createProject_noToken_returns401 → HTTP 401
- createProject_withValidJwt_returns201 → HTTP 201 (with Cognito JWT claims)
- listProjects_noToken_returns200 → HTTP 200 (public endpoint)
- updateProject_noToken_returns401 → HTTP 401
- deleteProject_noToken_returns401 → HTTP 401

## Decisions Made

1. **CSRF disabled** — Stateless JWT REST API does not require CSRF protection. Bearer token authentication is not susceptible to CSRF attacks (no cookies). Standard practice for OAuth2 resource servers.

2. **Session management set to STATELESS** — JWT resource server must not create server-side sessions. Required for correct stateless OAuth2 resource server behavior.

3. **@Import includes handler classes explicitly** — `@WebMvcTest` only component-scans the web layer. `@Component` beans in `infrastructure.security` are not auto-discovered in the test slice context, so `CustomAuthenticationEntryPoint` and `CustomAccessDeniedHandler` must be explicitly listed in `@Import({SecurityConfig.class, CustomAuthenticationEntryPoint.class, CustomAccessDeniedHandler.class})`.

## Deviations from Plan

### Auto-fixed Issues

**1. [Rule 2 - Missing critical functionality] CSRF disabled and session management set to STATELESS**
- **Found during:** Task 2/3 verification
- **Issue:** Three auth tests (createProject_noToken_returns401, updateProject_noToken_returns401, deleteProject_noToken_returns401) returned HTTP 403 instead of 401. Spring Security's default CSRF protection was intercepting unauthenticated POST/PUT/DELETE requests and returning 403 (CSRF token missing) before the authentication check ran.
- **Fix:** Added `.csrf(csrf -> csrf.disable())` and `.sessionManagement(session -> session.sessionCreationPolicy(STATELESS))` to SecurityConfig. This is the correct configuration for a stateless JWT resource server — CSRF protection is not needed when authentication is done via Bearer tokens.
- **Files modified:** src/main/java/com/kra/api/infrastructure/config/SecurityConfig.java
- **Commit:** 7860879

**2. [Rule 3 - Blocking issue] Explicit @Import of handler beans in test**
- **Found during:** Task 2/3 verification
- **Issue:** `@WebMvcTest` with `@Import(SecurityConfig.class)` failed to start the application context because `CustomAuthenticationEntryPoint` and `CustomAccessDeniedHandler` are `@Component` beans outside the web layer slice, not auto-discovered by `@WebMvcTest`.
- **Fix:** Updated `@Import` to `@Import({SecurityConfig.class, CustomAuthenticationEntryPoint.class, CustomAccessDeniedHandler.class})`.
- **Files modified:** src/test/java/com/kra/api/infrastructure/web/ProjectControllerTest.java
- **Commit:** 7860879

## Threat Mitigations Applied

All STRIDE threats from the plan's threat model were addressed:

| Threat | Status |
|--------|--------|
| T-08-01: Unauthenticated write access | Mitigated — SecurityFilterChain rejects POST/PUT/DELETE without Bearer token |
| T-08-02: JWT from wrong issuer | Mitigated — Spring Security validates `iss` claim against Cognito issuer-uri |
| T-08-03: Forged JWT signature | Mitigated — JwtDecoder validates RS256 against Cognito JWKS |
| T-08-05: Error body leaks internals | Mitigated — Custom handlers write controlled JSON only |
| T-08-07: Expired JWT re-use | Mitigated — JwtDecoder validates `exp` claim automatically |
| T-08-09: Wrong issuer-uri format | Mitigated — No trailing slash, verified by acceptance criteria |
| T-08-10: @Import missing in tests | Mitigated — All handler classes explicitly imported |
| T-08-11: new ObjectMapper() instead of injected bean | Mitigated — Both handlers use constructor-injected Spring bean |

## Known Stubs

None — all functionality is fully wired. Cognito JWKS discovery is handled by Spring Security using the configured issuer-uri.

## Self-Check: PASSED

- src/main/java/com/kra/api/infrastructure/config/SecurityConfig.java — FOUND
- src/main/java/com/kra/api/infrastructure/security/CustomAuthenticationEntryPoint.java — FOUND
- src/main/java/com/kra/api/infrastructure/security/CustomAccessDeniedHandler.java — FOUND
- Commit 69f7b77 (Task 0) — FOUND
- Commit b79757e (Task 1) — FOUND
- Commit 7860879 (Tasks 2+3) — FOUND
- Full test suite: 40/40 passing — VERIFIED
