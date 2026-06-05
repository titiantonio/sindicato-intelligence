Versión 2.0

Estado: Arquitectura Backend MVP Oficial

---

# 1. Objetivos

El backend será el núcleo de negocio de la plataforma.

Responsabilidades principales:

### Gestión de fuentes

### Gestión de noticias

### Gestión de clasificación IA

### Gestión de eventos

### Gestión de análisis IA

### Gestión editorial

### Gestión de publicaciones

### Gestión de usuarios

### Seguridad

### API para Angular

### API para futura App móvil

---

# 2. Arquitectura General

```text
┌─────────────────────────────┐
│        Angular Web          │
└──────────────┬──────────────┘
               │ REST API
               ▼

┌─────────────────────────────┐
│      Spring Boot API        │
└───────┬─────────┬───────────┘
        │         │
        ▼         ▼

 PostgreSQL      IA

        ▲
        │
        ▼

       n8n
```

---

# 3. Principios Arquitectónicos

### A-001

Monolito Modular.

---

### A-002

Clean Architecture.

---

### A-003

DDD (Domain Driven Design).

---

### A-004

Event es la entidad principal del sistema.

---

### A-005

Todo acceso a datos pasa por Spring Boot.

---

### A-006

n8n nunca accede directamente a Angular.

---

# 4. Arquitectura Interna

```text
Controller
    │
    ▼
Use Case
    │
    ▼
Domain
    │
    ▼
Repository Port
    │
    ▼
Infrastructure Adapter
```

---

# 5. Capas

## Presentation Layer

Responsable de:

- REST API
    
- DTOs
    
- Validaciones
    
- Seguridad
    
- Serialización JSON
    

---

## Application Layer

Responsable de:

- Casos de uso
    
- Orquestación
    
- Transacciones
    
- Coordinación de servicios
    

---

## Domain Layer

Responsable de:

- Entidades
    
- Value Objects
    
- Servicios de dominio
    
- Eventos de dominio
    
- Reglas de negocio
    

---

## Infrastructure Layer

Responsable de:

- PostgreSQL
    
- JPA
    
- IA
    
- Telegram
    
- Integraciones externas
    

---

# 6. Estructura Base de Paquetes

```text
com.sindicato.intelligence
```

```text
com.sindicato.intelligence

├── shared
├── security
├── configuration

├── source
├── news
├── classification
├── event
├── analysis
├── content
├── publication
├── user
```

---

# 7. Módulo Source

Responsable de:

- Fuentes RSS
    
- Fuentes oficiales
    
- Activación/desactivación
    
- Priorización
    

Entidad principal:

```text
Source
```

---

# 8. Módulo News

Responsable de:

- Consulta de noticias
    
- Detalle de noticias
    
- Estado de procesamiento
    

Entidades:

```text
News
Classification
```

---

# 9. Módulo Event

Módulo principal del sistema.

Responsable de:

- Creación de eventos
    
- Asociación de noticias
    
- Gestión de estados
    
- Cierre de eventos
    

Aggregate Root:

```text
Event
```

Estados:

```text
OPEN
MONITORING
CLOSED
ARCHIVED
```

---

# 10. Módulo Analysis

Responsable de:

- Resúmenes IA
    
- Resumen ejecutivo
    
- Resumen sindical
    
- Riesgos
    
- Oportunidades
    

Entidad principal:

```text
EventAnalysis
```

---

# 11. Módulo Content

Responsable de:

- Generación editorial
    
- Gestión de borradores
    
- Aprobación editorial
    

Entidad principal:

```text
GeneratedContent
```

Estados:

```text
GENERATED
PENDING_REVIEW
APPROVED
REJECTED
PUBLISHED
```

---

# 12. Módulo Publication

Responsable de:

- Publicación Telegram
    
- Gestión de estado de envío
    
- Trazabilidad
    

Entidad principal:

```text
Publication
```

Estados:

```text
PENDING
SCHEDULED
PUBLISHED
FAILED
```

---

# 13. Módulo User

Responsable de:

- Login
    
- Roles
    
- Permisos
    

Roles MVP:

```text
ADMIN
EDITOR
```

---

# 14. CQRS Ligero

No se implementará CQRS completo.

Se utilizará separación conceptual:

## Queries

Lectura.

Ejemplos:

```text
GetNewsById

GetEvents

GetEventDetail
```

---

## Commands

Escritura.

Ejemplos:

```text
CreateEvent

CloseEvent

GenerateContent

PublishContent
```

---

# 15. Seguridad

## Autenticación

JWT

---

## Autorización

Spring Security

---

Duración Access Token:

```text
15 minutos
```

---

Refresh Token:

```text
7 días
```

---

# 16. API REST MVP

## Noticias

```http
GET /api/v1/news

GET /api/v1/news/{id}
```

---

## Eventos

```http
GET /api/v1/events

GET /api/v1/events/{id}

POST /api/v1/events/{id}/close
```

---

## IA

```http
POST /api/v1/events/{id}/summarize

POST /api/v1/events/{id}/generate-content
```

---

## Contenido

```http
POST /api/v1/content/{id}/approve

POST /api/v1/content/{id}/reject
```

---

## Publicación

```http
POST /api/v1/publications/{id}/publish
```

---

## Usuarios

```http
POST /api/v1/auth/login

GET /api/v1/users
```

---

# 17. Persistencia

ORM:

```text
Spring Data JPA
```

---

Migraciones:

```text
Flyway
```

---

Regla:

Nunca modificar tablas manualmente en producción.

Toda modificación deberá realizarse mediante migraciones.

---

# 18. Integración IA

Se utilizará una capa de abstracción.

Interfaz:

```java
AIProvider
```

Implementaciones posibles:

```text
OpenAIProvider

ClaudeProvider

OllamaProvider
```

Beneficio:

Cambio de proveedor sin afectar al dominio.

---

# 19. Integración con n8n

n8n será responsable de:

- Captura de noticias
    
- Clasificación IA
    
- Agrupación de eventos
    
- Generación de resúmenes
    
- Automatizaciones
    

---

Regla principal:

```text
n8n
 ↓
REST API
 ↓
Spring Boot
 ↓
PostgreSQL
```

El acceso directo a PostgreSQL por parte de n8n deberá minimizarse progresivamente.

El objetivo final es que Spring Boot sea el único punto de acceso a datos.

---

# 20. Integración de Canales

Interfaz:

```java
PublishingProvider
```

Implementación MVP:

```text
TelegramPublisher
```

Implementaciones futuras:

```text
FacebookPublisher

XPublisher

LinkedInPublisher

InstagramPublisher
```

---

# 21. Caché

MVP:

```text
Sin Redis
```

Versión futura:

```text
Redis
```

Casos de uso:

- Dashboard
    
- Eventos activos
    
- Noticias recientes
    

---

# 22. Logging

Framework:

```text
Logback
```

Niveles:

```text
INFO
WARN
ERROR
```

Registrar especialmente:

- Generación IA
    
- Creación eventos
    
- Publicaciones
    
- Errores de integración
    

---

# 23. Infraestructura

```text
Proxmox
└── LXC Docker
      ├── Spring Boot
      ├── PostgreSQL
      ├── n8n
      └── Nginx
```

---

# 24. Roadmap Técnico MVP

Fase 1

```text
sources
news_articles
news_classifications
```

---

Fase 2

```text
events
event_news
```

---

Fase 3

```text
event_ai_analysis
```

---

Fase 4

```text
generated_content
```

---

Fase 5

```text
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

IA desacoplada mediante AIProvider.

### A-009

Canales desacoplados mediante PublishingProvider.

### A-010

Event como Aggregate Root principal del sistema.