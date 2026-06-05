---
name: sindicato-flyway-modelo-datos
description: Usar para crear o revisar migraciones Flyway, PostgreSQL, tablas, indices, constraints, BIGSERIAL, TIMESTAMPTZ y coherencia con el modelo fisico MVP. Activa esta skill ante cualquier cambio SQL, db/migration o modelo de datos.
---

# Sindicato Flyway Modelo Datos

## Proposito

Asegura que los cambios de base de datos respeten PostgreSQL, Flyway y el modelo fisico oficial del MVP.

## Documentacion a revisar

- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 20 – ERD Final MVP + Estrategia Flyway.md`.
- `docs/Documentacion Proyecto/Documento 03D - Modelo de Datos Producción v1.0.md`.
- `docs/Documentacion Proyecto/Documento 21 – Convenciones de Desarrollo.md`.

## Reglas

- Todo cambio de esquema se hace con Flyway.
- No modificar migraciones ya ejecutadas.
- Migraciones en `db/migration`.
- Tablas y columnas en `snake_case`.
- Claves primarias con `BIGSERIAL` segun modelo MVP.
- Fechas con `TIMESTAMP WITH TIME ZONE`.

## Tablas MVP

`sources`, `news_articles`, `news_classifications`, `events`, `event_news`, `event_ai_analysis`, `generated_content`, `publications`, `users`.

## Checklist

- Constraints e indices coherentes con Documento 20.
- Relaciones respetan que `Event` es aggregate root principal.
- Cambios de codigo asociados actualizan version, changelog y documentacion.
