# Carga temporal fuentes RSS

## Fecha

2026-06-06

## Objetivo

Cargar en la tabla `sources` una seleccion inicial de fuentes RSS para probar el workflow `WF-01-Capture-News` antes de consolidarlas en una migracion Flyway.

## Contexto

El usuario facilito una lista de fuentes con el formato historico `id_fuente`, `name` y `rss_url`. Para la base de datos actual se uso el formato del modulo `source`: `name`, `url`, `type`, `priority` y `active`.

## Fase MVP

Documento 30, Fase 5: `WF-01 Captura Noticias`.

## Archivos modificados

- `docs/Docs_Asistentes/2026_06_06_carga_temporal_fuentes_rss.md`.

## Decisiones

- No se uso `id_fuente`; PostgreSQL genero los IDs reales de `sources`.
- Se cargo por API con `POST /api/v1/sources`, respetando el caso de uso `CreateSourceUseCase` y la validacion de duplicados por URL.
- Todas las fuentes se cargaron con `type = RSS` y `active = true`.
- `priority` se asigno segun el orden de la lista facilitada, de `1` a `36`.
- No se creo migracion Flyway porque las fuentes aun deben depurarse.
- No se modifico `backend/pom.xml` ni `CHANGELOG.md` porque no hubo cambio de codigo ni migracion.

## Fuentes cargadas

- `1`: CCOO Enseñanza.
- `2`: STEs Intersindical.
- `3`: BOE.
- `4`: UGT Enseñanza.
- `5`: Boja Oposiciones.
- `6`: El Mundo Andalucía.
- `7`: Málaga Hoy.
- `8`: ANPE Andalucía.
- `9`: CSIF Educación.
- `10`: El País Educación.
- `11`: 20 Minutos.
- `12`: eldiario.es Andalucía.
- `13`: Granada Hoy.
- `14`: Diario de Sevilla.
- `15`: Huelva Información.
- `16`: Ideal.
- `17`: BOJA Oficial (Actualidad).
- `18`: El Diario de la Educación.
- `19`: La Opinión de Málaga.
- `20`: Magisnet.
- `21`: Educación 3.0.
- `22`: Campus Educación.
- `23`: Docentes 2.0.
- `24`: INTEF.
- `25`: Universidad de Sevilla.
- `26`: Administracion.gob.es.
- `27`: Opospills.
- `28`: Xataka Educación.
- `29`: Genbeta.
- `30`: MuyComputer Educación.
- `31`: Tiching Blog.
- `32`: Google for Education Blog.
- `33`: CRUE Universidades.
- `34`: Europa Press.
- `35`: Diario Sur.
- `36`: Prensa Google News (Profesorado Andalucía).

## Pruebas y verificaciones

- Se comprobo que el backend respondia en `http://localhost:8080/api/v1/sources`.
- Se cargaron las fuentes por API.
- La primera carga creo 20 fuentes y rechazo 16 por problema de codificacion en nombres con caracteres acentuados desde PowerShell.
- Se corrigio la carga usando JSON con Unicode escapado y se actualizaron/crearon las fuentes restantes.
- Verificacion final con Node contra `GET /api/v1/sources`: `loaded=36`, `missing=0`, `totalSources=37`.

## Resultado

Las 36 fuentes facilitadas estan disponibles en la base de datos con IDs generados por PostgreSQL y preparadas para probar `WF-01-Capture-News`.
