# Informe final de entrega TFM

**Fecha de auditoría:** 25/07/2026
**Estado corregido:** 29/07/2026
**Proyecto:** Sindicato Intelligence
**Repositorio:** <https://github.com/titiantonio/sindicato-intelligence>
**Requisitos revisados:** `Documentacion-TFM-Fundae-1.pdf`

## 1. Veredicto ejecutivo

**El proyecto fue entregado mediante el formulario oficial el 29/07/2026.**

Antes del envío se validaron el repositorio, las slides y las credenciales de
demostración. El vídeo se adjuntó directamente en el formulario y su URL no
forma parte de la documentación del repositorio.

Las slides cumplen el requisito como PPTX y PDF dentro del repositorio y como
presentación HTML pública en GitHub Pages:
<https://titiantonio.github.io/sindicato-intelligence/slides/tfm_presentacion.html>.

El despliegue en una URL pública es recomendable, no obligatorio según el documento recibido. La ejecución local con Docker está documentada y es reproducible.

La fecha límite efectiva confirmada por el autor es el **24/08/2026**.

## 2. Requisitos formales

| Requisito | Evidencia actual | Estado |
| --- | --- | --- |
| README completo | descripción, stack, instalación, estructura, funcionalidades y credenciales | Cumplido |
| Código accesible | repositorio público verificado el 26/07/2026 | Cumplido |
| Aplicación desplegada | ejecución local reproducible con Docker | No incluida; no obligatoria |
| Slides | PPTX y PDF en el repositorio; HTML público en GitHub Pages | Cumplido y verificado |
| Vídeo con voz y pantalla | adjuntado mediante el formulario oficial | Cumplido |
| Credenciales de prueba | `admin@sindicato.es` / `Admin@12345` y `editor@sindicato.es` / `Admin@12345` | Cumplido |
| Enlaces en documentación | README con repositorio y slides públicas | Cumplido |

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
| 5 Vídeo con explicación y pantalla | adjuntado mediante el formulario oficial |
| Documentación dentro del código | `README.md`, `docs/` e índice documental |
| Información de despliegue | README, guía Docker y Documento 14 |
| Slides dentro del código | documentos y fuente reproducible en `slides/` |
| Formulario | enviado con los datos, enlaces, credenciales y vídeo requeridos |

El repositorio devuelve HTTP 200 sin sesión y GitHub informa visibilidad
`public`. El commit de preparación `00e6fd4` está publicado en `main`. La
presentación de GitHub Pages y el PDF público devolvían HTTP 200 el 26/07/2026.

## 3. Estado técnico auditado

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

## 4. Documentación actualizada

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
- registro final de validación y entrega;
- presentación editable, PDF y HTML.

## 5. Elementos excluidos de la entrega

### Archivo retirado

- `backend/backend-startup.log`: log generado de un arranque antiguo fallido. No es código ni documentación y podía confundir al evaluador.

También se han añadido `*.log` y `tmp/` a `.gitignore`.

### Archivos locales y datos sensibles excluidos

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

La documentación histórica y `docs/Docs_Asistentes/` se conservan como
trazabilidad técnica, pero el README y `docs/indice_documentacion.md` son los
puntos de entrada para la evaluación.

## 6. Tabla final de verificación

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
| Slides públicas | Sí; GitHub Pages verificado el 26/07/2026 |
| Vídeo | Adjuntado mediante el formulario oficial |
| Credenciales de demostración | Validadas antes del envío |
| Formulario oficial | Enviado el 29/07/2026 |

## 7. Decisión de entrega

El código, la documentación y las slides cumplen los requisitos revisados. El
vídeo se adjuntó mediante el formulario, los enlaces y credenciales fueron
validados y la entrega formal está completada.
