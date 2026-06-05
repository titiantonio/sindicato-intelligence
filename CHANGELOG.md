# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
