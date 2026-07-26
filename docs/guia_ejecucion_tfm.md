# Guía de ejecución y evaluación del TFM

**Proyecto:** Sindicato Intelligence
**Actualizado:** 25/07/2026
**Entorno recomendado:** Docker Compose local

## 1. Objetivo

Esta guía permite levantar el proyecto completo, comprobar sus servicios y recorrer el flujo funcional sin configurar IA ni Telegram reales.

El entorno local inicia:

- PostgreSQL;
- MailHog;
- backend Spring Boot;
- frontend Angular servido por Nginx;
- n8n con el workflow `WF-01-Capture-News`.

Las automatizaciones `WF-02` a `WF-06` pertenecen al backend Spring Boot.

## 2. Requisitos

- Git.
- Docker Desktop iniciado.
- Docker Compose v2.
- PowerShell.
- Al menos 6 GB de memoria disponibles para Docker.
- Puertos libres: `4200`, `8080`, `5432`, `5678`, `8025` y `1025`.

Comprobación rápida:

```powershell
git --version
docker --version
docker compose version
```

## 3. Descarga e inicio

```powershell
git clone https://github.com/titiantonio/sindicato-intelligence.git
cd sindicato-intelligence
.\tfm-start.ps1
```

Si no existe `.env`, el script copia `.env.example`. Después construye las imágenes, inicia los contenedores, espera sus health checks e importa `WF-01` en n8n.

La primera ejecución puede tardar varios minutos por la descarga de imágenes y dependencias.

## 4. Comprobación automática

```powershell
.\tfm-check.ps1
```

El resultado esperado confirma:

- PostgreSQL preparado;
- backend con health `UP`;
- frontend accesible;
- n8n accesible y `WF-01` importado;
- MailHog accesible.

También puede consultarse el estado con:

```powershell
docker compose ps
```

## 5. URLs

| Componente | URL |
| --- | --- |
| Backoffice | <http://localhost:4200> |
| Backend health | <http://localhost:8080/api/v1/health> |
| Swagger UI | <http://localhost:8080/swagger-ui/index.html> |
| OpenAPI JSON | <http://localhost:8080/v3/api-docs> |
| n8n | <http://localhost:5678> |
| MailHog | <http://localhost:8025> |

## 6. Credenciales de demostración

### Backoffice y API

| Rol | Email | Contraseña |
| --- | --- | --- |
| `ADMIN` | `admin@sindicato.es` | `Admin@12345` |
| `EDITOR` | `editor@sindicato.es` | `Admin@12345` |

### Usuario técnico de n8n contra el backend

| Email | Contraseña |
| --- | --- |
| `n8n@sindicato.es` | `Admin@12345` |

### Acceso web a n8n

Los valores de demostración se encuentran en `.env.example` y se copian a `.env` en el primer arranque:

```text
N8N_BASIC_AUTH_USER=admin
N8N_BASIC_AUTH_PASSWORD=Admin@12345
```

Estas credenciales no son aptas para producción.

## 7. Recorrido recomendado de evaluación

### 7.1 Acceso y dashboard

1. Abre <http://localhost:4200>.
2. Accede como `admin@sindicato.es`.
3. Comprueba métricas, aviso editorial, eventos prioritarios y botones de automatización.

### 7.2 Noticias y eventos

1. Entra en `Noticias` y abre una noticia.
2. Comprueba su fuente, estado, clasificación y evento vinculado.
3. Entra en `Eventos`.
4. Observa el buscador, filtros, impacto, número de noticias y estado editorial.
5. Abre un evento para revisar noticias relacionadas, análisis y contenidos.

### 7.3 Flujo editorial

1. Desde un evento analizado, genera contenido.
2. Abre el contenido generado.
3. Edita título o cuerpo si procede.
4. Aprueba o rechaza el borrador.
5. Comprueba que solo el contenido aprobado puede publicarse.

El proveedor determinista local permite demostrar este flujo sin una API externa.

### 7.4 Publicaciones

1. Entra en `Publicaciones`.
2. Revisa el histórico y el detalle de una publicación.
3. Abre `Mensaje manual` para mostrar editor, destinos y adjuntos.

Telegram está deshabilitado por defecto. No debe activarse durante una evaluación salvo que exista un bot de pruebas seguro.

### 7.5 Administración

1. En `Fuentes`, revisa las fuentes RSS.
2. En `Usuarios`, comprueba alta, estados y roles.
3. En `Auditoría`, revisa acciones de usuario y editoriales.
4. En `Configuración`, recorre:
   - métricas IA;
   - prompts versionados;
   - automatizaciones;
   - publicación Telegram.

## 8. Flujo n8n

`WF-01-Capture-News` es el único workflow n8n activo:

1. obtiene RSS/XML;
2. normaliza los elementos;
3. se autentica contra Spring Boot;
4. envía las noticias a `POST /api/v1/news/bulk`.

Por seguridad y control de dominio, la clasificación, detección de eventos, análisis, contenido y publicación se ejecutan en Spring Boot.

Validación estática:

```powershell
.\n8n\validate-workflows.ps1
```

## 9. Pruebas

### Backend

```powershell
cd backend
.\mvnw.cmd test
cd ..
```

### Frontend

```powershell
cd frontend
npm install
npm test -- --watch=false --browsers=ChromeHeadless
npm run build
npm run e2e:mock
cd ..
```

Las pruebas Playwright mockeadas pueden ejecutarse sin backend ni PostgreSQL y no llaman a IA ni Telegram reales.

## 10. Parada

```powershell
.\tfm-stop.ps1
```

Para regenerar todos los datos:

```powershell
.\tfm-reset.ps1
```

`tfm-reset.ps1` elimina los volúmenes Docker del proyecto. No debe ejecutarse si se quieren conservar datos locales.

## 11. Resolución de problemas

### Un puerto está ocupado

Comprueba el proceso o contenedor que usa el puerto:

```powershell
docker compose ps
Get-NetTCPConnection -State Listen | Where-Object LocalPort -in 4200,8080,5432,5678,8025,1025
```

Detén solo el proceso conocido que pertenezca a una ejecución anterior del proyecto.

### El backend no está preparado

```powershell
docker compose logs backend --tail 200
docker compose ps
```

### Hay que reiniciar un servicio

```powershell
docker compose restart backend
```

### El workflow no aparece

```powershell
docker compose exec -T n8n n8n import:workflow --input=/workflows/wf_01_capture_news.json
```

## 12. Notas de seguridad

- No publiques `.env`.
- Cambia contraseñas y claves antes de cualquier despliegue.
- No grabes tokens, claves ni valores de configuración sensibles en el vídeo.
- No actives Telegram real ni IA de pago durante la demostración si no hay un entorno controlado.
- Swagger es público solo para facilitar la evaluación local.
