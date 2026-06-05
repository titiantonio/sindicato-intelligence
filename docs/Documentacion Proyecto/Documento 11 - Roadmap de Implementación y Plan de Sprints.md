## Plataforma de Inteligencia Informativa para Sindicato de Docentes de Andalucía

Versión 1.0

Estado: Plan de Ejecución

---

# 1. Objetivo

Transformar toda la documentación generada en:

```
Entregables reales
```

---

# Principio

Cada sprint debe dejar un sistema funcional.

Nunca desarrollaremos funcionalidades aisladas.

---

# 2. Roadmap General

## Fase 0

Fundación

---

## Fase 1

Captura Inteligente

---

## Fase 2

Agrupación por Eventos

---

## Fase 3

Backend API

---

## Fase 4

Frontend Angular

---

## Fase 5

Generación Contenido

---

## Fase 6

Publicación

---

## Fase 7

Producción

---

# Situación Actual

Ya tenemos:

```
Proxmox

Docker

n8n

PostgreSQL
```

Por tanto la Fase 0 está parcialmente completada.

---

# FASE 0

# Infraestructura Base

---

## Objetivo

Disponer del entorno técnico.

---

## Entregables

### PostgreSQL

---

### n8n

---

### Nginx

---

### Backups

---

### Git

---

## Resultado

```
Infraestructura estable
```

---

# FASE 1

# Captura Inteligente de Noticias

---

## Objetivo

Crear un sistema capaz de capturar noticias relevantes.

---

# Sprint 1

## BBDD

Tablas:

```
sources

news_articles
```

---

## Workflow

```
WF-01 Captura Noticias
```

---

## Funcionalidades

### Captura RSS

### Captura XML

### Normalización

### Inserción PostgreSQL

### Eliminación duplicados

---

## Resultado esperado

```
100-300 noticias diarias
```

---

# Sprint 2

## Mejorar calidad

---

### Nuevas fuentes

---

### Priorización

---

### Filtrado educativo

---

### Fuentes oficiales

---

### Fuentes sindicales

---

## Resultado esperado

```
Noticias relevantes Andalucía Educación
```

---

# HITO 1

```
Captura funcionando
```

---

# FASE 2

# Clasificación y Eventos

---

# Sprint 3

## IA Clasificación

Workflow:

```
WF-02
```

---

## Generar

```
Categoría

eywords

Relevancia
```

---

Guardar:

```
news_classifications
```

---

# Sprint 4

## Eventos

Workflow:

```
WF-03
```

---

## Crear

```
events

event_news
```

---

## IA

Agrupar noticias.

---

# Resultado

Ya no trabajamos con noticias.

Trabajamos con:

```
EVENTOS
```

---

# HITO 2

```
Agrupación inteligente
```

---

# FASE 3

# Backend Spring Boot

---

# Sprint 5

## Proyecto Base

---

Crear:

```
Spring Boot

Security

JWT

Flyway
```

---

# Sprint 6

## API Noticias

---

Endpoints:

```
GET /news

GET /news/{id}
```

---

# Sprint 7

## API Eventos

---

Endpoints:

```
GET /events

GET /events/{id}
```

---

# Resultado

Backend operativo.

---

# HITO 3

```
API funcionando
```

---

# FASE 4

# Angular

---

# Sprint 8

## Proyecto Angular

---

Crear:

```
Layout

Login

Dashboard
```

---

# Sprint 9

## Noticias

---

Pantallas:

```
Listado

Detalle
```

---

# Sprint 10

## Eventos

---

Pantallas:

```
Listado

Detalle Evento
```

---

# Resultado

Usuarios pueden consultar.

---

# HITO 4

```
Frontend operativo
```

---

# FASE 5

# IA Editorial

---

# Sprint 11

Workflow:

```
WF-04
```

---

## Generar

```
Resumen Ejecutivo

Resumen Sindical
```

---

Guardar:

```
event_ai_analysis
```

---

# Sprint 12

Workflow:

```
WF-05
```

---

## Generar

```
Telegram

acebook

X
```

---

Guardar:

```
generated_content
```

---

# Resultado

Contenido generado automáticamente.

---

# HITO 5

```
IA Editorial funcionando
```

---

# FASE 6

# Publicación

---

# Sprint 13

## Gestión contenido

---

Aprobar

Rechazar

Editar

---

# Sprint 14

## Publicación Telegram

Primer canal.

---

# Sprint 15

## Publicación Facebook

---

# Sprint 16

## Publicación X

---

# Resultado

Publicación real.

---

# HITO 6

```
Publicación automática
```

---

# FASE 7

# Producción

---

# Sprint 17

## Seguridad

---

Auditoría

Logs

Backups

---

# Sprint 18

## Optimización

---

Rendimiento

Consultas

IA

---

# Sprint 19

## Monitorización

---

Alertas

Métricas

Errores

---

# Resultado

Sistema productivo.

---

# MVP Real

Si tu objetivo es obtener valor rápido para el sindicato, el MVP termina aquí:

```
Noticias
↓
Clasificación
↓
Eventos
↓
Resúmenes
↓
Telegram
```

No necesitas inicialmente:

```
Facebook

X

Usuarios avanzados

Analíticas complejas

App móvil
```

---

# MVP Reducido (Recomendación)

Yo incluso haría primero:

```
WF-01 Captura
↓
WF-02 Clasificación
↓
WF-03 Eventos
↓
WF-04 Resumen
↓
Telegram
```

y lo pondría en producción.