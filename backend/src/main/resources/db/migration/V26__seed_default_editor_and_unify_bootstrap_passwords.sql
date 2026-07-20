-- Password comun de bootstrap/local: Admin@12345
-- Hash BCrypt generado con Spring Security para cuentas tecnicas/default.
WITH bootstrap_password AS (
    SELECT '$2a$10$7GM2nYgIU8j/7iI8EWm9pO7dr6VjpJG5nkuoNQue6mMAHWtJYfBKK'::VARCHAR(255) AS password_hash
), default_users(email, name, role) AS (
    VALUES
        ('admin@sindicato.es', 'Admin Sindicato', 'ADMIN'),
        ('n8n@sindicato.es', 'N8N Service', 'ADMIN'),
        ('editor@sindicato.es', 'Editor Sindicato', 'EDITOR')
)
INSERT INTO users (
    email,
    password_hash,
    name,
    role,
    active,
    created_at,
    updated_at,
    must_change_password,
    status,
    temporary_password_expires_at,
    last_password_change_at
)
SELECT
    default_users.email,
    bootstrap_password.password_hash,
    default_users.name,
    default_users.role,
    TRUE,
    NOW(),
    NOW(),
    FALSE,
    'ACTIVE',
    NULL,
    NOW()
FROM default_users
CROSS JOIN bootstrap_password
ON CONFLICT (email) DO UPDATE
SET password_hash = EXCLUDED.password_hash,
    name = EXCLUDED.name,
    role = EXCLUDED.role,
    active = TRUE,
    updated_at = NOW(),
    must_change_password = FALSE,
    status = 'ACTIVE',
    temporary_password_expires_at = NULL,
    last_password_change_at = NOW();

UPDATE refresh_tokens
SET revoked_at = NOW()
WHERE user_id IN (
    SELECT id
    FROM users
    WHERE email IN ('admin@sindicato.es', 'n8n@sindicato.es', 'editor@sindicato.es')
)
  AND revoked_at IS NULL;
