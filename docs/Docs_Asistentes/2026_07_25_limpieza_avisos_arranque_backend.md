# Fecha

2026-07-25

# Objetivo

Eliminar avisos de consola en el arranque de desarrollo del backend sin alterar el funcionamiento efectivo de autenticacion ni persistencia.

# Contexto

El backend mostraba dos avisos esperados:

- Spring Security detectaba un `DaoAuthenticationProvider` manual y avisaba de que no autoconfiguraria `UserDetailsService`.
- Spring Boot avisaba de que `spring.jpa.open-in-view` estaba habilitado por defecto.

# Fase MVP

Mantenimiento correctivo posterior al Sprint 12 sobre Fase 1 backend base, seguridad y configuracion de desarrollo.

# Archivos modificados

- `backend/src/main/resources/application.properties`
- `backend/pom.xml`
- `CHANGELOG.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`

# Decisiones

- Se mantiene `spring.jpa.open-in-view=true` de forma explicita para conservar el comportamiento efectivo previo y quitar el aviso de configuracion por defecto.
- Se ajusta solo el nivel del logger `InitializeUserDetailsBeanManagerConfigurer` a `ERROR`, porque el uso de `DaoAuthenticationProvider` manual es intencional y ya recibe el `UserDetailsService`.
- No se modifican filtros, roles, endpoints, contratos REST, casos de uso ni transacciones.
- Se incrementa la version backend a `0.0.117-SNAPSHOT`.

# Pruebas o verificaciones

- `mvnw.cmd -q test-compile` ejecutado en `backend/` con resultado OK.
- `mvnw.cmd -q "-Dtest=IntelligenceApplicationTests" test` ejecutado en `backend/` con resultado OK.
- En el arranque de contexto ya no aparecen los avisos `WARN` de `InitializeUserDetailsBeanManagerConfigurer` ni `spring.jpa.open-in-view`.
- Se conserva el `INFO` esperado de Spring Security indicando que el `AuthenticationManager` usa el `DaoAuthenticationProvider` configurado.
