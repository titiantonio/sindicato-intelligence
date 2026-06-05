## Plataforma de Inteligencia Informativa para Sindicato de Docentes de Andalucía

Versión: 1.0

Estado: DEFINITIVO MVP

---

# 1. Principios de Diseño

## MDP-001

Las noticias son datos de entrada.

---

## MDP-002

Los eventos son el núcleo del negocio.

---

## MDP-003

Todo contenido se genera desde eventos.

---

## MDP-004

Toda acción importante debe ser auditable.

---

## MDP-005

La IA debe ser trazable.

---

## MDP-006

La publicación debe ser reproducible.

---

# 2. Mapa General

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
    ├──────────────┐
    ▼              ▼
event_ai_analysis  event_relationships
    │
    ▼
generated_content
    │
    ▼
content_versions
    │
    ▼
publications

users
audit_log

ai_requests

source_errors
```

---

# 3. Módulo Captura

---

## sources

Fuentes monitorizadas.

```
sources
```

Campos:

```
id
name
source_type
url
category
priority
is_active
created_at
updated_at
```

---

## source_errors

Errores de captura.

```
source_errors
```

Campos:

```
id
source_id
error_message
error_date
resolved
resolved_at
```

---

# 4. Módulo Noticias

---

## news_articles

Materia prima.

Campos:

```
id
source_id

title
url

summary
content

author
image_url

published_at
captured_at

processing_status

language

hash

created_at
updated_at
```

---

### Nuevo Campo

```
hash
```

Permitirá detectar duplicados incluso si cambia la URL.

---

## Estados

```
CAPTURED

CLASSIFIED

EVENT_MATCHED

ANALYZED

CONTENT_GENERATED

PUBLISHED
```

---

## Índices

```
url UNIQUE

hash INDEX

published_at INDEX

processing_status INDEX
```

---

# 5. Clasificación IA

---

## news_classifications

Campos:

```
id

news_id

category
subcategory

relevance_score
urgency_score
impact_score

keywords JSONB

entities JSONB

ai_model

created_at
```

---

## Entities JSON

Ejemplo:

```
{
  "organizations": [],
  "locations": [],
  "people": [],
  "regulations": []
}
```

---

# 6. Módulo Eventos

---

# events

Entidad principal.

---

Campos:

```
id

title

canonical_description

category

importance

status

confidence_score

first_detected_at

last_activity_at

created_at
updated_at
```

---

## Importance

```
LOW

MEDIUM

HIGH

CRITICAL
```

---

## Status

```
OPEN

MONITORING

CLOSED

ARCHIVED
```

---

# event_news

Relación N:M.

---

Campos:

```
OPEN

MONITORING

CLOSED

ARCHIVED
```

---

Restricción:

```
UNIQUE(event_id, news_id)
```

---

# event_relationships

Nueva tabla.

---

Objetivo:

Relacionar eventos.

---

Ejemplo:

```
Oposiciones 2027
      │
      ├─ Calendario
      ├─ Tribunales
      └─ Baremo
```

---

Campos:

```
id

parent_event_id

child_event_id

relationship_type

created_at
```

---

Tipos:

```
RELATED

PARENT

CHILD

FOLLOW_UP
```

---

# 7. Módulo IA

---

## event_ai_analysis

Análisis consolidado.

---

Campos:

```
id

event_id

executive_summary

union_summary

key_points JSONB

risks JSONB

opportunities JSONB

sources_used JSONB

ai_model

generated_at
```

---

### Nuevo Campo

```
sources_used
```

Ejemplo:

```
[
  "BOJA",
  "Consejería",
  "ANPE"
]
```

---

# ai_requests

Trazabilidad IA.

---

Campos:

```
id

operation_type

entity_type

entity_id

prompt

response

model

tokens_input

tokens_output

execution_time_ms

created_at
```

---

## operation_type

```
CLASSIFICATION

EVENT_MATCHING

SUMMARY

CONTENT_GENERATION
```

---

# 8. Módulo Editorial

---

# generated_content

Contenido actual.

---

Campos:

```
id

event_id

channel

editorial_profile

title

content

status

current_version

generated_by_ai

created_at
updated_at
```

---

## channel

```
TELEGRAM

FACEBOOK

X
```

---

## editorial_profile

```
INFORMATIONAL

INSTITUTIONAL

DISCLOSURE
```

---

## status

```
GENERATED

PENDING_REVIEW

APPROVED

REJECTED

PUBLISHED
```

---

# content_versions

Nueva tabla.

---

Objetivo:

Versionado editorial.

---

Campos:

```
id

content_id

version_number

title

content

created_by

change_reason

created_at
```

---

Ejemplo:

```
v1 IA

v2 Editor

v3 Editor
```

---

# 9. Módulo Publicación

---

# publications

---

Campos:

```
id

content_id

channel

publication_status

external_id

response_payload

published_at

created_at
```

---

## publication_status

```
PENDING

SCHEDULED

PUBLISHED

FAILED
```

---

## external_id

Ejemplos:

```
Telegram Message ID

Facebook Post ID

Tweet ID
```

---

# 10. Administración

---

# users

Campos:

```
id

name

email

password_hash

role

is_active

last_login

created_at
updated_at
```

---

## Roles

```
ADMIN

EDITOR
```

---

# audit_log

---

Campos:

```
id

user_id

action

entity_type

entity_id

old_values

new_values

created_at
```

---

# Acciones auditables

```
LOGIN

CONTENT_APPROVED

CONTENT_REJECTED

CONTENT_EDITED

PUBLICATION_CREATED

PUBLICATION_SENT

EVENT_MERGED
```

---

# 11. Tablas Futuras (NO MVP)

No se implementan aún.

---

## notifications

Alertas internas.

---

## comments

Colaboración editorial.

---

## tags

Etiquetas avanzadas.

---

## event_metrics

Analítica avanzada.

---

## trend_analysis

Detección de tendencias.

---

# 12. Cardinalidades Finales

```
Source
 1 ────── N News

News
 1 ────── 1 Classification

News
 N ────── N Events

Event
 1 ────── N Analysis

Event
 1 ────── N Content

Content
 1 ────── N Versions

Content
 1 ────── N Publications

User
 1 ────── N AuditLog
```

---

# 13. Decisión Arquitectónica Final

Si hoy empezáramos el desarrollo desde cero:

### Este sería el orden correcto

```
03D Modelo Datos Final
↓
ERD Final
↓
Flyway Scripts
↓
Spring Boot Entities
↓
Repositorios
↓
Casos de Uso
↓
API REST
↓
Angular
```