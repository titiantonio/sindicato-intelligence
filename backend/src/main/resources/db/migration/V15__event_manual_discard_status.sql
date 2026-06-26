ALTER TABLE events
    ADD COLUMN manual_discarded BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN manual_discarded_at TIMESTAMPTZ;

UPDATE events event
SET manual_discarded = TRUE,
    manual_discarded_at = COALESCE((
        SELECT MAX(audit.created_at)
        FROM audit_log audit
        WHERE audit.action = 'EVENT_DISCARDED'
          AND audit.entity_type = 'EVENT'
          AND audit.entity_id = event.id
    ), event.updated_at),
    status = CASE
        WHEN event.status = 'ARCHIVED' THEN 'OPEN'
        ELSE event.status
    END,
    updated_at = COALESCE((
        SELECT MAX(audit.created_at)
        FROM audit_log audit
        WHERE audit.action = 'EVENT_DISCARDED'
          AND audit.entity_type = 'EVENT'
          AND audit.entity_id = event.id
    ), event.updated_at)
WHERE EXISTS (
    SELECT 1
    FROM audit_log audit
    WHERE audit.action = 'EVENT_DISCARDED'
      AND audit.entity_type = 'EVENT'
      AND audit.entity_id = event.id
);

CREATE INDEX idx_events_manual_discarded ON events (manual_discarded);
