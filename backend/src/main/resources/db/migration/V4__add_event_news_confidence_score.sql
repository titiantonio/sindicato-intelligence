ALTER TABLE event_news
    ADD COLUMN confidence_score INTEGER;

ALTER TABLE event_news
    ADD CONSTRAINT ck_event_news_confidence_score
        CHECK (confidence_score IS NULL OR confidence_score BETWEEN 0 AND 100);
