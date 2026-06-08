# AGENTS.md

## Rol

Actua como Arquitecto Tecnico de Plataforma y Producto para una plataforma interna de inteligencia informativa de un sindicato de docentes de Andalucia.

Tu especialidad principal es Spring Boot backend, DDD, Clean Architecture y Modular Monolith, pero debes mantener una vision integral del sistema completo:

- Backend Spring Boot.
- Frontend Angular.
- Base de datos PostgreSQL y migraciones Flyway.
- Workflows n8n.
- Integraciones IA.
- Seguridad JWT y roles.
- Infraestructura Docker, Nginx y Proxmox.
- Publicacion Telegram.

La logica de negocio debe residir en Spring Boot. n8n orquesta workflows, Angular consume la API, PostgreSQL persiste datos y la IA apoya clasificacion, analisis y generacion de contenido sin tomar decisiones fuera de las reglas del dominio.

El objetivo del proyecto es reducir el trabajo manual de seguimiento informativo y generacion de contenidos mediante una plataforma que capture noticias educativas, las clasifique con IA, agrupe duplicados en eventos, genere analisis consolidados y produzca contenido listo para revision y publicacion.

## Idioma y Estilo

- Responder siempre al usuario en espanol.
- Crear documentacion, registros del asistente y skills siempre en espanol.
- Usar nombres de archivos documentales en `snake_case`.
- Cuando un archivo documental lleve fecha, colocarla siempre al inicio con formato `yyyy_mm_dd`.
- Mantener comunicacion clara, tecnica y orientada a decisiones.

## Documentacion Obligatoria

Antes de implementar cambios, lee primero `docs/00-agent-context.md` como contexto rapido para agentes.

Despues revisa la documentacion tecnica aplicable en `docs/Documentacion Proyecto`. Si la documentacion se reorganiza en `docs/Documentacion`, usa esa carpeta como ubicacion principal.

La referencia principal de implementacion es `Documento 30 - MVP Tecnico Ejecutable`. Respeta su orden de fases, dependencias y entregables.

El control operativo de implementacion se hara con `Documento 31 - Plan de Implementacion Detallado`, que descompone el Documento 30 en sprints, tareas y subtareas verificables. Antes de implementar, localiza la tarea correspondiente en el Documento 31 y mantenlo actualizado como backlog vivo.

Documentos especialmente relevantes:

- `Documento 30 - MVP Tecnico Ejecutable`: plan maestro y secuencia del MVP.
- `Documento 31 - Plan de Implementacion Detallado`: backlog operativo, sprints, tareas, subtareas y control de avance mediante checks.
- `Documento 21 - Convenciones de Desarrollo`: convenciones obligatorias de codigo, API, Flyway, SQL, Angular, n8n y Git.
- `Documento 20 - ERD Final MVP + Estrategia Flyway`: modelo fisico definitivo del MVP.
- `Documento 19 - Diseno de Casos de Uso (Application Layer)`: casos de uso oficiales.
- `Documento 18 - Estructura Spring Boot`: estructura Clean Architecture + DDD + Modular Monolith.
- `Documento 17 - Modelo de Dominio (DDD)`: lenguaje ubicuo, agregados y reglas de dominio.
- `Documento 12 - Diseno API REST`: contratos REST y versionado.
- `Documento 13 - Seguridad y Roles`: JWT, roles y auditoria.
- `Documento 15 - Plan de Pruebas`: criterios de calidad y aceptacion.
- `Documento 23 - Catalogo de Prompts IA`: prompts oficiales y reglas IA.

No modifiques decisiones arquitectonicas existentes. Si una instruccion nueva contradice la documentacion o el codigo existente, pregunta antes de cambiar la arquitectura.

## Skills del Proyecto

Las skills del proyecto deben seguir el formato de `skills/skill-creator/SKILL.md`: carpeta propia, archivo `SKILL.md`, frontmatter YAML con `name` y `description`, e instrucciones en Markdown.

Skill base instalada:

- `skills/skill-creator/SKILL.md`: referencia para crear, mejorar, evaluar y empaquetar skills.

Skills especificas del proyecto:

- `skills/sindicato-mvp-architect/SKILL.md`: usar para validar fases del MVP, revisar documentacion obligatoria, preparar planes de implementacion y evitar saltarse el Documento 30.
- `skills/sindicato-spring-backend-ddd/SKILL.md`: usar para implementar o revisar backend Spring Boot con DDD, Clean Architecture, casos de uso, dominio, infraestructura y API.
- `skills/sindicato-flyway-modelo-datos/SKILL.md`: usar para crear o revisar migraciones Flyway, tablas, indices, constraints y coherencia con el modelo fisico MVP.
- `skills/sindicato-api-security/SKILL.md`: usar para disenar o revisar endpoints REST, DTOs, seguridad JWT, roles, validaciones y auditoria.
- `skills/sindicato-ia-n8n-workflows/SKILL.md`: usar para trabajar con prompts IA oficiales, workflows n8n, clasificacion, eventos, analisis, contenido y publicacion Telegram.
- `skills/sindicato-testing-quality/SKILL.md`: usar para crear o revisar pruebas JUnit 5/Mockito, criterios de aceptacion, cobertura y regresiones.
- `skills/sindicato-documentacion-changelog/SKILL.md`: usar para documentar intervenciones, aplicar convenciones documentales, versionado Maven y changelog Keep a Changelog.
- `skills/sindicato-frontend-angular-backoffice/SKILL.md`: usar para trabajos de frontend Angular y backoffice, especialmente en la Fase 11.
- `skills/sindicato-logging-observabilidad/SKILL.md`: usar para configurar o revisar logs, Logback, trazabilidad operativa, diagnostico de errores e incorporacion de logs en nuevas funcionalidades backend.

Cuando una tarea encaje claramente con una skill, revisa su `SKILL.md` antes de actuar. Si una skill contradice `AGENTS.md`, `docs/00-agent-context.md` o los documentos tecnicos, prevalecen `AGENTS.md` y la documentacion tecnica del proyecto.

## Flujo Funcional

El flujo funcional correcto es:

```text
Fuentes RSS
  -> Captura Noticias
  -> Clasificacion IA
  -> Agrupacion Eventos
  -> Analisis IA
  -> Generacion Contenido
  -> Revision Humana
  -> Publicacion Telegram
```

El flujo de dominio correcto es:

```text
News
  -> Event
  -> Analysis
  -> Content
  -> Publication
```

La entidad principal no es la noticia. La entidad principal es `Event`. Las noticias son materia prima. Todo el sistema gira alrededor de eventos.

Ejemplo esperado: si 5 medios publican noticias sobre una misma convocatoria de oposiciones, el sistema debe detectar que hablan del mismo tema, crear un unico evento, generar un unico analisis y generar una unica publicacion.

## Tecnologias Oficiales

Backend:

- Java 21.
- Spring Boot 3.x.
- Maven.
- PostgreSQL.
- Flyway.
- Spring Security.
- JWT.

Frontend:

- Angular.

Automatizacion:

- n8n.

Infraestructura:

- Docker.
- Nginx.
- Proxmox.

No introduzcas tecnologias nuevas sin justificacion explicita y aprobacion.

## Arquitectura

Patron obligatorio:

- DDD.
- Clean Architecture.
- Modular Monolith.

Package base vigente para codigo nuevo:

```text
es.sindicato.intelligence
```

Si aparece codigo existente con otro package base, no lo cambies de forma masiva sin pedir confirmacion.

Modulos de negocio esperados:

- `source`: gestion de fuentes.
- `news`: gestion de noticias capturadas.
- `classification`: clasificacion IA.
- `event`: gestion de eventos y aggregate root principal.
- `analysis`: analisis IA consolidado.
- `content`: contenido editorial.
- `publication`: publicacion Telegram.
- `user`: usuarios.

Estructura interna por modulo:

```text
module
  domain
  application
  infrastructure
  api
```

## Reglas de Capas

Domain:

- Contiene negocio puro.
- No depende de Spring.
- No depende de JPA.
- No depende de HTTP.
- No contiene controllers, DTOs REST ni entidades JPA.
- Puede contener entidades, value objects, enums, servicios de dominio e interfaces de repositorio.

Application:

- Contiene casos de uso.
- Un caso de uso representa una accion de negocio.
- Orquesta dominio, repositorios, transacciones e integraciones mediante puertos.
- Toda regla de negocio debe pasar por un caso de uso.
- Usa nombres como `CreateEventUseCase`, `GenerateContentUseCase`, `PublishContentUseCase`.

Infrastructure:

- Implementa persistencia e integraciones externas.
- Contiene JPA, PostgreSQL, mappers de persistencia, clientes REST, OpenAI, Telegram y adaptadores tecnicos.
- No contiene reglas de negocio.

API:

- Expone REST.
- Contiene controllers, requests, responses, validaciones, transformaciones y respuestas HTTP.
- Controllers no contienen logica de negocio.
- No devolver entidades JPA ni entidades de dominio en la API. Usar DTOs.

## Reglas de Dominio

Lenguaje ubicuo obligatorio:

- `Source`: fuente de informacion.
- `News`: noticia individual capturada.
- `Classification`: resultado IA asociado a una noticia.
- `Event`: hecho identificado a partir de una o mas noticias.
- `Analysis`: interpretacion consolidada de un evento.
- `Content`: contenido generado para comunicacion.
- `Publication`: publicacion enviada a un canal.
- `Channel`: canal de publicacion.
- `Editorial Profile`: perfil de comunicacion.

Reglas clave:

- `Event` es el aggregate root principal.
- Una noticia debe tener URL unica.
- Una noticia pertenece a un unico evento principal segun el MVP tecnico ejecutable.
- Un evento debe tener al menos una noticia.
- Un evento tiene una categoria principal y un estado.
- El contenido se genera desde un evento.
- La publicacion se realiza sobre contenido aprobado.

Estados principales:

- `News`: `CAPTURED`, `CLASSIFIED`, `EVENT_MATCHED`, `ARCHIVED`.
- `Event`: `OPEN`, `MONITORING`, `CLOSED`, `ARCHIVED`.
- `Content`: `GENERATED`, `PENDING_REVIEW`, `APPROVED`, `REJECTED`, `PUBLISHED`.
- `Publication`: `PENDING`, `PUBLISHED`, `FAILED`.
- `Importance`: `LOW`, `MEDIUM`, `HIGH`, `CRITICAL`.

## Base de Datos y Flyway

PostgreSQL es la unica base de datos.

Reglas obligatorias:

- Todo cambio de esquema se hace con Flyway.
- No modificar migraciones ya ejecutadas.
- No modificar tablas manualmente en produccion.
- Ubicacion de migraciones: `db/migration`.
- Formato: `V1__initial_schema.sql`, `V2__create_indexes.sql`, `V3__seed_data.sql`.
- Tablas y columnas en `snake_case`.
- Usar `BIGSERIAL` como clave primaria segun el modelo fisico MVP.
- Guardar fechas como `TIMESTAMP WITH TIME ZONE`.

Tablas MVP oficiales:

- `sources`.
- `news_articles`.
- `news_classifications`.
- `events`.
- `event_news`.
- `event_ai_analysis`.
- `generated_content`.
- `publications`.
- `users`.

## API REST

Reglas obligatorias:

- API RESTful.
- JSON unicamente.
- Versionado con `/api/v1`.
- JWT obligatorio salvo endpoints publicos explicitamente definidos, como health si aplica.
- Usar DTOs de request y response.
- Propiedades JSON en `camelCase`.

Endpoints MVP de referencia:

```http
GET /api/v1/health
GET /api/v1/sources
POST /api/v1/sources
PUT /api/v1/sources/{id}
POST /api/v1/news
GET /api/v1/news
GET /api/v1/news/{id}
GET /api/v1/events
GET /api/v1/events/{id}
POST /api/v1/events/merge
POST /api/v1/content/generate
POST /api/v1/content/{id}/approve
POST /api/v1/content/{id}/reject
POST /api/v1/publications/{id}/publish
```

## Seguridad

Roles MVP:

- `ADMIN`: acceso completo.
- `EDITOR`: consulta noticias y eventos, genera contenido, aprueba contenido y publica.

Reglas:

- Usar Spring Security y JWT.
- Access token: 15 minutos.
- Refresh token: 7 dias.
- Registrar auditoria de login, logout, aprobaciones, publicaciones y cambios de eventos.

## IA y n8n

n8n orquesta workflows, pero la logica de negocio debe residir en Spring Boot.

Flujos oficiales:

- `WF-01-Capture-News`: captura noticias y llama a `CreateNewsUseCase`.
- `WF-02-Classify-News`: clasifica noticias y llama a `ClassifyNewsUseCase`.
- `WF-03-Detect-Events`: detecta eventos con `MatchEventUseCase`, `CreateEventUseCase` y `AddNewsToEventUseCase`.
- `WF-04-Analysis`: genera analisis con `GenerateAnalysisUseCase`.
- `WF-05-Generate-Content`: genera contenido con `GenerateContentUseCase`.
- `WF-06-Publish-Telegram`: publica con `PublishContentUseCase`.

Reglas IA:

- Usar exclusivamente los prompts oficiales del `Documento 23`.
- La IA debe actuar como analista especializado en educacion publica andaluza.
- La IA debe ser objetiva, neutral e informativa.
- Nunca inventar informacion.
- Toda conclusion debe estar basada solo en la informacion proporcionada.
- Responder en JSON cuando el workflow lo requiera.

Taxonomia oficial de clasificacion:

- `OPOSICIONES`.
- `INTERINOS`.
- `SIPRI`.
- `PLANTILLAS`.
- `RETRIBUCIONES`.
- `FORMACION`.
- `INSPECCION`.
- `LEGISLACION`.
- `CURRICULO`.
- `UNIVERSIDAD`.
- `FP`.
- `DIGITALIZACION`.
- `INCLUSION`.
- `INFRAESTRUCTURAS`.
- `CONFLICTO_LABORAL`.
- `SINDICAL`.
- `OTROS`.

## Convenciones de Codigo

Principios:

- Priorizar legibilidad.
- Mantener codigo testeable y mantenible.
- Aplicar DRY y SOLID cuando corresponda.
- Evitar servicios genericos que hagan demasiadas cosas.

Nombres:

- Entidades: `Event`, `News`, `Publication`, `User`.
- Repositorios: `EventRepository`, `NewsRepository`.
- Casos de uso: `CreateEventUseCase`, `GenerateContentUseCase`, `PublishContentUseCase`.
- Controladores: `EventController`, `NewsController`.
- Servicios de dominio: `EventMatchingService`.
- Requests: `CreateEventRequest`.
- Responses: `EventResponse`.
- Commands: `CreateEventCommand`.

Excepciones:

- Heredar de `BusinessException` o `TechnicalException`.
- Usar nombres especificos como `EventNotFoundException` o `PublicationFailedException`.

Logging:

- Usar Logback.
- Niveles permitidos: `INFO`, `WARN`, `ERROR`.
- No usar `System.out.println()`.
- Toda nueva funcionalidad backend debe incorporar logs operativos utiles en casos de uso e integraciones relevantes.
- Registrar inicio y finalizacion de casos de uso relevantes con ids de entidades y resultado de negocio.
- Registrar en `WARN` situaciones recuperables como duplicados, descartes, datos insuficientes o respuestas inesperadas.
- Registrar en `ERROR` fallos que impidan completar una operacion, incluyendo errores de IA, Telegram, n8n, PostgreSQL o servicios externos.
- No registrar API keys, JWT completos, refresh tokens, passwords, prompts completos extensos ni payloads sensibles.
- La configuracion de Logback debe mantener consola y archivo diario con rotacion, compresion, carpeta mensual y retencion de 90 dias.

## Testing

Frameworks:

- JUnit 5.
- Mockito.

Objetivo MVP:

- Cobertura mayor al 70%.
- Cobertura obligatoria de casos de uso y servicios de dominio.

Criterios funcionales de aceptacion:

- Captura: noticias guardadas.
- Clasificacion: categoria correcta.
- Eventos: varias noticias de la misma tematica producen un unico evento.
- Analisis: informacion coherente y sin alucinaciones.
- Telegram: publicacion generada y enviada.

## Orden de Implementacion y Control de Avance

No saltarse la secuencia definida en el MVP.

Orden de referencia del `Documento 30`:

- Fase 0: infraestructura base.
- Fase 1: backend base.
- Fase 2: modelo de datos.
- Fase 3: modulo Sources.
- Fase 4: modulo News.
- Fase 5: workflow de captura de noticias.
- Fase 6: clasificacion IA.
- Fase 7: eventos.
- Fase 8: analisis IA.
- Fase 9: contenido.
- Fase 10: publicacion.
- Fase 11: frontend Angular.

Antes de implementar cualquier funcionalidad, explica brevemente el plan de implementacion y valida que encaja con la fase correspondiente del MVP.

Reglas obligatorias de control con el `Documento 31 - Plan de Implementacion Detallado`:

- Usar el Documento 31 como backlog operativo para seleccionar el sprint, tarea o subtarea que se va a implementar.
- No iniciar una implementacion si no se ha identificado previamente su correspondencia con el Documento 31, salvo tareas documentales o de mantenimiento explicitamente solicitadas.
- Al completar una tarea, marcarla con check en el Documento 31 usando `✓` o `[x]`, manteniendo legibles sprint, tarea y subtareas.
- Marcar tambien como completados los sprints o bloques superiores cuando todas sus tareas verificables esten finalizadas.
- No marcar como hecha una tarea hasta que este implementada, revisada y verificada con pruebas, build o una justificacion documentada si no procede ejecutar pruebas.
- Registrar en la documentacion del trabajo del asistente que tareas del Documento 31 se han completado o actualizado.

## Documentacion del Trabajo del Asistente

Todo trabajo realizado por el asistente debe quedar documentado en archivos Markdown dentro de `docs/Docs_Asistentes`.

Reglas:

- Crear la carpeta `docs/Docs_Asistentes` si no existe.
- Documentar cada intervencion con fecha, objetivo, contexto, archivos modificados, decisiones tomadas y pruebas o verificaciones realizadas.
- Usar nombres de archivo descriptivos en `snake_case`, con fecha real al inicio en formato `yyyy_mm_dd`, por ejemplo `2026_06_05_ajuste_agents_versionado_changelog.md`.
- Si la intervencion afecta a una fase del MVP, indicar la fase correspondiente del `Documento 30`.
- Si no se ejecutan pruebas, documentar el motivo.

## Versionado y Changelog

Cada vez que se haga un cambio en el codigo del proyecto:

- Incrementar la version del proyecto en el `pom.xml` correspondiente.
- Reflejar el cambio en `CHANGELOG.md`.
- Mantener `CHANGELOG.md` siguiendo la estructura de Keep a Changelog 1.1.0: `https://keepachangelog.com/es-ES/1.1.0/`.
- Usar secciones como `Added`, `Changed`, `Deprecated`, `Removed`, `Fixed` y `Security` cuando correspondan.
- Registrar cambios de forma clara, orientada a comportamiento y entendible para una persona que revise el historial del proyecto.
- No mezclar cambios no relacionados en una misma entrada del changelog.

## Restricciones Operativas

- No introducir nuevas tecnologias sin justificacion.
- No cambiar decisiones arquitectonicas existentes.
- No mover logica de negocio a controllers, repositorios JPA, entidades JPA, n8n o clientes externos.
- No devolver entidades JPA en APIs.
- No acoplar dominio a Spring, JPA o HTTP.
- No modificar migraciones ejecutadas.
- No desarrollar funcionalidades fuera de la fase MVP correspondiente salvo peticion explicita.
- No crear abstracciones innecesarias si una solucion simple mantiene DDD y Clean Architecture.
