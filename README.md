# 💪 GymFlow API

![CI](https://github.com/luizgabrielcb/gymflow/actions/workflows/workflow.yml/badge.svg)

RESTful API for managing workouts, training sessions, and physical assessments. Built with Spring Boot 3.5, Spring
Security with JWT authentication, and full integration test coverage.

🔗 **[Swagger UI](https://gymflow-api-t7h9.onrender.com/swagger-ui/index.html)** | 📦 *
*[Repository](https://github.com/luizgabrielcb/gymflow)**

## ⚙️ CI

The project uses **GitHub Actions** to automatically run tests on every push or pull request to the `main` branch.

The pipeline runs:

1. Project build
2. Unit and integration tests with Testcontainers

## ☁️ Deploy

The API is hosted on **Render** with automatic redeploy on every merge to `main`.

- **Base URL:** `https://gymflow-api-t7h9.onrender.com/gymflow/v1`

> ⚠️ The service may take a few seconds to respond on the first request since Render puts free-tier instances to sleep
> after inactivity.

---

## 🛠️ Stack

| Technology            | Version     |
|-----------------------|-------------|
| Java                  | 21          |
| Spring Boot           | 3.5         |
| Spring Security + JWT | auth0 4.5.2 |
| PostgreSQL            | 17          |
| Flyway                | -           |
| MapStruct             | -           |
| SpringDoc/Swagger UI  | -           |
| Bean Validation       | -           |
| Docker                | -           |

### Testing

| Technology     | Usage                                      |
|----------------|--------------------------------------------|
| JUnit 5        | Unit tests                                 |
| Mockito        | Mocking                                    |
| Testcontainers | PostgreSQL container for integration tests |
| REST Assured   | HTTP integration tests                     |

---

## 🔐 Authentication

The API uses **JWT** with a **rotating refresh token** strategy:

- `POST /auth/register` — User registration
- `POST /auth/login` — Login, returns `accessToken` (2h) + `refreshToken` (7 days)
- `POST /auth/refresh` — Generates a new token pair and revokes the previous refresh token
- `POST /auth/logout` — Revokes the refresh token

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

## 🧑‍💻 Test Credentials

The API has pre-registered users available for testing in the production environment:

| Role  | Email                  | Password |
|-------|------------------------|----------|
| ADMIN | `admin.test@gmail.com` | `test`   |
| USER  | `user.test@gmail.com`  | `test`   |

Use the `POST /auth/login` endpoint with these credentials to get a token and test the endpoints in Swagger.

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

## 🚀 Running locally

### Prerequisites

- Java 21
- Docker

### 1. Clone the repository

```bash
git clone https://github.com/luizgabrielcb/gymflow.git
cd gymflow
```

### 2. Start the database

```bash
docker-compose up -d
```

### 3. Configure environment variables

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

### 4. Run the application

Execute the main class `GymflowApplication` from your IDE.

### 5. Access Swagger

```
http://localhost:8080/swagger-ui/index.html
```

---

## 🧪 Tests

```bash
# Run all tests (unit + integration)
./mvnw clean verify
```

Integration tests use **Testcontainers** — Docker must be running.

---