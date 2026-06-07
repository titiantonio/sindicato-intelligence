# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.0.17-SNAPSHOT] - 2026-06-07

### Added

- Implementado `GeminiAIProvider` como proveedor real externo para `AIProvider`, activable con `app.ai.provider=gemini` y modelo por defecto `models/gemma-4-31b-it`.
- Añadida configuracion tecnica `app.ai` con seleccion de proveedor, API key, modelo, temperatura y limite de tokens mediante `application.yml` o variables de entorno.
- Añadida `AIProviderException` y respuesta HTTP `502 Bad Gateway` para fallos claros del proveedor IA externo sin fallback silencioso.
- Añadidos tests de parseo de respuesta Gemini, errores de configuracion/respuesta y seleccion de proveedor IA por propiedades.

### Changed

- `DeterministicAIProvider` queda activo por defecto solo cuando `app.ai.provider=deterministic` o no se configura proveedor.
- Versionado del backend actualizado a `0.0.17-SNAPSHOT`.

## [0.0.16-SNAPSHOT] - 2026-06-07

### Added

- Añadida la migracion Flyway `V3__seed_rss_sources.sql` con las 54 fuentes RSS iniciales revisadas para pruebas de `WF-01-Capture-News`.

### Changed

- Consolidadas las migraciones Flyway iniciales en `V1__create_mvp_schema.sql`, `V2__seed_admin_user.sql` y `V3__seed_rss_sources.sql` para reconstruir el esquema MVP desde cero en desarrollo.
- Añadida la constraint `uk_sources_url` en la creacion inicial de `sources` para impedir URLs de fuentes duplicadas.
- Integrado `event_news.confidence_score` y su check `0..100` en la creacion inicial de `event_news`, eliminando la necesidad de una migracion correctiva posterior.
- Eliminada la tabla tecnica `system_info` de la migracion inicial al no formar parte del modelo MVP ni estar usada por el codigo.
- Versionado del backend actualizado a `0.0.16-SNAPSHOT`.

## [0.0.15-SNAPSHOT] - 2026-06-06

### Added

- T6.1: creada la estructura inicial del modulo `event` con capas `domain`, `application`, `infrastructure` y `api`.
- T6.2: creada la entidad de dominio `Event` con estados, importancia, categorias oficiales e invariantes basicas del agregado.
- T6.3: creado el puerto de dominio `EventRepository` para persistencia y busqueda de eventos por estado, categoria e importancia.
- T6.4: convertido `Event` en aggregate root operativo con asociacion de noticias, cambios de estado y bloqueo de nuevas noticias en eventos cerrados o archivados.
- T6.5: creado el workflow n8n exportable `WF-03-Detect-Events` para procesar noticias clasificadas y delegar la deteccion de eventos en Spring Boot.
- T6.6: integrada la agrupacion IA de eventos con puerto `EventMatchingAIProvider`, prompt oficial WF-03 y proveedor determinista para el MVP tecnico.
- T6.7: implementada la asociacion noticia-evento con persistencia JPA en `events` y `event_news`, caso de uso `DetectEventUseCase` y endpoint `POST /api/v1/events/detect`.
- Sprint 6: añadida la migracion Flyway `V4__add_event_news_confidence_score.sql` para registrar `confidence_score` en `event_news` y cumplir la trazabilidad de asociaciones IA de WF-03.
- Ajustados los workflows n8n `WF-01`, `WF-02` y `WF-03` para usar `http://host.docker.internal:8080` en desarrollo cuando n8n corre en Docker y el backend en la maquina anfitriona.
- Ajustado `WF-01-Capture-News` para descargar RSS/Atom con `HTTP Request` como texto, parsear XML y normalizar Atom Junta Andalucia y RSS estandar antes de llamar a `POST /api/v1/news/bulk`.
- Sprint 6 completado y versionado del backend a `0.0.15-SNAPSHOT`.

### Fixed

- Corregido `WF-01-Capture-News` para usar `$input.all()` en los Code nodes y evitar el error de n8n `Cannot find name 'items'`.
- Corregido `Normalize RSS Items` en `WF-01-Capture-News` para detectar estructuras XML parseadas por n8n con envoltorios `data`, `root`, `body`, `feed`, `rss` o `channel`.
- Corregidos `WF-02-Classify-News` y `WF-03-Detect-Events` para usar `$input.all()` en los Code nodes de filtrado y evitar el error de n8n `Cannot find name 'items'`.

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
- Ajuste Sprint 4: creado el workflow n8n exportable `WF-01-Capture-News` para leer fuentes RSS activas y enviar lotes de noticias a `POST /api/v1/news/bulk`.
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
