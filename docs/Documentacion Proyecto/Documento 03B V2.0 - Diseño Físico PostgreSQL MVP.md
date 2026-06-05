
Versión: 2.0

Estado: OFICIAL

---

## Tablas MVP

sources

news_articles

news_classifications

events

event_news

event_ai_analysis

generated_content

publications

users

---

## Índices Obligatorios

news_articles.url

news_articles.hash

news_articles.published_at

news_articles.processing_status

events.status

events.category

generated_content.status

publications.publication_status

---

## Constraints

UNIQUE(url)

UNIQUE(event_id, news_id)

UNIQUE(email)

---

## Motor

PostgreSQL 16+

---

## Estrategia de Migraciones

Flyway

---

## Estrategia de Borrado

Soft Delete:

NO

---

## Estrategia de Auditoría

Fase MVP:

No implementada

Versión futura:

audit_log