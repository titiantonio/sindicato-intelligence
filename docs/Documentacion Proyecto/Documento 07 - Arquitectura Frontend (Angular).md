## Plataforma de Inteligencia Informativa para Sindicato de Docentes de Andalucía

Versión 1.0

Estado: Diseño Base

---

# 1. Objetivos

El Frontend será responsable de:

### Visualización

- Noticias
- Eventos
- Contenido generado
- Publicaciones

---

### Gestión

- Fuentes
- Usuarios
- Configuración

---

### Revisión editorial

- Aprobar contenido
- Editar contenido
- Publicar

---

### Analítica

- Dashboard
- Métricas

---

# 2. Stack Tecnológico

## Framework

```
Angular 20+
```

---

## UI

```
Angular Material
```

---

## Estilos

```
SCSS
```

---

## Estado

MVP:

```
Signals
```

---

Fase 2:

```
NgRx
```

si fuese necesario.

---

## Formularios

```
Reactive Forms
```

---

## HTTP

```
HttpClient
```

---

## Seguridad

```
JWT
```

---

# 3. Arquitectura General

## Patrón

```
Feature-Based Architecture
```

---

Estructura:

```
src/app

core
shared
features
layout
```

---

# 4. Core Module

Contendrá:

```
core
```

---

## Servicios Globales

```
auth.service

api.service

notification.service

storage.service
```

---

## Guards

```
auth.guard

role.guard
```

---

## Interceptors

```
jwt.interceptor

error.interceptor

loading.interceptor
```

---

# 5. Shared Module

Componentes reutilizables.

```
shared
```

---

## Componentes

```
table

search-box

pagination

confirm-dialog

loading-spinner

empty-state

badge
```

---

## Pipes

```
date-format

status-label

runcate
```

---

# 6. Layout

---

## Estructura Principal

```
layout

header
sidebar
footer
```

---

## Navegación

```
Dashboard

Noticias

Eventos

Contenido

Publicaciones

Fuentes

Usuarios

Configuración
```

---

# 7. Módulos Funcionales

---

# Dashboard

```
features/dashboard
```

---

Pantalla principal.

---

## Widgets

### Noticias

```
Noticias capturadas hoy
```

---

### Eventos

```
Eventos activos
```

---

### Contenido

```
Pendiente revisión
```

---

### Publicaciones

```
Publicadas hoy
```

---

# Noticias

```
features/news
```

---

## Pantallas

### Lista

```
/news
```

---

### Detalle

```
/news/:id
```

---

## Funcionalidades

- Buscar
- Filtrar
- Ordenar
- Ver detalle

---

# Eventos

```
features/events
```

---

Este será el módulo principal.

---

## Lista Eventos

```
/events
```

---

Filtros:

- Categoría
- Estado
- Fecha
- Importancia

---

## Detalle Evento

```
/events/:id
```

---

Información:

### Resumen

```
Título

Descripción

Categoría
```

---

### Noticias asociadas

```
Lista completa
```

---

### Resumen IA

```
Ejecutivo

Sindical
```

---

### Contenido generado

```
Telegram

Facebook
X
```

---

## Acciones

```
Fusionar

Separar

Cerrar
```

---

# Contenido

```
features/content
```

---

## Lista

```
/content
```

---

Filtros:

- Canal
- Estado
- Fecha

---

## Detalle

```
/content/:id
```

---

## Funcionalidades

### Editar

### Regenerar

### Aprobar

### Rechazar

---

# Publicaciones

```
features/publications
```

---

## Lista

```
/publications
```

---

## Información

```
Canal

Fecha

Estado

Resultado
```

---

# Fuentes

```
features/sources
```

---

## Funcionalidades

### Crear

### Editar

### Activar

### Desactivar

---

# Usuarios

```
features/users
```

---

Sólo ADMIN.

---

## Funcionalidades

### Crear usuario

### Editar usuario

### Desactivar usuario

---

# Configuración

```
features/settings
```

---

## Categorías

## Canales

## Perfiles Editoriales

---

# 8. Modelo de Navegación

```
Dashboard
│
├── Noticias
│     └── Detalle
│
├── Eventos
│     └── Detalle Evento
│
├── Contenido
│     └── Detalle Contenido
│
├── Publicaciones
│
├── Fuentes
│
├── Usuarios
│
└── Configuración
```

---

# 9. Responsive

## MVP

Diseño:

```
Desktop First
```

---

Resoluciones objetivo:

```
1920x1080

1366x768

1280x720
```

---

## Móvil futuro

Preparado para:

```
Angular PWA
```

---

# 10. Seguridad

---

# Auth Guard

Bloquea rutas protegidas.

---

# Role Guard

Controla:

```
ADMIN

EDITOR
```

---

# Menú Dinámico

Ejemplo:

EDITOR

```
Dashboard

Noticias

Eventos

Contenido

Publicaciones
```

---

ADMIN

```
Dashboard

Noticias

Eventos

Contenido

Publicaciones

Fuentes

Usuarios

Configuración
```

---

# 11. Estado de la Aplicación

MVP:

```
Signals
```

---

Estados globales:

```
Usuario

Token

Configuración
```

---

Estados locales:

```
Noticias

Eventos

Contenido
```

---

# 12. UX Específica para el Sindicato

Aquí aparece una mejora importante que no habíamos documentado.

---

## Vista de Evento

Debe ser la pantalla principal.

No la noticia.

---

Incorrecto:

```
Noticia
↓
Publicar
```

---

Correcto:

```
Evento
↓
Resumen IA
↓
Contenido generado
↓
Publicar
```

---

Porque el sindicato comunica:

```
Hechos
```

no

```
Artículos individuales
```

---

# 13. Componentes Críticos

---

## Event Summary Card

Mostrará:

```
Título

Categoría

Noticias asociadas

Importancia

Estado
```

---

## AI Summary Panel

Mostrará:

```
Resumen Ejecutivo

Resumen Sindical

Puntos Clave
```

---

## Content Editor

Editor enriquecido.

Permite:

```
Editar

Regenerar

Comparar versiones
```

---

## Publication Wizard

Asistente:

```
Seleccionar Canal
↓
Seleccionar Perfil
↓
Previsualizar
↓
Publicar
```

---

# 14. Roadmap Frontend

## Sprint 1

```
Login

Dashboard

Noticias
```

---

## Sprint 2

```
Eventos

Detalle Evento
```

---

## Sprint 3

```
Contenido

Aprobación
```

---

## Sprint 4

```
Publicaciones
```

---

## Sprint 5

```
Fuentes

Usuarios

Configuración
```

---

# Decisiones Arquitectónicas Frontend

### AF-001

Angular + Angular Material.

### AF-002

Feature-Based Architecture.

### AF-003

Signals como gestión de estado inicial.

### AF-004

Vista principal centrada en Eventos.

### AF-005

Desktop First.

### AF-006

Preparado para Angular PWA futura.

### AF-007

JWT + Guards.

### AF-008

Componentes reutilizables en Shared.