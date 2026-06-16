# Correccion scheduler automatizaciones backend

Fecha: 2026-06-16

## Objetivo

Corregir el fallo del backend registrado por el scheduler dinamico de automatizaciones:

```text
This ResultSet is closed
```

## Contexto

- Fase MVP afectada: consolidacion de automatizaciones internas posterior a las fases WF-02/WF-04 migradas a Spring Boot.
- Documento 31: bloque Sprint 12, tareas T12.11-T12.13 ya completadas; esta intervencion se registra como correccion tecnica sobre T12.13.
- El backend compilaba correctamente, pero el scheduler lanzaba errores periodicos en `JpaAutomationWorkflowSettingRepository.findDue`.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/automation/infrastructure/JpaAutomationWorkflowSettingRepository.java`
- `backend/pom.xml`
- `CHANGELOG.md`

## Decisiones tomadas

- Se sustituyo `getResultStream()` por `getResultList().stream()` en las consultas de configuracion de automatizaciones.
- El cambio mantiene el puerto de dominio y evita depender de un cursor JPA abierto durante el mapeo a dominio.
- No se modificaron reglas de negocio ni arquitectura.

## Pruebas y verificaciones

- `mvn -DskipTests compile`: OK antes del cambio.
- `mvn "-Dtest=ProcessDueAutomationWorkflowsUseCaseTest,RunAutomationWorkflowUseCaseTest,AutomationControllerTest" test`: OK, 9 tests.
- `mvn clean test`: OK.
- Reinicio operativo del backend local: `GET /api/v1/health` devuelve `200`.
- Verificada una pasada del scheduler a las 18:51:45 con `processedWorkflows=1` sin nuevos errores `This ResultSet is closed`.
