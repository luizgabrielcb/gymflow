INSERT INTO session_sets (training_session_id, exercise_id, set_number, reps_number, weight_kg, rest_seconds)
VALUES ((SELECT id
         FROM training_sessions
         WHERE user_id = (SELECT id FROM users WHERE email = 'user.test@gmail.com')
           AND status = 'COMPLETED'),
        (SELECT id FROM exercises WHERE name = 'Bench Press'),
        1,
        10,
        50.0,
        60);