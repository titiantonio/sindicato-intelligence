Versión: 1.0

Estado: Backlog Operativo Oficial

---

# 1. Objetivo

Descomponer el Documento 30 (MVP Técnico Ejecutable) en tareas concretas para ser ejecutadas por agentes IA.

Cada tarea debe:

* Ser pequeña.
* Ser verificable.
* Tener un único objetivo.
* Poder completarse en una única sesión de trabajo.

---

# 2. Metodología

Flujo obligatorio:

```text
Seleccionar tarea
      ↓
Asignar rol
      ↓
Implementar
      ↓
Revisar
      ↓
Commit Git
      ↓
Siguiente tarea
```

---

# 3. Estado Actual

## Sprint 0

Completado.

---

Infraestructura operativa:

```text
✓ Docker

✓ PostgreSQL

✓ Spring Boot

✓ Flyway

✓ Health Endpoint
```

Nota posterior 2026-06-07: añadida configuracion transversal de logging backend con Logback, consola, archivo diario, archivo de errores, carpetas mensuales, compresion y retencion de 90 dias. Esta intervencion se registra como mantenimiento tecnico de Fase 1/backend base.

Nota posterior 2026-06-07: extendidos logs operativos a los casos de uso existentes de `source`, `news` y `event`, y añadido reintento/diagnostico en respuestas recuperables de Gemini sin texto o sin JSON.

Nota posterior 2026-06-08: verificada la intervencion transversal de logs y reintento Gemini con `mvn test` en backend: 101 tests ejecutados, 0 fallos, 0 errores.

Nota posterior 2026-06-08: ajustada la salida de consola de Logback para colorear niveles de log y activada salida ANSI configurable con `SPRING_OUTPUT_ANSI_ENABLED`, manteniendo los archivos persistidos sin codigos ANSI y conservando la rotacion diaria por tamaño.

---

# 4. [x] Sprint 1

# Modelo de Datos MVP

---

Objetivo:

Construir el esquema completo MVP.

---

## [x] T1.1

Crear migración:

```text
V1__create_mvp_schema.sql
```

---

Rol:

```text
spring-backend-architect / ia-workflow-architect
```

---

Documentos:

```text
20
21
30
```

---

Resultado:

```text
sources

news_articles

news_classifications

events

event_news

event_ai_analysis

generated_content

publications

users
```

---

## [x] T1.2

Validar:

```text
Primary Keys

Foreign Keys

Indexes

Unique Constraints
```

---

Rol:

```text
postgres-data-architect
```

---

## [x] T1.3

Verificar ejecución Flyway.

---

Resultado esperado:

```sql
\dt
```

Muestra todas las tablas MVP.

---

## [x] T1.4

Crear seeds iniciales.

---

Tabla:

```text
users
```

---

Usuario:

```text
ADMIN
```

---

Estado:

```text
Completado
```

Nota posterior 2026-06-07: consolidadas las migraciones Flyway iniciales para desarrollo. `V1__create_mvp_schema.sql` crea el esquema MVP completo, incluye `uk_sources_url` en `sources` e integra `event_news.confidence_score` con su check. `V2__seed_admin_user.sql` carga el usuario admin y `V3__seed_rss_sources.sql` carga las 54 fuentes RSS iniciales. La tabla tecnica `system_info` se elimina al no formar parte del modelo MVP.

Nota posterior 2026-06-11: nueva consolidacion de arranque para fase de implementacion con reset de BBDD permitido. `V1__create_mvp_schema.sql` incorpora `password_reset_tokens` (antes migracion separada) y `V2__seed_admin_user.sql` incorpora tambien la semilla de `n8n@sindicato.es` (antes migracion separada), quedando la secuencia operativa en `V1`, `V2` y `V3`.

---

# 5. [x] Sprint 2

# Módulo Source

---

Objetivo:

Gestionar fuentes.

---

## [x] T2.1

Crear estructura módulo.

---

Rol:

```text
spring-backend-architect
```

---

Crear:

```text
source

├── domain
├── application
├── infrastructure
└── api
```

---

## [x] T2.2

Crear entidad dominio.

---

Clase:

```java
Source
```

---

## [x] T2.3

Crear SourceRepository.

---

Interface dominio.

---

## [x] T2.4

Crear entidad JPA.

---

Clase:

```java
SourceEntity
```

---

## [x] T2.5

Crear JpaSourceRepository.

---

Implementación.

---

## [x] T2.6

Crear DTOs.

---

```java
CreateSourceRequest

SourceResponse
```

---

## [x] T2.7

Crear Use Case.

---

```java
CreateSourceUseCase
```

---

## [x] T2.8

Crear API REST.

---

Endpoints:

```http
GET /api/v1/sources

POST /api/v1/sources

PUT /api/v1/sources/{id}
```

---

## [x] T2.9

Crear tests.

---

Estado:

```text
Completado
```

---

# 6. [x] Sprint 3

# Módulo News

---

Objetivo:

Gestionar noticias.

---

## [x] T3.1

Crear módulo news.

---

## [x] T3.2

Entidad dominio.

```java
NewsArticle
```

---

## [x] T3.3

Repositorio dominio.

---

## [x] T3.4

Entidad JPA.

---

## [x] T3.5

Implementación repositorio.

---

## [x] T3.6

DTOs.

---

## [x] T3.7

CreateNewsUseCase.

---

## [x] T3.8

GetNewsUseCase.

---

## [x] T3.9

API REST.

---

Endpoints:

```http
POST /api/v1/news

GET /api/v1/news

GET /api/v1/news/{id}
```

---

## [x] T3.10

Tests.

---

# 7. [x] Sprint 4

# WF-01 Captura Noticias

---

Objetivo:

Conectar n8n con Spring Boot.

---

## [x] T4.1

Modificar flujo captura.

---

Cambio:

```text
n8n
 ↓
API
 ↓
Spring Boot
 ↓
PostgreSQL
```

---

## [x] T4.2

Crear endpoint ingestión.

---

## [x] T4.3

Normalización RSS.

---

## [x] T4.4

Detección duplicados.

---

## [x] T4.5

Pruebas integración.

---

Resultado esperado:

```text
Noticias entrando por API
```

Nota posterior: creado el workflow n8n exportable `n8n/workflows/wf_01_capture_news.json` para materializar WF-01 con ejecucion cada 30 minutos, lectura de fuentes RSS activas y envio de lote a `POST /api/v1/news/bulk`.

Nota posterior 2026-06-06: ajustado `n8n/workflows/wf_01_capture_news.json` para sustituir el nodo RSS nativo por `HTTP Request` con headers `User-Agent: Wget/1.21.1` y `Accept: */*`, respuesta como texto, parseo XML y normalizacion compatible con Atom Junta Andalucia y RSS estandar. No se cargan fuentes por Flyway en este ajuste; se revisaran aparte.

Nota posterior 2026-06-06: corregidos los Code nodes de `WF-01-Capture-News` para usar `$input.all()` en lugar de la variable `items`, evitando el error de tipado de n8n `Cannot find name 'items'`.

Nota posterior 2026-06-06: ajustadas las llamadas HTTP de los workflows n8n `WF-01`, `WF-02` y `WF-03` a `http://host.docker.internal:8080` para el entorno de desarrollo donde n8n corre en Docker y el backend Spring Boot corre en la maquina anfitriona.

Nota posterior 2026-06-06: corregido el nodo `Normalize RSS Items` de `WF-01-Capture-News` para aceptar estructuras XML parseadas por n8n con envoltorios `data`, `root`, `body`, `feed`, `rss` o `channel`, evitando que el nodo reciba items pero devuelva una salida vacia.

Nota posterior 2026-06-11: añadido al WF-01 un paso de autenticacion tecnica contra `POST /api/v1/auth/login` y cabeceras `Authorization: Bearer` en las llamadas a `GET /api/v1/sources` y `POST /api/v1/news/bulk` para resolver el `401 Unauthorized` al ejecutar el workflow desde n8n.

Nota posterior 2026-06-11: separada la cuenta tecnica de n8n del usuario humano `admin` mediante un usuario propio `n8n@sindicato.es`, con credenciales dedicadas en `database/docker-compose.yml` y seeding Flyway en migracion inicial consolidada (actualmente integrada en `V2__seed_admin_user.sql`).

---

# 8. [x] Sprint 5

# Clasificación IA

---

Objetivo:

Clasificar noticias.

---

## [x] T5.1

Crear módulo classification.

---

## [x] T5.2

Crear entidad dominio.

---

## [x] T5.3

Repositorio.

---

## [x] T5.4

Integración AIProvider.

Nota posterior 2026-06-07: implementado `GeminiAIProvider` como proveedor IA externo para clasificacion, activable por configuracion tecnica (`app.ai.provider=gemini`) y usando por defecto el modelo `models/gemma-4-31b-it`. `DeterministicAIProvider` se mantiene como proveedor por defecto para desarrollo local y tests. Si Gemini falla, el backend devuelve error claro sin fallback silencioso.

Nota posterior 2026-06-07: ajustado `GeminiAIProvider` para enviar `systemInstruction` y `responseSchema` en la peticion a Gemini, evitando respuestas que reformulan el prompt en lugar de devolver el JSON de clasificacion.

Nota posterior 2026-06-08: verificada la integracion IA y sus pruebas de seleccion/proveedor Gemini con `mvn test` en backend: 101 tests ejecutados, 0 fallos, 0 errores.

---

## [x] T5.5

Implementar prompt WF-02.

Nota posterior 2026-06-07: reforzado el prompt oficial WF-02 para que noticias con informacion insuficiente devuelvan JSON valido con categoria `OTROS` en vez de texto libre del proveedor IA.

Nota posterior 2026-06-07: enriquecido el prompt oficial WF-02 con criterios de relevancia, impacto y urgencia orientados al impacto laboral docente andaluz, manteniendo el contrato JSON del backend.

Nota posterior 2026-06-07: diferenciadas explicitamente en WF-02 las noticias fuera de ambito (`OTROS` / `FUERA_DE_AMBITO`) de las noticias con informacion insuficiente (`OTROS` / `INFORMACION_INSUFICIENTE`).

---

## [x] T5.6

Crear workflow n8n.

Nota posterior 2026-06-07: ajustado `WF-02-Classify-News` para ejecutarse por schedule cada 5 minutos y limitar la clasificacion a 10 noticias capturadas por ejecucion, evitando saturar el proveedor IA.

Nota posterior 2026-06-07: hecho robusto el filtro de noticias capturadas de `WF-02-Classify-News` para soportar respuestas API entregadas por n8n como array en un unico item o como items individuales.

Nota posterior 2026-06-07: reducido temporalmente `WF-02-Classify-News` a 1 noticia por ejecucion para evitar rafagas paralelas mientras se estabiliza la respuesta JSON del proveedor IA externo.

---

## [x] T5.7

Persistir clasificación.

Nota posterior 2026-06-07: añadida trazabilidad por logs al caso de uso de clasificacion para registrar inicio, exito, duplicados y fallos por noticia.

---

## [x] T5.8

Actualizar estado noticia.

---

# 9. [x] Sprint 6

# Eventos

---

Objetivo:

Agrupar noticias.

---

## [x] T6.1

Crear módulo event.

---

## [x] T6.2

Crear entidad Event.

---

## [x] T6.3

Crear EventRepository.

---

## [x] T6.4

Crear Aggregate Root.

---

## [x] T6.5

Crear workflow detección eventos.

---

## [x] T6.6

Integrar IA agrupación.

---

## [x] T6.7

Crear asociación noticia-evento.

Nota actualizada 2026-06-07: `confidence_score` queda integrado directamente en `event_news` dentro de `V1__create_mvp_schema.sql`, junto con la constraint `ck_event_news_confidence_score`.

---

# 10. [x] Sprint 7

# Análisis IA

---

Objetivo:

Generar conocimiento consolidado.

---

## [x] T7.1

Crear módulo analysis.

Nota posterior 2026-06-08: creado el modulo `analysis` con capas `domain`, `application`, `infrastructure` y `api`, entidad de dominio `EventAIAnalysis`, puerto `EventAIAnalysisRepository` y pruebas unitarias de dominio.

---

## [x] T7.2

Implementar GenerateAnalysisUseCase.

Nota posterior 2026-06-08: implementado `GenerateAnalysisUseCase` con contratos de aplicacion para IA de analisis, carga de evento/noticias, persistencia mediante puerto `EventAIAnalysisRepository` y logs operativos de inicio, contexto, exito, descartes y errores.

---

## [x] T7.3

Implementar Prompt WF-04.

Nota posterior 2026-06-08: implementado `GenerateAnalysisPromptBuilder` basado en el Prompt WF-04 oficial, proveedor determinista de analisis y `GeminiAnalysisAIProvider` con `systemInstruction`, `responseSchema`, diagnostico acotado y reintentos recuperables.

---

## [x] T7.4

Persistir análisis.

Nota posterior 2026-06-08: añadida persistencia JPA de `event_ai_analysis`, endpoint `POST /api/v1/analysis/generate`, DTOs REST, pruebas de repositorio/API y workflow n8n `WF-04-Generate-Analysis` para generar analisis por `eventId`.

Nota posterior 2026-06-08: Sprint 7 completado con T7.1, T7.2, T7.3 y T7.4 verificadas mediante pruebas especificas y suite completa backend `mvn test`: 116 tests ejecutados, 0 fallos, 0 errores.

---

# 11. [x] Sprint 8

# Contenido

---

Objetivo:

Generar contenido.

---

## [x] T8.1

Crear módulo content.

Nota posterior 2026-06-08: creado el modulo `content` con capas `domain`, `application`, `infrastructure` y `api`, entidad de dominio `GeneratedContent`, enum `ContentStatus`, puerto `GeneratedContentRepository` y pruebas unitarias de dominio.

---

## [x] T8.2

Implementar GenerateContentUseCase.

Nota posterior 2026-06-08: implementado `GenerateContentUseCase` con puerto `ContentAIProvider`, resolucion de evento y analisis, estado `PENDING_REVIEW`, logs operativos y `CurrentContentAuthorProvider` transitorio basado en `app.content.default-created-by` hasta disponer de modulo `user` o JWT real.

---

## [x] T8.3

Implementar Prompt WF-05.

Nota posterior 2026-06-08: implementado `GenerateContentPromptBuilder` basado en el Prompt WF-05 oficial, proveedor determinista de contenido y `GeminiContentAIProvider` con `systemInstruction`, `responseSchema`, diagnostico acotado y reintentos recuperables.

---

## [x] T8.4

Persistir contenido.

Nota posterior 2026-06-09: añadida persistencia JPA de `generated_content`, API `POST /api/v1/content/generate`, endpoints `POST /api/v1/content/{id}/approve` y `POST /api/v1/content/{id}/reject`, pruebas de repositorio/API y workflow n8n `WF-05-Generate-Content` para generar contenido por `eventId` y `analysisId` opcional.

Nota posterior 2026-06-09: Sprint 8 completado con T8.1, T8.2, T8.3 y T8.4 verificadas mediante pruebas especificas de contenido: 17 tests ejecutados, 0 fallos, 0 errores. WF-05 validado como JSON correcto.

Nota posterior 2026-06-09: cierre verificado con suite completa backend `mvn test`: 133 tests ejecutados, 0 fallos, 0 errores.

---

# 12. Sprint 9 [x]

# Publicación Telegram

---

Objetivo:

Publicar contenido.

---

## T9.1 [x]

Crear módulo publication.

Nota posterior 2026-06-09: creado el modulo backend `publication` con dominio `Publication`, estados `PublicationStatus`, puerto `PublicationRepository` y prueba unitaria de dominio. Verificado con `mvn -Dtest=PublicationTest test`.

---

## T9.2 [x]

Crear interfaz:

```java
PublishingProvider
```

Nota posterior 2026-06-09: creado el puerto `PublishingProvider` con contratos `PublishingRequest`, `PublishingResult` y excepcion `PublishingProviderException`. Verificado con `mvn "-Dtest=Publishing*Test" test`.

---

## T9.3 [x]

Implementar:

```java
TelegramPublisher
```

Nota posterior 2026-06-09: implementado `TelegramPublisher` con Bot API de Telegram, configuracion por `TELEGRAM_BOT_TOKEN`, `TELEGRAM_CHAT_ID` y `TELEGRAM_BASE_URL`, logs seguros y parseo acotado de respuestas. Verificado con `mvn "-Dtest=TelegramPublisherTest" test`.

---

## T9.4 [x]

Crear workflow publicación.

Nota posterior 2026-06-09: creado `n8n/workflows/wf_06_publish_telegram.json` con trigger manual/webhook, normalizacion de `contentId`, llamada a `POST /api/v1/publications/{contentId}/publish` y 3 reintentos en el nodo HTTP. JSON validado con Node.

---

## T9.5 [x]

Registrar publicación.

Nota posterior 2026-06-09: implementado `PublishContentUseCase`, persistencia JPA de `publications`, DTO/API `POST /api/v1/publications/{contentId}/publish` y registro de estados `PENDING`, `PUBLISHED` y `FAILED`. Verificado con `mvn "-Dtest=PublishContentUseCaseTest,JpaPublicationRepositoryTest,PublicationControllerTest" test`.

Nota posterior 2026-06-09: Sprint 9 completado con T9.1, T9.2, T9.3, T9.4 y T9.5 verificadas. Pendiente documentado para Sprint 10: Seguridad.

Nota posterior 2026-06-09: cierre verificado con suite completa backend `mvn test`: 151 tests ejecutados, 0 fallos, 0 errores.

---

# 13. Sprint 10 [x]

# Seguridad

---

Objetivo:

Autenticación y autorización.

---

## T10.1 [x]

JWT.

Nota posterior 2026-06-10: creada base JWT con `JwtTokenService`, configuracion `app.security.jwt`, emision de access token (15 min) y refresh token (7 dias), configuracion de `JwtEncoder`/`JwtDecoder` HS256 y logs operativos de emision sin exponer secretos. Verificado con `mvn "-Dtest=JwtTokenServiceTest" test`.

---

## T10.2 [x]

Roles.

```text
ADMIN

EDITOR
```

Nota posterior 2026-06-10: implementado soporte de roles `ADMIN` y `EDITOR` con modelo de usuario, repositorio JPA y `DatabaseUserDetailsService` mapeando authorities `ROLE_ADMIN` y `ROLE_EDITOR`. Verificado con `mvn "-Dtest=DatabaseUserDetailsServiceTest" test`.

---

## T10.3 [x]

Protección endpoints.

Nota posterior 2026-06-10: aplicada proteccion de endpoints con JWT stateless y matriz de autorizacion por rol (`ADMIN`/`EDITOR`), incluyendo endpoints publicos de health/login y conversion de authorities desde claim `roles`. Verificado con `mvn "-Dtest=SecurityConfigTest" test`.

---

## T10.4 [x]

Login.

Nota posterior 2026-06-10: implementado login con `POST /api/v1/auth/login`, autenticacion por email/password mediante `AuthenticationManager`, emision de access/refresh tokens y respuesta con datos de usuario. Verificado con `mvn "-Dtest=AuthControllerTest,LoginUseCaseTest,SecurityConfigTest" test`.

Nota posterior 2026-06-10: Sprint 10 completado con T10.1, T10.2, T10.3 y T10.4 verificadas.

Nota posterior 2026-06-10: intentado cierre con `mvn clean test`, bloqueado por entorno local sin PostgreSQL disponible en `localhost:5432` (Docker daemon no activo). Verificacion funcional realizada con bateria de seguridad `mvn "-Dtest=JwtTokenServiceTest,DatabaseUserDetailsServiceTest,SecurityConfigTest,LoginUseCaseTest,AuthControllerTest" test`: 11 tests, 0 fallos, 0 errores.

---

## T10.5 [x]

Gestion de usuarios y recuperacion de password.

---

Alcance:

```text
Backend user management para ADMIN:
- alta de usuario
- listado de usuarios
- edicion de usuario
- desactivacion de usuario

Backend password recovery:
- POST /api/v1/auth/forgot-password
- POST /api/v1/auth/reset-password
- token temporal con expiracion
- envio de email (entorno dev con MailHog)
```

---

Subtareas:

```text
T10.5.1 Crear migracion Flyway para password_reset_tokens
T10.5.2 Implementar casos de uso de gestion de usuarios (Create/Update/Disable/List/Get)
T10.5.3 Exponer API REST /api/v1/users para ADMIN
T10.5.4 Implementar casos de uso forgot/reset password
T10.5.5 Integrar servicio de email para recuperacion (MailHog en local)
T10.5.6 Añadir pruebas unitarias e integracion de auth/user
```

Nota posterior 2026-06-11: implementadas las subtareas T10.5.1..T10.5.6 con API `POST /api/v1/auth/forgot-password`, `POST /api/v1/auth/reset-password` y CRUD administrativo `GET/POST/PUT /api/v1/users` + `POST /api/v1/users/{id}/disable`. El esquema de `password_reset_tokens` queda consolidado en `V1__create_mvp_schema.sql` para arranques limpios con reset de BBDD. Integrado envio SMTP para recuperacion y configuracion local de MailHog en Docker. Verificado con `mvn -Dtest=AuthControllerTest,UserControllerTest,SecurityConfigTest test`: 13 tests, 0 fallos, 0 errores.

Nota posterior 2026-06-12: ampliado T10.5 con flujo de alta sin password solicitado/proporcionado, password temporal generada y enviada por email, estado inicial `PENDING_ACTIVATION`, expiracion configurable de password temporal, regeneracion por expiracion, bloqueo/desbloqueo/activacion/desactivacion sin borrado fisico, auditoria `user_audit_log`, fechas `last_login_at` y `last_password_change_at`, y bloqueo de login cuando la password temporal expira. Build backend no ejecutado por `JAVA_HOME` no definido en el entorno WSL; build frontend verificado.

Nota posterior 2026-06-12: incorporado puerto `UserAccountNotificationSender` y sender SMTP basado en `JavaMailSender` para MailHog/local o SMTP productivo por variables existentes. Se notifican cambios de password, bloqueo, desactivacion y regeneracion de password temporal sin registrar secretos. Verificado con `mvn "-Dtest=ChangePasswordUseCaseTest,ResetPasswordUseCaseTest,ChangeUserStatusUseCaseTest,ResetTemporaryPasswordUseCaseTest,AuthControllerTest,UserControllerTest" test`: 16 tests, 0 fallos, 0 errores.

---

# 14. Sprint 11

# Frontend Angular

---

Objetivo:

Backoffice MVP.

---

## T11.1 [x]

Crear proyecto Angular.

Nota posterior 2026-06-10: creado el proyecto Angular 20 del backoffice en `frontend/` con routing, SCSS, configuracion npm, proxy local hacia `http://localhost:8080` para `/api` y estructura inicial alineada con `core`, `shared`, `layout` y `features`.

---

## T11.2 [x]

Layout principal.

Nota posterior 2026-06-10: implementado layout principal responsive con `ShellComponent`, sidebar, cabecera, menu dinamico por rol `ADMIN/EDITOR` y navegacion base para Dashboard, Eventos, Contenido, Publicaciones y Fuentes.

---

## T11.3 [x]

Login.

Nota posterior 2026-06-10: implementada la pantalla de login conectada a `POST /api/v1/auth/login`, con `AuthService`, almacenamiento de sesion, interceptor JWT y guards de autenticacion y rol.

---

## T11.4 [~]

Dashboard.

Nota posterior 2026-06-10: creada una primera version visual del dashboard con tarjetas metricas y tabla de eventos prioritarios usando datos mock temporales, a la espera de endpoints reales de dashboard/eventos.

Estado auditado 2026-06-12: tarea parcial, no completada funcionalmente. La pantalla sigue dependiendo de `MockDashboardService` y debe integrarse con API real antes de considerarse cerrada.

---

## T11.5 [~]

Eventos.

Nota posterior 2026-06-10: creada la pantalla de eventos con tabla y filtros visuales mock para validar UX del backoffice. Pendiente conexion real cuando existan `GET /api/v1/events` y `GET /api/v1/events/{id}`.

Estado auditado 2026-06-12: tarea parcial. El backend solo expone `POST /api/v1/events/detect`; faltan listado, detalle y operaciones editoriales sobre eventos.

---

## T11.6

Detalle Evento.

Pendiente: bloqueado por ausencia de endpoint real de detalle de evento en backend.

---

## T11.7 [~]

Contenido.

Nota posterior 2026-06-10: creada vista editorial mock de contenido con bandeja de revision y vista previa, manteniendo la navegacion y el flujo UX mientras se completan endpoints reales de listado/detalle.

Estado auditado 2026-06-12: tarea parcial. El backend expone generacion/aprobacion/rechazo, pero no listado, detalle ni edicion manual de contenido para el backoffice.

---

## T11.8 [~]

Publicaciones.

Nota posterior 2026-06-10: creada vista mock de historico de publicaciones para avanzar la experiencia visual del backoffice. Pendiente listado real desde backend.

Estado auditado 2026-06-12: tarea parcial. El backend solo expone `POST /api/v1/publications/{id}/publish`; faltan listado, detalle, historial real y scheduling documentado.

Nota posterior 2026-06-10: validado el arranque del frontend con `npm run build` y `npm test -- --watch=false --browsers=ChromeHeadless` en `frontend`.

---

## T11.9

Configuracion IA para ADMIN.

---

Alcance:

```text
Seleccion de proveedor IA
Modelo IA
Temperatura
Limite de tokens
Version de prompt
```

---

Nota posterior 2026-06-07: inicialmente el proveedor IA se seleccionara por configuracion tecnica del backend mediante `application.yml` o variables de entorno. La seleccion por usuario ADMIN queda pendiente para el backoffice Angular. Las API keys no deben guardarse en base de datos.

---

## T11.10 [~]

Usuarios y recuperacion de password en frontend.

---

Alcance:

```text
Auth:
- enlace "Olvide mi password" en login
- pantalla solicitar recuperacion
- pantalla reset de password con token

Admin:
- menu "Usuarios" visible solo para ADMIN
- listado de usuarios
- alta de usuario
- edicion de usuario
- desactivacion de usuario
```

---

Subtareas:

```text
T11.10.1 Crear rutas y pantallas forgot/reset password
T11.10.2 Integrar llamadas API de forgot/reset en AuthService
T11.10.3 Crear modulo/pantalla de usuarios para ADMIN
T11.10.4 Integrar CRUD de usuarios con API backend
T11.10.5 Añadir tests de frontend para formularios y servicios
```

Nota posterior 2026-06-11: completadas T11.10.1, T11.10.2, T11.10.3 y T11.10.4 con nuevas rutas/pantallas de recuperacion, enlace en login, menu `Usuarios` solo ADMIN, pantalla de gestion de usuarios e integracion HTTP con backend. Verificado build frontend con `node node_modules/@angular/cli/bin/ng.js build`. Pendiente T11.10.5 (tests frontend especificos de formularios y servicios).

Nota posterior 2026-06-12: ajustado T11.10 para retirar password del alta de usuarios, mostrar estado/ultimo login/ultimo cambio de password/expiracion temporal, añadir acciones de activar, desactivar, bloquear, desbloquear y reset temporal, y crear pantalla `change-password` con guard de cambio obligatorio tras primer login. Verificado con `npm run build` en `frontend`.

Nota posterior 2026-06-12: normalizada la pantalla `change-password` con el patron visual de recuperacion/reset de password, validacion `PASSWORD_PATTERN`, mensaje de exito previo al logout y feedback de exito/error en gestion de usuarios. Los botones administrativos quedan alineados por semantica: activar/desbloquear en verde, reset temporal en amarillo y bloquear/desactivar en rojo. Verificado con `npm.cmd run build`.

---

# 15. Sprint 12

# Optimización

---

## T12.1

Versionado prompts.

---

## T12.2

Métricas IA.

---

## T12.3

Monitorización workflows.

---

## T12.4

Dashboard métricas.

---

# 16. Auditoria integral del estado del proyecto - 2026-06-12

Esta seccion convierte este documento en la fuente unica de verdad del progreso operativo. La auditoria cruza requisitos documentados con codigo actual, migraciones, APIs, pantallas Angular, workflows n8n y tests disponibles. No sustituye los documentos de arquitectura, pero prevalece como estado de avance y pendientes.

## 16.1 Resumen ejecutivo

Funcionalidades completadas:
- Backend modular Spring Boot con DDD/Clean Architecture para `source`, `news`, `classification`, `event`, `analysis`, `content`, `publication` y `user`.
- Esquema PostgreSQL/Flyway MVP con fuentes, noticias, clasificaciones, eventos, analisis, contenido, publicaciones, usuarios, tokens de reset, historial de passwords y auditoria de usuarios.
- Workflows n8n `WF-01` a `WF-06` exportados para captura, clasificacion, eventos, analisis, contenido y publicacion Telegram.
- Seguridad JWT, roles `ADMIN`/`EDITOR`, login, recuperacion de password, password temporal, bloqueo de primer login y MailHog local.
- Publicacion Telegram backend con registro de publicacion.

Funcionalidades en progreso:
- Sprint 11 backoffice Angular: login y usuarios estan integrados con API real; dashboard, eventos, contenido y publicaciones siguen siendo visuales/mock.
- Gestion de fuentes en frontend existe como ruta ADMIN, pero no tiene CRUD real.
- Configuracion IA para ADMIN esta documentada como pendiente.
- Sprint 12 de optimizacion no iniciado.

Implementaciones parciales principales:
- Eventos: backend detecta eventos, pero no expone `GET /api/v1/events`, `GET /api/v1/events/{id}` ni `POST /api/v1/events/merge` documentados.
- Contenido: backend genera, aprueba y rechaza contenido, pero no expone listado/detalle/edicion para backoffice.
- Publicaciones: backend publica en Telegram, pero no expone listado/detalle/historial ni scheduling documentado.
- Dashboard: no existe API agregada de metricas; frontend usa `MockDashboardService`.
- Auditoria: existe persistencia de acciones de usuario, pero no hay API ni pantalla para consultar el log.

Pendientes criticos:
- Sustituir mocks del backoffice por contratos backend reales de eventos, contenido, publicaciones y dashboard.
- Completar integracion frontend de fuentes.
- Cerrar tests frontend de auth/users/guards y pruebas de integracion UI.

## 16.2 Backend

Implementado:
- APIs reales: health, auth/login, forgot/reset/change password, request temporary password, sources list/create/update, news create/bulk/list/get, classifications classify, events detect, analysis generate, content generate/approve/reject, publications publish, users list/get/create/update/status/reset temporal.
- Casos de uso y puertos por modulo respetan la estructura `domain/application/infrastructure/api`.
- Integraciones IA determinista/Gemini para clasificacion, eventos, analisis y contenido.
- Publicacion Telegram con adaptador dedicado.

Parcial o faltante:
| Requisito | Estado actual | Prioridad | Orden |
| --- | --- | --- | --- |
| Listar eventos `GET /api/v1/events` | Documentado en Documento 12 y requerido por UI; no existe controller/use case de listado. | Critical | 1 |
| Detalle evento `GET /api/v1/events/{id}` | Documentado y bloquea T11.6; no implementado. | Critical | 2 |
| Merge de eventos `POST /api/v1/events/merge` | Documentado en API REST; no implementado. | Medium | 9 |
| Listado/detalle de contenido | UI lo necesita; backend solo genera/aprueba/rechaza. | Critical | 3 |
| Edicion manual de contenido generado | UX/editorial lo presupone; no hay endpoint ni caso de uso. | High | 6 |
| Listado/detalle/historial de publicaciones | UI lo muestra con mock; backend solo publica por contentId. | Critical | 4 |
| Scheduling de publicaciones `POST /api/v1/publications/{id}/schedule` | Documentado; no implementado. | Medium | 10 |
| Dashboard/metricas backend | Requerido por UI; no hay API agregada. | High | 5 |
| API de auditoria de usuarios | Tabla y escritura existen; no hay endpoint de consulta. | Medium | 12 |
| Errores API uniformes | Hay manejo basico por controllers/tests; falta contrato transversal documentado/validado. | Medium | 13 |

## 16.3 Frontend

Implementado:
- Proyecto Angular, rutas, shell, sidebar por rol, login real, interceptor JWT, guards de autenticacion/rol/cambio obligatorio de password.
- Pantallas reales de auth: login, forgot-password, reset-password, change-password.
- Pantalla usuarios ADMIN integrada con API real y acciones de alta, edicion, activacion, desactivacion, bloqueo, desbloqueo y reset temporal.

Parcial o faltante:
| Requisito | Estado actual | Prioridad | Orden |
| --- | --- | --- | --- |
| Dashboard con datos reales | Usa `MockDashboardService`; sin API backend. | High | 7 |
| Listado de eventos real | Usa mock; faltan APIs backend de list/detail. | Critical | 8 |
| Detalle de evento | T11.6 pendiente; sin endpoint backend. | Critical | 9 |
| Bandeja de contenido real | Usa mock; backend sin list/detail. | Critical | 10 |
| Editor de contenido real | UX documentada; no implementado contra API. | High | 11 |
| Historico de publicaciones real | Usa mock; backend sin listado/historial. | Critical | 12 |
| Gestion de fuentes | Ruta ADMIN y placeholder; falta CRUD UI real. | High | 13 |
| Tests frontend auth/users/guards | T11.10.5 pendiente; solo existe test base de app. | High | 14 |
| Eliminacion de mocks | Bloqueada por APIs backend faltantes. | High | 15 |

## 16.4 Database

Implementado:
- `V1__create_mvp_schema.sql` incluye tablas MVP principales: `sources`, `users`, `password_reset_tokens`, `news_articles`, `news_classifications`, `events`, `event_news`, `event_ai_analysis`, `generated_content`, `publications` e indices relevantes.
- `V2__seed_admin_user.sql` siembra usuarios iniciales admin/n8n.
- `V3__seed_rss_sources.sql` siembra fuentes RSS.
- `V4__add_first_login_password_policy.sql` agrega `must_change_password` e historial de passwords.
- `V5__user_account_status_audit.sql` agrega estado de usuario, expiracion temporal, last login, last password change y `user_audit_log`.

Parcial o faltante:
| Requisito | Estado actual | Prioridad | Orden |
| --- | --- | --- | --- |
| Consolidacion Flyway vs migraciones incrementales | Documentacion menciona consolidacion previa, pero existen V4/V5 incrementales activas. Debe decidirse si se consolidan en reset de desarrollo o se mantienen. | Medium | 16 |
| Auditoria funcional completa | `user_audit_log` existe solo para acciones de usuario; no hay auditoria general de contenido/publicacion/eventos. | Medium | 17 |
| Scheduling de publicaciones | Tabla `publications` no modela programacion futura de forma explicita. | Medium | 18 |
| Metricas operativas IA/workflows | No hay tablas especificas de metricas de coste, latencia, intentos o trazabilidad avanzada. | Low | 22 |

## 16.5 Security & User Management

Implementado:
- JWT stateless, roles `ADMIN` y `EDITOR`, proteccion por `SecurityConfig`, guards Angular y menu por rol.
- Flujo de alta de usuario por ADMIN sin password en request, password temporal, estado `PENDING_ACTIVATION`, expiracion configurable, primer login con cambio obligatorio y bloqueo de funcionalidades hasta cambio.
- Password history para impedir reutilizacion, fechas de ultimo login y ultimo cambio de password.
- Notificaciones SMTP/MailHog para password temporal, cambio de password, bloqueo y desactivacion.
- Soft delete/status mediante `ACTIVE`, `INACTIVE`, `LOCKED`, `PENDING_ACTIVATION`.

Parcial o faltante:
| Requisito | Estado actual | Prioridad | Orden |
| --- | --- | --- | --- |
| Consulta visual/API de auditoria | Se registra auditoria, pero no se expone a administradores. | Medium | 19 |
| Cobertura total de acciones auditadas | Alta, cambio de estado, roles y resets estan cubiertos; confirmar en tests todas las ramas de role change/unlock/activate. | Medium | 20 |
| Politica de password inicial admin/n8n | Seeds documentan password inicial `Admin@123`, que no cumple politica runtime >=10. | Medium | 21 |
| Validaciones frontend completas | Usuarios/auth tienen validaciones basicas; falta suite automatizada frontend. | High | 14 |

## 16.6 n8n Workflows

Implementado:
- `WF-01-Capture-News` autentica contra backend, obtiene fuentes activas, normaliza RSS/Atom y envia bulk con JWT.
- `WF-02-Classify-News`, `WF-03-Detect-Events`, `WF-04-Generate-Analysis`, `WF-05-Generate-Content` y `WF-06-Publish-Telegram` existen como JSON exportables.
- n8n orquesta; las decisiones de dominio residen en Spring Boot.

Parcial o faltante:
| Requisito | Estado actual | Prioridad | Orden |
| --- | --- | --- | --- |
| Autenticacion uniforme en WF-02..WF-06 | WF-01 usa login/JWT; revisar y alinear los demas workflows si endpoints protegidos rechazan llamadas sin token. | High | 23 |
| Validacion automatizada de workflows | Hay validacion JSON/manual historica; falta test automatizado o checklist ejecutable. | Medium | 24 |
| Monitorizacion de workflows | Sprint 12 pendiente; sin metricas/alertas de ejecucion. | Medium | 25 |
| Reintentos/errores operativos | Parcial en backend IA; n8n necesita politica uniforme documentada y validada. | Medium | 26 |

## 16.7 AI Features

Implementado:
- Clasificacion IA con prompt oficial, proveedor determinista y Gemini.
- Matching/agrupacion de eventos con proveedor determinista y prompt de eventos.
- Analisis IA consolidado de eventos con persistencia.
- Generacion de contenido editorial con persistencia y flujo de aprobacion/rechazo.

Parcial o faltante:
| Requisito | Estado actual | Prioridad | Orden |
| --- | --- | --- | --- |
| Configuracion IA por ADMIN | Documentada como pendiente; hoy se selecciona por configuracion tecnica. | Medium | 27 |
| Versionado de prompts | Sprint 12 T12.1 pendiente. | Medium | 28 |
| Metricas IA | Sprint 12 T12.2 pendiente. | Medium | 29 |
| Revision humana completa | Hay aprobar/rechazar contenido, pero falta editor real y listado de cola. | High | 11 |

## 16.8 Publishing & Social Media

Implementado:
- Telegram es el unico canal MVP documentado e implementado.
- `TelegramPublisher` publica contenido aprobado y registra resultado en `publications`.
- `WF-06` orquesta publicacion por `contentId`.

Parcial o faltante:
| Requisito | Estado actual | Prioridad | Orden |
| --- | --- | --- | --- |
| Historial real de publicaciones | No hay API/listado para backoffice; UI usa mock. | Critical | 12 |
| Scheduling | Documentado; no implementado. Decidir implementar o declarar post-MVP. | Medium | 10 |
| Gestion de errores/reintentos de publicacion en UI | Backend registra fallos; UI no consume estado real. | Medium | 30 |
| Otros canales sociales | Fuera del MVP; mantener Telegram como unico canal salvo decision explicita. | Low | 31 |

## 16.9 Infrastructure & DevOps

Implementado:
- Docker Compose con PostgreSQL, n8n y MailHog.
- Configuracion local SMTP MailHog `localhost:1025` y UI `localhost:8025`.
- Scripts locales de arranque/diagnostico documentados.
- Maven/Angular builds ejecutables en entorno local Windows.

Parcial o faltante:
| Requisito | Estado actual | Prioridad | Orden |
| --- | --- | --- | --- |
| CI/CD | Documento 14 lo contempla; no se observa pipeline versionado. | Medium | 32 |
| Despliegue Proxmox/Nginx productivo | Documentado como infraestructura objetivo; no hay automatizacion completa versionada. | Medium | 33 |
| Gestion de secretos productivos | Variables previstas; falta checklist operativo final. | Medium | 34 |
| Observabilidad runtime | Logs existen; falta dashboard/metricas Sprint 12. | Medium | 25 |

## 16.10 Testing

Implementado:
- Amplia suite backend JUnit/Mockito/Spring para modulos principales, APIs, repositorios, proveedores IA y publicacion.
- Pruebas recientes de usuarios/auth/notificaciones pasan en bateria focalizada.
- Build frontend verificado tras cambios recientes.

Parcial o faltante:
| Requisito | Estado actual | Prioridad | Orden |
| --- | --- | --- | --- |
| Tests frontend especificos | T11.10.5 pendiente; faltan tests de formularios auth/users, guards y servicios. | High | 14 |
| E2E del flujo completo | No hay suite E2E versionada para RSS -> evento -> contenido -> publicacion. | High | 35 |
| Tests de workflows n8n | No hay validacion automatizada de JSON/contratos por workflow. | Medium | 24 |
| Tests de APIs faltantes | Deben crearse junto con list/detail/merge/scheduling. | Critical | 1 |
| Pruebas manuales MailHog | Documentadas como pendientes para verificacion visual de emails. | Low | 36 |

## 16.11 Documentation

Implementado:
- Documentacion amplia de vision, requisitos, modelo de datos, arquitectura, APIs, seguridad, DevOps, pruebas, prompts, UX, MVP tecnico y plan detallado.
- `Docs_Asistentes` registra intervenciones historicas.
- `CHANGELOG.md` mantiene trazabilidad general del proyecto.

Inconsistencias y deuda:
| Requisito | Estado actual | Prioridad | Orden |
| --- | --- | --- | --- |
| Documento 31 como fuente unica | Esta seccion corrige el estado operativo y debe prevalecer sobre notas antiguas. | Critical | 0 |
| Tareas visuales marcadas como completadas | T11.4/T11.5/T11.7/T11.8 pasan a estado parcial `[~]` hasta integracion real. | High | 7 |
| Proxima tarea desactualizada | Debe priorizar contratos backend para eliminar mocks, no solo T11.10. | High | 0 |
| Codificacion/mojibake en documentos antiguos | Existen textos con caracteres corruptos visibles; no bloquea funcionalidad, pero dificulta lectura. | Low | 37 |
| Contratos documentados no implementados | Documento 12 incluye eventos merge y scheduling de publicaciones; deben implementarse o reclasificarse como post-MVP. | Medium | 9 |

## 16.12 Roadmap recomendado

1. Critical: implementar `GET /api/v1/events` con filtros minimos para backoffice y tests.
2. Critical: implementar `GET /api/v1/events/{id}` con noticias, clasificacion, analisis y contenido asociado.
3. Critical: implementar listado/detalle de contenido generado para bandeja editorial.
4. Critical: implementar listado/detalle/historial de publicaciones.
5. High: crear API de dashboard/metricas MVP o endpoints agregados suficientes para sustituir mocks.
6. High: implementar edicion manual de contenido y conectar aprobar/rechazar desde UI real.
7. High: integrar dashboard Angular con datos reales.
8. Critical: integrar eventos Angular con APIs reales y completar detalle de evento T11.6.
9. Medium: implementar o descartar formalmente `POST /api/v1/events/merge`.
10. Medium: implementar o marcar post-MVP el scheduling de publicaciones.
11. High: completar editor/bandeja de contenido Angular con API real.
12. Critical: integrar publicaciones Angular con historial real.
13. High: completar gestion de fuentes Angular contra API existente.
14. High: cerrar T11.10.5 con tests frontend de auth/users/guards/services.
15. High: eliminar `MockDashboardService` cuando los contratos reales esten disponibles.
16. Medium: decidir estrategia Flyway: mantener V1..V5 o reconsolidar solo si se resetea BBDD de desarrollo.
17. Medium: ampliar auditoria a eventos/contenido/publicaciones si se requiere trazabilidad editorial completa.
18. Medium: modelar scheduling de publicaciones si se mantiene en MVP.
19. Medium: exponer consulta ADMIN de `user_audit_log`.
20. Medium: reforzar tests de auditoria de role change/unlock/activate/reset.
21. Medium: actualizar seeds admin/n8n para cumplir politica de password o documentar excepcion de bootstrap.
22. Low: definir tablas o exportadores para metricas IA si T12.2 lo requiere.
23. High: alinear autenticacion JWT en todos los workflows n8n protegidos.
24. Medium: anadir validacion automatizada de workflows n8n.
25. Medium: implementar monitorizacion de workflows y observabilidad Sprint 12.
26. Medium: documentar politica uniforme de reintentos n8n/backend.
27. Medium: construir configuracion IA ADMIN o declararla post-MVP.
28. Medium: versionar prompts segun T12.1.
29. Medium: medir coste/latencia/errores IA segun T12.2.
30. Medium: mostrar errores/reintentos de publicacion en UI.
31. Low: mantener canales no Telegram fuera del MVP.
32. Medium: versionar pipeline CI/CD basico.
33. Medium: completar guia/automatizacion de despliegue Proxmox/Nginx.
34. Medium: cerrar checklist de secretos productivos.
35. High: crear E2E minimo del flujo completo MVP.
36. Low: ejecutar verificacion manual MailHog y registrar resultado.
37. Low: normalizar codificacion de documentos historicos con mojibake.

## 16.13 Estado operativo recomendado

- Sprint actual real: Sprint 11 en progreso.
- Bloqueador principal: backend no expone contratos de lectura necesarios para backoffice real.
- No iniciar Sprint 12 hasta cerrar la integracion real del backoffice MVP o aceptar formalmente que Sprint 12 empieza con UI mock.
- La siguiente tarea debe ser backend-first: eventos list/detail, luego contenido/publicaciones list/detail, despues frontend.

---
# 17. Regla Operativa

Nunca avanzar al siguiente Sprint sin:

```text
Build OK

Tests OK

Flyway OK

Commit Git realizado
```

---

# 18. Orden Obligatorio

```text
Sprint 0
✓

Sprint 1
↓

Sprint 2
↓

Sprint 3
↓

Sprint 4
↓

Sprint 5
↓

Sprint 6
↓

Sprint 7
↓

Sprint 8
↓

Sprint 9
↓

Sprint 10
↓

Sprint 11
↓

Sprint 12
```

---

# 19. Proxima Tarea

Sprint 11 en progreso

```text
1. Implementar contratos backend reales para eliminar mocks del backoffice:
   - GET /api/v1/events
   - GET /api/v1/events/{id}
   - listado/detalle de contenido
   - listado/detalle/historial de publicaciones
2. Integrar pantallas Angular con APIs reales y eliminar MockDashboardService.
3. Completar gestion de fuentes en frontend.
4. Cerrar T11.10.5 con tests frontend de auth/users/guards/services.
```

Rol:

```text
spring-backend-architect / frontend-angular-backoffice / testing-quality
```
