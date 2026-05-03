CREATE TABLE session_sets
(
    id BIGSERIAL PRIMARY KEY,
    training_session_id BIGINT        NOT NULL REFERENCES training_sessions (id) ON DELETE CASCADE,
    exercise_id         BIGINT        NOT NULL REFERENCES exercises (id),
    set_number          INTEGER       NOT NULL,
    reps_number         INTEGER       NOT NULL,
    weight_kg           DECIMAL(5, 2) NOT NULL,
    rest_seconds        INTEGER       NOT NULL
);