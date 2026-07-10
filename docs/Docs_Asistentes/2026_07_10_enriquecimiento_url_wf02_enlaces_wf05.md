# 2026-07-10 - Enriquecimiento URL WF-02 y enlaces WF-05

## Objetivo

Implementar la mejora de WF-02 para usar la URL de la noticia cuando el contenido capturado sea insuficiente, y mejorar WF-05 para incluir enlaces relevantes permitidos en los borradores Telegram.

## Contexto

- Fase Documento 30: Fase 6 Clasificacion IA y Fase 9 Contenido.
- Documento 31: refinamiento posterior al Sprint 12 registrado como 19.25.
- WF-02 a WF-06 permanecen en Spring Boot; n8n sigue limitado a WF-01.

## Archivos modificados

- Backend clasificacion: prompt, request IA, caso de uso y adaptador de enriquecimiento URL.
- Backend contenido: request IA, prompt, caso de uso, extractor de enlaces relevantes y tests.
- Configuracion: `backend/src/main/resources/application.yml`.
- Documentacion: Documento 23, Documento 31 y `CHANGELOG.md`.
- Versionado: `backend/pom.xml` a `0.0.89-SNAPSHOT`.

## Decisiones

- No se confia en navegacion del modelo IA: Spring Boot intenta enriquecer desde URL antes de clasificar cuando el contexto local es corto.
- El enriquecimiento URL solo permite `http/https`, bloquea hosts locales/privados y limita tiempo y tamano.
- WF-05 solo pasa enlaces relevantes permitidos al prompt; se descartan dominios configurados de otros sindicatos y se priorizan dominios oficiales.

## Verificacion

- Ejecutado `mvn "-Dtest=ClassifyNewsPromptBuilderTest,ClassifyNewsUseCaseTest,GenerateContentPromptBuilderTest,GenerateContentUseCaseTest,*RelevantLink*Test,RestClientNewsContentEnrichmentAdapterTest" test` desde `backend`.
- Resultado: 15 tests, 0 fallos, 0 errores.
