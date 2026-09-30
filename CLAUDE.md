# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Stack

Spring Boot 4.1 (webmvc), Java 21, Maven wrapper, PostgreSQL 17, Liquibase, Spring Security with JWT (jjwt), MapStruct + Lombok, ArchUnit. Spotless version is declared in `pom.xml` properties.

## Commands

```
docker compose up -d              # Postgres (compose.yml); also auto-started via spring.docker.compose.file
./mvnw spring-boot:run            # run the app (needs env vars, see below)
./mvnw test                       # all tests
./mvnw test -Dtest=ArchitectureTests#all_services_should_have_transactional_annotation   # single test
```

Configuration comes from environment variables (`.env.example` lists them): `DB_*`, `JWT_AUTHENTICATION_TOKEN_*`, `JWT_RESET_PASSWORD_TOKEN_*`, `GOOGLE_CLIENT_ID`, `GOOGLE_SECRET`. `application-local.properties` sets the Liquibase context `local`. The Google redirect URI is hardcoded to `localhost:8080` in `application.properties`. `spring.jpa.hibernate.ddl-auto=validate`, so schema changes must go through a Liquibase changelog in `src/main/resources/db/changelog/v1/` (registered in `db.changelog-master.yaml`).

## Architecture

Package-by-feature under `org.bkd.saas`: `authentication`, `password_reset`, `social_authentication`, `user`, plus `security` (filter chain and `JwtAuthenticationFilter`) and `shared`. Within a feature, the layout is `rest` (controllers, `request/`, `Routes`), `service`, `db` (entities, repositories), `dto`, `mapper` (MapStruct), `exception`.

Conventions visible across the code:
- **Routes**: each feature has a `rest/Routes` constants class (private constructor) built on `shared/Routes` (`/api/v1`, `/api/v1/public`). Put new endpoint paths there, not inline in controllers.
- **Exceptions**: every exception class must carry `@ResponseStatus`. The HTTP mapping lives on the exception, not in a controller advice.
- **Services**: every `@Service` must be `@Transactional`.
- Both rules are enforced by `src/test/java/org/bkd/saas/ArchitectureTests.java`, so run it after adding services or exceptions.
- Entities are not exposed directly. Controllers and services use DTOs converted by MapStruct mappers.

### Two separate JWT token types
`AuthenticationTokenService` (login/session) and `PasswordResetTokenService` use different secrets and expirations (`app.jwt.authentication-token.*` vs `app.jwt.reset-password-token.*`).

### Social authentication (OAuth2 code flow)
`SocialAuthenticationService` orchestrates: `authorize(platform)` builds the provider URL and persists a `State` (`StateService`/`StateEntity`) for CSRF protection. `handleCallback` validates the state and its expiry, exchanges the code for tokens, fetches the profile, reads or creates the user by email, then deletes the state.

Platform support is a strategy pattern. `UrlBuilder`, `TokenExchanger` and `ProfileFetcher` all extend `PlatformScoped` (`supports(PlatformEnum)`). `SocialAuthenticationService.resolve` picks the single matching bean and throws `UnsupportedPlatformException` if none match, or `IllegalStateException` if more than one does. To add a provider:
- Add a `PlatformEnum` value.
- Add a properties block under `app.social-authentication.<platform>.*`, bound through `PlatformConfiguration(s)`.
- Implement the three interfaces, as `GoogleService` does for Google.
