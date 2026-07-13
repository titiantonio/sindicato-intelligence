ALTER TABLE generated_content
    ADD COLUMN content_type VARCHAR(40) NOT NULL DEFAULT 'TELEGRAM_POST',
    ADD COLUMN length VARCHAR(40) NOT NULL DEFAULT 'STANDARD',
    ADD COLUMN generation_metadata JSONB NOT NULL DEFAULT '{}'::jsonb;

ALTER TABLE generated_content
    ADD CONSTRAINT ck_generated_content_type CHECK (content_type IN ('TELEGRAM_POST', 'TELEGRAM_SHORT', 'UNION_STATEMENT'));

CREATE INDEX idx_generated_content_active_generation
    ON generated_content (event_id, analysis_id, channel, content_type, status);
