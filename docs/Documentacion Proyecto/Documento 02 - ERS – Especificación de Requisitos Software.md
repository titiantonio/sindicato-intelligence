 Estructurarlo siguiendo una adaptación de **IEEE 830 / ISO 29148**.
 
--- 
## Plataforma de Inteligencia Informativa para Sindicato de Docentes de Andalucía

**Versión:** 1.0  
**Estado:** Borrador Inicial  
**Documento derivado de:** Documento 01 – Visión y Arquitectura General

---

# 1. Introducción

## 1.1 Propósito

El propósito de este documento es definir de forma detallada los requisitos funcionales y no funcionales de la Plataforma de Inteligencia Informativa para el Sindicato de Docentes de Andalucía.

Este documento servirá como referencia para:

- Diseño funcional.
- Diseño técnico.
- Desarrollo.
- Pruebas.
- Validación.

---

## 1.2 Alcance

La plataforma permitirá:

- Capturar noticias de múltiples fuentes.
- Clasificarlas mediante IA.
- Agruparlas en eventos.
- Generar contenido editorial.
- Gestionar publicaciones.
- Publicar en canales de comunicación.
- Proporcionar herramientas de inteligencia informativa.

---

## 1.3 Definiciones

### Noticia

Unidad informativa obtenida desde una fuente.

---

### Evento

Agrupación de noticias relacionadas.

---

### Fuente

Origen de una noticia.

---

### Contenido

Texto generado para un canal específico.

---

### Publicación

Contenido aprobado y distribuido.

---

# 2. Descripción General

---

## 2.1 Perspectiva del Producto

La plataforma estará compuesta por:

```
Fuentes
   ↓
n8n
   ↓
PostgreSQL
   ↓
Spring Boot
   ↓
Angular
```

---

## 2.2 Usuarios

### Administrador

Responsable de:

- Configuración.
- Usuarios.
- Fuentes.
- Supervisión.

---

### Editor

Responsable de:

- Revisar eventos.
- Aprobar contenido.
- Publicar.

---

## 2.3 Restricciones

### TEC-R01

El sistema deberá utilizar PostgreSQL como única base de datos.

---

### TEC-R02

El sistema deberá ejecutarse sobre infraestructura propia en Proxmox.

---

### TEC-R03

Toda publicación deberá ser aprobada por un usuario humano.

---

### TEC-R04

La IA no podrá publicar contenido directamente.

---

# 3. Requisitos Funcionales

---

# MÓDULO 1

# Gestión de Fuentes

---

## RF-001 Crear Fuente

El administrador podrá registrar una fuente.

### Datos mínimos

- Nombre
- URL
- Tipo
- Estado

### Prioridad

Alta

---

## RF-002 Modificar Fuente

El administrador podrá modificar cualquier fuente.

---

## RF-003 Activar Fuente

El administrador podrá activar una fuente.

---

## RF-004 Desactivar Fuente

El administrador podrá desactivar una fuente.

---

## RF-005 Consultar Fuentes

El usuario podrá visualizar el catálogo de fuentes.

---

# MÓDULO 2

# Captura de Noticias

---

## RF-006 Captura Automática

El sistema deberá capturar noticias automáticamente.

---

## RF-007 Almacenamiento RAW

El sistema deberá almacenar la noticia original.

---

## RF-008 Evitar Duplicados

El sistema deberá impedir almacenar noticias duplicadas.

Criterio:

```
url única
```

---

## RF-009 Registrar Fecha Captura

Toda noticia deberá registrar fecha y hora de captura.

---

## RF-010 Consultar Noticias

Los usuarios podrán consultar noticias capturadas.

---

# MÓDULO 3

# Clasificación IA

---

## RF-011 Clasificar Categoría

El sistema deberá clasificar noticias.

Ejemplos:

- SIPRI
- Oposiciones
- FP
- Normativa

---

## RF-012 Calcular Relevancia

Escala:

```
1-10
```

---

## RF-013 Calcular Impacto

Escala:

```
1-10
```

---

## RF-014 Calcular Urgencia

Escala:

```
1-10
```

---

## RF-015 Generar Etiquetas

El sistema deberá generar etiquetas temáticas.

---

# MÓDULO 4

# Gestión de Eventos

---

## RF-016 Crear Evento

El sistema podrá crear eventos automáticamente.

---

## RF-017 Asociar Noticias a Evento

Una noticia deberá poder pertenecer a un evento.

---

## RF-018 Fusionar Eventos

El administrador podrá fusionar eventos.

---

## RF-019 Separar Noticias

El administrador podrá mover noticias entre eventos.

---

## RF-020 Cerrar Evento

El administrador podrá cerrar un evento.

---

## RF-021 Consultar Evento

Los usuarios podrán consultar un evento.

---

## RF-022 Visualizar Noticias Asociadas

El usuario podrá ver todas las noticias de un evento.

---

# MÓDULO 5

# Inteligencia Artificial

---

## RF-023 Generar Resumen Ejecutivo

Máximo:

```
150 palabras
```

---

## RF-024 Generar Resumen Sindical

Máximo:

```
300 palabras
```

---

## RF-025 Generar Puntos Clave

Mínimo:

```
3 puntos
```

Máximo:

```
10 puntos
```

---

## RF-026 Generar Riesgos Detectados

La IA podrá detectar riesgos relevantes.

---

## RF-027 Generar Oportunidades Detectadas

La IA podrá detectar oportunidades.

---

# MÓDULO 6

# Generación de Contenido

---

## RF-028 Generar Telegram

El sistema deberá generar contenido para Telegram.

---

## RF-029 Generar Facebook

El sistema deberá generar contenido para Facebook.

---

## RF-030 Generar X

El sistema deberá generar contenido para X.

---

## RF-031 Seleccionar Perfil Editorial

El usuario podrá elegir:

- Informativo
- Institucional
- Divulgativo
- Reivindicativo
- Movilizador

---

## RF-032 Regenerar Contenido

El usuario podrá solicitar una nueva versión.

---

## RF-033 Editar Contenido

El usuario podrá modificar manualmente el contenido.

---

# MÓDULO 7

# Publicaciones

---

## RF-034 Crear Publicación

El sistema podrá crear publicaciones.

---

## RF-035 Aprobar Publicación

Solo usuarios autorizados.

---

## RF-036 Programar Publicación

El usuario podrá programar fecha y hora.

---

## RF-037 Publicar Telegram

---

## RF-038 Publicar Facebook

---

## RF-039 Publicar X

---

## RF-040 Registrar Resultado

El sistema deberá registrar:

- fecha
- canal
- resultado

---

# MÓDULO 8

# Panel de Inteligencia

---

## RF-041 Dashboard General

Mostrar:

- Noticias capturadas
- Eventos abiertos
- Publicaciones pendientes

---

## RF-042 Ranking de Eventos

Ordenado por importancia.

---

## RF-043 Tendencias

Mostrar tendencias detectadas.

---

## RF-044 Estadísticas de Fuentes

Mostrar actividad por fuente.

---

# MÓDULO 9

# Gestión de Usuarios

---

## RF-045 Crear Usuario

---

## RF-046 Editar Usuario

---

## RF-047 Desactivar Usuario

---

## RF-048 Asignar Roles

Roles:

- ADMIN
- EDITOR

---

## RF-049 Iniciar Sesión

---

## RF-050 Cerrar Sesión

---

# 4. Requisitos No Funcionales

---

## RNF-001 Rendimiento

La consulta de eventos deberá responder en menos de 3 segundos.

---

## RNF-002 Disponibilidad

Disponibilidad mínima:

```
99%
```

---

## RNF-003 Seguridad

Autenticación JWT.

---

## RNF-004 Auditoría

Todas las acciones críticas deberán registrarse.

---

## RNF-005 Escalabilidad

La arquitectura deberá permitir crecimiento modular.

---

## RNF-006 Mantenibilidad

Backend basado en Clean Architecture.

---

## RNF-007 Portabilidad

Ejecución mediante Docker.

---

## RNF-008 Copias de Seguridad

Backups diarios automáticos.

---

# 5. Reglas de Negocio Iniciales

## RN-001

Toda noticia deberá pertenecer a una fuente.

---

## RN-002

Toda noticia deberá tener URL única.

---

## RN-003

Toda publicación deberá estar asociada a un evento.

---

## RN-004

No podrán publicarse contenidos sin aprobación humana.

---

## RN-005

Un evento podrá agrupar múltiples noticias.

---

## RN-006

Una noticia solo podrá pertenecer a un evento activo.

---

## RN-007

Todo contenido generado deberá conservar trazabilidad de su origen.

---

# 6. Criterios de Aceptación del MVP

El MVP será aceptado cuando permita:

### CA-001

Capturar noticias automáticamente.

---

### CA-002

Clasificarlas mediante IA.

---

### CA-003

Agruparlas en eventos.

---

### CA-004

Generar contenido para Telegram.

---

### CA-005

Generar contenido para Facebook.

---

### CA-006

Generar contenido para X.

---

### CA-007

Permitir revisión humana.

---

### CA-008

Publicar contenido aprobado.

---

### CA-009

Consultar eventos desde el panel web.

---

### CA-010

Mantener trazabilidad completa.