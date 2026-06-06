# Procedimiento de Desarrollo, Despliegue y Actualización

Versión: 1.0

Estado: Operativo

---

# 1. Objetivo

Definir el procedimiento oficial para:

* Desarrollo local.
* Gestión de cambios.
* Sincronización con GitHub.
* Despliegue en servidor Proxmox.
* Actualización de Backend.
* Actualización de Frontend.
* Actualización de Workflows n8n.
* Actualización de documentación.

---

# 2. Arquitectura Actual

## Entorno de Desarrollo

Equipo portátil:

```text
Windows 11

VS Code

Docker Desktop

PostgreSQL Docker

Spring Boot

Angular
```

---

## Entorno Servidor

Proxmox:

```text
LXC 103
sindicato-intelligence
```

IP:

```text
192.168.1.8
```

---

Servicios:

```text
PostgreSQL

n8n

Backend Spring Boot

Frontend Angular (futuro)
```

---

Repositorio Git:

```text
GitHub
↓
sindicato-intelligence
```

---

# 3. Flujo Oficial de Trabajo

```text
Desarrollo Local
      ↓
Commit
      ↓
Push GitHub
      ↓
Pull Servidor
      ↓
Despliegue
      ↓
Validación
```

---

# 4. Desarrollo Local

Realizar cambios en:

```text
backend/

frontend/

database/

docs/

.skills/

n8n/
```

---

Verificar:

```text
Compila correctamente

No rompe tests

No rompe Flyway
```

---

# 5. Commit

Desde la raíz del proyecto:

```bash
git add .
```

---

```bash
git commit -m "descripcion cambio"
```

---

Ejemplos:

```bash
git commit -m "feat: source module"
```

```bash
git commit -m "feat: news api"
```

```bash
git commit -m "fix: duplicate detection"
```

---

# 6. Push

Enviar cambios:

```bash
git push
```

---

Verificar:

```text
Cambios visibles en GitHub
```

---

# 7. Acceso al Servidor

Conectar:

```bash
ssh root@192.168.1.8
```

---

Moverse al proyecto:

```bash
cd /opt/sindicato-intelligence
```

---

# 8. Actualización de Código

Actualizar repositorio:

```bash
git pull
```

---

Verificar:

```bash
git status
```

Resultado esperado:

```text
working tree clean
```

---

# 9. Actualización Backend

Obligatorio cuando cambian:

```text
Java

Spring Boot

Controladores

Servicios

Use Cases

Entidades

Repositorios

DTOs

Configuración Spring

Flyway
```

---

Desplegar:

```bash
docker compose up -d --build backend
```

---

Ver logs:

```bash
docker logs sindicato-backend --tail=100
```

---

Validar:

```text
http://192.168.1.8:8080/api/v1/health
```

---

Resultado esperado:

```json
{
  "status": "UP"
}
```

---

# 10. Actualización Base de Datos

Cuando se añade una nueva migración Flyway:

Ejemplo:

```text
V3__create_events.sql
```

---

Proceso:

```bash
git pull
```

---

```bash
docker compose up -d --build backend
```

---

Flyway ejecutará automáticamente:

```text
Migraciones pendientes
```

---

Verificar:

```sql
SELECT * FROM flyway_schema_history;
```

---

# 11. Actualización Frontend

(Disponible cuando exista Frontend Dockerizado)

Cuando cambian:

```text
Angular

HTML

CSS

TypeScript
```

---

Actualizar:

```bash
docker compose up -d --build frontend
```

---

Verificar:

```text
Aplicación accesible
```

---

# 12. Actualización Workflows n8n

Cuando cambian:

```text
Workflows

Prompts

Automatizaciones
```

---

Método MVP:

```text
Importar workflow manualmente
```

---

Exportar desde desarrollo:

```json
workflow.json
```

---

Importar en servidor:

```text
n8n
↓
Import Workflow
```

---

Validar:

```text
Workflow activo

Ejecución correcta
```

---

# 13. Actualización Documentación

Cuando cambian:

```text
docs/

AGENTS.md

.skills/
```

---

Proceso:

```bash
git pull
```

---

No requiere:

```text
docker compose
```

---

# 14. Script Oficial de Despliegue Backend

Archivo:

```text
deploy-backend.sh
```

---

Ejecutar:

```bash
./deploy-backend.sh
```

---

Responsabilidades:

```text
Git Pull

Build Backend

Reinicio Backend

Mostrar Logs
```

---

# 15. Verificación Post Despliegue

Comprobar:

```bash
docker ps
```

---

Debe mostrar:

```text
sindicato-postgres

sindicato-n8n

sindicato-backend
```

---

Comprobar logs:

```bash
docker logs sindicato-backend --tail=50
```

---

Comprobar API:

```text
/api/v1/health
```

---

# 16. Procedimiento de Rollback

Si una versión falla:

Ver historial:

```bash
git log --oneline
```

---

Volver a commit anterior:

```bash
git checkout HASH
```

---

Reconstruir:

```bash
docker compose up -d --build backend
```

---

# 17. Buenas Prácticas

Nunca modificar código directamente en servidor.

---

Siempre:

```text
Portátil
↓
GitHub
↓
Servidor
```

---

Nunca subir:

```text
.env

credenciales

tokens

claves privadas
```

---

Mantener:

```text
Repositorio limpio

Commits pequeños

Documentación actualizada
```

---

# 18. Flujo Operativo Diario

```text
Programar
 ↓

Probar local
 ↓

Commit
 ↓

Push
 ↓

SSH servidor
 ↓

git pull
 ↓

./deploy-backend.sh
 ↓

Verificar health
 ↓

Continuar desarrollo
```

---

# 19. Estado Actual

Infraestructura operativa:

```text
✓ GitHub

✓ PostgreSQL

✓ Spring Boot

✓ Flyway

✓ Docker

✓ n8n

✓ Script Deploy Backend

✓ Servidor Proxmox
```

---

# 20. Próximo Objetivo

Continuar con:

```text
Sprint 4

WF-01 Captura Noticias

POST /api/v1/news

POST /api/v1/news/bulk

Integración n8n → Spring Boot
```
