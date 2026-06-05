## Plataforma de Inteligencia Informativa para Sindicato de Docentes de Andalucía

Versión 1.0

Estado: Arquitectura de Dominio

---

# 1. Objetivo

Definir:

- Lenguaje Ubicuo (Ubiquitous Language)
- Entidades
- Value Objects
- Agregados
- Servicios de Dominio
- Eventos de Dominio
- Bounded Contexts

---

# 2. Lenguaje Ubicuo

Todos los desarrolladores deberán utilizar exactamente estos términos.

---

## Source

Fuente de información.

Ejemplos:

- BOJA
- Consejería
- ANPE
- CSIF
- Europa Press

---

## News

Noticia individual capturada desde una fuente.

---

## Event

Hecho o acontecimiento identificado a partir de una o más noticias.

Es la entidad principal del sistema.

---

## Analysis

Interpretación consolidada de un evento.

---

## Content

Contenido generado para comunicación.

---

## Publication

Publicación enviada a un canal.

---

## Editorial Profile

Perfil de comunicación.

---

## Channel

Canal de publicación.

---

## Classification

Resultado IA asociado a una noticia.

---

# 3. Bounded Contexts

No todo pertenece al mismo dominio.

---

# Contexto 1

## News Intelligence

Responsable de:

```
Captura

Clasificación

Agrupación

Eventos
```

---

Entidades:

```
Source

News

Classification

Event
```

---

# Contexto 2

## Editorial

Responsable de:

```
Resúmenes

Contenido

Versiones
```

---

Entidades:

```
Analysis

Content
```

---

# Contexto 3

## Publication

Responsable de:

```
Telegram

Facebook

X
```

---

Entidades:

```
Publication

Channel
```

---

# Contexto 4

## Administration

Responsable de:

```
Usuarios

Roles

Configuración
```

---

Entidades:

```
User

Role
```

---

# 4. Mapa del Dominio

```
Source
  ↓

News
  ↓

Classification
  ↓

Event
  ↓

Analysis
  ↓

Content
  ↓

Publication
```

---

# 5. Entidad Source

Representa una fuente monitorizada.

---

## Atributos

```
id

name

url

type

priority

active
```

---

## Reglas

Una fuente:

- Puede generar muchas noticias.
- Puede activarse/desactivarse.

---

# 6. Entidad News

Representa una noticia individual.

---

## Atributos

```
id

title

url

summary

publishedAt

status
```

---

## Reglas

Una noticia:

- Procede de una única fuente.
- Puede pertenecer a varios eventos (casos excepcionales).
- Debe tener URL única.

---

## Estados

```
CAPTURED

CLASSIFIED

EVENT_MATCHED

ANALYZED
```

---

# 7. Value Object NewsStatus

No es entidad.

No tiene identidad propia.

---

Valores:

```
CAPTURED

CLASSIFIED

EVENT_MATCHED

ANALYZED
```

---

# 8. Entidad Classification

Resultado del análisis IA.

---

## Atributos

```
category

subcategory

relevance

impact

urgency
```

---

## Reglas

Una clasificación:

- Pertenece a una noticia.
- No puede existir sin noticia.

---

# 9. Agregado Event

Este es el Aggregate Root principal.

---

## ¿Por qué?

Porque todo el negocio gira alrededor del evento.

---

## Composición

```
Event
 ├── News
 ├── Analysis
 └── Content
```

---

# Entidad Event

---

## Atributos

```
id

title

description

category

importance

status
```

---

## Reglas

Un evento:

- Debe tener al menos una noticia.
- Puede contener cientos de noticias.
- Tiene una única categoría principal.
- Tiene un único estado.

---

## Estados

```
OPEN

MONITORING

CLOSED

ARCHIVED
```

---

# 10. Value Object Importance

---

Valores:

```
LOW

MEDIUM

HIGH

CRITICAL
```

---

Reglas:

No se guarda lógica fuera del VO.

---

# 11. Entidad Analysis

Representa conocimiento generado.

---

## Atributos

```
executiveSummary

unionSummary

keyPoints

risks

opportunities
```

---

## Regla

Un análisis:

- Siempre pertenece a un evento.
- Puede regenerarse.

---

# 12. Agregado Content

Aggregate Root Editorial.

---

## Entidad Content

Representa una pieza comunicativa.

---

## Atributos

```
id

title

content

channel

profile

status
```

---

## Estados

```
GENERATED

PENDING_REVIEW

APPROVED

REJECTED

PUBLISHED
```

---

# Regla

Un contenido:

- Se genera desde un evento.
- Puede tener múltiples versiones.

---

# 13. Entidad Publication

Representa un envío real.

---

## Atributos

```
id

channel

publishedAt

status

externalId
```

---

## Estados

```
PENDING

SCHEDULED

PUBLISHED

FAILED
```

---

# Regla

Una publicación:

- Siempre proviene de un contenido aprobado.

---

# 14. Entidad User

---

## Roles MVP

```
ADMIN

EDITOR
```

---

# ADMIN

Puede:

```
Gestionar todo
```

---

# EDITOR

Puede:

```
Consultar

Generar contenido

Aprobar

Publicar
```

---

# 15. Servicios de Dominio

Aquí vive la lógica compleja.

---

# EventMatchingService

Responsable de:

```
Detectar si una noticia pertenece a un evento.
```

---

Método:

```
Optional<Event> findMatchingEvent(
    News news
);
```

---

# EventImportanceService

Responsable de:

```
Calcular importancia.
```

---

Método:

```
Importance calculate(Event event);
```

---

# ContentGenerationService

Responsable de:

```
Generar contenido IA.
```

---

# PublicationService

Responsable de:

```
Enviar publicaciones.
```

---

# 16. Eventos de Dominio

Muy recomendables.

---

## NewsCaptured

Cuando se guarda una noticia.

---

## NewsClassified

Cuando IA clasifica.

---

## EventCreated

Cuando se crea evento.

---

## EventUpdated

Cuando se añaden noticias.

---

## AnalysisGenerated

Cuando IA genera resumen.

---

## ContentGenerated

Cuando IA genera contenido.

---

## ContentApproved

Cuando editor aprueba.

---

## PublicationCompleted

Cuando se publica.

---

# 17. Arquitectura Clean

## Domain Layer

Contendrá:

```
domain

news

events

analysis

content

publication

users
```

---

Ejemplo:

```
domain

 ├── news
 │     ├── News
 │     ├── Source
 │     └── Classification
 │
 ├── events
 │     ├── Event
 │     ├── EventMatchingService
 │     └── Importance
 │
 ├── content
 │     ├── Content
 │     └── ContentStatus
 │
 └── publication
       └── Publication
```

---

# 18. Regla Arquitectónica Principal

## DDD-001

La entidad central del negocio NO es la noticia.

Es:

```
EVENT
```

---

Todo debe construirse alrededor de:

```
Event
 ↓
Analysis
 ↓
Content
 ↓
Publication
```

---

# 19. Decisiones Estratégicas de Dominio

### DDD-001

Event es el Aggregate Root principal.

### DDD-002

News es materia prima.

### DDD-003

Analysis representa conocimiento.

### DDD-004

Content representa comunicación.

### DDD-005

Publication representa difusión.

### DDD-006

La IA es un servicio de apoyo, no una entidad del dominio.