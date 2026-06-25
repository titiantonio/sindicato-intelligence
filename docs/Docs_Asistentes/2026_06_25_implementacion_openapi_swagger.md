# Fecha

2026-06-25

# Objetivo

Implementar OpenAPI/Swagger en el backend Spring Boot por perfil, manteniendo la documentacion disponible en desarrollo y deshabilitada por defecto en produccion.

# Contexto

La integracion corresponde a una mejora tecnica prevista en la Fase 1 del `Documento 30 - MVP Tecnico Ejecutable`, donde OpenAPI aparece como dependencia base del backend. El Sprint 12 ya estaba cerrado, por lo que se registra como mantenimiento tecnico posterior sin cambios de arquitectura.

# Fase MVP

Fase 1: backend base.

# Archivos modificados

- `backend/pom.xml`
- `backend/src/main/java/es/sindicato/intelligence/core/config/OpenApiConfig.java`
- `backend/src/main/java/es/sindicato/intelligence/core/config/SecurityConfig.java`
- `backend/src/main/resources/application.yml`
- `backend/src/main/resources/application-prod.yml`
- `backend/src/test/java/es/sindicato/intelligence/core/config/SecurityConfigTest.java`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

# Decisiones

- Usar `springdoc-openapi-starter-webmvc-ui` para Spring Boot 3.x.
- Exponer Swagger UI y `/v3/api-docs` en desarrollo/local mediante propiedades.
- Deshabilitar Swagger UI y `/v3/api-docs` por defecto en `application-prod.yml`.
- Documentar seguridad Bearer JWT global en OpenAPI sin modificar contratos REST existentes.

# Pruebas o verificaciones

- `mvnw.cmd "-Dtest=SecurityConfigTest" test`: OK, 7 tests.
- `mvnw.cmd -DskipTests compile`: OK.

