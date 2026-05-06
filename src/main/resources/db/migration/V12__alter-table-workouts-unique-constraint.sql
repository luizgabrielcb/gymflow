ALTER TABLE workouts DROP CONSTRAINT workouts_name_key;
ALTER TABLE workouts ADD CONSTRAINT workouts_name_user_unique UNIQUE (name, user_id);