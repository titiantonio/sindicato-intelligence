---
name: sindicato-spring-backend-ddd
description: Usar para implementar o revisar backend Spring Boot, DDD, Clean Architecture, Modular Monolith, casos de uso, dominio, infraestructura, controllers y DTOs del proyecto sindicato-intelligence. Activa esta skill ante cualquier cambio Java, Spring, API backend o logica de negocio.
---

# Sindicato Spring Backend DDD

## Proposito

Guia la implementacion backend para mantener separacion estricta entre dominio, aplicacion, infraestructura y API.

## Documentacion a revisar

- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 18 – Estructura Spring Boot.md`.
- `docs/Documentacion Proyecto/Documento 19 – Diseño de Casos de Uso (Application Layer).md`.
- `docs/Documentacion Proyecto/Documento 17 - Modelo de Dominio (DDD).md`.
- `docs/Documentacion Proyecto/Documento 21 – Convenciones de Desarrollo.md`.

## Reglas de implementacion

- Usa package base `es.sindicato.intelligence` para codigo nuevo.
- Mantiene estructura por modulo: `domain`, `application`, `infrastructure`, `api`.
- Domain no depende de Spring, JPA, HTTP ni DTOs.
- Application contiene casos de uso con una accion de negocio.
- Infrastructure implementa persistencia e integraciones externas.
- API expone controllers, requests y responses sin logica de negocio.

## Checklist antes de finalizar

- No se devuelven entidades JPA ni entidades de dominio desde controllers.
- Las reglas de negocio pasan por casos de uso.
- Las excepciones heredan de `BusinessException` o `TechnicalException`.
- Si hay cambio de codigo, actualizar version Maven, `CHANGELOG.md` y documentacion del asistente.
