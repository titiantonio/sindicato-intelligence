CREATE TABLE ai_provider_settings
(
    provider_code VARCHAR(50) PRIMARY KEY,
    display_name VARCHAR(120) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    api_key_encrypted TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE ai_workflow_settings
(
    workflow_code VARCHAR(80) PRIMARY KEY,
    provider_code VARCHAR(50) NOT NULL,
    model_name VARCHAR(160) NOT NULL,
    temperature NUMERIC(4, 3) NOT NULL DEFAULT 0.2,
    max_output_tokens INTEGER NOT NULL DEFAULT 1024,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_ai_workflow_settings_provider FOREIGN KEY (provider_code) REFERENCES ai_provider_settings (provider_code),
    CONSTRAINT ck_ai_workflow_settings_temperature CHECK (temperature >= 0 AND temperature <= 2),
    CONSTRAINT ck_ai_workflow_settings_max_tokens CHECK (max_output_tokens >= 1)
);

INSERT INTO ai_provider_settings (
    provider_code,
    display_name,
    enabled
) VALUES
    ('deterministic', 'Determinista local', TRUE),
    ('gemini', 'Google Gemini', FALSE);

INSERT INTO ai_workflow_settings (
    workflow_code,
    provider_code,
    model_name,
    temperature,
    max_output_tokens
) VALUES
    ('WF02_CLASSIFICATION', 'deterministic', 'deterministic-classification', 0.2, 1024),
    ('WF03_EVENT_MATCHING', 'deterministic', 'deterministic-event-matching', 0.2, 1024),
    ('WF04_ANALYSIS', 'deterministic', 'deterministic-analysis', 0.2, 1024),
    ('WF05_CONTENT', 'deterministic', 'deterministic-content', 0.2, 1024);
