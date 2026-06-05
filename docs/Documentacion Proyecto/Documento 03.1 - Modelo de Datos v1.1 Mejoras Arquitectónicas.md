## Mejoras Arquitectónicas

---

# Mejora 1

## Separar Captura y Procesamiento

Actualmente tenemos:

```
news_articles
```

Pero realmente una noticia pasa por varios estados.

Recomiendo añadir:

```
processing_status
```

Valores:

```
CAPTURED
CLASSIFIED
EVENT_MATCHED
CONTENT_GENERATED
READY_FOR_REVIEW
PUBLISHED
ERROR
```

---

## Ventaja

Los workflows n8n podrán buscar fácilmente:

```
WHERE processing_status='CAPTURED'
```

sin lógica compleja.

---

# Mejora 2

## Tabla event_candidates

Esta tabla va a ser clave.

---

### Problema

Supongamos:

Noticia A

```
La Junta publica nuevas vacantes SIPRI
```

Noticia B

```
350 plazas para interinos en Andalucía
```

La IA cree que son el mismo evento.

Pero no está completamente segura.

---

### Solución

Tabla intermedia:

```
event_candidates
----------------
id
news_id
suggested_event_id
confidence_score
reason
status
created_at
```

---

## status

```
PENDING
ACCEPTED
REJECTED
```

---

### Beneficio

Podremos auditar decisiones IA.

---

# Mejora 3

## Tabla event_ai_analysis

Actualmente:

```
event_summaries
```

mezcla demasiadas responsabilidades.

---

Separaría:

```
event_ai_analysis
```

```
id
event_id
executive_summary
union_summary
key_points
opportunities
risks
created_at
```

---

## Ventaja

Todo el análisis IA queda agrupado.

---

# Mejora 4

## Versionado Real de Contenido

Actualmente:

```
generated_content
```

tiene un campo version.

---

Yo añadiría:

```
parent_content_id
```

---

Ejemplo

```
Versión 1
↓
Versión 2
↓
Versión 3
```

---

Permite:

- regenerar
- comparar
- volver atrás

---

# Mejora 5

## Catálogo de Categorías

No guardaría:

```
SIPRI
OPOSICIONES
FP
```

como texto libre.

---

Crearía:

```
categories
----------
id
name
description
active
```

---

Y:

```
subcategories
-------------
id
category_id
name
```

---

## Ventaja

La IA clasificará usando IDs.

---

# Mejora 6

## Tabla de Etiquetas

En lugar de guardar keywords como JSON.

---

Crearía:

```
tags
----
id
name
```

---

Y:

```
news_tags
---------
news_id
tag_id
```

---

### Beneficio

Búsquedas más rápidas.

---

# Mejora 7

## Historial de Estados

Muy importante.

---

Añadir:

```
event_status_history
```

```
id
event_id
old_status
new_status
changed_by
changed_at
```

---

Y:

```
publication_status_history
```

---

## Beneficio

Auditoría completa.

---

# Mejora 8

## Canales de Publicación

No hardcodear:

```
Telegram
Facebook
X
```

---

Crear:

```
channels
---------
id
name
active
```

---

Inicialmente:

```
TELEGRAM
FACEBOOK
X
```

---

En futuro:

```
INSTAGRAM
LINKEDIN
WHATSAPP
WEB
```

---

# Mejora 9

## Configuración IA

Muy recomendable.

```
ai_models
----------
id
provider
model_name
active
created_at
```

---

Ejemplo:

```
OPENAI
gpt-5
```

---

Mañana:

```
OLLAMA
llama3
```

---

Sin tocar código.

---

# Mejora 10

## Preparar Embeddings

Aunque no lo usemos todavía.

Crear ya:

```
embeddings
-----------
id
entity_type
entity_id
provider
vector_hash
created_at
```

---

No almacenamos vectores aún.

Pero dejamos preparado el modelo.

---

# Modelo Final MVP Recomendado

## Núcleo

```
sources

news_articles

news_classifications

events

event_news

event_candidates

event_ai_analysis
```

---

## Editorial

```
editorial_profiles

generated_content

publications

channels
```

---

## Administración

```
users

categories

subcategories

tags

news_tags
```

---

## Auditoría

```
workflow_executions

audit_logs

event_status_history

publication_status_history
```

---

## Futuro

```
ai_models

embeddings

trends

alerts
```