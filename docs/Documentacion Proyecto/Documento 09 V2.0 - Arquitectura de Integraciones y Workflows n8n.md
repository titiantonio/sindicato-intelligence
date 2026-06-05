## Plataforma de Inteligencia Informativa para Sindicato de Docentes de Andalucía

Versión 2.0

Estado: Arquitectura Oficial MVP

---

# 1. Objetivo

Definir:

- Arquitectura de automatización.
    
- Workflows oficiales.
    
- Responsabilidades.
    
- Entradas y salidas.
    
- Integración IA.
    
- Integración PostgreSQL.
    
- Integración Spring Boot.
    
- Gestión de errores.
    
- Escalabilidad futura.
    

---

# 2. Principios Arquitectónicos

## PA-001

n8n es un orquestador.

No contiene lógica de negocio compleja.

---

## PA-002

La lógica de negocio pertenece al Backend.

```text
n8n
 ↓
Spring Boot
 ↓
Reglas de negocio
```

---

## PA-003

El evento es la entidad principal del sistema.

```text
Noticias
 ↓
Eventos
 ↓
Análisis
 ↓
Contenido
 ↓
Publicación
```

---

## PA-004

Los workflows deben ser independientes.

Cada workflow debe poder ejecutarse de forma aislada.

---

## PA-005

Todo workflow debe ser reiniciable.

No debe perder datos ante errores.

---

# 3. Arquitectura General

```text
RSS / APIs / Web Sources
            │
            ▼

      WF-01 Captura
            │
            ▼

     news_articles
            │
            ▼

   WF-02 Clasificación
            │
            ▼

 news_classifications
            │
            ▼

   WF-03 Eventos
            │
            ▼

        events
            │
            ▼

 WF-04 Análisis IA
            │
            ▼

 event_ai_analysis
            │
            ▼

 WF-05 Contenido
            │
            ▼

 generated_content
            │
            ▼

 Revisión Humana
            │
            ▼

 WF-06 Publicación
            │
            ▼

      Telegram
```

---

# 4. Workflows MVP

## WF-01 Captura Noticias

Responsable de capturar noticias.

---

## WF-02 Clasificación IA

Responsable de clasificar noticias.

---

## WF-03 Detección de Eventos

Responsable de agrupar noticias.

---

## WF-04 Generación de Análisis

Responsable de generar conocimiento.

---

## WF-05 Generación de Contenido

Responsable de generar contenido editorial.

---

## WF-06 Publicación Telegram

Responsable de publicar contenido aprobado.

---

# 5. Workflows Post-MVP

No se implementan inicialmente.

```text
WF-07 Publicación Facebook

WF-08 Publicación X

WF-09 Monitorización avanzada

WF-10 Limpieza avanzada

WF-11 Tendencias

WF-12 Alertas inteligentes
```

---

# 6. Workflow WF-01

# Captura de Noticias

---

## Objetivo

Capturar noticias relevantes del ámbito educativo andaluz.

---

## Frecuencia

```text
Cada 30 minutos
```

---

## Entrada

```text
sources
```

---

## Fuentes Iniciales

### BOJA

### BOE

### Consejería de Desarrollo Educativo

### Consejería de Universidad

### Google News

### Sindicatos educativos

### Medios especializados

---

## Flujo

```text
Cron
 ↓
Leer Fuentes
 ↓
HTTP Request
 ↓
RSS/XML Parser
 ↓
Normalización
 ↓
Hash Noticias
 ↓
Detección Duplicados
 ↓
Guardar
```

---

## Tabla Destino

```text
news_articles
```

---

## Estado

```text
CAPTURED
```

---

# 7. Workflow WF-02

# Clasificación IA

---

## Objetivo

Clasificar noticias automáticamente.

---

## Entrada

```text
news_articles
```

---

## Filtro

```sql
processing_status='CAPTURED'
```

---

## Flujo

```text
Leer Noticias
 ↓
Agrupar por lotes
 ↓
Enviar a IA
 ↓
Clasificar
 ↓
Guardar clasificación
 ↓
Actualizar estado
```

---

## Resultado

```text
CLASSIFIED
```

---

## Tabla Destino

```text
news_classifications
```

---

## Datos Generados

```text
Categoría

Subcategoría

Relevancia

Impacto

Urgencia

Keywords

Entidades
```

---

# 8. Workflow WF-03

# Detección de Eventos

---

## Objetivo

Agrupar noticias sobre un mismo hecho.

---

## Entrada

```text
Noticias clasificadas
```

---

## Flujo

```text
Leer noticia
 ↓
Buscar eventos similares
 ↓
Comparación IA
 ↓
¿Existe evento?
```

---

### Sí

```text
Asociar noticia al evento
```

---

### No

```text
Crear nuevo evento
```

---

## Tablas Afectadas

```text
events

event_news
```

---

## Resultado

```text
EVENT_MATCHED
```

---

## Ejemplo

```text
5 noticias sobre:

Oposiciones Andalucía 2027

↓

1 único evento
```

---

# 9. Workflow WF-04

# Generación de Análisis IA

---

## Objetivo

Generar conocimiento consolidado sobre un evento.

---

## Trigger

Nuevo evento.

o

Evento actualizado.

---

## Entrada

```text
Todas las noticias asociadas
```

---

## Flujo

```text
Leer evento
 ↓
Leer noticias
 ↓
Generar contexto
 ↓
Enviar IA
 ↓
Guardar análisis
```

---

## Tabla Destino

```text
event_ai_analysis
```

---

## Información Generada

### Resumen Ejecutivo

### Resumen Sindical

### Puntos Clave

### Riesgos

### Oportunidades

---

# 10. Workflow WF-05

# Generación de Contenido

---

## Objetivo

Generar contenido listo para revisión.

---

## Trigger

Solicitud del editor.

---

## Entrada

```text
Event

EventAnalysis
```

---

## Flujo

```text
Leer Evento
 ↓
Leer Análisis
 ↓
Seleccionar Canal
 ↓
Seleccionar Tono
 ↓
Generar IA
 ↓
Guardar
```

---

## Tabla Destino

```text
generated_content
```

---

## Tonos MVP

### Informativo (por defecto)

### Institucional

### Divulgativo

---

## Canal MVP

```text
Telegram
```

---

# 11. Workflow WF-06

# Publicación Telegram

---

## Objetivo

Publicar contenido aprobado.

---

## Trigger

Contenido aprobado.

---

## Flujo

```text
Leer contenido
 ↓
Validar estado
 ↓
Enviar Telegram
 ↓
Guardar respuesta
 ↓
Actualizar publicación
```

---

## Tabla Destino

```text
publications
```

---

## Estados

```text
PENDING

PUBLISHED

FAILED
```

---

# 12. Gestión de Errores

Todos los workflows deben implementar:

---

## Captura

```text
3 reintentos
```

---

## IA

```text
2 reintentos
```

---

## Telegram

```text
3 reintentos
```

---

## Registro

Todos los errores deben registrarse.

---

# 13. Alertas Operativas

Canal:

```text
Telegram Administrador
```

---

Alertas:

### Error IA

### Error Captura

### Error Publicación

### Workflow detenido

---

# 14. Variables Globales

## IA

```text
AI_PROVIDER

AI_MODEL

OPENAI_API_KEY

OLLAMA_URL
```

---

## PostgreSQL

```text
DB_HOST

DB_PORT

DB_NAME

DB_USER

DB_PASSWORD
```

---

## Telegram

```text
TELEGRAM_BOT_TOKEN

TELEGRAM_CHANNEL_ID

ADMIN_CHAT_ID
```

---

# 15. Estrategia de Evolución

## MVP

```text
Captura

Clasificación

Eventos

Análisis

Contenido

Telegram
```

---

## v1.1

```text
Logs avanzados

Monitorización

Versionado contenido
```

---

## v1.2

```text
Facebook

X

Programación publicaciones
```

---

## v2.0

```text
Tendencias

Métricas

Alertas inteligentes

Analítica avanzada
```

---

# 16. Arquitectura Final MVP

```text
WF-01 Captura
          ↓

WF-02 Clasificación
          ↓

WF-03 Eventos
          ↓

WF-04 Análisis
          ↓

WF-05 Contenido
          ↓

Revisión Humana
          ↓

WF-06 Publicación Telegram
```

---

# Decisiones Arquitectónicas

### N8N-001

n8n actúa como orquestador.

### N8N-002

Event es la entidad principal.

### N8N-003

Todo contenido se genera desde eventos.

### N8N-004

Telegram es el único canal MVP.

### N8N-005

Los workflows deben ser independientes.

### N8N-006

La lógica de negocio residirá progresivamente en Spring Boot.

### N8N-007

La arquitectura está preparada para escalar a múltiples canales sin rediseño.