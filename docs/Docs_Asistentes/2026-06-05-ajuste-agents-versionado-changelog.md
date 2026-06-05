# Ajuste de AGENTS: documentacion, versionado y changelog

## Fecha

2026-06-05

## Objetivo

Anadir reglas operativas a `AGENTS.md` para que el trabajo del asistente quede documentado y para definir obligaciones de versionado y changelog cuando se hagan cambios en el codigo.

## Contexto

El usuario solicito incorporar tres reglas nuevas:

- Documentar todo lo que haga el asistente dentro de `docs/Docs_Asistentes` en archivos Markdown.
- Incrementar la version del proyecto en `pom.xml` cada vez que se cambie codigo.
- Reflejar cada cambio en `CHANGELOG.md` siguiendo Keep a Changelog 1.1.0.

## Archivos Modificados

- `AGENTS.md`.
- `docs/Docs_Asistentes/2026-06-05-ajuste-agents-versionado-changelog.md`.

## Decisiones

- Se anadio un apartado `Documentacion del Trabajo del Asistente`.
- Se anadio un apartado `Versionado y Changelog`.
- No se modifico `backend/pom.xml` porque no hubo cambios en codigo.
- No se modifico `CHANGELOG.md` porque la regla nueva aplica a cambios de codigo y esta intervencion solo modifica documentacion/instrucciones.

## Verificacion

- Se verifico que `CHANGELOG.md` existe en la raiz del proyecto.
- Se verifico que el `pom.xml` actual esta en `backend/pom.xml`.
- No se ejecutaron pruebas porque no hubo cambios de codigo ejecutable.
