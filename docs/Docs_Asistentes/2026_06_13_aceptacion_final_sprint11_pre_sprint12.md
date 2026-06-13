# Aceptacion final Sprint 11 previa a Sprint 12

Fecha: 2026-06-13

## Objetivo

Dejar Sprint 11 preparado para cierre formal y desbloquear el inicio de Sprint 12.

## Cambios realizados

- Creado `scripts/validate-sprint11-acceptance.ps1`.
- Creado `scripts/fake-telegram-server.ps1`.
- Actualizado Documento 31 para eliminar pendientes falsos del roadmap y registrar aceptacion final.
- Actualizado `CHANGELOG.md`.

## Entorno usado

- PostgreSQL: `localhost:5432`.
- Backend: `http://localhost:8080`.
- Frontend: `http://localhost:4200`.
- n8n: `http://localhost:5678`.
- MailHog: `http://localhost:8025`.
- Telegram fake local: `http://localhost:19090`.

El backend se arranco con:

```powershell
TELEGRAM_ENABLED=true
TELEGRAM_BASE_URL=http://localhost:19090
TELEGRAM_BOT_TOKEN=fake-token
TELEGRAM_CHAT_ID=fake-chat
```

## Validacion ejecutada

Comando:

```powershell
.\scripts\validate-sprint11-acceptance.ps1
```

Resultado:

```text
SPRINT11_ACCEPTANCE_OK userId=7 eventId=156 contentId=61 publicationId=25 scheduledPublicationId=24
```

Flujo validado:

- Login ADMIN.
- Alta de usuario EDITOR sin password.
- Password temporal enviada por MailHog.
- Primer login con cambio obligatorio.
- Cambio de password y notificacion por email.
- Bloqueo, desbloqueo y desactivacion con emails.
- Creacion de noticias.
- Clasificacion.
- Deteccion de eventos.
- Merge de eventos.
- Analisis IA determinista.
- Generacion, edicion y aprobacion de contenido.
- Publicacion programada `SCHEDULED`.
- Publicacion inmediata `PUBLISHED` contra Telegram fake.
- Dashboard.
- Auditoria de usuarios y editorial.

## Validacion n8n

Comando:

```powershell
.\n8n\validate-workflows.ps1
```

Resultado:

- OK para `WF-01` a `WF-06`.
- JSON parseable.
- Nodo `Authenticate Backend`.
- Cabecera `Authorization: Bearer`.
- Endpoints esperados presentes.

Workflows importados en n8n local:

```text
WF-01-Capture-News
WF-02-Classify-News
WF-03-Detect-Events
WF-04-Generate-Analysis
WF-05-Generate-Content
WF-06-Publish-Telegram
```

Nota operativa:

- `n8n execute` desde dentro del contenedor activo no se usa como criterio porque n8n bloquea el Task Broker `5679` cuando la instancia ya esta ejecutandose.
- La cobertura funcional queda validada por los endpoints reales del backend y por contratos de workflow.

## Resultado

Sprint 11 queda listo para cierre formal.

No quedan bloqueadores MVP para abrir Sprint 12.

Proxima tarea: iniciar Sprint 12 con `T12.1` versionado de prompts.
