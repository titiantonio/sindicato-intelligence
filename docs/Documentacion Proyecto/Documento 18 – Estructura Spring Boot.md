# Clean Architecture + DDD + Modular Monolith

Versión 1.0

Estado: Arquitectura de Implementación

---

# 1. Objetivo

Definir la estructura oficial del proyecto Spring Boot.

Este documento establece:

- Organización del código.
    
- Dependencias permitidas.
    
- Módulos de negocio.
    
- Capas de arquitectura.
    
- Convenciones.
    
- Separación de responsabilidades.
    

---

# 2. Principios Arquitectónicos

## SA-001

Monolito Modular.

---

## SA-002

DDD (Domain Driven Design).

---

## SA-003

Clean Architecture.

---

## SA-004

El dominio nunca depende de infraestructura.

---

## SA-005

Event es el Aggregate Root principal.

---

## SA-006

Toda lógica de negocio reside en Domain o Application.

---

## SA-007

Los Controllers nunca contienen lógica de negocio.

---

# 3. Arquitectura General

```text
Presentation
      │
      ▼
Application
      │
      ▼
Domain
      │
      ▼
Infrastructure
```

---

# 4. Estructura Raíz

```text
es.sindicato.intelligence
```

```text
es.sindicato.intelligence

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
└── user
```

---

# 5. Módulos de Negocio

## source

Gestión de fuentes.

---

## news

Gestión de noticias.

---

## classification

Clasificación IA.

---

## event

Gestión de eventos.

Aggregate Root principal.

---

## analysis

Análisis IA.

---

## content

Contenido editorial.

---

## publication

Publicación Telegram.

---

## user

Usuarios.

---

# 6. Estructura Interna de un Módulo

Ejemplo:

```text
event
```

```text
event

├── domain
├── application
├── infrastructure
└── api
```

---

# 7. Domain Layer

Contiene únicamente negocio.

---

Ejemplo:

```text
event/domain

Event

EventStatus

Importance

EventRepository

EventMatchingService
```

---

## Permitido

```text
Entidades

Value Objects

Enums

Servicios Dominio

Interfaces
```

---

## Prohibido

```text
JPA

Spring

Controllers

DTOs

HTTP
```

---

# 8. Application Layer

Orquesta casos de uso.

---

Ejemplo:

```text
event/application

CreateEventUseCase

CloseEventUseCase

GetEventUseCase

MergeEventUseCase
```

---

Responsabilidades:

```text
Transacciones

Orquestación

Invocación Dominio

Invocación Repositorios
```

---

# 9. Infrastructure Layer

Implementaciones técnicas.

---

Ejemplo:

```text
event/infrastructure

JpaEventRepository

EventEntity

EventMapper
```

---

Responsabilidades:

```text
JPA

PostgreSQL

OpenAI

Telegram

Clientes REST
```

---

# 10. API Layer

Responsable de exponer REST.

---

Ejemplo:

```text
event/api

EventController

EventRequest

EventResponse
```

---

Responsabilidades:

```text
Validaciones

DTOs

Transformaciones

Respuestas HTTP
```

---

# 11. Módulo Shared

Código reutilizable.

---

Estructura:

```text
shared

exception

validation

pagination

response

utils
```

---

# 12. Módulo Security

Responsable de:

```text
JWT

Spring Security

Roles

Permisos
```

---

Estructura:

```text
security

jwt

config

filters

handlers
```

---

# 13. Módulo Configuration

Configuraciones globales.

---

```text
configuration

database

openapi

jackson

cache
```

---

# 14. Modelo Event

Aggregate Root principal.

---

Entidad:

```java
Event
```

---

Value Objects:

```java
EventStatus

Importance
```

---

Servicios:

```java
EventMatchingService
```

---

Repositorio:

```java
EventRepository
```

---

# 15. Modelo News

Entidad:

```java
News
```

---

Repositorio:

```java
NewsRepository
```

---

Casos de uso:

```java
GetNewsByIdUseCase

SearchNewsUseCase
```

---

# 16. Modelo Analysis

Entidad:

```java
EventAnalysis
```

---

Casos de uso:

```java
GenerateAnalysisUseCase

GetAnalysisUseCase
```

---

# 17. Modelo Content

Entidad:

```java
GeneratedContent
```

---

Casos de uso:

```java
GenerateContentUseCase

ApproveContentUseCase

RejectContentUseCase
```

---

# 18. Modelo Publication

Entidad:

```java
Publication
```

---

Casos de uso:

```java
PublishContentUseCase

GetPublicationUseCase
```

---

# 19. DTOs

Regla obligatoria.

Nunca exponer entidades.

---

Incorrecto:

```java
return Event;
```

---

Correcto:

```java
return EventResponse;
```

---

# 20. Mappers

Responsables de:

```text
Entity → Domain

Domain → Entity

Domain → DTO
```

---

Herramienta recomendada:

```text
MapStruct
```

---

# 21. Persistencia

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

```text
No modificar tablas manualmente.
```

---

# 22. Eventos de Dominio

Eventos recomendados:

```java
NewsCapturedEvent

NewsClassifiedEvent

EventCreatedEvent

EventUpdatedEvent

AnalysisGeneratedEvent

ContentGeneratedEvent

PublicationSentEvent
```

---

# 23. Integraciones IA

Interfaz:

```java
AIProvider
```

---

Implementaciones:

```java
OpenAIProvider

ClaudeProvider

OllamaProvider
```

---

Beneficio:

Cambio de proveedor sin modificar negocio.

---

# 24. Integraciones Publicación

Interfaz:

```java
PublishingProvider
```

---

Implementación MVP:

```java
TelegramPublisher
```

---

Futuro:

```java
FacebookPublisher

XPublisher

LinkedInPublisher
```

---

# 25. Convenciones de Nombres

## Entidades

```java
Event

News

Publication
```

---

## Casos de Uso

```java
CreateEventUseCase

GenerateContentUseCase
```

---

## Repositorios

```java
EventRepository

NewsRepository
```

---

## Controladores

```java
EventController

NewsController
```

---

# 26. Dependencias Permitidas

```text
API
 ↓
Application
 ↓
Domain

Infrastructure
 ↓
Domain
```

---

Prohibido:

```text
Domain
 ↓
Infrastructure
```

---

# 27. Testing

Framework:

```text
JUnit 5
```

---

Mocking:

```text
Mockito
```

---

Objetivo MVP:

```text
Cobertura > 70%
```

---

# 28. Estructura de Proyecto Final

```text
es.sindicato.intelligence

├── shared
├── security
├── configuration

├── source
│
├── news
│
├── classification
│
├── event
│
├── analysis
│
├── content
│
├── publication
│
└── user
```

---

# Decisiones Arquitectónicas

### SB-001

Monolito Modular.

### SB-002

DDD.

### SB-003

Clean Architecture.

### SB-004

Event como Aggregate Root principal.

### SB-005

DTO obligatorio en API.

### SB-006

MapStruct para mapeos.

### SB-007

Flyway para migraciones.

### SB-008

IA desacoplada mediante AIProvider.

### SB-009

Publicación desacoplada mediante PublishingProvider.

### SB-010

El dominio nunca dependerá de infraestructura.