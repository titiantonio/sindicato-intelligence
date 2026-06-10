# T10.3 Proteccion de endpoints

## Fecha

2026-06-10

## Objetivo

Proteger endpoints con autenticacion JWT y autorizacion por roles segun matriz acordada.

## Contexto

Se revisaron Documento 31 (T10.3), Documento 13 (seguridad y roles), Documento 12 (auth login publico), y se aplico la matriz acordada: `EDITOR` para consulta de noticias/eventos y flujo editorial/publicacion; `ADMIN` para endpoints tecnicos y administracion.

## Fase MVP

Sprint 10: Seguridad.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/core/config/SecurityConfig.java`
- `backend/src/test/java/es/sindicato/intelligence/core/config/SecurityConfigTest.java`
- `CHANGELOG.md`

## Decisiones

- Seguridad stateless con JWT Resource Server.
- Endpoints publicos: `GET /api/v1/health` y `POST /api/v1/auth/login`.
- Endpoints solo `ADMIN`: `sources`, `news/bulk`, `classifications`, `analysis`, `events/detect`.
- Endpoints `ADMIN|EDITOR`: `news` consulta, `events` consulta, `content`, `publications`.
- Conversor de authorities desde claim JWT `roles` con prefijo `ROLE_`.

## Pruebas o verificaciones

- Verificado con `mvn "-Dtest=SecurityConfigTest" test`: 4 tests, 0 fallos, 0 errores.
- Se corrigio un primer intento de test que dependia de PostgreSQL; se adapto a `@WebMvcTest` con mocks para aislar seguridad HTTP.
