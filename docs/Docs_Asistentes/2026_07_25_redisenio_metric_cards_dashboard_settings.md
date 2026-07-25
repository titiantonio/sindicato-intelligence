# Fecha

2026-07-25

# Objetivo

Revisar y redisenar las metric cards porque los datos aparecian descuadrados, especialmente en configuracion, manteniendo en `/settings` cuatro tarjetas en la misma linea como en dashboard.

# Contexto

Intervencion de mantenimiento correctivo/evolutivo sobre el backoffice Angular. Encaja con la Fase 11/12 del MVP y con el Sprint 14 de modernizacion visual, sin cambios en contratos REST, backend, dominio, seguridad ni persistencia.

# Fase MVP

Fase 11/12. Mantenimiento visual posterior a Sprint 14.

# Archivos modificados

- `frontend/src/app/shared/components/metric-card/metric-card.component.html`
- `frontend/src/app/shared/components/metric-card/metric-card.component.scss`
- `frontend/src/app/shared/components/metric-card/metric-card.component.ts`
- `frontend/src/app/shared/components/metric-card/metric-card.component.spec.ts`
- `frontend/package.json`
- `frontend/package-lock.json`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

# Decisiones

- Se mantuvo un unico componente compartido para dashboard y settings.
- Se separo el valor principal de la tarjeta de los datos secundarios para mejorar jerarquia y evitar descuadres internos.
- Los datos secundarios pasan a filas compactas con etiqueta e icono a la izquierda y valor a la derecha.
- La etiqueta de estado (`Hoy`, `Diario`, `%`, `ms`, etc.) queda en la cabecera derecha en escritorio, junto al titulo de la card.
- `/settings` mantiene cuatro metric cards en una misma linea de escritorio, igual que dashboard.
- En movil la etiqueta baja de fila para evitar solapamientos.
- Version frontend subida a `0.0.43`.

# Pruebas o verificaciones

- `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/shared/components/metric-card/metric-card.component.spec.ts --include=src/app/features/settings/settings-page.component.spec.ts`: OK, 19 tests.
- `npm.cmd run build`: OK.
- `npx.cmd playwright test e2e/visual-system.mock.spec.ts`: OK, 4 tests.
