# Design System Frontend

Fecha: 2026-06-27

## Objetivo

Definir la base visual del backoffice tras la modernizacion a Angular 21, PrimeNG y Tailwind.

## Stack UI

- Angular 21.
- PrimeNG como libreria principal de componentes.
- `@primeng/themes` con preset Aura.
- PrimeIcons para iconografia.
- Tailwind CSS 4 y `tailwindcss-primeui` para utilidades.
- SCSS global para tokens del producto y compatibilidad con pantallas existentes.

## Tokens

Los tokens globales viven en `frontend/src/styles.scss`:

- Colores: `--color-primary`, `--color-secondary`, estados `success`, `warning`, `danger`, `info`.
- Superficies: `--color-page`, `--color-surface`, `--color-muted-surface`, `--color-raised-surface`.
- Bordes: `--color-border`, `--color-border-soft`, `--color-input-border`.
- Radios: `--radius-xs`, `--radius-sm`, `--radius-md`, `--radius-lg`, `--radius-xl`.
- Espaciado: escala de 8px mediante `--space-2`, `--space-4`, `--space-6`, `--space-8`.
- Sombras: `--shadow-soft`, `--shadow-card`, `--shadow-elevated`, `--shadow-compact`.
- Movimiento: `--duration-fast`, `--duration-normal`, `--duration-slow`.
- Foco: `--focus-ring`.

## Temas

El tema claro y oscuro se aplican con `ThemeService` mediante:

```text
:root
:root[data-theme='dark']
```

PrimeNG usa el mismo selector de modo oscuro:

```text
:root[data-theme="dark"]
```

## Componentes Base

- Botones: clases `.primary-button`, `.secondary-button`, `.danger-button`, `.ghost-button` o componentes PrimeNG cuando la pantalla se migre.
- Formularios: `.field`, `.form-field`, `.form-grid` y componentes PrimeNG.
- Tablas: `.data-table`, `.table-panel`, `.table-scroll`; objetivo de migracion progresiva a `p-table`.
- Estados: `.loading-state`, `.empty-state`, `.error-state`, `.skeleton-line`.
- Badges: `app-status-badge` usa `p-tag`.
- Metricas: `app-metric-card` usa PrimeIcons y tokens globales.
- Layout: shell con sidebar, header, selector de tema y skip link.

## Reglas de Uso

- No duplicar reglas de negocio en Angular.
- Usar componentes PrimeNG para tablas, dialogos, inputs complejos, paginacion y tags.
- Usar Tailwind para layout y ajustes simples, evitando crear SCSS por pantalla cuando una utilidad sea suficiente.
- Mantener las pantallas densas, escaneables y orientadas al trabajo editorial.
- `Event` debe seguir siendo el centro visual y operativo.
