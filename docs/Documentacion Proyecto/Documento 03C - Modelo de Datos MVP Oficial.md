## Plataforma de Inteligencia Informativa para Sindicato de Docentes de Andalucía

Versión 1.0

Estado: Definitivo MVP

---

# Principios

## MD-001

Las noticias son materia prima.

---

## MD-002

Los eventos son el núcleo del sistema.

---

## MD-003

Todo el contenido se genera desde eventos.

---

## MD-004

Toda acción relevante debe ser auditable.

---

# Vista Global

```
sources
    │
    ▼
news_articles
    │
    ▼
news_classifications
    │
    ▼
event_news
    │
    ▼
events
    │
    ▼
event_ai_analysis
    │
    ▼
generated_content
    │
    ▼
publications
```

---

# 1. sources

Fuentes monitorizadas.

```
CREATE TABLE sources (
    id BIGSERIAL PRIMARY KEY,

    name VARCHAR(255) NOT NULL,

    source_type VARCHAR(50) NOT NULL,

    url TEXT NOT NULL,

    category VARCHAR(100),

    priority INTEGER DEFAULT 50,

    is_active BOOLEAN DEFAULT TRUE,

    created_at TIMESTAMP DEFAULT NOW(),

    updated_at TIMESTAMP DEFAULT NOW()
);
```

---

# 2. news_articles

Noticias capturadas.

```
CREATE TABLE news_articles (

    id BIGSERIAL PRIMARY KEY,

    source_id BIGINT NOT NULL,

    title TEXT NOT NULL,

    url TEXT NOT NULL UNIQUE,

    summary TEXT,

    content TEXT,

    author VARCHAR(255),

    image_url TEXT,

    published_at TIMESTAMP,

    captured_at TIMESTAMP DEFAULT NOW(),

    processing_status VARCHAR(50) DEFAULT 'CAPTURED',

    language VARCHAR(10) DEFAULT 'es',

    created_at TIMESTAMP DEFAULT NOW(),

    updated_at TIMESTAMP DEFAULT NOW(),

    CONSTRAINT fk_news_source
        FOREIGN KEY (source_id)
        REFERENCES sources(id)
);
```

---

# 3. news_classifications

Resultado IA.

```
CREATE TABLE news_classifications (

    id BIGSERIAL PRIMARY KEY,

    news_id BIGINT NOT NULL,

    category VARCHAR(100),

    subcategory VARCHAR(100),

    relevance_score INTEGER,

    urgency_score INTEGER,

    impact_score INTEGER,

    keywords JSONB,

    entities JSONB,

    ai_model VARCHAR(100),

    created_at TIMESTAMP DEFAULT NOW(),

    CONSTRAINT fk_classification_news
        FOREIGN KEY (news_id)
        REFERENCES news_articles(id)
);
```

---

# 4. events

Entidad principal.

```
CREATE TABLE events (

    id BIGSERIAL PRIMARY KEY,

    title TEXT NOT NULL,

    canonical_description TEXT,

    category VARCHAR(100),

    importance VARCHAR(50),

    status VARCHAR(50),

    confidence_score INTEGER,

    first_detected_at TIMESTAMP,

    last_activity_at TIMESTAMP,

    created_at TIMESTAMP DEFAULT NOW(),

    updated_at TIMESTAMP DEFAULT NOW()
);
```

---

# 5. event_news

Relación N:M.

```
CREATE TABLE event_news (

    id BIGSERIAL PRIMARY KEY,

    event_id BIGINT NOT NULL,

    news_id BIGINT NOT NULL,

    match_confidence INTEGER,

    created_at TIMESTAMP DEFAULT NOW(),

    UNIQUE(event_id, news_id),

    CONSTRAINT fk_event_news_event
        FOREIGN KEY (event_id)
        REFERENCES events(id),

    CONSTRAINT fk_event_news_news
        FOREIGN KEY (news_id)
        REFERENCES news_articles(id)
);
```

---

# 6. event_ai_analysis

Análisis consolidado.

```
CREATE TABLE event_ai_analysis (

    id BIGSERIAL PRIMARY KEY,

    event_id BIGINT NOT NULL,

    executive_summary TEXT,

    union_summary TEXT,

    key_points JSONB,

    risks JSONB,

    opportunities JSONB,

    ai_model VARCHAR(100),

    generated_at TIMESTAMP DEFAULT NOW(),

    CONSTRAINT fk_analysis_event
        FOREIGN KEY (event_id)
        REFERENCES events(id)
);
```

---

# 7. generated_content

Contenido editorial.

```
CREATE TABLE generated_content (

    id BIGSERIAL PRIMARY KEY,

    event_id BIGINT NOT NULL,

    channel VARCHAR(50),

    editorial_profile VARCHAR(50),

    title TEXT,

    content TEXT,

    status VARCHAR(50),

    version INTEGER DEFAULT 1,

    generated_by_ai BOOLEAN DEFAULT TRUE,

    created_at TIMESTAMP DEFAULT NOW(),

    updated_at TIMESTAMP DEFAULT NOW(),

    CONSTRAINT fk_content_event
        FOREIGN KEY (event_id)
        REFERENCES events(id)
);
```

---

# 8. publications

Publicaciones reales.

```
CREATE TABLE publications (

    id BIGSERIAL PRIMARY KEY,

    content_id BIGINT NOT NULL,

    channel VARCHAR(50),

    publication_status VARCHAR(50),

    external_id VARCHAR(255),

    published_at TIMESTAMP,

    response_payload JSONB,

    created_at TIMESTAMP DEFAULT NOW(),

    CONSTRAINT fk_publication_content
        FOREIGN KEY (content_id)
        REFERENCES generated_content(id)
);
```

---

# 9. users

Usuarios internos.

```
CREATE TABLE users (

    id BIGSERIAL PRIMARY KEY,

    name VARCHAR(255),

    email VARCHAR(255) UNIQUE,

    password_hash TEXT,

    role VARCHAR(50),

    is_active BOOLEAN DEFAULT TRUE,

    created_at TIMESTAMP DEFAULT NOW(),

    updated_at TIMESTAMP DEFAULT NOW()
);
```

---

# 10. audit_log

Auditoría.

```
CREATE TABLE audit_log (

    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT,

    action VARCHAR(255),

    entity_type VARCHAR(100),

    entity_id BIGINT,

    old_values JSONB,

    new_values JSONB,

    created_at TIMESTAMP DEFAULT NOW()
);
```

---

# 11. ai_requests

Muy recomendable desde el inicio.

Te ahorrará muchos problemas.

```
CREATE TABLE ai_requests (

    id BIGSERIAL PRIMARY KEY,

    operation_type VARCHAR(100),

    entity_type VARCHAR(100),

    entity_id BIGINT,

    prompt TEXT,

    response JSONB,

    model VARCHAR(100),

    tokens_input INTEGER,

    tokens_output INTEGER,

    execution_time_ms INTEGER,

    created_at TIMESTAMP DEFAULT NOW()
);
```

---

# 12. source_errors

Para controlar fallos RSS/XML.

```
CREATE TABLE source_errors (

    id BIGSERIAL PRIMARY KEY,

    source_id BIGINT,

    error_message TEXT,

    error_date TIMESTAMP DEFAULT NOW(),

    resolved BOOLEAN DEFAULT FALSE
);
```

---

# Índices imprescindibles

```
CREATE INDEX idx_news_status
ON news_articles(processing_status);

CREATE INDEX idx_news_published
ON news_articles(published_at);

CREATE INDEX idx_events_status
ON events(status);

CREATE INDEX idx_events_category
ON events(category);

CREATE INDEX idx_content_status
ON generated_content(status);

CREATE INDEX idx_publication_status
ON publications(publication_status);
```

---

# Estados recomendados

## news_articles.processing_status

```
CAPTURED

CLASSIFIED

EVENT_MATCHED

ANALYZED

CONTENT_GENERATED

PUBLISHED
```

---

## events.status

```
OPEN

MONITORING

CLOSED

ARCHIVED
```

---

## generated_content.status

```
GENERATED

PENDING_REVIEW

APPROVED

REJECTED

PUBLISHED
```

---

## publications.publication_status

```
PENDING

SCHEDULED

PUBLISHED

FAILED
```