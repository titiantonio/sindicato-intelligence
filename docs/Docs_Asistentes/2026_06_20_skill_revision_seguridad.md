# 2026-06-20 - Skill transversal de revision de seguridad

## Objetivo

Crear una skill transversal para auditorias de seguridad del proyecto y alinear `AGENTS.md` con las reglas de seguridad operativas del MVP.

## Contexto

La intervencion parte de `docs/Promt/seguridad.md`, que proponia una skill de revision de seguridad completa. Se adapto el alcance al estado real del proyecto:

- Telegram es el canal de publicacion activo del MVP.
- Facebook y X quedan fuera del alcance operativo salvo decision futura explicita.
- `sindicato-api-security` se mantiene como skill especifica para API, JWT, roles y endpoints.
- La nueva skill cubre auditorias transversales de backend, frontend, PostgreSQL, Flyway, n8n, IA, infraestructura, secretos, Telegram, auditoria y privacidad.

## Archivos modificados

- `skills/sindicato-security-review/SKILL.md`
- `AGENTS.md`
- `docs/Docs_Asistentes/2026_06_20_skill_revision_seguridad.md`

## Decisiones tomadas

- Crear una skill nueva llamada `sindicato-security-review`, siguiendo el patron `sindicato-*` del proyecto.
- Mantener `sindicato-api-security` sin cambios para revisiones especificas de API REST y Spring Security.
- Incorporar en `AGENTS.md` reglas transversales compatibles con Documento 13, Documento 30 y Documento 31.
- No modificar `pom.xml` ni `CHANGELOG.md`, al tratarse de una intervencion documental y de configuracion de agentes, sin cambios de codigo de aplicacion.

## Fase MVP

Mantenimiento documental y operativo posterior a Sprint 10 Seguridad y Sprint 12 Observabilidad/Configuracion.

## Verificaciones

- Validado el frontmatter YAML de `skills/sindicato-security-review/SKILL.md`.
- Revisada la coherencia documental con Documento 13, Documento 30 y Documento 31.
- Confirmado que Telegram queda como canal MVP activo y Facebook/X como fuera de alcance operativo salvo decision futura explicita.

## Pruebas

No se ejecutan pruebas backend ni frontend porque la intervencion no modifica codigo de aplicacion.
