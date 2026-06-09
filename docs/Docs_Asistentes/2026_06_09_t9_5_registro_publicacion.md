# T9.5 Registro de publicacion

## Fecha

2026-06-09

## Objetivo

Registrar publicaciones en `publications` y exponer la publicacion de contenido aprobado mediante la API backend.

## Contexto

Se revisaron el Documento 31 para T9.5, el Documento 19 para `PublishContentUseCase`, el Documento 12 para `POST /api/v1/publications/{id}/publish`, el Documento 20 para la tabla `publications`, el Documento 09 para el flujo WF-06 y el Documento 13 sobre auditoria de publicaciones.

## Fase MVP

Fase 10: Publicacion.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/publication/application/PublishContentUseCase.java`
- `backend/src/main/java/es/sindicato/intelligence/publication/infrastructure/PublicationEntity.java`
- `backend/src/main/java/es/sindicato/intelligence/publication/infrastructure/JpaPublicationRepository.java`
- `backend/src/main/java/es/sindicato/intelligence/publication/api/PublicationController.java`
- `backend/src/main/java/es/sindicato/intelligence/publication/api/PublicationResponse.java`
- `backend/src/test/java/es/sindicato/intelligence/publication/application/PublishContentUseCaseTest.java`
- `backend/src/test/java/es/sindicato/intelligence/publication/infrastructure/JpaPublicationRepositoryTest.java`
- `backend/src/test/java/es/sindicato/intelligence/publication/api/PublicationControllerTest.java`
- `CHANGELOG.md`

## Decisiones

- El path documentado `POST /api/v1/publications/{id}/publish` se interpreta en el MVP como publicacion por `contentId`, porque la fila `publications` se crea durante el caso de uso.
- `PublishContentUseCase` valida que el contenido este `APPROVED` antes de invocar el proveedor.
- La publicacion se registra como `PENDING`, despues como `PUBLISHED` si Telegram responde correctamente o `FAILED` si falla el proveedor.
- El contenido pasa a `PUBLISHED` solo si la publicacion externa termina correctamente.
- Se usa `@Transactional(noRollbackFor = PublishingProviderException.class)` para conservar el registro `FAILED` cuando el proveedor devuelve error.

## Pruebas o verificaciones

- Se anaden pruebas unitarias de caso de uso para exito, estado no aprobado y fallo de proveedor.
- Se anaden pruebas de repositorio JPA para guardar y consultar publicaciones por contenido.
- Se anaden pruebas API para publicacion correcta y rechazo de contenido no aprobado.
- Verificado con `mvn "-Dtest=PublishContentUseCaseTest,JpaPublicationRepositoryTest,PublicationControllerTest" test`: 6 tests ejecutados, 0 fallos, 0 errores.
