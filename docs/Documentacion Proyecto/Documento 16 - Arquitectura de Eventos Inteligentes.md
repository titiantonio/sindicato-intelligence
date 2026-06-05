Versión 1.0

Estado: Crítico

---

# 1. Definición de Evento

## Regla EV-001

Un Evento representa:

```
Un hecho
Una decisión
Una convocatoria
Una medida
Un proceso
```

---

NO representa:

```
Una noticia
Una publicación
Un artículo
```

---

# Ejemplo

---

Evento:

```
Convocatoria Oposiciones Docentes Andalucía 2027
```

---

Noticias asociadas:

```
BOJA

Consejería

ANPE

CSIF

UGT

Prensa
```

---

# 2. Jerarquía Conceptual

```
FUENTE
 ↓
NOTICIA
 ↓
EVENTO
 ↓
RESUMEN
 ↓
CONTENIDO
 ↓
PUBLICACIÓN
```

---

# 3. Ciclo de Vida

## Estado 1

```
OPEN
```

Evento activo.

---

## Estado 2

```
MONITORING
```

Sigue generando noticias.

---

## Estado 3

```
CLOSED
```

No admite nuevas noticias.

---

## Estado 4

```
ARCHIVED
```

Histórico.

---

# 4. Tipología de Eventos

---

## Oposiciones

```
OPPOSITIONS
```

---

## SIPRI

```
SIPRI
```

---

## Retribuciones

```
SALARIES
```

---

## Normativa

```
REGULATION
```

---

## Formación

```
TRAINING
```

---

## Infraestructuras

```
INFRASTRUCTURE
```

---

## Universidades

```
UNIVERSITY
```

---

## FP

```
VOCATIONAL_TRAINING
```

---

# 5. Modelo de Detección

La IA nunca decidirá directamente.

Trabajaremos en varias capas.

---

# Capa 1

## Coincidencia de Entidades

Extraemos:

```
Organismos

Personas

Programas

Normativas

Localizaciones
```

---

Ejemplo:

```
Consejería Educación

SIPRI

Andalucía
```

---

# Capa 2

## Coincidencia Temporal

Noticias cercanas:

```
7 días
```

---

Mayor probabilidad.

---

# Capa 3

## Coincidencia Semántica

Comparación IA.

---

Pregunta:

```
¿Hablan del mismo hecho?
```

---

# Capa 4

## Validación

Generar score final.

---

# 6. Algoritmo Inicial MVP

---

## Score Entidades

```
40%
```

---

## Score Keywords

```
20%
```

---

## Score Temporal

```
10%
```

---

## Score IA

```
30%
```

---

# Resultado

```
Score Total
```

---

# Regla EV-002

---

Si:

```
Score >= 85
```

↓

Asociar automáticamente.

---

Si:

```
70-84
```

↓

Pendiente revisión.

---

Si:

```
<70
```

↓

Crear nuevo evento.

---

# 7. Evento Maestro

Cada evento tendrá:

---

## Título Canónico

Ejemplo:

```
Convocatoria Oposiciones Docentes Andalucía 2027
```

---

## Descripción Maestra

Generada IA.

---

## Categoría Principal

---

## Fecha Inicio

---

## Fecha Última Actualización

---

## Nivel de Importancia

---

# 8. Cálculo de Importancia

---

## Fuente Oficial

+25

---

## BOJA

+40

---

## BOE

+40

---

## Consejería

+35

---

## Sindicato

+20

---

## Más de 5 noticias

+15

---

## Más de 10 noticias

+30

---

Resultado:

```
LOW

MEDIUM

HIGH

CRITICAL
```

---

# 9. Resumen Consolidado

El resumen NO se genera desde una noticia.

---

Se genera desde:

```
Todas las noticias
```

---

Resultado:

### Qué ha ocurrido

---

### Quién está implicado

---

### Impacto para docentes

---

### Aspectos pendientes

---

### Fuentes utilizadas

---

# 10. Detección de Evolución

Ejemplo:

---

Día 1

```
Convocatoria anunciada
```

---

Día 5

```
Publicación BOJA
```

---

Día 10

```
Alegaciones sindicatos
```

---

Todo sigue siendo:

```
1 Evento
```

---

# 11. Eventos Relacionados

Ejemplo:

```
Oposiciones 2027
```

Relacionado con:

```
Baremo

ribunales

Calendario
```

---

Tabla:

```
event_relationships
```

---

# 12. Dashboard Estratégico

Esta será una de las pantallas más valiosas.

---

Mostrar:

```
Eventos críticos

Eventos emergentes

Eventos cerrados

Eventos con mayor impacto
```

---

# 13. IA Especializada para Eventos

Prompt principal:

```
Analiza la noticia y determina si pertenece a alguno de los eventos existentes.

No compares palabras.

Compara hechos.

Evalúa si describen el mismo asunto administrativo, político o educativo.

Devuelve JSON.
```

---

Respuesta:

```
{
  "match": true,
  "eventId": 142,
  "confidence": 92,
  "reason": "Ambas noticias tratan sobre la misma convocatoria de oposiciones docentes."
}
```

---

# 14. Ventaja Competitiva

La mayoría de herramientas hacen:

```
Noticias
↓
Publicación
```

---

Nuestra plataforma hará:

```
Noticias
↓
Evento
↓
Conocimiento
↓
Publicación
```