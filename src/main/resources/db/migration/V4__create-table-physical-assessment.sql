CREATE TABLE physical_assessment
(
    id             BIGSERIAL PRIMARY KEY,
    weight         NUMERIC(5,2) NOT NULL,
    height         NUMERIC(4,2) NOT NULL,
    fat_percentage NUMERIC(4,2) NOT NULL,
    user_id        BIGINT  NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
);