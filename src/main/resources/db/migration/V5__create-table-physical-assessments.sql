CREATE TABLE physical_assessments
(
    id BIGSERIAL PRIMARY KEY,
    weight         DECIMAL(5, 2) NOT NULL,
    height         DECIMAL(5, 2) NOT NULL,
    fat_percentage DECIMAL(5, 2),
    user_id        BIGINT        NOT NULL,
    created_at     TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);