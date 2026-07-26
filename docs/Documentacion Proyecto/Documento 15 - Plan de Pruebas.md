# Documento 15 - Plan de pruebas

**Actualizado:** 25/07/2026
**Objetivo MVP:** cobertura superior al 70 % con cobertura obligatoria de casos de uso y servicios de dominio.

## 1. Estrategia

La calidad se verifica en cuatro capas:

1. dominio y casos de uso;
2. integración backend con PostgreSQL;
3. componentes y servicios Angular;
4. recorridos E2E Playwright.

Las pruebas reales de IA y Telegram quedan fuera de la batería automática ordinaria para evitar coste, inestabilidad y efectos externos.

## 2. Backend

Frameworks:

- JUnit 5;
- Mockito;
- Spring Boot Test;
- MockMvc;
- PostgreSQL local controlado para repositorios e integración.

Áreas:

- reglas de dominio;
- casos de uso;
- DTOs y validaciones;
- controladores y autorización;
- persistencia JPA;
- Flyway;
- proveedores IA deterministas;
- publicación Telegram simulada;
- auditoría y automatizaciones.

Comando:

```powershell
cd backend
.\mvnw.cmd test
```

Resultado auditado el 25/07/2026: 347 pruebas.

Las pruebas de controlador que crean eventos aíslan previamente los eventos existentes para que el resultado no dependa de datos residuales de PostgreSQL.

## 3. Frontend unitario

Frameworks:

- Karma;
- Jasmine;
- Angular TestBed.

Áreas:

- servicios HTTP;
- guards e interceptores;
- sesión y roles;
- páginas principales;
- formularios y diálogos;
- componentes compartidos;
- transformación de modelos.

Comando:

```powershell
cd frontend
npm test -- --watch=false --browsers=ChromeHeadless
```

Resultado auditado el 25/07/2026: 163 pruebas.

## 4. Build

```powershell
cd frontend
npm run build
```

Debe completar el build de producción sin errores y respetar los budgets configurados.

## 5. Playwright mock

Ubicación: `frontend/e2e`.

Objetivos:

- arranque;
- login por rol;
- navegación;
- permisos ADMIN/EDITOR;
- flujo editorial completo;
- eventos;
- sistema visual;
- accesibilidad de diálogos;
- responsive a 320 px.

Comando:

```powershell
cd frontend
npm run e2e:mock
```

Características:

- no requiere backend ni PostgreSQL;
- intercepta `/api/v1/**`;
- no llama a IA real;
- no publica en Telegram;
- usa locators accesibles.

Resultado esperado auditado: 16 pruebas.

## 6. Playwright con backend real

Las suites reales son opt-in y se documentan en `frontend/e2e/README.md`.

Requisitos:

- backend y PostgreSQL levantados;
- usuario local de pruebas;
- IA determinista;
- Telegram deshabilitado;
- datos conocidos o restaurables.

No deben ejecutarse contra producción.

## 7. Infraestructura y scripts

### Docker Compose

```powershell
docker compose config --quiet
```

### Stack

```powershell
.\tfm-start.ps1
.\tfm-check.ps1
```

### n8n

```powershell
.\n8n\validate-workflows.ps1
```

El validador debe aceptar únicamente `WF-01` mientras sea el único workflow n8n activo.

### PowerShell

Todos los scripts versionados deben superar el parser de PowerShell.

## 8. Dependencias

Frontend:

```powershell
npm audit --omit=dev --audit-level=low
npm audit --audit-level=low
```

El primer comando es la puerta de seguridad de dependencias enviadas a producción. El segundo informa también de herramientas de desarrollo.

En la auditoría del 25/07/2026:

- producción: 0 vulnerabilidades;
- desarrollo: 9 vulnerabilidades altas transitivas en `brace-expansion` 1.x a través del stack heredado de Karma;
- `npm audit fix --force` propone un downgrade incompatible y no se acepta.

El riesgo residual no se entrega al navegador de producción y debe revisarse cuando Karma y sus dependencias publiquen una actualización compatible.

## 9. Criterios funcionales

### Captura

- una noticia válida se guarda;
- una URL duplicada no se duplica;
- un lote devuelve resultado por elemento.

### Clasificación

- una noticia `CAPTURED` se clasifica;
- la categoría pertenece a la taxonomía;
- una respuesta IA inválida se rechaza.

### Eventos

- noticias del mismo hecho producen un evento;
- cada noticia pertenece a un evento principal;
- un evento conserva al menos una noticia;
- la fusión mantiene trazabilidad.

### Análisis

- usa únicamente el contexto del evento;
- conserva métricas y versión de prompt;
- marca vigencia frente a cambios del evento.

### Contenido

- nace de un evento;
- requiere revisión;
- admite edición, aprobación y rechazo;
- una edición posterior obliga a revisar de nuevo.

### Publicación

- solo publica contenido aprobado;
- admite inmediata y programada;
- mantiene `PENDING`, `SCHEDULED`, `PUBLISHED` y `FAILED`;
- registra snapshots y auditoría.

### Seguridad

- login y refresh;
- recuperación;
- contraseña temporal y cambio obligatorio;
- roles;
- rate limiting;
- respuestas sin stack trace ni secretos.

### Frontend

- rutas protegidas;
- navegación por rol;
- estados de carga, vacío y error;
- diálogos accesibles y devolución de foco;
- uso a 320 px sin scroll horizontal global.

## 10. Puertas de entrega

Antes de etiquetar una versión:

- [ ] backend completo en verde;
- [ ] frontend unitario en verde;
- [ ] build Angular correcto;
- [ ] Playwright mock en verde;
- [ ] `npm audit --omit=dev` sin vulnerabilidades;
- [ ] Compose válido;
- [ ] `WF-01` válido;
- [ ] scripts PowerShell válidos;
- [ ] README y manual sincronizados;
- [ ] smoke manual del stack completo;
- [ ] ausencia de secretos y artefactos temporales versionados.

El informe de entrega fechado conserva el resultado concreto de cada puerta.
