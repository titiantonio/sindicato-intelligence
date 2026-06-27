# Modernizacion frontend PrimeNG Tailwind

## Fecha

2026-06-27

## Objetivo

Adaptar el roadmap de modernizacion frontend al proyecto e iniciar su implementacion tecnica con Angular 21, PrimeNG y Tailwind.

## Contexto

Trabajo posterior al Sprint 12 sobre el backoffice Angular. La logica de negocio permanece en Spring Boot y Angular conserva el consumo de APIs `/api/v1`, guards, JWT y servicios existentes.

## Fase MVP

Fase 11 Frontend Angular, como mejora posterior al MVP y Sprint 12.

## Archivos modificados

- `frontend/package.json`
- `frontend/package-lock.json`
- `frontend/angular.json`
- `frontend/.postcssrc.json`
- `frontend/src/tailwind.css`
- `frontend/src/styles.scss`
- `frontend/src/app/app.config.ts`
- `frontend/src/app/layout/shell/*`
- `frontend/src/app/shared/components/status-badge/*`
- `frontend/src/app/shared/components/metric-card/*`
- `docs/design-system.md`
- `docs/accessibility.md`
- `docs/frontend-review.md`
- `docs/Documentacion Proyecto/Documento 07 - Arquitectura Frontend (Angular).md`
- `docs/Documentacion Proyecto/Documento 24 – Diseño UX-UI del Backoffice.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`
- `CHANGELOG.md`

## Decisiones

- Se acepta cambiar la base UI de Angular Material/SCSS a Angular 21 + PrimeNG + Tailwind.
- PrimeNG queda configurado con tema Aura y dark mode mediante `:root[data-theme="dark"]`.
- Tailwind se separa en `src/tailwind.css` para evitar que Sass procese sus imports.
- La migracion visual por pantalla queda desglosada como trabajo progresivo para reducir riesgo de regresiones.
- No se cambian contratos REST ni reglas de dominio.

## Pruebas o verificaciones

- `npm install` en `frontend/`: OK.
- `npm run build` en `frontend/`: OK.
- `npm test -- --watch=false --browsers=ChromeHeadless` en `frontend/`: OK, 146 tests.
- Persisten warnings de budgets en bundle inicial y SCSS historico; quedan documentados en `docs/frontend-review.md`.
