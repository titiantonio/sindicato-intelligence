## Plataforma de Inteligencia Informativa para Sindicato de Docentes de Andalucía

Versión: 1.0

Estado: Diseño Base Aprobado

---

# 1. Objetivos

Este documento define:

- Tipos de datos PostgreSQL
- Primary Keys
- Foreign Keys
- Constraints
- Índices
- Estrategia de crecimiento
- Estrategia de auditoría

---

# 2. Convenciones

## Tablas

Formato:

```
snake_case
```

Ejemplo:

```
news_articles
generated_content
event_ai_analysis
```

---

## Columnas

Formato:

```
snake_case
```

Ejemplo:

```
published_at
created_at
updated_at
```

---

## Primary Keys

Todas las tablas:

```
id BIGSERIAL PRIMARY KEY
```

---

## Fechas

Todas las fechas:

```
TIMESTAMP WITH TIME ZONE
```

---

## JSON

Datos semiestructurados:

```
JSONB
```

---

# 3. Catálogos

---

# categories

```
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT NOW()
);
```

---

# subcategories

```
CREATE TABLE subcategories (
    id BIGSERIAL PRIMARY KEY,
    category_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    active BOOLEAN DEFAULT TRUE,

    CONSTRAINT fk_subcategories_category
    FOREIGN KEY (category_id)
    REFERENCES categories(id)
);
```

---

# channels

```
CREATE TABLE channels (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    active BOOLEAN DEFAULT TRUE
);
```

---

Datos iniciales:

```
TELEGRAM
FACEBOOK
X
```

---

# editorial_profiles

```
CREATE TABLE editorial_profiles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    active BOOLEAN DEFAULT TRUE
);
```

---

Datos iniciales:

```
INFORMATIVO
INSTITUCIONAL
DIVULGATIVO
REIVINDICATIVO
MOVILIZADOR
```

---

# 4. Captura de Noticias

---

# sources

```
CREATE TABLE sources (
    id BIGSERIAL PRIMARY KEY,

    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,

    url TEXT NOT NULL,

    active BOOLEAN DEFAULT TRUE,

    priority INTEGER DEFAULT 5,

    credibility_score NUMERIC(5,2),

    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);
```

---

# news_articles

Esta será una de las tablas más grandes del sistema.

```
CREATE TABLE news_articles (

    id BIGSERIAL PRIMARY KEY,

    source_id BIGINT NOT NULL,

    title TEXT NOT NULL,

    url TEXT NOT NULL,

    summary TEXT,

    content TEXT,

    featured_image_url TEXT,

    author VARCHAR(255),

    language VARCHAR(20) DEFAULT 'es',

    hash VARCHAR(128),

    processing_status VARCHAR(50),

    published_at TIMESTAMPTZ,

    captured_at TIMESTAMPTZ,

    created_at TIMESTAMPTZ DEFAULT NOW(),

    updated_at TIMESTAMPTZ DEFAULT NOW(),

    CONSTRAINT fk_news_source
    FOREIGN KEY (source_id)
    REFERENCES sources(id)
);
```

---

## Índices

```
CREATE UNIQUE INDEX idx_news_url
ON news_articles(url);
```

```
CREATE INDEX idx_news_status
ON news_articles(processing_status);
```

```
CREATE INDEX idx_news_published
ON news_articles(published_at);
```

```
CREATE INDEX idx_news_source
ON news_articles(source_id);
```

---

# 5. Clasificación IA

---

# news_classifications

```
CREATE TABLE news_classifications (

    id BIGSERIAL PRIMARY KEY,

    news_id BIGINT NOT NULL,

    category_id BIGINT,

    subcategory_id BIGINT,

    relevance_score NUMERIC(5,2),

    impact_score NUMERIC(5,2),

    urgency_score NUMERIC(5,2),

    confidence_score NUMERIC(5,2),

    keywords JSONB,

    organizations JSONB,

    created_at TIMESTAMPTZ DEFAULT NOW(),

    CONSTRAINT fk_classification_news
    FOREIGN KEY (news_id)
    REFERENCES news_articles(id)
);
```

---

# 6. Eventos

---

# events

La entidad principal del sistema.

```
CREATE TABLE events (

    id BIGSERIAL PRIMARY KEY,

    title TEXT NOT NULL,

    description TEXT,

    category_id BIGINT,

    status VARCHAR(50),

    importance_score NUMERIC(5,2),

    urgency_score NUMERIC(5,2),

    impact_score NUMERIC(5,2),

    first_detected_at TIMESTAMPTZ,

    last_updated_at TIMESTAMPTZ,

    created_at TIMESTAMPTZ DEFAULT NOW(),

    updated_at TIMESTAMPTZ DEFAULT NOW()
);
```

---

# event_news

```
CREATE TABLE event_news (

    event_id BIGINT NOT NULL,

    news_id BIGINT NOT NULL,

    created_at TIMESTAMPTZ DEFAULT NOW(),

    PRIMARY KEY(event_id, news_id),

    FOREIGN KEY(event_id)
    REFERENCES events(id),

    FOREIGN KEY(news_id)
    REFERENCES news_articles(id)
);
```

---

# event_candidates

```
CREATE TABLE event_candidates (

    id BIGSERIAL PRIMARY KEY,

    news_id BIGINT NOT NULL,

    suggested_event_id BIGINT,

    confidence_score NUMERIC(5,2),

    reason TEXT,

    status VARCHAR(50),

    created_at TIMESTAMPTZ DEFAULT NOW()
);
```

---

# event_ai_analysis

```
CREATE TABLE event_ai_analysis (

    id BIGSERIAL PRIMARY KEY,

    event_id BIGINT NOT NULL,

    executive_summary TEXT,

    union_summary TEXT,

    key_points JSONB,

    opportunities JSONB,

    risks JSONB,

    created_at TIMESTAMPTZ DEFAULT NOW()
);
```

---

# 7. IA

---

# ai_executions

Tabla fundamental para controlar:

- Costes
- Calidad
- Auditoría
- Reintentos

```
CREATE TABLE ai_executions (

    id BIGSERIAL PRIMARY KEY,

    entity_type VARCHAR(50),

    entity_id BIGINT,

    provider VARCHAR(100),

    model_name VARCHAR(100),

    prompt TEXT,

    response TEXT,

    tokens_input INTEGER,

    tokens_output INTEGER,

    execution_time_ms INTEGER,

    success BOOLEAN,

    error_message TEXT,

    created_at TIMESTAMPTZ DEFAULT NOW()
);
```

---

# 8. Contenido Editorial

---

# generated_content

```
CREATE TABLE generated_content (

    id BIGSERIAL PRIMARY KEY,

    parent_content_id BIGINT,

    event_id BIGINT NOT NULL,

    profile_id BIGINT NOT NULL,

    channel_id BIGINT NOT NULL,

    title TEXT,

    content TEXT NOT NULL,

    version INTEGER DEFAULT 1,

    status VARCHAR(50),

    created_at TIMESTAMPTZ DEFAULT NOW(),

    updated_at TIMESTAMPTZ DEFAULT NOW()
);
```

---

# publications

```
CREATE TABLE publications (

    id BIGSERIAL PRIMARY KEY,

    content_id BIGINT NOT NULL,

    channel_id BIGINT NOT NULL,

    status VARCHAR(50),

    scheduled_at TIMESTAMPTZ,

    published_at TIMESTAMPTZ,

    external_id VARCHAR(255),

    response TEXT,

    created_at TIMESTAMPTZ DEFAULT NOW()
);
```

---

# 9. Usuarios

---

# users

```
CREATE TABLE users (

    id BIGSERIAL PRIMARY KEY,

    name VARCHAR(255),

    email VARCHAR(255) UNIQUE NOT NULL,

    password_hash TEXT NOT NULL,

    role VARCHAR(50),

    active BOOLEAN DEFAULT TRUE,

    last_login TIMESTAMPTZ,

    created_at TIMESTAMPTZ DEFAULT NOW(),

    updated_at TIMESTAMPTZ DEFAULT NOW()
);
```

---

# 10. Auditoría

---

# audit_logs

```
CREATE TABLE users (

    id BIGSERIAL PRIMARY KEY,

    name VARCHAR(255),

    email VARCHAR(255) UNIQUE NOT NULL,

    password_hash TEXT NOT NULL,

    role VARCHAR(50),

    active BOOLEAN DEFAULT TRUE,

    last_login TIMESTAMPTZ,

    created_at TIMESTAMPTZ DEFAULT NOW(),

    updated_at TIMESTAMPTZ DEFAULT NOW()
);
```

---

# workflow_executions

```
CREATE TABLE workflow_executions (

    id BIGSERIAL PRIMARY KEY,

    workflow_name VARCHAR(255),

    status VARCHAR(50),

    started_at TIMESTAMPTZ,

    finished_at TIMESTAMPTZ,

    items_processed INTEGER,

    error_message TEXT
);
```

---

# 11. Estrategia MVP

Para el MVP implementaría únicamente estas tablas:

```
categories
channels
editorial_profiles

sources
news_articles

news_classifications

events
event_news

event_ai_analysis

generated_content

publications

users
```

Y dejaría para la Fase 2:

```
event_candidates
ai_executions
audit_logs
workflow_executions
```

Porque ahora mismo el objetivo es validar el flujo:

```
Captura
↓
Clasificación
↓
Evento
↓
Resumen
↓
Contenido
↓
Publicación
```

sin añadir complejidad excesiva.