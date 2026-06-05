
# 1. Introducción

## 1.1 Propósito del Documento

Este documento define la visión, alcance, objetivos y arquitectura general del proyecto denominado:

**Plataforma de Inteligencia Informativa para el Sindicato de Docentes de Andalucía**

Su finalidad es servir como documento maestro de referencia durante todo el ciclo de vida del proyecto.

Toda la documentación posterior deberá alinearse con las decisiones recogidas en este documento.

---

## 1.2 Objetivos del Documento

Los objetivos principales son:

- Definir la visión del producto.
- Establecer el alcance inicial del MVP.
- Identificar usuarios y actores.
- Definir la arquitectura general.
- Establecer principios de diseño.
- Servir como base para requisitos funcionales y técnicos.

---

## 1.3 Alcance del Documento

Este documento cubre:

### Negocio

- Objetivos estratégicos.
- Beneficios esperados.
- Alcance funcional.

### Tecnología

- Arquitectura general.
- Componentes principales.
- Integraciones.

### Organización

- Roles implicados.
- Procesos editoriales.

No incluye todavía:

- Diseño detallado de base de datos.
- Casos de uso completos.
- Diagramas técnicos detallados.
- APIs específicas.

Estos elementos serán tratados en documentos posteriores.

---

## 1.4 Audiencia

Este documento está dirigido a:

### Dirección sindical

Para comprender los objetivos y beneficios.

### Responsables de comunicación

Para comprender los procesos editoriales.

### Equipo técnico

Para comprender la arquitectura y alcance.

### Futuros desarrolladores

Como referencia de diseño.

---

## 1.5 Glosario

### Noticia

Información obtenida desde una fuente externa.

Ejemplos:

- Artículo de prensa.
- Publicación BOJA.
- Publicación BOE.

---

### Evento Informativo

Conjunto de noticias que hablan del mismo acontecimiento.

Ejemplo:

Cinco medios publican información sobre una adjudicación SIPRI.

El sistema debe identificar que todas pertenecen al mismo evento.

---

### Fuente

Origen de una noticia.

Ejemplos:

- BOJA
- BOE
- Consejería de Educación
- Europa Press
- ANPE
- CSIF

---

### Contenido Generado

Texto producido mediante IA para un canal concreto.

Ejemplos:

- Telegram
- Facebook
- X

---

### Perfil Editorial

Conjunto de reglas de tono y estilo.

Ejemplos:

- Informativo
- Institucional
- Divulgativo
- Reivindicativo

---

# 2. Visión Estratégica

## 2.1 Situación Actual

El sindicato recibe diariamente una gran cantidad de información procedente de múltiples fuentes.

Estas fuentes generan:

- Noticias.
- Convocatorias.
- Normativa.
- Comunicados.
- Informes.

Actualmente gran parte del análisis y difusión se realiza manualmente.

---

## 2.2 Problemas Detectados

### Duplicidad

Un mismo acontecimiento aparece repetido en múltiples medios.

---

### Saturación Informativa

Los responsables de comunicación deben revisar grandes cantidades de información.

---

### Tiempo de Respuesta

La detección de temas relevantes puede retrasarse.

---

### Escalabilidad

El crecimiento de fuentes aumenta la carga de trabajo.

---

### Dependencia Humana

Gran parte del proceso depende de revisiones manuales.

---

## 2.3 Oportunidad

La Inteligencia Artificial y la automatización permiten transformar el flujo de información.

El objetivo no es automatizar publicaciones.

El objetivo es construir una capacidad permanente de inteligencia informativa.

---

## 2.4 Visión

La plataforma deberá convertirse en el centro de inteligencia informativa del sindicato.

Será capaz de:

- Detectar eventos relevantes.
- Agrupar información dispersa.
- Priorizar temas importantes.
- Generar contenido útil.
- Ayudar en la toma de decisiones.

---

## 2.5 Misión

Transformar grandes volúmenes de información educativa en conocimiento útil y accionable para el sindicato.

---

## 2.6 Objetivos de Negocio

### OBJ-01

Reducir el tiempo dedicado al análisis informativo.

---

### OBJ-02

Incrementar la calidad del contenido publicado.

---

### OBJ-03

Detectar tendencias relevantes de forma temprana.

---

### OBJ-04

Centralizar el conocimiento generado.

---

### OBJ-05

Incrementar el valor percibido del sindicato.

---

## 2.7 Objetivos Técnicos

### TEC-01

Automatización de procesos repetitivos.

### TEC-02

Arquitectura escalable.

### TEC-03

Alta trazabilidad.

### TEC-04

Coste operativo reducido.

### TEC-05

Mantenimiento sencillo.

---

## 2.8 Factores de Éxito

La plataforma será considerada exitosa cuando:

- Detecte eventos relevantes automáticamente.
- Reduzca publicaciones duplicadas.
- Mantenga supervisión humana.
- Genere contenido de calidad.
- Sea utilizada diariamente por el sindicato.

---

# 3. Alcance del Producto

## 3.1 Alcance MVP

La primera versión deberá incluir:

### Captura Automática

- RSS
- Google News
- BOJA
- BOE
- Fuentes sindicales

---

### Clasificación IA

- Relevancia
- Categoría
- Prioridad

---

### Agrupación de Noticias

Detección de noticias que hablan del mismo evento.

---

### Generación de Contenido

Generación de:

- Telegram
- Facebook
- X

---

### Revisión Humana

Aprobación obligatoria antes de publicar.

---

### Panel Web

Gestión de eventos y contenidos.

---

## 3.2 Fuera del Alcance MVP

No se incluirá inicialmente:

- Aplicación móvil nativa.
- Portal para afiliados.
- Publicación automática sin revisión.
- Multi-organización.
- Multi-idioma.

---

## 3.3 Evolución Prevista

### Versión 2

- Dashboard avanzado.
- Analítica.
- Más canales.

### Versión 3

- App móvil.
- Sistema de alertas.
- Inteligencia predictiva.

---

# 4. Stakeholders

## 4.1 Administrador

Responsable de:

- Configuración.
- Usuarios.
- Fuentes.
- Supervisión.

---

## 4.2 Editor de Contenidos

Responsable de:

- Revisar eventos.
- Aprobar publicaciones.
- Editar contenido.

---

## 4.3 Sindicato

Beneficiario principal del sistema.

Obtendrá:

- Mayor capacidad de análisis.
- Mejor comunicación.
- Mayor impacto.

---

## 4.4 Afiliados (Futuro)

Posibles consumidores de contenido generado.

No forman parte del MVP.

---

## 4.5 Ciudadanía y Comunidad Educativa

Beneficiarios indirectos mediante la difusión de información relevante.

---
# 5. Arquitectura Conceptual

## 5.1 Principios de Diseño

La plataforma se construirá siguiendo los siguientes principios.

### P01 - Event Centric Architecture

La entidad principal del sistema será el:

```
Evento Informativo
```

No la noticia.

Una noticia representa una fuente individual.

Un evento representa un acontecimiento.

Ejemplo:

```
Evento:
Nueva adjudicación SIPRI

Noticias:
- BOJA
- CSIF
- ANPE
- Europa Press
- Diario Sur
```

El sistema deberá agrupar automáticamente estas noticias bajo un único evento.

---

### P02 - Human In The Loop

La IA nunca publicará directamente.

La IA:

- Clasifica
- Resume
- Propone

El usuario:

- Revisa
- Corrige
- Aprueba

---

### P03 - Single Source of Truth

Toda la información persistente residirá en PostgreSQL.

Ningún workflow de n8n almacenará datos críticos fuera de la base de datos.

---

### P04 - IA Desacoplada

La arquitectura no dependerá de un proveedor concreto.

Deberá permitir utilizar:

- OpenAI
- Anthropic
- Gemini
- Ollama
- Modelos locales

sin modificar la lógica de negocio.

---

### P05 - Modularidad

Cada componente deberá tener responsabilidades claramente definidas.

---

# 5.2 Arquitectura Conceptual Global

```
┌─────────────────────┐
│ Fuentes Externas    │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ n8n                 │
│ Captura Información │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ PostgreSQL          │
│ Noticias Raw        │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ Motor IA            │
│ Clasificación       │
│ Agrupación          │
│ Resumen             │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ Eventos             │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ Contenido Generado  │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ API Backend         │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ Panel Web Angular   │
└─────────────────────┘
```

---

# 5.3 Dominios del Sistema

La plataforma se dividirá en 4 dominios principales.

## Dominio de Captura

Responsable de obtener información.

Incluye:

- RSS
- Google News
- BOJA
- BOE
- Sindicatos
- Consejería

---

## Dominio de Inteligencia

Responsable de transformar información en conocimiento.

Incluye:

- Clasificación
- Agrupación
- Scoring
- Resumen

---

## Dominio Editorial

Responsable de generar contenido.

Incluye:

- Telegram
- Facebook
- X

---

## Dominio de Gestión

Responsable de administración.

Incluye:

- Usuarios
- Roles
- Fuentes
- Configuración

---

# 5.4 Flujo Principal de Negocio

## Paso 1

Captura de noticia.

---

## Paso 2

Almacenamiento en bruto.

---

## Paso 3

Clasificación IA.

---

## Paso 4

Búsqueda de evento existente.

---

## Paso 5

Creación o asociación a evento.

---

## Paso 6

Generación de resumen del evento.

---

## Paso 7

Generación de contenido editorial.

---

## Paso 8

Revisión humana.

---

## Paso 9

Publicación.

---

# 5.5 Modelo Conceptual Inicial

## Fuente

Representa el origen de una noticia.

Ejemplos:

- BOJA
- BOE
- ANPE
- CSIF

---

## Noticia

Unidad mínima capturada.

Características:

- título
- url
- contenido
- fecha
- fuente

---

## Evento

Agrupación de noticias relacionadas.

Representa un acontecimiento.

Será el núcleo del sistema.

---

## Contenido Generado

Material producido por IA.

Ejemplos:

- Telegram
- Facebook
- X

---

## Publicación

Resultado final aprobado por un editor.

---

## Usuario

Persona que interactúa con el sistema.

---

## Perfil Editorial

Define:

- tono
- estilo
- longitud
- canal

---

# 6. Arquitectura Técnica

## 6.1 Infraestructura Física

Infraestructura MVP:

```
Servidor Proxmox
│
├── LXC Docker
│
├── PostgreSQL
├── n8n
├── Backend Spring Boot
├── Frontend Angular
└── Monitoring
```

---

## 6.2 Contenedores Docker

### PostgreSQL

Persistencia principal.

---

### n8n

Automatización.

---

### Spring Boot

API y lógica de negocio.

---

### Angular

Panel de administración.

---

### Monitoring

Pendiente definir.

Posibles opciones:

- Uptime Kuma
- Grafana
- Prometheus

---

# 6.3 PostgreSQL

Se utilizará como única base de datos.

Motivos:

- robustez
- rendimiento
- escalabilidad
- soporte JSON

---

## Responsabilidades

Almacenar:

- noticias
- eventos
- contenidos
- publicaciones
- auditoría

---

# 6.4 n8n

Responsabilidades exclusivas:

## Captura

- RSS
- APIs
- Scraping controlado

---

## IA

Invocación de modelos.

---

## Automatización

Procesamiento programado.

---

## Publicación

Envío a canales.

---

n8n NO contendrá lógica de negocio compleja.

La lógica de negocio residirá en Spring Boot.

---

# 6.5 Backend Spring Boot

Será el cerebro de la aplicación.

Responsabilidades:

## Gestión usuarios

## Seguridad

## Reglas de negocio

## Gestión eventos

## Gestión publicaciones

## Exposición APIs

---

## Arquitectura Interna

```
Controller
↓
Application
↓
Domain
↓
Infrastructure
```

Siguiendo Clean Architecture.

---

# 6.6 Frontend Angular

Aplicación SPA.

Características:

### Dashboard

### Gestión Eventos

### Gestión Noticias

### Gestión Contenidos

### Gestión Publicaciones

### Administración

---

Diseño responsive para uso desde móvil.

---

# 6.7 Arquitectura IA

Se implementará mediante un servicio desacoplado.

```
Spring Boot
      ↓
 AI Service
      ↓
Proveedor IA
```

---

Funciones IA:

### Clasificación

### Clustering

### Resumen

### Generación de contenido

### Scoring

---

# 6.8 Seguridad

Autenticación:

```
JWT
```

Roles:

```
ADMIN
EDITOR
```

---

# 6.9 Backups

Frecuencia mínima:

## PostgreSQL

Diario.

---

## Configuración n8n

Diario.

---

## Ficheros

Diario.

---

# 6.10 Monitorización

MVP:

- Uptime Kuma
- Logs Docker

Futuro:

- Grafana
- Prometheus
- Loki

---

# Decisiones Arquitectónicas Aprobadas

### DA-001

Especialización Andalucía.

### DA-002

Arquitectura centrada en eventos.

### DA-003

Human In The Loop obligatorio.

### DA-004

PostgreSQL como única base de datos.

### DA-005

Spring Boot como backend.

### DA-006

Angular como frontend.

### DA-007

n8n exclusivamente para automatización.

### DA-008

IA desacoplada del proveedor.

---
# 7. Modelo de Dominio

## 7.1 Introducción

El modelo de dominio representa los conceptos fundamentales del negocio.

Todo el sistema se construirá alrededor de estos conceptos.

La regla más importante es:

```
La entidad principal NO es la noticia.
La entidad principal es el Evento.
```

---

# 7.2 Entidades Principales

## Fuente

Representa el origen de una información.

Ejemplos:

- BOJA
- BOE
- Consejería de Educación
- ANPE
- CSIF
- Europa Press
- El País
- Diario Sur

### Atributos principales

```
id
nombre
tipo
url
activo
credibilidad
prioridad
```

---

## Noticia

Representa una unidad informativa obtenida desde una fuente.

### Ejemplo

```
Europa Press publica:

"La Junta anuncia nuevas vacantes SIPRI"
```

### Atributos

```
id
source_id
titulo
url
contenido
fecha_publicacion
estado
```

---

## Evento

Representa un acontecimiento real.

Es la agrupación semántica de múltiples noticias.

### Ejemplo

```
Evento:
Nueva adjudicación SIPRI mayo 2026

Noticias asociadas:
- BOJA
- Europa Press
- ANPE
- CSIF
```

### Atributos

```
id
titulo
descripcion
categoria
estado
fecha_deteccion
fecha_actualizacion
```

---

## Categoría

Clasificación temática.

Ejemplos:

```
SIPRI
Oposiciones
Plantillas
FP
Universidad
Normativa
Retribuciones
Inclusión
igitalización
```

---

## Contenido Generado

Texto creado mediante IA.

### Tipos

```
telegram
facebook
x
webnewsletter
```

---

## Publicación

Representa una publicación aprobada.

### Estados

```
BORRADOR
PENDIENTE
APROBADA
PROGRAMADA
PUBLICADA
ERROR
```

---

## Usuario

### Roles

```
ADMIN
EDITOR
```

---

## Perfil Editorial

Define cómo debe escribir la IA.

### Ejemplos

#### Informativo

Neutro y objetivo.

---

#### Institucional

Comunicación oficial.

---

#### Divulgativo

Explicativo.

---

#### Reivindicativo

Defensa de intereses docentes.

---

# 7.3 Relación entre Entidades

```
FUENTE
   │
   ▼
NOTICIA
   │
   ▼
EVENTO
   │
   ├────► CONTENIDO_GENERADO
   │
   └────► PUBLICACION
```

---

# 7.4 Ciclo de Vida de una Noticia

```
CAPTURADA
↓
CLASIFICADA
↓
AGRUPADA
↓
ASOCIADA_EVENTO
↓
ARCHIVADA
```

---

# 7.5 Ciclo de Vida de un Evento

```
DETECTADO
↓
ENRIQUECIDO
↓
RESUMIDO
↓
CONTENIDO_GENERADO
↓
PENDIENTE_REVISION
↓
PUBLICADO
↓
CERRADO
```

---

# 8. Arquitectura IA

## 8.1 Principios

La IA no sustituye al editor.

La IA:

- clasifica
- resume
- propone

El editor:

- valida
- corrige
- aprueba

---

# 8.2 Funciones de IA

## Clasificación

Determinar:

### Categoría

```
SIPRI 
Oposiciones
FP
Normativa...
```

---

### Relevancia

Escala:

```
1-10
```

---

### Impacto

Escala:

```
1-10
```

---

### Urgencia

Escala:

```
1-10
```

---

# 8.3 Clustering Semántico

Es el proceso más importante del proyecto.

---

## Objetivo

Detectar que varias noticias hablan del mismo evento.

---

### Ejemplo

Noticia 1

```
La Junta publica nuevas vacantes SIPRI
```

---

Noticia 2

```
350 plazas ofertadas en SIPRI
```

---

Noticia 3

```
Nueva adjudicación de puestos docentes
```

---

Resultado

```
Evento único:
Adjudicación SIPRI Mayo 2026
```

---

# 8.4 Estrategia de Clustering

## Fase MVP

IA mediante prompts.

---

## Fase futura

Embeddings semánticos.

Posibles tecnologías:

- pgvector
- OpenAI embeddings
- BGE
- Ollama embeddings

---

# 8.5 Generación de Resúmenes

Cada evento tendrá:

## Resumen Ejecutivo

Máximo 150 palabras.

---

## Resumen Sindical

Máximo 300 palabras.

---

## Puntos Clave

Lista de bullets.

---

# 8.6 Generación de Contenido

Para cada evento:

### Telegram

### Facebook

### X

---

Cada canal tendrá reglas propias.

---

# 8.7 Sistema de Scoring

Cada evento tendrá:

## importance_score

Importancia.

---

## urgency_score

Urgencia.

---

## impact_score

Impacto.

---

## confidence_score

Confianza IA.

---

# 8.8 Estrategia de Proveedores IA

## MVP

Proveedor principal:

```
OpenAI
```

---

## Futuro

Compatibilidad con:

```
Anthropic
Gemini
Ollama
Llama
Mistral
```

---

# 9. Arquitectura de Automatización (n8n)

## 9.1 Filosofía

n8n será el motor de automatización.

No contendrá reglas de negocio complejas.

---

# 9.2 Workflow 1

## Captura de Noticias

Objetivo:

Obtener información.

### Fuentes

- RSS
- Google News
- BOJA
- BOE

Resultado:

```
news_articles
```

---

# 9.3 Workflow 2

## Clasificación IA

Entrada:

Noticias nuevas.

Salida:

```
categoria
impacto
urgencia
relevancia
```

---

# 9.4 Workflow 3

## Clustering

Entrada:

Noticias clasificadas.

Salida:

```
cluster_idevent_id
```

---

# 9.5 Workflow 4

## Enriquecimiento de Eventos

Generar:

- resumen ejecutivo
- resumen sindical
- puntos clave

---

# 9.6 Workflow 5

## Generación de Contenido

Crear:

- Telegram
- Facebook
- X

---

# 9.7 Workflow 6

## Publicación

Publicar contenido aprobado.

---

# 9.8 Workflow 7

## Auditoría

Registrar:

- errores
- tiempos
- ejecuciones

---

# 9.9 Workflow 8

## Tendencias

Detectar:

- temas emergentes
- aumento de menciones
- eventos relevantes

---

# 9.10 Workflow 9

## Resumen Diario

Generar:

```
Top eventos del día
```

para administradores.

---

# Decisiones Arquitectónicas Nuevas

### DA-009

El Evento es la entidad principal del dominio.

---

### DA-010

La IA agrupa noticias antes de generar contenido.

---

### DA-011

Nunca se publica contenido directamente desde una noticia individual.

---

### DA-012

Todo contenido publicado debe derivar de un Evento.

---

### DA-013

La IA será asistente editorial, no sustituto editorial.

---
# 10. Arquitectura Editorial

## 10.1 Introducción

El objetivo de la plataforma no es publicar automáticamente.

El objetivo es:

```
ayudar al equipo editorial a tomar mejores decisiones
```

La IA debe actuar como:

```
Asistente Editorial Inteligente
```

Nunca como sustituto del editor.

---

# 10.2 Flujo Editorial General

```
Evento Detectado
        ↓
Evento Clasificado
        ↓
Resumen Generado
        ↓
Contenido Generado
        ↓
Revisión Humana
        ↓
Aprobación
        ↓
Publicación
```

---

# 10.3 Principio Editorial Fundamental

Toda publicación debe originarse desde un Evento.

Nunca desde una noticia individual.

---

## Incorrecto

```
Noticia BOJA
→ Telegram

Noticia CSIF
→ Telegram

Noticia ANPE
→ Telegram
```

Resultado:

```
3 publicaciones duplicadas
```

---

## Correcto

```
BOJA
CSIF
ANPE
      ↓
 Evento
      ↓
 Telegram
```

Resultado:

```
1 única publicación
```

---

# 10.4 Perfiles Editoriales

La IA deberá soportar perfiles configurables.

---

## Perfil Informativo

Perfil por defecto.

Características:

- Neutral.
- Objetivo.
- Basado en hechos.
- Sin opinión.

---

## Perfil Institucional

Características:

- Comunicación oficial.
- Referencias al sindicato.
- Tono corporativo.

---

## Perfil Divulgativo

Características:

- Lenguaje sencillo.
- Explicativo.
- Orientado a docentes.

---

## Perfil Reivindicativo

Características:

- Defensa de intereses docentes.
- Posicionamiento sindical.

Uso restringido y siempre supervisado.

---

## Perfil Movilizador

Características:

- Convocatorias.
- Participación.
- Campañas.

---

# 10.5 Estrategia de Publicación

Cada canal tendrá características propias.

---

## Telegram

Objetivo:

Información rápida.

---

## Facebook

Objetivo:

Información ampliada.

---

## X

Objetivo:

Impacto y difusión rápida.

---

## Futuro

- Instagram
- LinkedIn
- Newsletter
- WhatsApp Channels
- Web corporativa

---

# 10.6 Cola Editorial

Todas las publicaciones pasarán por una cola.

Estados:

```
BORRADOR
↓
PENDIENTE_REVISION
↓
APROBADA
↓
PROGRAMADA
↓
PUBLICADA
```

---

# 10.7 Centro de Inteligencia Sindical

Además de publicar, la plataforma deberá servir para:

## Detectar Tendencias

Ejemplo:

```
SIPRI
↑ 35%
```

---

## Detectar Temas Emergentes

Ejemplo:

```
Nueva normativa FP
```

---

## Detectar Eventos Críticos

Ejemplo:

```
BOJA urgente
```

---

# 11. Roadmap del Proyecto

---

# Fase 1 - MVP Información

Objetivo:

Capturar y almacenar noticias.

Incluye:

- PostgreSQL
- n8n
- RSS
- Google News

Estado:

En curso.

---

# Fase 2 - Clasificación

Objetivo:

Clasificar noticias mediante IA.

Incluye:

- Categorización
- Relevancia
- Impacto
- Urgencia

---

# Fase 3 - Eventos

Objetivo:

Agrupar noticias similares.

Incluye:

- Clustering IA
- Creación de eventos

---

# Fase 4 - Generación de Contenido

Objetivo:

Crear contenido automáticamente.

Incluye:

- Telegram
- Facebook
- X

---

# Fase 5 - Panel Web

Objetivo:

Gestionar el sistema.

Incluye:

- Angular
- Spring Boot
- Usuarios
- Eventos

---

# Fase 6 - Inteligencia Sindical

Objetivo:

Convertir información en conocimiento.

Incluye:

- Tendencias
- Alertas
- Estadísticas

---

# Fase 7 - Optimización IA

Incluye:

- Embeddings
- pgvector
- Modelos locales

---

# Fase 8 - Aplicación Móvil

Incluye:

- Flutter
- Consumo de API existente

---

# 12. Riesgos del Proyecto

## R-001 Duplicidad de Noticias

Mitigación:

Clustering semántico.

---

## R-002 Coste IA

Mitigación:

- Cachés
- Agrupación previa
- Modelos locales futuros

---

## R-003 Dependencia de Fuentes

Mitigación:

Multiplicidad de fuentes.

---

## R-004 Calidad IA

Mitigación:

Human In The Loop.

---

## R-005 Crecimiento de Datos

Mitigación:

Archivado.

Retención.

Optimización PostgreSQL.

---

## R-006 Cambios en APIs Externas

Mitigación:

Arquitectura desacoplada.

---

# 13. Estrategia de Calidad

## Principios

### Trazabilidad

Todo debe poder rastrearse.

---

### Auditabilidad

Toda acción debe quedar registrada.

---

### Reproducibilidad

Los resultados deben poder regenerarse.

---

### Explicabilidad

Debe conocerse por qué la IA tomó una decisión.

---

# 14. Documentación Derivada

Este documento da origen a la siguiente documentación.

---

## Documento 02

ERS

Especificación de Requisitos Software.

---

## Documento 03

Casos de Uso.

---

## Documento 04

Reglas de Negocio.

---

## Documento 05

Modelo de Datos.

---

## Documento 06

Arquitectura Backend.

---

## Documento 07

Arquitectura Frontend.

---

## Documento 08

Arquitectura n8n.

---

## Documento 09

Arquitectura IA.

---

## Documento 10

Catálogo de Prompts.

---

## Documento 11

Plan de Pruebas.

---

## Documento 12

Plan de Despliegue.

---

## Documento 13

Plan de Seguridad.

---

## Documento 14

Manual de Usuario.

---

## Documento 15

Manual de Administración.

---

# 15. Diagramas a Crear

## Arquitectura

- Diagrama de Contexto
- C4 Nivel 1
- C4 Nivel 2
- C4 Nivel 3

---

## Datos

- ERD completo
- Modelo lógico
- Modelo físico

---

## Negocio

- Casos de uso
- Flujo editorial
- Estados de eventos

---

## Automatización

- Workflow captura
- Workflow clasificación
- Workflow clustering
- Workflow publicación

---

## IA

- Flujo de clasificación
- Flujo de generación
- Flujo de clustering

---

# 16. Criterios de Éxito

El proyecto será considerado exitoso cuando:

### CES-001

Detecte automáticamente eventos relevantes.

---

### CES-002

Reduzca significativamente publicaciones duplicadas.

---

### CES-003

Permita generar contenido útil para redes sociales.

---

### CES-004

Mantenga supervisión humana.

---

### CES-005

Reduzca tiempo dedicado al análisis informativo.

---

### CES-006

Sea utilizado diariamente por el equipo editorial.

---

### CES-007

Aumente la capacidad de comunicación del sindicato.

---

# 17. Conclusión

La Plataforma de Inteligencia Informativa para el Sindicato de Docentes de Andalucía no debe entenderse como un simple agregador de noticias.

Su objetivo es convertirse en un:

```
Sistema de Inteligencia Sindical
```

capaz de transformar grandes volúmenes de información dispersa en conocimiento útil, contenido editorial de calidad y capacidad de reacción para el sindicato.

