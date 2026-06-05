## Plataforma de Inteligencia Informativa para Sindicato de Docentes de Andalucía

Versión 1.0

Estado: MVP

---

# 1. Objetivos UX

La plataforma debe permitir:

### Descubrir

Nuevos eventos relevantes.

---

### Analizar

Información consolidada.

---

### Decidir

Qué contenido publicar.

---

### Publicar

Con el menor número de clics posible.

---

# Principio UX Principal

## El Evento es el centro

El usuario NO trabaja con noticias.

Trabaja con:

```
EVENTOS
```

---

# Flujo ideal

```
Noticias
↓
Evento
↓
Resumen IA
↓
Contenido
↓
Publicación
```

---

# 2. Mapa de Navegación

```
Login
│
├── Dashboard
│
├── Noticias
│    ├── Listado
│    └── Detalle
│
├── Eventos
│    ├── Listado
│    └── Detalle
│
├── Contenido
│    ├── Listado
│    └── Editor
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

# 3. Layout Principal

---

## Sidebar

Izquierda.

```
┌─────────────────┐
│ LOGO            │
├─────────────────┤
│ Dashboard       │
│ Noticias        │
│ Eventos         │
│ Contenido       │
│ Publicaciones   │
│ Fuentes         │
│ Usuarios        │
│ Configuración   │
└─────────────────┘
```

---

## Top Bar

```
┌───────────────────────────────────┐
│ Buscar │ Notificaciones │ Usuario │
└───────────────────────────────────┘
```

---

# 4. Pantalla Login

---

## Objetivo

Acceso seguro.

---

## Wireframe

```
┌───────────────────────────┐
│      LOGO SINDICATO       │
│                           │
│ Email                     │
│ [____________________]    │
│                           │
│ Contraseña                │
│ [____________________]    │
│                           │
│ [ Iniciar Sesión ]        │
└───────────────────────────┘
```

---

# 5. Dashboard

---

## Objetivo

Visión global.

---

## Wireframe

```
┌────────────┬────────────┐
│ Noticias   │ Eventos    │
│ Hoy        │ Activos    │
└────────────┴────────────┘

┌────────────┬────────────┐
│ Pendientes │ Publicadas │
└────────────┴────────────┘
```

---

## Widgets

### Noticias Hoy

---

### Eventos Activos

---

### Contenido Pendiente

---

### Publicaciones Hoy

---

### Últimos Eventos

```
Evento
Categoría
Estado
Fecha
```

---

# 6. Noticias

---

## Listado

```
┌───────────────────────────────┐
│ Buscar                        │
└───────────────────────────────┘

┌───────────────────────────────┐
│ Fuente | Categoría | Fecha    │
└───────────────────────────────┘

┌───────────────────────────────┐
│ Noticias                      │
└───────────────────────────────┘
```

---

## Tabla

```
Título

Fuente

Categoría

Fecha

Estado
```

---

# Detalle Noticia

---

## Panel izquierdo

```
Título

Fuente

Fecha

URL original
```

---

## Panel central

```
Contenido completo
```

---

## Panel derecho

```
Clasificación IA

Categoría

Relevancia

Keywords
```

---

# 7. Eventos

---

## Pantalla más importante del sistema

---

# Lista de Eventos

---

## Wireframe

```
┌─────────────────────────────────────┐
│ Buscar Evento                       │
└─────────────────────────────────────┘

Filtros

Categoría
Estado
Importancia
Fecha
```

---

## Tarjetas

```
┌────────────────────────────┐
│ Título Evento              │
│ Categoría                  │
│ Noticias: 8                │
│ Estado: Activo             │
└────────────────────────────┘
```

---

# Detalle Evento

---

## Cabecera

```
Título

Categoría

Estado

Importancia
```

---

## Pestañas

```
Resumen

Noticias

Contenido

Historial
```

---

### Resumen

```
Resumen Ejecutivo

Resumen Sindical

Puntos Clave
```

---

### Noticias

```
Noticias agrupadas
```

---

### Contenido

```
Telegram

Facebook

X
```

---

### Historial

```
Acciones realizadas
```

---

# 8. Editor de Contenido

---

## Objetivo

Modificar contenido generado por IA.

---

## Wireframe

```
┌─────────────────────────────┐
│ Canal                       │
│ Telegram                    │
└─────────────────────────────┘

┌─────────────────────────────┐
│ Editor Texto                │
│                             │
│ Contenido generado          │
│                             │
└─────────────────────────────┘

[ Regenerar ]

[ Aprobar ]

[ Rechazar ]
```

---

# 9. Comparador de Versiones

Muy recomendable para Fase 2.

---

```
┌──────────────┬──────────────┐
│ Versión 1    │ Versión 2    │
└──────────────┴──────────────┘
```

---

# 10. Publicaciones

---

## Listado

```
Canal

Fecha

Estado

Resultado
```

---

## Estados

```
Pendiente

Programada

Publicada

Error
```

---

# 11. Wizard de Publicación

---

## Paso 1

Seleccionar Canal

```
○ Telegram

○ Facebook

○ X
```

---

## Paso 2

Previsualización

```
Vista previa publicación
```

---

## Paso 3

Publicar

```
[ Publicar Ahora ]

[ Programar ]
```

---

# 12. Gestión de Fuentes

---

## Tabla

```
Nombre

Tipo

URL

Activa

Prioridad
```

---

## Acciones

```
Editar

Activar

Desactivar
```

---

# 13. Gestión de Usuarios

---

## Tabla

```
Nombre

Email

Rol

Estado
```

---

## Acciones

```
Crear

Editar

Desactivar
```

---

# 14. Diseño Visual

---

## Paleta Inicial

Recomiendo:

```
Color Principal:
Azul institucional
```

---

```
Color Secundario:
Gris neutro
```

---

```
Color Éxito:
Verde
```

---

```
Color Advertencia:
Naranja
```

---

```
Color Error:
ojo
```

---

# 15. Principios UX

---

## UX-001

Máximo 3 clics para llegar a un Evento.

---

## UX-002

Máximo 2 clics para publicar.

---

## UX-003

Toda acción crítica requiere confirmación.

---

## UX-004

Toda IA debe mostrar trazabilidad.

---

## UX-005

Las noticias nunca deben ocultar el Evento.

---

# 16. Evolución Futura

Preparar diseño para:

### PWA

```
Angular PWA
```

---

### App Móvil

```
Flutter
```

---

### Multi-sindicato

Posible fase futura.