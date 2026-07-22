# Guia de ejecucion TFM

## Objetivo

Permitir que el profesorado ejecute la plataforma completa desde una descarga limpia del repositorio publico usando Docker.

## Requisitos

- Docker Desktop o Docker Engine con Docker Compose.
- PowerShell 5.1 o superior.
- Puertos libres: `4200`, `8080`, `5678`, `5432`, `8025` y `1025`.

## Arranque rapido

Desde la raiz del repositorio:

```powershell
.\tfm-start.ps1
```

El script realiza estas acciones:

- Crea `.env` desde `.env.example` si no existe.
- Detiene la infraestructura Docker de desarrollo previa si estaba activa para liberar puertos compartidos.
- Detiene procesos locales reconocidos del propio proyecto en `8080` y `4200` si estaban activos.
- Construye la imagen Docker del backend Spring Boot.
- Construye la imagen Docker del frontend Angular servido por Nginx.
- Levanta PostgreSQL, MailHog, backend, frontend y n8n.
- Espera a que backend, frontend y n8n esten disponibles.
- Configura el owner inicial de n8n si el volumen esta limpio.
- Importa `WF-01-Capture-News` en n8n si no existe. El workflow se importa inactivo para evitar ejecuciones RSS automaticas durante la correccion.

## URLs

| Servicio | URL |
| --- | --- |
| Frontend Angular | `http://localhost:4200` |
| Backend health | `http://localhost:8080/api/v1/health` |
| Swagger/OpenAPI | `http://localhost:8080/swagger-ui/index.html` |
| n8n | `http://localhost:5678` |
| MailHog | `http://localhost:8025` |

## Credenciales

Las credenciales de evaluacion se entregan en el documento de contrasenas local preparado para el profesorado.

Cuentas esperadas en una instalacion limpia:

| Uso | Usuario |
| --- | --- |
| Backend ADMIN | `admin@sindicato.es` |
| Backend EDITOR | `editor@sindicato.es` |
| Cuenta tecnica backend para n8n | `n8n@sindicato.es` |
| Acceso n8n | `n8n@sindicato.es` |
| PostgreSQL | `sindicato` |

## Comprobacion

```powershell
.\tfm-check.ps1
```

Este comando valida:

- PostgreSQL responde.
- Backend health responde.
- Frontend responde.
- n8n responde con autenticacion basica.
- MailHog responde.
- `WF-01-Capture-News` esta importado en n8n.

## Parada

```powershell
.\tfm-stop.ps1
```

## Reset completo

```powershell
.\tfm-reset.ps1
```

Para resetear sin confirmacion y arrancar de nuevo:

```powershell
.\tfm-reset.ps1 -Force -StartAfter
```

## Notas operativas

- El proveedor IA por defecto es `deterministic`, por lo que no hace falta configurar una API key externa para corregir el flujo MVP.
- Telegram queda deshabilitado por defecto. Puede configurarse desde `/settings` o mediante variables si se quiere probar publicacion real.
- MailHog captura los correos locales de recuperacion y alta de usuarios.
- `WF-01` usa `BACKEND_BASE_URL`; en el compose TFM apunta a `http://backend:8080`.
- n8n importa `WF-01` inactivo por seguridad. Puede activarse manualmente desde la UI de n8n si se quiere probar captura RSS programada.
- `tfm-start.ps1` detiene previamente el stack Docker de desarrollo (`database/docker-compose.yml`) para evitar conflictos de puertos con PostgreSQL, n8n y MailHog.
- `tfm-start.ps1` tambien detiene el backend Spring Boot local del proyecto en `8080` y el frontend Angular local en `4200` cuando los reconoce. Si el puerto lo ocupa otro proceso, no lo mata y muestra un error claro.
