# Configuracion Logback y skill de logging

## Fecha

2026-06-07

## Objetivo

Configurar logs persistentes del backend en consola y archivos rotados por dia, crear una skill de logging/observabilidad y actualizar las reglas de agentes para que las nuevas implementaciones backend incorporen logs operativos.

## Contexto

- Fase MVP: Fase 1, backend base, como mantenimiento tecnico transversal.
- Documento 21: convenciones de logging con Logback y niveles `INFO`, `WARN`, `ERROR`.
- Documento 06 V2.0: registrar especialmente generacion IA, creacion de eventos, publicaciones y errores de integracion.
- Peticion del usuario: almacenar logs en archivo, separar por dias, organizar por carpetas mensuales y retener 90 dias.

## Archivos modificados

- `backend/src/main/resources/logback-spring.xml`
- `backend/.gitignore`
- `AGENTS.md`
- `skills/sindicato-logging-observabilidad/SKILL.md`
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

## Decisiones tomadas

- Mantener logs en consola y archivo simultaneamente.
- Crear archivo activo general en `logs/sindicato-intelligence.log`.
- Crear archivo activo de errores en `logs/errors/sindicato-intelligence-error.log`.
- Rotar por dia y por tamaño maximo de 50 MB.
- Comprimir historicos con `.gz`.
- Organizar historicos en carpetas mensuales `archive/yyyy-MM`.
- Retener logs durante 90 dias mediante `maxHistory`.
- Limitar historico general a 2 GB y errores a 1 GB.
- Permitir cambiar la ubicacion mediante variable de entorno `LOG_PATH`.
- Excluir `backend/logs/` del control de versiones.
- Crear la skill `sindicato-logging-observabilidad` para guiar futuras implementaciones.

## Verificaciones

- `mvn test`: 100 tests, 0 fallos, 0 errores.
- Comprobado que se genera `backend/logs/sindicato-intelligence.log`.
- Comprobado que se genera `backend/logs/errors/sindicato-intelligence-error.log`.

## Nota operativa

En desarrollo local los logs se generan por defecto bajo `backend/logs` si el backend se arranca desde la carpeta `backend`. En Docker se debe montar un volumen hacia `/app/logs` o definir `LOG_PATH` a una ruta persistente.
