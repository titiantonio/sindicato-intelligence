INSERT INTO users (email, password_hash, name, role, active, created_at, updated_at)
VALUES (
    'admin@sindicato.es',
    -- Semilla inicial: password en texto plano = Admin@123 (no cumple politica runtime de >=10).
    '$2a$10$.0NJUowt84Yi5NTnmPEewuAXtw2DvfYh9Iz/.c.iiBQCL7AZF12qG',
    'Admin Sindicato',
    'ADMIN',
    TRUE,
    NOW(),
    NOW()
)
ON CONFLICT (email) DO NOTHING;

INSERT INTO users (email, password_hash, name, role, active, created_at, updated_at)
VALUES (
    'n8n@sindicato.es',
    -- Semilla inicial: password en texto plano = Admin@123 (no cumple politica runtime de >=10).
    '$2a$10$.0NJUowt84Yi5NTnmPEewuAXtw2DvfYh9Iz/.c.iiBQCL7AZF12qG',
    'N8N Service',
    'ADMIN',
    TRUE,
    NOW(),
    NOW()
)
ON CONFLICT (email) DO NOTHING;
