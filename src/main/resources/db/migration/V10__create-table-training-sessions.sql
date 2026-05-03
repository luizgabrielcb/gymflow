CREATE TABLE training_sessions
(
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT      NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    workout_id       BIGINT      NOT NULL REFERENCES workouts (id) ON DELETE CASCADE,
    status           VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',
    started_at       TIMESTAMP   NOT NULL DEFAULT NOW(),
    finished_at      TIMESTAMP,
    duration_minutes INTEGER
);