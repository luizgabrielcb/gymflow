CREATE TABLE training_sessions
(
    id BIGSERIAL PRIMARY KEY,
    user_id          BIGINT      NOT NULL,
    workout_id       BIGINT      NOT NULL,
    status           VARCHAR(50) NOT NULL,
    started_at       TIMESTAMP   NOT NULL DEFAULT NOW(),
    finished_at      TIMESTAMP,
    duration_minutes INTEGER,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_workout FOREIGN KEY (workout_id) REFERENCES workouts (id)
);