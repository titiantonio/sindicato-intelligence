ALTER TABLE users
    ADD COLUMN must_change_password BOOLEAN NOT NULL DEFAULT FALSE;

CREATE TABLE user_password_history
(
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_user_password_history_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_user_password_history_user_id ON user_password_history (user_id);
CREATE INDEX idx_users_must_change_password ON users (must_change_password);
