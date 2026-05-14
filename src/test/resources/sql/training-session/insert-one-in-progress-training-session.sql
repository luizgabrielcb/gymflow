INSERT INTO training_sessions (user_id, workout_id, status, started_at, finished_at, duration_minutes)
VALUES ((SELECT id FROM users WHERE email = 'user.test@gmail.com'),
        (SELECT id FROM workouts WHERE name = 'Upper Body'),
        'IN_PROGRESS',
        NOW(),
        NULL,
        NULL);