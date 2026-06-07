INSERT INTO users (email, password_hash, name, role, active, created_at, updated_at)
VALUES (
    'admin@sindicato.es',
    '$2a$10$HWQTTEHUfYP5CPl/pb9x8eMSPRLhZe4ORY8phgDWm95dNXmSMTCYi',
    'Admin Sindicato',
    'ADMIN',
    TRUE,
    NOW(),
    NOW()
)
ON CONFLICT (email) DO NOTHING;
