## Estado

Fecha: Junio 2026

### Decisión Arquitectónica

Se adopta una estrategia:

MVP → Producción → Evolución

en lugar de:

Big Bang Development

---

## Objetivo del MVP

Construir una plataforma funcional capaz de:

1. Capturar noticias educativas de Andalucía.
2. Clasificarlas mediante IA.
3. Agruparlas en eventos.
4. Generar resúmenes.
5. Generar contenido editorial.
6. Publicar en Telegram.

---

## Funcionalidades fuera del MVP

Se posponen para versiones posteriores:

- Facebook
- X
- Moderación avanzada
- Versionado editorial
- Relaciones complejas entre eventos
- Métricas avanzadas
- Dashboard analítico avanzado
- Tendencias
- Notificaciones internas

---

## Modelo de Datos Oficial

Documento de referencia:

03D - Modelo de Datos Producción v1.0

---

## Workflows MVP

WF-01 Captura Noticias

WF-02 Clasificación IA

WF-03 Detección y Gestión de Eventos

WF-04 Generación de Resúmenes

WF-05 Generación y Publicación Telegram

---

## Arquitectura MVP

n8n
↓
PostgreSQL
↓
Spring Boot
↓
Angular
↓
Telegram

---

## Entidad Principal

EVENT

Toda la lógica del sistema gira alrededor del evento.

Las noticias son únicamente materia prima.