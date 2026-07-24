# Modernización visual integral del frontend y objetivo WCAG 2.2 AA

Fecha: 2026-07-24

## Objetivo

Extender a todo el frontend la dirección visual navy/teal aprobada por el usuario en el piloto `/events`, manteniendo Angular 21, PrimeNG y SCSS, y reforzando los criterios aplicables de WCAG 2.2 AA.

## Contexto y correspondencia MVP

- Fase del Documento 30: Fase 11, frontend Angular.
- Backlog operativo: Sprint 14 del Documento 31.
- Tareas cerradas: T14.2.8 y T14.3 a T14.7.
- No se modifican contratos REST, dominio, roles, backend, Flyway, n8n, IA ni Telegram.

## Cambios realizados

- Consolidado `frontend/src/styles/design-system.scss` con tokens y patrones compartidos para tema claro/oscuro.
- Modernizados shell, cabecera, sidebar, navegación móvil y selector de tema.
- Modernizadas las cuatro pantallas de autenticación.
- Extendida la composición visual a dashboard, eventos y detalle, noticias y detalle, contenido y detalle/editor, publicaciones y detalle.
- Extendida la composición visual a fuentes, usuarios, auditoría y settings.
- Modernizados tabla estándar, tarjetas métricas, badges y estados vacíos.
- Homogeneizados los 10 diálogos: aspecto, responsive, foco inicial/contenido, cierre accesible y retorno al disparador.
- Añadido `aria-sort` a todas las columnas ordenables; reforzados `aria-current`, `aria-pressed` y enlaces semánticos.
- Añadida cobertura E2E mockeada para las rutas autenticadas, temas, diálogos y viewport de 320 CSS px.
- Ampliada la API mockeada con noticias, detalles de publicación y destinos Telegram.
- Actualizado `docs/accessibility.md` con evidencia final por rutas y límites de la declaración de conformidad.
- Versión frontend incrementada de `0.0.39` a `0.0.40`.

## Archivos principales

- `frontend/src/styles/design-system.scss`
- `frontend/src/app/layout/shell/shell.component.html`
- `frontend/src/app/layout/shell/shell-modern.component.scss`
- `frontend/src/app/features/auth/auth-modern.component.scss`
- `frontend/src/app/shared/components/theme-toggle/*`
- `frontend/src/app/shared/directives/dialog-focus-return.directive.ts`
- Componentes HTML/TypeScript de dashboard, eventos, noticias, contenido, publicaciones, fuentes, usuarios, auditoría y settings.
- `frontend/e2e/visual-system.mock.spec.ts`
- `frontend/e2e/support/mock-api.ts`
- `frontend/e2e/editorial-flow.mock.spec.ts`
- `frontend/angular.json`
- `frontend/package.json`
- `frontend/package-lock.json`
- `docs/accessibility.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`
- `CHANGELOG.md`

## Decisiones

- Se mantiene la dirección aprobada: azul marino para estructura, teal para acento operativo, superficies claras sobrias y equivalente oscuro.
- El sistema visual se implementa como capa global posterior a los estilos existentes para reducir el riesgo funcional y conservar la encapsulación Angular.
- `/events` conserva sus estilos específicos de piloto; el resto consume patrones globales y componentes compartidos.
- No se añade una herramienta automática de auditoría de accesibilidad porque supondría una dependencia nueva no aprobada.
- La documentación no declara certificación WCAG global: recomienda completar lector de pantalla, zoom multinavegador y contraste sobre datos reales antes de una declaración formal.

## Verificaciones

- Suite Angular completa:
  - `npm.cmd test -- --watch=false --browsers=ChromeHeadless`
  - Resultado: `160 SUCCESS`.
- Suite Playwright mockeada:
  - `npm.cmd run e2e:mock`
  - Resultado: `14 passed`.
- Build de producción:
  - `npm.cmd run build`
  - Resultado: correcto.
  - Presupuesto preventivo del bundle recalibrado de `535 kB` a `550 kB`; límite de error conservado en `1 MB`.
- Revisión visual manual en Chromium:
  - login claro;
  - dashboard claro;
  - eventos oscuro;
  - settings claro;
  - diálogo de fuente a 320 px.
- La suite opt-in `e2e:backend` no se ejecutó: no hay cambios de contrato o integración y la verificación mockeada cubre el alcance visual sin invocar IA, Telegram ni PostgreSQL reales.
