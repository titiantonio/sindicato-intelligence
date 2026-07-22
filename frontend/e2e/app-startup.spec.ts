import { expect, test } from '@playwright/test';

test('muestra la pantalla de login del backoffice', async ({ page }) => {
  await page.goto('/login');

  await expect(page.getByRole('heading', { name: 'Acceso al backoffice' })).toBeVisible();
  await expect(page.getByLabel('Email')).toBeVisible();
  await expect(page.getByLabel('Password')).toBeVisible();
  await expect(page.getByRole('button', { name: 'Entrar' })).toBeVisible();
});
