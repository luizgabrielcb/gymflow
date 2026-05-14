ALTER TABLE refresh_tokens
    DROP CONSTRAINT refresh_tokens_user_id_fkey,
    ADD CONSTRAINT refresh_tokens_user_id_fkey
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;