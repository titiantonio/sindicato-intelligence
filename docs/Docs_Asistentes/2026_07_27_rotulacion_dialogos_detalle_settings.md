# Rotulación de diálogos de detalle en settings

## Fecha

2026-07-27

## Objetivo

Revisar todos los diálogos de detalle de operaciones y errores de
`/settings`, sustituyendo claves técnicas sin espacios por etiquetas
funcionales legibles.

## Contexto

La intervención corresponde a las Fases 11 y 12 del Documento 30 y refina las
tareas T12.24/T12.35 y T14.6/T14.7 del Documento 31. No modifica contratos
REST, lógica de dominio, persistencia ni workflows.

## Archivos modificados

- `frontend/src/app/features/settings/settings-page.component.html`.
- `frontend/src/app/features/settings/settings-page.component.ts`.
- `frontend/src/app/features/settings/settings-page.component.spec.ts`.
- `frontend/package.json`.
- `frontend/package-lock.json`.
- `CHANGELOG.md`.
- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 24 – Diseño UX-UI del Backoffice.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Docs_Asistentes/2026_07_27_rotulacion_dialogos_detalle_settings.md`.

## Decisiones

- Se han contrastado las claves del frontend con los mapas de detalle que
  generan `WF-02` a `WF-06`: 79 claves únicas, 64 visibles y 15 internas
  ocultas.
- Las 64 claves visibles disponen de etiqueta explícita en español; entre
  ellas, `candidateEventIds` pasa a `IDs de eventos candidatos` y
  `reducedContextRetry` a `Reintento con contexto reducido`.
- Las claves futuras desconocidas se separan automáticamente para evitar que
  vuelvan a mostrarse concatenadas.
- Los booleanos se presentan como `Sí/No`, y los tipos técnicos de operación y
  entidad se muestran con denominaciones funcionales.
- La versión frontend se incrementa de `0.0.50` a `0.0.51`.

## Pruebas y verificaciones

- Cruce automático backend/frontend: 0 claves visibles sin etiqueta.
- `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/settings/settings-page.component.spec.ts`:
  19 pruebas correctas.
- `npm.cmd test -- --watch=false --browsers=ChromeHeadless`: 169 pruebas
  correctas.
- `npm.cmd run build`: build de producción correcto; bundle inicial de
  `542.80 kB`.
- No se ejecutaron pruebas backend porque la intervención no modifica código,
  contratos ni comportamiento del backend.
