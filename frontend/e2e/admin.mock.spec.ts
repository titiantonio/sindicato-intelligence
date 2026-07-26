import { expect, test } from '@playwright/test';

import { loginWithMockRole, mockApi } from './support/mock-api';

test.beforeEach(async ({ page }) => {
  await mockApi(page);
});

test('permite navegar por pantallas ADMIN con API mockeada', async ({ page }) => {
  test.setTimeout(60_000);
  await loginWithMockRole(page, 'ADMIN');

  await expect(page.getByRole('heading', { name: 'Visor operativo del backoffice' })).toBeVisible();

  await page.getByRole('link', { name: 'Configuracion', exact: true }).click();
  await expect(page.getByRole('heading', { name: 'Configuracion' })).toBeVisible();
  await expect(page.getByText('Metricas diarias')).toBeVisible();

  await page.getByRole('link', { name: 'Fuentes', exact: true }).click();
  await expect(page.getByRole('heading', { name: 'Gestion de fuentes RSS' })).toBeVisible();
  await expect(page.getByText('BOJA Educacion')).toBeVisible();

  await page.getByRole('link', { name: 'Usuarios', exact: true }).click();
  await expect(page.getByRole('heading', { name: 'Gestion de usuarios' })).toBeVisible();
  await expect(page.getByText('admin.e2e@sindicato.test')).toBeVisible();

  await page.goto('/audit');
  await expect(page.getByRole('heading', { name: 'Registro operativo' })).toBeVisible();
  await expect(page.getByText('LOGIN_SUCCESS')).toBeVisible();
});
