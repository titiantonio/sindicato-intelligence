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
├── frontend/
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

### `frontend/`

Directorio destinado al frontend Angular del backoffice.

Incluye el proyecto Angular del backoffice MVP con layout, autenticacion JWT y pantallas iniciales del Sprint 11.

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

### Desarrollo Local

Para desarrollar no uses el stack TFM completo, porque ejecuta backend y frontend dentro de Docker con build de produccion.

Arranque recomendado desde la raiz del repositorio:

```powershell
.\dev-start.ps1
```

El script:

- Para el stack Docker TFM si estaba activo.
- Para una infraestructura Docker de desarrollo anterior si estaba activa.
- Crea `database/.env` desde `database/.env.example` si falta.
- Levanta solo PostgreSQL, n8n y MailHog desde `database/docker-compose.yml`.
- No borra volumenes ni datos.

Despues arranca el backend local en una terminal:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Y el frontend local en otra terminal:

```powershell
cd frontend
npm.cmd start
```

URLs de desarrollo:

```text
Frontend dev: http://localhost:4200
Backend dev: http://localhost:8080/api/v1/health
n8n: http://localhost:5678
MailHog: http://localhost:8025
```

No ejecutes a la vez `dev-start.ps1` y `tfm-start.ps1`, porque comparten puertos `5432`, `5678`, `8025` y `1025`. `dev-start.ps1` detiene primero los stacks previos para evitar ese conflicto.

### Ejecucion Docker para TFM

Requisitos:

- Docker Desktop o Docker Engine con Docker Compose.
- Puertos libres: `4200`, `8080`, `5678`, `5432`, `8025` y `1025`.

Arranque completo desde la raiz del repositorio:

```powershell
.\tfm-start.ps1
```

El script crea `.env` desde `.env.example` si no existe, construye backend y frontend, levanta PostgreSQL, Spring Boot, Angular/Nginx, n8n y MailHog, configura n8n e importa `WF-01-Capture-News` si falta.

URLs principales:

```text
Frontend: http://localhost:4200
Backend health: http://localhost:8080/api/v1/health
Swagger/OpenAPI: http://localhost:8080/swagger-ui/index.html
n8n: http://localhost:5678
MailHog: http://localhost:8025
```

Comprobacion rapida:

```powershell
.\tfm-check.ps1
```

Parada:

```powershell
.\tfm-stop.ps1
```

Reset completo de contenedores y volumenes:

```powershell
.\tfm-reset.ps1
```

Las credenciales de evaluacion se entregan en el documento de contrasenas local indicado por el autor del TFM.

Guia detallada:

```text
docs/guia_ejecucion_tfm.md
```

Desde `backend/`:

```powershell
.\mvnw.cmd test
```

Desde `database/`:

```powershell
docker compose up -d
```
