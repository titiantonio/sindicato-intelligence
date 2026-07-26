import { expect, test } from '@playwright/test';

import { createMockApiState, loginWithMockRole, mockApi } from './support/mock-api';

test.beforeEach(async ({ page }) => {
  await mockApi(page, createMockApiState());
  await loginWithMockRole(page, 'ADMIN');
});

test('mantiene el sistema visual en todas las rutas del backoffice', async ({ page }) => {
  test.setTimeout(60_000);
  const routes = [
    ['/dashboard', 'Visor operativo del backoffice'],
    ['/events', 'Eventos'],
    ['/events/101', 'Convocatoria extraordinaria de oposiciones'],
    ['/news', 'Noticias capturadas'],
    ['/news/1001', 'Convocatoria extraordinaria de oposiciones docentes'],
    ['/content', 'Bandeja editorial'],
    ['/content/201', 'Resumen sindical de oposiciones'],
    ['/publications', 'Historico operativo del canal'],
    ['/publications/401', 'Resumen sindical de oposiciones'],
    ['/sources', 'Gestion de fuentes RSS'],
    ['/users', 'Gestion de usuarios'],
    ['/audit', 'Registro operativo'],
    ['/settings', 'Configuracion']
  ] as const;

  for (const [route, heading] of routes) {
    await page.goto(route);
    await expect(page.getByRole('heading', { level: 1, name: heading })).toBeVisible();
    await expect(page.locator('main h1')).toHaveCount(1);
    await expect.poll(() => page.evaluate(() => document.documentElement.scrollWidth <= window.innerWidth)).toBe(true);
  }
});

test('conserva contraste tematico y preferencia de modo oscuro', async ({ page }) => {
  await page.goto('/dashboard');
  await expect.poll(() => page.locator('.page-header h1').evaluate((element) => getComputedStyle(element).color))
    .toBe('rgb(248, 250, 252)');
  const themeButton = page.getByRole('button', { name: 'Cambiar a modo oscuro' });
  await expect(themeButton).toBeVisible();
  await themeButton.click();

  await expect(page.locator('html')).toHaveAttribute('data-theme', 'dark');
  await expect(page.getByRole('button', { name: 'Cambiar a modo claro' })).toHaveAttribute('aria-pressed', 'true');

  await page.reload();
  await expect(page.locator('html')).toHaveAttribute('data-theme', 'dark');
});

test('abre y cierra dialogos accesibles devolviendo el foco', async ({ page }) => {
  await page.goto('/sources');
  const sourceTrigger = page.getByRole('button', { name: 'Añadir fuente' });
  await sourceTrigger.click();
  const sourceDialog = page.getByRole('dialog', { name: 'Anadir fuente' });
  await expect(sourceDialog).toBeVisible();
  await sourceDialog.getByRole('button', { name: 'Cerrar formulario de fuente' }).click();
  await expect(sourceTrigger).toBeFocused();

  await page.goto('/users');
  const userTrigger = page.getByRole('button', { name: 'Alta de usuario' });
  await userTrigger.click();
  const userDialog = page.getByRole('dialog', { name: 'Alta de usuario' });
  await expect(userDialog).toBeVisible();
  await page.keyboard.press('Escape');
  await expect(userTrigger).toBeFocused();

  await page.goto('/publications');
  const manualTrigger = page.getByRole('button', { name: 'Mensaje manual', exact: true });
  await manualTrigger.click();
  const manualDialog = page.getByRole('dialog', { name: 'Mensaje manual' });
  await expect(manualDialog).toBeVisible();
  await manualDialog.getByRole('button', { name: 'Cancelar' }).click();
  await expect(manualTrigger).toBeFocused();

  await page.goto('/audit');
  const auditTrigger = page.getByRole('button', { name: 'Ver detalle' }).first();
  await auditTrigger.click();
  await expect(page.getByRole('dialog')).toBeVisible();
  await page.keyboard.press('Escape');
  await expect(auditTrigger).toBeFocused();
});

test('mantiene shell, contenido y dialogos operables a 320 px', async ({ page }) => {
  await page.setViewportSize({ width: 320, height: 800 });
  await page.goto('/dashboard');

  await expect(page.getByRole('heading', { name: 'Visor operativo del backoffice' })).toBeVisible();
  await expect.poll(() => page.evaluate(() => document.documentElement.scrollWidth)).toBeLessThanOrEqual(320);

  const menuButton = page.locator('.shell__menu-button');
  await expect(menuButton).toHaveAccessibleName('Abrir menu lateral');
  await menuButton.click();
  await expect(page.getByRole('navigation', { name: 'Navegacion principal' })).toBeVisible();
  await expect(menuButton).toHaveAttribute('aria-expanded', 'true');

  await page.getByRole('link', { name: 'Fuentes', exact: true }).click();
  const sourceTrigger = page.getByRole('button', { name: 'Añadir fuente' });
  await sourceTrigger.click();
  await expect(page.getByRole('dialog', { name: 'Anadir fuente' })).toBeVisible();
  await expect.poll(() => page.evaluate(() => document.documentElement.scrollWidth)).toBeLessThanOrEqual(320);
});

test('permite desplazar el menu lateral en moviles con poca altura', async ({ page }) => {
  await page.setViewportSize({ width: 320, height: 360 });
  await page.goto('/dashboard');

  await page.locator('.shell__menu-button').click();
  const sidebar = page.locator('#main-navigation');

  await expect(sidebar).toBeVisible();
  await expect.poll(() => sidebar.evaluate((element) => element.scrollHeight > element.clientHeight)).toBe(true);

  await sidebar.evaluate((element) => {
    element.scrollTop = element.scrollHeight;
  });

  await expect.poll(() => sidebar.evaluate((element) => element.scrollTop)).toBeGreaterThan(0);
  await expect(page.getByRole('link', { name: 'Auditoria', exact: true })).toBeVisible();
});
