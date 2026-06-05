Versión: 1.0

Estado: Plan Director de Desarrollo

---

# 1. Objetivo

Transformar la arquitectura diseñada en:

- Épicas
- Historias de usuario
- Sprints
- Entregables
- Dependencias

para construir el MVP de forma incremental.

---

# 2. Filosofía de Desarrollo

## MVP Real

El objetivo NO es construir toda la plataforma.

El objetivo es conseguir que el sindicato pueda:

1. Capturar noticias.
2. Agruparlas por eventos.
3. Generar análisis.
4. Generar contenido.
5. Publicar en Telegram.

lo antes posible.

---

## Principio

Cada sprint debe generar valor real.

Nunca desarrollar funcionalidades que aún no puedan utilizarse.

---

# 3. Roadmap General

```text
FASE 0
Infraestructura

FASE 1
Captura Noticias

FASE 2
Clasificación IA

FASE 3
Eventos

FASE 4
Análisis IA

FASE 5
Contenido

FASE 6
Publicación

FASE 7
Frontend

FASE 8
Optimización
```

---

# 4. FASE 0 – Infraestructura

Objetivo:

Disponer de una plataforma estable.

---

## Historias

### INF-001

Como administrador

quiero disponer de PostgreSQL

para almacenar información.

---

### INF-002

Como administrador

quiero disponer de n8n

para ejecutar automatizaciones.

---

### INF-003

Como administrador

quiero disponer de Spring Boot

para centralizar la lógica.

---

### INF-004

Como administrador

quiero disponer de Nginx

para exponer servicios.

---

## Entregable

```text
Servidor operativo

PostgreSQL

n8n

Spring Boot

Nginx
```

---

# 5. FASE 1 – Captura de Noticias

Objetivo:

Almacenar noticias.

---

## Historias

### NEWS-001

Crear tabla:

```text
sources
```

---

### NEWS-002

Crear tabla:

```text
news_articles
```

---

### NEWS-003

Crear WF-01 Captura.

---

### NEWS-004

Evitar duplicados.

---

### NEWS-005

Registrar errores de captura.

---

## Entregable

```text
Noticias almacenadas automáticamente
```

---

# 6. FASE 2 – Clasificación IA

Objetivo:

Clasificar noticias.

---

## Historias

### CLS-001

Crear tabla:

```text
news_classifications
```

---

### CLS-002

Crear prompt clasificación.

---

### CLS-003

Crear WF-02.

---

### CLS-004

Actualizar estado noticia.

---

## Entregable

```text
Noticias clasificadas automáticamente
```

---

# 7. FASE 3 – Eventos

Objetivo:

Agrupar noticias similares.

---

## Historias

### EVT-001

Crear tabla:

```text
events
```

---

### EVT-002

Crear tabla:

```text
event_news
```

---

### EVT-003

Crear algoritmo agrupación.

---

### EVT-004

Crear WF-03.

---

### EVT-005

Asociar noticias a eventos.

---

## Entregable

```text
Evento único por tema
```

Ejemplo:

```text
5 noticias

↓

1 evento
```

---

# 8. FASE 4 – Análisis IA

Objetivo:

Generar conocimiento.

---

## Historias

### ANA-001

Crear tabla:

```text
event_ai_analysis
```

---

### ANA-002

Crear prompt análisis.

---

### ANA-003

Crear WF-04.

---

### ANA-004

Generar:

- Resumen ejecutivo
- Resumen sindical
- Riesgos
- Oportunidades

---

## Entregable

```text
Análisis consolidado por evento
```

---

# 9. FASE 5 – Contenido

Objetivo:

Generar contenido listo para publicar.

---

## Historias

### CNT-001

Crear tabla:

```text
generated_content
```

---

### CNT-002

Crear perfiles editoriales.

---

### CNT-003

Crear prompt Telegram.

---

### CNT-004

Crear WF-05.

---

### CNT-005

Permitir regeneración.

---

## Entregable

```text
Contenido editorial generado automáticamente
```

---

# 10. FASE 6 – Publicación

Objetivo:

Publicar en Telegram.

---

## Historias

### PUB-001

Crear tabla:

```text
publications
```

---

### PUB-002

Crear integración Telegram.

---

### PUB-003

Crear WF-06.

---

### PUB-004

Registrar publicaciones.

---

### PUB-005

Gestionar errores.

---

## Entregable

```text
Publicación automática Telegram
```

---

# 11. FASE 7 – Frontend Angular

Objetivo:

Interfaz para el editor.

---

## Historias

### UI-001

Login.

---

### UI-002

Listado noticias.

---

### UI-003

Listado eventos.

---

### UI-004

Detalle evento.

---

### UI-005

Detalle análisis.

---

### UI-006

Generación contenido.

---

### UI-007

Aprobación contenido.

---

### UI-008

Publicación manual.

---

## Entregable

```text
Backoffice funcional
```

---

# 12. FASE 8 – Optimización

Objetivo:

Mejorar calidad.

---

## Historias

### OPT-001

Mejorar prompts.

---

### OPT-002

Reducir costes IA.

---

### OPT-003

Mejorar agrupación eventos.

---

### OPT-004

Métricas básicas.

---

## Entregable

```text
Sistema optimizado
```

---

# 13. Sprint Planning Recomendado

## Sprint 1

Infraestructura

```text
PostgreSQL

Spring Boot

Flyway

Docker
```

---

## Sprint 2

Captura Noticias

```text
sources

news_articles

WF-01
```

---

## Sprint 3

Clasificación

```text
news_classifications

WF-02
```

---

## Sprint 4

Eventos

```text
events

event_news

WF-03
```

---

## Sprint 5

Análisis

```text
event_ai_analysis

WF-04
```

---

## Sprint 6

Contenido

```text
generated_content

WF-05
```

---

## Sprint 7

Telegram

```text
publications

WF-06
```

---

## Sprint 8

Frontend MVP

```text
Login

Noticias

Eventos

Contenido
```

---

# 14. MVP Mínimo Publicable

El sistema se considerará MVP cuando:

✓ Capture noticias

✓ Clasifique noticias

✓ Agrupe eventos

✓ Genere análisis

✓ Genere contenido

✓ Permita revisión humana

✓ Publique en Telegram

---

# 15. MVP Objetivo del Sindicato

Resultado esperado:

```text
50 noticias diarias
↓
15 eventos
↓
15 análisis
↓
15 borradores Telegram
↓
5 publicaciones finales
```

---

# 16. Riesgos Principales

## R-001

Agrupación incorrecta de eventos.

Mitigación:

Supervisión manual.

---

## R-002

Coste IA elevado.

Mitigación:

Procesamiento por lotes.

---

## R-003

Noticias duplicadas.

Mitigación:

Hash + URL única.

---

## R-004

Publicaciones erróneas.

Mitigación:

Aprobación humana obligatoria.

---

# 17. Criterio de Éxito

El proyecto será exitoso cuando:

- Reduzca más del 80% del trabajo manual.
- Evite publicaciones duplicadas.
- Permita identificar rápidamente temas relevantes para docentes andaluces.
- Genere contenido reutilizable para Telegram.
- Sirva como base para futuras apps móviles y multicanal.

---

# Decisiones Estratégicas

### PM-001

Event es la entidad central del sistema.

### PM-002

Telegram es el único canal MVP.

### PM-003

La aprobación humana es obligatoria.

### PM-004

Spring Boot será el núcleo del sistema.

### PM-005

n8n será únicamente el motor de automatización.

### PM-006

La arquitectura se diseña desde el inicio para evolucionar hacia aplicación móvil y publicación multicanal.