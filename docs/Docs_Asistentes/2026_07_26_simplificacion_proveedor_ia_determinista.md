# Simplificacion del proveedor IA determinista

## Fecha

2026-07-26

## Objetivo

Limitar la configuracion del proveedor `Determinista local` a su activacion o
pausa, eliminando de su tarjeta las operaciones de credenciales y modelos que
solo aplican a proveedores IA externos.

## Contexto

La intervencion corresponde a la Fase 12 del Documento 30 y refina las tareas
T12.32 y T12.35 del Documento 31. No cambia contratos REST, reglas de dominio,
workflows ni persistencia.

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
- `docs/Docs_Asistentes/2026_07_26_simplificacion_proveedor_ia_determinista.md`.

## Decisiones

- La tarjeta `Determinista local` conserva el interruptor y la accion
  `Guardar estado`.
- No muestra estado ni campo de clave API, eliminacion de credencial o recarga
  de modelos.
- Los proveedores externos mantienen todas sus opciones actuales.
- El componente ignora solicitudes de modelos y borrado de credenciales para
  el codigo `deterministic`, incluso si se invocan fuera de la plantilla.
- La version frontend se incrementa de `0.0.48` a `0.0.49`.

## Pruebas y verificaciones

- `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/settings/settings-page.component.spec.ts`: 18 pruebas correctas.
- `npm.cmd test -- --watch=false --browsers=ChromeHeadless`: 164 pruebas correctas.
- `npm.cmd run build`: build de produccion correcto.
