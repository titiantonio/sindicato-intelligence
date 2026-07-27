# Resumen expandible y decodificación HTML en eventos

## Fecha

27/07/2026.

## Objetivo

Evitar que las descripciones y contenidos extensos de un evento ocupen toda la
página, especialmente en el caso real del evento 166, y mostrar correctamente
entidades HTML como `Educaci&oacute;n`.

## Contexto

El detalle del evento interpolaba la descripción completa sin límite visual. En
el evento 166 el bloque alcanzaba 1408 px de altura y conservaba entidades HTML
literales procedentes de noticias agregadas.

La tarea se corresponde con T11.5 y T11.6 del Documento 31 y se registra como
mantenimiento correctivo posterior al Sprint 14.

## Fase MVP

Fase 11, frontend Angular y backoffice de eventos.

## Archivos modificados

- `frontend/src/app/shared/components/expandable-text/*`.
- `frontend/src/app/shared/utils/html-entities.ts`.
- `frontend/src/app/core/services/event.service.ts`.
- `frontend/src/app/core/services/event.service.spec.ts`.
- `frontend/src/app/features/events/events-page.component.*`.
- `frontend/src/app/features/events/event-detail-page.component.*`.
- `frontend/e2e/support/mock-api.ts`.
- `frontend/e2e/events-pilot.mock.spec.ts`.
- `frontend/package.json`.
- `frontend/package-lock.json`.
- `CHANGELOG.md`.
- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.

## Decisiones

- Se crea un componente compartido que recorta por líneas mediante CSS y mide
  el desbordamiento real para mostrar el control únicamente cuando hace falta.
- El listado usa dos líneas; el detalle del evento y sus contenidos generados
  usan cuatro.
- El control expone `aria-controls` y `aria-expanded`, y cambia entre
  `Mostrar más` y `Mostrar menos`.
- Las entidades HTML se decodifican al mapear la respuesta de `EventService`.
  El resultado se interpola como texto plano, sin usar `innerHTML` en la vista.
- No se modifican contratos REST, backend, dominio, roles ni automatizaciones.
- La versión frontend se incrementa de `0.0.49` a `0.0.50`.

## Documento 31

Se añade y completa la tarea `19.63 Resumen expandible y decodificacion HTML
en eventos`, vinculada a T11.5/T11.6.

## Pruebas y verificaciones

- Pruebas Angular focales: 19 correctas.
- Suite Angular completa: 168 correctas.
- Build Angular de producción: correcto, 542,80 kB iniciales.
- Playwright focal `events-pilot.mock.spec.ts`: 5 pruebas correctas.
- Verificación navegada contra el evento real 166:
  - previsualización de 102 px frente a 1408 px de contenido completo;
  - `Mostrar más/menos` y `aria-expanded` operativos;
  - entidades como `&oacute;` convertidas a caracteres legibles;
  - contenido generado #50 también recortado.
