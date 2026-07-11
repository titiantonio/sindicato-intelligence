# Ajuste fallback WF-02 con enriquecimiento URL

## Fecha

2026-07-11

## Objetivo

Corregir el fallback de `WF-02` porque `newsId=2927` y su duplicada `3065` seguian fallando con `Gemini response does not contain candidates[0].content.parts[0].text` y no pasaban a `DISCARDED`.

## Contexto

- Fase MVP relacionada: Fase 6 Clasificacion IA.
- Workflow relacionado: `WF-02-Classify-News`.
- Tarea registrada en Documento 31: `19.31 Ajuste fallback WF-02 con enriquecimiento URL`.

## Diagnostico

- `2927` y `3065` seguian en `CAPTURED` y acumulaban fallos en `ai_operation_metrics`.
- El fallback anterior evaluaba las senales educativas sobre `effectiveContent`, que puede incluir contenido enriquecido desde la URL.
- En paginas de medios generalistas, el enriquecimiento puede traer navegacion, enlaces o menus con palabras como `Educacion`, `Universidad` o `FP` aunque la noticia sea de sucesos.
- Esa contaminacion podia bloquear el fallback conservador y mantener el reintento normal.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/classification/application/ClassifyNewsUseCase.java`.
- `backend/src/test/java/es/sindicato/intelligence/classification/application/ClassifyNewsUseCaseTest.java`.
- `backend/pom.xml`.
- `CHANGELOG.md`.
- `docs/Documentacion Proyecto/Documento 31 - Documento 31 – Plan de Implementación Detallado.md`.

## Decisiones tomadas

- La decision de fallback fuera de ambito se calcula solo con el contexto capturado por WF-01: titulo, URL, resumen y contenido original.
- El contenido enriquecido sigue pudiendo enviarse al proveedor IA, pero no bloquea el fallback si el proveedor no devuelve texto.
- Se mantiene la proteccion de no descartar automaticamente noticias que ya tengan senales educativas/sindicales en el contexto original.

## Verificacion

- Backend focal: `mvnw.cmd "-Dtest=ClassifyNewsUseCaseTest" test` OK, 10 tests.

## Nota operativa

Tras desplegar esta correccion y reiniciar el backend compilado, la siguiente ejecucion de `WF-02` deberia clasificar `2927` y `3065` como `OTROS/FUERA_DE_AMBITO` y dejarlas en `DISCARDED`.
