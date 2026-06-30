# 2026-06-27 - Migracion editorial y administrativa parcial a PrimeNG

## Objetivo

Continuar la modernizacion frontend PrimeNG + Tailwind con pantallas editoriales y administrativas posteriores al bloque operativo.

## Contexto

- Fase MVP relacionada: Fase 11 Frontend Angular y bloque posterior a Sprint 12.
- Backlog operativo: Documento 31, tareas 19.6 y 19.9.
- Se mantienen intactos backend, contratos `/api/v1`, servicios Angular de datos, JWT, roles, guards y reglas de negocio.

## Archivos modificados

- `frontend/src/app/features/content/content-page.component.ts`
- `frontend/src/app/features/content/content-page.component.html`
- `frontend/src/app/features/publications/publications-page.component.ts`
- `frontend/src/app/features/publications/publications-page.component.html`
- `frontend/src/app/features/sources/sources-page.component.ts`
- `frontend/src/app/features/sources/sources-page.component.html`
- `frontend/src/app/features/audit/audit-page.component.ts`
- `frontend/src/app/features/audit/audit-page.component.html`
- `frontend/package.json`
- `frontend/package-lock.json`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`
- `docs/frontend-review.md`
- `CHANGELOG.md`

## Decisiones tomadas

- Migrar mensajes, filtros, botones, acciones y dialogos a PrimeNG en `content`, `publications`, `sources` y `audit`.
- Mantener tablas nativas temporalmente para conservar ordenacion, filtros y paginacion ya testeados.
- Dejar `users`, `settings`, `content-detail` y `publication-detail` como siguiente bloque por mayor densidad funcional.
- Subir version frontend a `0.0.4`.

## Verificacion

- `npm run build`: OK. Persiste warning de bundle inicial en 509.27 KB, 9.27 KB sobre budget.
- `npm test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/content/content-page.component.spec.ts --include=src/app/features/publications/publications-page.component.spec.ts --include=src/app/features/sources/sources-page.component.spec.ts --include=src/app/features/audit/audit-page.component.spec.ts`: OK, 21 tests.
- `npm test -- --watch=false --browsers=ChromeHeadless`: OK, 146 tests.
- `npm audit --audit-level=low`: OK, 0 vulnerabilidades.
- `npm install --package-lock-only`: OK, 0 vulnerabilidades.

## Pendiente

- Migrar `users`, `settings`, `content-detail` y `publication-detail`.
- Reducir warnings de budgets SCSS y bundle inicial.
- Revisar normalizacion de mojibake heredado en plantillas antes del cierre visual final.
