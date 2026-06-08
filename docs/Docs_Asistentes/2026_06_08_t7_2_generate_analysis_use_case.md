# T7.2 GenerateAnalysisUseCase

## Fecha

2026-06-08

## Objetivo

Implementar el caso de uso `GenerateAnalysisUseCase` para generar analisis IA consolidados a partir de un evento y sus noticias asociadas.

## Contexto

- Fase MVP: Fase 8, Analisis IA.
- Sprint: Sprint 7.
- Tarea Documento 31: T7.2 Implementar GenerateAnalysisUseCase.
- WF-04 invocara este caso de uso para generar conocimiento consolidado.

## Archivos modificados

- `backend/src/main/java/es/sindicato/intelligence/analysis/application/AnalysisAIProvider.java`
- `backend/src/main/java/es/sindicato/intelligence/analysis/application/AnalysisAIRequest.java`
- `backend/src/main/java/es/sindicato/intelligence/analysis/application/AnalysisAIResponse.java`
- `backend/src/main/java/es/sindicato/intelligence/analysis/application/AnalysisNewsItem.java`
- `backend/src/main/java/es/sindicato/intelligence/analysis/application/GenerateAnalysisCommand.java`
- `backend/src/main/java/es/sindicato/intelligence/analysis/application/GenerateAnalysisUseCase.java`
- `backend/src/test/java/es/sindicato/intelligence/analysis/application/GenerateAnalysisUseCaseTest.java`
- `CHANGELOG.md`

## Decisiones tomadas

- Crear un puerto `AnalysisAIProvider` especifico de analisis en lugar de reutilizar el puerto de clasificacion.
- El caso de uso carga el evento y sus noticias desde repositorios de dominio para mantener la logica en Spring Boot.
- La persistencia se realiza contra el puerto `EventAIAnalysisRepository`; la implementacion JPA queda para T7.4.
- Incorporar logs operativos de inicio, contexto cargado, exito, noticia asociada inexistente y error IA.

## Pruebas o verificaciones

- Ejecutado `mvn "-Dtest=EventAIAnalysisTest,GenerateAnalysisUseCaseTest" test` desde `backend`.
- Resultado: `Tests run: 8, Failures: 0, Errors: 0, Skipped: 0`.
- Resultado Maven: `BUILD SUCCESS`.
