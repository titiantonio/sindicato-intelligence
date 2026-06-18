CREATE TABLE ai_prompt_versions
(
    id BIGSERIAL PRIMARY KEY,
    prompt_key VARCHAR(80) NOT NULL,
    prompt_name VARCHAR(160) NOT NULL,
    module VARCHAR(80) NOT NULL,
    version VARCHAR(40) NOT NULL,
    checksum VARCHAR(64) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_ai_prompt_versions_key_version UNIQUE (prompt_key, version)
);

CREATE INDEX idx_ai_prompt_versions_active ON ai_prompt_versions (active);

CREATE TABLE ai_operation_metrics
(
    id BIGSERIAL PRIMARY KEY,
    operation_type VARCHAR(80) NOT NULL,
    prompt_key VARCHAR(80) NOT NULL,
    provider VARCHAR(80) NOT NULL,
    model VARCHAR(120),
    status VARCHAR(30) NOT NULL,
    related_entity_type VARCHAR(80),
    related_entity_id BIGINT,
    latency_ms BIGINT NOT NULL,
    error_message VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_ai_operation_metrics_status CHECK (status IN ('SUCCESS', 'FAILED')),
    CONSTRAINT ck_ai_operation_metrics_latency CHECK (latency_ms >= 0)
);

CREATE INDEX idx_ai_operation_metrics_created_at ON ai_operation_metrics (created_at DESC);
CREATE INDEX idx_ai_operation_metrics_operation_status ON ai_operation_metrics (operation_type, status);

INSERT INTO ai_prompt_versions (prompt_key, prompt_name, module, version, checksum, active)
VALUES
    ('WF02_CLASSIFICATION', 'Clasificacion de noticias', 'classification', '1.0.0', 'f5d89b8f7ce78e3137c6b0e789dc51d8a99625ff8d8b9af4ea6b5bd6a845621a', TRUE),
    ('WF03_EVENT_MATCHING', 'Agrupacion de eventos', 'event', '1.0.0', '8d7f41e6f77eae71b18d8d9704f87d95dba9ec4f2511d6f1d983b80b4fa6d3a4', TRUE),
    ('WF04_ANALYSIS', 'Analisis de evento', 'analysis', '1.0.0', '52a4ef08963b497595b5467ff3d3d2011cb1f983e9373c4e6ac94cd87bb24a68', TRUE),
    ('WF05_CONTENT', 'Generacion de contenido Telegram', 'content', '1.0.0', '7a9ca36bd2836edbdc2c696e003e41c70d6fd02c7acef27cd98c71555d2054b1', TRUE);
