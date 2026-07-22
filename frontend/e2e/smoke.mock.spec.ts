import { expect, test } from '@playwright/test';

import { loginWithMockRole, mockApi } from './support/mock-api';

test.beforeEach(async ({ page }) => {
  await mockApi(page);
});

test('permite login mockeado y navegacion editorial principal', async ({ page }) => {
  await loginWithMockRole(page, 'EDITOR');

  await expect(page.getByRole('heading', { name: 'Visor operativo del backoffice' })).toBeVisible();
  await expect(page.getByText('Convocatoria extraordinaria de oposiciones')).toBeVisible();

  await page.getByRole('link', { name: 'Eventos', exact: true }).click();
  await expect(page.getByRole('heading', { name: 'Centro operativo del sistema' })).toBeVisible();
  await expect(page.getByRole('cell', { name: 'Actualizacion de bolsas docentes' })).toBeVisible();

  await page.getByRole('link', { name: 'Contenido', exact: true }).click();
  await expect(page.getByRole('heading', { name: 'Bandeja editorial' })).toBeVisible();
  await expect(page.getByText('Resumen sindical de oposiciones')).toBeVisible();

  await page.getByRole('link', { name: 'Publicaciones', exact: true }).click();
  await expect(page.getByRole('heading', { name: 'Historico operativo del canal' })).toBeVisible();
  await expect(page.getByText('Contenido #201')).toBeVisible();
});

test('oculta rutas ADMIN a usuarios EDITOR', async ({ page }) => {
  await loginWithMockRole(page, 'EDITOR');

  await expect(page.getByRole('link', { name: 'Configuracion' })).toHaveCount(0);
  await expect(page.getByRole('link', { name: 'Fuentes' })).toHaveCount(0);
  await expect(page.getByRole('link', { name: 'Usuarios' })).toHaveCount(0);
  await expect(page.getByRole('link', { name: 'Auditoria' })).toHaveCount(0);

  await page.goto('/users');
  await expect(page).toHaveURL(/\/dashboard$/);
  await expect(page.getByRole('heading', { name: 'Visor operativo del backoffice' })).toBeVisible();
});
