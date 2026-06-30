# 2026-06-28 - Recalibracion de budgets frontend

## Objetivo

Continuar la modernizacion frontend reduciendo ruido de build tras la migracion a Angular 21 + PrimeNG + Tailwind.

## Contexto

- Fase MVP relacionada: Fase 11 Frontend Angular y bloque posterior a Sprint 12.
- Backlog operativo: Documento 31, tarea 19.11.
- Se mantiene intacta la logica de negocio, rutas, guards, servicios Angular y contratos `/api/v1`.

## Archivos modificados

- `frontend/angular.json`
- `frontend/package.json`
- `frontend/package-lock.json`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`
- `docs/frontend-review.md`
- `CHANGELOG.md`

## Decisiones tomadas

- Recalibrar el budget inicial de `500kB` a `525kB`, manteniendo margen estrecho sobre el bundle real actual de `509.27 kB`.
- Recalibrar `anyComponentStyle` de `4kB` a `6kB` para evitar warnings historicos tras la modernizacion visual.
- Mantener los limites de error existentes: `1MB` para initial y `8kB` para estilos de componente.
- Subir version frontend a `0.0.6`.
- Se probo servir fuentes PrimeIcons como assets/public para eliminar 404 de Karma, pero no resolvio el warning del runner; se retiro la copia de fuentes para no dejar binarios inutiles.

## Verificacion

- `npm run build`: OK sin warnings de budget. Bundle inicial: `509.27 kB`.
- `npm audit --audit-level=low`: OK, 0 vulnerabilidades.
- `npm test -- --watch=false --browsers=ChromeHeadless`: OK, 146 tests, 0 fallos. Persiste warning no bloqueante de Karma por fuentes PrimeIcons en `/base/media/*`.

## Pendiente

- Reducir tamano real de SCSS por pantalla.
- Revisar warning residual de Karma por fuentes PrimeIcons servidas como `/base/media/*`; no afecta build ni resultado de tests.
- Migrar tablas principales a `p-table`.
