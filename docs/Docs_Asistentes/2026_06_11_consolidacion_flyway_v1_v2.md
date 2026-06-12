# 2026-06-11 - Consolidacion Flyway V1-V2 para arranque limpio

## Objetivo

Consolidar migraciones Flyway en fase de implementacion temprana, aceptando reset de BBDD, para reducir complejidad inicial y dejar la secuencia base en `V1`, `V2` y `V3`.

## Contexto y fase MVP

- Documento 30: ajuste tecnico de base de datos durante implementacion del MVP.
- Documento 31: backlog vivo actualizado con nota de consolidacion.
- Decision operativa: consolidacion permitida porque el equipo confirma que no hay restriccion de reset de base de datos en esta etapa.

## Cambios realizados

- `backend/src/main/resources/db/migration/V1__create_mvp_schema.sql`
  - Se integra la tabla `password_reset_tokens` con sus constraints e indices.

- `backend/src/main/resources/db/migration/V2__seed_admin_user.sql`
  - Se integra la semilla del usuario tecnico `n8n@sindicato.es`.
  - Se mantiene comentario explicito de password semilla `Admin@123`.

- `backend/src/main/resources/db/migration/V4__seed_n8n_service_user.sql`
  - Eliminada por consolidacion en `V2`.

- `backend/src/main/resources/db/migration/V5__create_password_reset_tokens.sql`
  - Eliminada por consolidacion en `V1`.

- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`
  - Actualizadas notas para reflejar que la secuencia vigente de arranque queda en `V1..V3` y que lo previo de `V4/V5` queda absorbido.

- `CHANGELOG.md`
  - Actualizado para registrar la consolidacion Flyway.

- `backend/pom.xml`
  - Version incrementada a `0.0.34-SNAPSHOT`.

## Decisiones tecnicas

- Se adopta consolidacion de migraciones solo para fase temprana con reset permitido.
- En entornos persistentes (preproduccion/produccion), no se reescribiran migraciones aplicadas.
- Se conserva la credencial semilla unificada `Admin@123` en seeds, documentada con comentario.

## Verificaciones

- Verificacion estructural de archivos editados sin errores de diagnostico en VS Code.
- Pendiente de aplicacion efectiva en BBDD: reset de volumen/instancia y re-ejecucion de Flyway desde cero.

## Notas operativas

- Esta consolidacion requiere reconstruir la base de datos de desarrollo para evitar desalineacion de `flyway_schema_history`.
- No aplicar esta estrategia en bases ya estabilizadas sin plan de migracion controlado.
