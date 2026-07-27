Versión: 1.0

Estado: Diseño Funcional MVP

---

# 1. Objetivo

Definir:

- Experiencia de usuario.
- Pantallas.
- Navegación.
- Flujos operativos.
- Componentes principales.
- Diseño MVP.

Este documento servirá de base para:

- Angular Frontend.
- Diseño UI.
- Desarrollo Mobile futuro.

---

# 2. Principios de Diseño

## UX-001

Priorizar productividad.

---

## UX-002

Menos clics = mejor.

---

## UX-003

La información importante debe verse inmediatamente.

---

## UX-004

El editor trabaja sobre eventos.

No sobre noticias.

---

## UX-005

Diseño responsive.

Preparado para futura App móvil.

---

# 3. Perfiles de Usuario

## ADMIN

Puede:

- Gestionar usuarios.
- Gestionar fuentes.
- Configuración IA.
- Revisar contenido.
- Publicar.

---

## EDITOR

Puede:

- Revisar eventos.
- Generar contenido.
- Aprobar contenido.
- Publicar.

---

# 4. Menú Principal

```text
Dashboard

Eventos

Noticias

Contenido

Publicaciones

Fuentes

Usuarios

Configuración
```

---

# 5. Mapa de Navegación

```text
Login
 ↓

Dashboard
 ↓

 ├─ Eventos
 │     └─ Detalle Evento
 │
 ├─ Noticias
 │
 ├─ Contenido
 │     └─ Detalle Contenido
 │
 ├─ Publicaciones
 │
 ├─ Fuentes
 │
 └─ Configuración
```

---

# 6. Dashboard

Pantalla inicial.

---

## Objetivo

Conocer rápidamente:

- Qué está ocurriendo.
- Qué eventos son importantes.
- Qué contenido necesita revisión.

---

## Widgets

### Noticias capturadas hoy

```text
58
```

---

### Eventos activos

```text
14
```

---

### Contenidos pendientes

```text
6
```

---

### Publicaciones realizadas

```text
12
```

---

### Errores workflows

```text
1
```

---

## Tabla Eventos Prioritarios

Columnas:

```text
Título

Categoría

Impacto

Noticias Asociadas

Última Actualización

Estado
```

---

# 7. Pantalla Eventos

Es la pantalla principal del sistema.

---

## Tabla

Columnas:

```text
ID

Título

Categoría

Impacto

Noticias

Estado

Actualizado
```

---

## Filtros

Categoría

Estado

Impacto

Fecha

---

## Acciones

Ver

Generar análisis

Generar contenido

Cerrar evento

Fusionar evento

---

# 8. Detalle Evento

Pantalla más importante.

---

## Cabecera

```text
Título Evento

Categoría

Impacto

Estado
```

---

## Pestañas

### Resumen

### Noticias

### IA

### Contenido

### Historial

---

# 9. Pestaña Resumen

Mostrar:

```text
Descripción evento

Número noticias

Fecha inicio

Última actualización
```

---

# 10. Pestaña Noticias

Noticias asociadas.

---

## Tabla

```text
Fuente

Título

Fecha

Relevancia
```

---

## Acción

Abrir noticia original.

---

# 11. Pestaña IA

Mostrar análisis consolidado.

---

## Bloques

### Resumen Ejecutivo

---

### Resumen Sindical

---

### Puntos Clave

---

### Riesgos

---

### Oportunidades

---

### Colectivos Afectados

---

# 12. Pestaña Contenido

Contenido generado.

---

## Lista

```text
Telegram

Facebook

X

Estado
```

---

## Acciones

Ver

Editar

Aprobar

Rechazar

Regenerar

Publicar

---

# 13. Pantalla Noticias

Vista secundaria.

---

## Objetivo

Auditoría.

---

## Tabla

```text
Fuente

Título

Categoría

Evento

Fecha

Estado
```

---

## Filtros

Fuente

Categoría

Evento

Estado

---

# 14. Pantalla Contenido

Todos los contenidos generados.

---

## Tabla

```text
Canal

Título

Estado

Creado

Aprobado
```

---

## Estados

```text
GENERATED

PENDING_REVIEW

APPROVED

REJECTED

PUBLISHED
```

---

# 15. Editor de Contenido

Pantalla crítica.

---

## Panel Izquierdo

Información evento.

---

## Panel Derecho

Editor.

---

Campos:

```text
Título

Contenido

Hashtags
```

---

## Botones

```text
Guardar

Regenerar

Aprobar

Publicar
```

---

# 16. Pantalla Publicaciones

Histórico.

---

## Tabla

```text
Canal

Fecha

Estado

Respuesta Canal
```

---

Estados:

```text
PUBLISHED

FAILED

RETRY
```

---

# 17. Pantalla Fuentes

Administración.

---

## Tabla

```text
Nombre

Tipo

Prioridad

Estado
```

---

## Acciones

Crear

Editar

Desactivar

Probar fuente

---

# 18. Pantalla Usuarios

Solo ADMIN.

---

## Tabla

```text
Nombre

Email

Rol

Activo
```

---

# 19. Configuración IA

Solo ADMIN.

---

## Configuración

Modelo IA

Temperatura

Tokens

Prompt versión

---

El proveedor `Determinista local` no requiere credenciales ni catálogo de
modelos. En `/settings` su tarjeta permite únicamente activarlo o pausarlo y
guardar ese estado; las opciones de clave API y recarga de modelos se reservan
para proveedores externos.

---

# 20. Flujo Principal del Editor

```text
Dashboard
 ↓

Evento detectado
 ↓

Abrir evento
 ↓

Leer análisis IA
 ↓

Generar contenido
 ↓

Editar si necesario
 ↓

Aprobar
 ↓

Publicar Telegram
```

---

# 21. Flujo Principal del Administrador

```text
Dashboard
 ↓

Supervisar sistema
 ↓

Gestionar fuentes
 ↓

Revisar errores
 ↓

Gestionar usuarios
```

---

# 22. Diseño Responsive

Desktop:

```text
Sidebar + Contenido
```

---

Tablet:

```text
Sidebar colapsable
```

---

Móvil (futuro):

```text
Bottom Navigation
```

---

# 23. Componentes Angular

## Core

```text
Header

Sidebar

Footer

Notifications
```

---

## Shared

```text
DataTable

ConfirmDialog

LoadingSpinner

StatusBadge

SearchBar
```

---

## Features

```text
Events

News

Content

Publications

Users

Settings
```

---

# 24. MVP Inicial

Pantallas obligatorias:

✓ Login

✓ Dashboard

✓ Eventos

✓ Detalle Evento

✓ Contenido

✓ Publicaciones

---

Pantallas posteriores:

○ Usuarios

○ Configuración avanzada

○ Métricas

---

# 25. Diseño Visual

Estilo:

```text
Profesional

Institucional

Moderno

Minimalista
```

---

Inspiración:

```text
Linear

Notion

GitLab

Grafana
```

---

# 26. Decisiones UX

### UX-001

Event es el centro de navegación.

### UX-002

El editor trabaja sobre eventos.

### UX-003

Las noticias son información secundaria.

### UX-004

Todo contenido requiere aprobación humana.

### UX-005

Dashboard orientado a productividad.

### UX-006


---

# Actualizacion 2026-06-27 - Modernizacion Visual

## Principio

La modernizacion visual adopta PrimeNG + Tailwind como base de interfaz sin cambiar el flujo funcional ni la prioridad editorial del sistema.

## Reglas UX Aplicables

- `Event` permanece como centro de la navegacion y de la toma de decisiones editoriales.
- `News` se mantiene como vista secundaria de auditoria y trazabilidad.
- Las pantallas ADMIN (`settings`, `users`, `sources`, `audit`) deben priorizar densidad informativa, filtros claros y tablas escaneables.
- Las pantallas editoriales (`events`, `content`, `publications`) deben priorizar accion rapida, estado visible y confirmaciones no ambiguas.
- El modo claro/oscuro se mantiene como requisito transversal.
- La interfaz debe ser responsive para movil, tablet, laptop, desktop y ultrawide, evitando scroll horizontal salvo tablas complejas justificadas.
- La accesibilidad objetivo es WCAG 2.2 AA en navegacion por teclado, foco visible, contraste, formularios, tablas y dialogos.

---

# Actualización 2026-07-27 - Rotulación de detalles operativos

Los diálogos de detalle de operaciones y errores de `/settings` deben presentar
en español las claves técnicas recibidas desde `operationDetails`.

Reglas UX aplicables:

- Las claves conocidas de `WF-02` a `WF-06` tienen una etiqueta funcional
  explícita.
- Ninguna clave futura en `camelCase`, `snake_case` o `kebab-case` se muestra
  como una palabra técnica concatenada.
- Los valores booleanos se presentan como `Sí` o `No`.
- Los tipos de operación y entidad se muestran con su denominación legible sin
  modificar el contrato JSON de la API.
