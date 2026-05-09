CREATE TABLE workouts
(
    id BIGSERIAL PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    muscle_groups VARCHAR(255),
    user_id       BIGINT       NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT uk_workout_name_user UNIQUE (name, user_id)
);