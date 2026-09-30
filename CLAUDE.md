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