## Objetivo

Definir:

- Entidades
- Relaciones
- Cardinalidades
- Claves primarias
- Claves foráneas

Este documento servirá para:

- PostgreSQL
- Spring Boot (JPA)
- Backend API
- Workflows n8n

---

# 1. Diagrama de Alto Nivel

```
┌──────────────┐
│   SOURCES    │
└──────┬───────┘
       │ 1:N
       ▼
┌──────────────┐
│ NEWS_ARTICLES│
└──────┬───────┘
       │ 1:N
       ▼
┌─────────────────────┐
│NEWS_CLASSIFICATIONS │
└─────────────────────┘

       │
       │ N:M
       ▼

┌──────────────┐
│ EVENT_NEWS   │
└──────┬───────┘
       │
       ▼
┌──────────────┐
│   EVENTS     │
└──────┬───────┘
       │
       ├──────────────┐
       │              │
       ▼              ▼
EVENT_AI_ANALYSIS   GENERATED_CONTENT
                          │
                          ▼
                    PUBLICATIONS
```

---

# 2. Módulo de Captura

## sources

### PK

```
id
```

### Relaciones

```
sources 1:N news_articles
```

---

## news_articles

### PK

```
id
```

### FK

```
source_id → sources.id
```

---

### Cardinalidad

```
Una fuentepuede tener muchas noticias
```

```
1:N
```

---

## news_classifications

### PK

```
id
```

### FK

```
news_id → news_articles.id
```

---

### Cardinalidad

```
Una noticiapuede tener varias clasificaciones
```

```
1:N
```

---

# 3. Módulo de Eventos

---

## events

### PK

```
id
```

---

Entidad principal del sistema.

---

## event_news

Tabla puente.

### PK

```
(event_id, news_id)
```

---

### FK

```
event_id → events.id

news_id → news_articles.id
```

---

### Cardinalidad

```
events N:M news_articles
```

---

## event_candidates

### PK

```
id
```

---

### FK

```
news_id → news_articles.id

suggested_event_id → events.id
```

---

### Cardinalidad

```
news_articles 1:N event_candidates

events 1:N event_candidates
```

---

# 4. Módulo IA

---

## event_ai_analysis

### PK

```
id
```

### FK

```
event_id → events.id
```

---

### Cardinalidad

```
events 1:N event_ai_analysis
```

---

Permite conservar histórico.

---

# 5. Módulo Editorial

---

## editorial_profiles

### PK

```
id
```

---

### Cardinalidad

```
editorial_profiles 1:N generated_content
```

---

## channels

### PK

```
id
```

---

Valores iniciales:

```
TELEGRAM
FACEBOOK
X
```

---

## generated_content

### PK

```
id
```

### FK

```
event_id → events.id

profile_id → editorial_profiles.id

channel_id → channels.id

parent_content_id → generated_content.id
```

---

### Cardinalidades

```
events 1:N generated_content

profiles 1:N generated_content

channels 1:N generated_content
```

---

## publications

### PK

```
id
```

### FK

```
content_id → generated_content.id

channel_id → channels.id
```

---

### Cardinalidad

```
generated_content 1:N publications
```

---

# 6. Módulo Taxonomía

---

## categories

### PK

```
id
```

---

Ejemplos:

```
SIPRI
OPOSICIONES
FP
PLANTILLAS
NORMATIVA
```

---

## subcategories

### PK

```
id
```

### FK

```
category_id → categories.id
```

---

### Cardinalidad

```
categories 1:N subcategories
```

---

## tags

### PK

```
id
```

---

## news_tags

### PK

```
(news_id, tag_id)
```

---

### FK

```
news_id → news_articles.id

tag_id → tags.id
```

---

### Cardinalidad

```
news_articles N:M tags
```

---

# 7. Módulo Usuarios

---

## users

### PK

```
id
```

---

### Roles

```
ADMIN
EDITOR
```

---

# 8. Auditoría

---

## audit_logs

### PK

```
id
```

### FK

```
user_id → users.id
```

---

### Cardinalidad

```
users 1:N audit_logs
```

---

## workflow_executions

### PK

```
id
```

---

Registro de ejecución n8n.

---

## event_status_history

### PK

```
id
```

### FK

```
event_id → events.id
```

---

## publication_status_history

### PK

```
id
```

### FK

```
publication_id → publications.id
```

---

# 9. Diagrama ERD Completo (v1.0)

```
SOURCES
   │
   ▼
NEWS_ARTICLES
   │
   ├────► NEWS_CLASSIFICATIONS
   │
   ├────► NEWS_TAGS ◄──── TAGS
   │
   ▼
EVENT_NEWS
   │
   ▼
EVENTS
   │
   ├────► EVENT_CANDIDATES
   │
   ├────► EVENT_AI_ANALYSIS
   │
   ├────► GENERATED_CONTENT
   │              │
   │              ▼
   │         PUBLICATIONS
   │
   ▼
EVENT_STATUS_HISTORY

EDITORIAL_PROFILES
        │
        ▼
GENERATED_CONTENT

CHANNELS
    │
    ▼
GENERATED_CONTENT
    │
    ▼
PUBLICATIONS

USERS
  │
  ▼
AUDIT_LOGS
```

---

# Mejoras que haría antes de crear una sola tabla

Hay tres decisiones que deberíamos cerrar ahora, porque afectan muchísimo a PostgreSQL y a los workflows:

### Decisión DBD-001

¿Vamos a guardar el contenido completo de la noticia (`content`) o solamente:

```
titlesummaryurl
```

Guardar el contenido completo mejora muchísimo la IA y el clustering.

Yo recomiendo **guardar el contenido completo**.

---

### Decisión DBD-002

¿Vamos a almacenar también las imágenes destacadas de las noticias?

Yo recomiendo:

```
featured_image_url
```

desde el primer día.

---

### Decisión DBD-003

¿Vamos a almacenar los prompts y respuestas completas de IA para auditoría?

Yo recomiendo que sí.

Añadiríamos una tabla:

```
ai_executions
```

que será fundamental para depurar prompts, costes y calidad de resultados.