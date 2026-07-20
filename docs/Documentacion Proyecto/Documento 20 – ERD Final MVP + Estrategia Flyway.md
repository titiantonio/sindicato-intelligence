Versión: 1.0

Estado: Diseño Técnico Definitivo

---

# 1. Objetivo

Definir:

- Modelo físico definitivo MVP.
- Relaciones entre entidades.
- Índices.
- Constraints.
- Estrategia de migraciones Flyway.
- Convenciones de nomenclatura.

Este documento sustituye cualquier definición previa del modelo físico.

Documento maestro de referencia:

03D – Modelo de Datos Producción MVP

---

# 2. Principios de Diseño

## DB-001

PostgreSQL será la única base de datos.

---

## DB-002

Todas las tablas utilizarán:

```sql
BIGSERIAL
```

como clave primaria.

---

## DB-003

Todas las fechas se almacenarán en:

```sql
TIMESTAMP WITH TIME ZONE
```

---

## DB-004

Toda modificación de esquema se realizará mediante Flyway.

---

## DB-005

Nunca modificar tablas manualmente en producción.

---

# 3. Diagrama General MVP

```text
sources
   │
   │ 1:N
   ▼

news_articles
   │
   │ 1:1
   ▼

news_classifications

news_articles
   │
   │ N:M
   ▼

event_news
   │
   ▼

events
   │
   │ 1:N
   ▼

event_ai_analysis

events
   │
   │ 1:N
   ▼

generated_content
   │
   │ 1:N
   ▼

publications

users
   │
   │ 1:N
   ▼

generated_content
```

---

# 4. Tabla Sources

```sql
sources
```

---

## Campos

```sql
id BIGSERIAL PK

name VARCHAR(255)

url TEXT

type VARCHAR(50)

priority INTEGER

active BOOLEAN

created_at TIMESTAMPTZ

updated_at TIMESTAMPTZ
```

---

## Índices

```sql
idx_sources_active
idx_sources_priority
```

---

# 5. Tabla News Articles

```sql
news_articles
```

---

## Campos

```sql
id BIGSERIAL PK

source_id BIGINT

title TEXT

url TEXT

summary TEXT

content TEXT

hash VARCHAR(64)

published_at TIMESTAMPTZ

captured_at TIMESTAMPTZ

processing_status VARCHAR(50)

created_at TIMESTAMPTZ

updated_at TIMESTAMPTZ
```

---

## Estados

```text
CAPTURED

CLASSIFIED

EVENT_MATCHED

ARCHIVED
```

---

## Constraints

```sql
UNIQUE(url)

UNIQUE(hash)
```

---

## Índices

```sql
idx_news_status

idx_news_published_at

idx_news_source

idx_news_hash
```

---

# 6. Tabla News Classifications

```sql
news_classifications
```

---

## Campos

```sql
id BIGSERIAL PK

news_id BIGINT

category VARCHAR(100)

subcategory VARCHAR(100)

relevance_score NUMERIC(5,2)

impact_level VARCHAR(50)

urgency_level VARCHAR(50)

keywords JSONB

entities JSONB

classified_at TIMESTAMPTZ
```

---

## Constraint

```sql
UNIQUE(news_id)
```

---

## Índices

```sql
idx_classification_category

idx_classification_relevance
```

---

# 7. Tabla Events

```sql
events
```

---

## Campos

```sql
id BIGSERIAL PK

title VARCHAR(500)

description TEXT

category VARCHAR(100)

status VARCHAR(50)

importance VARCHAR(50)

first_detected_at TIMESTAMPTZ

last_updated_at TIMESTAMPTZ

created_at TIMESTAMPTZ

updated_at TIMESTAMPTZ
```

---

## Estados

```text
OPEN

MONITORING

CLOSED

ARCHIVED
```

---

## Índices

```sql
idx_event_status

idx_event_category

idx_event_importance
```

---

# 8. Tabla Event News

Tabla puente.

```sql
event_news
```

---

## Campos

```sql
id BIGSERIAL PK

event_id BIGINT

news_id BIGINT

confidence_score INTEGER

match_decision VARCHAR(80)

match_reason TEXT

created_at TIMESTAMPTZ
```

---

## Constraint

```sql
UNIQUE(event_id, news_id)
```

---

## Índices

```sql
idx_event_news_event

idx_event_news_news
```

---

# 9. Tabla Event AI Analysis

```sql
event_ai_analysis
```

---

## Campos

```sql
id BIGSERIAL PK

event_id BIGINT

executive_summary TEXT

union_summary TEXT

key_points JSONB

risks JSONB

opportunities JSONB

model_used VARCHAR(100)

generated_at TIMESTAMPTZ
```

---

## Índices

```sql
idx_analysis_event
```

---

# 10. Tabla Generated Content

```sql
generated_content
```

---

## Campos

```sql
id BIGSERIAL PK

event_id BIGINT

created_by BIGINT

channel VARCHAR(50)

tone VARCHAR(50)

title VARCHAR(500)

content TEXT

status VARCHAR(50)

generated_at TIMESTAMPTZ

approved_at TIMESTAMPTZ
```

---

## Estados

```text
GENERATED

PENDING_REVIEW

APPROVED

REJECTED

PUBLISHED
```

---

## Índices

```sql
idx_content_event

idx_content_status

idx_content_channel
```

---

# 11. Tabla Publications

```sql
publications
```

---

## Campos

```sql
id BIGSERIAL PK

content_id BIGINT

channel VARCHAR(50)

external_id VARCHAR(255)

publication_status VARCHAR(50)

published_at TIMESTAMPTZ

response_payload JSONB
```

---

## Estados

```text
PENDING

PUBLISHED

FAILED
```

---

## Índices

```sql
idx_publication_status

idx_publication_channel
```

---

# 12. Tabla Users

```sql
users
```

---

## Campos

```sql
id BIGSERIAL PK

email VARCHAR(255)

password_hash VARCHAR(255)

name VARCHAR(255)

role VARCHAR(50)

active BOOLEAN

created_at TIMESTAMPTZ

updated_at TIMESTAMPTZ
```

---

## Roles

```text
ADMIN

EDITOR
```

---

## Constraints

```sql
UNIQUE(email)
```

---

## Índices

```sql
idx_users_email

idx_users_role
```

---

# 13. Relaciones Oficiales

## Sources → News

```text
1:N
```

---

## News → Classification

```text
1:1
```

---

## News → Events

```text
N:M
```

---

## Events → Analysis

```text
1:N
```

---

## Events → Content

```text
1:N
```

---

## Content → Publications

```text
1:N
```

---

## Users → Content

```text
1:N
```

---

# 14. Estrategia Flyway

## Estructura

```text
src/main/resources/db/migration
```

---

## Convención

```text
V1__initial_schema.sql

V2__add_event_importance.sql

V3__add_content_status.sql
```

---

## Regla

Nunca modificar una migración ejecutada.

Siempre crear una nueva versión.

Excepción de desarrollo:

Cuando la base de datos pueda destruirse por completo y no existan datos productivos, se permite reconsolidar migraciones iniciales para reducir deuda técnica. Esta operación exige resetear la BBDD o el volumen PostgreSQL antes de volver a arrancar Flyway, porque los checksums de `flyway_schema_history` dejan de ser válidos.

Estado consolidado de desarrollo desde 2026-07-20:

- `V1__create_mvp_schema.sql`: esquema final operativo del MVP, tablas, constraints e índices.
- `V2__seed_initial_data.sql`: usuarios bootstrap, fuentes RSS, automatizaciones, Telegram, prompts IA y configuración IA inicial.

---

# 15. Migraciones Iniciales

## V1

```text
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

---

## V2

```text
Índices
```

---

## V3

```text
Datos iniciales
```

---

# 16. Datos Iniciales (Seed)

## Roles

```text
ADMIN

EDITOR
```

---

## Estados de Eventos

```text
OPEN

MONITORING

CLOSED

ARCHIVED
```

---

## Estados de Contenido

```text
GENERATED

PENDING_REVIEW

APPROVED

REJECTED

PUBLISHED
```

---

# 17. Decisiones Arquitectónicas

### DB-001

PostgreSQL como única base de datos.

### DB-002

Flyway obligatorio.

### DB-003

BIGSERIAL como PK.

### DB-004

JSONB para estructuras IA.

### DB-005

Event como Aggregate Root principal.

### DB-006

Todas las relaciones explícitas mediante FK.

### DB-007

Toda modificación del esquema mediante migraciones versionadas.
