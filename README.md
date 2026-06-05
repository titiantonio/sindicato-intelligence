# sindicato-intelligence

Plataforma interna de inteligencia informativa para un sindicato de docentes de Andalucia.

El objetivo del MVP es reducir el trabajo manual de seguimiento informativo: capturar noticias educativas, clasificarlas, agruparlas en eventos, generar analisis y preparar contenido para revision y publicacion.

## Flujo MVP

```text
Fuentes RSS
  -> n8n
  -> Spring Boot API
  -> PostgreSQL
  -> IA
  -> Eventos
  -> Contenido
  -> Telegram
```

La entidad central del sistema es `Event`. Las noticias son materia prima y no deben publicarse como unidades aisladas.

## Estructura del repositorio

```text
sindicato-intelligence/
├── docs/
├── fronted/
├── n8n/
├── backend/
├── database/
└── skills/
```

## Directorios principales

### `docs/`

Documentacion funcional, tecnica y operativa del proyecto.

Incluye los documentos de referencia del MVP, arquitectura, dominio, API, seguridad, modelo de datos, Flyway, pruebas, prompts IA y registros de trabajo de asistentes.

Documentos clave:

- `docs/00-agent-context.md`: contexto rapido para agentes.
- `docs/Documentacion Proyecto/Documento 30 – MVP Técnico Ejecutable.md`: plan maestro del MVP.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`: backlog operativo por sprints y tareas.
- `docs/Documentacion Proyecto/Documento 20 – ERD Final MVP + Estrategia Flyway.md`: modelo fisico definitivo del MVP.

### `fronted/`

Directorio destinado al frontend Angular del backoffice.

Nota: el nombre actual de la carpeta es `fronted/`. Se mantiene asi mientras no se apruebe un cambio de nombre para evitar romper referencias existentes.

### `n8n/`

Workflows de automatizacion n8n.

n8n orquesta los flujos de captura, clasificacion, deteccion de eventos, analisis, generacion de contenido y publicacion, pero la logica de negocio reside en el backend Spring Boot.

### `backend/`

Backend Spring Boot del MVP.

Tecnologias principales:

- Java 21.
- Spring Boot 3.x.
- Maven.
- Spring Security.
- Spring Data JPA.
- Flyway.
- PostgreSQL.

Arquitectura esperada:

- DDD.
- Clean Architecture.
- Modular Monolith.

Package base vigente:

```text
es.sindicato.intelligence
```

### `database/`

Configuracion local de PostgreSQL y recursos asociados de base de datos.

El esquema de aplicacion se gestiona mediante migraciones Flyway ubicadas en:

```text
backend/src/main/resources/db/migration
```

### `skills/`

Skills y guias especializadas para asistentes del proyecto.

Sirven para mantener coherencia en implementaciones backend, Flyway, seguridad, testing, documentacion, workflows IA/n8n y frontend.

## Estado de implementacion

El avance operativo se controla con el Documento 31. Las tareas completadas se marcan con `[x]` y los sprints se cierran solo cuando todas sus tareas verificables estan finalizadas.

## Comandos utiles

Desde `backend/`:

```powershell
.\mvnw.cmd test
```

Desde `database/`:

```powershell
docker compose up -d
```
