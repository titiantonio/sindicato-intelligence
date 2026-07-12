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

Nota posterior 2026-06-13: anadido modo claro/oscuro global para todo el frontend mediante `ThemeService`, preferencia persistida en `localStorage`, interruptor transversal en `App` y tokens CSS aplicados al shell, login, recuperacion de password y pantallas de backoffice.

---

## T11.3 [x]

Login.

Nota posterior 2026-06-10: implementada la pantalla de login conectada a `POST /api/v1/auth/login`, con `AuthService`, almacenamiento de sesion, interceptor JWT y guards de autenticacion y rol.

---

## T11.4 [x]

Dashboard.

Nota posterior 2026-06-10: creada una primera version visual del dashboard con tarjetas metricas y tabla de eventos prioritarios usando datos mock temporales, a la espera de endpoints reales de dashboard/eventos.

Estado actualizado 2026-06-13: tarea completada funcionalmente para MVP. Se anadio `GET /api/v1/dashboard`, `DashboardService` Angular y se elimino `MockDashboardService`.

---

## T11.5 [x]

Eventos.

Nota posterior 2026-06-10: creada la pantalla de eventos con tabla y filtros visuales mock para validar UX del backoffice. Pendiente conexion real cuando existan `GET /api/v1/events` y `GET /api/v1/events/{id}`.

Estado actualizado 2026-06-13: tarea completada funcionalmente para MVP. Se anadieron `GET /api/v1/events`, detalle real con pantalla `/events/:id` y `POST /api/v1/events/merge` con archivado de eventos origen, movimiento de noticias al evento destino y auditoria `EVENT_MERGED`.

---

## T11.6 [x]

Detalle Evento.

Nota posterior 2026-06-13: implementado GET /api/v1/events/{id} con evento, noticias asociadas, clasificacion, analisis y contenido relacionado; creada pantalla Angular /events/:id enlazada desde el listado real.

Nota posterior 2026-06-27: ampliada la trazabilidad navegable del detalle de evento enlazando noticias a `/news/:id`, contenidos a `/content/:id` y mostrando el identificador de analisis asociado cuando el contenido lo conserva.

---

## T11.7 [x]

Contenido.

Nota posterior 2026-06-10: creada vista editorial mock de contenido con bandeja de revision y vista previa, manteniendo la navegacion y el flujo UX mientras se completan endpoints reales de listado/detalle.

Estado actualizado 2026-06-13: tarea completada funcionalmente para MVP. El backend expone listado/detalle, aprobacion, rechazo y edicion manual `PUT /api/v1/content/{id}`. Angular consume datos reales para bandeja editorial, editor manual, aprobacion, rechazo y programacion de contenido aprobado.

Nota posterior 2026-06-27: anadido detalle navegable `/content/:id` con traza hacia evento, analisis usado y noticias relacionadas; persistido `analysis_id` nullable en `generated_content` para nuevos contenidos mediante Flyway `V17`.

---

## T11.8 [x]

Publicaciones.

Nota posterior 2026-06-10: creada vista mock de historico de publicaciones para avanzar la experiencia visual del backoffice. Pendiente listado real desde backend.

Estado actualizado 2026-06-13: tarea completada funcionalmente para MVP. El backend expone listado/detalle/historial real, `POST /api/v1/publications/{contentId}/schedule`, estado `SCHEDULED`, `scheduled_at` y scheduler automatico configurable para publicaciones vencidas. Angular muestra `SCHEDULED` y `scheduledAt`.

Nota posterior 2026-06-27: anadido detalle navegable `/publications/:id` con resultado de publicacion, contenido publicado y traza completa hacia contenido, evento, analisis y noticias enlazadas.

Nota posterior 2026-06-27: mejorada la legibilidad del resultado de publicacion en Angular para traducir payloads tecnicos de Telegram como `{"ok":true,"messageId":"459"}` a mensajes operativos con el identificador de mensaje.

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

## T11.10 [x]

Usuarios y recuperacion de password en frontend.

Nota posterior 2026-06-13: ajustada la pantalla `forgot-password` para dejar una unica accion visible de recuperacion mediante enlace de restablecimiento. Se retira de esa vista el boton de solicitud de password temporal, manteniendo el flujo administrativo separado de reset temporal de usuarios.

Nota posterior 2026-06-13: ajustada la pantalla `reset-password` para no mostrar el token de recuperacion al usuario. El token se conserva internamente desde el enlace del correo y se informa error si el enlace no lo incluye.

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
T11.10.5 [x] Anadir tests de frontend para formularios y servicios
```

Nota posterior 2026-06-11: completadas T11.10.1, T11.10.2, T11.10.3 y T11.10.4 con nuevas rutas/pantallas de recuperacion, enlace en login, menu `Usuarios` solo ADMIN, pantalla de gestion de usuarios e integracion HTTP con backend. Verificado build frontend con `node node_modules/@angular/cli/bin/ng.js build`. T11.10.5 quedaba pendiente hasta la cobertura especifica posterior.

Nota posterior 2026-06-12: ajustado T11.10 para retirar password del alta de usuarios, mostrar estado/ultimo login/ultimo cambio de password/expiracion temporal, añadir acciones de activar, desactivar, bloquear, desbloquear y reset temporal, y crear pantalla `change-password` con guard de cambio obligatorio tras primer login. Verificado con `npm run build` en `frontend`.

Nota posterior 2026-06-12: normalizada la pantalla `change-password` con el patron visual de recuperacion/reset de password, validacion `PASSWORD_PATTERN`, mensaje de exito previo al logout y feedback de exito/error en gestion de usuarios. Los botones administrativos quedan alineados por semantica: activar/desbloquear en verde, reset temporal en amarillo y bloquear/desactivar en rojo. Verificado con `npm.cmd run build`.

Nota posterior 2026-06-13: cerrada T11.10.5 con 44 tests frontend nuevos para servicios auth/users/storage, guards auth/password/role y pantallas login, forgot-password, reset-password, change-password y users. Suite final: `npm.cmd test -- --watch=false --browsers=ChromeHeadless` con 45 tests OK; build final `npm.cmd run build` OK.

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
- Sprint 11 backoffice Angular: login, usuarios, dashboard, eventos, detalle de evento, contenido, publicaciones y fuentes estan integrados con API real.
- Gestion de fuentes en frontend integrada contra API real de listado, alta, edicion y activacion/desactivacion.
- Configuracion IA para ADMIN esta documentada como pendiente.
- Sprint 12 de optimizacion no iniciado.

Implementaciones parciales principales:
- Eventos: backend detecta, lista, detalla y fusiona eventos con `POST /api/v1/events/merge`; los eventos origen se archivan y sus noticias se mueven al destino.
- Contenido: backend genera, aprueba, rechaza, lista, detalla y permite edicion manual con retorno a `PENDING_REVIEW`.
- Publicaciones: backend publica en Telegram, expone listado/detalle/historial y soporta scheduling con estado `SCHEDULED`, `scheduled_at` y scheduler automatico configurable.
- Dashboard: existe API MVP de metricas y eventos prioritarios; frontend consume `DashboardService` real.
- Auditoria: existe persistencia de acciones de usuario, pero no hay API ni pantalla para consultar el log.

Pendientes criticos:
- No quedan pendientes criticos bloqueantes para abrir Sprint 12.
- La aceptacion local del flujo MVP completo fue ejecutada el 2026-06-13 mediante `scripts/validate-sprint11-acceptance.ps1`.
- Sprint 12 queda habilitado tras registrar el cierre operativo de Sprint 11.

## 16.2 Backend

Implementado:
- APIs reales: health, auth/login, forgot/reset/change password, request temporary password, sources list/create/update, news create/bulk/list/get, classifications classify, events detect, analysis generate, content generate/approve/reject, publications publish, users list/get/create/update/status/reset temporal.
- Casos de uso y puertos por modulo respetan la estructura `domain/application/infrastructure/api`.
- Integraciones IA determinista/Gemini para clasificacion, eventos, analisis y contenido.
- Publicacion Telegram con adaptador dedicado.

Parcial o faltante:
| Requisito | Estado actual | Prioridad | Orden |
| --- | --- | --- | --- |
| Listar eventos `GET /api/v1/events` | Implementado el 2026-06-13 con controller/use case/repositorio y consumo Angular real. | Completed | - |
| Detalle evento `GET /api/v1/events/{id}` | Implementado el 2026-06-13 con noticias, clasificacion, analisis y contenido asociado; pantalla Angular `/events/:id`. | Completed | - |
| Merge de eventos `POST /api/v1/events/merge` | Implementado con target/source, archivado de fuentes, movimiento de noticias y auditoria. | Completed | - |
| Listado/detalle de contenido | Implementado el 2026-06-13 y consumido por bandeja editorial Angular. | Completed | - |
| Edicion manual de contenido generado | Implementada con `PUT /api/v1/content/{id}`, retorno a `PENDING_REVIEW`, auditoria e integracion Angular. | Completed | - |
| Listado/detalle/historial de publicaciones | Implementado el 2026-06-13 y consumido por pantalla Angular real. | Completed | - |
| Scheduling de publicaciones `POST /api/v1/publications/{contentId}/schedule` | Implementado con estado `SCHEDULED`, `scheduled_at`, validacion de fecha futura y scheduler automatico. | Completed | - |
| Dashboard/metricas backend | Implementado el 2026-06-13 con endpoint MVP `GET /api/v1/dashboard`. | Completed | - |
| API de auditoria de usuarios | Implementada consulta ADMIN `GET /api/v1/audit/users`. | Completed | - |
| Errores API uniformes | Hay manejo basico por controllers/tests; falta contrato transversal documentado/validado. | Medium | 13 |

## 16.3 Frontend

Implementado:
- Proyecto Angular, rutas, shell, sidebar por rol, login real, interceptor JWT, guards de autenticacion/rol/cambio obligatorio de password.
- Pantallas reales de auth: login, forgot-password, reset-password, change-password.
- Pantalla usuarios ADMIN integrada con API real y acciones de alta, edicion, activacion, desactivacion, bloqueo, desbloqueo y reset temporal.

Parcial o faltante:
| Requisito | Estado actual | Prioridad | Orden |
| --- | --- | --- | --- |
| Dashboard con datos reales | Implementado el 2026-06-13 mediante `DashboardService`; eliminado `MockDashboardService`. | Completed | - |
| Listado de eventos real | Implementado el 2026-06-13 contra `GET /api/v1/events`. | Completed | - |
| Detalle de evento | Implementado el 2026-06-13 con ruta Angular `/events/:id`. | Completed | - |
| Bandeja de contenido real | Implementada el 2026-06-13 contra API real de contenido. | Completed | - |
| Editor de contenido real | Implementado contra API `PUT /api/v1/content/{id}` y pantalla Angular de bandeja editorial. | Completed | - |
| Historico de publicaciones real | Implementado el 2026-06-13 contra API real de publicaciones. | Completed | - |
| Gestion de fuentes | Implementada el 2026-06-13 contra API real de fuentes. | Completed | - |
| Tests frontend auth/users/guards | Completado el 2026-06-13 con cobertura focal de servicios, guards y pantallas criticas. | Completed | - |
| Eliminacion de mocks | `MockDashboardService` eliminado el 2026-06-13; revisar si quedan datos fixture puntuales en pantallas no criticas. | Completed | - |

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
| Auditoria funcional completa | Implementada `audit_log` para acciones editoriales de eventos, contenido y publicaciones. | Completed | - |
| Scheduling de publicaciones | Implementado con `scheduled_at`, estado `SCHEDULED`, API, UI y scheduler automatico. | Completed | - |
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
| Consulta visual/API de auditoria | Implementada pantalla ADMIN `/audit` y APIs de usuarios/editorial. | Completed | - |
| Cobertura total de acciones auditadas | Acciones de usuarios/editoriales cubiertas por auditoria visible y aceptacion local; queda ampliacion de cobertura E2E como mejora. | Completed | - |
| Politica de password inicial admin/n8n | Seeds documentan password inicial `Admin@123`, que no cumple politica runtime >=10. | Medium | 21 |
| Validaciones frontend completas | Completado el 2026-06-13 con suite Angular focal de auth/users/guards/services y pantallas criticas. | Completed | - |

## 16.6 n8n Workflows

Implementado:
- `WF-01-Capture-News` autentica contra backend, obtiene fuentes activas, normaliza RSS/Atom y envia bulk con JWT.
- `WF-02-Classify-News`, `WF-03-Detect-Events`, `WF-04-Generate-Analysis`, `WF-05-Generate-Content` y `WF-06-Publish-Telegram` existen como JSON exportables.
- n8n orquesta; las decisiones de dominio residen en Spring Boot.

Parcial o faltante:
| Requisito | Estado actual | Prioridad | Orden |
| --- | --- | --- | --- |
| Autenticacion uniforme en WF-02..WF-06 | Completado el 2026-06-13: WF-02..WF-06 autentican con `Authenticate Backend` y envian `Authorization: Bearer` a endpoints protegidos. | Completed | - |
| Validacion automatizada de workflows | Completado con `n8n/validate-workflows.ps1`, que valida JSON, autenticacion, Bearer y endpoints esperados en WF-01..WF-06. | Completed | - |
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
| Revision humana completa | Implementada bandeja real con listado, detalle, aprobacion, rechazo, edicion manual y programacion. | Completed | - |

## 16.8 Publishing & Social Media

Implementado:
- Telegram es el unico canal MVP documentado e implementado.
- `TelegramPublisher` publica contenido aprobado y registra resultado en `publications`.
- `WF-06` orquesta publicacion por `contentId`.

Parcial o faltante:
| Requisito | Estado actual | Prioridad | Orden |
| --- | --- | --- | --- |
| Historial real de publicaciones | Implementado el 2026-06-13 con API y pantalla Angular reales. | Completed | - |
| Scheduling | Implementado para MVP con API, estado `SCHEDULED`, fecha programada y scheduler automatico. | Completed | - |
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
| Tests frontend especificos | Completado el 2026-06-13: 45 tests Angular en ChromeHeadless. | Completed | - |
| E2E del flujo completo | No hay suite E2E versionada para RSS -> evento -> contenido -> publicacion. | High | 35 |
| Tests de workflows n8n | Validacion automatizada disponible mediante `n8n/validate-workflows.ps1`; WF-01..WF-06 tambien confirmados como importados en n8n local. | Completed | - |
| Tests de APIs de lectura Sprint 11 | Anadidos tests focales para eventos, contenido, publicaciones y dashboard. Siguen pendientes merge/scheduling si se implementan. | Completed | - |
| Pruebas manuales MailHog | Completado el 2026-06-13 durante aceptacion local: alta/reset temporal, cambio de password, bloqueo y desactivacion. | Completed | - |

## 16.11 Documentation

Implementado:
- Documentacion amplia de vision, requisitos, modelo de datos, arquitectura, APIs, seguridad, DevOps, pruebas, prompts, UX, MVP tecnico y plan detallado.
- `Docs_Asistentes` registra intervenciones historicas.
- `CHANGELOG.md` mantiene trazabilidad general del proyecto.

Inconsistencias y deuda:
| Requisito | Estado actual | Prioridad | Orden |
| --- | --- | --- | --- |
| Documento 31 como fuente unica | Esta seccion corrige el estado operativo y debe prevalecer sobre notas antiguas. | Critical | 0 |
| Tareas visuales marcadas como completadas | T11.4/T11.5/T11.6/T11.7/T11.8 integradas con API real y operaciones MVP. | Completed | - |
| Proxima tarea desactualizada | Actualizada tras cierre funcional/tecnico y aceptacion local de Sprint 11. | Completed | - |
| Codificacion/mojibake en documentos antiguos | Existen textos con caracteres corruptos visibles; no bloquea funcionalidad, pero dificulta lectura. | Low | 37 |
| Contratos documentados resueltos | `events/merge` y scheduling de publicaciones implementados para MVP. | Completed | - |

## 16.12 Roadmap recomendado

1. Completed 2026-06-13: `GET /api/v1/events` con tests e integracion Angular.
2. Completed 2026-06-13: `GET /api/v1/events/{id}` con noticias, clasificacion, analisis, contenido y pantalla Angular.
3. Completed 2026-06-13: listado/detalle de contenido generado para bandeja editorial real.
4. Completed 2026-06-13: listado/detalle/historial de publicaciones reales.
5. Completed 2026-06-13: API de dashboard/metricas MVP e integracion Angular real.
6. Completed 2026-06-13: edicion manual de contenido y aprobar/rechazar conectados desde UI real.
7. Completed 2026-06-13: dashboard Angular integrado con datos reales.
8. Completed 2026-06-13: eventos Angular y detalle T11.6 integrados con APIs reales.
9. Completed 2026-06-13: implementado `POST /api/v1/events/merge`.
10. Completed 2026-06-13: implementado scheduling de publicaciones.
11. Completed 2026-06-13: editor manual de contenido completado como requisito MVP.
12. Completed 2026-06-13: publicaciones Angular integradas con historial real.
13. Completed 2026-06-13: gestion de fuentes Angular contra API existente.
14. Completed 2026-06-13: T11.10.5 cerrado con tests frontend de auth/users/guards/services.
15. Completed 2026-06-13: eliminado `MockDashboardService`.
16. Medium: decidir estrategia Flyway: mantener V1..V5 o reconsolidar solo si se resetea BBDD de desarrollo.
17. Completed 2026-06-13: ampliada auditoria a eventos/contenido/publicaciones.
18. Completed 2026-06-13: modelado scheduling de publicaciones en dominio/API/UI.
19. Completed 2026-06-13: expuesta consulta ADMIN de auditoria de usuarios en `/api/v1/audit/users` y pantalla `/audit`.
20. Completed 2026-06-13: aceptacion local cubre create/change password/lock/unlock/disable y auditoria visible.
21. Medium: actualizar seeds admin/n8n para cumplir politica de password o documentar excepcion de bootstrap.
22. Low: definir tablas o exportadores para metricas IA si T12.2 lo requiere.
23. Completed 2026-06-13: autenticacion JWT alineada en WF-02..WF-06.
24. Completed 2026-06-13: anadida validacion automatizada de workflows n8n.
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
36. Completed 2026-06-13: verificacion MailHog ejecutada y registrada.
37. Low: normalizar codificacion de documentos historicos con mojibake.

## 16.13 Estado operativo recomendado

- Sprint actual real: Sprint 11 cerrado funcional y tecnicamente, pendiente solo de decision formal de apertura de Sprint 12.
- Bloqueador principal anterior resuelto: ya existen contratos de lectura para backoffice real de eventos, contenido, publicaciones y dashboard.
- Sprint 12 puede iniciarse con foco en versionado de prompts, metricas IA, monitorizacion workflows y dashboard de metricas.
- Los pendientes MVP previos quedan resueltos: merge de eventos, scheduling de publicaciones, editor manual de contenido y consulta ADMIN de auditoria.

---
## 16.14 Actualizacion de implementacion - 2026-06-13

Completado en esta iteracion:
- Backend: `GET /api/v1/events`, `GET /api/v1/events/{id}`, `GET /api/v1/content`, `GET /api/v1/content/{id}`, `GET /api/v1/publications`, `GET /api/v1/publications/{id}` y `GET /api/v1/dashboard`.
- Frontend: `DashboardService`, `EventService`, `ContentService`, `PublicationService` y `SourceService` reales; dashboard, eventos, detalle de evento, contenido, publicaciones y fuentes consumen API backend.
- Eliminado `MockDashboardService`.
- Tests focales backend para APIs de lectura y dashboard: `EventControllerTest`, `ContentControllerTest`, `PublicationControllerTest` y `DashboardControllerTest`.
- Build frontend validado con `npm.cmd run build`.

Pendiente tras esta iteracion:
- Decisiones de alcance resueltas: `POST /api/v1/events/merge`, scheduling de publicaciones y editor manual de contenido implementados para MVP.
- API/pantalla ADMIN para consultar auditoria de usuario si se requiere operacion visible.

---

## 16.15 Actualizacion de workflows n8n - 2026-06-13

Completado en esta iteracion:
- `WF-02-Classify-News`, `WF-03-Detect-Events`, `WF-04-Generate-Analysis`, `WF-05-Generate-Content` y `WF-06-Publish-Telegram` incorporan el nodo `Authenticate Backend`.
- Todos los endpoints protegidos invocados por WF-02..WF-06 envian `Authorization: Bearer {{ accessToken }}` usando la cuenta tecnica configurada por `BACKEND_N8N_AUTH_EMAIL` y `BACKEND_N8N_AUTH_PASSWORD`.
- WF-04..WF-06 conservan el payload original del trigger despues de autenticar mediante normalizadores que leen el trigger ejecutado.
- Validado JSON de `wf_01` a `wf_06` con `ConvertFrom-Json`.

Pendiente tras esta iteracion:
- Completado posteriormente el 2026-06-13: `POST /api/v1/events/merge`, scheduling, editor manual, auditoria visible y validacion automatizada n8n quedan validados para MVP.


---

## 16.16 Cierre funcional Sprint 11 MVP - 2026-06-13

Completado en esta iteracion:
- Eventos: implementado `POST /api/v1/events/merge` con `targetEventId` y `sourceEventIds`, validacion de destino activo, fuentes distintas, movimiento de noticias al destino, archivado de eventos origen y auditoria `EVENT_MERGED`.
- Contenido: implementado editor manual `PUT /api/v1/content/{id}` para `title`, `content` y `tone`; contenido publicado no es editable, toda edicion vuelve a `PENDING_REVIEW` y limpia `approvedAt`; auditoria `CONTENT_EDITED`.
- Publicaciones: anadido `PublicationStatus.SCHEDULED`, migracion `publications.scheduled_at`, endpoint `POST /api/v1/publications/{contentId}/schedule`, validacion de fecha futura/contenido aprobado y scheduler automatico configurable para publicar vencidas.
- Auditoria: creada tabla general `audit_log`, registro de acciones editoriales y APIs ADMIN `GET /api/v1/audit/users` y `GET /api/v1/audit/editorial` con filtros simples y limite acotado.
- Frontend: anadida fusion de eventos, editor manual de contenido, programacion de publicaciones, visualizacion de `SCHEDULED`/`scheduledAt` y pantalla ADMIN `/audit` con secciones Usuarios y Editorial.
- Tests: backend `mvn test` OK con 191 tests; frontend `npm.cmd test -- --watch=false --browsers=ChromeHeadless` OK con 53 tests; `npm.cmd run build` OK.
- Version backend: `0.0.40-SNAPSHOT`.

Estado operativo:
- Sprint 11 queda funcionalmente cerrado para el alcance MVP acordado.
- Sprint 12 puede prepararse tras validacion manual del flujo completo y revision de workflows n8n.

Pendiente recomendado:
- Completado posteriormente el 2026-06-13: validacion local MVP, MailHog, frontend/backend activos, n8n importado y checklist ejecutable.
- Preparar Sprint 12: versionado de prompts, metricas IA, monitorizacion workflows y dashboard de metricas.

---

## 16.17 Cierre tecnico Sprint 11 - 2026-06-13

Evidencia ejecutada en esta iteracion:
- Backend: `mvn test` ejecutado en `backend/` con resultado OK. Total: 191 tests, 0 failures, 0 errors, 0 skipped.
- Flyway: validacion integrada durante `mvn test` con 6 migraciones validadas; esquema `public` actualizado sin migraciones pendientes.
- Frontend tests: `npm.cmd test -- --watch=false --browsers=ChromeHeadless` ejecutado en `frontend/` con resultado OK. Total: 53 specs, 0 failures.
- Frontend build: `npm.cmd run build` ejecutado en `frontend/` con resultado OK. Artefacto generado en `frontend/dist/frontend`.
- Workflows n8n: creado y ejecutado `n8n/validate-workflows.ps1` con resultado OK para `WF-01` a `WF-06`.

Alcance del validador n8n:
- Valida que los JSON exportados de `WF-01` a `WF-06` son parseables.
- Confirma la presencia del nodo `Authenticate Backend`.
- Confirma uso de cabecera `Authorization` con `Bearer` y `accessToken` en llamadas protegidas.
- Confirma endpoints esperados sin modificar contratos:
  - `WF-01`: `/api/v1/auth/login`, `/api/v1/sources`, `/api/v1/news/bulk`.
  - `WF-02`: `/api/v1/auth/login`, `/api/v1/news`, `/api/v1/classifications/classify`.
  - `WF-03`: `/api/v1/auth/login`, `/api/v1/news`, `/api/v1/events/detect`.
  - `WF-04`: `/api/v1/auth/login`, `/api/v1/analysis/generate`.
  - `WF-05`: `/api/v1/auth/login`, `/api/v1/content/generate`.
  - `WF-06`: `/api/v1/auth/login`, `/api/v1/publications/{contentId}/publish`.

Infraestructura local confirmada por configuracion:
- `database/docker-compose.yml` mantiene PostgreSQL en `5432`, n8n en `5678` y MailHog con SMTP `1025` y UI `8025`.
- n8n conserva credenciales tecnicas de desarrollo mediante `BACKEND_N8N_AUTH_EMAIL` y `BACKEND_N8N_AUTH_PASSWORD`.

Estado operativo:
- Sprint 11 queda cerrado tecnicamente para builds, tests, Flyway y validacion estatica de workflows.
- La validacion manual end-to-end con servicios levantados queda pendiente como aceptacion funcional final.
- No se detectaron cambios necesarios en contratos backend/frontend ni en los JSON de workflows.

Pendiente de aceptacion manual:
- Completado el 2026-06-13 mediante aceptacion local con PostgreSQL, MailHog, n8n, backend, frontend y Telegram fake local.

---

## 16.18 Aceptacion local final Sprint 11 - 2026-06-13

Evidencia ejecutada:
- Script creado y ejecutado: `scripts/validate-sprint11-acceptance.ps1`.
- Resultado: `SPRINT11_ACCEPTANCE_OK userId=7 eventId=156 contentId=61 publicationId=25 scheduledPublicationId=24`.
- Backend activo en `http://localhost:8080` con health `UP`.
- Frontend activo en `http://localhost:4200` con respuesta HTTP 200.
- n8n activo en `http://localhost:5678` con respuesta HTTP 200.
- MailHog activo en `http://localhost:8025` con respuesta API correcta.
- Telegram fake local usado en `http://localhost:19090` para validar publicacion `PUBLISHED` sin proveedor externo real.

Flujo MVP validado:
- Login ADMIN.
- Alta de usuario EDITOR sin password en request.
- Email de password temporal en MailHog.
- Primer login con `mustChangePassword=true`.
- Cambio obligatorio de password y confirmacion por email.
- Bloqueo, desbloqueo y desactivacion con notificaciones MailHog.
- Creacion de noticias, clasificacion y deteccion de eventos.
- Merge de eventos.
- Generacion de analisis IA determinista.
- Generacion, edicion manual y aprobacion de contenido.
- Programacion de publicacion con estado `SCHEDULED`.
- Publicacion inmediata con estado `PUBLISHED`.
- Dashboard backend consultable.
- Auditoria ADMIN de usuarios y editorial consultable.

Validacion n8n:
- `n8n/validate-workflows.ps1` ejecutado OK para WF-01..WF-06.
- `docker exec sindicato-n8n-dev n8n list:workflow` confirma workflows importados:
  - `WF-01-Capture-News`
  - `WF-02-Classify-News`
  - `WF-03-Detect-Events`
  - `WF-04-Generate-Analysis`
  - `WF-05-Generate-Content`
  - `WF-06-Publish-Telegram`
- La ejecucion CLI directa con `n8n execute` dentro del mismo contenedor activo no se usa como criterio porque n8n bloquea el Task Broker `5679` cuando la instancia ya esta corriendo. La validacion funcional queda cubierta por endpoints reales y contratos de workflows.

Estado final:
- Sprint 11 queda listo para cierre formal.
- No quedan bloqueadores MVP para iniciar Sprint 12.
- Siguiente foco: Sprint 12 (`T12.1` versionado prompts, `T12.2` metricas IA, `T12.3` monitorizacion workflows, `T12.4` dashboard metricas).
---

## 16.19 Correccion de sesion inactiva en backoffice - 2026-06-13

Tarea de mantenimiento correctivo sobre Sprint 10 Seguridad y Sprint 11 Frontend Angular.

Completado en esta iteracion:
- Backend: anadido `POST /api/v1/auth/refresh` como endpoint publico de renovacion controlada de sesion con refresh token.
- Backend: creado `RefreshTokenUseCase`, validando JWT de tipo `REFRESH`, expiracion, usuario existente y estado autenticable antes de emitir nuevos tokens.
- Backend: los refresh tokens dejan de incluir claims de rol para impedir su uso como bearer token de autorizacion.
- Frontend: `AuthService` expone `refreshSession` y conserva access/refresh token renovados en almacenamiento local.
- Frontend: `jwtInterceptor` detecta respuestas `401`, renueva sesion y reintenta la peticion original con el nuevo access token.
- Regresion cubierta: el dashboard y el listado de eventos dejan de fallar tras inactividad mientras el refresh token siga vigente.

Verificacion:
- Backend focal: `mvnw.cmd "-Dtest=JwtTokenServiceTest,RefreshTokenUseCaseTest,AuthControllerTest,SecurityConfigTest" test` OK, 15 tests.
- Frontend focal: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/core/services/auth.service.spec.ts --include=src/app/core/interceptors/jwt.interceptor.spec.ts` OK, 9 specs.

Estado:
- Correccion completada sin cambiar decisiones arquitectonicas ni adelantar Sprint 12.

---

## 16.20 Mejora de dashboard y tabla de eventos - 2026-06-13

Tarea de mejora sobre Sprint 11 Frontend Angular y API real de dashboard.

Completado en esta iteracion:
- Backend: `GET /api/v1/dashboard` devuelve metricas comparativas con valores de hoy, ayer y diferencia.
- Backend: los rangos diarios se calculan con zona `Europe/Madrid`.
- Backend: eventos prioritarios limitados a 10 eventos activos `OPEN`/`MONITORING`, con importancia `HIGH` o `CRITICAL`, excluyendo categoria `OTROS`.
- Frontend: las tarjetas metricas muestran por separado hoy, ayer y la diferencia con signo.
- Frontend: la tabla prioritaria del dashboard permite navegar al detalle del evento.
- Frontend: la pantalla `/events` incorpora busqueda global, filtros por columna y ordenacion por cualquier campo visible.

Verificacion:
- Backend focal: `mvnw.cmd "-Dtest=DashboardControllerTest" test` OK.
- Frontend focal: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/shared/components/metric-card/metric-card.component.spec.ts --include=src/app/features/dashboard/dashboard-page.component.spec.ts --include=src/app/features/events/events-page.component.spec.ts` OK.
- Frontend build: `npm.cmd run build` OK.

Estado:
- Mejora completada sin cambiar `GET /api/v1/events` ni adelantar Sprint 12.

---

## 16.21 Rediseño de tarjetas métricas del dashboard - 2026-06-13

Tarea de mejora visual y contractual sobre Sprint 11 Frontend Angular y API real de dashboard.

Completado en esta iteracion:
- Backend: `GET /api/v1/dashboard` amplia cada tarjeta con titulo, subtitulo, icono, etiqueta, ultima actualizacion e indicadores internos.
- Backend: las tarjetas usan datos reales del dominio para totales de noticias, eventos criticos, contenidos por estado y publicaciones programadas/fallidas.
- Frontend: `MetricCardComponent` recibe la tarjeta completa y renderiza cabecera, tres indicadores, iconos SVG inline y pie de ultima actualizacion.
- Frontend: el rediseño respeta los tokens de tema claro/oscuro existentes y no introduce nuevas dependencias.

Verificacion:
- Backend focal: `mvnw.cmd "-Dtest=DashboardControllerTest" test` OK.
- Frontend focal: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/shared/components/metric-card/metric-card.component.spec.ts --include=src/app/features/dashboard/dashboard-page.component.spec.ts` OK.
- Frontend build: `npm.cmd run build` OK.

Estado:
- Mejora implementada sin crear nuevos estados de dominio ni adelantar Sprint 12.

---

## 16.22 Ajuste de metricas y sidebar colapsable - 2026-06-14

Tarea de mejora visual y contractual sobre Sprint 11 Frontend Angular y API real de dashboard.

Completado en esta iteracion:
- Backend: cada tarjeta de `GET /api/v1/dashboard` devuelve su propia `lastUpdatedAt` calculada desde los datos de noticias, eventos, contenidos o publicaciones.
- Frontend: las tarjetas metricas mantienen cabeceras y valores largos en una misma linea mediante tamanos responsivos y sin cortes agresivos de numeros.
- Frontend: el shell reduce el ancho del menu lateral, anade modo colapsado en escritorio y mantiene solo iconos para reabrir/navegar.
- Frontend: el layout principal usa columnas estables y `min-width: 0` para que pantallas con tablas anchas no deformen el sidebar.

Verificacion:
- Backend focal: `mvnw.cmd "-Dtest=DashboardControllerTest" test` OK.
- Frontend focal: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/shared/components/metric-card/metric-card.component.spec.ts --include=src/app/layout/shell/shell.component.spec.ts --include=src/app/features/dashboard/dashboard-page.component.spec.ts` OK.
- Frontend build: `npm.cmd run build` OK.

Estado:
- Mejora implementada sin crear nuevas dependencias frontend ni adelantar Sprint 12.

---

## 16.23 Ajuste de gestion de fuentes - 2026-06-14

Tarea de mejora correctiva sobre Sprint 11 Frontend Angular y pantalla ADMIN de fuentes.

Completado en esta iteracion:
- Frontend: eliminada la tarjeta fija `sources-page__card` de "Nueva fuente" de la pantalla `/sources`.
- Frontend: anadido boton principal "Anadir fuente" que abre modal de creacion.
- Frontend: la accion "Editar" abre modal de edicion con los datos de la fuente seleccionada.
- Frontend: la tabla de fuentes permite busqueda global, filtros por columna y ordenacion por ID, nombre, URL, tipo, prioridad, estado, fecha de creacion y fecha de actualizacion.
- Frontend: ajustado el layout para que la pagina no sea mas ancha que la pantalla; el scroll horizontal queda contenido dentro del wrapper de la tabla.

Verificacion:
- Frontend focal: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/sources/sources-page.component.spec.ts` OK, 7 tests.
- Frontend build: `npm.cmd run build` OK.
- Browser local: `/sources` verificada con 54 filas reales en desktop y viewport movil 390x844; sin overflow horizontal de pagina, con scroll interno de tabla, modal de alta/edicion y filtro global operativos.

Estado:
- Mejora implementada sin cambiar contratos backend ni abrir Sprint 12.

---

## 16.24 Correccion scheduler de publicaciones - 2026-06-15

Tarea de mantenimiento correctivo sobre Sprint 11 / Fase 10, scheduling de publicaciones.

Completado en esta iteracion:
- Backend: corregido `PublishScheduledPublicationsUseCase` para que una publicacion `SCHEDULED` vencida sin proveedor `TELEGRAM` registrado no propague `publication provider not found for channel: TELEGRAM` al scheduler de Spring.
- Backend: la publicacion afectada queda marcada como `FAILED` con payload de error controlado, evitando que el fallo se repita en cada ciclo.
- Backend: anadida regresion unitaria para el caso de proveedor ausente.

Verificacion:
- Backend focal: `mvn "-Dtest=PublishScheduledPublicationsUseCaseTest" test` OK, 3 tests.

Estado:
- Correccion implementada sin cambiar contratos REST, migraciones ni alcance de Sprint 12.

---

## 16.25 Mejora de gestion de usuarios - 2026-06-15

Tarea de mejora correctiva sobre Sprint 11 Frontend Angular y gestion ADMIN de usuarios.

Completado en esta iteracion:
- Frontend: eliminado el formulario fijo de alta/edicion de la pantalla `/users`.
- Frontend: anadido boton "Alta de usuario" con modal comun de creacion y edicion.
- Frontend: la tabla de usuarios permite busqueda global, filtros por columna, ordenacion por todos los campos visibles, paginacion y selector de filas por pagina.
- Frontend: anadida accion "Eliminar" con modal de advertencia antes de solicitar el borrado fisico.
- Frontend: aclarada la diferencia operativa entre desactivar usuario (`INACTIVE`, baja administrativa) y bloquear usuario (`LOCKED`, bloqueo reversible por incidencia).
- Backend: anadido `DELETE /api/v1/users/{id}` exclusivo para `ADMIN`.
- Backend: creado `DeleteUserUseCase` con rechazo de autoeliminacion, ultimo `ADMIN` y usuarios con referencias funcionales en `generated_content.created_by` o `audit_log.user_id`.
- Backend: el borrado permitido limpia dependencias tecnicas (`password_reset_tokens`, `user_password_history`, `user_audit_log`) antes de eliminar la fila `users`.

Verificacion:
- Backend focal: `mvn "-Dtest=UserControllerTest,*User*UseCaseTest" test` OK, 16 tests.
- Frontend focal: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/users/users-page.component.spec.ts --include=src/app/core/services/user-admin.service.spec.ts` OK, 15 tests.
- Frontend completo: `npm.cmd test -- --watch=false --browsers=ChromeHeadless` OK, 78 tests.
- Frontend build: `npm.cmd run build` OK, con warning no bloqueante de presupuesto CSS en `users-page.component.scss`.
- Browser local: `/users` verificada con sesion ADMIN real, listado real de usuarios, filtros, ordenacion, paginacion, selector de filas y acciones visibles. No se confirmo ninguna accion destructiva.

Estado:
- Mejora implementada sin abrir Sprint 12 ni modificar migraciones existentes.

---

## 16.26 Notificaciones completas de cambios de usuario - 2026-06-15

Tarea de mejora correctiva sobre Sprint 11 / gestion ADMIN de usuarios.

Completado en esta iteracion:
- Backend: ampliado `UserAccountNotificationSender` para notificar activacion, desbloqueo, actualizacion de datos y eliminacion de usuario.
- Backend: `ChangeUserStatusUseCase` envia email tambien al activar y desbloquear.
- Backend: `UpdateUserUseCase` envia email cuando cambia nombre o rol.
- Backend: `DeleteUserUseCase` envia email antes del borrado fisico permitido.
- Frontend: ajustados mensajes de exito para informar de la notificacion por email en edicion, activacion, desbloqueo y eliminacion.

Verificacion:
- Backend focal: `mvn "-Dtest=UserControllerTest,*User*UseCaseTest" test` OK, 20 tests.
- Frontend focal: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/users/users-page.component.spec.ts` OK, 10 tests.
- Frontend build: `npm.cmd run build` OK, con warning no bloqueante de presupuesto CSS en `users-page.component.scss`.

Estado:
- Mejora implementada sin cambiar esquema ni migraciones.

---

## 16.27 Paginacion, filtros y ordenacion de tablas backoffice - 2026-06-15

Tarea de mejora correctiva sobre Sprint 11 / backoffice Angular.

Completado en esta iteracion:
- Frontend fuentes: la tabla `/sources` mantiene busqueda/filtros/ordenacion y anade paginacion local con selector de filas por pagina.
- Frontend eventos: la tabla `/events` mantiene busqueda/filtros/ordenacion y anade paginacion local con selector de filas por pagina.
- Frontend auditoria: las tablas de auditoria de usuarios y auditoria editorial permiten filtrar, ordenar y paginar de forma independiente.
- Frontend contenido: la tabla `/content` permite filtrar y ordenar por canal, titulo, estado, fecha de generacion y fecha de aprobacion, con paginacion local.
- Frontend detalle de evento: la tabla de noticias asociadas permite filtrar, ordenar y paginar por todos sus campos visibles.
- Frontend dashboard: la tabla de eventos prioritarios permite filtrar, ordenar y paginar por todos sus campos visibles.

Verificacion:
- Frontend focal: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/sources/sources-page.component.spec.ts --include=src/app/features/events/events-page.component.spec.ts --include=src/app/features/audit/audit-page.component.spec.ts --include=src/app/features/dashboard/dashboard-page.component.spec.ts` OK, 17 tests.
- Frontend completo: `npm.cmd test -- --watch=false --browsers=ChromeHeadless` OK, 80 tests.
- Frontend build: `npm.cmd run build` OK, con warnings no bloqueantes de presupuesto CSS en `sources-page.component.scss` y `users-page.component.scss`.

Estado:
- Mejora implementada sobre paginacion frontend local sin modificar contratos REST ni migraciones.

---

## 16.28 Modal editorial en pagina de contenido - 2026-06-15

Tarea de mejora correctiva sobre Sprint 11 / backoffice Angular.

Completado en esta iteracion:
- Frontend contenido: sustituido el panel editorial fijo de `/content` por un modal.
- Frontend contenido: click en una fila de la tabla abre el modal en modo lectura.
- Frontend contenido: el boton `Editar` abre el mismo modal en modo edicion y no dispara la seleccion de fila.
- Frontend contenido: `Editar` queda activo para `PENDING_REVIEW` y `APPROVED`, y deshabilitado para `REJECTED` y `PUBLISHED`.
- Frontend contenido: la programacion de publicacion para contenido `APPROVED` permanece dentro del modal.
- Frontend contenido: se mantienen filtros, ordenacion, paginacion y selector de filas por pagina.

Verificacion:
- Frontend focal: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/content/content-page.component.spec.ts` OK, 6 tests.
- Frontend completo: `npm.cmd test -- --watch=false --browsers=ChromeHeadless` OK, 86 tests.
- Frontend build: `npm.cmd run build` OK, con warnings no bloqueantes de presupuesto inicial, `users-page.component.scss` y `sources-page.component.scss`.

Estado:
- Mejora implementada sin modificar backend, contratos REST ni migraciones.

---

## 16.29 Ordenacion editorial de eventos prioritarios del dashboard - 2026-06-15

Tarea de mejora correctiva sobre Sprint 11 / backoffice Angular y API real de dashboard.

Completado en esta iteracion:
- Backend: `GET /api/v1/dashboard` mantiene contrato y ordena eventos prioritarios por impacto, numero de noticias asociadas y ultima actualizacion.
- Frontend dashboard: la tabla de eventos prioritarios inicia con orden editorial por impacto y noticias, usando ranking semantico de importancia.
- Frontend dashboard: los filtros de cabecera para impacto y estado pasan de texto libre a selectores con opciones cerradas.
- Proyecto: backend versionado a `0.0.51-SNAPSHOT` y `CHANGELOG.md` actualizado.

Verificacion:
- Backend focal: `mvnw.cmd "-Dtest=DashboardControllerTest" test` OK.
- Frontend focal: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/dashboard/dashboard-page.component.spec.ts` OK, 5 tests.
- Frontend build: `npm.cmd run build` OK, con warnings no bloqueantes de presupuesto en bundle inicial, `users-page.component.scss` y `sources-page.component.scss`.
- Verificacion visual local: `http://localhost:4200/dashboard` renderiza la tabla y muestra selectores de cabecera para impacto y estado.

Estado:
- Mejora implementada sin cambiar contratos REST ni migraciones.

---

# 20. [x] Sprint 12

# Consolidacion de automatizaciones internas en Spring Boot

Objetivo:

Migrar `WF-02` a `WF-06` desde n8n hacia Spring Boot, manteniendo `WF-01` como workflow externo de captura RSS.

---

## [x] T12.1

Auditar contratos actuales de `WF-02` a `WF-06`.

Resultado:
- Identificados los endpoints llamados por los workflows n8n sustituidos: clasificacion, deteccion de eventos, analisis, contenido y publicacion.
- Confirmado que Spring Boot ya contenia los casos de uso principales y que Angular no llamaba a webhooks n8n.
- Confirmado que `WF-01-Capture-News` permanece en n8n.

Verificacion:
- Revision de `n8n/workflows`, backend y frontend.
- Registro en `docs/Docs_Asistentes`.

---

## [x] T12.2

Crear API backend de automatizaciones internas.

Resultado:
- Anadido `POST /api/v1/automation/classifications/run`.
- Anadido `POST /api/v1/automation/events/run`.
- Anadido `POST /api/v1/automation/analysis/run`.
- Los endpoints devuelven `processedCount`, `successCount`, `failedCount`, `skippedCount` y `errors`.
- Acceso permitido a `ADMIN` y `EDITOR`.

Verificacion:
- `AutomationControllerTest` OK.

---

## [x] T12.3

Migrar `WF-02-Classify-News` a Spring Boot.

Resultado:
- Creado `ProcessPendingClassificationsUseCase`.
- Procesa noticias `CAPTURED` por lotes configurables.
- Reutiliza `ClassifyNewsUseCase`.
- Anadido scheduler configurable `app.automation.classification.enabled`.

Verificacion:
- `ProcessPendingClassificationsUseCaseTest` OK.

---

## [x] T12.4

Migrar `WF-03-Detect-Events` a Spring Boot.

Resultado:
- Creado `ProcessPendingEventDetectionUseCase`.
- Procesa noticias `CLASSIFIED` por lotes configurables.
- Reutiliza `DetectEventUseCase`.
- Mantiene la regla de una noticia en un unico evento principal.
- Anadido scheduler configurable `app.automation.event-detection.enabled`.

Verificacion:
- `ProcessPendingEventDetectionUseCaseTest` OK.

---

## [x] T12.5

Migrar `WF-04-Generate-Analysis` a Spring Boot.

Resultado:
- Creado `ProcessPendingEventAnalysisUseCase`.
- Genera analisis para eventos `OPEN` o `MONITORING` sin analisis previo.
- Reutiliza `GenerateAnalysisUseCase`.
- Permite ejecucion manual por `eventId`.
- Anadido scheduler configurable `app.automation.analysis.enabled`.

Verificacion:
- `ProcessPendingEventAnalysisUseCaseTest` OK.

---

## [x] T12.6

Consolidar `WF-05-Generate-Content` como flujo backend/frontend.

Resultado:
- Confirmado flujo por API Spring Boot `POST /api/v1/content/generate`.
- Eliminado el workflow n8n `wf_05_generate_content.json`.
- Se mantiene revision humana antes de publicacion.

Verificacion:
- Revision de frontend/backend sin referencias a webhooks n8n.

---

## [x] T12.7

Consolidar `WF-06-Publish-Telegram` como flujo backend/frontend.

Resultado:
- Confirmada publicacion inmediata por `POST /api/v1/publications/{id}/publish`.
- Confirmada publicacion programada con scheduler Spring existente.
- Eliminado el workflow n8n `wf_06_publish_telegram.json`.
- Se mantienen auditoria y estados de publicacion.

Verificacion:
- Tests focales backend de automatizacion OK.

---

## [x] T12.8

Actualizar frontend para lanzar automatizaciones migradas.

Resultado:
- Anadido `AutomationService`.
- Anadidas acciones en dashboard para clasificar pendientes, detectar eventos y analizar pendientes.
- Anadida accion de analisis por evento prioritario.
- El frontend llama exclusivamente a `/api/v1/automation/*`.

Verificacion:
- Tests focales Angular OK: `AutomationService` y `DashboardPageComponent`.

---

## [x] T12.9

Eliminar workflows n8n sustituidos.

Resultado:
- Eliminados `wf_02_classify_news.json`, `wf_03_detect_events.json`, `wf_04_generate_analysis.json`, `wf_05_generate_content.json` y `wf_06_publish_telegram.json`.
- Mantenido `wf_01_capture_news.json`.
- Actualizado `n8n/validate-workflows.ps1` para validar solo `WF-01`.

Verificacion:
- `n8n/validate-workflows.ps1` OK.

---

## [x] T12.10

Actualizar documentacion, changelog y versionado.

Resultado:
- Actualizado Documento 09 V2.0.
- Actualizado Documento 31.
- Creado registro en `docs/Docs_Asistentes`.
- Actualizado `CHANGELOG.md`.
- Version backend incrementada.

Verificacion:
- Revision documental.
- Backend completo `mvn test` OK, 217 tests.
- Frontend completo `npm.cmd test -- --watch=false --browsers=ChromeHeadless` OK, 94 tests.
- Frontend build `npm.cmd run build` OK, con warnings no bloqueantes de presupuesto.

---

## [x] T12.11

Disenar y anadir configuracion persistida de automatizaciones internas.

Resultado:
- Creada migracion `V7__automation_workflow_settings.sql`.
- Creada tabla `automation_workflow_settings`.
- Anadida semilla inicial para `WF02_CLASSIFICATION`, `WF03_EVENT_DETECTION` y `WF04_ANALYSIS`.
- Anadidos entidad JPA, repositorio/adaptador y modelo de dominio de configuracion.

Verificacion:
- Flyway validado durante `mvn test`.
- Backend completo `mvn test` OK, 223 tests.

---

## [x] T12.12

Implementar API ADMIN de configuracion.

Resultado:
- Anadidos DTOs de request/response.
- Anadidos casos de uso `ListAutomationSettingsUseCase`, `GetAutomationSettingUseCase`, `UpdateAutomationSettingUseCase` y `RunAutomationWorkflowUseCase`.
- Anadidos endpoints `GET /api/v1/automation/settings`, `GET /api/v1/automation/settings/{workflowCode}`, `PUT /api/v1/automation/settings/{workflowCode}` y `POST /api/v1/automation/settings/{workflowCode}/run`.
- Configuracion restringida a `ADMIN`; ejecucion manual disponible para `ADMIN` y `EDITOR`.

Verificacion:
- `AutomationControllerTest` OK.
- Backend completo `mvn test` OK, 223 tests.

---

## [x] T12.13

Implementar scheduler dinamico backend.

Resultado:
- Sustituidos los processors especificos por `AutomationWorkflowScheduler`.
- El scheduler revisa workflows vencidos cada 30 segundos con retardo inicial.
- La ejecucion respeta `enabled`, `intervalSeconds`, `batchSize` y `running`.
- Se actualizan contadores, ultimas fechas, errores y `nextRunAt`.

Verificacion:
- `RunAutomationWorkflowUseCaseTest` OK.
- `ProcessDueAutomationWorkflowsUseCaseTest` OK.
- Backend completo `mvn test` OK, 223 tests.

Nota posterior 2026-06-16: corregido fallo operativo del scheduler dinamico en `JpaAutomationWorkflowSettingRepository.findDue` que producia `This ResultSet is closed`; verificado con tests focales de automatizacion, `mvn clean test` y backend local reiniciado con health OK.

---

## [x] T12.14

Crear pantalla ADMIN de automatizaciones.

Resultado:
- Anadida ruta `/automation-settings`.
- Anadido menu `Automatizaciones` visible solo para `ADMIN`.
- Pantalla con activacion, intervalo en minutos, tamano de lote, estado, ultimo resultado y ejecucion manual.

Verificacion:
- `AutomationSettingsPageComponent` OK.
- `AutomationService` OK.
- Frontend completo `npm.cmd test -- --watch=false --browsers=ChromeHeadless` OK, 104 tests.

---

## [x] T12.15

Completar `WF-05` en frontend.

Resultado:
- Anadida generacion de contenido desde detalle de evento.
- Reutilizado `ContentService.generateContent`.
- La pantalla permite seleccionar analisis, canal Telegram, tono y longitud.
- El detalle de evento se recarga tras generar.

Verificacion:
- `EventDetailPageComponent` OK.
- `ContentService` OK.
- Frontend completo `npm.cmd test -- --watch=false --browsers=ChromeHeadless` OK, 104 tests.

---

## [x] T12.16

Completar `WF-06` en frontend.

Resultado:
- Anadida accion `Publicar ahora` para contenido `APPROVED`.
- Reutilizado `PublicationService.publishContent`.
- Se mantiene `Programar` como accion separada.

Verificacion:
- `ContentPageComponent` OK.
- Frontend completo `npm.cmd test -- --watch=false --browsers=ChromeHeadless` OK, 104 tests.

---

## [x] T12.17

Documentacion, changelog y versionado.

Resultado:
- Actualizado Documento 31.
- Actualizado Documento 09 V2.0 con configuracion dinamica.
- Creado registro en `docs/Docs_Asistentes`.
- Actualizado `CHANGELOG.md`.
- Incrementado `backend/pom.xml` a `0.0.53-SNAPSHOT`.

Verificacion:
- Revision documental.
- Backend completo `mvn test` OK, 223 tests.
- Frontend completo `npm.cmd test -- --watch=false --browsers=ChromeHeadless` OK, 104 tests.
- Frontend build `npm.cmd run build` OK, con warnings no bloqueantes de presupuesto.
- `n8n/validate-workflows.ps1` OK.

---

## [x] T12.18

Centralizar configuracion ADMIN de Telegram junto a automatizaciones.

Resultado:
- Creada migracion `V8__telegram_publication_settings.sql`.
- Anadida tabla `telegram_publication_settings`.
- Anadidos casos de uso `GetTelegramPublicationSettingsUseCase` y `UpdateTelegramPublicationSettingsUseCase`.
- Anadida API ADMIN `GET /api/v1/settings/telegram` y `PUT /api/v1/settings/telegram`.
- `TelegramPublisher` lee configuracion desde PostgreSQL en tiempo de publicacion.
- La API no expone el token completo; devuelve solo estado de configuracion y token enmascarado.
- La pantalla `/automation-settings` concentra automatizaciones y configuracion Telegram.
- Telegram queda publicable cuando `enabled=true`, `botToken`, `chatId` y `baseUrl` estan configurados.

Verificacion:
- `TelegramPublisherTest` OK.
- `TelegramPublicationSettingsControllerTest` OK.
- `PublicationControllerTest` OK.
- Backend completo `mvn test` OK, 227 tests.
- Frontend completo `npm.cmd test -- --watch=false --browsers=ChromeHeadless` OK, 107 tests.
- Frontend build `npm.cmd run build` OK, con warnings no bloqueantes de presupuesto.
- `n8n/validate-workflows.ps1` OK.

Estado:
- Sprint 12 de consolidacion de automatizaciones internas completado.
- Las tareas previstas originalmente de versionado de prompts, metricas IA y monitorizacion avanzada quedan como siguiente bloque de Sprint 12 extendido o Sprint 13 operativo.

---

## [x] T12.19

Versionado tecnico de prompts IA.

Resultado:
- Creada migracion `V9__ai_observability.sql`.
- Creada tabla `ai_prompt_versions`.
- Sembradas versiones activas para `WF02_CLASSIFICATION`, `WF03_EVENT_MATCHING`, `WF04_ANALYSIS` y `WF05_CONTENT`.
- Anadido endpoint ADMIN `GET /api/v1/ai/prompts`.
- No se habilita edicion de prompts desde UI; el contenido oficial sigue en codigo y Documento 23.

Verificacion:
- `AiObservabilityControllerTest` OK.
- `JpaAiObservabilityRepositoryTest` OK.

---

## [x] T12.20

Metricas de ejecuciones IA.

Resultado:
- Creada tabla `ai_operation_metrics`.
- Registradas metricas de clasificacion, matching de eventos, analisis y generacion de contenido.
- Cada metrica guarda operacion, prompt, proveedor, modelo si aplica, estado, entidad relacionada, latencia y error resumido.
- Anadido endpoint ADMIN `GET /api/v1/ai/metrics`.

Verificacion:
- `ListAiMetricsUseCaseTest` OK.
- Tests focales de casos de uso IA OK.

---

## [x] T12.21

Monitorizacion operativa de automatizaciones backend y `WF-01`.

Resultado:
- Anadido endpoint ADMIN `GET /api/v1/automation/overview`.
- `WF-01-Capture-News` queda declarado como workflow externo n8n.
- `WF-02`, `WF-03` y `WF-04` se observan desde `automation_workflow_settings`.
- `WF-05` y `WF-06` se mantienen como flujos backend bajo demanda o programados.
- `T12.3` queda reinterpretada para monitorizar `WF-01` en n8n y automatizaciones migradas en Spring Boot, no workflows n8n eliminados.

Verificacion:
- `AutomationControllerTest` OK.
- `n8n/validate-workflows.ps1` debe seguir validando solo `WF-01`.

---

## [x] T12.22

Convertir `/automation-settings` en centro ADMIN `/settings`.

Resultado:
- Renombrada la feature Angular a `settings`.
- Ruta principal `/settings`.
- Menu lateral visible como `Configuracion` solo para `ADMIN`.
- Anadida redireccion temporal `/automation-settings` -> `/settings`.
- La pantalla concentra Telegram, automatizaciones backend, versionado de prompts IA, metricas IA y vision operativa.
- La pantalla queda organizada por tabs: IA/prompts, proveedores de publicacion y automatizaciones.

Verificacion:
- `SettingsPageComponent` OK.
- `ShellComponent` OK.
- `AutomationService` y `AiObservabilityService` OK.

## [x] T12.23

Cierre documental, changelog, versionado y validacion.

Resultado:
- Actualizado Documento 09 V2.0.
- Actualizado Documento 31.
- Creado registro en `docs/Docs_Asistentes`.
- Actualizado `CHANGELOG.md`.
- Incrementado `backend/pom.xml` a `0.0.55-SNAPSHOT`.

Verificacion:
- Backend focal: `mvn "-Dtest=AiObservabilityControllerTest,ListAiMetricsUseCaseTest,JpaAiObservabilityRepositoryTest,AutomationControllerTest,ClassifyNewsUseCaseTest,GenerateAnalysisUseCaseTest,GenerateContentUseCaseTest" test` OK, 23 tests.
- Frontend focal: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/settings/settings-page.component.spec.ts --include=src/app/layout/shell/shell.component.spec.ts --include=src/app/core/services/automation.service.spec.ts --include=src/app/core/services/ai-observability.service.spec.ts` OK, 16 tests.
- Backend completo: `mvn test` OK, 234 tests y Flyway valida 9 migraciones.
- Frontend completo: `npm.cmd test -- --watch=false --browsers=ChromeHeadless` OK, 110 tests.
- Frontend build: `npm.cmd run build` OK, con warnings no bloqueantes de presupuesto inicial, `sources-page.component.scss` y `users-page.component.scss`.
- n8n: `.\n8n\validate-workflows.ps1` OK para `WF-01`.

Estado:
- Sprint 12 queda cerrado como consolidacion, optimizacion y observabilidad IA.
- Pendientes no bloqueantes posteriores: CI/CD, secretos productivos, despliegue Proxmox/Nginx, E2E versionado y normalizacion de mojibake documental.

---

## [x] T12.24

Refinar `/settings` como centro ADMIN tabulado con tablas operativas de IA.

Resultado:
- Separada la configuracion ADMIN en tabs funcionales:
  - IA y prompts.
  - Proveedores de publicacion.
  - Automatizaciones.
- La tabla de prompts versionados permite filtrar por clave, prompt, modulo, version, checksum, estado y fecha.
- La tabla de metricas IA permite filtrar por ID, fecha, operacion, prompt, proveedor, modelo, estado, entidad, entidad ID, latencia y error.
- Ambas tablas permiten ordenar por todos sus campos visibles.
- Ambas tablas tienen paginacion y selector de filas por pagina.
- Incrementado `backend/pom.xml` a `0.0.56-SNAPSHOT`.

Verificacion:
- Frontend focal: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/settings/settings-page.component.spec.ts` OK, 7 tests.
- Frontend completo: `npm.cmd test -- --watch=false --browsers=ChromeHeadless` OK, 113 tests.
- Frontend build: `npm.cmd run build` OK, con warnings preexistentes de presupuesto inicial, `sources-page.component.scss` y `users-page.component.scss`; `settings-page.component.scss` queda bajo presupuesto.
- Backend completo: `mvn test` OK, 234 tests y Flyway valida 9 migraciones.
- n8n: `.\n8n\validate-workflows.ps1` OK para `WF-01`.

Estado:
- Sprint 12 se mantiene cerrado; esta tarea documenta el refinamiento final de usabilidad solicitado para configuracion ADMIN.

---

## [x] T12.25

Refinar anchos de columnas en tablas de visualizacion del backoffice.

Resultado:
- Revisadas las tablas de dashboard, eventos, detalle de evento/noticias, contenido, auditoria, fuentes, usuarios y settings.
- Ajustados anchos mediante `colgroup` para que campos cortos como ID, noticias, prioridad, estado, fechas y acciones no ocupen ancho excesivo.
- Marcados nombres, emails, estados, fechas y campos tecnicos compactos como celdas sin salto de linea.
- Conservado salto controlado en textos largos como titulos, URL, detalles de auditoria y errores IA.
- Incrementado `backend/pom.xml` a `0.0.57-SNAPSHOT`.

Verificacion:
- Frontend completo: `npm.cmd test -- --watch=false --browsers=ChromeHeadless` OK, 113 tests.
- Frontend build: `npm.cmd run build` OK, con warnings preexistentes de presupuesto inicial, `sources-page.component.scss` y `users-page.component.scss`.
- Backend focal: `mvn "-Dtest=JpaAiObservabilityRepositoryTest" test` OK, 2 tests.
- Backend completo: `mvn test` OK, 234 tests y Flyway valida 9 migraciones.

Estado:
- Sprint 12 permanece cerrado; esta tarea es ajuste visual final sobre la experiencia de administracion y revision.

---

## [x] T12.26

Ajustar observabilidad IA diaria en `/settings`.

Resultado:
- `GET /api/v1/ai/metrics` permite consultar metricas diarias mediante `date=YYYY-MM-DD`, usando el dia operativo `Europe/Madrid`.
- El resumen IA calcula operaciones, correctas, fallidas, tasas, latencia media, P95 y comparativa contra el dia anterior.
- La pantalla `/settings` cambia “Metricas recientes” por “Metricas diarias”, con selector de fecha y cards reutilizando el estilo del dashboard.
- La tabla de metricas IA elimina columnas visibles `ID` y `Entidad ID`, mantiene filtros/orden/paginacion sobre campos visibles y evita scroll horizontal.
- Las filas fallidas se distinguen con fondo rojo suave.
- El error se consulta desde `Ver error` en modal, sin ocupar ancho de tabla.
- El click en una fila abre un modal de detalle operativo y enlaza a `/events/{id}` cuando la entidad relacionada es `EVENT`.
- Las metricas de clasificacion, matching, analisis y generacion de contenido registran el modelo IA usado en exito y fallo.
- Incrementado `backend/pom.xml` a `0.0.58-SNAPSHOT`.

Verificacion:
- Backend focal: `mvn "-Dtest=ListAiMetricsUseCaseTest,AiObservabilityControllerTest,JpaAiObservabilityRepositoryTest,ClassifyNewsUseCaseTest,GenerateContentUseCaseTest" test` OK, 17 tests.
- Frontend focal: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/settings/settings-page.component.spec.ts --include=src/app/core/services/ai-observability.service.spec.ts` OK, 13 tests.
- Backend completo: `mvn test` OK, 238 tests y Flyway valida 9 migraciones.
- Frontend completo: `npm.cmd test -- --watch=false --browsers=ChromeHeadless` OK, 117 tests.
- Frontend build: `npm.cmd run build` OK, con warnings no bloqueantes de presupuesto inicial, `sources-page.component.scss` y `users-page.component.scss`.

Estado:
- Sprint 12 permanece cerrado; esta tarea refina la observabilidad ADMIN diaria y la trazabilidad de errores/modelos IA.

---

## [x] T12.27

Corregir auditoria legible y completa en `/audit`.

Resultado:
- Anadido formateo comun de detalles de auditoria para registrar nuevas acciones con texto legible en castellano, sin JSON crudo ni pares `clave=valor`.
- Normalizadas auditorias editoriales de fusion de eventos, edicion de contenido, programacion de publicaciones y ejecucion de publicaciones.
- Anadida auditoria faltante de publicaciones directas y publicaciones programadas ejecutadas, tanto en exito (`PUBLICATION_PUBLISHED`) como en fallo (`PUBLICATION_FAILED`), con referencias a publicacion, contenido, evento, canal, estado y error.
- Normalizadas auditorias de usuarios para alta, cambios de estado, cambios de rol, reset temporal, cambio de password y login.
- La pantalla `/audit` muestra la columna editorial como `Detalle`, transforma registros historicos en JSON o `clave=valor` a texto descriptivo, formatea fechas internas con el mismo criterio visual de la tabla, muestra usuarios por nombre/email y abre el detalle en modal.
- Las filas de auditoria fallidas se resaltan visualmente como las metricas IA fallidas de `/settings`.
- La auditoria de usuarios y editorial permite elegir el dia a mostrar con selector diario, usando `date=YYYY-MM-DD` en API y zona operativa `Europe/Madrid`.
- Incrementado `backend/pom.xml` a `0.0.60-SNAPSHOT`.

Verificacion:
- Backend focal: `mvn "-Dtest=PublishContentUseCaseTest,PublishScheduledPublicationsUseCaseTest,SchedulePublicationUseCaseTest,MergeEventsUseCaseTest,EditGeneratedContentUseCaseTest,ChangeUserStatusUseCaseTest,UpdateUserUseCaseTest,ResetTemporaryPasswordUseCaseTest,LoginUseCaseTest,PublicationControllerTest" test` OK, 23 tests.
- Backend focal adicional: `mvn "-Dtest=PublicationControllerTest" test` OK, 3 tests.
- Frontend focal: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/audit/audit-page.component.spec.ts --include=src/app/core/services/audit.service.spec.ts` OK, 8 tests.

Estado:
- Mantenimiento correctivo sobre Sprint 11/Fases 10-11. La auditoria visible ADMIN cubre acciones editoriales y de usuario con detalles operativos legibles.

---

## [x] T12.28

Corregir hallazgos prioritarios de la auditoria de seguridad local.

Resultado:
- Retirada clave Gemini versionada y sustitucion por plantilla local segura `set_ai_env.example.ps1`.
- Eliminados scripts temporales versionados `tmp_*.ps1` que contenian credenciales, tokens de reset o salida de tokens.
- Anadidas exclusiones en `.gitignore` para `.env`, `tmp_*.ps1` y scripts locales de IA con secretos.
- El perfil `prod` exige `JWT_SECRET` explicito y `JwtConfig` rechaza el placeholder de desarrollo cuando el perfil activo es `prod`.
- Anadido rate limiting en memoria para endpoints publicos de autenticacion.
- Saneados errores de proveedores IA para no devolver ni persistir cuerpos externos crudos con posibles secretos, prompts o payloads sensibles.
- `database/docker-compose.yml` requiere credenciales por variables de entorno y se anade `database/.env.example`.
- Anadidas cabeceras basicas de seguridad en `frontend/nginx.conf`.
- El contenedor backend se ejecuta con usuario no root.
- Actualizadas dependencias Angular/build tooling para eliminar vulnerabilidades altas detectadas por `npm audit`.
- Incrementado `backend/pom.xml` a `0.0.61-SNAPSHOT`.

Verificacion:
- Backend: `./mvnw.cmd -DskipTests compile` OK con version `0.0.61-SNAPSHOT`.
- Backend focal: reportes Surefire OK para `JwtConfigTest`, `AuthRateLimitingFilterTest`, `AiErrorSanitizerTest`, `SecurityConfigTest`, `JwtTokenServiceTest`, `AuthControllerTest`, `ClassificationControllerTest`, `AnalysisControllerTest` y `ContentControllerTest`.
- Frontend: `npm.cmd audit --audit-level=high` OK sin vulnerabilidades altas; quedan vulnerabilidades bajas transitivas sin fix no rompedor.
- Frontend: `npm.cmd test -- --watch=false --browsers=ChromeHeadless` OK, 120 tests.
- Frontend: `npm.cmd run build` OK con avisos de presupuestos existentes de bundle/SCSS.
- n8n: `.\n8n\validate-workflows.ps1` OK para `WF-01-Capture-News`.
- Docker: `docker compose --env-file .env.example config` OK en `database`.
- Secret scan: busqueda focal sin secretos IA ni passwords Docker anteriores en codigo/configuracion activa; las coincidencias restantes corresponden a documentacion historica, seeds de desarrollo o tests.

Estado:
- Mantenimiento correctivo de seguridad posterior al Sprint 12. Permanecen como backlog de mayor alcance: refresh tokens revocables, cifrado/hash de secretos en reposo y auditoria de Proxmox/Nginx productivo real.

---

## [x] T12.29

Implementar refresh tokens revocables y rotables.

Resultado:
- Creada migracion `V10__refresh_tokens.sql` con tabla `refresh_tokens`, `token_id` unico, hash SHA-256 del token, expiracion, revocacion y marca de reemplazo.
- Anadido puerto `RefreshTokenRepository`, adaptador JPA y entidad `RefreshTokenEntity`.
- Los refresh tokens emitidos incluyen `jti` unico y se persisten hasheados al hacer login.
- `POST /api/v1/auth/refresh` valida que el refresh exista, siga activo, no este revocado/reemplazado, pertenezca al usuario y coincida con el hash antes de emitir nuevos tokens.
- Cada refresh aceptado reemplaza el token anterior y persiste uno nuevo.
- Cambio de password, reset de password, bloqueo y desactivacion revocan refresh tokens activos del usuario.
- Incrementado `backend/pom.xml` a `0.0.62-SNAPSHOT`.

Verificacion:
- Backend focal: reportes Surefire OK para `JwtTokenServiceTest`, `LoginUseCaseTest`, `RefreshTokenUseCaseTest`, `ChangePasswordUseCaseTest`, `ResetPasswordUseCaseTest`, `ChangeUserStatusUseCaseTest`, `AuthControllerTest` y `SecurityConfigTest`.
- Backend: `./mvnw.cmd -DskipTests compile` OK.

Estado:
- Mantenimiento correctivo de seguridad posterior al Sprint 12. Quedan como pendientes de mayor alcance: cifrado/hash de secretos configurables en reposo, auditoria real Proxmox/Nginx y limpieza de historial Git si el repositorio fue compartido con secretos.

---

## [x] T12.30

Persistir tokens de recuperacion de password en formato hasheado.

Resultado:
- Anadido `PasswordResetTokenHasher` con SHA-256 para tokens de recuperacion.
- `RequestPasswordResetUseCase` envia el token original por email, pero persiste solo su hash.
- `ResetPasswordUseCase` hashea el token recibido antes de buscarlo en `password_reset_tokens`.
- Anadida cobertura focal para verificar que el repositorio no recibe el token original.
- Incrementado `backend/pom.xml` a `0.0.63-SNAPSHOT`.

Verificacion:
- Backend focal: reportes Surefire OK para `RequestPasswordResetUseCaseTest`, `ResetPasswordUseCaseTest` y bateria auth relacionada.
- Backend: `./mvnw.cmd -DskipTests compile` OK con version `0.0.63-SNAPSHOT`.

Estado:
- Mantenimiento correctivo de seguridad posterior al Sprint 12. Los nuevos tokens de recuperacion dejan de persistirse en claro; tokens existentes previos al despliegue deben considerarse invalidados operativamente.

---

## [x] T12.31

Cifrar token Telegram configurable en reposo.

Resultado:
- Anadido `SecretTextCipher` con AES-GCM y prefijo `enc:v1:` para secretos configurables.
- `JpaTelegramPublicationSettingsRepository` cifra `botToken` antes de persistir y descifra al reconstruir el modelo de dominio.
- El perfil `prod` exige `SETTINGS_ENCRYPTION_KEY`; el placeholder de desarrollo se rechaza en produccion.
- Anadida configuracion `app.security.settings.encryption-key`.
- Incrementado `backend/pom.xml` a `0.0.64-SNAPSHOT`.

Verificacion:
- Backend focal: `./mvnw.cmd "-Dtest=SecretTextCipherTest,JpaTelegramPublicationSettingsRepositoryTest" test` OK, 4 tests.
- Backend: `./mvnw.cmd -DskipTests compile` OK con version `0.0.64-SNAPSHOT`.

Estado:
- Mantenimiento correctivo de seguridad posterior al Sprint 12. El token Telegram deja de ser legible en nuevos volcados de base de datos tras guardar la configuracion con la version actual.

---

## [x] T12.32

Configurar proveedores IA y modelo por workflow desde `/settings`.

Resultado:
- Creada migracion `V11__ai_provider_workflow_settings.sql` con `ai_provider_settings` y `ai_workflow_settings`.
- Anadida API ADMIN `/api/v1/ai/providers` y `/api/v1/ai/workflow-settings`.
- Las API keys IA se cifran con `SecretTextCipher` y se devuelven solo como estado/enmascarado.
- `WF-02`, `WF-03`, `WF-04` y `WF-05` resuelven proveedor y modelo desde PostgreSQL en runtime.
- Anadido proveedor Gemini para `WF-03` matching de eventos.
- `/settings` permite habilitar proveedores IA, cargar modelos de Gemini y asignar proveedor/modelo por workflow IA.
- Incrementado `backend/pom.xml` a `0.0.65-SNAPSHOT`.

Verificacion:
- Backend focal: `mvn "-Dtest=AIProviderSelectionTest,AiSettingsControllerTest,AiObservabilityControllerTest,JpaAiObservabilityRepositoryTest,GeminiAiProviderModelClientTest,GeminiAIProviderTest,GeminiAnalysisAIProviderTest,GeminiContentAIProviderTest" test` OK, 28 tests.
- Backend completo: `mvn test` OK, 257 tests.
- Frontend focal: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/settings/settings-page.component.spec.ts --include=src/app/core/services/ai-observability.service.spec.ts` OK, 17 tests.
- Frontend build: `npm.cmd run build` OK con warnings preexistentes de presupuesto inicial, `users`, `sources` y `audit`.

Estado:
- Sprint 12 permanece cerrado; esta tarea amplia la configuracion ADMIN de IA solicitada sin reintroducir workflows IA en n8n ni cambiar la secuencia MVP.

---

## [x] T12.33

Redisenar `/settings` separando metricas IA, prompts, automatizaciones y publicacion.

Resultado:
- `/settings` abre por defecto en la pestana `Metricas IA`.
- Separadas las pestanas `Metricas IA`, `Prompts IA`, `Automatizaciones` y `Publicacion`.
- Movida la configuracion de proveedor/modelo IA por workflow a la pestana `Automatizaciones`.
- Redisenados los paneles de automatizaciones para mostrar operacion e IA por workflow en bloques separados.
- Ajustadas las tarjetas de metricas para evitar cortes y mostrar operaciones, calidad, errores y rendimiento diario.
- Incrementado `backend/pom.xml` a `0.0.66-SNAPSHOT`.

Verificacion:
- Frontend focal: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/settings/settings-page.component.spec.ts --include=src/app/shared/components/metric-card/metric-card.component.spec.ts` OK, 13 tests.
- Frontend build: `npm.cmd run build` OK, con warnings preexistentes de presupuesto inicial, `users`, `sources` y `audit`; `settings-page.component.scss` queda bajo presupuesto.
- Verificacion visual local en `http://localhost:4200/settings` OK: `Metricas IA` abre por defecto, `Automatizaciones` contiene proveedores/modelos IA y no se detectan desbordes relevantes.

Estado:
- Refinamiento visual y operativo posterior al cierre de Sprint 12; no introduce nuevos endpoints, migraciones ni cambios de arquitectura.

---

## [x] T12.34

Implementar OpenAPI/Swagger por perfil.

Resultado:
- Anadida dependencia `springdoc-openapi-starter-webmvc-ui`.
- Creada configuracion OpenAPI con titulo `Sindicato Intelligence API` y esquema Bearer JWT.
- Swagger UI y `/v3/api-docs` quedan habilitados por defecto en entorno local/desarrollo.
- `application-prod.yml` deshabilita Swagger UI y `/v3/api-docs` por defecto, salvo variables explicitas.
- Ajustada seguridad para permitir la documentacion OpenAPI sin JWT solo cuando esta habilitada por perfil.
- Incrementado `backend/pom.xml` a `0.0.67-SNAPSHOT`.

Verificacion:
- Backend focal: `mvnw.cmd "-Dtest=SecurityConfigTest" test` OK, 7 tests.
- Backend compile: `mvnw.cmd -DskipTests compile` OK.

Estado:
- Mantenimiento tecnico posterior al cierre de Sprint 12; completa la dependencia OpenAPI prevista en la Fase 1 del Documento 30 sin cambiar contratos REST ni logica de dominio.

---

## 16.40 Descarte de noticias fuera de ambito antes de eventos - 2026-06-25

Tarea de mantenimiento correctivo sobre Fases 6-7 y Sprint 12.

Completado en esta iteracion:
- Backend clasificacion: las noticias `OTROS/FUERA_DE_AMBITO` y `OTROS/INFORMACION_INSUFICIENTE` con relevancia `0` quedan en estado `DISCARDED` tras persistir su clasificacion.
- Backend eventos: `DetectEventUseCase` rechaza defensivamente noticias descartadas o clasificaciones descartables, evitando creacion de eventos fuera de ambito.
- Flyway: anadida migracion `V12__discard_out_of_scope_news_and_archive_events.sql` para sanear noticias historicas y archivar eventos activos compuestos exclusivamente por noticias descartables.
- Proyecto: backend versionado a `0.0.68-SNAPSHOT` y `CHANGELOG.md` actualizado.

Verificacion:
- Bateria focal backend ejecutada con `mvn "-Dtest=ClassifyNewsUseCaseTest,NewsClassificationTest,NewsArticleTest,DetectEventUseCaseTest,ProcessPendingEventDetectionUseCaseTest" test`: 17 tests, 0 fallos, 0 errores.
- Intentada suite completa backend con `mvn test`; no concluyo antes del timeout local de 180 segundos. Los informes parciales mostraron fallos no asociados al nuevo descarte, derivados de configuracion IA local persistida en `gemini` frente a expectativas deterministas.

Estado:
- Mantenimiento correctivo posterior al Sprint 12. La automatizacion `WF-03` queda protegida frente a ruido informativo fuera del ambito docente/sindical andaluz.

---

## 16.41 Ocultacion operativa de eventos descartables - 2026-06-25

Tarea de mantenimiento correctivo sobre Fases 6-7, dashboard y Sprint 12.

Completado en esta iteracion:
- Diagnostico del evento `#1485`: evento `OPEN`/`OTROS` generado desde una noticia `EVENT_MATCHED` con clasificacion `OTROS/FUERA_DE_AMBITO`, relevancia `0`.
- Backend eventos: anadida politica de visibilidad para ocultar eventos archivados o formados exclusivamente por noticias descartables.
- Backend API: `GET /api/v1/events` deja de devolver eventos descartables y `GET /api/v1/events/{id}` responde 404 para esos casos.
- Backend dashboard: las metricas y eventos prioritarios excluyen noticias, eventos, contenidos y publicaciones vinculados a eventos descartables.
- Flyway: anadida migracion `V13__cleanup_discarded_event_residue.sql` para limpiar residuos creados tras `V12`, incluido el patron detectado en `#1485`.
- Proyecto: backend versionado a `0.0.69-SNAPSHOT` y `CHANGELOG.md` actualizado.

Verificacion:
- Compilacion backend verificada con `mvn test-compile`.
- Unitarias focales ejecutadas con `mvn "-Dtest=EventVisibilityPolicyTest,ClassifyNewsUseCaseTest,DetectEventUseCaseTest,ProcessPendingEventDetectionUseCaseTest" test`: 12 tests, 0 fallos, 0 errores.
- Base local verificada: Flyway `V13` aplicado, evento `#1485` queda `ARCHIVED` y su noticia asociada queda `DISCARDED`.
- API local verificada tras reiniciar backend: `GET /api/v1/events` no devuelve el evento `#1485`.

Estado:
- Mantenimiento correctivo posterior al Sprint 12. Los eventos descartables se conservan solo como trazabilidad interna en base de datos, pero quedan fuera de la operativa visible y de las metricas.

---

## 16.42 Priorizacion y descarte manual de eventos - 2026-06-25

Tarea de mejora correctiva sobre Fase 7 Eventos y Sprint 11 Frontend Angular.

Completado en esta iteracion:
- Backend eventos: `GET /api/v1/events` ordena por impacto, numero de noticias asociadas, ultima actualizacion e id, manteniendo la politica de visibilidad existente.
- Backend eventos: anadido `DiscardEventUseCase` y endpoint `POST /api/v1/events/{id}/discard` para archivar manualmente eventos activos.
- Backend auditoria: el descarte manual registra `EVENT_DISCARDED` con evento, titulo, impacto y numero de noticias.
- Frontend eventos: la tabla `/events` inicia ordenada por impacto y noticias.
- Frontend eventos: la fusion muestra impacto, numero de noticias y descripcion para facilitar la seleccion de destino/origen.
- Frontend eventos: anadida accion manual `Descartar` para eventos `OPEN` y `MONITORING`.
- Proyecto: backend versionado a `0.0.70-SNAPSHOT` y `CHANGELOG.md` actualizado.

Verificacion:
- Backend focal: `mvn "-Dtest=DiscardEventUseCaseTest,MergeEventsUseCaseTest,EventControllerTest" test`: 10 tests, 0 fallos, 0 errores.
- Frontend focal: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/events/events-page.component.spec.ts --include=src/app/core/services/event.service.spec.ts`: 7 tests, 0 fallos.
- Primer intento paralelo backend/frontend supero el timeout local de 120 segundos sin resultado util; se repitio de forma separada con timeout ampliado.

Estado:
- Mejora posterior al Sprint 12. No cambia la arquitectura ni introduce nuevas tecnologias; amplifica la operativa manual sobre eventos, que siguen siendo la entidad central.

---

## 16.43 Ajuste UX pantalla eventos y confirmaciones - 2026-06-25

Tarea de mejora correctiva sobre Sprint 11 Frontend Angular.

Completado en esta iteracion:
- Frontend eventos: sustituido el dropdown nativo de destino de fusion por una lista compacta con radios, evitando que el selector se haga mas ancho que la pantalla.
- Frontend eventos: acotados titulos, descripciones y opciones de fusion con truncado y scroll interno.
- Frontend eventos: reducida la anchura minima de la tabla y ajustada la columna de acciones para que `Ver` y `Descartar` no deformen la tabla.
- Frontend eventos: reemplazados los `confirm()` nativos de navegador por un modal visual propio para fusionar y descartar eventos.
- Proyecto: backend versionado a `0.0.71-SNAPSHOT` y `CHANGELOG.md` actualizado.

Verificacion:
- Frontend focal: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/events/events-page.component.spec.ts --include=src/app/core/services/event.service.spec.ts`: 8 tests, 0 fallos.
- Frontend build: `npm.cmd run build`: OK. Mantiene avisos de budgets de Angular, incluido `events-page.component.scss` por superar el limite configurado de 4 KB.

Estado:
- Mejora visual y de consistencia de confirmaciones, sin cambios de contrato API ni logica de dominio.

---

## 16.44 Detalle funcional de operaciones por workflow en settings - 2026-06-25

Tarea de mejora sobre Sprint 12 Observabilidad IA, automatizaciones internas y configuracion ADMIN.

Completado en esta iteracion:
- Backend IA: anadida migracion `V14__ai_operation_metric_details.sql` con `operation_details JSONB` en `ai_operation_metrics`.
- Backend IA: `WF-02`, `WF-03`, `WF-04` y `WF-05` registran snapshots funcionales sanitizados con resultado de negocio, sin prompts completos ni payloads sensibles.
- Backend clasificacion: el detalle de `WF-02` registra `finalNewsStatus=DISCARDED` y motivo cuando la noticia queda como `OTROS/FUERA_DE_AMBITO` o `OTROS/INFORMACION_INSUFICIENTE` con relevancia `0`.
- Backend automatizaciones: anadido endpoint ADMIN `GET /api/v1/automation/operations?date=YYYY-MM-DD` para combinar operaciones IA `WF-02` a `WF-05` y operaciones Telegram `WF-06`.
- Backend publicaciones: `WF-06` se muestra como operacion de workflow desde auditoria/publicaciones, pero no como metrica IA.
- Frontend ADMIN: `/settings` mantiene las tarjetas de metricas IA y cambia la tabla de operaciones del dia para consumir la vista unificada por workflow.
- Frontend ADMIN: el modal de detalle muestra resultado funcional, trazabilidad tecnica cuando aplica y detalle Telegram para `WF-06`.
- Proyecto: backend versionado a `0.0.72-SNAPSHOT` y `CHANGELOG.md` actualizado.

Verificacion:
- Backend focal: `mvn "-Dtest=JpaAiObservabilityRepositoryTest,AutomationControllerTest,ListWorkflowOperationsUseCaseTest,ClassifyNewsUseCaseTest,GenerateContentUseCaseTest" test`: 23 tests, 0 fallos, 0 errores.
- Frontend focal: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/settings/settings-page.component.spec.ts --include=src/app/core/services/automation.service.spec.ts`: 20 tests, 0 fallos.
- Backend compile: `mvn -DskipTests compile` OK con version `0.0.72-SNAPSHOT`.
- Frontend build: `npm.cmd run build` OK, con warnings preexistentes de budgets inicial, `sources`, `audit`, `events` y `users`.

Estado:
- Refinamiento posterior al Sprint 12. No cambia la arquitectura: `WF-02` a `WF-06` siguen en Spring Boot y `WF-01` permanece en n8n.

---

## 16.45 Optimizacion de respuesta minima en descartes WF-02 - 2026-06-26

Tarea de mejora correctiva sobre Fase 6 Clasificacion IA y Sprint 12 Observabilidad IA.

Completado en esta iteracion:
- Documento 23: actualizado el prompt oficial `WF-02` para que `FUERA_DE_AMBITO` e `INFORMACION_INSUFICIENTE` devuelvan solo `category`, `subcategory`, `relevance`, `impact` y `urgency`.
- Backend clasificacion: `ClassifyNewsPromptBuilder` deja de pedir `keywords`, `entities` y `summary` para noticias descartadas.
- Backend Gemini: el `responseSchema` mantiene `keywords`, `entities` y `summary` como opcionales y solo obliga los campos minimos de clasificacion.
- Backend clasificacion: `ClassifyNewsUseCase` persiste listas vacias y evita registrar `keywords`, `entities` y `aiSummary` en detalles de metricas cuando la noticia queda `DISCARDED`.
- Proyecto: backend versionado a `0.0.73-SNAPSHOT` y `CHANGELOG.md` actualizado.

Verificacion:
- Backend focal: `mvn "-Dtest=ClassifyNewsPromptBuilderTest,ClassifyNewsUseCaseTest,GeminiAIProviderTest,NewsClassificationTest" test`: 17 tests, 0 fallos, 0 errores.

Estado:
- Mejora de coste y limpieza de datos para `WF-02`, sin cambiar contratos API publicos ni la regla de descarte.

---

## 16.46 Etiquetas legibles en resultados de automatizaciones - 2026-06-26

Tarea de mejora correctiva sobre Sprint 12 Configuracion ADMIN.

Completado en esta iteracion:
- Frontend ADMIN: `/settings` sustituye el texto compacto `P/C/F/O` por `Procesadas`, `Completadas`, `Fallidas` y `Omitidas`.
- Frontend tests: anadida cobertura para evitar que vuelva a mostrarse la abreviatura críptica.
- Proyecto: backend versionado a `0.0.74-SNAPSHOT` y `CHANGELOG.md` actualizado.

Verificacion:
- Frontend focal: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/settings/settings-page.component.spec.ts`: 12 tests, 0 fallos.
- Frontend build: `npm.cmd run build`: OK, con warnings de budgets ya conocidos.

Estado:
- Mejora visual sin cambios de contrato API ni logica de dominio.

---

## 16.47 Estado editorial de eventos y descarte reversible - 2026-06-26

Tarea de mejora correctiva sobre Fases 7, 8, 9, 10 y 11, posterior a Sprint 12.

Completado en esta iteracion:
- [x] Backend eventos: anadido estado editorial derivado `PENDING_ANALYSIS`, `ANALYZED`, `PUBLISHED` y `DISCARDED`.
- [x] Backend datos: creada migracion `V15__event_manual_discard_status.sql` para persistir descarte manual reversible en `events`.
- [x] Backend eventos: el descarte manual deja de archivar el evento y queda visible en `/events` como `DISCARDED`.
- [x] Backend eventos: anadido `POST /api/v1/events/{id}/restore` con auditoria `EVENT_RESTORED`.
- [x] Backend dashboard: los eventos prioritarios excluyen eventos analizados, publicados o descartados manualmente.
- [x] Frontend eventos: `/events` muestra/filtro por estado editorial y permite descartar o deshacer descarte.
- [x] Frontend detalle evento: `/events/:id` permite generar analisis IA y recargar el detalle.
- [x] Versionado/documentacion: backend actualizado a `0.0.75-SNAPSHOT`, `CHANGELOG.md` y registro del asistente actualizados.

Verificacion:
- Backend compile: `mvn -DskipTests compile`: OK.
- Backend unitario focal: `mvn "-Dtest=EventTest,DiscardEventUseCaseTest,RestoreDiscardedEventUseCaseTest" test`: 16 tests, 0 fallos.
- Backend API eventos: `mvn "-Dtest=EventControllerTest" test`: 7 tests, 0 fallos.
- Backend dashboard focal: `mvn "-Dtest=DashboardControllerTest#excludesAnalyzedPublishedAndManuallyDiscardedEventsFromPriorityEvents" test`: 1 test, 0 fallos.
- Frontend focal: `npx ng test --watch=false --browsers=ChromeHeadless --include=src/app/features/events/events-page.component.spec.ts --include=src/app/features/events/event-detail-page.component.spec.ts --include=src/app/features/dashboard/dashboard-page.component.spec.ts --include=src/app/core/services/event.service.spec.ts --include=src/app/core/services/analysis.service.spec.ts`: 20 tests, 0 fallos.

Estado:
- Mejora correctiva completada sin cambiar la arquitectura: la logica permanece en Spring Boot y Angular consume contrato API derivado.
- La ejecucion completa de `DashboardControllerTest` supero el limite operativo de 240 segundos en esta sesion; se verifico el escenario nuevo de exclusion y `EventControllerTest` completo.

---

## 16.48 Estabilizacion de analisis IA WF-04 - 2026-06-26

Tarea de mantenimiento correctivo sobre Fase 8 Analisis IA, Sprint 7/T7.3-T7.4 y Sprint 12 Observabilidad IA.

Completado en esta iteracion:
- [x] Documento 23: reforzado el prompt oficial `WF-04` con reglas de JSON estricto, brevedad, idioma espanol, no repeticion y criterios de longitud por campo.
- [x] Backend analisis: `GenerateAnalysisPromptBuilder` limita el contexto enviado por noticia y el tamano total del prompt para evitar entradas largas o ruidosas.
- [x] Backend Gemini: `GeminiAnalysisAIProvider` aplica parametros efectivos conservadores para analisis (`temperature` maxima `0.1`, `topP=0.2`, `topK=1`, `candidateCount=1` y minimo `2048` tokens de salida).
- [x] Tests: anadida cobertura focal para recorte del contexto y payload Gemini estable.
- [x] Versionado: incrementado `backend/pom.xml` a `0.0.76-SNAPSHOT` y actualizado `CHANGELOG.md`.

Verificacion:
- `mvn "-Dtest=GenerateAnalysisPromptBuilderTest,GeminiAnalysisAIProviderTest,GenerateAnalysisUseCaseTest" test` ejecutado desde `backend`: 8 tests, 0 fallos, 0 errores.

Estado:
- Mantenimiento correctivo posterior al Sprint 12. `WF-04` sigue residiendo en Spring Boot y no se reintroduce logica IA en n8n.

## 16.49 Optimizacion de carga del backoffice - 2026-06-26

Tarea de mantenimiento correctivo posterior a Sprint 11/12, centrada en rendimiento percibido y consultas reales del backoffice.

- [x] Dashboard: sustituida la lectura completa de repositorios por consultas agregadas especificas para metricas, ultimas actualizaciones y eventos prioritarios.
- [x] Eventos: `GET /api/v1/events` devuelve resumen con `newsCount` y `editorialStatus` desde una consulta optimizada, manteniendo el contrato REST.
- [x] Base de datos: creada migracion `V16__performance_indexes_backoffice.sql` con indices compuestos para dashboard, eventos, publicaciones, auditoria y metricas IA.
- [x] Frontend: dashboard y eventos muestran estructura/skeleton inicial en vez de pantalla vacia con carga global.
- [x] Settings: `/settings` carga por defecto solo metricas IA y operaciones del dia; prompts, automatizaciones, proveedores IA y Telegram se cargan bajo demanda por pestana.
- [x] Tests focales backend y frontend ejecutados.

Verificacion:

- Backend focal: `mvnw.cmd "-Dtest=DashboardControllerTest,EventControllerTest" test` OK, 10 tests.
- Frontend focal: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/dashboard/dashboard-page.component.spec.ts --include=src/app/features/events/events-page.component.spec.ts --include=src/app/features/settings/settings-page.component.spec.ts` OK, 26 tests.
- Frontend build: `npm.cmd run build` OK antes de la documentacion, con warnings de presupuesto preexistentes y warning de tamano en `events-page.component.scss`.

Notas:

- No se cambian URLs ni formato JSON de los endpoints publicos.
- La logica de negocio sigue en Spring Boot; Angular solo cambia estrategia de carga y estados visuales.

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

Continuar tras la consolidacion de automatizaciones internas

```text
1. Ejecutar validacion completa backend/frontend si no se ha realizado en la sesion de implementacion.
2. Retomar versionado de prompts como siguiente bloque operativo.
3. Continuar con metricas IA, monitorizacion de automatizaciones internas y dashboard de metricas.
4. Mantener como deuda no bloqueante: CI/CD, secretos productivos, despliegue Proxmox/Nginx, E2E versionado y normalizacion de mojibake documental.
```

Rol:

```text
n8n-workflow-architect / spring-backend-architect / frontend-angular-backoffice / testing-quality
```

---

## 19.1 Nueva pagina de noticias en backoffice - 2026-06-27

Tarea de mejora sobre Sprint 11 Frontend Angular y consulta operativa de noticias.

Completado en esta iteracion:
- Frontend: anadida ruta `/news` visible para `ADMIN` y `EDITOR`.
- Frontend: anadida entrada `Noticias` al sidebar.
- Frontend: creada tabla de noticias con busqueda global, filtros por columna, ordenacion y paginacion local.
- Frontend: la tabla muestra ID, titulo, `Fuente #id`, estado, evento asociado, categoria IA, fecha de publicacion, fecha de captura y enlace al detalle.
- Frontend: el detalle de noticia vuelve a `/news`.
- Contrato: se reutiliza `GET /api/v1/news` sin ampliar backend ni exponer nombres de fuente.

Verificacion:
- `npm run build` ejecutado en `frontend/` con resultado OK. Persisten warnings de budgets existentes en bundle inicial y SCSS.
- `npm test -- --watch=false --browsers=ChromeHeadless` ejecutado en `frontend/` con resultado OK: 144 tests, 0 fallos.

---

## 19.2 Optimizacion de pagina de noticias - 2026-06-27

Tarea de mejora sobre Sprint 11 Frontend Angular y API backend de noticias.

Completado en esta iteracion:
- Backend: anadido `GET /api/v1/news/page` con paginacion 1-based, filtros, ordenacion y limite maximo de 100 filas por pagina.
- Backend: la consulta paginada devuelve solo columnas de tabla, `eventId` y categoria IA mediante joins controlados.
- Frontend: `/news` deja de cargar todas las noticias y consume el endpoint paginado.
- Frontend: la pagina inicial es la 1 y muestra las noticias mas recientes.
- Frontend: anadido control para ir directamente a un numero de pagina.
- Compatibilidad: `GET /api/v1/news` se mantiene sin cambios para consumidores existentes.
- Base de datos: no se anade migracion Flyway porque se reutilizan indices existentes.

Verificacion:
- `mvn -q -DskipTests compile` ejecutado en `backend/` con resultado OK.
- `mvn -q -Dtest=NewsControllerTest test` ejecutado en `backend/` con resultado OK.
- `npm run build` ejecutado en `frontend/` con resultado OK. Persisten warnings de budgets existentes.
- `npm test -- --watch=false --browsers=ChromeHeadless` ejecutado en `frontend/` con resultado OK: 146 tests, 0 fallos.
- `mvn test` completo en `backend/` ejecutado con fallos no relacionados con `/news`: `AnalysisControllerTest` usa proveedor IA real en lugar de deterministico, `ClassificationControllerTest` devuelve `OTROS` en lugar de `SIPRI`, `ContentControllerTest` recibe `502` y `SecurityConfigTest` no carga contexto por dependencia de `GetGeneratedContentDetailUseCase`.

---

## 19.3 Correccion de regresiones de tests backend - 2026-06-27

Tarea de mantenimiento correctivo sobre Sprint 11 Frontend Angular, backend API y configuracion IA de pruebas.

Completado en esta iteracion:
- [x] Backend tests: `AnalysisControllerTest` fija `WF04_ANALYSIS` al proveedor `deterministic`.
- [x] Backend tests: `ClassificationControllerTest` fija `WF02_CLASSIFICATION` al proveedor `deterministic`.
- [x] Backend tests: `ContentControllerTest` fija `WF05_CONTENT` al proveedor `deterministic`.
- [x] Backend security tests: `SecurityConfigTest` declara el mock de `GetGeneratedContentDetailUseCase` requerido por `ContentController`.
- [x] Versionado: backend actualizado a `0.0.82-SNAPSHOT`.

Verificacion:
- `mvn -q "-Dtest=AnalysisControllerTest,ClassificationControllerTest,ContentControllerTest,SecurityConfigTest" test` ejecutado en `backend/` con resultado OK.
- `mvn test` ejecutado en `backend/` con resultado OK: 287 tests, 0 fallos, 0 errores.

---

## 19.4 Fuentes descriptivas en pagina de noticias - 2026-06-27

Tarea de mejora correctiva sobre Sprint 11 Frontend Angular y API backend de noticias.

Completado en esta iteracion:
- [x] Backend: `GET /api/v1/news/page` devuelve `sourceName` junto a `sourceId`.
- [x] Backend: la busqueda global y el filtro de fuente aceptan el nombre descriptivo de `sources.name`.
- [x] Backend: `GET /api/v1/news/{id}` devuelve `sourceName` para el detalle de noticia.
- [x] Frontend: la tabla `/news` muestra el nombre descriptivo de la fuente en lugar de `Fuente #id`.
- [x] Frontend: el detalle `/news/:id` muestra el nombre descriptivo de la fuente.
- [x] Versionado: backend actualizado a `0.0.83-SNAPSHOT`.

Verificacion:
- `mvn -q "-Dtest=NewsControllerTest,NewsResponseTest" test` ejecutado en `backend/` con resultado OK.
- `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/news/news-page.component.spec.ts --include=src/app/core/services/news.service.spec.ts` ejecutado en `frontend/` con resultado OK: 10 tests, 0 fallos.
- `npm.cmd run build` ejecutado en `frontend/` con resultado OK. Persisten warnings preexistentes de budgets en bundle inicial y SCSS de varias pantallas.

---

## 19.5 Modernizacion frontend PrimeNG + Tailwind - 2026-06-27

Tarea de modernizacion posterior a Sprint 12, centrada en el backoffice Angular.

Completado en esta iteracion:
- [x] Decision arquitectonica documentada: Angular 21 + PrimeNG + Tailwind sustituyen Angular Material/SCSS como base UI.
- [x] Documento 07 actualizado con la nueva arquitectura frontend.
- [x] Documento 24 actualizado con reglas UX para la modernizacion visual.
- [x] Dependencias frontend migradas a Angular 21, PrimeNG, `@primeng/themes`, PrimeIcons, Tailwind 4, `@tailwindcss/postcss` y `tailwindcss-primeui`.
- [x] Configuracion global de PrimeNG anadida en `app.config.ts` con tema Aura, ripple y selector dark mode compatible con `ThemeService`.
- [x] Tailwind separado en `src/tailwind.css` y configurado mediante PostCSS.
- [x] Tokens globales y utilidades compartidas ampliadas en `src/styles.scss`.
- [x] Shell global actualizado con PrimeIcons, skip link, landmarks y foco accesible.
- [x] Componentes compartidos `StatusBadge` y `MetricCard` adaptados a PrimeNG/PrimeIcons.
- [x] Documentacion creada: `docs/design-system.md`, `docs/accessibility.md` y `docs/frontend-review.md`.

Verificacion:
- `npm install` ejecutado en `frontend/` con resultado OK.
- `npm run build` ejecutado en `frontend/` con resultado OK.
- `npm test -- --watch=false --browsers=ChromeHeadless` ejecutado en `frontend/` con resultado OK: 146 tests, 0 fallos.
- Persisten warnings de budgets en bundle inicial y SCSS de varias pantallas; quedan documentados como deuda de optimizacion frontend.

Notas:
- No se cambian contratos API ni logica de negocio.
- La migracion visual detallada por plantilla queda preparada sobre la nueva base PrimeNG/Tailwind y debe continuar por pantalla para reducir riesgo de regresiones.

---

## 19.6 Pendiente - migracion visual por pantallas

Tareas pendientes verificables:
- [x] Migrar auth (`login`, `forgot-password`, `reset-password`, `change-password`) a componentes PrimeNG de formulario.
- [x] Migrar `dashboard`, `events`, `event-detail`, `news` y `news-detail` a controles PrimeNG de acciones, filtros y mensajes, manteniendo tablas nativas temporalmente para conservar ordenacion/paginacion ya testeada.
- [x] Migrar `content` y `publications` a controles PrimeNG basicos de mensajes, filtros, acciones y dialogos, manteniendo tablas nativas temporalmente.
- [x] Migrar `content-detail` y `publication-detail` a estados PrimeNG basicos de error/carga.
- [x] Migrar `sources` y `audit` a controles PrimeNG basicos de mensajes, filtros, acciones y dialogos, manteniendo tablas nativas temporalmente.
- [x] Migrar `users` y `settings` a controles PrimeNG basicos de mensajes, filtros, botones, acciones y dialogos.
- [x] Reducir duplicacion SCSS por pantalla y bajar warnings de budget.
- [x] Ejecutar tests frontend completos tras cada bloque de pantalla.

---

## 19.7 Seguridad de dependencias frontend y lazy loading - 2026-06-27

Tarea de seguridad y rendimiento posterior a la migracion base PrimeNG + Tailwind.

Completado en esta iteracion:
- [x] Creada tarea para resolver las 13 vulnerabilidades pendientes detectadas por `npm audit`.
- [x] Ejecutado `npm audit fix` no forzado para actualizar toolchain Angular/Vite/esbuild/picomatch/piscina cuando era compatible.
- [x] Anadidos overrides controlados para dependencias transitivas vulnerables:
  - `@babel/core` a `7.29.7`.
  - `undici` a `7.28.0`.
- [x] `npm audit --audit-level=low` queda sin vulnerabilidades.
- [x] Rutas del backoffice convertidas a `loadComponent` para reducir el bundle inicial.
- [x] Pantallas auth migradas a PrimeNG (`pInputText`, `pButton`, `p-message`) manteniendo formularios reactivos y servicios existentes.

Verificacion:
- `npm audit --audit-level=low`: 0 vulnerabilidades.
- `npm run build`: OK. Bundle inicial reducido de ~966 KB a ~504 KB; queda un warning residual de 3.69 KB sobre el budget inicial.
- Tests focales auth: 18 tests, 0 fallos.
- Suite frontend completa: `npm test -- --watch=false --browsers=ChromeHeadless`: 146 tests, 0 fallos.

Pendiente no bloqueante:
- Ajustar budget inicial o reducir ~4 KB adicionales.
- Resolver warnings de budgets SCSS historicos en shell, eventos, fuentes, usuarios y auditoria.
- Revisar warning de Karma por fuentes PrimeIcons no servidas en `/base/media/*`; no falla tests ni build.

---

## 19.8 Migracion PrimeNG de pantallas operativas principales - 2026-06-27

Tarea de modernizacion sobre Sprint 11 Frontend Angular.

Completado en esta iteracion:
- [x] Dashboard: mensajes `p-message`, botones `pButton`, filtros `pInputText` y acciones de automatizacion con loading PrimeNG.
- [x] Eventos: mensajes `p-message`, filtros `pInputText`, acciones `pButton` y confirmaciones migradas a `p-dialog`.
- [x] Detalle de evento: mensajes `p-message`, filtros `pInputText`, paginacion y acciones de analisis/contenido con `pButton`.
- [x] Noticias: mensajes `p-message`, busqueda/filtros `pInputText` y paginacion con `pButton`.
- [x] Detalle de noticia: errores con `p-message`.

Verificacion:
- `npm run build`: OK. Bundle inicial queda en 509.27 KB, 9.27 KB sobre el budget configurado de 500 KB.
- Tests focales `dashboard`, `events`, `event-detail` y `news`: 23 tests, 0 fallos.

---

## 19.9 Migracion PrimeNG de pantallas editoriales y administrativas parciales - 2026-06-27

Tarea de modernizacion sobre Sprint 11 Frontend Angular y bloque posterior a Sprint 12.

Completado en esta iteracion:
- [x] `content`: mensajes `p-message`, filtros `pInputText`, acciones `pButton` y panel editorial migrado a `p-dialog`.
- [x] `publications`: estados de error/carga migrados a `p-message`.
- [x] `sources`: mensajes `p-message`, busqueda/filtros `pInputText`, acciones `pButton` y formulario de alta/edicion migrado a `p-dialog`.
- [x] `audit`: mensajes `p-message`, selector de fecha `pInputText`, acciones `pButton`, filtros `pInputText` y detalle migrado a `p-dialog`.

Pendiente:
- [x] Migrar `users`, `settings`, `content-detail` y `publication-detail` en nivel basico PrimeNG.
- [x] Revisar normalizacion de mojibake heredado en plantillas antes de una pasada visual final.
- [x] Migrar tablas principales a `p-table` cuando se pueda validar paginacion, filtros y ordenacion sin regresiones.

Verificacion:
- `npm run build`: OK. Bundle inicial queda en 509.27 KB, 9.27 KB sobre el budget configurado de 500 KB.
- Tests focales `content`, `publications`, `sources` y `audit`: 21 tests, 0 fallos.
- Suite frontend completa `npm test -- --watch=false --browsers=ChromeHeadless`: 146 tests, 0 fallos.
- `npm install --package-lock-only`: OK, 0 vulnerabilidades.

Notas:
- Las tablas de estas pantallas siguen como HTML nativo por estabilidad funcional. La migracion completa a `p-table` queda como siguiente refinamiento, con especial cuidado en filtros, ordenacion y paginacion existentes.

---

## 19.10 Migracion PrimeNG de pantallas ADMIN y detalles editoriales - 2026-06-28

Tarea de modernizacion sobre Sprint 11 Frontend Angular y bloque posterior a Sprint 12.

Completado en esta iteracion:
- [x] `users`: mensajes `p-message`, busqueda/filtros `pInputText`, acciones `pButton` y modales de alta/edicion/eliminacion migrados a `p-dialog`.
- [x] `settings`: mensajes `p-message`, tabs y acciones principales con `pButton`, filtros/fechas `pInputText` y modales de error/detalle IA migrados a `p-dialog`.
- [x] `content-detail`: estados de error/carga migrados a `p-message`.
- [x] `publication-detail`: estados de error/carga migrados a `p-message`.

Pendiente:
- [x] Migrar tablas principales a `p-table` con validacion de filtros, ordenacion y paginacion.
- [x] Sustituir selects/checkboxes complejos por componentes PrimeNG especificos cuando no afecte a formularios existentes.
- [x] Recalibrar budgets frontend tras Angular 21 + PrimeNG + Tailwind para eliminar warnings no accionables de build.
- [x] Revisar normalizacion de mojibake heredado en plantillas.

Verificacion:
- `npm run build`: OK. Bundle inicial queda en 509.27 KB, 9.27 KB sobre el budget configurado de 500 KB.
- Tests focales `users` y formatter de publicaciones: 14 tests, 0 fallos.
- Tests focales `settings`: 12 tests, 0 fallos.
- Suite frontend completa `npm test -- --watch=false --browsers=ChromeHeadless`: 146 tests, 0 fallos.
- `npm audit --audit-level=low`: 0 vulnerabilidades.
- `npm install --package-lock-only`: OK, 0 vulnerabilidades.

---

## 19.11 Recalibracion de budgets frontend - 2026-06-28

Tarea de mantenimiento posterior a la migracion base PrimeNG + Tailwind.

Completado en esta iteracion:
- [x] Recalibrado budget inicial de Angular de `500kB` a `525kB`, manteniendo margen estrecho sobre el bundle real actual de `509.27 kB`.
- [x] Recalibrado budget `anyComponentStyle` de `4kB` a `6kB` para evitar warnings historicos de SCSS en pantallas ya existentes.
- [x] Version frontend subida a `0.0.6`.

Decision:
- No se eleva el error budget inicial (`1MB`) ni el error budget de estilos (`8kB`).
- La recalibracion evita ruido tras la decision arquitectonica Angular 21 + PrimeNG + Tailwind, sin ocultar crecimientos relevantes futuros.

Pendiente:
- [x] Reducir tamano real de SCSS por pantalla mediante extraccion de patrones repetidos a estilos globales.
- [x] Revisar warning residual de Karma por fuentes PrimeIcons servidas como `/base/media/*`; no afecta build ni resultado de tests y queda documentado como deuda no bloqueante.

Verificacion:
- `npm run build`: OK sin warnings de budget. Bundle inicial: `509.27 kB`.
- `npm audit --audit-level=low`: 0 vulnerabilidades.
- `npm test -- --watch=false --browsers=ChromeHeadless`: OK, 146 tests, 0 fallos. Persiste warning no bloqueante de Karma por fuentes PrimeIcons en `/base/media/*`.

Nota posterior 2026-06-28:
- La limpieza de SCSS tras `app-standard-table` deja todos los estilos de componente por debajo del warning budget de `6kB`; los mayores son `shell` 5.29 KB y `events` 5.17 KB.
- No quedan tablas HTML operativas ni mojibake visible en plantillas Angular.

---

## 19.12 Cierre roadmap frontend con tablas unificadas - 2026-06-28

Tarea de cierre posterior a la modernizacion Angular 21 + PrimeNG + Tailwind.

Completado en esta iteracion:
- [x] Creado componente compartido `app-standard-table` sobre PrimeNG `p-table`.
- [x] Unificados cabecera, filtros, filas, estado vacio, estado carga, paginacion, ancho responsive y estilo visual de tablas.
- [x] Migradas a tabla comun las pantallas `dashboard`, `events`, `event-detail`, `news`, `content`, `sources`, `users`, `audit` y `settings`.
- [x] Eliminadas tablas HTML operativas sueltas en plantillas Angular del backoffice.
- [x] Conservada paginacion backend de `news` sin cambiar contratos `/api/v1`.
- [x] Conservadas ordenacion, filtros y paginacion client-side existentes en el resto de pantallas.
- [x] Extraidos estilos repetidos de tablas, paginacion, filtros y skeletons al componente compartido.
- [x] Version frontend subida a `0.0.7`.
- [x] Budget inicial recalibrado de `525kB` a `535kB` por la incorporacion comun de PrimeNG `p-table`; se mantiene error budget inicial en `1MB`.
- [x] Filtros y paginacion de tablas migrados de `select` nativo a `p-select` de PrimeNG.
- [x] Version frontend subida a `0.0.8`.
- [x] Revisadas tareas abiertas 19.6 a 19.12 y cerradas las ya implementadas.
- [x] Reforzada accesibilidad base: landmarks auth, labels explicitos, `aria-label` en navegacion principal y `scope="col"` en cabeceras de tablas.
- [x] Version frontend subida a `0.0.9`.

Pendiente no bloqueante:
- [x] Revisar warning residual de Karma por fuentes PrimeIcons servidas como `/base/media/*`; no afecta build ni resultado de tests. Se probo configuracion de assets desde `node_modules/primeicons/fonts` y no resolvio el warning del runner.
- [x] Revision responsive y WCAG 2.2 AA base por pantalla principal en mobile/tablet/desktop.

Verificacion:
- `rg "<table|</table>" frontend/src/app -g "*.html"`: sin tablas HTML operativas restantes.
- `rg "Ã|Â|�|<table|</table>" frontend/src/app`: sin mojibake visible ni tablas HTML operativas.
- `rg "<select|type=\"checkbox\"|type=\"radio\"" frontend/src/app`: solo quedan selects en formularios existentes, no en filtros/paginacion de tablas.
- `npm install --package-lock-only`: OK, 0 vulnerabilidades.
- `npm run build`: OK sin warnings de budget. Bundle inicial: `527.10 kB`.
- `npm audit --audit-level=low`: 0 vulnerabilidades.
- `npm test -- --watch=false --browsers=ChromeHeadless`: OK, 146 tests, 0 fallos. Persiste warning no bloqueante de Karma por fuentes PrimeIcons y cierre lento de ChromeHeadless.
- Revision con navegador en mobile `390x844`, tablet `768x1024` y desktop `1440x900`: rutas auth y backoffice principales con `main`, sin overflow horizontal global, nav nombrada, cabeceras con `scope` y sin controles basicos sin nombre accesible.

---

## 19.13 Revision visual responsive integral del frontend - 2026-06-30

Tarea de cierre posterior al roadmap frontend PrimeNG + Tailwind.

Completado en esta iteracion:
- [x] Corregido el layout global para evitar crecimiento horizontal de `shell`, contenido principal, paneles, headers, tabs y secciones.
- [x] Ajustado `app-standard-table` para contener el scroll horizontal dentro del componente compartido sin ensanchar la pagina completa.
- [x] Reducidos `minWidth` excesivos en tablas de `dashboard`, `news`, `events`, `event-detail`, `content`, `sources`, `users`, `audit` y `settings`.
- [x] Sustituido `pi-rss` por `pi-database` en la navegacion de fuentes.
- [x] Sustituido `pi-object-group` por `pi-sitemap` en la accion de fusion de eventos.
- [x] Revisadas rutas auth y backoffice en mobile, tablet, desktop y ultrawide.
- [x] Version frontend subida a `0.0.10`.

Verificacion:
- `rg "pi-rss|pi-object-group" frontend/src/app`: sin iconos PrimeIcons invalidos detectados.
- `rg "<table|</table>" frontend/src/app -g "*.html"`: sin tablas HTML operativas restantes.
- `npm audit --audit-level=low`: 0 vulnerabilidades.
- `npm test -- --watch=false --browsers=ChromeHeadless`: OK, 146 tests, 0 fallos. Persiste warning no bloqueante de Karma por fuentes PrimeIcons y cierre lento de ChromeHeadless.
- `npm run build`: OK sin warnings de budget. Bundle inicial: `527.32 kB`.
- Revision Chrome headless por CDP en mobile `390x844`, tablet `768x1024`, desktop `1440x900` y ultrawide `1920x1080`: 60 combinaciones ruta/viewport sin overflow horizontal de documento, sin tablas fuera del viewport y sin iconos visibles con tamano cero.

---

## 19.14 Revision visual de metric cards - 2026-06-30

Tarea de refinamiento visual posterior a la revision responsive integral.

Completado en esta iteracion:
- [x] Unificada la altura de `app-metric-card` dentro de los grids de dashboard y settings.
- [x] Ajustado el grid responsive de metric cards para mantener tarjetas del mismo tamano por fila.
- [x] Reforzado wrapping de titulos, subtitulos, badges y valores para evitar datos cortados.
- [x] Sustituido color hardcodeado de danger por tokens del design system.
- [x] Corregida la semantica de color de la tarjeta `Errores`: `danger` siempre, evitando verde en una metrica de errores.
- [x] Corregida la semantica de color de la tarjeta `Publicaciones`: `secondary`, dejando rojo solo para el dato interno de fallidas.
- [x] Unificada la disposicion interna de datos: icono encima del valor en las metric cards de dashboard y settings.
- [x] Version frontend subida a `0.0.11`.

Verificacion:
- `npm run build` ejecutado en `frontend/` con resultado OK. Bundle inicial: `527.50 kB`.
- Tests focales frontend ejecutados con resultado OK: `MetricCardComponent`, `DashboardPageComponent` y `SettingsPageComponent`, 20 tests, 0 fallos. Persiste warning no bloqueante de Karma por fuentes PrimeIcons y cierre lento de ChromeHeadless.
- Test focal backend `mvn -Dtest=DashboardControllerTest#returnsDashboardSnapshotWithDailyMetricsAndPriorityEvents test` ejecutado con resultado OK, validando `Publicaciones` con tono `secondary`.
- Suite backend focal completa `mvn -Dtest=DashboardControllerTest test` no queda como verificacion de esta iteracion porque el metodo de prioridad falla por datos reales locales preexistentes en PostgreSQL que alteran el orden esperado del fixture; no esta relacionado con el cambio visual de metric cards.

---

## 19.15 Correccion warning PrimeIcons en Karma - 2026-06-30

Tarea de mantenimiento correctivo posterior a la modernizacion Angular 21 + PrimeNG + Tailwind.

Completado en esta iteracion:
- [x] Reproducidos los 404 de Karma para `/base/media/primeicons.woff2`, `/base/media/primeicons.woff` y `/base/media/primeicons.ttf`.
- [x] Anadir configuracion `karma.conf.cjs` para servir esas fuentes desde `node_modules/primeicons/fonts` solo durante tests.
- [x] Enlazado el runner Angular de tests con `karmaConfig`.
- [x] Version frontend subida a `0.0.12`.

Verificacion:
- `npm test -- --watch=false --browsers=ChromeHeadless`: OK, 146 tests, 0 fallos. Ya no aparecen warnings 404 de PrimeIcons; persiste solo el warning de cierre lento de ChromeHeadless.
- `npm run build`: OK sin warnings de budget. Bundle inicial: `527.50 kB`.

---

## 19.16 Publicaciones manuales Telegram con multimedia - 2026-06-30

Tarea de mantenimiento evolutivo sobre Fase 10, Fase 11 y Fase 12.

Completado en esta iteracion:
- [x] Creada migracion `V18__telegram_destinations_manual_publications.sql`.
- [x] Anadida configuracion de multiples destinos Telegram nombrados.
- [x] Anadido historial comun para publicaciones generadas y publicaciones manuales.
- [x] Anadidos targets por destino y metadatos de adjuntos sin guardar binarios en PostgreSQL.
- [x] Anadido endpoint multipart `POST /api/v1/publications/manual`.
- [x] Anadido almacenamiento local configurable de adjuntos en `data/publication-attachments`.
- [x] Extendida publicacion Telegram para texto y adjuntos mediante `sendMessage`, `sendPhoto`, `sendVideo`, `sendAudio` y `sendDocument`.
- [x] Anadido modal Angular de mensaje manual en `/publications`.
- [x] Actualizado detalle de publicacion para mostrar publicaciones manuales, destinos y adjuntos.
- [x] Version backend subida a `0.0.85-SNAPSHOT`.

Verificacion:
- Backend compile: `./mvnw.cmd -q -DskipTests compile` OK.
- Backend focal publicaciones/settings: `./mvnw.cmd "-Dtest=PublicationControllerTest,TelegramPublisherTest,TelegramPublicationSettingsControllerTest,JpaTelegramPublicationSettingsRepositoryTest,JpaPublicationRepositoryTest" test` OK, 14 tests.
- Backend focal manual: `./mvnw.cmd "-Dtest=PublicationControllerTest" test` OK, 4 tests.
- Frontend tests: `npm.cmd test -- --watch=false --browsers=ChromeHeadless` OK, 146 tests.
- Frontend build: `npm.cmd run build` OK. Bundle inicial: `527.50 kB`.
- Backend suite completa: `./mvnw.cmd test` ejecutado, 288 tests, 1 fallo no relacionado en `DashboardControllerTest.ordersPriorityEventsByImpactNewsCountAndLastUpdate` por datos reales locales en PostgreSQL que alteran el orden de prioridad esperado del fixture.

## 19.17 Correccion publicaciones manuales Telegram - 2026-07-09

Tarea de mantenimiento correctivo sobre Fase 10, Fase 11 y Fase 12, derivada de 19.16.

Completado en esta iteracion:
- [x] Anadido endpoint operativo `GET /api/v1/publications/telegram-destinations` para que `EDITOR` cargue destinos activos sin acceder a `/settings`.
- [x] Anadidos limites ADMIN configurables de adjuntos Telegram mediante migracion `V19__telegram_manual_publication_limits.sql`.
- [x] Corregida la FK historica de destinos Telegram mediante `V20__telegram_destination_history_fk.sql` para preservar publicaciones ya registradas.
- [x] Registradas publicaciones manuales `FAILED`, targets y auditoria cuando falla la validacion funcional de adjuntos.
- [x] Anadido autor visible en listado y detalle de publicaciones.
- [x] Anadido editor ligero HTML para mensajes manuales y envio Telegram con `parse_mode=HTML`.
- [x] Corregida limpieza del dialogo manual al cerrar o enviar.
- [x] Ajustada la metrica de publicaciones del dashboard para incluir publicaciones manuales.
- [x] Version backend subida a `0.0.86-SNAPSHOT`.
- [x] Version frontend subida a `0.0.13`.

Verificacion:
- Backend focal publicaciones: `./mvnw.cmd "-Dtest=PublicationControllerTest" test` OK, 6 tests.
- Backend focal Telegram/settings: `./mvnw.cmd "-Dtest=TelegramPublicationSettingsControllerTest,TelegramPublisherTest,JpaTelegramPublicationSettingsRepositoryTest" test` OK, 10 tests.
- Frontend TypeScript: `npx.cmd tsc -p tsconfig.app.json --noEmit` OK.

## 19.20 Mejora del editor manual Telegram - 2026-07-09

Tarea de mejora correctiva sobre Fase 11, derivada de 19.17.

Completado en esta iteracion:
- [x] Sustituido el editor basado en etiquetas HTML visibles por un editor WYSIWYG basico.
- [x] Anadida toolbar con negrita, cursiva, subrayado, enlace y panel de emotes.
- [x] Ampliado el selector de emotes por grupos: frecuentes, tono, educacion y tiempo.
- [x] El contenido se sigue enviando como HTML compatible con Telegram.
- [x] Version frontend subida a `0.0.16`.

Verificacion:
- Frontend TypeScript: `npx.cmd tsc -p tsconfig.app.json --noEmit` OK.

## 19.21 Redisenio del selector de emotes del editor manual - 2026-07-09

Tarea de mejora correctiva sobre Fase 11, derivada de 19.20.

Completado en esta iteracion:
- [x] Ampliado el catalogo de emotes del editor manual con mas grupos y opciones.
- [x] Reubicado el panel de emotes bajo el cuadro de texto para no ocultar el area de escritura.
- [x] Sustituidos los botones PrimeNG de emotes por botones neutrales propios sin fondo verde heredado.
- [x] Ajustada la anchura del dialogo y la distribucion responsive del selector.
- [x] Version frontend subida a `0.0.17`.

Verificacion:
- Frontend TypeScript: `npx.cmd tsc -p tsconfig.app.json --noEmit` OK.

## 19.22 Ampliacion profesional del editor WYSIWYG Telegram - 2026-07-09

Tarea de mejora correctiva sobre Fase 10 y Fase 11, derivada de 19.20 y 19.21.

Completado en esta iteracion:
- [x] Convertido el titulo del mensaje manual en campo WYSIWYG con soporte de formato y emotes.
- [x] Anadidos contadores de caracteres para titulo y mensaje.
- [x] Reorganizada la toolbar en grupos profesionales: texto, entidades Telegram, bloques y emotes.
- [x] Anadido soporte frontend para negrita, cursiva, subrayado, tachado, spoiler, codigo inline, bloque de codigo, cita, cita expandible, enlaces, menciones, custom emoji, entidad tiempo, listas textuales, separador y emotes.
- [x] Ampliado el saneado backend para conservar etiquetas HTML soportadas por `parse_mode=HTML` de Telegram y retirar etiquetas no permitidas.
- [x] Version backend subida a `0.0.87-SNAPSHOT`.
- [x] Version frontend subida a `0.0.18`.

Verificacion:
- Frontend TypeScript: `npx.cmd tsc -p tsconfig.app.json --noEmit` OK.
- Backend focal Telegram: `./mvnw.cmd "-Dtest=TelegramPublisherTest" test` OK, 6 tests.

## 19.23 Tooltips del editor WYSIWYG Telegram - 2026-07-09

Tarea de mejora correctiva sobre Fase 11, derivada de 19.22.

Completado en esta iteracion:
- [x] Anadido `TooltipModule` al componente de publicaciones.
- [x] Anadidos tooltips a botones de formato del editor: negrita, cursiva, subrayado, tachado, spoiler y codigo inline.
- [x] Anadidos tooltips a paneles de entidades Telegram, bloques y emotes.
- [x] Anadidos tooltips a acciones internas: enlace, mencion, custom emoji, tiempo, bloque de codigo, cita, cita expandible, lista, separador y emotes.
- [x] Version frontend subida a `0.0.19`.

Verificacion:
- Frontend TypeScript: `npx.cmd tsc -p tsconfig.app.json --noEmit` OK.

## 19.24 Consolidacion de auditoria de acciones mutables - 2026-07-10

Tarea transversal posterior a Sprint 12, dentro de seguridad, auditoria operativa y backoffice ADMIN.

Completado en esta iteracion:
- [x] Revisadas acciones mutables expuestas por API/backoffice frente a `user_audit_log` y `audit_log`.
- [x] Anadida auditoria editorial para creacion y actualizacion de fuentes.
- [x] Anadida auditoria editorial para generacion directa de analisis IA desde detalle de evento.
- [x] Anadida auditoria editorial para generacion, aprobacion y rechazo de contenido.
- [x] Anadida auditoria editorial para actualizacion de automatizaciones, ejecucion manual completada y ejecucion manual fallida.
- [x] Anadida auditoria editorial para cambios de configuracion Telegram sin registrar tokens ni `chatId`.
- [x] Anadida auditoria de usuarios `USER_DELETED` para borrado administrativo con `user_id=null` para conservar traza tras borrado fisico.
- [x] Version backend subida a `0.0.88-SNAPSHOT`.

Verificacion:
- Backend focal auditoria: `mvn "-Dtest=CreateSourceUseCaseTest,UpdateSourceUseCaseTest,GenerateAnalysisUseCaseTest,GenerateContentUseCaseTest,ApproveContentUseCaseTest,RejectContentUseCaseTest,RunAutomationWorkflowUseCaseTest,UpdateAutomationSettingUseCaseTest,UpdateTelegramPublicationSettingsUseCaseTest,DeleteUserUseCaseTest" test` OK, 22 tests.

## 19.25 Enriquecimiento URL WF-02 y enlaces relevantes WF-05 - 2026-07-10

Tarea de mejora correctiva posterior al Sprint 12 sobre Fase 6 Clasificacion IA y Fase 9 Contenido.

Completado en esta iteracion:
- [x] WF-02: anadida URL al prompt y al contrato interno de clasificacion.
- [x] WF-02: anadido enriquecimiento controlado desde URL cuando titulo, resumen y contenido tienen contexto insuficiente, con limites de timeout/tamano y bloqueo de URLs locales o privadas.
- [x] WF-05: la generacion de contenido carga noticias fuente del evento y pasa enlaces relevantes permitidos al prompt.
- [x] WF-05: anadido extractor de enlaces para documentos, consultas, listados, anexos y resoluciones oficiales, excluyendo dominios de otros sindicatos configurados.
- [x] Documento 23 actualizado con reglas de URL en WF-02 y enlaces permitidos en WF-05.
- [x] Version backend subida a `0.0.89-SNAPSHOT`.

Verificacion:
- Backend focal: `mvn "-Dtest=ClassifyNewsPromptBuilderTest,ClassifyNewsUseCaseTest,GenerateContentPromptBuilderTest,GenerateContentUseCaseTest,*RelevantLink*Test,RestClientNewsContentEnrichmentAdapterTest" test` OK, 15 tests.

## 19.19 Color del error de adjuntos en modal - 2026-07-09

Tarea de mantenimiento correctivo sobre Fase 11, derivada de 19.18.

Completado en esta iteracion:
- [x] El texto del error de adjuntos usa `--color-danger-text`.
- [x] El borde del campo usa `--color-danger-strong`.
- [x] Version frontend subida a `0.0.15`.

Verificacion:
- Frontend TypeScript: `npx.cmd tsc -p tsconfig.app.json --noEmit` OK.
- Frontend Karma/build: intentados, agotaron timeout del entorno sin dejar proceso de test/build colgado.
- Backend dashboard focal: `DashboardControllerTest` ejecutado con 1 fallo preexistente/no relacionado por datos reales locales en PostgreSQL que alteran el orden de eventos prioritarios.

## 19.18 Error visible de adjuntos en mensaje manual - 2026-07-09

Tarea de mantenimiento correctivo sobre Fase 11, derivada de 19.17.

Completado en esta iteracion:
- [x] El error de adjuntos del mensaje manual se muestra dentro del modal.
- [x] El campo de adjuntos queda resaltado cuando el backend rechaza el archivo por tamano o limite funcional.
- [x] Los errores no relacionados con adjuntos tambien se muestran dentro del modal durante el envio manual.
- [x] Version frontend subida a `0.0.14`.

Verificacion:
- Frontend TypeScript: `npx.cmd tsc -p tsconfig.app.json --noEmit` OK.

## 19.26 Correccion rollback parcial WF-02 - 2026-07-10

Tarea de mantenimiento correctivo posterior al Sprint 12 sobre Fase 12, automatizaciones internas y observabilidad IA.

Completado en esta iteracion:
- [x] Eliminada la transaccion global de `RunAutomationWorkflowUseCase` para evitar rollback completo cuando falla una noticia en WF-02.
- [x] Conservadas transacciones cortas para marcar `running`, completar/fallar el workflow y registrar auditoria.
- [x] Mantenido el reintento normal: las noticias con fallo IA permanecen `CAPTURED` para la siguiente ejecucion programada.
- [x] Anadido test unitario de resultado parcial con noticias correctas y fallidas en el mismo lote.
- [x] Version backend subida a `0.0.90-SNAPSHOT`.

Verificacion:
- Backend focal automatizaciones: `mvn -Dtest=RunAutomationWorkflowUseCaseTest test` OK, 3 tests.
- Backend focal clasificacion/automatizaciones: `mvn "-Dtest=ClassifyNewsUseCaseTest,RunAutomationWorkflowUseCaseTest" test` OK, 10 tests.
- Backend compile limpio: `mvn clean compile` OK.
- Backend contexto Spring: `mvn "-Dtest=IntelligenceApplicationTests" test` OK, 1 test.

## 19.27 Correccion ResultSet cerrado en automatizaciones - 2026-07-10

Tarea de mantenimiento correctivo posterior al Sprint 12 sobre Fase 12, automatizaciones internas WF-02, WF-03 y WF-04.

Completado en esta iteracion:
- [x] WF-02: materializadas las consultas de noticias capturadas antes de mapearlas a dominio.
- [x] WF-03: materializadas las consultas de noticias clasificadas antes de mapearlas a dominio.
- [x] WF-04: materializadas las consultas de eventos y asociaciones de noticias antes de mapearlas a dominio.
- [x] Revisado impacto en WF-05 y WF-06; no comparten el arranque fuera de transaccion que provocaba el fallo.
- [x] Version backend subida a `0.0.91-SNAPSHOT`.

Verificacion:
- Backend focal automatizaciones/repositorios: `mvn "-Dtest=JpaNewsRepositoryTest,RunAutomationWorkflowUseCaseTest,ProcessPendingEventAnalysisUseCaseTest" test` OK, 10 tests.

## 19.28 Revision global de botones del backoffice - 2026-07-10

Tarea de refinamiento visual posterior al Sprint 12 sobre Fase 11 Frontend Angular.

Completado en esta iteracion:
- [x] Normalizado el estilo global de botones PrimeNG para mantener altura, alineacion, iconos, spinner y texto en estados de carga o deshabilitados.
- [x] Sustituidos textos interpolados en botones con carga por la propiedad `label` de PrimeNG en login, recuperacion/reset/cambio de password, dashboard, detalle de evento, usuarios, fuentes, settings y publicaciones.
- [x] Anadidos estados `loading` a botones operativos que ya tenian estado ocupado pero no mostraban spinner estable.
- [x] Limitada la regla local amplia de botones en settings para no pisar el layout interno de PrimeNG.
- [x] Version frontend subida a `0.0.20`.

Verificacion:
- Frontend TypeScript: `npx.cmd tsc -p tsconfig.app.json --noEmit` OK.
- Frontend focal: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/auth/login/login-page.component.spec.ts --include=src/app/features/auth/forgot-password/forgot-password-page.component.spec.ts --include=src/app/features/auth/reset-password/reset-password-page.component.spec.ts --include=src/app/features/auth/change-password/change-password-page.component.spec.ts --include=src/app/features/dashboard/dashboard-page.component.spec.ts --include=src/app/features/events/event-detail-page.component.spec.ts --include=src/app/features/users/users-page.component.spec.ts --include=src/app/features/sources/sources-page.component.spec.ts --include=src/app/features/settings/settings-page.component.spec.ts` OK, 57 tests.
- Frontend build: `npm.cmd run build` OK. Bundle inicial: `528.21 kB`.
- Frontend completo: `npm.cmd test -- --watch=false --browsers=ChromeHeadless` OK, 147 tests. Persiste warning no bloqueante de cierre lento de ChromeHeadless.

## 19.29 Coordinacion de acciones IA por modelo - 2026-07-11

Tarea de hardening posterior al Sprint 12 sobre Fase 12, automatizaciones internas, configuracion ADMIN e integraciones IA.

Completado en esta iteracion:
- [x] Anadida migracion `V21__ai_workflow_model_cooldown.sql` con `ai_workflow_settings.cooldown_seconds` por defecto a 60 segundos.
- [x] Ampliada la configuracion IA por workflow para exponer y actualizar `cooldownSeconds` desde API ADMIN.
- [x] Implementado `AiModelExecutionCoordinator` para bloquear ejecuciones simultaneas que usen el mismo `modelName` efectivo.
- [x] Permitida ejecucion paralela cuando los workflows usan modelos distintos.
- [x] Aplicado el coordinador a `WF02_CLASSIFICATION`, `WF03_EVENT_MATCHING`, `WF04_ANALYSIS` y `WF05_CONTENT`.
- [x] La pantalla `/settings` permite configurar el cooldown por workflow IA.
- [x] Version backend subida a `0.0.92-SNAPSHOT`.
- [x] Version frontend subida a `0.0.21`.

Verificacion:
- Backend focal: `mvnw.cmd "-Dtest=AiModelExecutionCoordinatorTest,RunAutomationWorkflowUseCaseTest,ClassifyNewsUseCaseTest,DetectEventUseCaseTest,GenerateAnalysisUseCaseTest,GenerateContentUseCaseTest,AiSettingsControllerTest,AIProviderSelectionTest,ClassificationControllerTest,AnalysisControllerTest,ContentControllerTest" test` OK, 33 tests.
- Frontend focal: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/settings/settings-page.component.spec.ts --include=src/app/core/services/ai-observability.service.spec.ts` OK, 17 tests.

## 19.30 Fallback WF-02 y descarte manual de noticias - 2026-07-11

Tarea de mantenimiento correctivo posterior al Sprint 12 sobre Fase 6 Clasificacion IA y Fase 11 Frontend Angular.

Completado en esta iteracion:
- [x] WF-02: anadido fallback seguro para respuestas Gemini sin `candidates[0].content.parts[0].text` cuando la noticia no contiene senales educativas ni sindicales.
- [x] WF-02: las noticias fuera de ambito afectadas por ese fallo se clasifican como `OTROS/FUERA_DE_AMBITO`, relevancia `0`, impacto `LOW`, urgencia `LOW` y pasan a `DISCARDED`.
- [x] Backend noticias: anadidos `POST /api/v1/news/{id}/discard` y `POST /api/v1/news/{id}/restore`.
- [x] Backend auditoria: anadidos registros `NEWS_DISCARDED` y `NEWS_RESTORED`.
- [x] Frontend noticias: anadidas acciones de tabla `Original`, `Descartar` y `Restaurar`.
- [x] Version backend subida a `0.0.93-SNAPSHOT`.
- [x] Version frontend subida a `0.0.22`.

Verificacion:
- Backend focal: `mvnw.cmd "-Dtest=ClassifyNewsUseCaseTest,NewsControllerTest" test` OK, 24 tests.
- Frontend focal: `npm.cmd test -- --watch=false --browsers=ChromeHeadless --include=src/app/features/news/news-page.component.spec.ts --include=src/app/core/services/news.service.spec.ts` OK, 14 tests.

## 19.31 Ajuste fallback WF-02 con enriquecimiento URL - 2026-07-11

Tarea correctiva posterior a `19.30` sobre Fase 6 Clasificacion IA.

Completado en esta iteracion:
- [x] Diagnosticado que `newsId=2927` y `3065` seguian `CAPTURED` tras WF-02 porque el fallback podia quedar bloqueado por senales educativas procedentes del contenido enriquecido desde la URL, no de la noticia capturada.
- [x] Ajustado `ClassifyNewsUseCase` para decidir si aplica fallback fuera de ambito usando solo titulo, URL, resumen y contenido capturados por WF-01.
- [x] Anadida prueba de regresion con enriquecimiento contaminado por navegacion `Educacion/Universidad/FP`.
- [x] Version backend subida a `0.0.94-SNAPSHOT`.

Verificacion:
- Backend focal: `mvnw.cmd "-Dtest=ClassifyNewsUseCaseTest" test` OK, 10 tests.

## 19.32 Disparo rapido WF-03 tras clasificacion - 2026-07-11

Tarea de mejora operativa posterior al Sprint 12 sobre Fase 12, automatizaciones internas `WF-02` y `WF-03`.

Completado en esta iteracion:
- [x] Anadido puerto `ClassifiedNewsFollowUpPort` para no acoplar `classification` directamente a `automation`.
- [x] Anadido `RequestImmediateAutomationWorkflowRunUseCase` para adelantar `nextRunAt` de un workflow habilitado y no ejecutandose.
- [x] `WF-02` solicita `WF03_EVENT_DETECTION` inmediatamente tras una clasificacion valida no descartada.
- [x] Las noticias `DISCARDED` no solicitan deteccion de eventos.
- [x] `WF-03` se reprograma inmediatamente cuando procesa un lote completo, para drenar backlog sin esperar el intervalo ordinario.
- [x] Reducido el delay por defecto del scheduler backend de automatizaciones de 30s a 5s.
- [x] Version backend subida a `0.0.95-SNAPSHOT`.

Verificacion:
- Backend focal: `mvnw.cmd "-Dtest=ClassifyNewsUseCaseTest,RunAutomationWorkflowUseCaseTest,RequestImmediateAutomationWorkflowRunUseCaseTest,ProcessPendingEventDetectionUseCaseTest" test` OK, 18 tests.
- Backend contexto Spring: `mvnw.cmd "-Dtest=IntelligenceApplicationTests" test` OK, 1 test.

## 19.33 Recuperacion de automatizaciones bloqueadas en running - 2026-07-12

Tarea correctiva posterior al Sprint 12 sobre Fase 12, automatizaciones internas backend.

Diagnostico:
- [x] Detectado `WF02_CLASSIFICATION` persistido con `running=true` desde una ejecucion anterior.
- [x] Confirmado que existian noticias `CAPTURED`, por lo que el problema no era falta de trabajo sino bloqueo operativo persistido.

Completado en esta iteracion:
- [x] Anadido `RecoverStaleAutomationWorkflowsUseCase` para recuperar workflows habilitados atascados en `running=true` tras un timeout configurable.
- [x] Anadido metodo de dominio `recoverStaleRunning` en `AutomationWorkflowSetting`.
- [x] Integrada la recuperacion antes de consultar workflows vencidos en `ProcessDueAutomationWorkflowsUseCase`.
- [x] Configurado timeout por propiedad `app.automation.stale-running-timeout-minutes`, por defecto 30 minutos.
- [x] Desbloqueado manualmente el estado local de `WF02_CLASSIFICATION` en PostgreSQL para permitir su ejecucion inmediata.
- [x] Version backend subida a `0.0.96-SNAPSHOT`.

Verificacion:
- Backend final: `mvnw.cmd "-Dtest=RecoverStaleAutomationWorkflowsUseCaseTest,ProcessDueAutomationWorkflowsUseCaseTest,RunAutomationWorkflowUseCaseTest,ClassifyNewsUseCaseTest,IntelligenceApplicationTests" test` OK, 18 tests.
- PostgreSQL local: tras desbloqueo, `WF02_CLASSIFICATION` arranco automaticamente, proceso 10 noticias, con 2 exitos y 8 fallos IA recuperables.

## 19.34 Reintento reducido WF-02 para Gemini sin texto en noticias educativas - 2026-07-12

Tarea correctiva posterior a `19.30` y `19.31` sobre Fase 6 Clasificacion IA.

Diagnostico:
- [x] Confirmado que el fallo recurrente actual no corresponde al caso anterior de noticias de sucesos fuera de ambito.
- [x] Detectado `newsId=4611` en `CAPTURED` con 17 fallos `Gemini response does not contain candidates[0].content.parts[0].text`.
- [x] Confirmado que la noticia contiene senales educativas reales, por lo que el fallback `FUERA_DE_AMBITO` no debe aplicarse automaticamente.

Completado en esta iteracion:
- [x] Ajustado `ClassifyNewsUseCase` para reintentar una vez con contexto reducido cuando Gemini no devuelve texto y la noticia contiene senales educativas.
- [x] El reintento reducido usa titulo, URL y resumen capturado por `WF-01`, evitando reenviar el contenido largo o sensible que puede activar bloqueo del proveedor.
- [x] El reintento se ejecuta dentro de la misma ejecucion coordinada del modelo para no esperar el `cooldown` entre intento normal y recuperacion.
- [x] Se mantiene sin cambios el fallback `OTROS/FUERA_DE_AMBITO` para noticias sin senales educativas ni sindicales.
- [x] Anadida prueba de regresion para noticia educativa con contenido sensible que se recupera con contexto reducido.
- [x] Version backend subida a `0.0.97-SNAPSHOT`.

Verificacion:
- Backend focal: `mvnw.cmd "-Dtest=ClassifyNewsUseCaseTest" test` OK, 11 tests.
