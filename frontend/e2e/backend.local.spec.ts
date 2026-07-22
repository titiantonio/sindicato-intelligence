import { expect, test } from '@playwright/test';

type BackendRole = 'ADMIN' | 'EDITOR';

const backendEnabled = process.env['E2E_BACKEND_ENABLED'] === 'true';
const backendEmail = process.env['E2E_BACKEND_EMAIL'] ?? '';
const backendPassword = process.env['E2E_BACKEND_PASSWORD'] ?? '';
const backendRole = process.env['E2E_BACKEND_ROLE'] as BackendRole | undefined;

test.describe('backend local controlado', () => {
  test.skip(!backendEnabled, 'Suite backend local deshabilitada. Define E2E_BACKEND_ENABLED=true para ejecutarla.');

  test.beforeEach(() => {
    if (!backendEmail || !backendPassword || !backendRole) {
      throw new Error('Define E2E_BACKEND_EMAIL, E2E_BACKEND_PASSWORD y E2E_BACKEND_ROLE para ejecutar la suite con backend real.');
    }
  });

  test('redirige rutas protegidas al login sin sesion', async ({ page }) => {
    await page.goto('/dashboard');

    await expect(page).toHaveURL(/\/login$/);
    await expect(page.getByRole('heading', { name: 'Acceso al backoffice' })).toBeVisible();
  });

  test('permite login real y carga rutas operativas comunes', async ({ page }) => {
    await loginAgainstBackend(page);

    await expect(page.getByRole('heading', { name: 'Visor operativo del backoffice' })).toBeVisible();

    await page.getByRole('link', { name: 'Eventos', exact: true }).click();
    await expect(page.getByRole('heading', { name: 'Centro operativo del sistema' })).toBeVisible();

    await page.getByRole('link', { name: 'Contenido', exact: true }).click();
    await expect(page.getByRole('heading', { name: 'Bandeja editorial' })).toBeVisible();

    await page.getByRole('link', { name: 'Publicaciones', exact: true }).click();
    await expect(page.getByRole('heading', { name: 'Historico operativo del canal' })).toBeVisible();
  });

  test('permite rutas ADMIN con backend real', async ({ page }) => {
    test.skip(backendRole !== 'ADMIN', 'La validacion ADMIN requiere E2E_BACKEND_ROLE=ADMIN.');

    await loginAgainstBackend(page);

    await page.getByRole('link', { name: 'Configuracion', exact: true }).click();
    await expect(page.getByRole('heading', { name: 'Configuracion' })).toBeVisible();

    await page.getByRole('link', { name: 'Fuentes', exact: true }).click();
    await expect(page.getByRole('heading', { name: 'Gestion de fuentes RSS' })).toBeVisible();

    await page.getByRole('link', { name: 'Usuarios', exact: true }).click();
    await expect(page.getByRole('heading', { name: 'Gestion de usuarios' })).toBeVisible();

    await page.goto('/audit');
    await expect(page.getByRole('heading', { name: 'Registro operativo' })).toBeVisible();
  });

  test('bloquea rutas ADMIN para EDITOR con backend real', async ({ page }) => {
    test.skip(backendRole !== 'EDITOR', 'La validacion EDITOR requiere E2E_BACKEND_ROLE=EDITOR.');

    await loginAgainstBackend(page);

    await expect(page.getByRole('link', { name: 'Configuracion', exact: true })).toHaveCount(0);
    await expect(page.getByRole('link', { name: 'Fuentes', exact: true })).toHaveCount(0);
    await expect(page.getByRole('link', { name: 'Usuarios', exact: true })).toHaveCount(0);
    await expect(page.getByRole('link', { name: 'Auditoria', exact: true })).toHaveCount(0);

    await page.goto('/users');
    await expect(page).toHaveURL(/\/dashboard$/);
    await expect(page.getByRole('heading', { name: 'Visor operativo del backoffice' })).toBeVisible();
  });
});

async function loginAgainstBackend(page: import('@playwright/test').Page): Promise<void> {
  await page.goto('/login');
  await page.getByLabel('Email').fill(backendEmail);
  await page.getByLabel('Password').fill(backendPassword);
  await page.getByRole('button', { name: 'Entrar' }).click();
}
