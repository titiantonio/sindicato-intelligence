# Jerarquía Oficial del Modelo de Datos

03D = DOCUMENTO MAESTRO

03A depende de 03D

03B depende de 03D

03 depende de 03D



## Documento 03
Visión General del Modelo de Datos

Objetivo:
Explicar conceptualmente las entidades principales y sus relaciones.

Nivel:
Alto nivel (negocio).

No contiene SQL.

---

## Documento 03A
ERD (Entity Relationship Diagram)

Objetivo:
Representación gráfica completa del modelo de datos.

Fuente:
Derivado de 03D.

Nivel:
Arquitectura.

---

## Documento 03B
Diseño Físico PostgreSQL

Objetivo:
Definición física de tablas, índices, constraints y optimizaciones PostgreSQL.

Fuente:
Derivado de 03D.

Nivel:
Implementación.

---

## Documento 03D
Modelo de Datos Producción v1.0

Objetivo:
Documento maestro.

Fuente oficial del modelo.

Nivel:
Negocio + técnico.

Contiene:
- Entidades
- Relaciones
- Reglas
- Cardinalidades
- Estados
- Campos