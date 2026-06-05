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
```

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