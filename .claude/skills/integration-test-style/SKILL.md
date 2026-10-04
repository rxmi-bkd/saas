---
name: integration-test-style
description: House style for writing integration tests in this Spring Boot SaaS repo (extends AbstractIntegrationTests, real HTTP through RestClient, Testcontainers Postgres). Use whenever the user asks to add, write, extend or refactor a test under src/test (e.g. CreateUserTests, a new <Feature>Tests class, a new endpoint test, a test for a service method), even if they only say "add a test for X" without mentioning integration tests.
---

# Integration test style

Tests here exercise the real app over HTTP against a Testcontainers PostgreSQL. They never mock the service or repository layer: the point is to verify the endpoint end to end, so a test goes through the controller, service, and DB exactly like a client would.

## Layout

- One test class per use case/endpoint, named `<Action><Feature>Tests` (e.g. `CreateUserTests`), in `src/test/java/org/bkd/saas/<feature>/`, mirroring the main package.
- The class is `public`, extends `AbstractIntegrationTests`, and uses JUnit 5 `@Test` (package-private methods).
- HTTP helpers for a feature live in `src/test/java/org/bkd/saas/<feature>/<Feature>TestUtils.java` (same package as the tests) (e.g. `UserTestUtils`): a private-constructor class (`@NoArgsConstructor(access = AccessLevel.PRIVATE)`) with static methods. Add new calls there rather than inlining RestClient code in tests.
- Shared test-only types (e.g. `ErrorDto`) live in `src/test/java/org/bkd/saas/shared/dto/`, mirroring the main `shared` package.

## Helper conventions

For each endpoint, expose a pair in the TestUtils class that wraps one private generic method:

- `<action>Ok(request, host)` returns `ResponseEntity<SuccessDto>`
- `<action>Ko(request, host)` returns `ResponseEntity<ErrorDto>`

The private generic method uses `RestClient` and `.onStatus(HttpStatusCode::isError, (request, response) -> {})` so error statuses don't throw and can be asserted on. Use the route constants from the main code (`org.bkd.saas.<feature>.rest.Routes`), not string literals.

In the test class, add thin private wrappers that pass `server()`:

```java
private ResponseEntity<UserDto> createUserOk(CreateUserRequest request) {
  return UserTestUtils.createUserOk(request, server());
}
```

## Test structure

- Reset state in `@BeforeEach` (e.g. `userRepository.deleteAll()`), with the repository `@Autowired` into the test, so tests are independent.
- Constants for shared inputs at the top (`EMAIL`, `PASSWORD`).
- Method names: `<action>_<condition>_<expectedResult>`, e.g. `createUser_withAlreadyUsedEmail_returnsConflict`. Drop the condition for the happy path (`createUser_returnsCreatedUser`).
- Body is split by `// arrange`, `// act`, `// assert` comments, in that order, always all three.
- Assert with AssertJ (`assertThat`). On success, check the status code and each meaningful field. On failure, check `status()`, `message()` and `error()` of the `ErrorDto`.

## Rules that matter

- **Never construct a DTO/request inline as an argument.** Build it into a local variable in `// arrange` first, then pass the variable. `createUserOk(new CreateUserRequest(...))` is wrong; `CreateUserRequest request = new CreateUserRequest(...); createUserOk(request);` is right. This keeps arrange separate from act.
- Setup calls that are really part of the scenario (creating the first user before testing a conflict) go in `// act`, not `// arrange`; arrange only builds data.
- Don't mock; call the API. Use the repository directly only for cleanup or for checking persisted state.
- Keep one behavior per test; if two assertions need different arranged data, make two tests.

## Example

```java
@Test
void createUser_withDifferentCaseEmail_returnsConflict() {
  // arrange
  CreateUserRequest createUserRequest = new CreateUserRequest(EMAIL, PASSWORD);
  CreateUserRequest upperCaseRequest = new CreateUserRequest(EMAIL.toUpperCase(), PASSWORD);

  // act
  createUserOk(createUserRequest);
  ResponseEntity<ErrorDto> response = createUserKo(upperCaseRequest);

  // assert
  assertThat(response.getBody().status()).isEqualTo(409);
  assertThat(response.getBody().message()).isEqualTo("Email already used");
  assertThat(response.getBody().error()).isEqualTo("Conflict");
}
```

After writing tests, run them with `./mvnw test -Dtest=<ClassName>` and format with `./mvnw spotless:apply`.
