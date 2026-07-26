# Informe de preparación de entrega TFM

**Fecha de auditoría:** 25/07/2026
**Estado corregido:** 26/07/2026
**Proyecto:** Sindicato Intelligence
**Repositorio:** <https://github.com/titiantonio/sindicato-intelligence>
**Requisitos revisados:** `Documentacion-TFM-Fundae-1.pdf`

## 1. Veredicto ejecutivo

**El proyecto está técnicamente verificado y el repositorio ya es público, pero
todavía no está listo para enviar el formulario de entrega.**

Bloqueos obligatorios:

1. los cambios locales de entrega deben confirmarse y publicarse en `main`;
2. el vídeo obligatorio todavía debe grabarse y publicarse;
3. las URL definitivas deben incorporarse al README y al formulario.

Las slides ya cumplen materialmente el requisito al estar preparadas como
PPTX y PDF dentro del repositorio. Cuando se publique el commit, la página del
PDF en GitHub podrá utilizarse como URL pública; GitHub Pages es una mejora
opcional para visualizar la variante HTML.

El despliegue en una URL pública es recomendable, no obligatorio según el documento recibido. La ejecución local con Docker está documentada y es reproducible.

La fecha límite efectiva confirmada por el autor es el **24/08/2026**.

## 2. Requisitos formales

| Requisito | Evidencia actual | Estado |
| --- | --- | --- |
| README completo | descripción, stack, instalación, estructura, funcionalidades y credenciales | Cumplido |
| Código accesible | repositorio público verificado el 26/07/2026 | Cumplido |
| Aplicación desplegada | no hay URL pública verificada; existe Docker local | Recomendado pendiente |
| Slides | PPTX, PDF y HTML en `slides/`; el PDF tendrá URL pública al subir el commit | Cumplido localmente; pendiente publicar cambios |
| Vídeo con voz y pantalla | guion preparado; falta grabación y URL | Bloqueante |
| Credenciales de prueba | `admin@sindicato.es` / `Admin@12345` y `editor@sindicato.es` / `Admin@12345` | Cumplido |
| Enlaces en documentación | README preparado para centralizarlos | Parcial hasta disponer de URL |

### Desglose exacto del PDF de requisitos

| Apartado del PDF | Evidencia |
| --- | --- |
| 1.a Descripción general | `README.md`, secciones «Problema y solución» y «Arquitectura» |
| 1.b Stack tecnológico | `README.md`, sección «Stack tecnológico» |
| 1.c Instalación y ejecución | `README.md` y `docs/guia_ejecucion_tfm.md` |
| 1.d Estructura | `README.md`, sección «Estructura del repositorio» |
| 1.e Funcionalidades | `README.md`, sección «Funcionalidades principales» |
| 1.f Usuario y contraseña | `README.md`, sección «Usuarios de demostración» |
| 2 Código fuente | repositorio público en GitHub |
| 3 Despliegue recomendado | ejecución Docker completa; sin URL pública |
| 4 Slides | PPTX, PDF y HTML incluidos en `slides/` |
| 5 Vídeo con explicación y pantalla | guion completo; grabación y URL pendientes |
| Documentación dentro del código | `README.md`, `docs/` e índice documental |
| Información de despliegue | README, guía Docker y Documento 14 |
| Slides dentro del código | documentos y fuente reproducible en `slides/` |
| Formulario | checklist preparada; nombre, email y URL finales debe completarlos el autor |

El repositorio devuelve HTTP 200 sin sesión y GitHub informa visibilidad
`public`. El commit remoto `main` coincide con el `HEAD` local previo a la
auditoría, por lo que los cambios actuales todavía deben confirmarse y subirse.

## 3. Comparación con el TFM de referencia

El repositorio de referencia del compañero usa un patrón correcto y fácil de evaluar:

- README como portada de la entrega;
- enlaces visibles a slides y vídeo al comienzo;
- presentación HTML versionada;
- GitHub Pages para servir la presentación;
- explicación de problema, solución, arquitectura, IA, pruebas, seguridad e instalación.

La entrega de Sindicato Intelligence adopta ese formato sin copiar contenido:

- README actualizado como punto de entrada;
- slides PowerPoint editables;
- variante HTML apta para GitHub Pages;
- guion específico de vídeo;
- índice que separa documentación vigente de histórica.

## 4. Estado técnico auditado

### Backend

- Java 21 y Spring Boot 3.5.14.
- DDD, Clean Architecture y monolito modular.
- PostgreSQL y Flyway con dos migraciones consolidadas.
- JWT, refresh, recuperación, contraseña temporal y roles.
- Automatizaciones `WF-02` a `WF-06` en Spring Boot.
- IA determinista local y Gemini configurable.
- publicación Telegram inmediata, programada y manual.

Pruebas:

- 347 pruebas ejecutadas;
- tres pruebas de controlador dependían de eventos residuales de PostgreSQL;
- se corrigió su aislamiento archivando temporalmente los eventos previos dentro de la transacción de prueba;
- la reejecución focal de 10 pruebas pasó sin fallos;
- la batería completa terminó con 347 pruebas, 0 fallos, 0 errores y 0 omitidas.

### Frontend

- Angular 21.2, PrimeNG 21 y Tailwind CSS 4.
- rutas operativas de login, dashboard, noticias, eventos, contenido, publicaciones, fuentes, usuarios, auditoría y settings.
- roles y guards.
- diseño responsive, tema claro/oscuro y controles de accesibilidad.

Verificaciones realizadas:

- 163 pruebas Karma/Jasmine correctas;
- build de producción correcto;
- `npm audit --omit=dev`: 0 vulnerabilidades;
- quedan 9 vulnerabilidades altas solo de desarrollo, transitivas desde Karma a `brace-expansion` 1.x;
- no se acepta `npm audit fix --force` porque propone un cambio incompatible;
- el diálogo de mensaje manual se corrigió para tener nombre accesible real;
- las 16 pruebas Playwright mockeadas pasan.

### n8n

- solo se versiona `WF-01-Capture-News`;
- el validador estático pasa;
- `WF-02` a `WF-06` no deben recrearse en n8n.

### Docker e infraestructura

- `docker compose config --quiet` correcto;
- scripts de arranque, comprobación, parada y reset disponibles;
- scripts PowerShell validados sintácticamente;
- construcción limpia de las imágenes backend y frontend correcta;
- `tfm-check.ps1` confirma PostgreSQL, backend, frontend, n8n, MailHog y
  `WF-01-Capture-News`;
- el stack se detuvo de forma controlada después del smoke;
- no hay despliegue público verificado;
- CI/CD, TLS, secretos productivos y backups probados siguen pendientes de producción.

## 5. Documentación actualizada

- `README.md`;
- `docs/00-agent-context.md`;
- `docs/indice_documentacion.md`;
- `docs/guia_ejecucion_tfm.md`;
- `docs/Documentacion_Final/Manual_Operativo_Usuario.md`;
- Documento 12 - API REST;
- Documento 13 - Seguridad y roles;
- Documento 14 - DevOps e infraestructura;
- Documento 15 - Plan de pruebas;
- Documento 30 - estado de ejecución;
- Documento 31 - Sprint 15 de entrega;
- guía de pruebas E2E;
- guion de vídeo;
- checklist operativa de publicación y cierre;
- presentación editable, PDF y HTML.

## 6. Qué retirar

### Retirado

- `backend/backend-startup.log`: log generado de un arranque antiguo fallido. No es código ni documentación y podía confundir al evaluador.

También se han añadido `*.log` y `tmp/` a `.gitignore`.

### No debe entrar nunca en la entrega

- `.env`;
- tokens, API keys o contraseñas reales;
- `node_modules/`;
- `backend/target/`;
- `frontend/dist/`;
- `frontend/test-results/`;
- `frontend/playwright-report/`;
- logs locales;
- adjuntos reales de Telegram;
- dumps de base de datos con datos personales.

Estos elementos deben comprobarse antes de publicar.

### Opcional: simplificación documental

No se recomienda borrar ahora documentación histórica porque explica la evolución y existen reglas de trazabilidad. Si se quiere una entrega más limpia:

- mover versiones sustituidas a `docs/historico/`;
- mantener el índice documental como entrada;
- revisar todos los enlaces después del movimiento.

`CLAUDE.md` solo remite a `AGENTS.md` y puede retirarse en una limpieza posterior si ninguna herramienta lo necesita. No es un bloqueo.

`docs/Docs_Asistentes/` contiene registros exigidos por el proyecto. Debe mantenerse mientras siga vigente `AGENTS.md`, aunque no sea documentación principal del evaluador.

## 7. Qué falta añadir o completar

### Obligatorio

- Publicar en el repositorio público los cambios locales de preparación.
- Verificar la URL pública del PDF de slides en GitHub o, alternativamente,
  habilitar GitHub Pages para la versión HTML.
- vídeo de 5 a 10 minutos con voz y captura de pantalla.
- URL pública del vídeo.
- envío del formulario.

### Recomendado

- decidir si el repositorio público tendrá una licencia de software explícita;
- despliegue temporal accesible para el evaluador;
- GitHub Actions con backend, frontend, Playwright y validadores;
- release o tag final después de completar enlaces;

La licencia no debe añadirse sin decidir antes si la publicación es solo
académica o de código abierto.

## 8. Seguridad antes de publicar los cambios finales

1. ejecutar un escaneo final de secretos en archivos rastreados;
2. mantener activo el secret scanning de GitHub como complemento al escaneo
   dirigido ya realizado sobre el historial de `main`;
3. confirmar que `.env` no está rastreado;
4. mantener solo credenciales claramente identificadas como demostración;
5. comprobar que screenshots y vídeos no muestran tokens;
6. revisar settings, Swagger y n8n antes de grabar;
7. rotar cualquier secreto que haya estado expuesto.

## 9. Orden recomendado para cerrar la entrega

1. revisar y confirmar los cambios con `git diff`;
2. decidir la política de licencia;
3. crear y subir el commit con los materiales finales;
4. verificar la URL pública del PDF de slides y, opcionalmente, habilitar
   GitHub Pages para `slides/tfm_presentacion.html`;
5. grabar el vídeo siguiendo el guion;
6. subir el vídeo como público u oculto accesible por enlace;
7. añadir las URL definitivas al README;
8. crear un tag o release final;
9. verificar todo antes del 24/08/2026;
10. rellenar y enviar el formulario.

## 10. Tabla final de verificación

| Verificación | Resultado |
| --- | --- |
| Backend `mvn test` | 347/347 correctas |
| Frontend unitario | 163/163 correctas |
| Frontend build | Correcto; bundle inicial 542,77 kB |
| Playwright mock | 16/16 correctas |
| Dependencias de producción | 0 vulnerabilidades |
| Docker Compose config | Correcto |
| Workflow n8n | Correcto |
| Scripts PowerShell | Correctos |
| Smoke stack completo | Correcto |
| Secretos en archivos actuales e historial `main` | Sin patrones comunes de alta entropía |
| Enlaces locales de entrega | Correctos |
| PPTX / PDF / HTML | 10 slides verificadas |
| Repositorio público | Sí; verificado el 26/07/2026 |
| Slides públicas | No |
| Vídeo público | No |

## 11. Decisión de entrega

El código, la documentación y los materiales locales son candidatos de entrega:
todas las verificaciones técnicas previstas están en verde. La entrega formal
solo estará lista cuando los cambios locales estén publicados y existan URL
públicas verificadas de slides y vídeo.
