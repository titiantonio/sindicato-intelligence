# Fecha

2026-06-25

# Objetivo

Evitar que noticias fuera de ambito o con informacion insuficiente generen eventos en el flujo `WF-02 -> WF-03`.

# Contexto

El Documento 23 ya define que la clasificacion IA debe devolver `OTROS/FUERA_DE_AMBITO` o `OTROS/INFORMACION_INSUFICIENTE` con relevancia `0` para noticias descartables. El problema estaba en que esas noticias seguian marcandose como `CLASSIFIED`, por lo que la deteccion de eventos podia crear eventos sin relacion con el ambito de la aplicacion.

# Fase MVP

Fase 6 Clasificacion IA, Fase 7 Eventos y Sprint 12 Automatizaciones internas.

# Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/news/domain/NewsStatus.java`
- `backend/src/main/java/es/sindicato/intelligence/news/domain/NewsArticle.java`
- `backend/src/main/java/es/sindicato/intelligence/classification/domain/NewsClassification.java`
- `backend/src/main/java/es/sindicato/intelligence/classification/application/ClassifyNewsUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/event/application/DetectEventUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/event/application/EventVisibilityPolicy.java`
- `backend/src/main/java/es/sindicato/intelligence/event/application/ListEventsUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/event/application/GetEventDetailUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/dashboard/application/DashboardSnapshotUseCase.java`
- `backend/src/main/resources/db/migration/V12__discard_out_of_scope_news_and_archive_events.sql`
- `backend/src/main/resources/db/migration/V13__cleanup_discarded_event_residue.sql`
- `backend/src/test/java/es/sindicato/intelligence/classification/application/ClassifyNewsUseCaseTest.java`
- `backend/src/test/java/es/sindicato/intelligence/classification/domain/NewsClassificationTest.java`
- `backend/src/test/java/es/sindicato/intelligence/news/domain/NewsArticleTest.java`
- `backend/src/test/java/es/sindicato/intelligence/event/application/DetectEventUseCaseTest.java`
- `backend/src/test/java/es/sindicato/intelligence/event/application/EventVisibilityPolicyTest.java`
- `backend/src/test/java/es/sindicato/intelligence/event/api/EventControllerTest.java`
- `backend/src/test/java/es/sindicato/intelligence/automation/application/ProcessPendingEventDetectionUseCaseTest.java`
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

# Decisiones

- Se anade `DISCARDED` como estado operativo explicito de noticia.
- Se conserva siempre la clasificacion IA persistida para trazabilidad.
- Solo se descartan clasificaciones `OTROS` con subcategoria `FUERA_DE_AMBITO` o `INFORMACION_INSUFICIENTE` y relevancia `0`.
- Las noticias educativas de baja relevancia con categoria tematica valida no se descartan.
- La migracion historica archiva solo eventos activos cuyas noticias asociadas son todas descartables.
- Tras revisar el evento `#1485`, se confirma que pueden existir eventos residuales creados por una ejecucion anterior o no desplegada del backend. Por eso se anade una politica de visibilidad: los eventos descartables no se muestran en API ni dashboard aunque existan en base de datos.
- No se borran fisicamente eventos ni asociaciones para preservar trazabilidad; se ocultan de la operativa y se excluyen de metricas.

# Pruebas o verificaciones

Ejecutado correctamente:

```text
mvn "-Dtest=ClassifyNewsUseCaseTest,NewsClassificationTest,NewsArticleTest,DetectEventUseCaseTest,ProcessPendingEventDetectionUseCaseTest" test
```

Resultado: 17 tests, 0 fallos, 0 errores.

Tambien se intento `mvn test` completo. No concluyo antes del timeout local de 180 segundos; se cerraron los procesos Maven/Surefire generados por esa ejecucion. Los informes parciales mostraron fallos existentes de configuracion IA local persistida en `gemini` frente a expectativas deterministas, no relacionados con el nuevo descarte.

Diagnostico adicional sobre evento `#1485`:

```text
events.id=1485, category=OTROS, status=OPEN
news.processing_status=EVENT_MATCHED
classification=OTROS/FUERA_DE_AMBITO, relevance=0
```

Correccion adicional pendiente de verificar: `EventVisibilityPolicy`, filtro de eventos/detalle/dashboard y migracion `V13`.

Verificacion adicional ejecutada:

```text
mvn test-compile
mvn "-Dtest=EventVisibilityPolicyTest,ClassifyNewsUseCaseTest,DetectEventUseCaseTest,ProcessPendingEventDetectionUseCaseTest" test
```

Resultado: compilacion correcta; 12 tests, 0 fallos, 0 errores.

Base local tras `V13`:

```text
flyway version=13 success=true
events.id=1485 status=ARCHIVED
news.id=745 processing_status=DISCARDED
```

API local tras reiniciar backend:

```text
GET /api/v1/health -> 200 {"status":"UP"}
GET /api/v1/events -> event_1485_visible=False
```
