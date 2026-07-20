## Plataforma de Inteligencia Informativa para Sindicato de Docentes de Andalucía

Versión 2.0

Estado: Arquitectura Oficial MVP

---

# 1. Objetivo

Definir:

- Arquitectura de automatización.
    
- Workflows oficiales.
    
- Responsabilidades.
    
- Entradas y salidas.
    
- Integración IA.
    
- Integración PostgreSQL.
    
- Integración Spring Boot.
    
- Gestión de errores.
    
- Escalabilidad futura.
    

---

# 2. Principios Arquitectónicos

## PA-001

n8n es un orquestador.

No contiene lógica de negocio compleja.

---

## PA-002

La lógica de negocio pertenece al Backend.

```text
n8n
 ↓
Spring Boot
 ↓
Reglas de negocio
```

---

## PA-003

El evento es la entidad principal del sistema.

```text
Noticias
 ↓
Eventos
 ↓
Análisis
 ↓
Contenido
 ↓
Publicación
```

---

## PA-004

Los workflows deben ser independientes.

Cada workflow debe poder ejecutarse de forma aislada.

---

## PA-005

Todo workflow debe ser reiniciable.

No debe perder datos ante errores.

---

# 3. Arquitectura General

```text
RSS / APIs / Web Sources
            │
            ▼

      WF-01 Captura
            │
            ▼

     news_articles
            │
            ▼

   WF-02 Clasificación
            │
            ▼

 news_classifications
            │
            ▼

   WF-03 Eventos
            │
            ▼

        events
            │
            ▼

 WF-04 Análisis IA
            │
            ▼

 event_ai_analysis
            │
            ▼

 WF-05 Contenido
            │
            ▼

 generated_content
            │
            ▼

 Revisión Humana
            │
            ▼

 WF-06 Publicación
            │
            ▼

      Telegram
```

---

# 4. Workflows MVP

## WF-01 Captura Noticias

Responsable de capturar noticias.

---

## WF-02 Clasificación IA

Responsable de clasificar noticias.

---

## WF-03 Detección de Eventos

Responsable de agrupar noticias.

---

## WF-04 Generación de Análisis

Responsable de generar conocimiento.

---

## WF-05 Generación de Contenido

Responsable de generar contenido editorial.

---

## WF-06 Publicación Telegram

Responsable de publicar contenido aprobado.

---

# 5. Workflows Post-MVP

No se implementan inicialmente.

```text
WF-07 Publicación Facebook

WF-08 Publicación X

WF-09 Monitorización avanzada

WF-10 Limpieza avanzada

WF-11 Tendencias

WF-12 Alertas inteligentes
```

---

# 6. Workflow WF-01

# Captura de Noticias

---

## Objetivo

Capturar noticias relevantes del ámbito educativo andaluz.

---

## Frecuencia

```text
Cada 30 minutos
```

---

## Entrada

```text
sources
```

---

## Fuentes Iniciales

### BOJA

### BOE

### Consejería de Desarrollo Educativo

### Consejería de Universidad

### Google News

### Sindicatos educativos

### Medios especializados

---

## Flujo

```text
Cron
 ↓
Leer Fuentes
 ↓
HTTP Request
 ↓
RSS/XML Parser
 ↓
Normalización
 ↓
Hash Noticias
 ↓
Detección Duplicados
 ↓
Guardar
```

---

## Tabla Destino

```text
news_articles
```

---

## Estado

```text
CAPTURED
```

---

# 7. Workflow WF-02

# Clasificación IA

---

## Objetivo

Clasificar noticias automáticamente.

---

## Entrada

```text
news_articles
```

---

## Filtro

```sql
processing_status='CAPTURED'
```

---

## Flujo

```text
Leer Noticias
 ↓
Agrupar por lotes
 ↓
Enviar a IA
 ↓
Clasificar
 ↓
Guardar clasificación
 ↓
Actualizar estado
```

---

## Resultado

```text
CLASSIFIED
```

---

## Tabla Destino

```text
news_classifications
```

---

## Datos Generados

```text
Categoría

Subcategoría

Relevancia

Impacto

Urgencia

Keywords

Entidades
```

---

# 8. Workflow WF-03

# Detección de Eventos

---

## Objetivo

Agrupar noticias sobre un mismo hecho.

---

## Entrada

```text
Noticias clasificadas
```

---

## Flujo

```text
Leer noticia
 ↓
Buscar eventos similares
 ↓
Comparación IA
 ↓
¿Existe evento?
```

---

### Sí

```text
Asociar noticia al evento
```

---

### No

```text
Crear nuevo evento
```

---

## Tablas Afectadas

```text
events

event_news
```

---

## Resultado

```text
EVENT_MATCHED
```

---

## Ejemplo

```text
5 noticias sobre:

Oposiciones Andalucía 2027

↓

1 único evento
```

---

# 9. Workflow WF-04

# Generación de Análisis IA

---

## Objetivo

Generar conocimiento consolidado sobre un evento.

---

## Trigger

Nuevo evento.

o

Evento actualizado.

---

## Entrada

```text
Todas las noticias asociadas
```

---

## Flujo

```text
Leer evento
 ↓
Leer noticias
 ↓
Generar contexto
 ↓
Enviar IA
 ↓
Guardar análisis
```

---

## Tabla Destino

```text
event_ai_analysis
```

---

## Información Generada

### Resumen Ejecutivo

### Resumen Sindical

### Puntos Clave

### Riesgos

### Oportunidades

---

# 10. Workflow WF-05

# Generación de Contenido

---

## Objetivo

Generar contenido listo para revisión.

---

## Trigger

Solicitud del editor.

---

## Entrada

```text
Event

EventAnalysis
```

---

## Flujo

```text
Leer Evento
 ↓
Leer Análisis
 ↓
Seleccionar Canal
 ↓
Seleccionar Tono
 ↓
Generar IA
 ↓
Guardar
```

---

## Tabla Destino

```text
generated_content
```

---

## Tonos MVP

### Informativo (por defecto)

### Institucional

### Divulgativo

---

## Canal MVP

```text
Telegram
```

---

# 11. Workflow WF-06

# Publicación Telegram

---

## Objetivo

Publicar contenido aprobado.

---

## Trigger

Contenido aprobado.

---

## Flujo

```text
Leer contenido
 ↓
Validar estado
 ↓
Enviar Telegram
 ↓
Guardar respuesta
 ↓
Actualizar publicación
```

---

## Tabla Destino

```text
publications
```

---

## Estados

```text
PENDING

PUBLISHED

FAILED
```

---

# 12. Gestión de Errores

Todos los workflows deben implementar:

---

## Captura

```text
3 reintentos
```

---

## IA

```text
2 reintentos
```

---

## Telegram

```text
3 reintentos
```

---

## Registro

Todos los errores deben registrarse.

---

# 13. Alertas Operativas

Canal:

```text
Telegram Administrador
```

---

Alertas:

### Error IA

### Error Captura

### Error Publicación

### Workflow detenido

---

# 14. Variables Globales

## IA

```text
AI_PROVIDER

AI_MODEL

OPENAI_API_KEY

OLLAMA_URL
```

---

## PostgreSQL

```text
DB_HOST

DB_PORT

DB_NAME

DB_USER

DB_PASSWORD
```

---

## Telegram

```text
TELEGRAM_BOT_TOKEN

TELEGRAM_CHANNEL_ID

ADMIN_CHAT_ID
```

---

# 15. Estrategia de Evolución

## MVP

```text
Captura

Clasificación

Eventos

Análisis

Contenido

Telegram
```

---

## v1.1

```text
Logs avanzados

Monitorización

Versionado contenido
```

---

## v1.2

```text
Facebook

X

Programación publicaciones
```

---

## v2.0

```text
Tendencias

Métricas

Alertas inteligentes

Analítica avanzada
```

---

# 16. Arquitectura Final MVP

```text
WF-01 Captura
          ↓

WF-02 Clasificación
          ↓

WF-03 Eventos
          ↓

WF-04 Análisis
          ↓

WF-05 Contenido
          ↓

Revisión Humana
          ↓

WF-06 Publicación Telegram
```

---

# Decisiones Arquitectónicas

### N8N-001

n8n actúa como orquestador.

### N8N-002

Event es la entidad principal.

### N8N-003

Todo contenido se genera desde eventos.

### N8N-004

Telegram es el único canal MVP.

### N8N-005

Los workflows deben ser independientes.

### N8N-006

La lógica de negocio residirá progresivamente en Spring Boot.

### N8N-007

La arquitectura está preparada para escalar a múltiples canales sin rediseño.
---

# 17. Actualizacion Operativa 2026-06-15

El MVP operativo mantiene n8n solo para `WF-01-Capture-News`.

Los flujos `WF-02` a `WF-06` quedan migrados a Spring Boot:

```text
WF-02 Clasificacion        -> ProcessPendingClassificationsUseCase
WF-03 Eventos              -> ProcessPendingEventDetectionUseCase
WF-04 Analisis             -> ProcessPendingEventAnalysisUseCase
WF-05 Contenido            -> GenerateContentUseCase
WF-06 Publicacion Telegram -> PublishContentUseCase / PublishScheduledPublicationsUseCase
```

La aplicacion Angular no llama a n8n. Angular consume `/api/v1` y Spring Boot ejecuta reglas de negocio, persistencia, IA, auditoria y publicacion.

Endpoints operativos para ejecucion manual:

```http
POST /api/v1/automation/classifications/run
POST /api/v1/automation/events/run
POST /api/v1/automation/analysis/run
POST /api/v1/content/generate
POST /api/v1/publications/{id}/publish
POST /api/v1/publications/{id}/schedule
```

### N8N-008

Desde 2026-06-15, n8n conserva solo la captura RSS/XML. Las automatizaciones internas de clasificacion, eventos, analisis, contenido y publicacion residen en Spring Boot.

---

# 18. Actualizacion Operativa 2026-06-16

`WF-02`, `WF-03` y `WF-04` se ejecutan mediante un scheduler interno de Spring Boot dirigido por la tabla `automation_workflow_settings`.

Configuracion persistida por workflow:

```text
workflowCode
enabled
intervalSeconds
batchSize
running
lastRunAt
lastSuccessAt
lastFailureAt
nextRunAt
lastProcessedCount
lastSuccessCount
lastFailedCount
lastSkippedCount
lastError
```

Valores iniciales conservadores desde la configuracion inicial:

```text
WF02_CLASSIFICATION    enabled=false  intervalSeconds=600   batchSize=1
WF03_EVENT_DETECTION   enabled=false  intervalSeconds=600   batchSize=3
WF04_ANALYSIS          enabled=false  intervalSeconds=900   batchSize=1
```

La configuracion se administra desde Angular en `/settings`, visible solo para `ADMIN`, que debe activar manualmente los workflows cuando proceda.

Endpoints de configuracion:

```http
GET  /api/v1/automation/settings
GET  /api/v1/automation/settings/{workflowCode}
PUT  /api/v1/automation/settings/{workflowCode}
POST /api/v1/automation/settings/{workflowCode}/run
```

`WF-05` no tiene scheduler automatico: se ejecuta bajo demanda desde el detalle de evento mediante `POST /api/v1/content/generate`.

`WF-06` se ejecuta bajo demanda desde contenido aprobado mediante `POST /api/v1/publications/{contentId}/publish` o por programacion existente mediante `POST /api/v1/publications/{contentId}/schedule`.

### N8N-009

La programacion operativa de `WF-02` a `WF-04` pertenece a Spring Boot y PostgreSQL. n8n no conserva responsabilidad sobre frecuencia, lotes ni ejecucion de estos workflows migrados.

---

# 19. Actualizacion Operativa 2026-06-16 - Configuracion Telegram

La publicacion Telegram deja de depender de configuracion estatica exclusiva de `application.yml` o variables de entorno.

Spring Boot persiste la configuracion operativa en `telegram_publication_settings`:

```text
enabled
baseUrl
botToken
chatId
disableWebPagePreview
updatedAt
```

El administrador configura estos parametros desde la pantalla Angular `/settings`, que queda como pagina central de configuracion operativa.

Endpoints ADMIN:

```http
GET /api/v1/settings/telegram
PUT /api/v1/settings/telegram
```

La API no devuelve el token completo. Solo informa si existe token configurado y una vista enmascarada.

Condicion para publicar en Telegram:

```text
enabled=true
botToken configurado
chatId configurado
baseUrl configurado
```

`PublishContentUseCase` mantiene la regla de dominio: solo se publica contenido `APPROVED`. `TelegramPublisher` valida la configuracion en tiempo de publicacion.

### N8N-010

`WF-06` sigue residiendo en Spring Boot. La capacidad real de publicar en Telegram queda controlada por configuracion ADMIN persistida y no por n8n.

---

# 20. Actualizacion Operativa 2026-06-18 - Observabilidad IA y Settings

El cierre extendido del Sprint 12 consolida la observabilidad de IA y la configuracion ADMIN.

n8n mantiene solo `WF-01-Capture-News`. Los procesos `WF-02` a `WF-06` no vuelven a n8n:

```text
WF-01 Captura RSS/XML      -> n8n
WF-02 Clasificacion        -> Spring Boot
WF-03 Eventos              -> Spring Boot
WF-04 Analisis             -> Spring Boot
WF-05 Contenido            -> Spring Boot bajo demanda
WF-06 Publicacion Telegram -> Spring Boot bajo demanda o scheduler
```

Spring Boot registra metricas de operaciones IA en `ai_operation_metrics`:

```text
operationType
promptKey
provider
model
status
relatedEntityType
relatedEntityId
latencyMs
errorMessage
createdAt
```

El versionado tecnico de prompts se consulta desde `ai_prompt_versions`. No se habilita edicion de prompts desde UI en este cierre.

Endpoints ADMIN:

```http
GET /api/v1/ai/prompts
GET /api/v1/ai/metrics
GET /api/v1/automation/overview
```

La pantalla Angular `/settings` es el centro unico de configuracion para `ADMIN` e incluye:

```text
Telegram
Automatizaciones backend
WF-01 externo en n8n
Prompts IA versionados
Metricas IA recientes
```

### N8N-011

La monitorizacion de `T12.3` queda reinterpretada: `WF-01` se valida como workflow externo n8n y `WF-02` a `WF-06` se observan mediante Spring Boot, PostgreSQL y la pantalla ADMIN `/settings`.

---

# 21. Actualizacion Operativa 2026-07-11 - Coordinacion IA por Modelo

Las acciones IA manuales y programadas de `WF-02` a `WF-05` se coordinan en Spring Boot por `modelName` efectivo configurado en `ai_workflow_settings`.

Regla operativa:

```text
Si dos acciones IA usan el mismo modelo, la segunda espera a que termine la primera y respeta el cooldown configurado.
Si usan modelos distintos, pueden ejecutarse sin bloqueo por modelo.
```

El cooldown por defecto es de 60 segundos y se configura por workflow desde `/settings` mediante el campo `cooldownSeconds`.

Esta regla aplica a:

```text
WF-02 Clasificacion automatica o manual
WF-03 Matching de eventos automatico o manual
WF-04 Analisis automatico o manual
WF-05 Generacion de contenido bajo demanda
```

### N8N-012

n8n no participa en esta coordinacion. El bloqueo por modelo y el cooldown se resuelven exclusivamente en Spring Boot para las automatizaciones internas migradas y acciones IA bajo demanda.

---

# 22. Actualizacion Operativa 2026-07-11 - Disparo rapido de WF-03 tras WF-02

`WF-03_EVENT_DETECTION` sigue residiendo en Spring Boot y conserva su scheduler configurable como respaldo operativo.

Desde esta actualizacion, cuando `WF-02_CLASSIFICATION` clasifica correctamente una noticia y la noticia no queda `DISCARDED`, Spring Boot solicita que `WF-03_EVENT_DETECTION` quede vencido inmediatamente (`nextRunAt = now`). La ejecucion real continua haciendose por el scheduler backend y respeta:

```text
enabled
running
batchSize
cooldownSeconds por modelo IA
trazabilidad en automation_workflow_settings
```

Si `WF-03` consume un lote completo, se reprograma de nuevo de forma inmediata para drenar backlog de noticias `CLASSIFIED` sin esperar el intervalo ordinario. Si no quedan pendientes, la siguiente ejecucion registra lote vacio y vuelve al intervalo configurado.

Las noticias descartadas por `WF-02` no solicitan `WF-03`.

### N8N-013

El disparo rapido de eventos no devuelve responsabilidad a n8n ni acopla la clasificacion al matching de eventos. La clasificacion persiste primero su resultado y solo solicita adelantar la automatizacion backend de eventos.

---

# 23. Actualizacion Operativa 2026-07-12 - Recuperacion de automatizaciones bloqueadas

Las automatizaciones backend pueden quedar persistidas con `running=true` si el proceso Spring Boot se detiene o reinicia mientras un workflow esta en ejecucion.

Desde esta actualizacion, antes de procesar workflows vencidos, Spring Boot revisa `automation_workflow_settings` y recupera automaticamente workflows habilitados que sigan `running=true` mas alla del timeout operativo:

```text
app.automation.stale-running-timeout-minutes=30
```

Comportamiento de recuperacion:

```text
running=false
last_failure_at=now
last_error="Workflow recuperado tras quedar bloqueado en running=true"
next_run_at=now
```

Esto permite que el scheduler los reintente sin intervencion manual. La recuperacion queda registrada en logs `WARN` y no cambia la responsabilidad de n8n.

### N8N-014

La recuperacion de candados obsoletos pertenece a Spring Boot y PostgreSQL. n8n sigue limitado a `WF-01-Capture-News`.
