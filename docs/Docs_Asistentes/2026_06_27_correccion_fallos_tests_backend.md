# 2026-06-27 - Correccion de fallos en tests backend

## Fecha

2026-06-27

## Objetivo

Corregir los cuatro fallos detectados al ejecutar `mvn test` tras la optimizacion de la pagina de noticias.

## Contexto

La intervencion encaja con mantenimiento de calidad de backend dentro del MVP ya completado. Los fallos no requerian cambios de contrato API ni de arquitectura: tres tests de integracion heredaban la configuracion IA real de la base de datos y un `WebMvcTest` de seguridad no declaraba el mock de un caso de uso incorporado al controlador de contenidos.

## Fase MVP

Fase 11 Frontend Angular y fase 12 automatizaciones/observabilidad IA, como correccion transversal de pruebas de regresion.

## Archivos modificados

- `backend/src/test/java/es/sindicato/intelligence/analysis/api/AnalysisControllerTest.java`
- `backend/src/test/java/es/sindicato/intelligence/classification/api/ClassificationControllerTest.java`
- `backend/src/test/java/es/sindicato/intelligence/content/api/ContentControllerTest.java`
- `backend/src/test/java/es/sindicato/intelligence/core/config/SecurityConfigTest.java`
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

## Decisiones

- Fijar explicitamente `WF02_CLASSIFICATION`, `WF04_ANALYSIS` y `WF05_CONTENT` al proveedor `deterministic` dentro de los tests API afectados.
- Mantener la configuracion productiva sin cambios.
- Anadir los mocks de `GetGeneratedContentDetailUseCase` y `EventResponseMapper` al slice de seguridad para reflejar las dependencias actuales de `ContentController`.
- Incrementar version Maven a `0.0.82-SNAPSHOT`.

## Pruebas o verificaciones

- `mvn -q "-Dtest=AnalysisControllerTest,ClassificationControllerTest,ContentControllerTest,SecurityConfigTest" test` ejecutado en `backend/` con resultado OK.
- `mvn test` ejecutado en `backend/` con resultado OK: 287 tests, 0 fallos, 0 errores.
