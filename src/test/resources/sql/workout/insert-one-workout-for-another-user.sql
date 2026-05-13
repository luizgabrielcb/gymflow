INSERT INTO workouts (name, muscle_groups, user_id)
VALUES ('Upper Body', 'CHEST', (SELECT id FROM users WHERE email = 'test.test@gmail.com'));