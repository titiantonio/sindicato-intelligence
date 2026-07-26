# Auditoría y preparación de la entrega TFM

## Fecha

25/07/2026.

## Objetivo

Revisar el proyecto completo frente a los requisitos de
`Documentacion-TFM-Fundae-1.pdf`, corregir incidencias verificables, actualizar
la documentación, preparar las slides y redactar el guion del vídeo de entrega.

## Contexto y correspondencia con el MVP

- Cierre transversal de las fases 0 a 12 del Documento 30.
- Sprint 15 del Documento 31: preparación de entrega TFM.
- La arquitectura DDD, Clean Architecture y monolito modular no se ha alterado.
- `Event` se mantiene como aggregate root principal.
- n8n conserva únicamente `WF-01`; `WF-02` a `WF-06` permanecen en Spring Boot.

## Requisitos de entrega revisados

- README completo.
- Código accesible en GitHub.
- Despliegue público recomendado.
- Slides mediante archivo o URL pública.
- Vídeo obligatorio con voz y captura de pantalla.
- Fecha límite efectiva corregida posteriormente por el autor a 24/08/2026.

## Incidencias encontradas y decisiones

1. El repositorio remoto estaba privado durante la auditoría inicial. El
   26/07/2026 se verificó su cambio a visibilidad pública y acceso sin sesión.
2. No existen todavía URL públicas de slides ni vídeo.
3. Tres pruebas backend dependían de eventos residuales en PostgreSQL. Se
   aislaron los datos previos dentro de la transacción de prueba.
4. El diálogo manual de Telegram no tenía nombre accesible efectivo. Se enlazó
   su cabecera al identificador ARIA proporcionado por PrimeNG.
5. Dos pruebas Playwright necesitaban reflejar el recorrido real: timeout del
   itinerario ADMIN y nombre del diálogo.
6. Las dependencias de producción del frontend quedaron sin vulnerabilidades.
   Las nueve alertas restantes pertenecen solo a la cadena de desarrollo de
   Karma; `npm audit` propone una corrección forzada incompatible y no se aplicó.
7. Se eliminó `backend/backend-startup.log`, un log generado y ya obsoleto. El
   archivo puede recuperarse desde el historial Git anterior.
8. Se añadió una guía E2E que faltaba aunque el README la referenciaba.
9. No se añadió licencia porque requiere una decisión de titularidad y política
   de publicación del autor.

## Archivos principales modificados

### Código y configuración

- `.gitignore`.
- `backend/pom.xml`.
- `backend/src/main/resources/application.yml`.
- `backend/src/main/resources/application-prod.yml`.
- pruebas de `DashboardController` y `EventController`.
- `frontend/package.json`.
- `frontend/package-lock.json`.
- `frontend/src/app/features/publications/publications-page.component.html`.
- pruebas Playwright `admin.mock.spec.ts` y `visual-system.mock.spec.ts`.
- `frontend/e2e/README.md`.

### Documentación

- `README.md`.
- `CHANGELOG.md`.
- `docs/00-agent-context.md`.
- `docs/indice_documentacion.md`.
- `docs/guia_ejecucion_tfm.md`.
- `docs/Documentacion_Final/Manual_Operativo_Usuario.md`.
- Documentos 12, 13, 14, 15, 30 y 31.
- `docs/Documentacion_Final/2026_07_25_informe_preparacion_entrega_tfm.md`.
- `docs/Documentacion_Final/2026_07_25_guion_video_tfm.md`.

### Presentación

- `slides/sindicato_intelligence_tfm.pptx`.
- `slides/sindicato_intelligence_tfm.pdf`.
- `slides/tfm_presentacion.html`.
- `slides/sindicato_intelligence_tfm_preview.png`.
- `slides/generate_presentation.mjs`.
- `slides/assets/`.
- `slides/README.md`.

## Versionado

- Backend: `0.0.119-SNAPSHOT` a `0.0.120-SNAPSHOT`.
- Frontend: `0.0.47` a `0.0.48`.
- OpenAPI sincronizado con la versión backend.
- `CHANGELOG.md` actualizado siguiendo Keep a Changelog.

## Pruebas y verificaciones

| Verificación | Resultado |
| --- | --- |
| `mvn test` | 347 pruebas, 0 fallos, 0 errores, 0 omitidas |
| `npm.cmd test -- --watch=false --browsers=ChromeHeadless` | 163/163 |
| `npm.cmd run build` | correcto; bundle inicial 542,77 kB |
| `npm.cmd run e2e:mock` | 16/16 |
| `npm.cmd audit --omit=dev --audit-level=low` | 0 vulnerabilidades |
| auditoría npm completa | 9 altas solo en herramientas Karma de desarrollo |
| `docker compose config --quiet` | correcto |
| `n8n/validate-workflows.ps1` | correcto; solo WF-01 |
| análisis sintáctico de scripts PowerShell | correcto |
| `tfm-start.ps1` con build limpio | correcto |
| `tfm-check.ps1` | PostgreSQL, backend, frontend, n8n, MailHog y WF-01 correctos |
| escaneo de patrones de secretos en archivos actuales | sin coincidencias |
| escaneo dirigido del historial `main` | sin patrones Google/Gemini, OpenAI, GitHub, AWS ni Telegram |
| enlaces locales de documentación de entrega | correctos |
| PPTX | 10 slides, 10 notas con fuentes y 0 objetos fuera del lienzo |
| PDF | 10 páginas 16:9 |
| HTML | 10 slides, JavaScript válido y assets presentes |

El stack Docker se detuvo de forma controlada después del smoke.

## Pendientes externos actualizados el 26/07/2026

- Confirmar y subir al repositorio público los cambios locales de entrega.
- Publicar y verificar la URL de las slides.
- Grabar, publicar y enlazar el vídeo.
- Mantener activos `Secret Protection` y `Push protection` en GitHub como
  controles continuos.
- Añadir las URL finales al README y enviar el formulario.

## Resultado

El código, la documentación y los materiales locales quedan verificados como
candidatos de entrega. La fecha límite efectiva es el 24/08/2026 y el
repositorio ya es público. El Sprint 15 permanece abierto por la publicación de
los cambios, las slides, el vídeo y el formulario.
