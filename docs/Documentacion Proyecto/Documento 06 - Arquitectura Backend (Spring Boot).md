Versión 1.0

Estado: Arquitectura Base

---

# 1. Objetivos

El backend será responsable de:

### Gestión de noticias

### Gestión de eventos

### Gestión IA

### Gestión editorial

### Publicaciones

### Usuarios

### Seguridad

### API para Angular

### API para App móvil futura

---

# 2. Arquitectura General

```
┌─────────────────────────────┐
│         Angular Web         │
└──────────────┬──────────────┘
               │ REST API
               ▼

┌─────────────────────────────┐
│       Spring Boot API       │
└──────────────┬──────────────┘
               │
 ┌─────────────┼─────────────┐
 │             │             │
 ▼             ▼             ▼

PostgreSQL     IA           n8n
```

---

# 3. Arquitectura Interna

## Clean Architecture

```
Controller
     │
     ▼
Use Case
     │
     ▼
Domain
     │
     ▼
Repository
```

---

# Capas

---

## Presentation Layer

Responsable de:

```
REST API
Validaciones
DTOs
eguridad
```

---

## Application Layer

Responsable de:

```
Casos de uso
Orquestación
Reglas negocio
```

---

## Domain Layer

Responsable de:

```
Entidades
Value Objects
Interfaces
Reglas de dominio
```

---

## Infrastructure Layer

Responsable de:

```
Postgre
SQL
OpenAI
Telegram
FacebookXn8n
```

---

# 4. Estructura de Paquetes

```
com.sindicato.intelligence
```

---

# Core

```
core
```

Contendrá:

```
exceptions
security
shared
config
```

---

# Módulo Noticias

```
news
```

---

Estructura

```
news

├─ domain
├─ application
├─ infrastructure
└─ api
```

---

## Responsabilidades

```
Consultar noticias
Detalle noticia
Clasificaciones
```

---

# Módulo Eventos

```
events
```

---

Responsabilidades

```
Crear eventos
Consultar eventos
Fusionar eventos
Separar noticias
Cerrar eventos
```

---

# Módulo IA

```
ai
```

---

Responsabilidades

```
Resúmenes
Clasificación
Generación contenido
```

---

# Módulo Editorial

```
editorial
```

---

Responsabilidades

```
Perfiles editoriales
Versiones contenido
Aprobaciones
```

---

# Módulo Publicaciones

```
publishing
```

---

Responsabilidades

```
Telegram
FacebookX
```

---

# Módulo Usuarios

```
users
```

---

Responsabilidades

```
Login
Roles
Permisos
```

---

# Módulo Configuración

```
configuration
```

---

Responsabilidades

```
Fuentes
Categorías
Canales
```

---

# 5. Patrón CQRS Ligero

No implementaremos CQRS completo.

---

Pero sí separación:

## Queries

```
Sólo lectura
```

---

## Commands

```
Cambios estado
```

---

Ejemplo:

```
GetEventByIdQuery

CreateEventCommand

MergeEventsCommand
```

---

# 6. Seguridad

## Autenticación

```
JWT
```

---

## Autorización

```
Spring Security
```

---

Roles MVP

```
ADMIN
EDITOR
```

---

# 7. API REST

---

# Noticias

```
GET /api/news
```

Lista noticias.

---

```
GET /api/news/{id}
```

Detalle.

---

# Eventos

```
GET /api/events
```

---

```
GET /api/events/{id}
```

---

```
POST /api/events/{id}/merge
```

---

```
POST /api/events/{id}/close
```

---

# IA

```
POST /api/ai/event/{id}/summarize
```

---

```
POST /api/ai/event/{id}/generate-content
```

---

# Editorial

```
POST /api/content/{id}/approve
```

---

```
POST /api/content/{id}/reject
```

---

# Publicación

```
POST /api/publications/{id}/publish
```

---

```
POST /api/publications/{id}/schedule
```

---

# Usuarios

```
POST /api/auth/login
```

---

```
GET /api/users
```

---

# 8. Integración con PostgreSQL

## ORM

```
Spring Data JPA
```

---

## Migraciones

```
Flyway
```

---

Muy importante.

Nunca crear tablas manualmente en producción.

---

# 9. Integración con IA

## Capa de abstracción

Crear interfaz:

```
AIProvider
```

---

Implementaciones:

```
OpenAIProvider

OllamaProvider

ClaudeProvider
```

---

Ventaja:

Cambiar modelo sin tocar negocio.

---

# 10. Integración con n8n

## Regla Arquitectónica

n8n NO accede directamente a Angular.

---

n8n interactúa con:

```
Spring Boot API
```

---

Flujo correcto:

```
n8n
 ↓
REST API
 ↓
Spring Boot
 ↓
PostgreSQL
```

---

# ¿Por qué?

Porque así:

- Centralizamos reglas.
- Centralizamos seguridad.
- Centralizamos auditoría.

---

# 11. Integración Canales

Crear interfaz:

```
PublishingProvider
```

---

Implementaciones:

```
TelegramPublisher

FacebookPublisher

XPublisher
```

---

En el futuro:

```
LinkedinPublisher

InstagramPublisher

WebPublisher
```

---

# 12. Cache

MVP:

```
Sin Redis
```

---

Fase 2:

```
Redis
```

para:

- Dashboard
- Estadísticas
- Noticias recientes

---

# 13. Logs

Framework:

```
Logback
```

---

Niveles:

```
INFO
WARN
ERROR
```

---

Auditoría crítica:

```
Aprobaciones
Publicaciones
Fusiones eventos
```

---

# 14. Despliegue

Infraestructura actual:

```
Proxmox
└── LXC Docker
      ├── Spring Boot
      ├── PostgreSQL
      ├── n8n
      └── Nginx
```

---

# 15. Roadmap Técnico

## Fase 1

Noticias

```
sources
news_articles
news_classifications
```

---

## Fase 2

Eventos

```
events
event_news
```

---

## Fase 3

IA

```
event_ai_analysis
```

---

## Fase 4

Editorial

```
generated_content
```

---

## Fase 5

Publicaciones

```
publications
```

---

# Decisiones Arquitectónicas Aprobadas

### A-001

Monolito Modular + Clean Architecture.

### A-002

Spring Boot 3.x.

### A-003

PostgreSQL como única base de datos.

### A-004

JWT para autenticación.

### A-005

Flyway para migraciones.

### A-006

n8n como motor de automatización.

### A-007

Backend como punto único de acceso a datos.

### A-008

IA desacoplada mediante interfaz `AIProvider`.

### A-009

Canales desacoplados mediante interfaz `PublishingProvider`.