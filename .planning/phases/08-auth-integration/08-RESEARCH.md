# Phase 8: Auth Integration - Research

**Researched:** 2026-04-08
**Domain:** Spring Security OAuth2 Resource Server with AWS Cognito JWT
**Confidence:** HIGH

## Summary

Phase 8 protects write endpoints (POST, PUT, DELETE) with AWS Cognito JWT tokens via Spring Security OAuth2 Resource Server, while keeping read endpoints (GET) publicly accessible. The implementation requires adding Spring Security OAuth2 starters to pom.xml, configuring JWT validation against Cognito's JWKS endpoint, and creating a SecurityFilterChain bean that enforces selective authorization by HTTP method. Testing uses Spring Security's `jwt()` MockMvc post-processor for unit tests.

**Primary recommendation:** Add `spring-boot-starter-oauth2-resource-server` (included automatically in Spring Boot 3.5.0), configure `spring.security.oauth2.resourceserver.jwt.issuer-uri` pointing to your Cognito User Pool, create a SecurityFilterChain bean that allows GET requests and requires authentication for POST/PUT/DELETE, and update existing tests with JWT claims.

## Standard Stack

### Core

| Library | Version | Purpose | Why Standard |
|---------|---------|---------|--------------|
| spring-boot-starter-oauth2-resource-server | 3.5.0 (included) | OAuth2 resource server support with JWT decoding | Spring Boot 3.5.0 includes this as transitive dependency via spring-boot-starter-security |
| spring-security-oauth2-jose | 6.5.0 (included) | JWT/JOSE token validation and verification | Required alongside resource-server for JWT Bearer Token support [CITED: Spring Security docs] |
| spring-boot-starter-security | 3.5.0 (included) | Core Spring Security framework | Already in pom.xml, provides SecurityFilterChain infrastructure |
| AWS Cognito User Pool | (Phase 4) | Authorization server, OIDC provider, JWT issuer | Validates JWT signatures via JWKS endpoint at `https://cognito-idp.<region>.amazonaws.com/<poolId>/.well-known/jwks.json` |

### Supporting

| Library | Version | Purpose | When to Use |
|---------|---------|---------|-------------|
| spring-security-test | 6.5.0 (included) | MockMvc jwt() post-processor for testing | Required for @WebMvcTest and JWT claim customization [VERIFIED: official Spring Security docs] |
| WireMock | (optional) | Mock JWKS endpoint for offline testing | Useful if testing without network access to real Cognito, or for startup delay scenarios |

### Alternatives Considered

| Instead of | Could Use | Tradeoff |
|------------|-----------|----------|
| Spring Security OAuth2 Resource Server | Cognito SDK direct JWT validation | More control, but requires manual JWKS fetching, caching, rotation handling — error-prone |
| JWT issuer-uri (auto-discovery) | Manual jwk-set-uri | Explicit jwk-set-uri needed if Cognito endpoint doesn't support /.well-known/openid-configuration, but Cognito does support it |
| Bearer Token (stateless JWT) | Opaque tokens (introspection) | Requires call to Cognito /oauth2/introspect for every request — adds latency, Cognito tokens are JWTs so stateless is natural |

**Installation:**
No new Maven dependencies required. Spring Boot 3.5.0 already includes all necessary OAuth2 and JWT libraries.

```bash
# Verify current Spring Boot version in pom.xml (should be 3.5.0)
grep "<version>3.5" pom.xml | head -1
```

## Architecture Patterns

### Recommended Project Structure

```
src/main/java/com/kra/api/
├── domain/
│   ├── model/          # Project, ProjectId
│   └── repository/     # ProjectRepository (interface)
├── application/        # ProjectService (use cases)
└── infrastructure/
    ├── config/
    │   ├── DynamoDbConfig.java      # [existing]
    │   └── SecurityConfig.java      # [NEW] OAuth2 resource server + selective authz
    ├── repository/     # [existing]
    └── web/
        ├── ProjectController.java   # [existing]
        └── GlobalExceptionHandler.java  # [existing, may need 401 handling]
```

### Pattern 1: SecurityFilterChain Bean for Selective Authorization

**What:** Configure HTTP authorization rules at the filter level to allow public GET access and require JWT for write operations.

**When to use:** All Spring Boot OAuth2 resource servers; this is the standard approach in Spring Security 6.x [VERIFIED: official Spring Security docs].

**Example:**

```java
package com.kra.api.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((authorize) -> authorize
                // GET requests: public, no authentication required
                .requestMatchers(HttpMethod.GET, "/projects/**").permitAll()
                // Write operations: require OAuth2 JWT authentication
                .requestMatchers(HttpMethod.POST, "/projects/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/projects/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/projects/**").authenticated()
                // Any other requests: authenticated (safe default)
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer((oauth2) -> oauth2
                .jwt(withDefaults())
            );
        
        return http.build();
    }
}
```

Source: [CITED: Spring Security Authorization docs](https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html)

### Pattern 2: JWT Configuration with Cognito Issuer URI

**What:** Single property configuration that enables automatic JWKS discovery and validation.

**When to use:** When your authorization server is OIDC-compliant (like Cognito) and supports /.well-known/openid-configuration endpoint.

**Configuration (application.properties):**

```properties
# OAuth2 Resource Server - Cognito JWT validation
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://cognito-idp.eu-west-1.amazonaws.com/eu-west-1_sDg68GJKt

# [Optional] Audience validation - uncomment if your app needs it
# spring.security.oauth2.resourceserver.jwt.audiences=your-app-resource-id
```

Source: [CITED: Spring Security JWT resource server docs](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html)

**What this property does:**
1. Resource Server discovers the JWKS endpoint via `/.well-known/openid-configuration`
2. On first JWT validation request, fetches Cognito's public keys (cached with 5-minute TTL)
3. Validates `iss` (issuer) claim against the configured issuer-uri
4. Validates JWT signature using Cognito's public key
5. Validates `exp` and `nbf` (expiration, not-before) claims automatically

### Pattern 3: Testing Endpoints with JWT Claims

**What:** Use Spring Security's `jwt()` MockMvc post-processor to simulate authenticated requests in tests.

**When to use:** In @WebMvcTest tests for endpoints protected by OAuth2 JWT.

**Example:**

```java
@WebMvcTest(ProjectController.class)
@Import(SecurityConfig.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectService projectService;

    // POST without JWT → 401
    @Test
    void createProject_noToken_returns401() throws Exception {
        mockMvc.perform(post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"My Project\"}"))
            .andExpect(status().isUnauthorized());
    }

    // POST with JWT → 201
    @Test
    void createProject_withValidJwt_returns201() throws Exception {
        Project fakeProject = new Project(ProjectId.of("abc-123"),
                "My Project", "desc", "https://url.com", "content");
        when(projectService.createProject(any(), any(), any(), any()))
            .thenReturn(fakeProject);

        mockMvc.perform(post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"My Project\",\"description\":\"desc\",\"url\":\"https://url.com\",\"content\":\"content\"}")
                .with(jwt()
                    .jwt(jwt -> jwt
                        .claim("sub", "cognito-user-id")
                        .claim("email", "[email protected]")
                    )
                )
            )
            .andExpect(status().isCreated());
    }

    // GET without JWT → 200 (public)
    @Test
    void listProjects_noToken_returns200() throws Exception {
        when(projectService.getAllProjects(50)).thenReturn(List.of());

        mockMvc.perform(get("/projects"))
            .andExpect(status().isOk());
    }
}
```

Import required: `import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;`

Source: [CITED: Spring Security MockMvc JWT testing](https://docs.spring.io/spring-security/reference/servlet/test/mockmvc/oauth2.html)

### Anti-Patterns to Avoid

- **Hardcoding user roles in application code:** Use `@PreAuthorize("hasRole('ADMIN')")` or SecurityFilterChain rules instead of if-statements checking user roles. Makes authorization testable and centralized.
- **Forgetting to import SecurityConfig in @WebMvcTest:** Use `@Import(SecurityConfig.class)` in test classes, otherwise SecurityFilterChain bean is not loaded and endpoints appear unprotected.
- **Validating JWT manually:** Don't write custom JWT parsing code. Spring Security's JwtDecoder handles signature validation, expiration, claims validation — using manual parsing leads to security bugs.
- **Missing issuer-uri validation:** Always configure `issuer-uri` (not just `jwk-set-uri`) so Spring validates the `iss` claim. Omitting this allows tokens from other issuers to pass validation.
- **Not handling missing/expired JWT:** Spring Security returns 401 Unauthorized by default. Ensure client code handles 401 responses and refreshes tokens using Cognito's refresh token flow.

## Don't Hand-Roll

| Problem | Don't Build | Use Instead | Why |
|---------|-------------|-------------|-----|
| JWT signature validation | Custom HMAC/RSA validation | Spring Security JwtDecoder | Key rotation, algorithm negotiation, caching — complex to handle correctly |
| JWKS caching | Manual HTTP client + HashMap | Spring's RemoteJWKSet (via issuer-uri) | Automatic 5-minute TTL, thread-safe, Cognito rotates keys periodically |
| Bearer token extraction | Custom header parsing | Spring Security's BearerTokenAuthenticationFilter (automatic) | RFC 6750 compliance, error handling (missing header vs. invalid format) |
| Authorization rules by HTTP method | Annotation-based @PreAuthorize | SecurityFilterChain.authorizeHttpRequests() | Declarative, testable, centralized in one place |

**Key insight:** Spring Security's OAuth2 resource server implementation is battle-tested in production systems. Custom JWT handling is a high-risk area for security bugs (e.g., not validating issuer, skipping signature checks). Use the framework.

## Common Pitfalls

### Pitfall 1: JWKS Endpoint Unavailable on Startup

**What goes wrong:** Application starts successfully, but first request with JWT hangs for 30+ seconds or fails with "Couldn't retrieve remote JWK set" error.

**Why it happens:** Spring Security loads JWKS lazily on first request (not at startup). If Cognito is unreachable or slow, the first request blocks. No error on startup means you don't catch this until runtime.

**How to avoid:** 
1. In dev/test, mock Cognito's JWKS endpoint (use WireMock or @MockBean JwtDecoder)
2. In production, ensure network connectivity to Cognito's JWKS endpoint is available
3. Configure a connection timeout property if needed (Spring Security uses default timeouts)

**Warning signs:**
- Application starts but `/projects` GET requests are fast, but POST with JWT hangs
- Error logs: "Couldn't retrieve remote JWK set" or timeout errors

### Pitfall 2: Wrong Issuer-URI Format

**What goes wrong:** JWT validation always fails with "Invalid issuer" error, even though the token is valid.

**Why it happens:** Cognito issuer-uri format is `https://cognito-idp.<region>.amazonaws.com/<poolId>` (no trailing slash, no `/.well-known`). Common mistakes:
- Using the JWKS URL directly: `https://cognito-idp.eu-west-1.amazonaws.com/eu-west-1_sDg68GJKt/.well-known/jwks.json` ← WRONG
- Including trailing slash: `https://cognito-idp.eu-west-1.amazonaws.com/eu-west-1_sDg68GJKt/` ← WRONG
- Using the ID token endpoint instead: `https://cognito-idp.eu-west-1.amazonaws.com/eu-west-1_sDg68GJKt/oauth2/authorize` ← WRONG

**How to avoid:** Use exactly this format: `https://cognito-idp.<AWS_REGION>.amazonaws.com/<USER_POOL_ID>` (Spring will append `/.well-known/openid-configuration` automatically).

**Warning signs:**
- JWT validation error: "Issuer claim does not match configured issuer" or similar
- Check application logs for the issuer-uri value being used

### Pitfall 3: @Import(SecurityConfig.class) Missing in @WebMvcTest

**What goes wrong:** Test GET /projects returns 200 OK (good), test POST /projects returns 201 Created (bad — should be 401).

**Why it happens:** Without `@Import(SecurityConfig.class)`, the SecurityFilterChain bean is not loaded during test context, so all endpoints appear unprotected.

**How to avoid:** Always add `@Import(SecurityConfig.class)` to test classes that test protected endpoints:

```java
@WebMvcTest(ProjectController.class)
@Import(SecurityConfig.class)  // ← Don't forget this
class ProjectControllerTest { ... }
```

**Warning signs:**
- POST/PUT/DELETE endpoints return 2xx even without JWT
- GET endpoints also return 401 (if you accidentally protect them too)

### Pitfall 4: Mixing Authenticated and Anonymous Requests in Same Test

**What goes wrong:** Test expects GET /projects to return 200 with anonymous access, but fails or returns 401.

**Why it happens:** If SecurityFilterChain requires `.anyRequest().authenticated()` without explicit `.permitAll()` for GET, all requests need JWT.

**How to avoid:** Be explicit in SecurityFilterChain:
```java
.requestMatchers(HttpMethod.GET, "/projects/**").permitAll()  // ← explicit allow
.requestMatchers(HttpMethod.POST, "/projects/**").authenticated()
```

**Warning signs:**
- Test `listProjects_noToken_returns200()` fails with 401
- Need to add `.with(jwt())` to GET requests

### Pitfall 5: 401 vs 403 Confusion

**What goes wrong:** Endpoint returns 403 Forbidden when it should return 401 Unauthorized (or vice versa).

**Why it happens:**
- 401 = No credential or invalid credential (missing JWT, expired JWT, invalid signature)
- 403 = Credential is valid but lacks permission (valid JWT but wrong scope/role)

By default, Spring Security routes unauthenticated (no JWT) requests to AccessDeniedHandler → 403 Forbidden.

**How to avoid:** Configure a custom `AuthenticationEntryPoint` if you want 401 for missing JWTs (optional; 403 is also correct).

**Warning signs:**
- Missing JWT returns 403 instead of 401 (incorrect for REST APIs)
- HTTP clients expect 401 to prompt re-authentication

Source: [CITED: Spring Security 401 vs 403](https://medium.com/@samrat.alam/spring-security-authentication-vs-authorization-failure-401-vs-403-basicauthenticationfilter-773ef4c32031)

### Pitfall 6: Cognito User Pool ID vs App Client ID Confusion

**What goes wrong:** Configuration uses App Client ID instead of User Pool ID in issuer-uri, causing JWKS discovery to fail.

**Why it happens:** Cognito has two IDs:
- **User Pool ID** (format: `eu-west-1_sDg68GJKt`) — use this in issuer-uri
- **App Client ID** (format: `abc123def456ghi789...`) — use this for OAuth2 client registration (Phase 4, not Phase 8)

**How to avoid:** Verify your User Pool ID in AWS Console > Cognito > User Pools > (your pool) > Pool settings. Format is `<region>_<randomString>`.

**Warning signs:**
- JWKS discovery error with 404 Not Found
- Issuer-uri logs show the App Client ID instead of User Pool ID

## Code Examples

### Complete SecurityConfig Implementation

```java
package com.kra.api.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Spring Security configuration for OAuth2 Resource Server with AWS Cognito.
 * 
 * - GET /projects: public (no JWT required)
 * - POST /projects, PUT /projects/{id}, DELETE /projects/{id}: require valid Cognito JWT
 * 
 * Configuration:
 *   spring.security.oauth2.resourceserver.jwt.issuer-uri=https://cognito-idp.<region>.amazonaws.com/<poolId>
 * 
 * Testing:
 *   Use @Import(SecurityConfig.class) in @WebMvcTest classes
 *   Use .with(jwt()) MockMvc post-processor for authenticated requests
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((authorize) -> authorize
                // GET requests are public
                .requestMatchers(HttpMethod.GET, "/projects/**").permitAll()
                // POST, PUT, DELETE require authentication
                .requestMatchers(HttpMethod.POST, "/projects/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/projects/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/projects/**").authenticated()
                // Anything else requires authentication (safe default)
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer((oauth2) -> oauth2
                .jwt(withDefaults())  // Enable JWT Bearer Token validation
            );
        
        return http.build();
    }
}
```

Source: [CITED: Spring Security 6.5 OAuth2 Resource Server](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html)

### Testing Protected Endpoints with JWT

```java
package com.kra.api.infrastructure.web;

import com.kra.api.application.ProjectNotFoundException;
import com.kra.api.application.ProjectService;
import com.kra.api.domain.model.Project;
import com.kra.api.domain.model.ProjectId;
import com.kra.api.infrastructure.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
@Import(SecurityConfig.class)  // ← REQUIRED: load SecurityFilterChain bean
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectService projectService;

    // --- POST /projects: Authentication Required ---

    @Test
    void createProject_noToken_returns401() throws Exception {
        mockMvc.perform(post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"My Project\",\"description\":\"desc\",\"url\":\"https://url.com\",\"content\":\"content\"}"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void createProject_withValidJwt_returns201() throws Exception {
        Project fakeProject = new Project(ProjectId.of("abc-123"),
                "My Project", "desc", "https://url.com", "content");
        when(projectService.createProject(any(), any(), any(), any())).thenReturn(fakeProject);

        mockMvc.perform(post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"My Project\",\"description\":\"desc\",\"url\":\"https://url.com\",\"content\":\"content\"}")
                .with(jwt()
                    .jwt(jwt -> jwt
                        .claim("sub", "cognito-user-id-12345")
                        .claim("email", "[email protected]")
                    )
                )
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("abc-123"));
    }

    // --- GET /projects: Public Access (No JWT Required) ---

    @Test
    void listProjects_noToken_returns200() throws Exception {
        when(projectService.getAllProjects(50)).thenReturn(List.of());

        mockMvc.perform(get("/projects"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    void listProjects_withJwt_still200() throws Exception {
        when(projectService.getAllProjects(50)).thenReturn(List.of());

        mockMvc.perform(get("/projects")
                .with(jwt()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    // --- PUT /projects/{id}: Authentication Required ---

    @Test
    void updateProject_noToken_returns401() throws Exception {
        mockMvc.perform(put("/projects/abc-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Updated\"}"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void updateProject_withJwt_returns200() throws Exception {
        Project updated = new Project(ProjectId.of("abc-123"), "Updated", "new desc", null, null);
        when(projectService.updateProject(eq("abc-123"), any(), any(), any(), any())).thenReturn(updated);

        mockMvc.perform(put("/projects/abc-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Updated\",\"description\":\"new desc\"}")
                .with(jwt()
                    .jwt(jwt -> jwt.claim("sub", "cognito-user-id"))
                )
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Updated"));
    }

    // --- DELETE /projects/{id}: Authentication Required ---

    @Test
    void deleteProject_noToken_returns401() throws Exception {
        mockMvc.perform(delete("/projects/abc-123"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteProject_withJwt_returns204() throws Exception {
        mockMvc.perform(delete("/projects/abc-123")
                .with(jwt()))
            .andExpect(status().isNoContent());
    }
}
```

Source: [CITED: Spring Security Test MockMvc OAuth2](https://docs.spring.io/spring-security/reference/servlet/test/mockmvc/oauth2.html)

### Required application.properties Entry

```properties
# OAuth2 Resource Server - JWT validation with AWS Cognito
# Format: https://cognito-idp.<AWS_REGION>.amazonaws.com/<USER_POOL_ID>
# Example: https://cognito-idp.eu-west-1.amazonaws.com/eu-west-1_sDg68GJKt
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://cognito-idp.eu-west-1.amazonaws.com/eu-west-1_sDg68GJKt

# [Optional] Validate the 'aud' (audience) claim if your tokens include it
# Uncomment if your app has a specific resource ID
# spring.security.oauth2.resourceserver.jwt.audiences=https://your-resource-id
```

Source: [CITED: Spring Security JWT Resource Server Configuration](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html)

## State of the Art

| Old Approach | Current Approach | When Changed | Impact |
|--------------|------------------|--------------|--------|
| Manual JWT parsing with jose4j / Nimbus | Spring Security JwtDecoder (auto-configured) | Spring Security 5.0+ | Framework handles JWKS caching, key rotation, validation — no custom code needed |
| Global @PreAuthorize annotations on methods | SecurityFilterChain.authorizeHttpRequests() | Spring Security 6.0+ | Centralized, declarative, testable; avoids scattered authorization logic |
| Configuration via XML | Java @Configuration beans | Spring Boot 2.0+ | Type-safe, IDE support, easier testing |
| Manual OIDC discovery | Automatic /.well-known/openid-configuration | Spring Security 5.0+ | issuer-uri alone bootstraps everything; no manual endpoint configuration |

**Deprecated/outdated:**
- **WebSecurityConfigurerAdapter:** Deprecated since Spring Security 5.7, removed in 6.0. Use SecurityFilterChain bean instead (which is what SecurityConfig implements).
- **@EnableWebSecurity alone without SecurityFilterChain bean:** No longer recommended. Always create explicit SecurityFilterChain bean.

## Assumptions Log

| # | Claim | Section | Risk if Wrong |
|---|-------|---------|---------------|
| A1 | Cognito User Pool ID exists from Phase 4 | Phase Requirements | If not provisioned, no issuer-uri to configure; Phase 8 cannot complete |
| A2 | Cognito User Pool supports OIDC discovery endpoint (/.well-known/openid-configuration) | Standard Stack | AWS Cognito does support this, but if using non-OIDC provider, need to specify jwk-set-uri explicitly |
| A3 | Spring Boot 3.5.0 is final version for this project | Standard Stack | If upgrading to Spring Boot 4.x in future, Spring Security version may change (7.x), requiring dependency review |
| A4 | All endpoints except GET require authentication (not role-based) | Architecture Patterns | Phase 8 spec says "endpoints de escritura protegidos", implies any authenticated user can write; if role-based (ADMIN-only writes), SecurityConfig needs changes |

**User validation needed:** Confirm that any authenticated Cognito user can POST/PUT/DELETE. If only specific roles/groups can write, that's a Phase 9+ concern (authorization/RBAC).

## Open Questions

1. **Should GET return user identity in response?**
   - What we know: Phase 8 success criteria don't require returning user info; endpoints are public for reading.
   - What's unclear: Does GET /projects need to expose created_by or owner_id? Does listing need to filter by user?
   - Recommendation: Assume no user filtering on GET for now. If future phases require "my projects" vs "all projects", add query param and authorization check then.

2. **How to handle token refresh in client?**
   - What we know: POST returns 401 if JWT is missing/invalid.
   - What's unclear: Should client implement refresh token rotation (Cognito provides refresh_token alongside access_token)?
   - Recommendation: Out of scope for Phase 8; client-side concern. Document that 401 means token expired, client should refresh via Cognito.

3. **Network connectivity to Cognito JWKS on startup?**
   - What we know: Spring Security loads JWKS lazily (on first request).
   - What's unclear: Should we pre-load JWKS on application startup to catch Cognito connectivity issues early?
   - Recommendation: Not required for Phase 8; lazy loading is acceptable. If startup health checks are needed, add in Phase 9+.

## Environment Availability

| Dependency | Required By | Available | Version | Fallback |
|------------|------------|-----------|---------|----------|
| Java | Compilation, runtime | ✓ | 21 | — |
| Maven | Build, dependency management | ✓ | 3.x | — |
| AWS Cognito User Pool | JWT issuer, JWKS endpoint | ✓ (from Phase 4) | — | None — required for real auth |
| Spring Boot 3.5.0 | Framework, OAuth2 starters | ✓ | 3.5.0 | — |

**Missing dependencies with no fallback:**
- AWS Cognito User Pool (required from Phase 4)

**Missing dependencies with fallback:**
- None — all required libraries are in Spring Boot 3.5.0

## Validation Architecture

### Test Framework

| Property | Value |
|----------|-------|
| Framework | JUnit 5 (Jupiter) + Spring Boot Test + Mockito + Spring Security Test |
| Config file | None — uses @SpringBootTest, @WebMvcTest annotations |
| Quick run command | `mvn test -Dtest="ProjectControllerTest" -Dgroups="!integration"` |
| Full suite command | `mvn test` |

### Phase Requirements → Test Map

| Req ID | Behavior | Test Type | Automated Command | File Exists? |
|--------|----------|-----------|-------------------|-------------|
| AUTH-01 | POST /projects without token returns 401 | unit | `mvn test -Dtest="ProjectControllerTest#createProject_noToken_returns401"` | ❌ Wave 0 |
| AUTH-02 | POST /projects with valid Cognito JWT returns 201 | unit | `mvn test -Dtest="ProjectControllerTest#createProject_withValidJwt_returns201"` | ❌ Wave 0 |
| AUTH-03 | GET /projects without token returns 200 (public) | unit | `mvn test -Dtest="ProjectControllerTest#listProjects_noToken_returns200"` | ❌ Wave 0 |
| AUTH-04 | PUT /projects/{id} without token returns 401 | unit | `mvn test -Dtest="ProjectControllerTest#updateProject_noToken_returns401"` | ❌ Wave 0 |
| AUTH-05 | PUT /projects/{id} with valid JWT returns 200 | unit | `mvn test -Dtest="ProjectControllerTest#updateProject_withJwt_returns200"` | ❌ Wave 0 |
| AUTH-06 | DELETE /projects/{id} without token returns 401 | unit | `mvn test -Dtest="ProjectControllerTest#deleteProject_noToken_returns401"` | ❌ Wave 0 |
| AUTH-07 | DELETE /projects/{id} with valid JWT returns 204 | unit | `mvn test -Dtest="ProjectControllerTest#deleteProject_withJwt_returns204"` | ❌ Wave 0 |

### Sampling Rate

- **Per task commit:** `mvn test -Dtest="ProjectControllerTest"`
- **Per wave merge:** `mvn test` (full suite including DynamoDB integration tests)
- **Phase gate:** Full suite green + manual smoke test with Cognito JWT before `/gsd-verify-work`

### Wave 0 Gaps

- [ ] `src/main/java/com/kra/api/infrastructure/config/SecurityConfig.java` — new file, SecurityFilterChain bean
- [ ] `src/main/resources/application.properties` — add `spring.security.oauth2.resourceserver.jwt.issuer-uri` property
- [ ] `ProjectControllerTest.java` — update existing tests to add `@Import(SecurityConfig.class)` and new authentication tests (AUTH-01 through AUTH-07)
- [ ] `src/test/java/com/kra/api/infrastructure/web/SecurityConfigTest.java` — optional integration test verifying SecurityFilterChain rules (can defer to Phase 9 if time-boxed)

*(Existing test infrastructure covers all phase requirements once gaps are filled)*

## Security Domain

### Applicable ASVS Categories

| ASVS Category | Applies | Standard Control |
|---------------|---------|-----------------|
| V2 Authentication | yes | AWS Cognito User Pool + Spring Security OAuth2 JwtDecoder |
| V3 Session Management | no | Stateless JWT; no server-side sessions |
| V4 Access Control | yes | SecurityFilterChain.authorizeHttpRequests() — method-based (GET vs. POST) |
| V5 Input Validation | yes | Already in place (Phase 7: @NotBlank, @Valid) |
| V6 Cryptography | yes | Cognito signs JWTs with RSA private key; Spring verifies with public keys from JWKS endpoint |
| V7 Audit Logging | no | Out of scope for Phase 8 (logging handled by existing GlobalExceptionHandler) |

### Known Threat Patterns for OAuth2 JWT

| Pattern | STRIDE | Standard Mitigation |
|---------|--------|---------------------|
| JWT signature bypass (HMAC algorithm confusion) | Tampering | JwtDecoder specifies RS256/RS512 (asymmetric); never uses HS256 with leaked secret. [VERIFIED: Spring Security enforces this] |
| Token injection (accepting tokens from wrong issuer) | Spoofing | issuer-uri validation in SecurityConfig ensures `iss` claim matches Cognito issuer only. [VERIFIED: Spring Security docs] |
| Expired token acceptance | Tampering | JwtDecoder validates `exp` claim automatically. [CITED: Spring Security JWT validation] |
| JWKS endpoint downtime (network latency) | Denial of Service | RemoteJWKSet caches keys with 5-min TTL; first request to down endpoint will timeout (30s default). Acceptable for Phase 8; add circuit breaker in Phase 9+ if needed. |
| Missing Bearer token (404 instead of 401) | Information Disclosure | Spring Security returns 401 for missing Authorization header (correct). |

Source: [CITED: Spring Security 6.5 Resource Server](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html)

## Sources

### Primary (HIGH confidence)

- **Spring Security 6.5 Official Docs** - OAuth2 Resource Server with JWT ([https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html))
  - JWT bearer token validation, issuer-uri configuration, JWKS discovery
  
- **Spring Security 6.5 Official Docs** - HTTP Authorization ([https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html](https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html))
  - SecurityFilterChain, authorizeHttpRequests(), method-based authorization rules
  
- **Spring Security 6.5 Official Docs** - Testing OAuth2 with MockMvc ([https://docs.spring.io/spring-security/reference/servlet/test/mockmvc/oauth2.html](https://docs.spring.io/spring-security/reference/servlet/test/mockmvc/oauth2.html))
  - `jwt()` post-processor, claim customization, test patterns
  
- **Spring Boot 3.5 Dependencies** - Includes Spring Security 6.5 + OAuth2 starters as transitive dependencies ([https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.5-Release-Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.5-Release-Notes))

### Secondary (MEDIUM confidence)

- **AWS Cognito Documentation** - JWT Verification ([https://docs.aws.amazon.com/cognito/latest/developerguide/amazon-cognito-user-pools-using-tokens-verifying-a-jwt.html](https://docs.aws.amazon.com/cognito/latest/developerguide/amazon-cognito-user-pools-using-tokens-verifying-a-jwt.html))
  - Cognito issuer format, JWKS endpoint URL construction

- **Cognito OAuth2 & JWKS with Spring Boot** - DEV Community ([https://dev.to/visepol/oauth2-jwt-and-jwks-using-amazon-cognito-as-idp-1jod](https://dev.to/visepol/oauth2-jwt-and-jwks-using-amazon-cognito-as-idp-1jod))
  - issuer-uri format verification, Cognito-specific integration patterns

- **Spring Security 6.5 Resource Server JWT** - GitHub source ([https://github.com/spring-projects/spring-security/blob/6.5.0/docs/modules/ROOT/pages/servlet/oauth2/resource-server/jwt.adoc](https://github.com/spring-projects/spring-security/blob/6.5.0/docs/modules/ROOT/pages/servlet/oauth2/resource-server/jwt.adoc))
  - Authoritative reference for JWT configuration

### Tertiary (LOW confidence)

- **Baeldung: Authenticating with Amazon Cognito Using Spring Security** ([https://www.baeldung.com/spring-security-oauth-cognito](https://www.baeldung.com/spring-security-oauth-cognito))
  - Cognito integration patterns (not fully fetched; used as reference for validation)

- **kevcodez: Secure Spring Boot with OAuth2 and Cognito** ([https://kevcodez.de/posts/2020-03-26-secure-spring-boot-app-with-oauth2-aws-cognito/](https://kevcodez.de/posts/2020-03-26-secure-spring-boot-app-with-oauth2-aws-cognito/))
  - Cognito issuer-uri and JWKS endpoint configuration examples

- **WWT: Automated Testing with Spring Boot as OAuth2 Resource Server** ([https://www.wwt.com/article/automated-testing-with-spring-boot-as-an-oauth2-resource-server](https://www.wwt.com/article/automated-testing-with-spring-boot-as-an-oauth2-resource-server))
  - WireMock and testing patterns for resource servers

- **Spring Security Issue #10471** - JWKS pre-loading on startup ([https://github.com/spring-projects/spring-security/issues/10471](https://github.com/spring-projects/spring-security/issues/10471))
  - Known behavior: lazy JWKS loading (not on startup), discussion of workarounds

## Metadata

**Confidence breakdown:**

| Area | Level | Reason |
|------|-------|--------|
| Standard Stack | HIGH | Spring Boot 3.5.0 and Spring Security 6.5 are current LTS versions; all libraries verified via official docs and release notes |
| Architecture (SecurityFilterChain) | HIGH | Official Spring Security patterns; stable API in 6.x |
| Authorization Rules | HIGH | Directly from Spring Security docs with code examples |
| Testing Patterns | HIGH | Spring Security test framework is stable; jwt() post-processor is documented standard |
| Cognito Integration | MEDIUM | Cognito issuer-uri format verified via AWS docs; integration pattern confirmed via multiple sources but not exhaustively tested in this research |
| Common Pitfalls | MEDIUM | Based on GitHub issues and community articles; not all scenarios may apply to this specific project |
| ASVS Mapping | HIGH | Standard OAuth2 JWT patterns map clearly to ASVS authentication/cryptography requirements |

**Research date:** 2026-04-08
**Valid until:** 2026-05-08 (30 days; Spring Security 6.5 is stable)

---

## Next Steps for Planner

1. **Add dependency verification task:** Confirm that spring-boot-starter-oauth2-resource-server and spring-security-oauth2-jose are included in Spring Boot 3.5.0 (they should be, no pom.xml changes needed).

2. **Clarify Cognito User Pool ID:** Extract the actual Cognito User Pool ID from Phase 4 artifacts (Terraform state or AWS Console) — needed to construct issuer-uri.

3. **Plan SecurityConfig creation:** New file in src/main/java/com/kra/api/infrastructure/config/ following the pattern in this research.

4. **Plan test updates:** ProjectControllerTest needs @Import(SecurityConfig.class) and new test methods for AUTH-01 through AUTH-07.

5. **Plan application.properties update:** Add spring.security.oauth2.resourceserver.jwt.issuer-uri property.

6. **Smoke test strategy:** Manual curl commands with real Cognito JWT to verify 401 on POST without token and 201 with token.
