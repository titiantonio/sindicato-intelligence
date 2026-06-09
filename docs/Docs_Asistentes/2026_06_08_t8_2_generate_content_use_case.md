# T8.2 GenerateContentUseCase

## Fecha

2026-06-08

## Objetivo

Implementar el caso de uso `GenerateContentUseCase` para generar contenido editorial desde un evento y su analisis IA.

## Contexto

- Fase MVP: Fase 9, Contenido.
- Sprint: Sprint 8.
- Tarea Documento 31: T8.2 Implementar GenerateContentUseCase.
- `generated_content.created_by` es obligatorio y referencia `users`.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/content/application/ContentAIProvider.java`
- `backend/src/main/java/es/sindicato/intelligence/content/application/ContentAIRequest.java`
- `backend/src/main/java/es/sindicato/intelligence/content/application/ContentAIResponse.java`
- `backend/src/main/java/es/sindicato/intelligence/content/application/CurrentContentAuthorProvider.java`
- `backend/src/main/java/es/sindicato/intelligence/content/application/GenerateContentCommand.java`
- `backend/src/main/java/es/sindicato/intelligence/content/application/GenerateContentUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/content/infrastructure/TransientCurrentContentAuthorProvider.java`
- `backend/src/main/resources/application.yml`
- `backend/src/test/java/es/sindicato/intelligence/content/application/GenerateContentUseCaseTest.java`

## Decisiones tomadas

- Crear un puerto `ContentAIProvider` especifico para generacion de contenido editorial.
- El caso de uso carga el evento y el analisis IA desde repositorios de dominio.
- Si no se indica `analysisId`, se usa el analisis mas reciente del evento segun el repositorio.
- Crear `CurrentContentAuthorProvider` para no exponer `createdBy` como parametro del contrato publico.
- Implementar `TransientCurrentContentAuthorProvider` como solucion transitoria usando `app.content.default-created-by`, por defecto `1`, hasta que exista modulo `user` o JWT real.
- Cuando exista JWT, se debera reemplazar esta implementacion para obtener el usuario desde `SecurityContext`, manteniendo estable el caso de uso y evitando que el cliente envie `createdBy`.
- Incorporar logs operativos de inicio, contexto, exito, ausencia de analisis, analisis de otro evento y errores IA.

## Pruebas o verificaciones

- Ejecutado `mvn "-Dtest=GeneratedContentTest,GenerateContentUseCaseTest" test` desde `backend`.
- Resultado: `Tests run: 10, Failures: 0, Errors: 0, Skipped: 0`.
- Resultado Maven: `BUILD SUCCESS`.
