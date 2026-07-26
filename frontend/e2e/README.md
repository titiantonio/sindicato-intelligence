# Pruebas End-to-End

Las pruebas Playwright del backoffice se dividen en dos grupos para mantener una
ejecución segura y reproducible.

## Suite mockeada

No requiere backend, PostgreSQL, proveedor de IA ni Telegram. Intercepta la API
desde Playwright y cubre:

- arranque y login;
- navegación editorial y rutas `ADMIN`;
- flujo evento, contenido, aprobación, rechazo y programación;
- accesibilidad de diálogos y retorno de foco;
- tema claro y oscuro;
- adaptación móvil hasta `320 CSS px`.

Ejecución:

```powershell
cd frontend
npm.cmd run e2e:mock
```

## Suite con backend local

Es opt-in y está en `backend.local.spec.ts`. Requiere:

1. PostgreSQL y backend local disponibles;
2. frontend accesible en la URL indicada por Playwright;
3. un usuario de pruebas válido;
4. variables de entorno configuradas sin versionar secretos.

Variables admitidas:

```text
E2E_BACKEND_ENABLED=true
E2E_ADMIN_EMAIL=<usuario de pruebas>
E2E_ADMIN_PASSWORD=<contraseña de pruebas>
```

Ejecución:

```powershell
cd frontend
$env:E2E_BACKEND_ENABLED='true'
$env:E2E_ADMIN_EMAIL='<usuario de pruebas>'
$env:E2E_ADMIN_PASSWORD='<contraseña de pruebas>'
npx.cmd playwright test e2e/backend.local.spec.ts
```

La suite real no debe ejecutar proveedores de IA ni publicar en Telegram salvo
que exista un entorno seguro creado expresamente para ello.
