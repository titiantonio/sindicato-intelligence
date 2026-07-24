import { expect, test } from '@playwright/test';

import { createMockApiState, loginWithMockRole, mockApi } from './support/mock-api';

test.beforeEach(async ({ page }) => {
  await mockApi(page, createMockApiState());
});

test('cubre flujo evento contenido aprobacion y programacion simulada', async ({ page }) => {
  await loginWithMockRole(page, 'EDITOR');

  await page.goto('/events/101');
  await expect(page.getByRole('heading', { name: 'Convocatoria extraordinaria de oposiciones' })).toBeVisible();
  await expect(page.getByText('Impacto relevante en oposiciones docentes.')).toBeVisible();

  await page.getByLabel('Tipo editorial').selectOption('TELEGRAM_SHORT');
  await page.getByRole('button', { name: 'Generar contenido' }).click();

  await expect(page.getByText('Contenido generado correctamente.')).toBeVisible();
  await expect(page.getByRole('link', { name: 'Resumen breve E2E de oposiciones' })).toBeVisible();

  await page.getByRole('link', { name: 'Contenido', exact: true }).click();
  await expect(page.getByRole('heading', { name: 'Bandeja editorial' })).toBeVisible();

  const generatedRow = page.getByRole('row', { name: /Resumen breve E2E de oposiciones/ });
  await expect(generatedRow).toBeVisible();
  await generatedRow.getByRole('button', { name: 'Aprobar' }).click();

  await expect(page.getByText('Contenido aprobado correctamente.')).toBeVisible();
  await expect(generatedRow).toContainText('APPROVED');

  await generatedRow.getByText('Resumen breve E2E de oposiciones').click();
  const tomorrow = new Date(Date.now() + 24 * 60 * 60 * 1000);
  const scheduledAt = `${tomorrow.getFullYear()}-${String(tomorrow.getMonth() + 1).padStart(2, '0')}-${String(tomorrow.getDate()).padStart(2, '0')}T10:00`;
  await page.getByLabel('Programar publicacion').fill(scheduledAt);
  await page.getByRole('button', { name: 'Programar' }).click();

  await expect(page.getByText('Publicacion programada correctamente.')).toBeVisible();

  await page.goto('/publications');
  await expect(page.getByRole('heading', { name: 'Historico operativo del canal' })).toBeVisible();
  await expect(page.getByText('Contenido #202')).toBeVisible();
  await expect(page.getByText(/Programada:/)).toBeVisible();
  await expect(page.getByText('SCHEDULED')).toBeVisible();
});

test('permite rechazar contenido pendiente sin publicarlo', async ({ page }) => {
  await loginWithMockRole(page, 'EDITOR');

  await page.getByRole('link', { name: 'Contenido', exact: true }).click();
  const pendingRow = page.getByRole('row', { name: /Resumen sindical de oposiciones/ });

  await expect(pendingRow).toBeVisible();
  await pendingRow.getByRole('button', { name: 'Rechazar' }).click();

  await expect(page.getByText('Contenido rechazado correctamente.')).toBeVisible();
  await expect(pendingRow).toContainText('REJECTED');
  await expect(pendingRow.getByRole('button', { name: 'Publicar ahora' })).toBeDisabled();
});
