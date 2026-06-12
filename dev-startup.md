# Dev Startup Script - Sindicato Intelligence

## Objetivo

Script automatizado para arrancar el entorno local de desarrollo en orden correcto:

1. Resetear base de datos (Docker down -v, up -d)
2. Levantar stack Docker (postgres, n8n, mailhog)
3. Esperar disponibilidad de PostgreSQL
4. Validar que Flyway aplicó migraciones V1, V2, V3
5. Arrancar backend Spring Boot

## Requisitos

- PowerShell 5.0+ (Windows)
- Docker Desktop en ejecución
- Maven (mvnw incluido en `backend/`)
- Git Bash o similar NO necesario
- Permisos de administrador para Docker

## Uso

### Opción 1: Desde Windows Explorer

1. Abre la carpeta raíz del proyecto
2. Haz clic derecho → **Abrir terminal PowerShell aquí**
3. Ejecuta:
   ```powershell
   .\dev-startup.ps1
   ```

### Opción 2: Desde terminal PowerShell existente

```powershell
cd C:\Users\Antonio\Desktop\Proyectos\sindicato-intelligence
.\dev-startup.ps1
```

### Opción 3: Bypass de políticas de ejecución (si requiere)

```powershell
powershell -ExecutionPolicy Bypass -File .\dev-startup.ps1
```

## Qué hace el script

### [1/5] Verificación de directorios

- Valida que existan `database/` y `backend/`
- Si falta alguno, detiene la ejecución

### [2/5] Reset de Docker

```bash
docker compose down -v       # Elimina contenedores + volúmenes
docker compose up -d         # Crea y levanta contenedores
```

**Contenedores levantados:**
- `sindicato-postgres`: PostgreSQL 17 en puerto 5432
- `sindicato-n8n-dev`: n8n en puerto 5678
- `sindicato-mailhog`: MailHog (SMTP 1025, UI 8025)

### [3/5] Espera a PostgreSQL

Intenta conectar a `localhost:5432` hasta 60 segundos.
- Si se conecta: continúa
- Si falla tras 60s: detiene con error

### [4/5] Validación Flyway

Ejecuta query SQL:
```sql
SELECT COUNT(*) FROM flyway_schema_history WHERE version IS NOT NULL;
```

**Esperado:** 3 migraciones aplicadas
- V1: create mvp schema
- V2: seed admin user
- V3: seed rss sources

**Estados:**
- ✓ Exactamente 3: OK, continúa
- ⚠ Otra cantidad: Advertencia, pero continúa
- ✗ No se puede validar: Advertencia, pero continúa

### [5/5] Arranque de backend

Ejecuta en `backend/`:
```bash
.\mvnw.cmd spring-boot:run
```

**Salida esperada:**
```
Started IntelligenceApplication in X seconds
Tomcat started on port 8080 (http) with context path '/'
```

**URLs disponibles:**
- http://localhost:8080/api/v1/health → JSON `{"status":"UP"}`
- http://localhost:4200 → Angular frontend (si levantado por separado)
- http://localhost:5678 → n8n
- http://localhost:8025 → MailHog UI

## Detener el backend

Presiona `Ctrl+C` en la terminal donde corre el script.

Docker seguirá corriendo. Para detener también Docker:
```powershell
cd database
docker compose down
```

## Solución de problemas

### Error: "PowerShell no reconoce .\dev-startup.ps1"

**Causa:** Política de ejecución de scripts restringida

**Solución:**
```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

Luego ejecuta el script nuevamente.

### Error: "Docker no disponible"

**Causa:** Docker Desktop no está en ejecución

**Solución:**
1. Abre Docker Desktop
2. Espera a que esté listo (icono estable)
3. Ejecuta el script nuevamente

### Error: "Puerto 8080 ya en uso"

**Causa:** Backend anterior sigue en ejecución o está ocupado

**Solución:**
```powershell
# Opción 1: Detén todos los procesos Java
Stop-Process -Name java -Force

# Opción 2: Encuentra qué está usando el puerto
$conn = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue
if ($null -ne $conn) {
    Get-Process -Id $conn.OwningProcess
}
```

### Error: "PostgreSQL no está disponible"

**Causa:** Docker tardó más de 60 segundos en iniciar

**Solución:**
```powershell
# Verifica estado de Docker
docker ps

# Si postgres está en "Restarting", espera más y reinicia
docker restart sindicato-postgres

# Ejecuta el script nuevamente
.\dev-startup.ps1
```

### Error: "Flyway con migraciones inesperadas"

**Causa:** BBDD anterior no fue reseteada

**Solución:**
```powershell
# Limpia completamente
cd database
docker compose down -v
docker compose up -d

# Vuelve a correr el script
cd ..
.\dev-startup.ps1
```

## Workflow diario recomendado

### Primer arranque del día

```powershell
cd C:\Users\Antonio\Desktop\Proyectos\sindicato-intelligence
.\dev-startup.ps1
# El script deja backend corriendo en la terminal
```

### Código disponible para desarrollo

Mientras backend corre, en **otra terminal**:

```powershell
# Terminal 2: Frontend (opcional)
cd frontend
npm start

# Terminal 3: Trabajo en código
cd backend
# editar archivos en VS Code
```

### Cambios en backend

Si cambias código Java:
1. Presiona `Ctrl+C` en terminal con backend
2. El script termina
3. Ejecuta `.\dev-startup.ps1` nuevamente

Si cambias archivos de configuración o Flyway:
1. Presiona `Ctrl+C` en terminal
2. Ejecuta `.\dev-startup.ps1` (resetea BBDD completo)

### Al terminar jornada

Opción 1: Dejar corriendo (bajo consumo de recursos)
```powershell
# Solo detén backend con Ctrl+C
# Docker continúa corriendo
```

Opción 2: Apagar todo
```powershell
# Terminal con backend
Ctrl+C

# Nueva terminal
cd database
docker compose down
```

## Variables de entorno por defecto

El script usa la configuración predefinida:

- PostgreSQL: `sindicato` / `sindicato123`
- n8n: `n8n@sindicato.es` / `Admin@123`
- Base de datos: `sindicato_intelligence`
- MailHog SMTP: `localhost:1025`
- MailHog UI: `localhost:8025`

Para cambiar credenciales, edita `database/docker-compose.yml`.

## Archivos relevantes

```text
dev-startup.ps1                    ← Este script
database/docker-compose.yml        ← Configuración Docker
backend/pom.xml                    ← Versionado Maven
backend/src/main/resources/
  db/migration/
    V1__create_mvp_schema.sql      ← Esquema + password_reset_tokens
    V2__seed_admin_user.sql        ← Seeds admin + n8n
    V3__seed_rss_sources.sql       ← Fuentes RSS iniciales
  application.yml                  ← Configuración Spring
```

## Logs y troubleshooting

Durante ejecución, el script genera salida de:

1. **Docker Compose:** confirma contenedores levantados
2. **PostgreSQL:** conexión validada
3. **Flyway:** migraciones aplicadas
4. **Spring Boot:** startup sequence completo

Todos los logs quedan en consola.

Para ver logs de Docker después:
```powershell
# Postgres
docker logs sindicato-postgres --tail=50

# n8n
docker logs sindicato-n8n-dev --tail=50

# MailHog
docker logs sindicato-mailhog --tail=50

# Backend (si está corriendo)
# Presiona Ctrl+C para copiar logs, luego copia manual
```

## Notas finales

- El script es **idempotente**: ejecutarlo múltiples veces es seguro
- Resetea BBDD cada vez (por diseño en fase dev)
- No afecta código ni configuración Git
- Los logs de ejecución útiles aparecen en consola
- Sigue el orden exacto: Docker → PostgreSQL → Flyway → Backend

---

**Última actualización:** 2026-06-11  
**Script:** `dev-startup.ps1`  
**Documentación:** `dev-startup.md`
