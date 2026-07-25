# Aclarado del panel del login en modo claro

Fecha: 2026-07-25

## Objetivo

Reducir el peso visual del bloque `login-page__intro` en modo claro sin perder la identidad navy/teal aprobada ni alterar el modo oscuro.

## Contexto

Intervencion de mantenimiento visual de la Fase 11, posterior al Sprint 14. Se revisaron `docs/00-agent-context.md`, `docs/accessibility.md`, la documentacion tecnica aplicable y la tarea operativa del Documento 31.

## Archivos modificados

- `frontend/src/app/features/auth/auth-modern.component.scss`
- `frontend/e2e/app-startup.spec.ts`
- `frontend/package.json`
- `frontend/package-lock.json`
- `CHANGELOG.md`
- `docs/accessibility.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 - Plan de Implementacion Detallado.md`

## Decisiones

- El tema claro usa ahora un degradado menta muy suave, una segunda luz azul discreta y una sombra teal ligera.
- El titular, la descripcion y la etiqueta usan colores oscuros con contraste WCAG 2.2 AA.
- Las dos tarjetas informativas pasan a una superficie blanca translucida con borde suave.
- Los valores anteriores se encapsulan como variables de la variante `.theme-dark`, conservando el aspecto oscuro.
- No se han modificado estructura HTML, textos, foco, autenticacion ni contratos de API.

## Accesibilidad

Contrastes calculados sobre los extremos del degradado claro:

- Titular: `12.37:1` a `13.59:1`.
- Texto descriptivo: `6.33:1` a `6.96:1`.
- Etiqueta de seccion: `6.62:1` a `7.27:1`.

Todos superan el minimo `4.5:1` exigido para texto normal. La regresion E2E fija el tema claro, verifica el fondo renderizado y comprueba el color del titular.

## Verificaciones

- `npm.cmd run build`: OK.
- `npx.cmd playwright test e2e/app-startup.spec.ts --reporter=list`: OK, 2 tests.
- `git diff --check`: OK.
- Inspeccion visual a `1440 x 900` en claro y oscuro.

Evidencias:

- `docs/Docs_Asistentes/2026_07_25_login_modo_claro.png`
- `docs/Docs_Asistentes/2026_07_25_login_modo_oscuro.png`

## Versionado

- Frontend actualizado de `0.0.46` a `0.0.47`.
- Cambio registrado en `CHANGELOG.md`.
- Tarea 19.61 incorporada al Documento 31.
