# Cierre tecnico Sprint 11 - Validacion

Fecha: 2026-06-13

## Objetivo

Cerrar el bloque tecnico de Sprint 11 con evidencia de tests, build, Flyway y validacion estatica de workflows n8n antes de iniciar Sprint 12.

## Cambios realizados

- Creado `n8n/validate-workflows.ps1`.
- Actualizado Documento 31 con la evidencia de cierre tecnico.
- Actualizado `CHANGELOG.md`.

## Validacion ejecutada

### Backend

Comando:

```powershell
mvn test
```

Resultado:

- OK.
- 191 tests ejecutados.
- 0 failures.
- 0 errors.
- 0 skipped.
- Flyway valido 6 migraciones.
- Esquema `public` actualizado, sin migraciones pendientes.

### Frontend tests

Comando:

```powershell
npm.cmd test -- --watch=false --browsers=ChromeHeadless
```

Resultado:

- OK.
- 53 specs ejecutadas.
- 0 failures.

### Frontend build

Comando:

```powershell
npm.cmd run build
```

Resultado:

- OK.
- Artefacto generado en `frontend/dist/frontend`.

### n8n workflows

Comando:

```powershell
& .\n8n\validate-workflows.ps1
```

Resultado:

- OK para `WF-01` a `WF-06`.
- JSON parseable.
- Nodo `Authenticate Backend` presente.
- Cabeceras `Authorization: Bearer` con `accessToken`.
- Endpoints esperados presentes.

## Infraestructura local revisada

`database/docker-compose.yml` mantiene:

- PostgreSQL: `localhost:5432`.
- n8n: `localhost:5678`.
- MailHog SMTP: `localhost:1025`.
- MailHog UI: `http://localhost:8025`.

## Pendiente

No se ejecuto validacion manual integrada con servicios levantados en esta iteracion.

Pendiente de aceptacion funcional:

1. Levantar PostgreSQL, MailHog, n8n, backend y frontend.
2. Ejecutar flujo MVP completo `source/news -> classification -> event -> analysis -> content -> approval/edit -> publication/scheduling`.
3. Confirmar emails en MailHog.
4. Confirmar `/audit`, merge de eventos y publicaciones `SCHEDULED`.
5. Si la aceptacion manual es OK, abrir Sprint 12.
