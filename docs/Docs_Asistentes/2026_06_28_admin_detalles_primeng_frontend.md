# 2026-06-28 - Migracion ADMIN y detalles editoriales a PrimeNG

## Objetivo

Continuar la modernizacion frontend PrimeNG + Tailwind cerrando el bloque basico pendiente de `users`, `settings`, `content-detail` y `publication-detail`.

## Contexto

- Fase MVP relacionada: Fase 11 Frontend Angular y bloque posterior a Sprint 12.
- Backlog operativo: Documento 31, tareas 19.6 y 19.10.
- Se mantienen intactos backend, contratos `/api/v1`, servicios Angular de datos, JWT, roles, guards y reglas de negocio.

## Archivos modificados

- `frontend/src/app/features/users/users-page.component.ts`
- `frontend/src/app/features/users/users-page.component.html`
- `frontend/src/app/features/settings/settings-page.component.ts`
- `frontend/src/app/features/settings/settings-page.component.html`
- `frontend/src/app/features/content/content-detail-page.component.ts`
- `frontend/src/app/features/content/content-detail-page.component.html`
- `frontend/src/app/features/publications/publication-detail-page.component.ts`
- `frontend/src/app/features/publications/publication-detail-page.component.html`
- `frontend/package.json`
- `frontend/package-lock.json`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`
- `docs/frontend-review.md`
- `CHANGELOG.md`

## Decisiones tomadas

- Migrar `users` a `p-message`, `pInputText`, `pButton` y `p-dialog` sin cambiar flujos ADMIN ni formularios reactivos.
- Migrar `settings` de forma conservadora: mensajes, tabs/acciones principales, filtros/fechas y modales IA, manteniendo selects y checkboxes nativos hasta una pasada especifica.
- Migrar `content-detail` y `publication-detail` a `p-message` para estados de error y carga.
- Mantener tablas HTML nativas temporalmente para conservar filtros, ordenacion y paginacion ya testeados.
- Subir version frontend a `0.0.5`.

## Verificacion

- `npm run build`: OK. Persiste warning de bundle inicial en 509.27 KB, 9.27 KB sobre budget.
- `npm test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/users/users-page.component.spec.ts --include=src/app/features/publications/publication-result.formatter.spec.ts`: OK, 14 tests.
- `npm test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/settings/settings-page.component.spec.ts`: OK, 12 tests.
- `npm test -- --watch=false --browsers=ChromeHeadless`: OK, 146 tests.
- `npm audit --audit-level=low`: OK, 0 vulnerabilidades.
- `npm install --package-lock-only`: OK, 0 vulnerabilidades.

## Pendiente

- Migrar tablas principales a `p-table`.
- Migrar selects, checkboxes y formularios complejos a componentes PrimeNG especificos.
- Reducir warnings de budgets SCSS y bundle inicial.
- Revisar mojibake heredado en plantillas.
