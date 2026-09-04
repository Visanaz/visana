import { expect, test } from '@playwright/test';

test('loads the public foundation shell', async ({ page }) => {
  await page.goto('/');

  await expect(page.getByRole('heading', { name: 'VISANA Plan 3 frontend' })).toBeVisible();
  await expect(page.getByText('Runtime configuration')).toBeVisible();
});
