# Nota configuracion IA ADMIN backoffice

## Fecha

2026-06-07

## Objetivo

Registrar en el backlog operativo que la seleccion de proveedor IA por usuario `ADMIN` queda pendiente para el backoffice Angular.

## Contexto

Se reviso que la arquitectura contempla proveedores IA intercambiables mediante `AIProvider` y que el diseño UX del backoffice contempla una pantalla de `Configuracion IA` solo para `ADMIN`. Para la primera implementacion del proveedor real externo se acuerda seleccionar el proveedor por configuracion tecnica del backend, dejando la seleccion administrable para una fase posterior.

## Fase MVP

Documento 30, Fase 11: frontend Angular / backoffice.

## Archivos modificados

- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementacion Detallado.md`.
- `docs/Docs_Asistentes/2026_06_07_nota_configuracion_ia_admin_backoffice.md`.

## Decisiones

- Se anadio la tarea `T11.9 Configuracion IA para ADMIN` dentro de Sprint 11.
- La tarea futura incluye seleccion de proveedor IA, modelo, temperatura, limite de tokens y version de prompt.
- Se explicita que inicialmente el proveedor IA se seleccionara por `application.yml` o variables de entorno.
- Se explicita que las API keys no deben guardarse en base de datos.
- No se actualizo `backend/pom.xml` porque no hubo cambios de codigo ni migraciones.

## Pruebas y verificaciones

- No se ejecutaron pruebas automatizadas porque el cambio es exclusivamente documental.
