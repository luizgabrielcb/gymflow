CREATE TABLE workout_exercise
(
    id          BIGSERIAL PRIMARY KEY,

    workout_id  BIGINT NOT NULL,
    exercise_id BIGINT NOT NULL,

    sets        INT    NOT NULL,
    reps        INT    NOT NULL,
    weight      DECIMAL(10, 2),

    CONSTRAINT fk_workout
        FOREIGN KEY (workout_id)
            REFERENCES workouts (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_exercise
        FOREIGN KEY (exercise_id)
            REFERENCES exercises (id)
            ON DELETE CASCADE
);