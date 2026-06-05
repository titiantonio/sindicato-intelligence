---
name: sindicato-testing-quality
description: Usar para crear o revisar pruebas JUnit 5, Mockito, criterios de aceptacion MVP, cobertura de casos de uso, servicios de dominio, integracion API PostgreSQL y regresiones. Activa esta skill ante cualquier tarea de tests, calidad o verificacion.
---

# Sindicato Testing Quality

## Proposito

Guia la verificacion tecnica y funcional del MVP.

## Documentacion a revisar

- `docs/00-agent-context.md`.
- `docs/Documentacion Proyecto/Documento 15 - Plan de Pruebas.md`.
- `docs/Documentacion Proyecto/Documento 21 – Convenciones de Desarrollo.md`.
- `docs/Documentacion Proyecto/Documento 19 – Diseño de Casos de Uso (Application Layer).md`.

## Prioridades de prueba

- Unitarias para casos de uso.
- Unitarias para servicios de dominio.
- Integracion API + PostgreSQL cuando aplique.
- Casos IA con resultados verificables.

## Criterios MVP

- Captura: noticias guardadas.
- Clasificacion: categoria correcta.
- Eventos: varias noticias de la misma tematica producen un unico evento.
- Analisis: informacion coherente y sin alucinaciones.
- Telegram: publicacion generada y enviada.

## Checklist

- Usar JUnit 5 y Mockito.
- Buscar cobertura mayor al 70%.
- Ejecutar pruebas relevantes o documentar por que no se ejecutan.
