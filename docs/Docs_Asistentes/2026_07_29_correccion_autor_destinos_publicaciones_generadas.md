# Correccion de autor y destinos en publicaciones generadas

## Fecha

2026-07-29.

## Objetivo

Corregir el detalle de publicaciones no manuales para mostrar el autor y los destinos reales del envio.

## Contexto

Intervencion de mantenimiento correctivo sobre las fases 10 y 11 del MVP, correspondiente a `T11.8 - Publicaciones` del Documento 31. Las publicaciones manuales ya persistian autor y destinos, mientras que las publicaciones generadas conservaban el autor en el contenido original y los destinos en el snapshot de respuesta de Telegram.

## Fase MVP

- Fase 10: Publicacion.
- Fase 11: Frontend Angular.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/publication/api/PublicationController.java`.
- `backend/src/test/java/es/sindicato/intelligence/publication/api/PublicationControllerTest.java`.
- `backend/pom.xml`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Docs_Asistentes/2026_07_29_correccion_autor_destinos_publicaciones_generadas.md`.

## Decisiones

- El detalle usa `generated_content.created_by` como autor de respaldo solo para publicaciones generadas sin `requested_by`.
- Los destinos persistidos en `publication_targets` mantienen prioridad.
- Si no existen destinos persistidos, se leen los snapshots `destinationId`, `destinationName` y `messageId` de `response_payload.targets`.
- No se usa la configuracion Telegram actual como respaldo, porque podria no coincidir con los destinos historicos del envio.
- No se modifica el esquema de PostgreSQL ni las migraciones Flyway.

## Pruebas o verificaciones

- `.\mvnw.cmd -DskipTests compile`: OK.
- `.\mvnw.cmd "-Dtest=PublicationControllerTest" test`: OK, 7 pruebas, 0 fallos y 0 errores.
- `.\mvnw.cmd test`: 350 pruebas ejecutadas, 349 correctas y 1 fallo ajeno al cambio en `DashboardControllerTest.returnsDashboardSnapshotWithDailyMetricsAndPriorityEvents`. El backend local permanecia activo y el contador de publicaciones del dia encontro 2 registros frente al unico registro esperado por el fixture.
- No se detuvo el backend activo ni se modificaron datos locales del usuario para forzar el aislamiento de la suite.
