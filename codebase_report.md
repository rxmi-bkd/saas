Code Review: saas (Spring Boot Auth/User Service)

Scorecard: 36 files reviewed · 5 Critical · 6 High · 7 Medium · 5 Low (23 total findings) + test-coverage gap analysis

Critical (fix before any production use)

┌─────┬─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┬──────────────────────────────────────────┐
│  #  │                                                                          Issue                                                                          │                  Where                   │
├─────┼─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┼──────────────────────────────────────────┤
│ C1  │ Password-reset JWT (a full account-takeover credential) is logged in plaintext instead of emailed — there's no email integration at all, so reset       │ PasswordResetService.java:29             │
│     │ currently can't reach real users                                                                                                                        │                                          │
├─────┼─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┼──────────────────────────────────────────┤
│ C2  │ forgotPassword catches UsernameNotFoundException, but the actual thrown type is UserNotFoundException (unrelated class) — catch never fires, so unknown │ PasswordResetService.java:26-32          │
│     │  emails return 404 vs known emails return 204. User-enumeration oracle.                                                                                 │                                          │
├─────┼─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┼──────────────────────────────────────────┤
│ C3  │ Liquibase seeds admin@example.com/user@example.com with the same hardcoded bcrypt hash, unconditionally run in every environment including prod         │ 002-insert-default-users.yaml            │
├─────┼─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┼──────────────────────────────────────────┤
│ C4  │ email column has no DB-level unique constraint — only an app-side check-then-insert, so concurrent registrations can create duplicate-email accounts    │ 001-add-user-table.yaml,                 │
│     │                                                                                                                                                         │ UserService.java:28-33                   │
├─────┼─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┼──────────────────────────────────────────┤
│ C5  │ JWT secrets are literal placeholder strings ("your-secret-key-change-in-production") committed to application.properties, with no startup guard         │ application.properties:18,22             │
│     │ rejecting them                                                                                                                                          │                                          │
└─────┴─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┴──────────────────────────────────────────┘

High

- H1 resetPassword silently no-ops on invalid/expired token but controller still returns 204 — user thinks password changed when it didn't
- H2 No rate limiting on login/forgot-password/register — brute-force and cheap CPU-exhau
- H3 No password length/strength validation (@NotBlank only) — 1-char passwords accepted
- H4 Timing-based user enumeration in login (Optional.filter short-circuits bcrypt check for nonexistent emails)
- H5 Emails never normalized (case) — duplicate-account and login-mismatch bugs
- H6 AccessJwtService.readRole can throw uncaught IllegalArgumentException, propagating filter

Medium (highlights)

- JWT parsed/verified up to 3x per request (M1)
- Heavy duplication between AccessJwtService/PasswordResetJwtService (M2)
- No centralized @ControllerAdvice — exceptions leak raw library messages (M4)
- Undocumented "double-bcrypt" trick for reset-token invalidation (M5)
- Dead code, meaningless ROLE_ADMIN (never checked anywhere) (M6, M7)

Low

CORS unconfigured, plaintext local DB creds in properties, unconventional Maven artifact ids worth double-checking, thin ArchUnit rules, minor snake_case naming.

Test coverage gaps

No tests exist for any controller, JwtAuthenticationFilter, SecurityConfiguration, or — notably — PasswordResetService/PasswordResetJwtService, the exact classes containing C1, C2, and H1. A single test for "forgot-password with unknown email" would have caught the enumeration bug.

---

Bottom line: solid architecture overall (stateless JWT, BCrypt, MapStruct, Liquibase, ArchUnit), but the password-reset flow has two real security defects (C1, C2), plus unsafe defaults (C3 seeded admin, C5
placeholder secrets) and a real concurrency bug (C4). I'd treat C1–C5 and H1–H2 as block

Want me to start fixing any of these (I'd suggest starting with C2, C1, and C5 as quick, high-impact fixes)?