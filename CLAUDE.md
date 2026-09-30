This `CLAUDE.md` file provides guidelines for Claude Code (claude.ai/code) when interacting with the code in this repository.

## Stack

The project utilizes Spring Boot 4.1, Java 21, Maven Wrapper, PostgreSQL 17, Liquibase, Spring Security with JWT (jjwt), MapStruct, Lombok, ArchUnit, and Spotless.

## Commands

The following commands are standard for this project:

```
docker compose up -d                                                                   # Starts PostgreSQL (via compose.yml); also auto-starts via spring.docker.compose.file
./mvnw spring-boot:run                                                                 # Runs the application (requires environment variables)
./mvnw test                                                                            # Runs all tests
./mvnw test -Dtest=ArchitectureTests#all_services_should_have_transactional_annotation # Runs a single designated test
./mvnw spotless:apply                                                                  # Formats code using google-java-format (use spotless:check to verify)
```

## Project Structure

Code is organized by feature (package-by-feature) under `src/main/java/org/bkd/saas/`. Do not group code by technical layer at the top level.

* Each feature is one top-level package named in snake_case and must follow this layout, creating only the sub-packages it needs:

```
src/main/java/org/bkd/saas/<feature>/
├── rest/             # Controller, Routes
│   └── request/      # Request records
├── service/          # Services
├── db/               # Entities, Repositories
├── dto/              # Dtos, Enums
├── mapper/           # MapStruct Mappers
└── exception/        # Exceptions
```

* Sub-packages must match the Naming table under Conventions; never put a class in a package that does not match its suffix.
* Features must not reach into another feature's `db` package: use that feature's service and DTOs instead.
* Cross-cutting code shared by several features goes in `shared/`.
* Feature-specific configuration classes go in a `configuration/` sub-package of the feature.
* Database schema changes go in Liquibase changelogs under `src/main/resources/db/changelog/`. Never edit an already-applied changeset.
* Tests mirror the main package structure under `src/test/java/org/bkd/saas/`.

## Conventions

* **Dependency injection**: Use `@RequiredArgsConstructor` with `private final` fields, and strictly avoid using `@Autowired`.


* **Routes**: Each feature must include a `rest/Routes` constants class (with a private constructor) built upon `shared/Routes` (e.g., `/api/v1`, `/api/v1/public`). Define new endpoint paths within these constant classes rather than inline within the controllers.


* **Exceptions**: Every exception class must be annotated with `@ResponseStatus`. The HTTP status mapping must be defined directly on the exception class itself, rather than handled in a global controller advice.


* **Services**: Every class annotated with `@Service` must also be annotated with `@Transactional`.


* **Naming**: A class's suffix must indicate its architectural layer, and its package must match this structure accordingly:



| Package | Suffix |
| --- | --- |
| `rest` | `Controller` (along with the `Routes` constants class)
| `rest/request` | `Request`<br> |
| `service` | `Service`<br> |
| `db` | `Entity`, `Repository`<br> |
| `dto` | `Dto`, `Enum`<br> |
| `mapper` | `Mapper`<br> |
| `exception` | `Exception`<br> |

* **Interfaces**: Interfaces should be named based on their specific roles without appending a general suffix. The only exceptions are `Mapper` and `Repository` interfaces, which must retain their respective package suffixes.


* **Requests**: Request bodies must reside in the `rest/request` package of their respective features. They should be implemented as Java records and utilize Jakarta validation annotations (such as `@NotBlank` or `@Email`). They must be bound in controllers using `@Valid @RequestBody`. These records serve strictly as input for the REST layer; they should never be passed down to services or persisted in the database.


* **Entities**: Entities must never be exposed directly to the REST layer. Only services are permitted to manipulate entities, and services must always return DTOs that have been mapped using MapStruct.


* **Controllers**: Every method within a controller must return a `ResponseEntity`.