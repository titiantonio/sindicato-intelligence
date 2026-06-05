Versión: 1.0

Estado: Obligatorio

---

# 1. Objetivo

Definir las normas de desarrollo oficiales del proyecto.

Estas convenciones aplican a:

- Spring Boot
- Angular
- PostgreSQL
- n8n
- IA
- Git
- Docker

---

# 2. Principios Generales

## DEV-001

La legibilidad es prioritaria frente a la complejidad.

---

## DEV-002

Todo código debe ser mantenible.

---

## DEV-003

Todo código debe ser testeable.

---

## DEV-004

No duplicar lógica.

Aplicar DRY.

---

## DEV-005

Aplicar SOLID cuando corresponda.

---

# 3. Convenciones Java

Versión oficial:

```text
Java 21 LTS
```

---

Framework:

```text
Spring Boot 3.x
```

---

# 4. Nombres de Clases

## Entidades

```java
Event
News
Publication
User
```

---

## Repositorios

```java
EventRepository
NewsRepository
```

---

## Casos de Uso

```java
CreateEventUseCase
GenerateContentUseCase
PublishContentUseCase
```

---

## Controladores

```java
EventController
NewsController
```

---

## Servicios de Dominio

```java
EventMatchingService
```

---

# 5. DTOs

Sufijos obligatorios.

---

Request:

```java
CreateEventRequest
```

---

Response:

```java
EventResponse
```

---

Command:

```java
CreateEventCommand
```

---

# 6. Controllers

Regla:

```text
NO lógica de negocio
```

---

Correcto:

```java
@PostMapping
public EventResponse create(...) {
   return createEventUseCase.execute(...);
}
```

---

Incorrecto:

```java
@PostMapping
public EventResponse create(...) {

   if(...) {
      ...
   }

   repository.save(...);

}
```

---

# 7. Casos de Uso

Un caso de uso:

```text
Una responsabilidad
```

---

Correcto:

```java
CreateEventUseCase
```

---

Incorrecto:

```java
EventService
```

haciendo 20 cosas.

---

# 8. Repositorios

Los repositorios:

```text
Persisten
```

No contienen negocio.

---

Correcto:

```java
save()

findById()

findAll()
```

---

Incorrecto:

```java
calculateImpact()

generateSummary()
```

---

# 9. Exceptions

Todas las excepciones deben heredar de:

```java
BusinessException
```

o

```java
TechnicalException
```

---

Ejemplos:

```java
EventNotFoundException

UserNotFoundException

PublicationFailedException
```

---

# 10. Logging

Framework:

```text
Logback
```

---

Niveles permitidos:

```text
INFO
WARN
ERROR
```

---

No utilizar:

```text
System.out.println()
```

---

# 11. Testing

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
>70%
```

---

Cobertura obligatoria:

```text
Use Cases

Servicios Dominio
```

---

# 12. Convenciones SQL

Tablas:

```sql
snake_case
```

---

Correcto:

```sql
news_articles
event_ai_analysis
generated_content
```

---

Incorrecto:

```sql
NewsArticles
GeneratedContent
```

---

Columnas:

```sql
snake_case
```

---

Correcto:

```sql
published_at

created_at

updated_at
```

---

# 13. Convenciones Flyway

Ubicación:

```text
db/migration
```

---

Formato:

```text
V1__initial_schema.sql

V2__create_indexes.sql

V3__seed_data.sql
```

---

Nunca:

```text
Modificar una migración ejecutada
```

---

# 14. Convenciones Angular

Carpetas:

```text
features

shared

core
```

---

Ejemplo:

```text
features/news

features/events

features/content
```

---

Componentes:

```text
PascalCase
```

---

Archivos:

```text
kebab-case
```

---

Ejemplo:

```text
event-list.component.ts
```

---

# 15. Convenciones API REST

Prefijo:

```http
/api/v1
```

---

Correcto:

```http
GET /api/v1/events

GET /api/v1/events/{id}

POST /api/v1/events
```

---

Incorrecto:

```http
/getEvents

/createEvent
```

---

# 16. Convenciones JSON

Propiedades:

```json
camelCase
```

---

Correcto:

```json
{
  "eventId": 10,
  "eventStatus": "OPEN"
}
```

---

# 17. Convenciones n8n

Formato:

```text
WF-01-Capture-News

WF-02-Classify-News

WF-03-Detect-Events
```

---

Variables:

```text
UPPER_SNAKE_CASE
```

---

Ejemplo:

```text
OPENAI_API_KEY

DB_HOST

DB_PORT
```

---

# 18. Convenciones Git

Modelo:

```text
GitFlow Simplificado
```

---

Ramas:

```text
main

develop
```

---

Features:

```text
feature/news-module

feature/event-module

feature/content-module
```

---

Fixes:

```text
fix/publication-error
```

---

# 19. Convenciones Commit

Formato:

```text
tipo: descripción
```

---

Ejemplos:

```text
feat: create event module

fix: publication retry logic

docs: update architecture document

refactor: simplify event matching
```

---

Tipos:

```text
feat

fix

docs

refactor

test

chore
```

---

# 20. Docker

Contenedores oficiales MVP

```text
postgres

spring-boot

n8n

nginx
```

---

# 21. Seguridad

Nunca almacenar:

```text
API Keys

Passwords

Tokens
```

en código.

---

Utilizar:

```text
.env

Docker Secrets (futuro)
```

---

# 22. IA

Todo acceso a IA debe realizarse mediante:

```java
AIProvider
```

---

Nunca:

```java
new OpenAIClient()
```

directamente desde el negocio.

---

# 23. Publicaciones

Todo canal debe implementar:

```java
PublishingProvider
```

---

Ejemplo:

```java
TelegramPublisher
```

---

# 24. Revisión de Código

Antes de mergear:

- Compila.
- Tests OK.
- Sin warnings críticos.
- Documentado.
- Cumple este documento.

---

# 25. Decisiones Arquitectónicas

### DEV-001

Java 21 LTS.

### DEV-002

Spring Boot 3.x.

### DEV-003

Clean Architecture obligatoria.

### DEV-004

DDD obligatorio.

### DEV-005

Flyway obligatorio.

### DEV-006

DTO obligatorio.

### DEV-007

Controllers sin lógica.

### DEV-008

Casos de uso como punto único de entrada.

### DEV-009

GitFlow simplificado.

### DEV-010

Event como Aggregate Root principal.