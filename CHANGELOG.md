# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.0.14-SNAPSHOT] - 2026-06-06

### Added

- T5.1: creada la estructura inicial del modulo `classification` con capas `domain`, `application`, `infrastructure` y `api`.
- T5.2: creada la entidad de dominio `NewsClassification` con taxonomia oficial, niveles de impacto/urgencia y pruebas unitarias.
- T5.3: creado el puerto de dominio `NewsClassificationRepository` para persistencia y consultas por `newsId`.
- T5.4: integrada la interfaz `AIProvider` con un adaptador determinista para clasificacion IA en MVP tecnico.
- T5.5: implementado `ClassifyNewsPromptBuilder` con el prompt oficial WF-02 del Documento 23 y pruebas unitarias.
- T5.6: creado el workflow n8n exportable `WF-02-Classify-News` para leer noticias capturadas y llamar a la API de clasificacion.
- T5.7: implementada persistencia de clasificaciones con JPA, `ClassifyNewsUseCase` y endpoint `POST /api/v1/classifications/classify`.
- T5.8: actualizado `ClassifyNewsUseCase` para marcar la noticia como `CLASSIFIED` tras guardar la clasificacion.
- Sprint 5 completado y versionado del backend a `0.0.14-SNAPSHOT`.

## [0.0.13-SNAPSHOT] - 2026-06-06

### Added

- T4.1: creado `IngestNewsBatchUseCase` con procesamiento parcial por item para alinear WF-01 con el flujo `n8n -> API -> Spring Boot -> PostgreSQL`.
- T4.2: creado el endpoint de ingestión masiva `POST /api/v1/news/bulk` con respuesta de resumen por lote.
- T4.3: implementada la normalizacion RSS en backend mediante `NewsCaptureNormalizer` antes de persistir cada item del lote.
- T4.4: implementada deteccion de duplicados por `url` y `hash` dentro del mismo lote, ademas de la validacion existente contra base de datos.
- T4.5: añadidas pruebas de integracion para `POST /api/v1/news/bulk` cubriendo lote parcial, duplicados y request vacio.
- Sprint 4 completado y versionado del backend a `0.0.13-SNAPSHOT`.

## [0.0.12-SNAPSHOT] - 2026-06-06

### Added

- T3.1: creada la estructura inicial del modulo `news` con capas `domain`, `application`, `infrastructure` y `api`.
- T3.2: creada la entidad de dominio `NewsArticle` con `NewsStatus` y pruebas unitarias.
- T3.3: creado el puerto de dominio `NewsRepository` para persistencia y consultas de noticias.
- T3.4: creada la entidad JPA `NewsArticleEntity` mapeada a `news_articles` con pruebas de mapeo.
- T3.5: implementado `JpaNewsRepository` con persistencia, consultas por `id`, `url`, `hash` y listado de noticias.
- T3.6: creados los DTOs `CreateNewsRequest` y `NewsResponse` con validaciones de entrada y pruebas.
- T3.7: creado `CreateNewsUseCase` con validacion de fuente, deteccion de duplicados por URL/hash y calculo interno SHA-256.
- T3.8: creados `GetNewsUseCase`, `ListNewsUseCase` y `NewsNotFoundException` con pruebas unitarias.
- T3.9: creada la API REST del modulo `news` con endpoints de creacion, listado y detalle.
- T3.10: verificado el Sprint 3 completo con 52 tests de backend sin fallos y versionado a `0.0.12-SNAPSHOT`.

## [0.0.11-SNAPSHOT] - 2026-06-06

### Added

- Implementada la API REST del modulo `source` con endpoints `GET /api/v1/sources`, `POST /api/v1/sources` y `PUT /api/v1/sources/{id}`, junto con pruebas de integracion REST.

## [0.0.10-SNAPSHOT] - 2026-06-06

### Added

- Creado `CreateSourceUseCase` con `CreateSourceCommand` para registrar fuentes desde la capa de aplicacion, evitando URLs duplicadas.

## [0.0.9-SNAPSHOT] - 2026-06-06

### Added

- Creados los DTOs `CreateSourceRequest` y `SourceResponse` para la API del modulo `source`, con validaciones de entrada y pruebas unitarias.

## [0.0.8-SNAPSHOT] - 2026-06-06

### Added

- Implementado `JpaSourceRepository` como adaptador JPA del puerto de dominio `SourceRepository`, con pruebas de integracion de persistencia.

## [0.0.7-SNAPSHOT] - 2026-06-06

### Added

- Creada la entidad JPA `SourceEntity` mapeada a la tabla `sources`, con pruebas de mapeo basico.

## [0.0.6-SNAPSHOT] - 2026-06-06

### Added

- Creada la interfaz de dominio `SourceRepository` como puerto de persistencia del modulo `source`.

## [0.0.5-SNAPSHOT] - 2026-06-06

### Added

- Creada la entidad de dominio `Source` con campos de auditoria `createdAt` y `updatedAt`, comportamiento de activacion/desactivacion y pruebas unitarias.

## [0.0.4-SNAPSHOT] - 2026-06-05

### Added

- Creada la estructura inicial del modulo `source` con capas `domain`, `application`, `infrastructure` y `api`.

## [0.0.3-SNAPSHOT] - 2026-06-05

### Added

- Creada la migracion Flyway `V3__seed_data.sql` con el usuario inicial `ADMIN` en la tabla `users`.

## [0.0.2-SNAPSHOT] - 2026-06-05

### Added

- Creada la migracion Flyway `V2__create_mvp_schema.sql` con el esquema completo MVP: fuentes, noticias, clasificaciones, eventos, analisis IA, contenido generado, publicaciones y usuarios.
