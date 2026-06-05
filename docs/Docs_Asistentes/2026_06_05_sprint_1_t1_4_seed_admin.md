# Sprint 1 T1.4 - Seed inicial de usuario ADMIN

## Fecha

2026-06-05

## Objetivo

Crear los datos iniciales del Sprint 1 mediante una migracion Flyway con el usuario `ADMIN` requerido por la T1.4 del Documento 31.

## Contexto

La tarea seleccionada en el Documento 31 fue:

- Sprint 1 - Modelo de Datos MVP.
- T1.4 - Crear seeds iniciales en la tabla `users` para el usuario `ADMIN`.

El Sprint 1 ya tenia completadas T1.1, T1.2 y T1.3, por lo que al finalizar T1.4 se cerro el Sprint 1 completo.

## Fase MVP relacionada

Documento 30, Fase 2: Modelo de Datos.

## Archivos modificados

- `backend/src/main/resources/db/migration/V3__seed_data.sql`.
- `backend/pom.xml`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.
- `docs/Docs_Asistentes/2026_06_05_sprint_1_t1_4_seed_admin.md`.

## Decisiones tomadas

- Se creo una nueva migracion Flyway `V3__seed_data.sql`; no se modificaron V1 ni V2 porque ya estaban aplicadas.
- Se inserto el usuario inicial `admin@sindicato.es` con rol `ADMIN`, estado activo, nombre `Admin Sindicato`y Password `Cambiar123!`
- Se guardo `password_hash` con BCrypt, sin almacenar contrasena en texto plano en la base de datos.
- Se uso `ON CONFLICT (email) DO NOTHING` para evitar sobrescribir un ADMIN existente en otros entornos.
- Se incremento la version backend a `0.0.3-SNAPSHOT`.
- Se registro el cambio en `CHANGELOG.md`.

## Documento 31 actualizado

- `[x] T1.4`: seed inicial de usuario ADMIN completado.
- `[x] Sprint 1`: marcado como completado porque T1.1, T1.2, T1.3 y T1.4 estan finalizadas y verificadas.

## Pruebas y verificaciones

Verificaciones realizadas:

- `mvnw test`: `BUILD SUCCESS`, 1 test ejecutado, 0 fallos.
- Flyway valido 3 migraciones y aplico `V3__seed_data.sql` correctamente.
- Consulta de `flyway_schema_history`: V1, V2 y V3 aparecen con `success = true`.
- Consulta de `users`: existe `admin@sindicato.es`, rol `ADMIN`, `active = true` y `password_hash` con formato BCrypt.

Resultado: Sprint 1 finalizado correctamente.
