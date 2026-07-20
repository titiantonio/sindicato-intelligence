# Infraestructura MVP

```
Proxmox
│
└── LXC Docker
      │
      ├── nginx
      ├── springboot
      ├── postgres
      ├── n8n
      └── ollama (opcional)
```

---

# Docker Compose

Servicios:

```
postgres

springboot

angular

n8n

nginx

mailhog
```

---

# Ejecucion Local TFM

La entrega academica usa el `docker-compose.yml` de la raiz del repositorio.

Servicios incluidos:

```text
postgres
backend
frontend
n8n
mailhog
```

Scripts operativos:

```powershell
.\tfm-start.ps1
.\tfm-check.ps1
.\tfm-stop.ps1
.\tfm-reset.ps1
```

El script de arranque crea `.env` desde `.env.example` si no existe, construye backend/frontend, levanta el stack, configura n8n e importa `WF-01-Capture-News` si falta.

---

# Backups

## PostgreSQL

Diario.

---

## n8n

Diario.

---

## Archivos

Diario.

---

# Monitorización

## Fase MVP

```
Uptime Kuma
```

---

## Fase 2

```
Prometheus

Grafana
```

---

# CI/CD

## MVP

```
GitHub

GitHub Actions
```

Pipeline:

```
Build

Tests

Docker Image

Deploy
```
