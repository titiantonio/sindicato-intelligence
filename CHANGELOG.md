# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Fixed

- Backoffice rendimiento: optimizadas las lecturas de `GET /api/v1/dashboard` y `GET /api/v1/events` con consultas agregadas, indices Flyway y carga Angular progresiva para reducir pantallas en blanco con "Cargando...".
- Backend IA WF-04: endurecida la generacion de analisis con contexto de noticias acotado, prompt JSON mas estricto y parametros Gemini conservadores para reducir respuestas repetitivas, mixtas o sin cierre JSON.

### Security

- Backend IA: las claves API de proveedores IA configurables se guardan cifradas en reposo y no se exponen en respuestas API, logs ni URLs de llamadas a Gemini.
- Auditoria seguridad: retirada clave Gemini versionada y scripts temporales con credenciales/tokens; se anade plantilla segura `set_ai_env.example.ps1` y exclusiones en `.gitignore`.
- Backend seguridad: el perfil productivo exige `JWT_SECRET` explicito y rechaza el secreto JWT placeholder; se anade rate limiting en memoria para endpoints publicos de autenticacion.
- Backend seguridad: anadida persistencia hasheada, revocacion y rotacion de refresh tokens; cambio/reset de password y bloqueo/desactivacion revocan sesiones activas.
- Backend seguridad: los nuevos tokens de recuperacion de password se persisten hasheados y solo se envia el token original por email.
- Backend seguridad: el token Telegram configurable se cifra en reposo mediante AES-GCM con clave externa `SETTINGS_ENCRYPTION_KEY`.
- Backend IA: saneados errores de proveedores IA para no devolver ni persistir cuerpos externos crudos con posibles secretos, prompts o payloads sensibles.
- Infraestructura: `database/docker-compose.yml` pasa a requerir secretos por variables de entorno y se anade `database/.env.example`; Nginx frontend incorpora cabeceras basicas de seguridad.
- Frontend seguridad: actualizadas dependencias Angular/build tooling para eliminar vulnerabilidades altas detectadas por `npm audit`.

### Added

- Backend/frontend eventos: anadido estado editorial derivado `PENDING_ANALYSIS`, `ANALYZED`, `PUBLISHED` y `DISCARDED` en listados, detalle y dashboard.
- Backend eventos: anadido descarte manual reversible con `POST /api/v1/events/{id}/restore`, persistencia Flyway y auditoria `EVENT_RESTORED`.
- Frontend eventos: el listado muestra y filtra estado editorial, permite deshacer descartes manuales y el detalle permite generar analisis IA del evento.
- Backend/Frontend ADMIN: anadido detalle funcional de operaciones por workflow en `/settings`, combinando metricas IA `WF-02` a `WF-05` con operaciones Telegram `WF-06`, incluyendo snapshots sanitizados de clasificacion, eventos, analisis, contenido y publicacion.
- Backend eventos: anadido descarte manual de eventos mediante `POST /api/v1/events/{id}/discard`, archivado operativo y auditoria `EVENT_DISCARDED`.
- Frontend eventos: anadida accion `Descartar` en la tabla de eventos activos y mas contexto de impacto, volumen y descripcion en la fusion de eventos.
- Backend eventos/dashboard: anadida politica de visibilidad para ocultar eventos formados solo por noticias descartables y excluirlos de metricas operativas.
- Backend clasificacion/eventos: anadido estado `DISCARDED` para noticias fuera de ambito o con informacion insuficiente, evitando que generen eventos en `WF-03`.
- Backend API: anadida documentacion OpenAPI/Swagger por perfil con Springdoc, esquema Bearer JWT y Swagger deshabilitado por defecto en produccion.
- Backend IA: anadida configuracion ADMIN de proveedores IA y seleccion de proveedor/modelo por workflow para `WF-02`, `WF-03`, `WF-04` y `WF-05`, con Gemini funcional y estructura extensible.
- Backend IA: anadido listado de modelos Gemini desde la API oficial filtrando modelos compatibles con `generateContent`.
- Backend IA: anadido proveedor Gemini para matching de eventos `WF-03`, manteniendo proveedor determinista para desarrollo y pruebas.
- Frontend ADMIN: `/settings` permite habilitar proveedores IA, reemplazar API keys, cargar modelos y asignar proveedor/modelo por workflow IA.
- Backend auditoria: anadido formateo legible para detalles de auditoria editorial y de usuarios, con referencias a eventos, contenidos, publicaciones, estados y errores.
- Backend auditoria: `GET /api/v1/audit/users` y `GET /api/v1/audit/editorial` aceptan `date=YYYY-MM-DD` para consultar auditoria diaria en zona operativa `Europe/Madrid`.
- Backend publicaciones: anadida auditoria de publicaciones directas y ejecuciones programadas tanto en exito como en fallo.
- Frontend auditoria: la pantalla `/audit` muestra detalles descriptivos en modal `Ver detalle`, transforma registros historicos en JSON o `clave=valor`, formatea fechas internas, muestra usuarios por nombre/email, resalta filas fallidas y permite elegir el dia a mostrar.
- Backend IA: `GET /api/v1/ai/metrics` permite consultar metricas diarias por `date=YYYY-MM-DD`, con resumen del dia, comparativa contra ayer, tasas de exito/fallo y latencia P95.
- Frontend ADMIN: `/settings` muestra metricas IA diarias con selector de fecha, cards estilo dashboard, modales de error/detalle y enlace a evento relacionado cuando aplica.
- Frontend ADMIN: `/settings` organiza la configuracion por tabs de IA/prompts, proveedores de publicacion y automatizaciones.
- Frontend ADMIN: las tablas de prompts versionados y metricas IA incorporan filtros por columna, ordenacion por todos sus campos visibles y paginacion con selector de filas.
- Backend IA: anadidas tablas `ai_prompt_versions` y `ai_operation_metrics` para versionado tecnico de prompts y observabilidad de operaciones IA.
- Backend IA: anadidos endpoints ADMIN `GET /api/v1/ai/prompts` y `GET /api/v1/ai/metrics`.
- Backend automatizaciones: anadido endpoint ADMIN `GET /api/v1/automation/overview` con estado de `WF-01` externo en n8n y resumen de automatizaciones backend.
- Frontend ADMIN: renombrada la pantalla operativa a `/settings` como centro de `Configuracion`, con redireccion temporal desde `/automation-settings`.
- Frontend ADMIN: `/settings` muestra configuracion Telegram, automatizaciones backend, versionado de prompts IA y metricas recientes de operaciones IA.
- Backend configuracion: anadida tabla `telegram_publication_settings` y API ADMIN `GET/PUT /api/v1/settings/telegram` para configurar Telegram desde la aplicacion sin depender de variables de entorno en runtime.
- Backend publicacion: `TelegramPublisher` lee `enabled`, `baseUrl`, `botToken`, `chatId` y `disableWebPagePreview` desde PostgreSQL antes de publicar.
- Frontend ADMIN: la pantalla `/automation-settings` pasa a concentrar configuracion de automatizaciones y Telegram, mostrando si Telegram esta listo para publicar.
- Backend automatizaciones: anadida configuracion persistida `automation_workflow_settings`, API ADMIN `/api/v1/automation/settings`, scheduler dinamico cada 30 segundos y ejecucion con `batchSize` configurable para `WF02`, `WF03` y `WF04`.
- Frontend ADMIN: anadida pantalla `/automation-settings` para activar/desactivar automatizaciones, editar intervalo/lote, ver ultima/proxima ejecucion y lanzar ejecucion manual.
- Frontend eventos/contenido: anadida generacion de contenido desde detalle de evento y accion `Publicar ahora` para contenido aprobado.
- Backend automatizaciones: anadidos `POST /api/v1/automation/classifications/run`, `POST /api/v1/automation/events/run` y `POST /api/v1/automation/analysis/run` para ejecutar desde Spring Boot los procesos antes cubiertos por `WF-02` a `WF-04`.
- Backend automatizaciones: anadidos casos de uso batch y schedulers configurables para clasificacion pendiente, deteccion de eventos pendiente y analisis pendiente.
- Frontend dashboard: anadidas acciones operativas para clasificar pendientes, detectar eventos y generar analisis desde la API Spring Boot.
- Frontend contenido: anadido modal editorial en `/content` para visualizar contenido desde la fila y editar desde accion explicita, manteniendo programacion de publicaciones dentro del modal.
- Frontend backoffice: anadida paginacion, selector de filas, filtros por columna y ordenacion por todos los campos visibles en las tablas de fuentes, eventos, auditoria, contenido, detalle de evento y dashboard.
- Backend usuarios: anadidas notificaciones por email para activacion, desbloqueo, actualizacion de usuario y eliminacion fisica.
- Frontend usuarios: anadido boton "Alta de usuario", modal comun de alta/edicion, busqueda global, filtros por columna, ordenacion, paginacion y selector de filas por pagina en `/users`.
- Backend usuarios: anadido `DELETE /api/v1/users/{id}` para `ADMIN`, con bloqueo de borrado por autoeliminacion, ultimo `ADMIN` o referencias funcionales en contenido/auditoria.
- Frontend fuentes: anadido boton "Anadir fuente", modal de alta, modal de edicion y tests focales de la pantalla `/sources`.
- Frontend fuentes: anadida busqueda global, filtros por columna y ordenacion por todos los campos visibles de la tabla de fuentes.
- Frontend shell: anadido modo colapsado del menu lateral en escritorio con navegacion por iconos y boton de expandir/ocultar.
- Dashboard: contrato ampliado de tarjetas metricas con titulo, subtitulo, icono, etiqueta, ultima actualizacion e indicadores internos por tarjeta.
- Dashboard: metricas comparativas con valores de hoy, ayer y diferencia para noticias capturadas, eventos detectados, contenidos pendientes y publicaciones realizadas.
- Frontend eventos: busqueda global, filtros por columna y ordenacion ascendente/descendente por todos los campos visibles de la tabla `/events`.
- Backend auth: anadido endpoint `POST /api/v1/auth/refresh` y caso de uso `RefreshTokenUseCase` para emitir nuevos access/refresh tokens a partir de refresh tokens validos.
- Frontend UI: anadido modo claro/oscuro global con `ThemeService`, preferencia persistida, interruptor transversal y tokens CSS aplicados al backoffice y pantallas de autenticacion.
- Sprint 11 cierre MVP: implementados POST /api/v1/events/merge, editor manual PUT /api/v1/content/{id}, scheduling POST /api/v1/publications/{contentId}/schedule, estado SCHEDULED, scheduler automatico de publicaciones vencidas y auditoria visible ADMIN (/api/v1/audit/users, /api/v1/audit/editorial).
- Frontend Sprint 11: anadidas acciones de fusion de eventos, editor manual de contenido, programacion de publicaciones, visualizacion de publicaciones SCHEDULED/scheduledAt y pantalla ADMIN /audit.
- Testing Sprint 11: anadidos tests backend para merge, editor manual, scheduling y scheduler, y tests Angular para servicios de eventos/contenido/publicaciones/auditoria y pantalla de auditoria.
- Cierre tecnico Sprint 11: anadido `n8n/validate-workflows.ps1` para validar JSON, autenticacion JWT/Bearer y endpoints esperados en `WF-01` a `WF-06`.
- Aceptacion Sprint 11: anadidos `scripts/validate-sprint11-acceptance.ps1` y `scripts/fake-telegram-server.ps1` para validar localmente flujo MVP completo con MailHog y Telegram fake.
- Documentacion final: creado `docs/Documentacion_Final/Manual_Operativo_Usuario.md` como manual operativo de usuario en espanol para Administradores y Editores.

- n8n: alineada autenticacion JWT en `WF-02` a `WF-06` con login tecnico `Authenticate Backend` y cabeceras `Authorization: Bearer` en endpoints protegidos.
- T11.10.5: anadida cobertura frontend focal para AuthService, UserAdminService, StorageService, guards de auth/roles/cambio de password y pantallas criticas de login, recuperacion, reset, cambio obligatorio y gestion de usuarios.
- Sprint 11: anadidos endpoints reales de lectura para eventos, detalle de evento, contenido generado, publicaciones y dashboard; el backoffice Angular consume APIs reales para dashboard, eventos, detalle de evento, contenido, publicaciones y fuentes, eliminando `MockDashboardService`.
- T10.5/T11.10: anadido puerto `UserAccountNotificationSender` e implementacion SMTP compatible con MailHog para notificar cambio de password, bloqueo, desactivacion y reset temporal; normalizada la UI de cambio obligatorio y gestion de usuarios con mensajes de confirmacion y botones semanticos.
- T10.5/T11.10: reforzado el flujo de gestion de usuarios con alta exclusiva para `ADMIN` sin password en request, generacion automatica de password temporal por email, estado inicial `PENDING_ACTIVATION`, expiracion configurable, regeneracion de password temporal expirada, auditoria de acciones, bloqueo/desbloqueo, fechas de ultimo login y ultimo cambio de password, y pantalla Angular de cambio obligatorio en primer login.
- T10.5: implementada la gestion de usuarios para `ADMIN` con API `GET /api/v1/users`, `GET /api/v1/users/{id}`, `POST /api/v1/users`, `PUT /api/v1/users/{id}` y `POST /api/v1/users/{id}/disable`, junto con casos de uso de alta, consulta, listado, edicion y desactivacion.
- T10.5: implementado flujo completo de recuperacion de password con `POST /api/v1/auth/forgot-password`, `POST /api/v1/auth/reset-password`, tokens temporales con expiracion y repositorio JPA dedicado.
- T10.5: añadido envio de correo SMTP para recuperacion de password y configuracion `spring.mail`/`app.security.password-reset`.
- T11.10: añadidas pantallas Angular `forgot-password`, `reset-password` y `users`, enlace "Olvide mi password" en login, ruta protegida de usuarios para `ADMIN` y servicios frontend para integracion con API de auth/users.

- T10.1: creada base JWT con `JwtTokenService`, configuracion `app.security.jwt`, soporte access/refresh tokens y pruebas unitarias de claims y expiracion.
- T10.2: implementado soporte de roles `ADMIN` y `EDITOR` con modelo `UserAccount`, repositorio `UserRepository`, adaptador JPA y `DatabaseUserDetailsService` para Spring Security.
- T10.3: protegidos endpoints con seguridad stateless JWT, reglas por rol `ADMIN/EDITOR` y conversion de authorities desde claim `roles`.
- T10.4: implementado endpoint `POST /api/v1/auth/login` con autenticacion email/password, respuesta con access/refresh tokens y datos de usuario, y manejo `401` para credenciales invalidas.
- T11.1: creado el proyecto Angular 20 del backoffice en `frontend/` con routing, SCSS, proxy local hacia `/api/v1` y estructura inicial `core/shared/layout/features`.
- T11.2: implementado layout principal responsive con shell, sidebar, cabecera y menu dinamico por rol `ADMIN/EDITOR`.
- T11.3: implementada la pantalla de login conectada a `POST /api/v1/auth/login`, guardas de autenticacion/rol e interceptor JWT.
- Sprint 11 visual: creadas pantallas mock de `dashboard`, `events`, `content` y `publications` para avanzar la experiencia de backoffice mientras se completan los endpoints reales.

### Changed

- Versionado del backend actualizado a `0.0.75-SNAPSHOT`.
- Backend dashboard: los eventos prioritarios excluyen eventos analizados, publicados o descartados manualmente.
- Versionado del backend actualizado a `0.0.74-SNAPSHOT`.
- Frontend ADMIN: el resultado de automatizaciones en `/settings` muestra etiquetas completas de procesadas, completadas, fallidas y omitidas en lugar de la abreviatura `P/C/F/O`.
- Versionado del backend actualizado a `0.0.73-SNAPSHOT`.
- Backend IA/WF-02: las noticias descartadas por `FUERA_DE_AMBITO` o `INFORMACION_INSUFICIENTE` usan respuesta minima de clasificacion y no generan ni registran `keywords`, `entities` ni `summary`.
- Versionado del backend actualizado a `0.0.71-SNAPSHOT`.
- Frontend eventos: redisenada la fusion para sustituir el dropdown largo por listas compactas, contener titulos/descripciones, ajustar la tabla de acciones y usar un modal propio de confirmacion para fusionar o descartar eventos.
- Versionado del backend actualizado a `0.0.70-SNAPSHOT`.
- Backend/frontend eventos: el listado general de eventos y la tabla `/events` priorizan inicialmente por impacto, numero de noticias y ultima actualizacion.
- Versionado del backend actualizado a `0.0.69-SNAPSHOT`.
- Backend datos: anadida migracion Flyway adicional para limpiar eventos residuales creados tras el primer saneamiento de noticias fuera de ambito.
- Versionado del backend actualizado a `0.0.68-SNAPSHOT`.
- Backend datos: anadida migracion Flyway para marcar noticias descartables historicas y archivar eventos activos compuestos exclusivamente por noticias fuera de ambito.
- Versionado del backend actualizado a `0.0.67-SNAPSHOT`.
- Versionado del backend actualizado a `0.0.66-SNAPSHOT`.
- Frontend ADMIN: `/settings` abre por defecto en `Metricas IA`, separa prompts, automatizaciones y publicacion en pestanas independientes, y mueve la seleccion de proveedor/modelo IA por workflow a `Automatizaciones`.
- Frontend metricas: ajustadas las tarjetas de metricas para evitar cortes o desbordes en valores largos y mostrar indicadores IA diarios de operaciones, calidad, errores y rendimiento.
- Versionado del backend actualizado a `0.0.64-SNAPSHOT`.
- Versionado del backend actualizado a `0.0.63-SNAPSHOT`.
- Versionado del backend actualizado a `0.0.62-SNAPSHOT`.
- Versionado del backend actualizado a `0.0.61-SNAPSHOT`.
- Versionado del backend actualizado a `0.0.60-SNAPSHOT`.
- Versionado del backend actualizado a `0.0.59-SNAPSHOT`.
- Backend IA: las metricas de clasificacion, matching de eventos, analisis y generacion de contenido registran el modelo IA usado tambien en fallos.
- Frontend ADMIN: la tabla de metricas IA elimina columnas tecnicas `ID` y `Entidad ID`, compacta anchos sin scroll horizontal y marca filas fallidas con fondo rojo suave.
- Versionado del backend actualizado a `0.0.58-SNAPSHOT`.
- Frontend backoffice: ajustados anchos de columnas y comportamiento de salto de linea en todas las tablas de visualizacion para compactar campos cortos y evitar cortes en nombres, emails y textos principales.
- Versionado del backend actualizado a `0.0.57-SNAPSHOT`.
- Versionado del backend actualizado a `0.0.56-SNAPSHOT`.
- Versionado del backend actualizado a `0.0.55-SNAPSHOT`.
- Versionado del backend actualizado a `0.0.54-SNAPSHOT`.
- Frontend ADMIN: renombrado el encabezado operativo de `/automation-settings` a `Configuracion` para centralizar parametros de aplicacion.
- Versionado del backend actualizado a `0.0.53-SNAPSHOT`.
- Backend automatizaciones: sustituidos los schedulers especificos por workflow por un scheduler unico dirigido por configuracion en PostgreSQL.
- Versionado del backend actualizado a `0.0.52-SNAPSHOT`.
- n8n: `WF-01-Capture-News` queda como unico workflow operativo; `WF-02` a `WF-06` se migran a Spring Boot y el validador n8n comprueba solo `WF-01`.
- Versionado del backend actualizado a `0.0.51-SNAPSHOT`.
- Dashboard: los eventos prioritarios se ordenan por impacto, numero de noticias asociadas y ultima actualizacion, y los filtros de impacto y estado usan selectores cerrados.
- Versionado del backend actualizado a `0.0.50-SNAPSHOT`.
- Frontend contenido: el boton `Editar` queda deshabilitado para contenido `REJECTED` y `PUBLISHED`, manteniendose activo para `PENDING_REVIEW` y `APPROVED`.
- Versionado del backend actualizado a `0.0.49-SNAPSHOT`.
- Versionado del backend actualizado a `0.0.48-SNAPSHOT`.
- Frontend usuarios: los mensajes de edicion, activacion, desbloqueo y eliminacion informan que se envia notificacion por email.
- Versionado del backend actualizado a `0.0.47-SNAPSHOT`.
- Frontend usuarios: aclarada la diferencia operativa entre `Desactivar` como baja administrativa y `Bloquear` como bloqueo reversible por incidencia.
- Versionado del backend actualizado a `0.0.46-SNAPSHOT`.
- Frontend fuentes: eliminado el formulario fijo "Nueva fuente" de la pantalla y ajustado el layout para que el ancho sobrante quede contenido en el scroll interno de la tabla.
- Versionado del backend actualizado a `0.0.45-SNAPSHOT`.
- Dashboard: cada tarjeta metrica calcula y muestra su propia fecha y hora de ultima actualizacion segun los datos de noticias, eventos, contenidos o publicaciones.
- Dashboard: ajustado el layout de indicadores para mantener cabeceras y valores largos en una misma linea mediante tamanos responsivos.
- Frontend shell: reducido y estabilizado el ancho del sidebar para evitar que pantallas con tablas anchas desconfiguren el layout.
- Versionado del backend actualizado a `0.0.44-SNAPSHOT`.
- Dashboard: redisenadas las cuatro tarjetas metricas con cabecera, tres indicadores internos, iconos SVG inline y estilos adaptados a tema claro/oscuro.
- Versionado del backend actualizado a `0.0.43-SNAPSHOT`.
- Dashboard: la tabla de eventos prioritarios muestra hasta 10 eventos activos de importancia `HIGH` o `CRITICAL`, excluye categoria `OTROS` y permite navegar al detalle del evento desde la fila.
- Versionado del backend actualizado a `0.0.42-SNAPSHOT`.
- Versionado del backend actualizado a `0.0.41-SNAPSHOT`.
- Frontend UI: sustituido el interruptor textual de tema por un boton con icono de luna/sol integrado en la cabecera del backoffice, antes del bloque de usuario/logout.
- Frontend auth: ocultado el token en la pantalla de establecimiento de nueva password; se mantiene internamente desde el enlace de recuperacion y se muestra error si falta.
- Frontend auth: simplificada la pantalla de recuperacion de password para dejar una unica accion de envio de enlace de restablecimiento, retirando el boton de password temporal de esa vista.
- Documento 31 actualizado con evidencia final de `mvn test`, Flyway, tests/build frontend y validacion n8n; la proxima tarea queda como aceptacion manual final de Sprint 11 antes de abrir Sprint 12.
- Documento 31 actualizado con aceptacion local final de Sprint 11 y roadmap corregido para abrir Sprint 12.

- Versionado del backend actualizado a .0.40-SNAPSHOT.

- Versionado del backend actualizado a `0.0.39-SNAPSHOT`.
- Versionado del backend actualizado a `0.0.38-SNAPSHOT`.
- Versionado del backend actualizado a `0.0.37-SNAPSHOT`.
- WF-01: separada la cuenta tecnica de n8n en un usuario propio `n8n@sindicato.es` con credenciales dedicadas y seeding Flyway especifico para automatizaciones.
- WF-01: añadido login tecnico de servicio para obtener JWT antes de llamar a endpoints protegidos del backend desde n8n, con credenciales de desarrollo en `database/docker-compose.yml`.
- Flyway inicial consolidado de nuevo para fase de implementacion: `V1__create_mvp_schema.sql` incorpora `password_reset_tokens` (antes `V5`) y `V2__seed_admin_user.sql` incorpora la semilla de `n8n@sindicato.es` (antes `V4`), dejando la secuencia en `V1..V3` para reinicios desde cero.
- Sprint 10 de Seguridad completado y versionado del backend actualizado a `0.0.29-SNAPSHOT`.
- Ajustada la configuracion de Spring Security para API stateless JWT desactivando `formLogin` y `httpBasic`.
- Renombrada la carpeta del frontend de `fronted/` a `frontend/` para alinear el repositorio con la documentacion y el nuevo proyecto Angular.
- Versionado del proyecto actualizado a `0.0.32-SNAPSHOT` para reflejar la separacion de la cuenta tecnica de n8n.
- Añadido servicio `mailhog` en `database/docker-compose.yml` para pruebas locales del flujo de recuperacion por email.
- Versionado del backend actualizado a `0.0.33-SNAPSHOT`.
- Versionado del backend actualizado a `0.0.34-SNAPSHOT`.
- Versionado del backend actualizado a `0.0.35-SNAPSHOT`.
- Versionado del backend actualizado a `0.0.36-SNAPSHOT`.

### Fixed

- Backend tests: `JpaAiObservabilityRepositoryTest` deja de depender de que la base de integracion no tenga metricas IA recientes previas.
- Backend automatizaciones: corregido el scheduler dinamico para materializar las consultas JPA de configuracion antes de mapearlas, evitando `This ResultSet is closed` en `findDue`.
- Backend publicaciones: el scheduler de publicaciones programadas ya no propaga `publication provider not found for channel: TELEGRAM` al arrancar si hay publicaciones vencidas y el proveedor Telegram no esta habilitado; marca la publicacion como `FAILED` con error controlado.
- Frontend/auth: renovada automaticamente la sesion con `POST /api/v1/auth/refresh` cuando el access token caduca, reintentando la peticion original para evitar el error "No se pudo cargar el dashboard" tras inactividad.

### Security

- Backend auth: los refresh tokens dejan de incluir claims `role`/`roles`, evitando que puedan actuar como bearer token de autorizacion en endpoints protegidos.

## [0.0.28-SNAPSHOT] - 2026-06-09

### Added

- Sprint 11 cierre MVP: implementados POST /api/v1/events/merge, editor manual PUT /api/v1/content/{id}, scheduling POST /api/v1/publications/{contentId}/schedule, estado SCHEDULED, scheduler automatico de publicaciones vencidas y auditoria visible ADMIN (/api/v1/audit/users, /api/v1/audit/editorial).
- Frontend Sprint 11: anadidas acciones de fusion de eventos, editor manual de contenido, programacion de publicaciones, visualizacion de publicaciones SCHEDULED/scheduledAt y pantalla ADMIN /audit.
- Testing Sprint 11: anadidos tests backend para merge, editor manual, scheduling y scheduler, y tests Angular para servicios de eventos/contenido/publicaciones/auditoria y pantalla de auditoria.

- T9.1: creado el modulo backend `publication` con capas `domain`, `application`, `infrastructure` y `api`, entidad de dominio `Publication`, enum `PublicationStatus` y puerto `PublicationRepository`.
- T9.2: creado el puerto `PublishingProvider` con contratos `PublishingRequest`, `PublishingResult` y excepcion `PublishingProviderException` para integraciones de publicacion.
- T9.3: implementado `TelegramPublisher` con Bot API de Telegram, configuracion por variables de entorno, logs seguros y parseo acotado de respuestas.
- T9.4: creado el workflow n8n `WF-06-Publish-Telegram` para publicar contenido aprobado mediante la API backend y reintentar errores Telegram.
- T9.5: implementado `PublishContentUseCase`, persistencia JPA de `publications` y API `POST /api/v1/publications/{id}/publish` para registrar publicaciones `PENDING`, `PUBLISHED` y `FAILED`.

### Changed

- Versionado del backend actualizado a .0.40-SNAPSHOT.

- Sprint 9 de Publicacion Telegram completado y versionado del backend actualizado a `0.0.28-SNAPSHOT`.

## [0.0.27-SNAPSHOT] - 2026-06-09

### Added

- Sprint 11 cierre MVP: implementados POST /api/v1/events/merge, editor manual PUT /api/v1/content/{id}, scheduling POST /api/v1/publications/{contentId}/schedule, estado SCHEDULED, scheduler automatico de publicaciones vencidas y auditoria visible ADMIN (/api/v1/audit/users, /api/v1/audit/editorial).
- Frontend Sprint 11: anadidas acciones de fusion de eventos, editor manual de contenido, programacion de publicaciones, visualizacion de publicaciones SCHEDULED/scheduledAt y pantalla ADMIN /audit.
- Testing Sprint 11: anadidos tests backend para merge, editor manual, scheduling y scheduler, y tests Angular para servicios de eventos/contenido/publicaciones/auditoria y pantalla de auditoria.

- T8.1: creado el modulo backend `content` con capas `domain`, `application`, `infrastructure` y `api`, entidad de dominio `GeneratedContent`, enum `ContentStatus` y puerto `GeneratedContentRepository`.
- T8.2: implementado `GenerateContentUseCase` con puerto `ContentAIProvider`, resolucion de evento/analisis, autor transitorio mediante `CurrentContentAuthorProvider` y logs operativos.
- T8.3: implementado el prompt oficial `WF-05` para contenido Telegram, proveedor determinista de contenido y proveedor Gemini especifico con salida JSON estructurada y reintentos recuperables.
- T8.4: añadida persistencia JPA de `generated_content`, API `POST /api/v1/content/generate`, endpoints de aprobacion/rechazo y workflow n8n `WF-05-Generate-Content`.

### Changed

- Versionado del backend actualizado a .0.40-SNAPSHOT.

- Sprint 8 de Contenido completado y versionado del backend actualizado a `0.0.27-SNAPSHOT`.

## [0.0.26-SNAPSHOT] - 2026-06-08

### Added

- Sprint 11 cierre MVP: implementados POST /api/v1/events/merge, editor manual PUT /api/v1/content/{id}, scheduling POST /api/v1/publications/{contentId}/schedule, estado SCHEDULED, scheduler automatico de publicaciones vencidas y auditoria visible ADMIN (/api/v1/audit/users, /api/v1/audit/editorial).
- Frontend Sprint 11: anadidas acciones de fusion de eventos, editor manual de contenido, programacion de publicaciones, visualizacion de publicaciones SCHEDULED/scheduledAt y pantalla ADMIN /audit.
- Testing Sprint 11: anadidos tests backend para merge, editor manual, scheduling y scheduler, y tests Angular para servicios de eventos/contenido/publicaciones/auditoria y pantalla de auditoria.

- T7.1: creado el modulo backend `analysis` con capas `domain`, `application`, `infrastructure` y `api`, entidad de dominio `EventAIAnalysis` y puerto `EventAIAnalysisRepository`.
- T7.2: implementado `GenerateAnalysisUseCase` con contratos de aplicacion para IA de analisis, carga de evento/noticias asociadas, persistencia mediante puerto y logs operativos `INFO`, `WARN` y `ERROR`.
- T7.3: implementado el prompt oficial `WF-04` para analisis de eventos, proveedor determinista de analisis y proveedor Gemini especifico con salida JSON estructurada, diagnostico acotado y reintentos recuperables.
- T7.4: añadida persistencia JPA de `event_ai_analysis`, endpoint `POST /api/v1/analysis/generate` y workflow n8n `WF-04-Generate-Analysis` para orquestar la generacion de analisis por `eventId`.

### Changed

- Versionado del backend actualizado a .0.40-SNAPSHOT.

- Sprint 7 de Analisis IA completado y versionado del backend actualizado a `0.0.26-SNAPSHOT`.

## [0.0.25-SNAPSHOT] - 2026-06-08

### Changed

- Versionado del backend actualizado a .0.40-SNAPSHOT.

- Ajustada la salida de consola de Logback para colorear los niveles de log con el conversor ANSI de Spring Boot, manteniendo los archivos persistidos sin codigos de color.
- Activada la salida ANSI por defecto mediante `spring.output.ansi.enabled`, configurable con `SPRING_OUTPUT_ANSI_ENABLED`.
- Versionado del backend actualizado a `0.0.25-SNAPSHOT`.

## [0.0.24-SNAPSHOT] - 2026-06-07

### Added

- Sprint 11 cierre MVP: implementados POST /api/v1/events/merge, editor manual PUT /api/v1/content/{id}, scheduling POST /api/v1/publications/{contentId}/schedule, estado SCHEDULED, scheduler automatico de publicaciones vencidas y auditoria visible ADMIN (/api/v1/audit/users, /api/v1/audit/editorial).
- Frontend Sprint 11: anadidas acciones de fusion de eventos, editor manual de contenido, programacion de publicaciones, visualizacion de publicaciones SCHEDULED/scheduledAt y pantalla ADMIN /audit.
- Testing Sprint 11: anadidos tests backend para merge, editor manual, scheduling y scheduler, y tests Angular para servicios de eventos/contenido/publicaciones/auditoria y pantalla de auditoria.

- Añadidos logs operativos a los casos de uso existentes de `source`, `news` y `event`, cubriendo inicio, fin, conteos, descartes, duplicados y fallos recuperables.
- Añadido reintento controlado en `GeminiAIProvider` para respuestas recuperables sin texto, sin JSON o con JSON invalido.
- Añadido diagnostico acotado de respuestas Gemini sin `candidates[0].content.parts[0].text`, incluyendo `finishReason`, `blockReason` y `safetyRatings` sin registrar prompts completos ni secretos.

### Changed

- Versionado del backend actualizado a .0.40-SNAPSHOT.

- Los fallos de clasificacion IA ahora registran la excepcion completa en backend para facilitar diagnostico desde archivo de log.
- Versionado del backend actualizado a `0.0.24-SNAPSHOT`.

## [0.0.23-SNAPSHOT] - 2026-06-07

### Added

- Sprint 11 cierre MVP: implementados POST /api/v1/events/merge, editor manual PUT /api/v1/content/{id}, scheduling POST /api/v1/publications/{contentId}/schedule, estado SCHEDULED, scheduler automatico de publicaciones vencidas y auditoria visible ADMIN (/api/v1/audit/users, /api/v1/audit/editorial).
- Frontend Sprint 11: anadidas acciones de fusion de eventos, editor manual de contenido, programacion de publicaciones, visualizacion de publicaciones SCHEDULED/scheduledAt y pantalla ADMIN /audit.
- Testing Sprint 11: anadidos tests backend para merge, editor manual, scheduling y scheduler, y tests Angular para servicios de eventos/contenido/publicaciones/auditoria y pantalla de auditoria.

- Añadida configuracion `logback-spring.xml` con salida a consola, archivo general, archivo de errores, rotacion diaria por tamaño, compresion `.gz`, carpetas mensuales y retencion de 90 dias.
- Añadida variable `LOG_PATH` para configurar la ubicacion de logs, con valor por defecto `logs`.
- Creada la skill `sindicato-logging-observabilidad` para guiar la incorporacion de logs seguros y utiles en nuevas implementaciones backend.

### Changed

- Versionado del backend actualizado a .0.40-SNAPSHOT.

- Actualizado `AGENTS.md` para exigir logs operativos en nuevas funcionalidades backend y registrar la nueva skill de logging.
- Versionado del backend actualizado a `0.0.23-SNAPSHOT`.

## [0.0.22-SNAPSHOT] - 2026-06-07

### Fixed

- Corregida la llamada a Gemini para enviar el prompt de sistema como `systemInstruction` y el texto de la noticia como contenido de usuario, evitando que el modelo responda con una reformulacion de las instrucciones.
- Añadido `responseSchema` en `GeminiAIProvider` para forzar una salida JSON con las claves y enums esperados por el contrato de clasificacion.
- Reducido temporalmente `WF-02-Classify-News` a 1 noticia por ejecucion mientras se estabiliza la salida JSON del proveedor IA y se evita lanzar rafagas paralelas.
- Versionado del backend actualizado a `0.0.22-SNAPSHOT`.

## [0.0.21-SNAPSHOT] - 2026-06-07

### Changed

- Versionado del backend actualizado a .0.40-SNAPSHOT.

- Añadidos logs `INFO`, `WARN` y `ERROR` al caso de uso de clasificacion para trazar inicio, finalizacion, duplicados y fallos por noticia.
- Añadido log `WARN` con fragmento acotado de respuesta Gemini cuando el proveedor IA no devuelve un objeto JSON clasificable.
- Hecho robusto el filtro de `WF-02-Classify-News` para procesar correctamente respuestas de `GET /api/v1/news` tanto si n8n las entrega como array en un item como si las entrega item a item.
- Versionado del backend actualizado a `0.0.21-SNAPSHOT`.

## [0.0.20-SNAPSHOT] - 2026-06-07

### Changed

- Versionado del backend actualizado a .0.40-SNAPSHOT.

- Ajustado el prompt WF-02 para diferenciar noticias fuera de ambito con `category=OTROS`, `subcategory=FUERA_DE_AMBITO`, `relevance=0`, `impact=LOW` y `urgency=LOW`.
- Diferenciadas las noticias con informacion insuficiente usando `subcategory=INFORMACION_INSUFICIENTE`, evitando mezclarlas con descartes fuera de ambito.
- Versionado del backend actualizado a `0.0.20-SNAPSHOT`.

## [0.0.19-SNAPSHOT] - 2026-06-07

### Changed

- Versionado del backend actualizado a .0.40-SNAPSHOT.

- Reforzado el prompt oficial WF-02 con criterios de relevancia, impacto y urgencia basados en afectacion laboral al profesorado andaluz, manteniendo el contrato JSON de clasificacion del backend.
- Ajustado `WF-02-Classify-News` para procesar como maximo 10 noticias `CAPTURED` por ejecucion y poder ejecutarse cada 5 minutos desde n8n.
- Versionado del backend actualizado a `0.0.19-SNAPSHOT`.

## [0.0.18-SNAPSHOT] - 2026-06-07

### Fixed

- Reforzado el prompt WF-02 de clasificacion para que noticias con informacion insuficiente devuelvan JSON valido con categoria `OTROS`, evitando respuestas libres de Gemini/Gemma que el backend rechaza como no clasificables.
- Versionado del backend actualizado a `0.0.18-SNAPSHOT`.

## [0.0.17-SNAPSHOT] - 2026-06-07

### Added

- Sprint 11 cierre MVP: implementados POST /api/v1/events/merge, editor manual PUT /api/v1/content/{id}, scheduling POST /api/v1/publications/{contentId}/schedule, estado SCHEDULED, scheduler automatico de publicaciones vencidas y auditoria visible ADMIN (/api/v1/audit/users, /api/v1/audit/editorial).
- Frontend Sprint 11: anadidas acciones de fusion de eventos, editor manual de contenido, programacion de publicaciones, visualizacion de publicaciones SCHEDULED/scheduledAt y pantalla ADMIN /audit.
- Testing Sprint 11: anadidos tests backend para merge, editor manual, scheduling y scheduler, y tests Angular para servicios de eventos/contenido/publicaciones/auditoria y pantalla de auditoria.

- Implementado `GeminiAIProvider` como proveedor real externo para `AIProvider`, activable con `app.ai.provider=gemini` y modelo por defecto `models/gemma-4-31b-it`.
- Añadida configuracion tecnica `app.ai` con seleccion de proveedor, API key, modelo, temperatura y limite de tokens mediante `application.yml` o variables de entorno.
- Añadida `AIProviderException` y respuesta HTTP `502 Bad Gateway` para fallos claros del proveedor IA externo sin fallback silencioso.
- Añadidos tests de parseo de respuesta Gemini, errores de configuracion/respuesta y seleccion de proveedor IA por propiedades.

### Changed

- Versionado del backend actualizado a .0.40-SNAPSHOT.

- `DeterministicAIProvider` queda activo por defecto solo cuando `app.ai.provider=deterministic` o no se configura proveedor.
- Versionado del backend actualizado a `0.0.17-SNAPSHOT`.

## [0.0.16-SNAPSHOT] - 2026-06-07

### Added

- Sprint 11 cierre MVP: implementados POST /api/v1/events/merge, editor manual PUT /api/v1/content/{id}, scheduling POST /api/v1/publications/{contentId}/schedule, estado SCHEDULED, scheduler automatico de publicaciones vencidas y auditoria visible ADMIN (/api/v1/audit/users, /api/v1/audit/editorial).
- Frontend Sprint 11: anadidas acciones de fusion de eventos, editor manual de contenido, programacion de publicaciones, visualizacion de publicaciones SCHEDULED/scheduledAt y pantalla ADMIN /audit.
- Testing Sprint 11: anadidos tests backend para merge, editor manual, scheduling y scheduler, y tests Angular para servicios de eventos/contenido/publicaciones/auditoria y pantalla de auditoria.

- Añadida la migracion Flyway `V3__seed_rss_sources.sql` con las 54 fuentes RSS iniciales revisadas para pruebas de `WF-01-Capture-News`.

### Changed

- Versionado del backend actualizado a .0.40-SNAPSHOT.

- Consolidadas las migraciones Flyway iniciales en `V1__create_mvp_schema.sql`, `V2__seed_admin_user.sql` y `V3__seed_rss_sources.sql` para reconstruir el esquema MVP desde cero en desarrollo.
- Añadida la constraint `uk_sources_url` en la creacion inicial de `sources` para impedir URLs de fuentes duplicadas.
- Integrado `event_news.confidence_score` y su check `0..100` en la creacion inicial de `event_news`, eliminando la necesidad de una migracion correctiva posterior.
- Eliminada la tabla tecnica `system_info` de la migracion inicial al no formar parte del modelo MVP ni estar usada por el codigo.
- Versionado del backend actualizado a `0.0.16-SNAPSHOT`.

## [0.0.15-SNAPSHOT] - 2026-06-06

### Added

- Sprint 11 cierre MVP: implementados POST /api/v1/events/merge, editor manual PUT /api/v1/content/{id}, scheduling POST /api/v1/publications/{contentId}/schedule, estado SCHEDULED, scheduler automatico de publicaciones vencidas y auditoria visible ADMIN (/api/v1/audit/users, /api/v1/audit/editorial).
- Frontend Sprint 11: anadidas acciones de fusion de eventos, editor manual de contenido, programacion de publicaciones, visualizacion de publicaciones SCHEDULED/scheduledAt y pantalla ADMIN /audit.
- Testing Sprint 11: anadidos tests backend para merge, editor manual, scheduling y scheduler, y tests Angular para servicios de eventos/contenido/publicaciones/auditoria y pantalla de auditoria.

- T6.1: creada la estructura inicial del modulo `event` con capas `domain`, `application`, `infrastructure` y `api`.
- T6.2: creada la entidad de dominio `Event` con estados, importancia, categorias oficiales e invariantes basicas del agregado.
- T6.3: creado el puerto de dominio `EventRepository` para persistencia y busqueda de eventos por estado, categoria e importancia.
- T6.4: convertido `Event` en aggregate root operativo con asociacion de noticias, cambios de estado y bloqueo de nuevas noticias en eventos cerrados o archivados.
- T6.5: creado el workflow n8n exportable `WF-03-Detect-Events` para procesar noticias clasificadas y delegar la deteccion de eventos en Spring Boot.
- T6.6: integrada la agrupacion IA de eventos con puerto `EventMatchingAIProvider`, prompt oficial WF-03 y proveedor determinista para el MVP tecnico.
- T6.7: implementada la asociacion noticia-evento con persistencia JPA en `events` y `event_news`, caso de uso `DetectEventUseCase` y endpoint `POST /api/v1/events/detect`.
- Sprint 6: añadida la migracion Flyway `V4__add_event_news_confidence_score.sql` para registrar `confidence_score` en `event_news` y cumplir la trazabilidad de asociaciones IA de WF-03.
- Ajustados los workflows n8n `WF-01`, `WF-02` y `WF-03` para usar `http://host.docker.internal:8080` en desarrollo cuando n8n corre en Docker y el backend en la maquina anfitriona.
- Ajustado `WF-01-Capture-News` para descargar RSS/Atom con `HTTP Request` como texto, parsear XML y normalizar Atom Junta Andalucia y RSS estandar antes de llamar a `POST /api/v1/news/bulk`.
- Sprint 6 completado y versionado del backend a `0.0.15-SNAPSHOT`.

### Fixed

- Corregido `WF-01-Capture-News` para usar `$input.all()` en los Code nodes y evitar el error de n8n `Cannot find name 'items'`.
- Corregido `Normalize RSS Items` en `WF-01-Capture-News` para detectar estructuras XML parseadas por n8n con envoltorios `data`, `root`, `body`, `feed`, `rss` o `channel`.
- Corregidos `WF-02-Classify-News` y `WF-03-Detect-Events` para usar `$input.all()` en los Code nodes de filtrado y evitar el error de n8n `Cannot find name 'items'`.

## [0.0.14-SNAPSHOT] - 2026-06-06

### Added

- Sprint 11 cierre MVP: implementados POST /api/v1/events/merge, editor manual PUT /api/v1/content/{id}, scheduling POST /api/v1/publications/{contentId}/schedule, estado SCHEDULED, scheduler automatico de publicaciones vencidas y auditoria visible ADMIN (/api/v1/audit/users, /api/v1/audit/editorial).
- Frontend Sprint 11: anadidas acciones de fusion de eventos, editor manual de contenido, programacion de publicaciones, visualizacion de publicaciones SCHEDULED/scheduledAt y pantalla ADMIN /audit.
- Testing Sprint 11: anadidos tests backend para merge, editor manual, scheduling y scheduler, y tests Angular para servicios de eventos/contenido/publicaciones/auditoria y pantalla de auditoria.

- T5.1: creada la estructura inicial del modulo `classification` con capas `domain`, `application`, `infrastructure` y `api`.
- T5.2: creada la entidad de dominio `NewsClassification` con taxonomia oficial, niveles de impacto/urgencia y pruebas unitarias.
- T5.3: creado el puerto de dominio `NewsClassificationRepository` para persistencia y consultas por `newsId`.
- T5.4: integrada la interfaz `AIProvider` con un adaptador determinista para clasificacion IA en MVP tecnico.
- T5.5: implementado `ClassifyNewsPromptBuilder` con el prompt oficial WF-02 del Documento 23 y pruebas unitarias.
- T5.6: creado el workflow n8n exportable `WF-02-Classify-News` para leer noticias capturadas y llamar a la API de clasificacion.
- T5.7: implementada persistencia de clasificaciones con JPA, `ClassifyNewsUseCase` y endpoint `POST /api/v1/classifications/classify`.
- T5.8: actualizado `ClassifyNewsUseCase` para marcar la noticia como `CLASSIFIED` tras guardar la clasificacion.
- Sprint 5 completado y versionado del backend a `0.0.14-SNAPSHOT`.

## [0.0.13-SNAPSHOT] - 2026-06-06

### Added

- Sprint 11 cierre MVP: implementados POST /api/v1/events/merge, editor manual PUT /api/v1/content/{id}, scheduling POST /api/v1/publications/{contentId}/schedule, estado SCHEDULED, scheduler automatico de publicaciones vencidas y auditoria visible ADMIN (/api/v1/audit/users, /api/v1/audit/editorial).
- Frontend Sprint 11: anadidas acciones de fusion de eventos, editor manual de contenido, programacion de publicaciones, visualizacion de publicaciones SCHEDULED/scheduledAt y pantalla ADMIN /audit.
- Testing Sprint 11: anadidos tests backend para merge, editor manual, scheduling y scheduler, y tests Angular para servicios de eventos/contenido/publicaciones/auditoria y pantalla de auditoria.

- T4.1: creado `IngestNewsBatchUseCase` con procesamiento parcial por item para alinear WF-01 con el flujo `n8n -> API -> Spring Boot -> PostgreSQL`.
- T4.2: creado el endpoint de ingestión masiva `POST /api/v1/news/bulk` con respuesta de resumen por lote.
- T4.3: implementada la normalizacion RSS en backend mediante `NewsCaptureNormalizer` antes de persistir cada item del lote.
- T4.4: implementada deteccion de duplicados por `url` y `hash` dentro del mismo lote, ademas de la validacion existente contra base de datos.
- T4.5: añadidas pruebas de integracion para `POST /api/v1/news/bulk` cubriendo lote parcial, duplicados y request vacio.
- Ajuste Sprint 4: creado el workflow n8n exportable `WF-01-Capture-News` para leer fuentes RSS activas y enviar lotes de noticias a `POST /api/v1/news/bulk`.
- Sprint 4 completado y versionado del backend a `0.0.13-SNAPSHOT`.

## [0.0.12-SNAPSHOT] - 2026-06-06

### Added

- Sprint 11 cierre MVP: implementados POST /api/v1/events/merge, editor manual PUT /api/v1/content/{id}, scheduling POST /api/v1/publications/{contentId}/schedule, estado SCHEDULED, scheduler automatico de publicaciones vencidas y auditoria visible ADMIN (/api/v1/audit/users, /api/v1/audit/editorial).
- Frontend Sprint 11: anadidas acciones de fusion de eventos, editor manual de contenido, programacion de publicaciones, visualizacion de publicaciones SCHEDULED/scheduledAt y pantalla ADMIN /audit.
- Testing Sprint 11: anadidos tests backend para merge, editor manual, scheduling y scheduler, y tests Angular para servicios de eventos/contenido/publicaciones/auditoria y pantalla de auditoria.

- T3.1: creada la estructura inicial del modulo `news` con capas `domain`, `application`, `infrastructure` y `api`.
- T3.2: creada la entidad de dominio `NewsArticle` con `NewsStatus` y pruebas unitarias.
- T3.3: creado el puerto de dominio `NewsRepository` para persistencia y consultas de noticias.
- T3.4: creada la entidad JPA `NewsArticleEntity` mapeada a `news_articles` con pruebas de mapeo.
- T3.5: implementado `JpaNewsRepository` con persistencia, consultas por `id`, `url`, `hash` y listado de noticias.
- T3.6: creados los DTOs `CreateNewsRequest` y `NewsResponse` con validaciones de entrada y pruebas.
- T3.7: creado `CreateNewsUseCase` con validacion de fuente, deteccion de duplicados por URL/hash y calculo interno SHA-256.
- T3.8: creados `GetNewsUseCase`, `ListNewsUseCase` y `NewsNotFoundException` con pruebas unitarias.
- T3.9: creada la API REST del modulo `news` con endpoints de creacion, listado y detalle.
- T3.10: verificado el Sprint 3 completo con 52 tests de backend sin fallos y versionado a `0.0.12-SNAPSHOT`.

## [0.0.11-SNAPSHOT] - 2026-06-06

### Added

- Sprint 11 cierre MVP: implementados POST /api/v1/events/merge, editor manual PUT /api/v1/content/{id}, scheduling POST /api/v1/publications/{contentId}/schedule, estado SCHEDULED, scheduler automatico de publicaciones vencidas y auditoria visible ADMIN (/api/v1/audit/users, /api/v1/audit/editorial).
- Frontend Sprint 11: anadidas acciones de fusion de eventos, editor manual de contenido, programacion de publicaciones, visualizacion de publicaciones SCHEDULED/scheduledAt y pantalla ADMIN /audit.
- Testing Sprint 11: anadidos tests backend para merge, editor manual, scheduling y scheduler, y tests Angular para servicios de eventos/contenido/publicaciones/auditoria y pantalla de auditoria.

- Implementada la API REST del modulo `source` con endpoints `GET /api/v1/sources`, `POST /api/v1/sources` y `PUT /api/v1/sources/{id}`, junto con pruebas de integracion REST.

## [0.0.10-SNAPSHOT] - 2026-06-06

### Added

- Sprint 11 cierre MVP: implementados POST /api/v1/events/merge, editor manual PUT /api/v1/content/{id}, scheduling POST /api/v1/publications/{contentId}/schedule, estado SCHEDULED, scheduler automatico de publicaciones vencidas y auditoria visible ADMIN (/api/v1/audit/users, /api/v1/audit/editorial).
- Frontend Sprint 11: anadidas acciones de fusion de eventos, editor manual de contenido, programacion de publicaciones, visualizacion de publicaciones SCHEDULED/scheduledAt y pantalla ADMIN /audit.
- Testing Sprint 11: anadidos tests backend para merge, editor manual, scheduling y scheduler, y tests Angular para servicios de eventos/contenido/publicaciones/auditoria y pantalla de auditoria.

- Creado `CreateSourceUseCase` con `CreateSourceCommand` para registrar fuentes desde la capa de aplicacion, evitando URLs duplicadas.

## [0.0.9-SNAPSHOT] - 2026-06-06

### Added

- Sprint 11 cierre MVP: implementados POST /api/v1/events/merge, editor manual PUT /api/v1/content/{id}, scheduling POST /api/v1/publications/{contentId}/schedule, estado SCHEDULED, scheduler automatico de publicaciones vencidas y auditoria visible ADMIN (/api/v1/audit/users, /api/v1/audit/editorial).
- Frontend Sprint 11: anadidas acciones de fusion de eventos, editor manual de contenido, programacion de publicaciones, visualizacion de publicaciones SCHEDULED/scheduledAt y pantalla ADMIN /audit.
- Testing Sprint 11: anadidos tests backend para merge, editor manual, scheduling y scheduler, y tests Angular para servicios de eventos/contenido/publicaciones/auditoria y pantalla de auditoria.

- Creados los DTOs `CreateSourceRequest` y `SourceResponse` para la API del modulo `source`, con validaciones de entrada y pruebas unitarias.

## [0.0.8-SNAPSHOT] - 2026-06-06

### Added

- Sprint 11 cierre MVP: implementados POST /api/v1/events/merge, editor manual PUT /api/v1/content/{id}, scheduling POST /api/v1/publications/{contentId}/schedule, estado SCHEDULED, scheduler automatico de publicaciones vencidas y auditoria visible ADMIN (/api/v1/audit/users, /api/v1/audit/editorial).
- Frontend Sprint 11: anadidas acciones de fusion de eventos, editor manual de contenido, programacion de publicaciones, visualizacion de publicaciones SCHEDULED/scheduledAt y pantalla ADMIN /audit.
- Testing Sprint 11: anadidos tests backend para merge, editor manual, scheduling y scheduler, y tests Angular para servicios de eventos/contenido/publicaciones/auditoria y pantalla de auditoria.

- Implementado `JpaSourceRepository` como adaptador JPA del puerto de dominio `SourceRepository`, con pruebas de integracion de persistencia.

## [0.0.7-SNAPSHOT] - 2026-06-06

### Added

- Sprint 11 cierre MVP: implementados POST /api/v1/events/merge, editor manual PUT /api/v1/content/{id}, scheduling POST /api/v1/publications/{contentId}/schedule, estado SCHEDULED, scheduler automatico de publicaciones vencidas y auditoria visible ADMIN (/api/v1/audit/users, /api/v1/audit/editorial).
- Frontend Sprint 11: anadidas acciones de fusion de eventos, editor manual de contenido, programacion de publicaciones, visualizacion de publicaciones SCHEDULED/scheduledAt y pantalla ADMIN /audit.
- Testing Sprint 11: anadidos tests backend para merge, editor manual, scheduling y scheduler, y tests Angular para servicios de eventos/contenido/publicaciones/auditoria y pantalla de auditoria.

- Creada la entidad JPA `SourceEntity` mapeada a la tabla `sources`, con pruebas de mapeo basico.

## [0.0.6-SNAPSHOT] - 2026-06-06

### Added

- Sprint 11 cierre MVP: implementados POST /api/v1/events/merge, editor manual PUT /api/v1/content/{id}, scheduling POST /api/v1/publications/{contentId}/schedule, estado SCHEDULED, scheduler automatico de publicaciones vencidas y auditoria visible ADMIN (/api/v1/audit/users, /api/v1/audit/editorial).
- Frontend Sprint 11: anadidas acciones de fusion de eventos, editor manual de contenido, programacion de publicaciones, visualizacion de publicaciones SCHEDULED/scheduledAt y pantalla ADMIN /audit.
- Testing Sprint 11: anadidos tests backend para merge, editor manual, scheduling y scheduler, y tests Angular para servicios de eventos/contenido/publicaciones/auditoria y pantalla de auditoria.

- Creada la interfaz de dominio `SourceRepository` como puerto de persistencia del modulo `source`.

## [0.0.5-SNAPSHOT] - 2026-06-06

### Added

- Sprint 11 cierre MVP: implementados POST /api/v1/events/merge, editor manual PUT /api/v1/content/{id}, scheduling POST /api/v1/publications/{contentId}/schedule, estado SCHEDULED, scheduler automatico de publicaciones vencidas y auditoria visible ADMIN (/api/v1/audit/users, /api/v1/audit/editorial).
- Frontend Sprint 11: anadidas acciones de fusion de eventos, editor manual de contenido, programacion de publicaciones, visualizacion de publicaciones SCHEDULED/scheduledAt y pantalla ADMIN /audit.
- Testing Sprint 11: anadidos tests backend para merge, editor manual, scheduling y scheduler, y tests Angular para servicios de eventos/contenido/publicaciones/auditoria y pantalla de auditoria.

- Creada la entidad de dominio `Source` con campos de auditoria `createdAt` y `updatedAt`, comportamiento de activacion/desactivacion y pruebas unitarias.

## [0.0.4-SNAPSHOT] - 2026-06-05

### Added

- Sprint 11 cierre MVP: implementados POST /api/v1/events/merge, editor manual PUT /api/v1/content/{id}, scheduling POST /api/v1/publications/{contentId}/schedule, estado SCHEDULED, scheduler automatico de publicaciones vencidas y auditoria visible ADMIN (/api/v1/audit/users, /api/v1/audit/editorial).
- Frontend Sprint 11: anadidas acciones de fusion de eventos, editor manual de contenido, programacion de publicaciones, visualizacion de publicaciones SCHEDULED/scheduledAt y pantalla ADMIN /audit.
- Testing Sprint 11: anadidos tests backend para merge, editor manual, scheduling y scheduler, y tests Angular para servicios de eventos/contenido/publicaciones/auditoria y pantalla de auditoria.

- Creada la estructura inicial del modulo `source` con capas `domain`, `application`, `infrastructure` y `api`.

## [0.0.3-SNAPSHOT] - 2026-06-05

### Added

- Sprint 11 cierre MVP: implementados POST /api/v1/events/merge, editor manual PUT /api/v1/content/{id}, scheduling POST /api/v1/publications/{contentId}/schedule, estado SCHEDULED, scheduler automatico de publicaciones vencidas y auditoria visible ADMIN (/api/v1/audit/users, /api/v1/audit/editorial).
- Frontend Sprint 11: anadidas acciones de fusion de eventos, editor manual de contenido, programacion de publicaciones, visualizacion de publicaciones SCHEDULED/scheduledAt y pantalla ADMIN /audit.
- Testing Sprint 11: anadidos tests backend para merge, editor manual, scheduling y scheduler, y tests Angular para servicios de eventos/contenido/publicaciones/auditoria y pantalla de auditoria.

- Creada la migracion Flyway `V3__seed_data.sql` con el usuario inicial `ADMIN` en la tabla `users`.

## [0.0.2-SNAPSHOT] - 2026-06-05

### Added

- Sprint 11 cierre MVP: implementados POST /api/v1/events/merge, editor manual PUT /api/v1/content/{id}, scheduling POST /api/v1/publications/{contentId}/schedule, estado SCHEDULED, scheduler automatico de publicaciones vencidas y auditoria visible ADMIN (/api/v1/audit/users, /api/v1/audit/editorial).
- Frontend Sprint 11: anadidas acciones de fusion de eventos, editor manual de contenido, programacion de publicaciones, visualizacion de publicaciones SCHEDULED/scheduledAt y pantalla ADMIN /audit.
- Testing Sprint 11: anadidos tests backend para merge, editor manual, scheduling y scheduler, y tests Angular para servicios de eventos/contenido/publicaciones/auditoria y pantalla de auditoria.

- Creada la migracion Flyway `V2__create_mvp_schema.sql` con el esquema completo MVP: fuentes, noticias, clasificaciones, eventos, analisis IA, contenido generado, publicaciones y usuarios.
