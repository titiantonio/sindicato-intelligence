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

# 10. Sprint 7

# Análisis IA

---

Objetivo:

Generar conocimiento consolidado.

---

## T7.1

Crear módulo analysis.

---

## T7.2

Implementar GenerateAnalysisUseCase.

---

## T7.3

Implementar Prompt WF-04.

---

## T7.4

Persistir análisis.

---

# 11. Sprint 8

# Contenido

---

Objetivo:

Generar contenido.

---

## T8.1

Crear módulo content.

---

## T8.2

Implementar GenerateContentUseCase.

---

## T8.3

Implementar Prompt WF-05.

---

## T8.4

Persistir contenido.

---

# 12. Sprint 9

# Publicación Telegram

---

Objetivo:

Publicar contenido.

---

## T9.1

Crear módulo publication.

---

## T9.2

Crear interfaz:

```java
PublishingProvider
```

---

## T9.3

Implementar:

```java
TelegramPublisher
```

---

## T9.4

Crear workflow publicación.

---

## T9.5

Registrar publicación.

---

# 13. Sprint 10

# Seguridad

---

Objetivo:

Autenticación y autorización.

---

## T10.1

JWT.

---

## T10.2

Roles.

```text
ADMIN

EDITOR
```

---

## T10.3

Protección endpoints.

---

## T10.4

Login.

---

# 14. Sprint 11

# Frontend Angular

---

Objetivo:

Backoffice MVP.

---

## T11.1

Crear proyecto Angular.

---

## T11.2

Layout principal.

---

## T11.3

Login.

---

## T11.4

Dashboard.

---

## T11.5

Eventos.

---

## T11.6

Detalle Evento.

---

## T11.7

Contenido.

---

## T11.8

Publicaciones.

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

# 16. Regla Operativa

Nunca avanzar al siguiente Sprint sin:

```text
Build OK

Tests OK

Flyway OK

Commit Git realizado
```

---

# 17. Orden Obligatorio

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

# 18. Próxima Tarea

Sprint 7

```text
Continuar con Analisis IA
```

Rol:

```text
postgres-data-architect
```
