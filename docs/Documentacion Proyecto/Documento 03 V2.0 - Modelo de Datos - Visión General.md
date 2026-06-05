Versión: 2.0

Estado: ACTUALIZADO

---

## Objetivo

Definir las entidades de negocio principales de la plataforma.

Este documento es conceptual.

La definición técnica oficial se encuentra en:

Documento 03D.

---

## Entidades Principales

### Source

Representa una fuente de información.

Ejemplos:

- BOJA
- Consejería de Educación
- ANPE
- CSIF
- Europa Press

---

### News

Representa una noticia capturada.

Características:

- Procede de una única fuente.
- Tiene URL única.
- Es clasificada mediante IA.
- Puede asociarse a uno o varios eventos.

---

### Classification

Representa el análisis IA de una noticia.

Incluye:

- Categoría
- Subcategoría
- Relevancia
- Impacto
- Urgencia
- Entidades detectadas

---

### Event

Representa un hecho relevante.

Es la entidad principal del sistema.

Ejemplos:

- Oposiciones Docentes 2027
- Convocatoria SIPRI
- Incremento Salarial Docente

---

### Analysis

Representa el conocimiento consolidado generado por IA a partir de un evento.

Incluye:

- Resumen ejecutivo
- Resumen sindical
- Riesgos
- Oportunidades

---

### Content

Representa contenido editorial generado desde un evento.

Ejemplos:

- Mensaje Telegram
- Comunicado interno
- Nota informativa

---

### Publication

Representa una publicación enviada a un canal.

---

### User

Representa un usuario interno.

Roles MVP:

- ADMIN
- EDITOR

---

## Relación Principal

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