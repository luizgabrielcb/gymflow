# 💪 GymFlow API

![CI](https://github.com/luizgabrielcb/gymflow/actions/workflows/workflow.yml/badge.svg)

RESTful API for managing workouts, training sessions, and physical assessments. Built with Spring Boot 3.5, Spring Security with JWT authentication, and a two-layer test strategy with full controller coverage.

🔗 **[Swagger UI](https://gymflow-api-t7h9.onrender.com/swagger-ui/index.html)** | 📦 **[Repository](https://github.com/luizgabrielcb/gymflow)**

---

## 📖 About

A portfolio project built to demonstrate production-ready patterns in a Spring Boot back-end: JWT with rotating refresh tokens, role-based access control, a two-layer testing strategy with real PostgreSQL via Testcontainers, and CI/CD with automatic deploy. Built as part of my journey into back-end development.

## ✨ Highlights

- **222 tests** running in ~10s — integration tests at the controller layer and unit tests at the service layer
- **100% controller coverage** with integration tests using Testcontainers (real PostgreSQL) and REST Assured
- **Service layer** tested with JUnit 5 + Mockito (99% line coverage)
- **88% overall line coverage**, 96% class coverage
- **Rotating refresh token** strategy with revocation tracking
- **Custom exception handling** with proper HTTP status mapping (400, 403, 404, 409)
- **Multi-stage Dockerfile** and automated deploy pipeline

---

## 🛠️ Stack

| Technology            | Version |
|-----------------------|---------|
| Java                  | 21      |
| Spring Boot           | 3.5     |
| Spring Security + JWT | -       |
| PostgreSQL            | 17      |
| Flyway                | -       |
| MapStruct             | -       |
| SpringDoc/Swagger UI  | -       |
| Bean Validation       | -       |
| Docker                | -       |

### Testing

| Technology     | Usage                                      |
|----------------|--------------------------------------------|
| JUnit 5        | Unit and integration test framework        |
| Mockito        | Service-layer mocking                      |
| Testcontainers | PostgreSQL container for integration tests |
| REST Assured   | HTTP integration tests                     |

---

## 🏗️ Project Structure

```
com.luizgabriel.gymflow
 ├── config/      — OpenAPI/Swagger configuration
 ├── controller/  — REST endpoints
 ├── domain/      — JPA entities
 ├── dto/         — Request and response DTOs
 ├── exception/   — Custom exceptions and global handler
 ├── mapper/      — MapStruct mappers (entity ↔ DTO)
 ├── repository/  — Spring Data JPA repositories
 ├── security/    — Spring Security configuration, JWT filter, and token service
 └── service/     — Business logic
```

---

## 📦 Entities

```
User
 ├── Workout (1→N)
 │    └── WorkoutExercise (1→N)
 ├── PhysicalAssessment (1→N)
 └── TrainingSession (1→N)
      └── SessionSet (1→N)
```

---

## 🔐 Authentication

The API uses **JWT** with a **rotating refresh token** strategy:

- `POST /auth/register` — User registration
- `POST /auth/login` — Login, returns `accessToken` (2h) + `refreshToken` (7 days)
- `POST /auth/refresh` — Generates a new token pair and revokes the previous refresh token
- `POST /auth/logout` — Revokes the refresh token

---

## 📋 Business Rules

### Roles

- **ADMIN** — manages exercises and physical assessments for all users
- **USER** — manages their own workouts and training sessions

### Exercises

- Only ADMIN can create, edit, and delete exercises
- Any authenticated user can view the exercise list
- An exercise linked to a workout cannot be deleted

### Workouts

- Users manage only their own workouts
- Workout name must be unique per user
- A workout with associated training sessions cannot be deleted

### Training Sessions

- A user can only have one session with `IN_PROGRESS` status at a time
- Sets (`SessionSet`) can only be added, edited, or deleted in sessions with `IN_PROGRESS` status
- Sessions with `COMPLETED` or `CANCELLED` status are immutable — they represent training history
- Users can only access their own sessions

### Physical Assessments

- Only ADMIN can create, edit, and delete physical assessments
- Users can only view their own assessments

### Refresh Token

- Login returns an `accessToken` (expires in 2h) and a `refreshToken` (expires in 7 days)
- Each use of `POST /auth/refresh` revokes the old refresh token and generates a new token pair (rotation)
- Logout revokes the refresh token, preventing future refreshes

---

## 🔗 Endpoints

### Auth

| Method | Endpoint         | Access | Description    |
|--------|------------------|--------|----------------|
| POST   | `/auth/register` | Public | Register       |
| POST   | `/auth/login`    | Public | Login          |
| POST   | `/auth/refresh`  | Public | Refresh tokens |
| POST   | `/auth/logout`   | Public | Logout         |

### Users

| Method | Endpoint      | Access | Description                 |
|--------|---------------|--------|-----------------------------|
| GET    | `/users`      | ADMIN  | List all users              |
| GET    | `/users/{id}` | ADMIN  | Get user by ID              |
| GET    | `/users/me`   | USER   | Get authenticated user data |
| PUT    | `/users`      | USER   | Update user data            |
| DELETE | `/users/{id}` | ADMIN  | Delete user                 |
| DELETE | `/users/me`   | USER   | Delete own account          |

### Exercises

| Method | Endpoint          | Access        | Description     |
|--------|-------------------|---------------|-----------------|
| GET    | `/exercises`      | Authenticated | List all        |
| GET    | `/exercises/{id}` | Authenticated | Get by ID       |
| POST   | `/exercises`      | ADMIN         | Create exercise |
| PUT    | `/exercises`      | ADMIN         | Update exercise |
| DELETE | `/exercises/{id}` | ADMIN         | Delete exercise |

### Workouts

| Method | Endpoint         | Access | Description          |
|--------|------------------|--------|----------------------|
| GET    | `/workouts`      | USER   | List user's workouts |
| POST   | `/workouts`      | USER   | Create workout       |
| PUT    | `/workouts`      | USER   | Update workout       |
| DELETE | `/workouts/{id}` | USER   | Delete workout       |

### Physical Assessments

| Method | Endpoint                          | Access | Description                           |
|--------|-----------------------------------|--------|---------------------------------------|
| GET    | `/physical-assessments`           | USER   | List authenticated user's assessments |
| GET    | `/physical-assessments/{id}`      | ADMIN  | Get by ID                             |
| GET    | `/physical-assessments/user/{id}` | ADMIN  | List assessments by user              |
| POST   | `/physical-assessments`           | ADMIN  | Create assessment                     |
| PUT    | `/physical-assessments`           | ADMIN  | Update assessment                     |
| DELETE | `/physical-assessments/{id}`      | ADMIN  | Delete assessment                     |

### Training Sessions

| Method | Endpoint                               | Access | Description          |
|--------|----------------------------------------|--------|----------------------|
| GET    | `/training-sessions`                   | USER   | List user's sessions |
| GET    | `/training-sessions/current`           | USER   | Get current session  |
| GET    | `/training-sessions/{id}`              | USER   | Get session by ID    |
| POST   | `/training-sessions`                   | USER   | Start session        |
| POST   | `/training-sessions/{id}/sets`         | USER   | Add set              |
| PUT    | `/training-sessions/{id}/sets/{setId}` | USER   | Update set           |
| DELETE | `/training-sessions/{id}/sets/{setId}` | USER   | Delete set           |
| PATCH  | `/training-sessions/{id}/finish`       | USER   | Finish session       |
| PATCH  | `/training-sessions/{id}/cancel`       | USER   | Cancel session       |

---

## 🧪 Testing

The project follows a **two-layer testing strategy**:

- **Integration tests (controllers)** — hit real HTTP endpoints against a live PostgreSQL container managed by Testcontainers, using REST Assured. Test classes share a singleton container instance for fast execution.
- **Unit tests (services)** — isolate business logic with Mockito, mocking repositories and other dependencies.

### Coverage

| Layer         | Class | Method | Line | Branch |
|---------------|-------|--------|------|--------|
| `controller`  | 100%  | 100%   | 100% | 100%   |
| `service`     | 100%  | 100%   | 99%  | 100%   |
| `security`    | 100%  | 100%   | 96%  | 83%    |
| `config`      | 100%  | 100%   | 100% | 100%   |
| `domain`      | 100%  | 100%   | 100% | 100%   |
| **Overall**   | 96%   | 94%    | 88%  | 65%    |

### Running tests

```bash
# Run all tests (unit + integration)
./mvnw clean verify
```

Integration tests use **Testcontainers** — Docker must be running.

---

## 🧑‍💻 Test Credentials

The API has pre-registered users available for testing in the production environment:

| Role  | Email                  | Password |
|-------|------------------------|----------|
| ADMIN | `admin.test@gmail.com` | `test`   |
| USER  | `user.test@gmail.com`  | `test`   |

Use the `POST /auth/login` endpoint with these credentials to get a token and test the endpoints in Swagger.

---

## ⚙️ CI/CD

The project uses **GitHub Actions** for CI and **Render** for continuous deployment.

The CI pipeline runs on every push and PR to `main`:

1. Sets up JDK 21 with Maven dependency cache
2. Executes `./mvnw clean verify` (unit + integration tests)
3. Integration tests spin up a real PostgreSQL container via Testcontainers
4. On success against `main`, Render auto-deploys the new build

### Deploy

The API is hosted on **Render** with automatic redeploy on every merge to `main`.

- **Base URL:** `https://gymflow-api-t7h9.onrender.com/gymflow/v1`

> 💡 Free-tier note: the first request after inactivity may take ~30s while Render wakes the instance.

---

## 🚀 Running locally

### Prerequisites

- Java 21
- Docker

### 1. Clone the repository

```bash
git clone https://github.com/luizgabrielcb/gymflow.git
cd gymflow
```

### 2. Configure environment variables

Copy the `.envTemplate` file and rename it to `.env`:

```bash
cp .envTemplate .env
```

Fill in the variables in `.env` for Docker Compose:

```env
POSTGRES_USER=your_user
POSTGRES_PASSWORD=your_password
POSTGRES_DB=gymflow
```

Configure the following environment variables in your IDE or operating system using the same values defined in `.env`:

```
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/${POSTGRES_DB}
SPRING_DATASOURCE_USERNAME=${POSTGRES_USER}
SPRING_DATASOURCE_PASSWORD=${POSTGRES_PASSWORD}
ENV_JWT_SECRET=your_secret
```

### 3. Start the database

```bash
docker-compose up -d
```

### 4. Run the application

Execute the main class `GymflowApplication` from your IDE.

### 5. Access Swagger

```
http://localhost:8080/swagger-ui/index.html
```