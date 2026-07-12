ALTER TABLE event_news
    ADD COLUMN match_decision VARCHAR(80),
    ADD COLUMN match_reason TEXT;

CREATE INDEX idx_event_news_match_decision ON event_news (match_decision);
