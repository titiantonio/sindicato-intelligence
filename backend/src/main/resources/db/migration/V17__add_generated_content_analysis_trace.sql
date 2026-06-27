ALTER TABLE generated_content
    ADD COLUMN analysis_id BIGINT NULL;

ALTER TABLE generated_content
    ADD CONSTRAINT fk_generated_content_analysis
        FOREIGN KEY (analysis_id)
        REFERENCES event_ai_analysis (id);

CREATE INDEX idx_generated_content_analysis_id
    ON generated_content (analysis_id);
