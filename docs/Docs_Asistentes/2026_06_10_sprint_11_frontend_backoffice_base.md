# Sprint 11 frontend backoffice base

## Fecha

2026-06-10

## Objetivo

Iniciar Sprint 11 del MVP creando el frontend Angular del backoffice, con layout principal, login funcional contra backend y pantallas visuales mock para dashboard, eventos, contenido y publicaciones.

## Contexto

Se reviso previamente la documentacion obligatoria del proyecto y los contratos reales del backend para ajustar el alcance del Sprint 11 a los endpoints actualmente implementados. La carpeta existente `fronted/` se renombro a `frontend/` por decision explicita del usuario antes de generar el proyecto Angular.

## Fase MVP

Sprint 11: Frontend Angular.

## Tareas Documento 31

- T11.1 completada.
- T11.2 completada.
- T11.3 completada.
- T11.4 completada con datos mock temporales.
- T11.5 completada con datos mock temporales.
- T11.7 completada con datos mock temporales.
- T11.8 completada con datos mock temporales.
- T11.6 pendiente por falta de endpoint real de detalle de evento.
- T11.9 pendiente.

## Archivos modificados

- `frontend/**`
- `README.md`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

## Decisiones

- Se crea el frontend en `frontend/`, alineando el repositorio con el nombre definitivo de carpeta.
- Se usa Angular 20 con SCSS, routing, Signals y estructura `core/shared/layout/features`.
- El login consume el endpoint real `POST /api/v1/auth/login` y persiste sesion local con `accessToken`, `refreshToken` y `user`.
- Se añaden `authGuard`, `roleGuard` e interceptor JWT para preparar el backoffice antes de integrar el resto de APIs.
- Se implementan pantallas mock para `dashboard`, `events`, `content` y `publications` sin inventar contratos REST inexistentes.
- La ruta `sources` queda visible solo para `ADMIN`, alineada con la matriz de roles del backend.

## Limitaciones conocidas

- No existen todavia endpoints reales para listado/detalle de eventos, listado de contenidos ni listado de publicaciones.
- `Detalle Evento` no se implementa funcionalmente por ausencia de `GET /api/v1/events/{id}`.
- Las vistas mock son temporales y deben sustituirse por integracion real cuando el backend exponga los contratos faltantes.

## Pruebas o verificaciones

- `npm run build` en `frontend`: OK.
- `npm test -- --watch=false --browsers=ChromeHeadless` en `frontend`: 1 test, OK.

## Versionado y changelog

- `CHANGELOG.md` actualizado con T11.1, T11.2, T11.3 y el avance visual mock del Sprint 11.
- `backend/pom.xml` actualizado a `0.0.30-SNAPSHOT`.
