CREATE TABLE workout_exercise
(
    id BIGSERIAL PRIMARY KEY,
    workout_id  BIGINT  NOT NULL,
    exercise_id BIGINT  NOT NULL,
    sets        INTEGER NOT NULL,
    reps        INTEGER NOT NULL,
    CONSTRAINT fk_workout FOREIGN KEY (workout_id) REFERENCES workouts (id) ON DELETE CASCADE,
    CONSTRAINT fk_exercise FOREIGN KEY (exercise_id) REFERENCES exercises (id)
);