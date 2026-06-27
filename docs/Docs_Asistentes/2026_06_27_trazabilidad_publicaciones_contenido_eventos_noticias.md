# 2026-06-27 - Trazabilidad de publicaciones, contenido, eventos y noticias

## Fecha

2026-06-27.

## Objetivo

Ampliar la trazabilidad del backoffice para que publicaciones, contenidos, eventos y noticias permitan navegar entre sus entidades relacionadas y muestren los identificadores principales del flujo `News -> Event -> Analysis -> Content -> Publication`.

## Contexto

La mejora corresponde a una ampliacion posterior al Sprint 12 sobre tareas ya completadas del Documento 31: `T11.6 Detalle Evento`, `T11.7 Contenido` y `T11.8 Publicaciones`.

## Fase MVP

Fases 9, 10 y 11 del Documento 30 ya completadas. No se modifica la arquitectura ni se devuelve logica de negocio al frontend.

## Archivos modificados

- Backend: modulos `content`, `publication`, `news` y `event`, mas migracion Flyway `V17__add_generated_content_analysis_trace.sql`.
- Frontend: rutas y pantallas `/publications/:id`, `/content/:id`, `/news/:id`, enlaces desde publicaciones, contenido y detalle de evento.
- Documentacion: Documento 31, `CHANGELOG.md` y version Maven.

## Decisiones

- Se anadio `analysis_id` nullable en `generated_content` para conservar el analisis usado solo en nuevos contenidos; los contenidos historicos muestran analisis no registrado.
- Los endpoints simples existentes se mantienen y se anaden detalles enriquecidos en `/api/v1/content/{id}/detail` y `/api/v1/publications/{id}/detail`.
- `GET /api/v1/news/{id}` se enriquece con `eventId` y clasificacion nullable.

## Pruebas o verificaciones

- `mvn -DskipTests compile`: correcto.
- `mvn "-Dtest=GenerateContentUseCaseTest,JpaGeneratedContentRepositoryTest,NewsControllerTest,PublicationControllerTest" test`: 16 tests correctos.
- `npm test -- --watch=false --browsers=ChromeHeadless`: 132 tests correctos.
- `npm run build`: correcto con avisos previos de presupuesto Angular en bundle inicial y estilos de pantallas existentes.
- `mvn test`: no completado por timeout local. El subconjunto con `ContentControllerTest` fallo en `generatesApprovesAndRejectsContent` por llamada a Gemini real y respuesta 502, no por compilacion ni por contrato de trazabilidad.
