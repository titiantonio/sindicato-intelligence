import { expect, test } from '@playwright/test';

import { createMockApiState, loginWithMockRole, mockApi } from './support/mock-api';

test.beforeEach(async ({ page }) => {
  await mockApi(page, createMockApiState());
  await loginWithMockRole(page, 'EDITOR');
  await page.getByRole('link', { name: 'Eventos', exact: true }).click();
  await expect(page).toHaveURL(/\/events$/);
  await expect(page.getByRole('heading', { name: 'Eventos', exact: true })).toBeVisible();
});

test('permite explorar, ordenar y limpiar eventos con nombres accesibles', async ({ page }) => {
  await expect(page.locator('html')).toHaveAttribute('lang', 'es');
  await expect(page).toHaveTitle('Eventos | Sindicato Intelligence');
  await expect(page.getByRole('heading', { name: 'Eventos', exact: true })).toBeVisible();
  await expect(page.getByLabel('Buscar en todos los campos')).toBeVisible();
  await expect(page.locator('th[aria-sort="ascending"]')).toContainText('Impacto');

  await page.getByLabel('Buscar en todos los campos').fill('SIPRI');
  await expect(page.getByRole('link', { name: 'Actualizacion de bolsas docentes' })).toBeVisible();
  await expect(page.getByRole('link', { name: 'Convocatoria extraordinaria de oposiciones' })).toBeHidden();

  await page.getByRole('button', { name: 'Limpiar filtros' }).click();
  await expect(page.getByRole('link', { name: 'Convocatoria extraordinaria de oposiciones' })).toBeVisible();

  await page.getByRole('button', { name: 'Ordenar por título de forma ascendente' }).click();
  await expect(page.locator('th[aria-sort="ascending"]')).toContainText('Título');
});

test('mantiene la fusion operable por teclado y confirma en un dialogo accesible', async ({ page }) => {
  await page.getByText('Fusionar eventos duplicados', { exact: true }).click();

  const destination = page.getByRole('radio', { name: /#101.*Convocatoria extraordinaria/ });
  const source = page.getByRole('checkbox', { name: /#102.*Actualizacion de bolsas/ });
  const mergeButton = page.getByRole('button', { name: 'Revisar y fusionar' });

  await expect(destination).toBeChecked();
  await expect(mergeButton).toBeDisabled();
  await source.check();
  await expect(mergeButton).toBeEnabled();
  await mergeButton.click();

  const dialog = page.getByRole('alertdialog', { name: 'Fusionar eventos' });
  await expect(dialog).toBeVisible();
  await expect(dialog.getByText(/Se archivaran 1 eventos origen/)).toBeVisible();
  await expect(dialog.getByRole('button', { name: 'Cancelar' })).toBeFocused();

  for (let index = 0; index < 6; index += 1) {
    await page.keyboard.press('Tab');
    await expect.poll(() => page.evaluate(() => document.activeElement?.closest('[role="alertdialog"]') !== null)).toBe(true);
  }

  await page.keyboard.press('Escape');
  await expect(dialog).toBeHidden();
  await expect(mergeButton).toBeFocused();
});

test('evita desbordamiento global y conserva objetivos táctiles en móvil', async ({ page }) => {
  await page.setViewportSize({ width: 390, height: 844 });
  await page.reload();

  await expect(page.getByRole('heading', { name: 'Eventos', exact: true })).toBeVisible();
  let documentWidth = await page.evaluate(() => document.documentElement.scrollWidth);
  expect(documentWidth).toBeLessThanOrEqual(390);

  await page.setViewportSize({ width: 320, height: 800 });
  documentWidth = await page.evaluate(() => document.documentElement.scrollWidth);
  expect(documentWidth).toBeLessThanOrEqual(320);

  const controls = page.locator('main button:visible, main summary:visible, main input:visible');
  const count = await controls.count();
  for (let index = 0; index < count; index += 1) {
    const box = await controls.nth(index).boundingBox();
    if (box) {
      expect(Math.max(box.width, box.height)).toBeGreaterThanOrEqual(24);
    }
  }

  await expect(page.locator('main')).not.toContainText('Â');
});

test('conserva contenido y controles con el espaciado de texto WCAG', async ({ page }) => {
  await page.setViewportSize({ width: 320, height: 800 });
  await page.addStyleTag({
    content: `
      main, main * {
        line-height: 1.5 !important;
        letter-spacing: 0.12em !important;
        word-spacing: 0.16em !important;
      }

      main p {
        margin-block-end: 2em !important;
      }
    `
  });

  await expect(page.getByRole('heading', { name: 'Eventos', exact: true })).toBeVisible();
  await expect(page.getByLabel('Buscar en todos los campos')).toBeVisible();
  await page.getByText('Fusionar eventos duplicados', { exact: true }).click();
  await expect(page.getByRole('button', { name: 'Revisar y fusionar' })).toBeVisible();

  const documentWidth = await page.evaluate(() => document.documentElement.scrollWidth);
  expect(documentWidth).toBeLessThanOrEqual(320);

  const clippedControls = await page.locator('main').evaluate((main) =>
    [...main.querySelectorAll<HTMLElement>('button, a, summary')]
      .filter((element) => element.getClientRects().length > 0)
      .filter((element) => (element.textContent?.trim().length ?? 0) > 0)
      .filter((element) => element.scrollWidth > element.clientWidth + 1 || element.scrollHeight > element.clientHeight + 1)
      .map((element) => element.getAttribute('aria-label') ?? element.textContent?.trim() ?? element.tagName)
  );
  expect(clippedControls).toEqual([]);
});
