## Plataforma de Inteligencia Informativa para Sindicato de Docentes de Andalucía

Versión 1.0

Estado: Diseño Base

---

# 1. Objetivo

Definir:

- Modelos IA
- Casos de uso IA
- Prompts
- Respuestas JSON
- Validaciones
- Estrategia de costes
- Estrategia de calidad

---

# 2. Principios Generales

---

## IA-001

La IA nunca es fuente de información.

La fuente siempre son las noticias.

---

## IA-002

La IA interpreta.

No inventa.

---

## IA-003

La IA debe responder siempre en JSON cuando el resultado sea procesado por el sistema.

---

## IA-004

La IA debe indicar incertidumbre.

---

## IA-005

La IA debe priorizar fuentes oficiales.

---

# 3. Casos de Uso IA

---

## IA-01

Clasificación de noticias

---

## IA-02

Agrupación en eventos

---

## IA-03

Generación de resúmenes

---

## IA-04

Extracción de entidades

---

## IA-05

Generación de contenido

---

## IA-06

Detección de tendencias

(Fase futura)

---

# 4. Arquitectura IA

Crear abstracción:

```
AIProvider
```

---

Implementaciones:

```
OpenAIProvider

ClaudeProvider

OllamaProvider
```

---

# Recomendación MVP

## Opción Cloud

OpenAI

Modelo:

```
GPT-5.5
```

---

## Opción Local

Ollama

Modelo:

```
Qwen3 30B
```

o

```
Llama 4
```

---

# Estrategia Recomendada

MVP:

```
GPT-5.5
```

---

Producción futura:

```
Clasificación → 

LocalContenido → GPT
```

---

# 5. IA Clasificación de Noticias

---

## Objetivo

Convertir noticia en datos estructurados.

---

## Entrada

```
{
  "title": "",
  "summary": "",
  "source": ""
}
```

---

## Categorías Iniciales

```
OPOSICIONES

SIPRI

INTERINOS

FUNCIONARIOS

RETRIBUCIONES

FORMACION

NORMATIVA

INSPECCION

UNIVERSIDADES

FP

INFRAESTRUCTURAS

DIGITALIZACION

OTROS
```

---

# Prompt Clasificación

```
Eres un analista especializado en educación pública andaluza.

Analiza la noticia.

Clasifica únicamente usando las categorías proporcionadas.

Devuelve exclusivamente JSON válido.
```

---

## Respuesta

```
{
  "category":"SIPRI",
  "subcategory":"Vacantes",
  "relevance":92,
  "urgency":75,
  "impact":88,
  "keywords":[
    "SIPRI",
    "vacantes",
    "interinos"
  ]
}
```

---

# 6. IA Detección de Eventos

---

## Objetivo

Determinar si una noticia pertenece a un evento existente.

---

# Entrada

Evento:

```
{
  "event_title":"",
  "event_summary":""
}
```

---

Nueva noticia:

```
{
  "title":"",
  "summary":""
}
```

---

# Prompt

```
Compara la noticia con el evento.

Determina si describen el mismo hecho.

No compares redacción.
Compara significado.

Devuelve únicamente JSON.
```

---

# Respuesta

```
{
  "match": true,
  "confidence": 91,
  "reason": "La noticia describe el mismo proceso de adjudicación SIPRI."
}
```

---

# Regla

Sólo asociar automáticamente:

```
confidence >= 85
```

---

# 7. IA Generación de Resúmenes

---

## Objetivo

Crear resumen consolidado del evento.

---

## Entrada

Todas las noticias asociadas.

---

# Prompt

```
Actúa como analista especializado en educación pública andaluza.

Dispones de múltiples noticias que describen el mismo evento.

Identifica:

Hechos confirmados.

Aspectos relevantes para docentes.

Posibles implicaciones.

No inventes información.

No añadas opiniones.
```

---

# Respuesta

```
{
  "executive_summary":"",
  "union_summary":"",
  "key_points":[],
  "risks":[],
  "opportunities":[]
}
```

---

# 8. IA Extracción de Entidades

---

## Objetivo

Identificar actores relevantes.

---

# Entidades

```
Consejería

Ministerio

BOJA

BOE

ANPE

CSIF

UGT

CCOO

USTEA

Sindicatos

Universidades

Ayuntamientos
```

---

# Respuesta

```
{
  "organizations":[],
  "locations":[],
  "people":[]
}
```

---

# 9. IA Generación Telegram

---

## Objetivo

Crear mensaje listo para publicar.

---

# Perfil

Informativo neutro.

---

# Prompt

```
Genera una publicación para Telegram.

Tono:
Informativo
Neutro
Profesional

Máximo 1200 caracteres.

Incluye:

Título breve.

Resumen.

Aspectos relevantes para docentes.

No inventes información.

No uses lenguaje partidista.
```

---

# Respuesta

```
{
  "title":"",
  "content":""
}
```

---

# 10. IA Generación Facebook

---

# Prompt

```
Genera una publicación para Facebook.

Tono:
Informativo
Institucional

Longitud:
300-500 palabras

Incluye llamada a la lectura.

No inventes información.
```

---

# Respuesta

```
{
  "content":""
}
```

---

# 11. IA Generación X

---

# Prompt

```
Genera publicación para X.

Máximo 280 caracteres.

Información clara.

Sin hashtags excesivos.

Sin emojis innecesarios.
```

---

# Respuesta

```
{
  "content":""
}
```

---

# 12. Perfiles Editoriales

Esta parte será clave para diferenciar el sistema.

---

## Perfil Informativo

Por defecto.

```
Neutro

Objetivo

Preciso
```

---

## Perfil Institucional

```
Formal

Corporativo

Representativo
```

---

## Perfil Divulgativo

```
Más sencillo

Más cercano

Más explicativo
```

---

## Perfil Sindical

(No MVP)

```
Mayor análisis

Mayor posicionamiento
```

---

# 13. Control de Alucinaciones

---

## Regla IA-006

No generar datos no presentes.

---

## Regla IA-007

No generar cifras inventadas.

---

## Regla IA-008

No atribuir declaraciones no verificadas.

---

## Regla IA-009

No inferir consecuencias futuras como hechos.

---

# 14. Sistema de Validación

Antes de guardar respuesta IA:

---

Verificar:

```
JSON válido
```

---

Verificar:

```
Campos obligatorios
```

---

Verificar:

```
Longitud máxima
```

---

Si falla:

```
Reintento
```

---

# 15. Estrategia de Costes IA

---

## Clasificación

Lotes:

```
20 noticias
```

---

## Detección eventos

Individual.

---

## Resúmenes

Por evento.

---

## Contenido

Bajo demanda.

---

# 16. Roadmap IA

## MVP

```
Clasificación

Eventos

Resúmenes

Telegram

Facebook

X
```

---

## Fase 2

```
Etiquetado automático

Tendencias

Alertas

Predicciones
```

---

# Decisiones Estratégicas IA

### DIA-001

La IA nunca sustituye las fuentes.

### DIA-002

Todo procesamiento estructurado devuelve JSON.

### DIA-003

El evento es la unidad principal de análisis.

### DIA-004

La publicación se genera desde eventos, no desde noticias.

### DIA-005

El tono por defecto será neutro e informativo.

### DIA-006

La supervisión humana seguirá siendo obligatoria.