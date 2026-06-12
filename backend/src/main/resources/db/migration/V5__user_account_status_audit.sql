ALTER TABLE users
    ADD COLUMN status VARCHAR(50),
    ADD COLUMN temporary_password_expires_at TIMESTAMP WITH TIME ZONE,
    ADD COLUMN last_login_at TIMESTAMP WITH TIME ZONE,
    ADD COLUMN last_password_change_at TIMESTAMP WITH TIME ZONE;

UPDATE users
SET status = CASE WHEN active THEN 'ACTIVE' ELSE 'INACTIVE' END;

ALTER TABLE users
    ALTER COLUMN status SET NOT NULL;

CREATE TABLE user_audit_log
(
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    actor_email VARCHAR(255),
    action VARCHAR(100) NOT NULL,
    details TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_user_audit_log_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_users_status ON users (status);
CREATE INDEX idx_users_temporary_password_expires_at ON users (temporary_password_expires_at);
CREATE INDEX idx_user_audit_log_user_id ON user_audit_log (user_id);
CREATE INDEX idx_user_audit_log_action ON user_audit_log (action);
CREATE INDEX idx_user_audit_log_created_at ON user_audit_log (created_at);
