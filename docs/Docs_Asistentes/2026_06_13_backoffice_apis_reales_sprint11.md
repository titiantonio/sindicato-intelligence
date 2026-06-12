# Backoffice con APIs reales Sprint 11

Fecha: 2026-06-13

## Objetivo

Completar el primer bloque recomendado tras la auditoria: sustituir mocks criticos del backoffice por contratos backend reales para eventos, contenido, publicaciones, dashboard y fuentes.

## Cambios realizados

- Backend: anadidos casos de uso y endpoints de lectura para eventos, detalle de evento, contenido generado, publicaciones y dashboard MVP.
- Frontend: anadidos servicios Angular reales para dashboard, eventos, contenido, publicaciones y fuentes.
- Frontend: integradas pantallas de dashboard, eventos, detalle de evento, contenido, publicaciones y fuentes contra API real.
- Frontend: eliminado `MockDashboardService`.
- Testing: anadidos/actualizados tests MockMvc con JWT simulado para las nuevas APIs.
- Documentacion: actualizado Documento 31 como fuente operativa de estado y pendientes; actualizado CHANGELOG y version backend a `0.0.37-SNAPSHOT`.

## Verificacion

- `mvn "-Dtest=EventControllerTest,ContentControllerTest,PublicationControllerTest,DashboardControllerTest" test` en `backend`: OK.
- `npm.cmd run build` en `frontend`: OK con permisos elevados por restricciones del sandbox local.

## Pendientes recomendados

1. Cerrar T11.10.5 con tests frontend de auth/users/guards/services.
2. Revisar autenticacion JWT uniforme en n8n WF-02..WF-06.
3. Decidir si `POST /api/v1/events/merge`, scheduling de publicaciones y editor manual de contenido entran en MVP o quedan post-MVP.
4. Exponer consulta ADMIN de auditoria si se requiere para operacion del backoffice.