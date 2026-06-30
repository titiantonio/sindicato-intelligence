# Auth PrimeNG y auditoria frontend

## Fecha

2026-06-27

## Objetivo

Continuar la modernizacion frontend PrimeNG + Tailwind con el bloque de autenticacion, reduccion de bundle inicial y resolucion de vulnerabilidades npm pendientes.

## Contexto

Trabajo posterior al Sprint 12 sobre Fase 11 Frontend Angular. No se modifican backend, contratos REST, JWT, roles ni reglas de dominio.

## Fase MVP

Fase 11 Frontend Angular, mejora posterior al MVP.

## Archivos modificados

- `frontend/package.json`
- `frontend/package-lock.json`
- `frontend/src/app/app.routes.ts`
- `frontend/src/app/features/auth/login/*`
- `frontend/src/app/features/auth/forgot-password/*`
- `frontend/src/app/features/auth/reset-password/*`
- `frontend/src/app/features/auth/change-password/*`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`
- `docs/frontend-review.md`
- `docs/accessibility.md`
- `CHANGELOG.md`

## Decisiones

- Se usa `npm audit fix` no forzado para evitar downgrades mayores.
- Se anaden overrides transitorios para `@babel/core` y `undici` porque Angular 21.2.17 arrastra versiones vulnerables.
- Las rutas se convierten a `loadComponent` para reducir bundle inicial sin cambiar URLs ni guards.
- Auth migra a PrimeNG manteniendo formularios reactivos y servicios existentes.

## Pruebas o verificaciones

- `npm audit --audit-level=low`: 0 vulnerabilidades.
- `npm run build`: OK, con warning residual de bundle inicial 503.69 KB frente a budget 500 KB y warnings SCSS historicos.
- Tests focales auth: 18 tests, 0 fallos.
- Suite frontend completa: 146 tests, 0 fallos.
