# Documento 30 – MVP Técnico Ejecutable

**Versión:** 1.1
**Estado:** plan maestro ejecutado y consolidado
**Última revisión:** 25/07/2026

---

## Estado de ejecución

Las fases 0 a 12 del MVP están implementadas. El estado vigente es:

- backend Spring Boot con DDD, Clean Architecture y monolito modular;
- esquema PostgreSQL consolidado mediante Flyway;
- `WF-01-Capture-News` como único workflow n8n;
- `WF-02` a `WF-04` como automatizaciones internas configurables;
- `WF-05` y `WF-06` bajo demanda o programación en Spring Boot;
- backoffice Angular operativo;
- JWT, roles, usuarios y auditoría;
- configuración ADMIN, observabilidad IA y Telegram;
- pruebas unitarias, integración y Playwright.

La preparación de entrega TFM se controla como Sprint 15 en el Documento 31.
El repositorio ya es público; los únicos pendientes obligatorios de entrega son
externos al código: publicar los cambios locales, habilitar las slides públicas
y grabar/publicar el vídeo.

---

# 1. Objetivo

Definir exactamente:

- Qué se va a construir.
    
- En qué orden.
    
- Qué dependencias existen.
    
- Qué entregables tendrá cada fase.
    
- Qué criterio marca el final de cada fase.
    

Este documento será la referencia principal durante todo el desarrollo.

---

# 2. Filosofía del MVP

El objetivo NO es construir una plataforma completa.

El objetivo es validar que podemos:

1. Capturar noticias educativas de Andalucía.
    
2. Clasificarlas mediante IA.
    
3. Agruparlas en eventos.
    
4. Generar análisis consolidados.
    
5. Generar contenido editorial.
    
6. Publicar contenido útil para el sindicato.
    

---

# 3. Arquitectura MVP vigente

```text
Fuentes RSS
  -> WF-01 n8n Captura Noticias
  -> Spring Boot / PostgreSQL
  -> Automatización Clasificación IA
  -> Automatización Agrupación Eventos
  -> Automatización Análisis IA
  -> Generación Contenido bajo demanda
  -> Revisión Humana
  -> Publicación Telegram inmediata o programada
```

Flujo de dominio:

```text
News -> Event -> Analysis -> Content -> Publication
```

`Event` es el aggregate root principal. n8n no contiene reglas de negocio.

---

# 4. MVP Fase 0

Infraestructura Base

Objetivo:

Tener una plataforma estable y preparada.

---

## Entregables

### PostgreSQL

Base de datos operativa.

---

### Spring Boot

Proyecto inicial.

---

### Flyway

Migraciones activas.

---

### Docker

Contenedores operativos.

---

### Git

Repositorio inicial.

---

## Resultado esperado

```text
docker ps

postgres
springboot
n8n
nginx
```

Funcionando.

---

# 5. MVP Fase 1

Backend Base

Objetivo:

Construir el núcleo del sistema.

---

## Crear proyecto Spring Boot

Java 21

Spring Boot 3.x

---

## Dependencias

```text
Spring Web

Spring Data JPA

PostgreSQL

Validation

Security

JWT

Flyway

OpenAPI

Lombok
```

---

## Endpoint inicial

```http
GET /api/v1/health
```

Respuesta:

```json
{
  "status": "UP"
}
```

---

## Resultado esperado

API funcionando.

---

# 6. MVP Fase 2

Modelo de Datos

Objetivo:

Crear esquema definitivo MVP.

---

## Migración V1

Tablas:

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

## Resultado esperado

Base de datos creada automáticamente mediante Flyway.

---

# 7. MVP Fase 3

Módulo Sources

Objetivo:

Gestionar fuentes.

---

## Entidad

Source

---

## Repository

SourceRepository

---

## Casos de Uso

CreateSourceUseCase

UpdateSourceUseCase

ListSourcesUseCase

---

## Endpoints

```http
GET /api/v1/sources

POST /api/v1/sources

PUT /api/v1/sources/{id}
```

---

## Resultado esperado

Fuentes gestionables desde API.

---

# 8. MVP Fase 4

Módulo News

Objetivo:

Almacenar noticias.

---

## Entidad

NewsArticle

---

## Repository

NewsRepository

---

## Casos de Uso

CreateNewsUseCase

GetNewsUseCase

SearchNewsUseCase

---

## Endpoints

```http
POST /api/v1/news

GET /api/v1/news

GET /api/v1/news/{id}
```

---

## Cambio Arquitectónico

n8n dejará de escribir directamente en PostgreSQL.

Nuevo flujo:

```text
n8n
 ↓
POST /api/v1/news
 ↓
Spring Boot
 ↓
PostgreSQL
```

---

## Resultado esperado

Noticias entrando por API.

---

# 9. MVP Fase 5

WF-01 Captura Noticias

Objetivo:

Integrar captura real.

---

## Workflow

Cron

↓

Leer fuentes

↓

RSS/XML

↓

Normalizar

↓

POST API

↓

Guardar noticia

---

## Resultado esperado

Noticias capturadas automáticamente.

---

# 10. MVP Fase 6

Clasificación IA

Objetivo:

Clasificar noticias.

---

## Tabla

news_classifications

---

## Workflow

WF-02

---

## IA

Prompt WF-02

Documento 23

---

## Resultado esperado

Noticias clasificadas.

---

# 11. MVP Fase 7

Eventos

Objetivo:

Agrupar noticias.

---

## Tablas

events

event_news

---

## Workflow

WF-03

---

## Regla

Una noticia pertenece a un único evento principal.

---

## Resultado esperado

5 noticias

↓

1 evento

---

# 12. MVP Fase 8

Análisis IA

Objetivo:

Generar conocimiento.

---

## Tabla

event_ai_analysis

---

## Workflow

WF-04

---

## Resultado esperado

Resumen Ejecutivo

Resumen Sindical

Riesgos

Oportunidades

---

# 13. MVP Fase 9

Contenido

Objetivo:

Generar contenido editorial.

---

## Tabla

generated_content

---

## Workflow

WF-05

---

## Canales MVP

Telegram

---

## Resultado esperado

Contenido listo para revisión.

---

# 14. MVP Fase 10

Publicación

Objetivo:

Publicar.

---

## Tabla

publications

---

## Workflow

WF-06

---

## Canal

Telegram

---

## Resultado esperado

Publicación real.

---

# 15. MVP Fase 11

Frontend Angular

Objetivo:

Crear backoffice.

---

## Pantallas MVP

Login

Dashboard

Eventos

Detalle Evento

Contenido

Publicaciones

---

## Resultado esperado

Editor trabajando sin acceder a PostgreSQL.

---

# 16. MVP Fase 12

Optimización IA

Objetivo:

Reducir costes.

---

## Mejoras

Procesamiento por lotes

Reutilización análisis

Cache IA

Versionado prompts

---

# 17. Criterio de MVP Completado

El MVP se considerará terminado cuando:

✓ Captura noticias automáticamente.

✓ Clasifica noticias.

✓ Agrupa noticias.

✓ Genera eventos.

✓ Genera análisis.

✓ Genera contenido.

✓ Permite aprobación humana.

✓ Publica en Telegram.

✓ Dispone de backoffice web.

---

# 18. Orden Exacto de Construcción

FASE 0

Infraestructura

↓

FASE 1

Spring Boot Base

↓

FASE 2

Flyway

↓

FASE 3

Modelo de Datos

↓

FASE 4

Sources

↓

FASE 5

News

↓

FASE 6

WF-01

↓

FASE 7

Clasificación IA

↓

FASE 8

Eventos

↓

FASE 9

Análisis

↓

FASE 10

Contenido

↓

FASE 11

Telegram

↓

FASE 12

Angular

---

# 19. Regla de Oro

Nunca desarrollar:

Eventos

Análisis

Contenido

Angular

antes de tener completamente estable:

```text
Sources
+
News
+
WF-01
```

Porque todo el sistema depende de ello.

---

# 20. Próximo Desarrollo

Primer entregable real:

Proyecto Spring Boot funcionando.

Objetivo inmediato:

```http
GET /api/v1/health
```

respondiendo correctamente.

Después:

```text
Flyway

V1__initial_schema.sql

Source

NewsArticle
```
