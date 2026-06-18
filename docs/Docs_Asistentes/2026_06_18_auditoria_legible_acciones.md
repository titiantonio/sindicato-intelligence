# 2026_06_18 - Auditoria legible de acciones

## Fecha

2026-06-18

## Objetivo

Corregir la auditoria visible en `/audit` para que las acciones auditadas muestren detalles descriptivos, con referencias suficientes a eventos, contenidos, publicaciones, estados y fallos, evitando JSON crudo o pares `clave=valor`.

## Contexto

El problema detectado era doble:

- La publicacion directa no generaba entrada en auditoria editorial, mientras que la programacion si quedaba registrada.
- Los detalles visibles de auditoria se mostraban con valores tecnicos como JSON o `clave=valor`, poco utiles para revision operativa.

## Fase MVP

Mantenimiento correctivo sobre Sprint 11 / Fases 10-11:

- Fase 10: publicacion Telegram.
- Fase 11: backoffice Angular y auditoria visible ADMIN.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/audit/application/AuditDetailFormatter.java`
- `backend/src/main/java/es/sindicato/intelligence/event/application/MergeEventsUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/content/application/EditGeneratedContentUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/publication/application/PublishContentUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/publication/application/PublishScheduledPublicationsUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/publication/application/SchedulePublicationUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/user/application/*`
- `backend/src/main/java/es/sindicato/intelligence/auth/application/*PasswordUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/auth/application/LoginUseCase.java`
- `frontend/src/app/features/audit/audit-page.component.ts`
- `frontend/src/app/features/audit/audit-page.component.html`
- Tests backend y frontend asociados.
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

## Decisiones

- No se modifica el esquema de `audit_log` ni `user_audit_log`; los campos `oldValues`, `newValues` y `details` siguen siendo texto.
- Las nuevas auditorias se escriben ya como frases legibles desde backend.
- Angular mantiene compatibilidad con registros historicos y transforma JSON o `clave=valor` a texto descriptivo al pintar, filtrar y ordenar.
- La tabla `/audit` muestra usuarios por nombre/email cuando la API puede resolverlos, evita mostrar el detalle largo en cada fila y abre un modal `Ver detalle` con el mismo patron operativo usado en errores de metricas IA.
- Las fechas internas detectadas dentro del detalle se formatean con el mismo formato visual que la columna Fecha.
- Las filas de auditoria fallidas se resaltan con el tratamiento visual de error usado en metricas IA.
- La auditoria se consulta por dia mediante `date=YYYY-MM-DD` en backend y selector diario en frontend, usando zona operativa `Europe/Madrid`.
- `PublishContentUseCase` crea una publicacion `PENDING` antes de resolver proveedor para poder registrar `FAILED` si Telegram no esta disponible.
- El versionado se actualiza finalmente a `0.0.60-SNAPSHOT` tras incorporar el filtro diario de auditoria.

## Pruebas o verificaciones

- Backend focal:

```powershell
mvn "-Dtest=PublishContentUseCaseTest,PublishScheduledPublicationsUseCaseTest,SchedulePublicationUseCaseTest,MergeEventsUseCaseTest,EditGeneratedContentUseCaseTest,ChangeUserStatusUseCaseTest,UpdateUserUseCaseTest,ResetTemporaryPasswordUseCaseTest,LoginUseCaseTest,PublicationControllerTest" test
```

Resultado: OK, 23 tests.

- Backend focal adicional:

```powershell
mvn "-Dtest=PublicationControllerTest" test
```

Resultado: OK, 3 tests.

- Frontend focal:

```powershell
npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/audit/audit-page.component.spec.ts --include=src/app/core/services/audit.service.spec.ts
```

Resultado: OK, 8 tests.
