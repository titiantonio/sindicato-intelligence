ALTER TABLE event_ai_analysis
    ADD COLUMN affected_groups JSONB,
    ADD COLUMN recommended_monitoring JSONB,
    ADD COLUMN analysis_type VARCHAR(40) NOT NULL DEFAULT 'STANDARD',
    ADD COLUMN generation_trigger VARCHAR(40) NOT NULL DEFAULT 'BATCH',
    ADD COLUMN event_updated_at_snapshot TIMESTAMP WITH TIME ZONE,
    ADD COLUMN context_news_count INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN context_truncated BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE event_ai_analysis analysis
SET event_updated_at_snapshot = event.updated_at,
    context_news_count = COALESCE((
        SELECT COUNT(*)
        FROM event_news
        WHERE event_news.event_id = analysis.event_id
    ), 0)
FROM events event
WHERE event.id = analysis.event_id
  AND analysis.event_updated_at_snapshot IS NULL;

ALTER TABLE event_ai_analysis
    ALTER COLUMN event_updated_at_snapshot SET NOT NULL,
    ADD CONSTRAINT ck_event_ai_analysis_type CHECK (analysis_type IN ('QUICK', 'STANDARD', 'PRIORITY', 'CRISIS')),
    ADD CONSTRAINT ck_event_ai_analysis_trigger CHECK (generation_trigger IN ('BATCH', 'MANUAL', 'PRIORITY_AUTO', 'REANALYSIS')),
    ADD CONSTRAINT ck_event_ai_analysis_context_news_count CHECK (context_news_count >= 0);

CREATE INDEX idx_event_ai_analysis_event_generated
    ON event_ai_analysis (event_id, generated_at DESC, id DESC);

CREATE INDEX idx_event_ai_analysis_type_trigger
    ON event_ai_analysis (analysis_type, generation_trigger);
