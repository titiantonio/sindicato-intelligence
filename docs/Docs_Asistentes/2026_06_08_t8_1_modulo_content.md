# T8.1 modulo content

## Fecha

2026-06-08

## Objetivo

Crear la estructura inicial del modulo `content` para el Sprint 8 de Contenido.

## Contexto

- Fase MVP: Fase 9, Contenido.
- Sprint: Sprint 8.
- Tarea Documento 31: T8.1 Crear modulo content.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/content/domain/ContentStatus.java`
- `backend/src/main/java/es/sindicato/intelligence/content/domain/GeneratedContent.java`
- `backend/src/main/java/es/sindicato/intelligence/content/domain/GeneratedContentRepository.java`
- `backend/src/main/java/es/sindicato/intelligence/content/domain/package-info.java`
- `backend/src/main/java/es/sindicato/intelligence/content/application/package-info.java`
- `backend/src/main/java/es/sindicato/intelligence/content/infrastructure/package-info.java`
- `backend/src/main/java/es/sindicato/intelligence/content/api/package-info.java`
- `backend/src/test/java/es/sindicato/intelligence/content/domain/GeneratedContentTest.java`
- `CHANGELOG.md`

## Decisiones tomadas

- Mantener dominio puro sin dependencias de Spring, JPA ni HTTP.
- Modelar `GeneratedContent` con los campos oficiales de `generated_content` definidos en el Documento 20.
- Incorporar reglas de estado basicas para aprobacion, rechazo y publicacion.
- Crear el puerto de repositorio en dominio, dejando JPA para la tarea T8.4.

## Pruebas o verificaciones

- Ejecutado `mvn "-Dtest=GeneratedContentTest" test` desde `backend`.
- Resultado: `Tests run: 6, Failures: 0, Errors: 0, Skipped: 0`.
- Resultado Maven: `BUILD SUCCESS`.
