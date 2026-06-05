## Plataforma de Inteligencia Informativa para Sindicato de Docentes de Andalucía

Versión 1.0

---

# 1. Objetivos del Modelo de Datos

El modelo deberá permitir:

### Capturar noticias

Desde múltiples fuentes.

---

### Agrupar noticias

En eventos.

---

### Procesar IA

Clasificación y análisis.

---

### Generar contenido

Para múltiples canales.

---

### Gestionar publicaciones

Con trazabilidad completa.

---

### Mantener histórico

Para futuras analíticas.

---

# 2. Principios de Diseño

## MD-001

PostgreSQL como única fuente de verdad.

---

## MD-002

Arquitectura Event-Centric.

---

## MD-003

Trazabilidad completa.

---

## MD-004

Auditoría de acciones críticas.

---

## MD-005

Preparado para IA futura.

---

# 3. Entidades Principales

## Fuentes

Origen de información.

---

## Noticias

Información capturada.

---

## Eventos

Acontecimiento detectado.

---

## Clasificaciones IA

Resultado de IA.

---

## Contenido Generado

Textos creados.

---

## Publicaciones

Distribución.

---

## Usuarios

Gestión sistema.

---

# 4. Modelo Conceptual

```
SOURCE
   │
   ▼
NEWS_ARTICLE
   │
   ▼
EVENT
   │
   ├────► EVENT_SUMMARY
   │
   ├────► GENERATED_CONTENT
   │
   └────► PUBLICATION
```

---

# 5. Tablas MVP

---

# 5.1 sources

Catálogo de fuentes.

```
sources
-------
id
name
type
url
active
priority
credibility_score
created_at
updated_at
```

---

## type

Valores:

```
RSS
GOOGLE_NEWS
BOJA
BOE
SINDICATO
PRENSA
CONSEJERIA
```

---

# 5.2 news_articles

Noticias capturadas.

```
news_articles
-------------
id
source_id
title
url
summary
content
author
published_at
captured_at
status
language
hash
created_at
updated_at
```

---

## status

```
RAW
CLASSIFIED
CLUSTERED
ARCHIVED
ERROR
```

---

## Índices

```
UNIQUE(url)

INDEX(source_id)

INDEX(published_at)

INDEX(status)
```

---

# 5.3 news_classifications

Resultado IA.

Separado para poder reclasificar.

```
news_classifications
--------------------
id
news_id
category
subcategory
relevance_score
impact_score
urgency_score
confidence_score
keywords
organizations
created_at
```

---

## category

Ejemplos:

```
SIPRI
OPOSICIONES
FP
PLANTILLAS
RETRIBUCIONES
NORMATIVA
```

---

# 5.4 events

Tabla principal del sistema.

```
events
------
id
title
description
category
status
importance_score
urgency_score
impact_score
first_detected_at
last_updated_at
created_at
updated_at
```

---

## status

```
DETECTED
ENRICHED
REVIEW_PENDING
PUBLISHED
CLOSED
```

---

# 5.5 event_news

Relación N:M

Porque un evento tiene muchas noticias.

```
event_news
----------
event_id
news_id
created_at
```

---

# 5.6 event_summaries

Resúmenes generados.

```
event_summaries
---------------
id
event_id
executive_summary
union_summary
key_points
generated_by
created_at
```

---

## key_points

Tipo:

```
JSONB
```

Ejemplo:

```
[
  "350 plazas publicadas",
  "Afecta a Secundaria",
  "Incorporación inmediata"
]
```

---

# 5.7 editorial_profiles

Perfiles de tono.

```
editorial_profiles
------------------
id
name
description
active
created_at
```

---

## Valores iniciales

```
INFORMATIVO
INSTITUCIONAL
DIVULGATIVO
REIVINDICATIVO
MOVILIZADOR
```

---

# 5.8 generated_content

Contenido generado por IA.

```
generated_content
-----------------
id
event_id
profile_id
channel
title
content
version
status
created_at
updated_at
```

---

## channel

```
TELEGRAM
FACEBOOKX
```

---

## status

```
DRAFT
REVIEW_PENDING
APPROVED
REJECTED
```

---

# 5.9 publications

Publicaciones finales.

```
publications
------------
id
content_id
channel
status
scheduled_at
published_at
external_id
response
created_at
```

---

## status

```
PENDING
SCHEDULED
PUBLISHED
ERROR
```

---

# 5.10 users

Usuarios.

```
users
-----
id
name
email
password_hash
role
active
last_login
created_at
updated_at
```

---

## role

```
ADMIN
EDITOR
```

---

# 5.11 workflow_executions

Auditoría de n8n.

```
workflow_executions
-------------------
id
workflow_name
status
started_at
finished_at
items_processed
error_message
```

---

# 5.12 audit_logs

Auditoría general.

```
audit_logs
----------
id
user_id
entity_type
entity_id
action
old_value
new_value
created_at
```

---

# 6. Relaciones

## Fuente → Noticias

```
1:N
```

---

## Noticia → Clasificación

```
1:N
```

Permite reclasificar.

---

## Evento → Noticias

```
N:M
```

---

## Evento → Resumen

```
1:N
```

---

## Evento → Contenido

```
1:N
```

---

## Contenido → Publicación

```
1:N
```

---

# 7. Campos JSONB

Aprovecharemos PostgreSQL.

---

## keywords

```
[
  "sipri",
  "vacantes",
  "secundaria"
]
```

---

## organizations

```
[
  "Consejería",
  "CSIF",
  "ANPE"
]
```

---

## key_points

```
[
  "Punto 1",
  "Punto 2"
]
```

---

# 8. Estrategia de Clustering

Para el MVP.

---

## events

Entidad principal.

---

## event_news

Tabla puente.

---

La IA decidirá:

```
¿Esta noticia pertenecea un evento existente?
```

---

Si:

```
Sí
```

se asocia.

---

Si:

```
No
```

se crea evento nuevo.

---

# 9. Índices Críticos

```
news_articles(url)

news_articles(status)

events(status)

events(category)

generated_content(status)

publications(status)
```

---

# 10. Tablas Futuras (NO MVP)

Estas NO se implementarán inicialmente.

---

## trends

Tendencias.

---

## alerts

Alertas.

---

## embeddings

Búsquedas semánticas.

---

## organization_mentions

Menciones.

---

## topic_statistics

Estadísticas.

---

# 11. Modelo Físico MVP

Tablas iniciales:

```
sources

news_articles

news_classifications

events

event_news

event_summaries

editorial_profiles

generated_content

publications

users

workflow_executions

audit_logs
```