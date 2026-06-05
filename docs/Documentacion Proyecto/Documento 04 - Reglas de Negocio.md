## Plataforma de Inteligencia Informativa para Sindicato de Docentes de Andalucía

Versión 1.0

Estado: Aprobado para MVP

---

# 1. Introducción

## Objetivo

Definir las reglas que gobiernan el comportamiento funcional del sistema.

Estas reglas serán aplicadas por:

- Backend Spring Boot
- Workflows n8n
- IA
- Frontend Angular

---

# 2. Reglas Generales

---

## RN-001

Toda noticia deberá pertenecer a una fuente válida.

### Ejemplo

Correcto:

```
Noticia → BOJA
```

Incorrecto:

```
Noticia sin fuente
```

---

## RN-002

Una fuente desactivada no podrá generar nuevas capturas.

---

## RN-003

La eliminación física de noticias estará prohibida.

Se utilizará archivado lógico.

---

## RN-004

Todas las fechas se almacenarán en UTC.

---

## RN-005

Toda modificación crítica deberá quedar registrada.

---

# 3. Reglas de Captura

---

## RN-006

La URL de una noticia debe ser única.

---

## RN-007

Si una URL ya existe:

```
NO insertar
```

---

## RN-008

Si una noticia cambia de contenido pero mantiene URL:

```
Actualizar contenido
Registrar fecha actualización
```

---

## RN-009

Toda noticia nueva se crea con:

```
processing_status = CAPTURED
```

---

## RN-010

La captura nunca deberá publicar contenido.

---

# 4. Reglas de Clasificación

---

## RN-011

Toda noticia capturada deberá ser clasificada.

---

## RN-012

Una noticia no podrá clasificarse dos veces simultáneamente.

---

## RN-013

La clasificación IA deberá generar al menos:

- categoría
- relevancia
- palabras clave

---

## RN-014

Toda clasificación deberá almacenar:

```
confidence_score
```

---

## RN-015

Si confidence_score < 60%

La noticia quedará marcada para revisión.

---

# 5. Reglas de Eventos

---

# Regla Fundamental

## RN-016

Un Evento representa un hecho.

No una noticia.

---

### Ejemplo

Incorrecto:

```
Evento BOJA
Evento ANPE
Evento CSIF
```

---

Correcto:

```
Evento:
Adjudicación SIPRI mayo 2026
```

---

## RN-017

Un evento debe contener al menos una noticia.

---

## RN-018

Una noticia sólo podrá pertenecer a un evento activo.

---

## RN-019

Un evento podrá contener noticias de múltiples fuentes.

---

## RN-020

La IA deberá intentar asociar una noticia a un evento existente antes de crear uno nuevo.

---

## RN-021

Sólo se creará un nuevo evento cuando no exista coincidencia suficiente.

---

## RN-022

Toda asociación IA deberá registrar:

```
confidence_score
```

---

## RN-023

El administrador podrá:

- fusionar eventos
- separar noticias
- mover noticias

---

## RN-024

Los eventos cerrados no admitirán nuevas noticias.

---

# 6. Reglas de Resumen IA

---

## RN-025

Todo resumen deberá generarse desde el evento.

Nunca desde una noticia individual.

---

## RN-026

Resumen Ejecutivo:

Máximo:

```
150 palabras
```

---

## RN-027

Resumen Sindical:

Máximo:

```
300 palabras
```

---

## RN-028

Toda generación IA deberá almacenar:

- modelo
- fecha
- resultado

---

# 7. Reglas de Generación de Contenido

---

## RN-029

Todo contenido deberá derivar de un evento.

---

## RN-030

No podrá existir contenido sin evento asociado.

---

## RN-031

Todo contenido tendrá:

```
Canal
Perfil editorial
Versión
```

---

## RN-032

Toda regeneración generará una nueva versión.

Nunca sobrescribirá la anterior.

---

## RN-033

El contenido generado inicialmente utilizará:

```
Perfil Informativo
```

---

## RN-034

El contenido generado por IA deberá incluir referencia a la fuente principal cuando sea posible.

---

# 8. Reglas de Publicación

---

## RN-035

La IA no podrá publicar directamente.

---

## RN-036

Toda publicación deberá ser aprobada por un usuario.

---

## RN-037

Sólo podrán aprobar:

```
ADMIN
EDITOR
```

---

## RN-038

Un contenido rechazado no podrá publicarse.

---

## RN-039

Una publicación programada podrá cancelarse antes de ejecutarse.

---

## RN-040

Toda publicación deberá registrar:

- fecha
- canal
- resultado

---

# 9. Reglas de Usuarios

---

## RN-041

El email deberá ser único.

---

## RN-042

Las contraseñas nunca se almacenarán en texto plano.

---

## RN-043

Los usuarios inactivos no podrán iniciar sesión.

---

## RN-044

Sólo los administradores podrán gestionar usuarios.

---

# 10. Reglas de Auditoría

---

## RN-045

Toda aprobación deberá quedar registrada.

---

## RN-046

Toda publicación deberá quedar registrada.

---

## RN-047

Toda modificación de evento deberá quedar registrada.

---

## RN-048

Toda ejecución IA deberá ser trazable.

---

# 11. Reglas IA Específicas del Sindicato

Estas reglas son muy importantes para evitar problemas futuros.

---

## RN-049

La IA deberá mantener neutralidad informativa por defecto.

---

## RN-050

La IA no deberá inventar información.

---

## RN-051

La IA no deberá generar cifras no presentes en las fuentes.

---

## RN-052

La IA deberá indicar incertidumbre cuando exista.

---

## RN-053

La IA no podrá atribuir declaraciones a personas u organismos sin evidencia.

---

## RN-054

La IA deberá priorizar fuentes oficiales frente a medios de comunicación.

---

### Prioridad de Fuentes

Nivel 1

```
BOJA
BOE
Consejería Educación
Ministerio Educación
```

---

Nivel 2

```
Universidades
Organismos públicos
```

---

Nivel 3

```
Sindicatos
```

---

Nivel 4

```
Prensa generalista
```

---

# 12. Reglas de Detección de Duplicados

---

## RN-055

Dos noticias con la misma URL son duplicadas.

---

## RN-056

Dos noticias con distinto URL podrán pertenecer al mismo evento.

---

## RN-057

La similitud semántica superior al umbral definido permitirá agrupar noticias.

---

### MVP

```
85%
```

---

# 13. Reglas de Calidad

---

## RN-058

Ningún evento podrá publicarse sin resumen.

---

## RN-059

Ningún contenido podrá publicarse sin revisión.

---

## RN-060

Toda noticia deberá conservar trazabilidad hacia su fuente original.

---

# 14. Reglas Futuras (No MVP)

Reservadas para:

- Tendencias
- Alertas
- Predicciones
- Recomendaciones estratégicas
- Embeddings
- Análisis histórico

---

# Decisiones de Negocio Aprobadas

### DNB-001

La entidad principal del sistema es el Evento.

---

### DNB-002

La publicación siempre se realiza desde Eventos.

---

### DNB-003

La supervisión humana es obligatoria.

---

### DNB-004

La IA actúa como asistente editorial.

---

### DNB-005

La información oficial tiene prioridad sobre la información periodística.