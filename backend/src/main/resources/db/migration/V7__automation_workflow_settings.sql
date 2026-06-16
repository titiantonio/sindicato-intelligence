CREATE TABLE automation_workflow_settings
(
    workflow_code VARCHAR(50) PRIMARY KEY,
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    interval_seconds INTEGER NOT NULL,
    batch_size INTEGER NOT NULL,
    running BOOLEAN NOT NULL DEFAULT FALSE,
    last_run_at TIMESTAMP WITH TIME ZONE,
    last_success_at TIMESTAMP WITH TIME ZONE,
    last_failure_at TIMESTAMP WITH TIME ZONE,
    next_run_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_processed_count INTEGER NOT NULL DEFAULT 0,
    last_success_count INTEGER NOT NULL DEFAULT 0,
    last_failed_count INTEGER NOT NULL DEFAULT 0,
    last_skipped_count INTEGER NOT NULL DEFAULT 0,
    last_error TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_automation_workflow_settings_interval CHECK (interval_seconds >= 60),
    CONSTRAINT ck_automation_workflow_settings_batch_size CHECK (batch_size >= 1)
);

INSERT INTO automation_workflow_settings (
    workflow_code,
    enabled,
    interval_seconds,
    batch_size,
    next_run_at
) VALUES
    ('WF02_CLASSIFICATION', TRUE, 600, 1, NOW() + INTERVAL '10 minutes'),
    ('WF03_EVENT_DETECTION', TRUE, 600, 3, NOW() + INTERVAL '10 minutes'),
    ('WF04_ANALYSIS', FALSE, 900, 1, NOW() + INTERVAL '15 minutes');
