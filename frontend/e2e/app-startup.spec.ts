import { expect, test } from '@playwright/test';

test('muestra la pantalla de login del backoffice', async ({ page }) => {
  await page.addInitScript(() => {
    window.localStorage.setItem('sindicato-theme', JSON.stringify('light'));
  });
  await page.goto('/login');

  await expect(page.getByRole('heading', { name: 'Acceso al backoffice' })).toBeVisible();
  await expect(page.getByLabel('Email')).toBeVisible();
  await expect(page.getByLabel('Password')).toBeVisible();
  await expect(page.getByRole('button', { name: 'Entrar' })).toBeVisible();
});

test('usa una introduccion clara y legible en el tema claro', async ({ page }) => {
  await page.addInitScript(() => {
    window.localStorage.setItem('sindicato-theme', JSON.stringify('light'));
  });
  await page.goto('/login');

  const intro = page.locator('.login-page__intro');
  const introBackground = await intro.evaluate(
    (element) => window.getComputedStyle(element).backgroundImage
  );

  await expect(page.locator('html')).toHaveAttribute('data-theme', 'light');
  expect(introBackground).toContain('rgb(223, 244, 239)');
  expect(introBackground).not.toContain('rgb(7, 24, 39)');
  await expect(intro.getByRole('heading', { level: 1 })).toHaveCSS(
    'color',
    'rgb(16, 47, 54)'
  );
});
