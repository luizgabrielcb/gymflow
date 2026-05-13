INSERT INTO workouts (name, muscle_groups, user_id)
VALUES ('Upper Body', 'CHEST', (SELECT id FROM users WHERE email = 'user.test@gmail.com')),
       ('Lower Body', 'LEGS', (SELECT id FROM users WHERE email = 'user.test@gmail.com'));