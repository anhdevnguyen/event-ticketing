import { test, expect } from '@playwright/test';

test('trang chủ load thành công', async ({ page }) => {
  await page.goto('/');
  await expect(page).toHaveTitle(/.+/);
});
