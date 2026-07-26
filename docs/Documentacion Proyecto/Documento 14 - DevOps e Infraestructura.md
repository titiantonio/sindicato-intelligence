# Documento 14 - DevOps e infraestructura

**Actualizado:** 25/07/2026

## 1. Estado actual

La entrega dispone de un entorno local reproducible mediante el `docker-compose.yml` de la raíz.

Servicios:

```text
postgres
mailhog
backend
frontend
n8n
```

No existe, a fecha de revisión, una URL pública de producción verificada. Proxmox y Nginx siguen siendo la arquitectura objetivo.

## 2. Topología local

```text
Navegador :4200
  -> frontend Angular / Nginx
  -> backend Spring Boot :8080
  -> PostgreSQL :5432

n8n :5678
  -> backend /api/v1/news/bulk

backend
  -> MailHog SMTP :1025
  -> MailHog UI :8025
  -> proveedor IA opcional
  -> Telegram opcional
```

Volúmenes:

- `postgres_data`;
- `n8n_data`;
- `backend_data`;
- `backend_logs`.

## 3. Operación local TFM

```powershell
.\tfm-start.ps1
.\tfm-check.ps1
.\tfm-stop.ps1
.\tfm-reset.ps1
```

`tfm-start.ps1`:

1. valida Docker;
2. crea `.env` desde `.env.example` si falta;
3. construye backend y frontend;
4. inicia el stack;
5. espera health checks;
6. configura e importa `WF-01` en n8n.

`tfm-check.ps1` comprueba servicios y workflow.

`tfm-reset.ps1` elimina volúmenes y datos locales. Su uso debe ser consciente.

## 4. Imágenes

- PostgreSQL 17.
- MailHog oficial.
- backend desde `backend/Dockerfile`.
- frontend desde `frontend/Dockerfile`.
- n8n desde imagen oficial.

Las etiquetas no fijadas de imágenes externas deben cerrarse a versiones conocidas antes de producción para evitar actualizaciones inesperadas.

## 5. Configuración

`.env.example` documenta las variables necesarias. `.env` no se versiona.

Grupos:

- PostgreSQL;
- JWT y cifrado de settings;
- autenticación n8n;
- proveedor IA;
- Telegram;
- OpenAPI.

El perfil de producción exige secretos externos y deshabilita OpenAPI por defecto.

## 6. Despliegue objetivo

```text
Proxmox
  -> VM o LXC Docker
      -> Nginx reverse proxy + TLS
      -> frontend
      -> backend
      -> PostgreSQL en red privada
      -> n8n en red privada
      -> MailHog solo en entornos no productivos
```

Nginx debe:

- terminar TLS;
- aplicar cabeceras de seguridad;
- limitar tamaño de cuerpo conforme a adjuntos;
- aplicar rate limiting;
- ocultar servicios internos;
- redirigir HTTP a HTTPS.

## 7. Persistencia y backup

Objetivos:

- backup diario de PostgreSQL;
- exportación/versionado controlado de `WF-01`;
- backup de volúmenes operativos necesarios;
- retención definida;
- prueba periódica de restauración.

Una copia no se considera fiable hasta haber verificado su restauración.

## 8. Logging y observabilidad

El backend usa Logback con:

- consola;
- archivo diario;
- compresión;
- carpeta mensual;
- retención de 90 días.

Se registran `INFO`, `WARN` y `ERROR` sin secretos.

Para producción se recomienda:

- health checks externos;
- alerta de indisponibilidad;
- seguimiento de errores de IA, Telegram y n8n;
- capacidad de disco y estado de backups;
- métricas de host.

Prometheus/Grafana o Uptime Kuma son opciones documentadas, no componentes implementados en la entrega local.

## 9. CI/CD

No hay una canalización GitHub Actions verificada como parte del estado actual. Antes de producción debe añadirse:

```text
checkout
  -> backend test/package
  -> frontend install/test/build
  -> Playwright mock
  -> validación n8n y scripts
  -> análisis de dependencias
  -> build de imágenes
  -> publicación y despliegue controlado
```

Para el TFM, la evidencia se obtiene mediante los comandos reproducibles documentados en el README y la guía de ejecución.

## 10. Pendientes de producción

- publicar en `main` los cambios finales de entrega; el repositorio ya es
  público y fue verificado sin sesión el 26/07/2026;
- decidir y ejecutar un despliegue público si se desea;
- usar dominio y TLS;
- introducir un gestor de secretos;
- fijar versiones de todas las imágenes;
- automatizar backup y restauración;
- añadir CI/CD;
- retirar MailHog y credenciales de demostración;
- restringir Swagger, n8n y PostgreSQL.
