# Gym Booking System

> A production-style REST API for booking gym classes — built to practice and demonstrate real backend engineering skills.
> 
> **Scenario:** a single gym with limited spots per class. Users register, browse classes, and request a booking. A `SUPPORT`/`ADMIN` reviews and approves or rejects the request. The system protects against overbooking even under concurrent requests for the last spot.
<p align="center">
  <a href="https://skillicons.dev">
    <img src="https://skillicons.dev/icons?i=java,spring,redis,rabbitmq,postgres,docker,github,githubactions" />
  </a>
</p>

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Run It Locally](#run-it-locally)
- [Environment Variables](#environment-variables)
- [Roles & Flow](#roles--flow)
- [Notable Design Decisions](#notable-design-decisions)
- [Testing](#testing)
- [API Overview](#api-overview)
---

## Tech stack

- **Java 25**, **Spring Boot**
- **Spring Security** — JWT (access + refresh tokens, with rotation), role-based authorization, OAuth2 (Google login)
- **Spring Data JPA** + **PostgreSQL**
- **Redis** — response caching (`@Cacheable`)
- **RabbitMQ** — delayed auto-rejection of stale pending bookings (TTL + dead-letter exchange)
- **Bucket4j** — rate limiting on auth endpoints
- **Testcontainers** — integration testing against a real Postgres instance
- **JUnit 5 + Mockito** — unit and slice testing
- **springdoc-openapi / Swagger UI** — live, interactive API docs
- **Docker & Docker Compose** — one-command local environment
- **GitHub Actions** — CI (build + full test suite on every push)

---

### Swagger UI
<img width="1473" height="712" alt="image" src="https://github.com/user-attachments/assets/4903bb88-5676-4c7b-8238-8fbe2b078e2c" />

## Run it locally
### Requirements
- Docker & Docker Compose

### Steps

1. Clone the repo.
2. Create a `.env` file in the project root (see [Environment variables](#environment-variables) below).
3. Run:
   ```bash
   docker-compose up --build
   ```
4. The app starts on `http://localhost:8080`. Postgres, Redis, and RabbitMQ start alongside it in the same network.

### Swagger UI

Interactive API docs, with a built-in "Authorize" button for testing JWT-protected endpoints:

```
http://localhost:8080/swagger-ui.html
```

### RabbitMQ management UI

```
http://localhost:15672
```
(default credentials: `guest` / `guest`, unless overridden)

---

## Environment variables

None of the secrets are hardcoded — everything is read from environment variables, injected via `.env` when running through Docker Compose. You can use data from application.properties.example file but it doesn't contain all data!

| Variable | Description |
|---|---|
| `ADMIN_EMAIL` / `ADMIN_PASSWORD` | Credentials for the default admin account, seeded automatically on first startup |
| `JWT_SECRET` | Signing key for access tokens |
| `JWT_EXPIRATION` | Access token lifetime (ms) |
| `REFRESH_EXPIRATION` | Refresh token lifetime (days) |
| `RABBITMQ_TTL_TIME` | How long a booking stays `PENDING` before being auto-rejected (ms) |
| `GOOGLE_CLIENT_ID` / `GOOGLE_CLIENT_SECRET` | OAuth2 credentials for "Login with Google" |
| `APP_FRONTEND_OAUTH2_REDIRECT_URI` | Where the backend redirects after a successful Google login |

A default `ADMIN` user is seeded automatically on startup (`ADMIN_EMAIL` / `ADMIN_PASSWORD`) — this is the only way to get an initial admin, since regular registration always creates a `USER`.

---

## Roles & flow

- **USER** — registers, browses classes, requests bookings, cancels their own pending bookings.
- **SUPPORT / ADMIN** — approves or rejects pending booking requests.
- **ADMIN** — creates gym classes, promotes users to `SUPPORT`/`ADMIN`.

## Notable design decisions

- **JWT is stateless; refresh tokens are not.** Access tokens are verified by signature alone. Refresh tokens are stored in the database (one active token per user, rotated on every use) specifically so they can be revoked — on logout, or if compromised — which a pure stateless token cannot support.
- **Pessimistic locking only where it matters.** `approveBooking` takes a `PESSIMISTIC_WRITE` lock on the gym class row, because that's the one place with real contention over a limited resource. `rejectBooking` does not — it never touches occupancy, so locking there would just add overhead with no benefit.
- **OAuth2 login never puts the JWT in a URL.** The success handler issues a short-lived, single-use code (stored in Redis with a TTL, deleted on first use) instead of the token itself. The frontend exchanges that code for the real JWT via a separate POST request — the token itself never appears in browser history, server logs, or a `Referer` header.
- **Stale bookings are cleared automatically.** A booking that sits in `PENDING` for too long (nobody approved or rejected it) is auto-rejected via a RabbitMQ delay queue (TTL + dead-letter exchange to a real consumer queue) — chosen over a simple `@Scheduled` poll specifically to demonstrate the message-queue pattern and its resilience to a server restart mid-wait.
- **`@Async` for email notifications.** Booking status-change emails are sent on a background thread so the HTTP response to `approve`/`reject` doesn't wait on a slow "mail server."

---

## Testing

Three layers, each with a different job:

- **Unit tests (JUnit + Mockito)** — business logic in isolation, no Spring context, no database.
- **Slice tests (`@WebMvcTest`)** — the HTTP layer: status codes, validation, role-based access, error responses — real `SecurityConfig`, service layer mocked.
- **Integration test (Testcontainers)** — a real, throwaway Postgres instance, two threads racing to approve the last spot on a class, asserting the pessimistic lock actually holds under real concurrency.

Run everything:
```bash
./gradlew test
```

CI runs the full suite (including the Testcontainers-based integration test, with Postgres/Redis/RabbitMQ as GitHub Actions service containers) on every push to `main`.

---

## API overview

Full, interactive documentation is in Swagger UI once the app is running. Broad strokes:

| Area        | Endpoints                                                                                                  |
| ----------- | ---------------------------------------------------------------------------------------------------------- |
| Auth        | register, login, refresh, logout, Google OAuth2 login                                                      |
| Gym classes | list (public), create (`ADMIN`/`SUPPORT`)                                                                  |
| Bookings    | create, cancel own pending, list own, list pending (`SUPPORT`/`ADMIN`), approve/reject (`SUPPORT`/`ADMIN`) |
| Users       | assign role (`ADMIN` only)                                                                                 |
