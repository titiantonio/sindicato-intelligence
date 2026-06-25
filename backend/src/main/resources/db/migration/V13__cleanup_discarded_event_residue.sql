UPDATE news_articles news
SET processing_status = 'DISCARDED',
    updated_at = NOW()
FROM news_classifications classification
WHERE classification.news_id = news.id
  AND classification.category = 'OTROS'
  AND classification.subcategory IN ('FUERA_DE_AMBITO', 'INFORMACION_INSUFICIENTE')
  AND classification.relevance_score = 0
  AND news.processing_status IN ('CLASSIFIED', 'EVENT_MATCHED', 'ARCHIVED');

UPDATE events event
SET status = 'ARCHIVED',
    last_updated_at = NOW(),
    updated_at = NOW()
WHERE event.status IN ('OPEN', 'MONITORING', 'CLOSED')
  AND EXISTS (
      SELECT 1
      FROM event_news event_news
      WHERE event_news.event_id = event.id
  )
  AND NOT EXISTS (
      SELECT 1
      FROM event_news event_news
      LEFT JOIN news_classifications classification ON classification.news_id = event_news.news_id
      WHERE event_news.event_id = event.id
        AND (
            classification.news_id IS NULL
            OR NOT (
                classification.category = 'OTROS'
                AND classification.subcategory IN ('FUERA_DE_AMBITO', 'INFORMACION_INSUFICIENTE')
                AND classification.relevance_score = 0
            )
        )
  );
