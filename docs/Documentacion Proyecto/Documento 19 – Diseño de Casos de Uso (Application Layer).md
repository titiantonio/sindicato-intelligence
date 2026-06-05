Versión: 1.0

Estado: Diseño Funcional MVP

---

# 1. Objetivo

Definir todos los casos de uso del sistema.

Este documento representa la capa Application de la arquitectura.

Cada caso de uso:

- Tiene una única responsabilidad.
- Orquesta el dominio.
- Ejecuta reglas de negocio.
- Gestiona transacciones.
- Puede ser invocado desde:
  - API REST
  - n8n
  - Jobs internos
  - Futuras Apps móviles

---

# 2. Principios

## UC-001

Un caso de uso = una acción de negocio.

---

## UC-002

Los Controllers nunca contienen lógica.

---

## UC-003

Los repositorios nunca contienen lógica de negocio.

---

## UC-004

Toda regla de negocio pasa por un caso de uso.

---

# 3. Módulo Source

Responsable de la gestión de fuentes.

---

## CreateSourceUseCase

Crear una fuente.

### Entrada

```java
CreateSourceCommand
```

### Salida

```java
SourceResponse
```

---

## UpdateSourceUseCase

Modificar una fuente.

---

## EnableSourceUseCase

Activar una fuente.

---

## DisableSourceUseCase

Desactivar una fuente.

---

## GetSourceUseCase

Consultar una fuente.

---

## ListSourcesUseCase

Listar fuentes.

---

# 4. Módulo News

Gestión de noticias capturadas.

---

## CreateNewsUseCase

Registrar noticia capturada.

### Invocado por

```text
WF-01 Captura Noticias
```

---

## GetNewsUseCase

Consultar noticia.

---

## SearchNewsUseCase

Buscar noticias.

Filtros:

- Fecha
- Fuente
- Categoría
- Estado

---

## UpdateNewsStatusUseCase

Actualizar estado.

Estados:

```text
CAPTURED
CLASSIFIED
EVENT_MATCHED
ARCHIVED
```

---

## ArchiveNewsUseCase

Archivar noticia.

---

# 5. Módulo Classification

Clasificación IA.

---

## ClassifyNewsUseCase

Clasificar noticia.

### Invocado por

```text
WF-02 Clasificación IA
```

### Resultado

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

## GetClassificationUseCase

Consultar clasificación.

---

# 6. Módulo Event

Aggregate Root Principal.

---

## CreateEventUseCase

Crear evento.

### Invocado por

```text
WF-03 Detección Eventos
```

---

## AddNewsToEventUseCase

Asociar noticia a evento.

---

## RemoveNewsFromEventUseCase

Eliminar noticia de evento.

---

## CloseEventUseCase

Cerrar evento.

Estados:

```text
OPEN
MONITORING
CLOSED
ARCHIVED
```

---

## ReopenEventUseCase

Reabrir evento.

---

## GetEventUseCase

Consultar evento.

---

## SearchEventsUseCase

Buscar eventos.

Filtros:

- Estado
- Categoría
- Impacto
- Fecha

---

## MergeEventsUseCase

Fusionar eventos.

### Ejemplo

```text
Evento A
+
Evento B

↓

Evento único
```

---

## SplitEventUseCase

Separar evento.

---

# 7. Módulo Analysis

Análisis consolidado IA.

---

## GenerateAnalysisUseCase

Generar análisis.

### Invocado por

```text
WF-04 Análisis IA
```

---

### Resultado

```text
Resumen Ejecutivo

Resumen Sindical

Puntos Clave

Riesgos

Oportunidades
```

---

## RegenerateAnalysisUseCase

Regenerar análisis.

---

## GetAnalysisUseCase

Consultar análisis.

---

# 8. Módulo Content

Contenido editorial.

---

## GenerateContentUseCase

Generar contenido.

### Invocado por

```text
WF-05 Generación Contenido
```

---

### Parámetros

```text
Canal

Tono

Longitud
```

---

## ApproveContentUseCase

Aprobar contenido.

---

## RejectContentUseCase

Rechazar contenido.

---

## RegenerateContentUseCase

Regenerar contenido.

---

## EditContentUseCase

Modificar contenido manualmente.

---

## GetContentUseCase

Consultar contenido.

---

## SearchContentUseCase

Buscar contenido.

---

# 9. Módulo Publication

Publicación.

---

## CreatePublicationUseCase

Crear publicación.

---

## PublishContentUseCase

Publicar contenido.

### Invocado por

```text
WF-06 Publicación Telegram
```

---

## RetryPublicationUseCase

Reintentar publicación.

---

## GetPublicationUseCase

Consultar publicación.

---

## SearchPublicationUseCase

Buscar publicaciones.

---

# 10. Módulo User

Usuarios.

---

## CreateUserUseCase

Crear usuario.

---

## UpdateUserUseCase

Modificar usuario.

---

## DisableUserUseCase

Desactivar usuario.

---

## LoginUseCase

Autenticación.

---

## ChangePasswordUseCase

Cambio contraseña.

---

## GetUserUseCase

Consultar usuario.

---

# 11. Casos de Uso Internos IA

No expuestos públicamente.

---

## DetectDuplicateNewsUseCase

Detectar duplicados.

---

## MatchEventUseCase

Buscar evento similar.

---

## CalculateEventImpactUseCase

Calcular impacto.

---

## ExtractEntitiesUseCase

Extraer entidades.

---

## DetectTrendUseCase

Reservado para futuras versiones.

---

# 12. Casos de Uso Invocados por n8n

WF-01

```text
CreateNewsUseCase
```

---

WF-02

```text
ClassifyNewsUseCase
```

---

WF-03

```text
MatchEventUseCase

CreateEventUseCase

AddNewsToEventUseCase
```

---

WF-04

```text
GenerateAnalysisUseCase
```

---

WF-05

```text
GenerateContentUseCase
```

---

WF-06

```text
PublishContentUseCase
```

---

# 13. Casos de Uso Expuestos por API

Noticias

```text
GetNewsUseCase

SearchNewsUseCase
```

---

Eventos

```text
GetEventUseCase

SearchEventsUseCase

CloseEventUseCase
```

---

Contenido

```text
GetContentUseCase

ApproveContentUseCase

RejectContentUseCase
```

---

Publicaciones

```text
GetPublicationUseCase
```

---

Usuarios

```text
LoginUseCase

GetUserUseCase
```

---

# 14. Priorización MVP

## Críticos

```text
CreateNewsUseCase

ClassifyNewsUseCase

CreateEventUseCase

AddNewsToEventUseCase

GenerateAnalysisUseCase

GenerateContentUseCase

PublishContentUseCase
```

---

## Importantes

```text
GetEventUseCase

SearchEventsUseCase

ApproveContentUseCase

RejectContentUseCase
```

---

## Secundarios

```text
MergeEventsUseCase

SplitEventUseCase

RegenerateContentUseCase
```

---

# 15. Roadmap de Implementación

Sprint 1

```text
Source

News
```

---

Sprint 2

```text
Classification

Event
```

---

Sprint 3

```text
Analysis
```

---

Sprint 4

```text
Content
```

---

Sprint 5

```text
Publication
```

---

Sprint 6

```text
User
```

---

# Decisiones Arquitectónicas

### UC-001

Todos los accesos a negocio se realizan mediante casos de uso.

### UC-002

Los casos de uso son el punto de entrada de la capa Application.

### UC-003

n8n invoca casos de uso mediante API.

### UC-004

Los casos de uso son independientes de la tecnología.

### UC-005

Event es el Aggregate Root principal del sistema.

### UC-006

Los workflows n8n nunca implementan lógica de negocio compleja.