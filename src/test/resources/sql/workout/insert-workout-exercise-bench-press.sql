INSERT INTO workout_exercise (workout_id, exercise_id, sets, reps)
VALUES ((SELECT id FROM workouts WHERE name = 'Upper Body'),
        (SELECT id FROM exercises WHERE name = 'Bench Press'),
        3,
        10);