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
V2__create_mvp_schema.sql
```

---

Rol:

```text
postgres-data-architect
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

---

# 5. Sprint 2

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

## T2.8

Crear API REST.

---

Endpoints:

```http
GET /api/v1/sources

POST /api/v1/sources

PUT /api/v1/sources/{id}
```

---

## T2.9

Crear tests.

---

Estado:

```text
Pendiente
```

---

# 6. Sprint 3

# Módulo News

---

Objetivo:

Gestionar noticias.

---

## T3.1

Crear módulo news.

---

## T3.2

Entidad dominio.

```java
NewsArticle
```

---

## T3.3

Repositorio dominio.

---

## T3.4

Entidad JPA.

---

## T3.5

Implementación repositorio.

---

## T3.6

DTOs.

---

## T3.7

CreateNewsUseCase.

---

## T3.8

GetNewsUseCase.

---

## T3.9

API REST.

---

Endpoints:

```http
POST /api/v1/news

GET /api/v1/news

GET /api/v1/news/{id}
```

---

## T3.10

Tests.

---

# 7. Sprint 4

# WF-01 Captura Noticias

---

Objetivo:

Conectar n8n con Spring Boot.

---

## T4.1

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

## T4.2

Crear endpoint ingestión.

---

## T4.3

Normalización RSS.

---

## T4.4

Detección duplicados.

---

## T4.5

Pruebas integración.

---

Resultado esperado:

```text
Noticias entrando por API
```

---

# 8. Sprint 5

# Clasificación IA

---

Objetivo:

Clasificar noticias.

---

## T5.1

Crear módulo classification.

---

## T5.2

Crear entidad dominio.

---

## T5.3

Repositorio.

---

## T5.4

Integración AIProvider.

---

## T5.5

Implementar prompt WF-02.

---

## T5.6

Crear workflow n8n.

---

## T5.7

Persistir clasificación.

---

## T5.8

Actualizar estado noticia.

---

# 9. Sprint 6

# Eventos

---

Objetivo:

Agrupar noticias.

---

## T6.1

Crear módulo event.

---

## T6.2

Crear entidad Event.

---

## T6.3

Crear EventRepository.

---

## T6.4

Crear Aggregate Root.

---

## T6.5

Crear workflow detección eventos.

---

## T6.6

Integrar IA agrupación.

---

## T6.7

Crear asociación noticia-evento.

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

T1.1

```text
Crear V2__create_mvp_schema.sql
```

Rol:

```text
postgres-data-architect
```
