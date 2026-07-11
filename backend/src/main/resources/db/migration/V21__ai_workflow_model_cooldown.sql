ALTER TABLE ai_workflow_settings
    ADD COLUMN cooldown_seconds INTEGER NOT NULL DEFAULT 60;

ALTER TABLE ai_workflow_settings
    ADD CONSTRAINT ck_ai_workflow_settings_cooldown CHECK (cooldown_seconds >= 0);
