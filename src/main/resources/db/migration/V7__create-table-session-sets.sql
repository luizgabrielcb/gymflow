CREATE TABLE session_sets
(
    id BIGSERIAL PRIMARY KEY,
    training_session_id BIGINT  NOT NULL,
    exercise_id         BIGINT  NOT NULL,
    set_number          INTEGER NOT NULL,
    reps_number         INTEGER NOT NULL,
    weight_kg           DECIMAL(5, 2),
    rest_seconds        INTEGER,
    CONSTRAINT fk_training_session FOREIGN KEY (training_session_id) REFERENCES training_sessions (id) ON DELETE CASCADE,
    CONSTRAINT fk_exercise FOREIGN KEY (exercise_id) REFERENCES exercises (id),
    CONSTRAINT uk_session_exercise_set UNIQUE (training_session_id, exercise_id, set_number)
);