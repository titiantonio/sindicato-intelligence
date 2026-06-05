## Plataforma de Inteligencia Informativa para Sindicato de Docentes de Andalucía

Versión 1.0

Estado: Diseño Base

---

# 1. Objetivo

Definir:

- Todos los workflows.
- Responsabilidades.
- Entradas.
- Salidas.
- Dependencias.
- Gestión de errores.
- Integración con IA.
- Integración con Spring Boot.

---

# 2. Principio Arquitectónico

## PA-001

n8n NO contiene reglas de negocio.

---

Incorrecto:

```
n8n
 ↓
Decide lógica
 ↓
Actualiza BBDD
```

---

Correcto:

```
n8n
 ↓
Orquesta
 ↓
Spring Boot
 ↓
Reglas de negocio
```

---

# 3. Mapa General de Workflows

```
WF-01 Captura Noticias

WF-02 Clasificación IA

WF-03 Detección Eventos

WF-04 Generación Resúmenes

WF-05 Generación Contenido

WF-06 Revisión Editorial 

WF-07 Publicación 

WF-08 Monitorización 

WF-09 Limpieza 
```

---

# 4. MVP

Implementaremos inicialmente:

```
WF-01

WF-02

WF-03

WF-04

WF-05
```

---

# 5. Workflow 01

# Captura de Noticias

---

## Objetivo

Capturar noticias.

---

## Trigger

```
Cada 30 minutos
```

---

## Entrada

```
sources
```

---

## Fuentes

### Google News

### BOJA

### BOE

### Consejería Educación

### Sindicatos

### Medios especializados

---

## Flujo

```
Cron
 ↓
Obtener Fuentes
 ↓
HTTP Request
 ↓
XML Parser
 ↓
Normalización
 ↓
Eliminar duplicados
 ↓
Guardar noticia
```

---

## Salida

```
news_articles
```

---

## Estado inicial

```
CAPTURED
```

---

# 6. Workflow 02

# Clasificación IA

---

## Objetivo

Clasificar noticias.

---

## Trigger

```
processing_status='CAPTURED'
```

---

## Entrada

```
news_articles
```

---

## Flujo

```
Leer noticias pendientes
 ↓
Agrupar lote
 ↓
Enviar IA
 ↓
Clasificar
 ↓
Guardar clasificación
 ↓
Actualizar estado
```

---

## Resultado

```
CLASSIFIED
```

---

## Salida

```
news_classifications
```

---

# Prompt IA

Generar:

```
CategoríaSubcateCategoría

Subcategoría

Relevancia

Impacto

Urgencia

Keywords

OrganizacionesgoríaRelevanciaImpactoUrgenciaKeywordsOrganizaciones
```

---

# 7. Workflow 03

# Detección de Eventos

---

## Objetivo

Agrupar noticias.

---

## Trigger

```
processing_status='CLASSIFIED'
```

---

## Entrada

```
Noticias clasificadas
```

---

## Flujo

```
Leer noticia
 ↓
Buscar eventos similares
 ↓
IA compara
 ↓
¿Existe evento?
```

---

### SI

```
Asociar noticia
```

---

### NO

```
Crear evento
```

---

## Resultado

```
EVENT_MATCHED
```

---

# Prompt IA

Responder:

```
{
  "match": true,
  "eventId": 123,
  "confidence": 91
}
```

---

# 8. Workflow 04

# Generación de Resúmenes

---

## Objetivo

Generar análisis consolidado.

---

## Trigger

Nuevo evento.

o

Evento actualizado.

---

## Entrada

```
Todas las noticias del evento
```

---

## Flujo

```
Leer evento
 ↓
Leer noticias asociadas
 ↓
Enviar IA
 ↓
Generar resumen
 ↓
Guardar análisis
```

---

## Salida

```
event_ai_analysis
```

---

## Generar

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

# 9. Workflow 05

# Generación de Contenido

---

## Objetivo

Crear contenido editorial.

---

## Trigger

Solicitud usuario.

---

## Entrada

```
Evento
```

---

## Flujo

```
Leer evento
 ↓
Leer resumen
 ↓
Seleccionar canal
 ↓
Seleccionar perfil
 ↓
Generar IA
 ↓
Guardar contenido
```

---

## Salida

```
generated_content
```

---

# Generar

### Telegram

---

### Facebook

---

### X

---

# 10. Workflow 06

# Revisión Editorial

(Fase 2)

---

## Objetivo

Automatizar aprobaciones.

---

Estado:

```
NO MVP
```

---

# 11. Workflow 07

# Publicación

---

## Trigger

Publicación aprobada.

---

## Flujo

```
Contenido aprobado
 ↓
Canal seleccionado
 ↓
Enviar
 ↓
Guardar respuesta
```

---

## Canales

### Telegram

---

### Facebook

---

### X

---

# 12. Workflow 08

# Monitorización

---

## Objetivo

Control del sistema.

---

## Métricas

### Noticias capturadas

### Noticias procesadas

### Eventos creados

### Errores

---

## Alertas

Telegram Administrador.

---

# 13. Workflow 09

# Limpieza

---

## Frecuencia

Diaria.

---

## Acciones

### Eliminar temporales

### Compactar logs

### Archivar registros antiguos

---

# 14. Gestión de Errores

Todos los workflows:

---

## Error Captura

```
Fuente caída
```

↓

Registrar error.

↓

Continuar.

---

## Error IA

```
Timeout
```

↓

Reintento.

---

## Error Publicación

↓

Guardar error.

↓

Notificar administrador.

---

# 15. Reintentos

---

## Captura

```
3 intentos
```

---

## IA

```
2 intentos
```

---

## Publicación

```
3 intentos
```

---

# 16. Variables Globales n8n

---

## IA

```
OPENAI_API_KEY

OLLAMA_URL

AI_MODEL
```

---

## PostgreSQL

```
DB_HOST

DB_NAME

DB_USER

DB_PASSWORD
```

---

## Telegram

```
TELEGRAM_TOKEN

TELEGRAM_CHAT_ID
```

---

# 17. Arquitectura Final MVP

```
WF-01 Captura Noticias
           ↓
WF-02 Clasificación IA
           ↓
WF-03 Detección Eventos
           ↓
WF-04 Resumen Evento
           ↓
WF-05 Generación Contenido
           ↓
Revisión Humana
           ↓
WF-07 Publicación
```