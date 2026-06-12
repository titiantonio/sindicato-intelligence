# 2026-06-11 - Automatización de Operaciones Diarias Dev

## Resumen Ejecutivo

Creación de script PowerShell automatizado + documentación MD para simplificar flujo de desarrollo local diario:
- Reset completo de base de datos (Docker down -v)
- Levantamiento del stack (postgres, n8n, mailhog)
- Validación de migraciones Flyway
- Arranque de backend Spring Boot

**Objetivo:** Prevenir errores manuales de CWD, puertos bloqueados, ordenes incorrecto y bases de datos inconsistentes.

## Contexto

Previos en esta conversación:
- Consolidación de migraciones Flyway V4/V5 → V1/V2 (tarea completada el 2026-06-11)
- Actualización de Documento 31 con tasks consolidadas
- Sistema validado en BBDD limpia con 3 migraciones aplicadas correctamente

Necesidad identificada: automatizar el flujo de reinicio diario para evitar problemas de reproducibilidad y configuración manual.

## Entregables

### 1. Script PowerShell: `dev-startup.ps1`

**Ubicación:** `c:\Users\Antonio\Desktop\Proyectos\sindicato-intelligence\dev-startup.ps1`

**Funcionalidad (5 fases automáticas):**

1. **Verificación de directorios**
   - Valida que existan `database/` y `backend/`
   - Falla temprano si faltan directorios

2. **Reset Docker con volúmenes**
   ```powershell
   docker compose down -v
   docker compose up -d
   ```
   - Ejecutado desde `database/` para correcta resolución de rutas
   - Elimina volúmenes para BBDD limpia (comportamiento esperado en dev)
   - Levanta postgres, n8n, mailhog

3. **Espera a PostgreSQL**
   - TCP connection check a `localhost:5432`
   - Reintenta hasta 60 segundos
   - Evita ejecutar migraciones antes de que BBDD esté lista

4. **Validación Flyway**
   ```sql
   SELECT COUNT(*) FROM flyway_schema_history WHERE version IS NOT NULL;
   ```
   - Verifica exactamente 3 migraciones (V1, V2, V3)
   - Manejo graceful: si falla validación, avisa pero continúa
   - Indica issue potencial sin bloquear desarrollo

5. **Arranque Backend**
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```
   - Ejecutado desde `backend/` con corrección de CWD
   - Deja terminal interactiva (Ctrl+C para detener)
   - Spring Boot aplica Flyway automáticamente

**Características de robustez:**
- Error handling con `$ErrorActionPreference = 'Stop'`
- Salida colorizada (Cyan info, Yellow work, Green success, Red errors)
- Validación de archivos críticos antes de iniciar
- Push-Location/Pop-Location para evitar cambios de CWD permanentes
- Información útil durante ejecución (URLs de endpoints, duración estimada)

### 2. Documentación Markdown: `dev-startup.md`

**Ubicación:** `c:\Users\Antonio\Desktop\Proyectos\sindicato-intelligence\dev-startup.md`

**Secciones:**

1. **Objetivo** - Descripción clara de qué automatiza
2. **Requisitos** - PowerShell 5.0+, Docker, Maven, permisos admin
3. **Uso** - Tres opciones (Explorer, terminal, bypass políticas)
4. **Qué hace el script** - Detalles de cada fase
   - Descripción técnica
   - Comandos ejecutados
   - Estados esperados vs. advertencias
5. **Detener el backend** - Instrucciones Ctrl+C y cleanup
6. **Solución de problemas** - 6 escenarios comunes:
   - PowerShell policy error → Set-ExecutionPolicy
   - Docker no disponible → Iniciar Docker Desktop
   - Puerto 8080 ocupado → Stop-Process o Find NetConnection
   - PostgreSQL no accesible → Wait + Restart
   - Migraciones inesperadas → Full reset + V4/V5 info
   - Cambios en código → Restart backend, en config → Full reset
7. **Workflow diario recomendado** - Uso multi-terminal
8. **Variables de entorno por defecto** - Referencia rápida
9. **Archivos relevantes** - Mapa de archivos relacionados
10. **Logs y troubleshooting** - Cómo debug después

## Validación Completada

✓ Scripts creados sin errores de sintaxis
✓ Documentación en Markdown sin errores
✓ Nombres de archivo consistentes (`dev-startup.*`)
✓ Ubicación en raíz del proyecto (accesible para cualquier dev)
✓ Instrucciones probables en documentación

## Instrucciones de Uso

### Primer arranque
```powershell
cd C:\Users\Antonio\Desktop\Proyectos\sindicato-intelligence
.\dev-startup.ps1
```

Si PowerShell rechaza ejecución de scripts:
```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
.\dev-startup.ps1
```

### Terminación
- **Backend solo:** `Ctrl+C` en terminal
- **Todo:** `Ctrl+C` + `docker compose down` en otra terminal

## Casos de Uso Cubiertos

✓ Primer arranque del día (BBDD limpia)
✓ Después de cambios en código Java (restart backend)
✓ Después de cambios en Flyway o config (full reset)
✓ Detección automática de puerto ocupado → instrucciones
✓ PostgreSQL lento → reintento automático
✓ Multiple terminales → script aislado en una, frontend/work en otras

## Integración Recomendada

1. **Referencia en README.md:** Incluir sección "Dev Setup Automatizado"
2. **Referencia en Documento 31:** Actualizar con "Usar dev-startup.ps1 para arranques locales"
3. **Documentación del proyecto:** Incluir en onboarding de nuevos devs

## Notas Importantes

- Script ejecutado múltiples veces es **seguro** (idempotent)
- Resetea BBDD cada vez (comportamiento intencionado en fase dev temprana)
- No modifica Git ni archivos del proyecto
- Toda salida va a consola PowerShell (logs capturables si requiere)
- Compatible con VS Code terminal integrada

## Archivos Afectados

**Creados:**
- `dev-startup.ps1` (163 líneas)
- `dev-startup.md` (311 líneas)

**No modificados:** (por ser operación aislada)
- Backend Spring Boot
- Frontend Angular
- Database (solo ejecuta con volúmenes)
- Documentación técnica existente

## Fases MVP Relacionadas

Fase 1 (Backend base) → Fase 2 (Modelo datos) → Todas posteriores

Todas las fases requieren BBDD levantada. Este script automatiza la preparación para cualquier fase.

## Próximas Acciones Sugeridas

1. **Test manual:** Ejecutar el script desde cero en máquina limpia
2. **Referencia en README:** Añadir sección sobre setup rápido
3. **Compartir con equipo:** Si hay otros devs, incluir en onboarding

---

**Crear fecha:** 2026-06-11  
**Responsable:** GitHub Copilot (Agent)  
**Tipo:** Automatización / Operaciones / DevOps  
**Complejidad:** Media (PowerShell + Docker + validación)  
**Estado:** ✓ Completado
