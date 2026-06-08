# Colores en consola Logback

## Fecha

2026-06-08

## Objetivo

Aclarar el comportamiento de rotacion diaria de logs y añadir colores por nivel en la salida de consola.

## Contexto

- Fase MVP: Fase 1 backend base como mantenimiento transversal de logging.
- El archivo `sindicato-intelligence.log` es el archivo activo; los historicos se archivan por dia y por tamaño bajo `logs/archive/yyyy-MM/`.
- Los colores deben aplicarse solo a consola para no guardar codigos ANSI en archivos persistidos.

## Archivos modificados

- `backend/src/main/resources/logback-spring.xml`
- `backend/src/main/resources/application.yml`
- `backend/pom.xml`
- `CHANGELOG.md`
- `skills/sindicato-logging-observabilidad/SKILL.md`
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`
- `docs/Docs_Asistentes/2026_06_08_colores_consola_logback.md`

## Decisiones

- Separar `CONSOLE_LOG_PATTERN` y `FILE_LOG_PATTERN`.
- Usar el conversor `%clr` de Spring Boot para colorear el nivel en consola.
- Activar ANSI por defecto con `SPRING_OUTPUT_ANSI_ENABLED=always`, permitiendo desactivarlo con `SPRING_OUTPUT_ANSI_ENABLED=never` si una terminal o runtime no lo necesita.
- Mantener ficheros generales y de error sin color para facilitar busqueda, compresion y procesamiento.
- Mantener la rotacion existente diaria y por tamaño con retencion de 90 dias.

## Pruebas o verificaciones

- Ejecutado `mvn test` desde `backend`.
- Resultado: `Tests run: 101, Failures: 0, Errors: 0, Skipped: 0`.
- Resultado Maven: `BUILD SUCCESS`.
