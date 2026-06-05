## Plataforma de Inteligencia Informativa para Sindicato de Docentes de Andalucía

Versión 1.0

Estado: MVP

---

# 1. Introducción

## Objetivo

Definir las interacciones entre los actores y el sistema.

---

# 2. Actores

## Administrador

Responsable de:

- Configuración
- Gestión de usuarios
- Gestión de fuentes
- Supervisión global
- Corrección de eventos

---

## Editor

Responsable de:

- Revisar noticias
- Revisar eventos
- Generar contenido
- Aprobar publicaciones

---

# 3.# Mapa General de Casos de Uso

```
ADMINISTRADOR

 ├─ Gestionar Fuentes
 ├─ Gestionar Usuarios
 ├─ Gestionar Eventos
 ├─ Revisar IA
 ├─ Configurar Canales
 └─ Consultar Dashboard


EDITOR

 ├─ Consultar Noticias
 ├─ Consultar Eventos
 ├─ Revisar Resúmenes
 ├─ Generar Contenido
 ├─ Editar Contenido
 ├─ Aprobar Contenido
 └─ Publicar
```

---

# MÓDULO 1

# Gestión de Fuentes

---

# CU-001 Crear Fuente

## Actor

Administrador

---

## Descripción

Permite registrar una nueva fuente de información.

---

## Flujo Principal

```
Administrador
↓
Nueva Fuente
↓
Introduce datos
↓
Guardar
↓
Fuente creada
```

---

## Datos

- Nombre
- Tipo
- URL
- Prioridad

---

## Resultado

Fuente disponible para captura.

---

# CU-002 Editar Fuente

## Actor

Administrador

---

## Resultado

Actualización de datos.

---

# CU-003 Activar Fuente

---

# CU-004 Desactivar Fuente

---

# CU-005 Consultar Fuentes

---

# MÓDULO 2

# Noticias

---

# CU-006 Consultar Noticias

## Actor

Editor

Administrador

---

## Objetivo

Visualizar noticias capturadas.

---

## Filtros

- Fecha
- Categoría
- Fuente
- Estado

---

## Información mostrada

- Título
- Fuente
- Fecha
- Categoría
- Estado

---

# CU-007 Ver Detalle de Noticia

---

## Información

- Título
- Resumen
- Contenido
- Fuente
- URL original
- Clasificación IA

---

# MÓDULO 3

# Eventos

---

# CU-008 Consultar Eventos

## Actor

Editor

Administrador

---

## Objetivo

Visualizar eventos detectados.

---

## Filtros

- Categoría
- Estado
- Importancia
- Fecha

---

# CU-009 Ver Evento

## Actor

Editor

Administrador

---

## Información

### Datos básicos

- Título
- Descripción
- Categoría

---

### Noticias asociadas

Lista completa.

---

### Resumen IA

- Ejecutivo
- Sindical

---

### Contenido generado

- Telegram
- Facebook
- X

---

# CU-010 Fusionar Eventos

## Actor

Administrador

---

## Flujo

```
Seleccionar Evento A
↓
Seleccionar Evento B
↓
Fusionar
↓
Nuevo Evento Consolidado
```

---

# CU-011 Separar Noticias de Evento

## Actor

Administrador

---

## Objetivo

Corregir agrupaciones erróneas.

---

# CU-012 Cerrar Evento

## Actor

Administrador

---

## Resultado

Evento archivado.

---

# MÓDULO 4

# Inteligencia Artificial

---

# CU-013 Clasificar Noticias

## Actor

Sistema

---

## Trigger

Workflow n8n.

---

## Resultado

Creación de clasificación.

---

# CU-014 Generar Resumen Ejecutivo

## Actor

Sistema

---

## Resultado

Resumen corto.

---

# CU-015 Generar Resumen Sindical

## Actor

Sistema

---

## Resultado

Resumen ampliado.

---

# CU-016 Regenerar Análisis

## Actor

Administrador

Editor

---

## Objetivo

Volver a ejecutar IA.

---

# MÓDULO 5

# Generación de Contenido

---

# CU-017 Generar Contenido

## Actor

Editor

---

## Flujo

```
Seleccionar Evento
↓
Seleccionar Canal
↓
Seleccionar Perfil
↓
Generar
↓
Contenido generado
```

---

## Canales

- Telegram
- Facebook
- X

---

## Perfiles

- Informativo
- Institucional
- Divulgativo

(MVP)

---

# CU-018 Editar Contenido

## Actor

Editor

---

## Objetivo

Modificar contenido generado.

---

# CU-019 Regenerar Contenido

## Actor

Editor

---

## Resultado

Nueva versión.

---

# CU-020 Comparar Versiones

## Actor

Editor

---

## Objetivo

Comparar diferentes propuestas IA.

---

# MÓDULO 6

# Publicaciones

---

# CU-021 Aprobar Contenido

## Actor

Editor

Administrador

---

## Flujo

```
Contenido generado
↓
Revisión
↓
Aprobación
↓
Pendiente publicación
```

---

# CU-022 Rechazar Contenido

---

## Resultado

Vuelve a borrador.

---

# CU-023 Programar Publicación

## Actor

Editor

---

## Datos

- Fecha
- Hora
- Canal

---

# CU-024 Publicar Inmediatamente

## Actor

Editor

---

## Resultado

Publicación enviada.

---

# CU-025 Consultar Historial de Publicaciones

---

## Información

- Canal
- Fecha
- Estado
- Resultado

---

# MÓDULO 7

# Dashboard

---

# CU-026 Dashboard Principal

## Actor

Administrador

Editor

---

## Indicadores

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

### Publicaciones

```
Pendientes
ublicadas
Errores
```

---

# CU-027 Consultar Estadísticas

## Actor

Administrador

---

## Métricas

- Noticias por fuente
- Noticias por categoría
- Eventos creados

---

# MÓDULO 8

# Usuarios

---

# CU-028 Crear Usuario

## Actor

Administrador

---

# CU-029 Modificar Usuario

---

# CU-030 Desactivar Usuario

---

# CU-031 Reactivar Usuario

---

# CU-032 Cambiar Rol

---

# MÓDULO 9

# Configuración

---

# CU-033 Gestionar Categorías

## Actor

Administrador

---

# CU-034 Gestionar Canales

## Actor

Administrador

---

# CU-035 Gestionar Perfiles Editoriales

## Actor

Administrador

---

# Casos de Uso MVP Prioritarios

Estos son los que realmente necesitaremos implementar para tener una primera versión funcional:

### Prioridad 1

```
CU-001 Gestión Fuentes

CU-006 Consultar Noticias

CU-008 Consultar Eventos

CU-009 Ver Evento

CU-017 Generar Contenido

CU-018 Editar Contenido

CU-021 Aprobar Contenido

CU-024 Publicar
```

---

### Prioridad 2

```
CU-010 Fusionar Eventos

CU-011 Separar Noticias

CU-019 Regenerar Contenido

CU-023 Programar Publicación
```

---

### Prioridad 3

```
CU-027 Estadísticas

CU-034 Canales

CU-035 Perfiles Editoriales
```