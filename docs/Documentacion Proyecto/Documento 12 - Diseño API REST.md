## Objetivo

Definir todos los contratos entre:

```
Angular
↓
Spring Boot
↓
PostgreSQL
```

---

# Principios

## API-001

RESTful.

---

## API-002

JSON únicamente.

---

## API-003

JWT obligatorio.

---

## API-004

Versionado.

```
/api/v1/
```

---

# Módulos API

```
Auth

News

Events

AI

Content

Publications

Sources

Users

Settings
```

---

# Auth

## Login

```
POST /api/v1/auth/login
```

Request

```
{
  "email":"admin@sindicato.es",
  "password":"*****"
}
```

Response

```
{
  "accessToken":"",
  "refreshToken":"",
  "user":{
     "id":1,
     "name":"",
     "role":"ADMIN"
  }
}
```

---

# News

## Listar noticias

```
GET /api/v1/news
```

Filtros:

```
?page=1
&size=20
&source=BOJA
&category=SIPRI
```

---

## Obtener noticia

```
GET /api/v1/news/{id}
```

---

# Events

## Listar eventos

```
GET /api/v1/events
```

---

## Evento detalle

```
GET /api/v1/events/{id}
```

---

## Fusionar eventos

```
POST /api/v1/events/merge
```

---

# Content

## Generar contenido

```
POST /api/v1/content/generate
```

---

## Aprobar contenido

```
POST /api/v1/content/{id}/approve
```

---

## Rechazar contenido

```
POST /api/v1/content/{id}/reject
```

---

# Publications

## Publicar

```
POST /api/v1/publications/{id}/publish
```

---

## Programar

```
POST /api/v1/publications/{id}/schedule
```