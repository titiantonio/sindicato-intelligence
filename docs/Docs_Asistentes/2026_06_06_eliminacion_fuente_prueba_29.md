# Eliminacion fuente prueba 29

## Fecha

2026-06-06

## Objetivo

Eliminar de la base de datos la fuente de prueba con `id = 29`.

## Contexto

El usuario solicito eliminar la fuente `id = 29`. La API actual del modulo `source` no expone un endpoint `DELETE`, por lo que la operacion se realizo directamente sobre PostgreSQL.

## Fase MVP

Documento 30, Fase 5: `WF-01 Captura Noticias`.

## Datos eliminados

- Fuente `id = 29`, nombre `BOJA`, URL `https://www.juntadeandalucia.es/boja`.
- 4 noticias asociadas en `news_articles`.

## Decisiones

- El primer intento de borrado fallo por la foreign key `fk_news_articles_source`.
- Se consultaron las dependencias directas antes de borrar.
- No habia registros asociados en `news_classifications` ni `event_news`.
- Tras confirmacion del usuario, se eliminaron primero las noticias dependientes y despues la fuente en una transaccion.

## Verificaciones

- Antes del borrado: `news_articles = 4`, `sources = 1`, `news_classifications = 0`, `event_news = 0`.
- Operacion ejecutada en transaccion con `ON_ERROR_STOP=1`.
- Verificacion posterior: `sources` con `id = 29` queda en `0` y `news_articles` con `source_id = 29` queda en `0`.

## Archivos modificados

- `docs/Docs_Asistentes/2026_06_06_eliminacion_fuente_prueba_29.md`.

## Pruebas

- No se ejecutaron tests backend porque no hubo cambios en codigo ni migraciones.
