# T9.2 PublishingProvider

## Fecha

2026-06-09

## Objetivo

Crear el contrato de integracion para canales de publicacion del Sprint 9.

## Contexto

Se revisaron el Documento 31 para T9.2, el Documento 18 y el Documento 21 sobre `PublishingProvider`, y el Documento 09 V2.0 para el flujo WF-06.

## Fase MVP

Fase 10: Publicacion.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/publication/application/PublishingProvider.java`
- `backend/src/main/java/es/sindicato/intelligence/publication/application/PublishingRequest.java`
- `backend/src/main/java/es/sindicato/intelligence/publication/application/PublishingResult.java`
- `backend/src/main/java/es/sindicato/intelligence/publication/application/PublishingProviderException.java`
- `backend/src/test/java/es/sindicato/intelligence/publication/application/PublishingRequestTest.java`
- `backend/src/test/java/es/sindicato/intelligence/publication/application/PublishingResultTest.java`
- `CHANGELOG.md`

## Decisiones

- `PublishingProvider` se ubica en application como puerto de integracion externa consumible por casos de uso.
- Se separa el contrato de entrada (`PublishingRequest`) del resultado externo (`PublishingResult`).
- Se anade excepcion especifica para fallos tecnicos del proveedor.

## Pruebas o verificaciones

- Se anaden pruebas unitarias de validacion para request y result.
- Verificado con `mvn "-Dtest=Publishing*Test" test`: 4 tests ejecutados, 0 fallos, 0 errores.
- Un intento previo con coma sin entrecomillar fallo por sintaxis de PowerShell; no fue un fallo de codigo.
