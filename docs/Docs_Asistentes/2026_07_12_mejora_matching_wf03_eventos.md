# Mejora matching WF-03 eventos

## Objetivo

Reducir duplicados en `WF-03-Detect-Events` y mejorar la explicabilidad de la asociacion noticia-evento.

## Contexto

- Fase Documento 30: Fase 7 Eventos y mejora posterior de Fase 12 automatizaciones internas.
- Tarea Documento 31: `19.36 Mejora de matching WF-03 y reduccion de duplicados`.
- La IA no consulta PostgreSQL directamente; Spring Boot selecciona candidatos y valida la respuesta.

## Cambios realizados

- `WF-03` envia a la IA candidatos enriquecidos con estado, fechas, numero de noticias y titulos recientes.
- El selector de candidatos prioriza misma categoria y permite categorias relacionadas cuando hay coincidencia textual fuerte.
- Se anade segunda verificacion para respuestas en banda dudosa `70-84`.
- Se persiste trazabilidad de asociacion en `event_news.match_decision` y `event_news.match_reason`.
- La respuesta tecnica de deteccion incluye `matchDecision`.
- Las metricas IA incorporan detalles sanitizados de candidatos, umbrales, segunda verificacion y decision final.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/event/application/DetectEventUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/event/application/EventMatchCandidate.java`
- `backend/src/main/java/es/sindicato/intelligence/event/application/EventMatchPromptBuilder.java`
- `backend/src/main/java/es/sindicato/intelligence/event/application/DetectEventResult.java`
- `backend/src/main/java/es/sindicato/intelligence/event/api/DetectEventResponse.java`
- `backend/src/main/java/es/sindicato/intelligence/event/api/EventController.java`
- `backend/src/main/java/es/sindicato/intelligence/event/domain/EventMatchDecision.java`
- `backend/src/main/java/es/sindicato/intelligence/event/domain/EventRepository.java`
- `backend/src/main/java/es/sindicato/intelligence/event/infrastructure/EventNewsEntity.java`
- `backend/src/main/java/es/sindicato/intelligence/event/infrastructure/JpaEventRepository.java`
- `backend/src/main/java/es/sindicato/intelligence/event/infrastructure/GeminiEventMatchingAIProvider.java`
- `backend/src/main/java/es/sindicato/intelligence/event/infrastructure/DeterministicEventMatchingAIProvider.java`
- `backend/src/main/resources/db/migration/V22__event_news_match_trace.sql`
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 20 – ERD Final MVP + Estrategia Flyway.md`
- `docs/Documentacion Proyecto/2026_06_27_flujo_completo_wf_01_wf_06.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

## Decisiones

- No se crea una pantalla nueva de revision en esta iteracion.
- La banda `70-84` no asocia automaticamente; obliga a una segunda verificacion defensiva.
- Si la duda persiste, se crea evento nuevo y se deja trazabilidad para revision posterior o fusion manual.

## Verificacion

- `mvnw.cmd "-Dtest=DetectEventUseCaseTest,EventMatchPromptBuilderTest,DeterministicEventMatchingAIProviderTest" test`: OK, 10 tests.
- Una ejecucion ampliada con `EventControllerTest` compilo y aplico `V22`, pero no se usa como verificacion final porque el entorno local conserva datos persistentes y configuracion IA real previa que hacen esa prueba dependiente del estado de PostgreSQL.
