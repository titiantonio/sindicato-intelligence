# Índice de documentación

**Actualizado:** 25/07/2026  
**Objetivo:** identificar la documentación vigente y evitar que versiones históricas se interpreten como contratos actuales.

## Punto de entrada

1. [`00-agent-context.md`](00-agent-context.md): contexto técnico breve y estado operativo.
2. [`../README.md`](../README.md): descripción, instalación, credenciales de demostración y enlaces de entrega.
3. [`guia_ejecucion_tfm.md`](guia_ejecucion_tfm.md): recorrido reproducible para evaluación.
4. [`Documentacion_Final/Manual_Operativo_Usuario.md`](Documentacion_Final/Manual_Operativo_Usuario.md): uso funcional del backoffice.
5. [`Documentacion_Final/2026_07_25_informe_preparacion_entrega_tfm.md`](Documentacion_Final/2026_07_25_informe_preparacion_entrega_tfm.md): auditoría final y pendientes externos.
6. [`Documentacion_Final/2026_07_25_guion_video_tfm.md`](Documentacion_Final/2026_07_25_guion_video_tfm.md): guion de demostración de 7–8 minutos.

## Referencias técnicas vigentes

| Área | Documento canónico |
| --- | --- |
| Plan maestro | [`Documento 30 – MVP Técnico Ejecutable.md`](<Documentacion Proyecto/Documento 30 – MVP Técnico Ejecutable.md>) |
| Backlog operativo | [`Documento 31 – Plan de Implementación Detallado.md`](<Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md>) |
| Convenciones | [`Documento 21 – Convenciones de Desarrollo.md`](<Documentacion Proyecto/Documento 21 – Convenciones de Desarrollo.md>) |
| Dominio DDD | [`Documento 17 - Modelo de Dominio (DDD).md`](<Documentacion Proyecto/Documento 17 - Modelo de Dominio (DDD).md>) |
| Estructura backend | [`Documento 18 – Estructura Spring Boot.md`](<Documentacion Proyecto/Documento 18 – Estructura Spring Boot.md>) |
| Casos de uso | [`Documento 19 – Diseño de Casos de Uso.md`](<Documentacion Proyecto/Documento 19 – Diseño de Casos de Uso (Application Layer).md>) |
| Modelo físico y Flyway | [`Documento 20 – ERD Final MVP + Estrategia Flyway.md`](<Documentacion Proyecto/Documento 20 – ERD Final MVP + Estrategia Flyway.md>) |
| API REST | [`Documento 12 - Diseño API REST.md`](<Documentacion Proyecto/Documento 12 - Diseño API REST.md>) |
| Seguridad | [`Documento 13 - Seguridad y Roles.md`](<Documentacion Proyecto/Documento 13 - Seguridad y Roles.md>) |
| Infraestructura | [`Documento 14 - DevOps e Infraestructura.md`](<Documentacion Proyecto/Documento 14 - DevOps e Infraestructura.md>) |
| Pruebas | [`Documento 15 - Plan de Pruebas.md`](<Documentacion Proyecto/Documento 15 - Plan de Pruebas.md>) |
| n8n y automatizaciones | [`Documento 09 V2.0 - Arquitectura de Integraciones y Workflows n8n.md`](<Documentacion Proyecto/Documento 09 V2.0 - Arquitectura de Integraciones y Workflows n8n.md>) |
| Flujo extremo a extremo | [`2026_06_27_flujo_completo_wf_01_wf_06.md`](<Documentacion Proyecto/2026_06_27_flujo_completo_wf_01_wf_06.md>) |
| Prompts IA | [`Documento 23 – Catálogo de Prompts IA.md`](<Documentacion Proyecto/Documento 23 – Catálogo de Prompts IA.md>) |
| UX/UI | [`Documento 24 – Diseño UX-UI del Backoffice.md`](<Documentacion Proyecto/Documento 24 – Diseño UX-UI del Backoffice.md>) |

## Documentos históricos

Los documentos siguientes se conservan por trazabilidad, pero no deben usarse como contrato de implementación cuando exista una versión posterior:

- `Documento 03`, `03.1`, `03A` y `03B`: antecedentes del modelo de datos. Prevalece el Documento 20.
- `Documento 06`: arquitectura backend inicial. Prevalecen los Documentos 17, 18, 19 y 30.
- `Documento 09` sin `V2.0`: diseño n8n anterior. El estado vigente mantiene solo `WF-01` en n8n.
- `Documento 10` y catálogos iniciales de IA: prevalece el Documento 23.
- `Documento 11` y `Documento 22`: planificación anterior. Prevalecen los Documentos 30 y 31.

No se eliminan estos archivos durante la preparación del TFM porque explican la evolución del proyecto. Si se desea simplificar la entrega, deben moverse en una intervención separada a `docs/historico/`, actualizando antes todos los enlaces.

## Documentación del trabajo del asistente

`Docs_Asistentes/` contiene registros de intervención exigidos por las reglas del proyecto. Son evidencia de trazabilidad técnica, no documentación principal para el evaluador. El README y este índice deben ser siempre los puntos de entrada.

## Convención de vigencia

Ante cualquier contradicción, se aplica este orden:

1. `AGENTS.md`;
2. `docs/00-agent-context.md`;
3. Documentos 30 y 31;
4. documentación técnica canónica de la tabla anterior;
5. código y migraciones ejecutables;
6. documentos históricos.
