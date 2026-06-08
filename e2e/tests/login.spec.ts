import { test, expect } from '@playwright/test';

const ADMIN = {
  id: 'admin',
  pw: 'admin1234',
  name: '관리자',
};

test.describe('이용자 홈 화면', () => {
  test('메인 페이지가 표시된다', async ({ page }) => {
    await page.goto('/');

    await expect(page).toHaveTitle('도서 대여 관리 시스템');
    await expect(page.getByRole('heading', { name: '도서 대여 시스템' })).toBeVisible();
    await expect(page.getByRole('button', { name: '도서 반납' })).toBeVisible();
    await expect(page.getByRole('link', { name: '관리자' })).toBeVisible();
    await expect(page.getByRole('button', { name: '도서 목록' })).toBeVisible();
    await expect(page.getByRole('button', { name: '대여 순위' })).toBeVisible();
  });

  test('관리자 페이지로 이동할 수 있다', async ({ page }) => {
    await page.goto('/');
    await page.getByRole('link', { name: '관리자' }).click();

    await expect(page).toHaveURL(/\/admin$/);
    await expect(page.getByRole('heading', { name: '관리자 로그인' })).toBeVisible();
  });
});

test.describe('관리자 로그인', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/admin');
    await page.evaluate(() => sessionStorage.clear());
    await page.reload();
  });

  test('로그인 화면이 표시된다', async ({ page }) => {
    await expect(page.getByRole('heading', { name: '관리자 로그인' })).toBeVisible();
    await expect(page.getByPlaceholder('관리자 아이디')).toBeVisible();
    await expect(page.getByPlaceholder('비밀번호')).toBeVisible();
    await expect(page.getByRole('button', { name: '로그인' })).toBeVisible();
    await expect(page.getByText('초기 계정: admin / admin1234')).toBeVisible();
  });

  test('TC-ADMIN-LOGIN-001 | 빈 값 제출 시 클라이언트 검증 메시지가 표시된다', async ({ page }) => {
    await page.getByRole('button', { name: '로그인' }).click();

    await expect(page.getByText('아이디와 비밀번호를 입력해 주세요.')).toBeVisible();
    await expect(page.getByRole('heading', { name: '관리자 로그인' })).toBeVisible();
  });

  test('TC-ADMIN-LOGIN-001 | 정상 로그인 시 대시보드가 표시된다', async ({ page }) => {
    await page.getByPlaceholder('관리자 아이디').fill(ADMIN.id);
    await page.getByPlaceholder('비밀번호').fill(ADMIN.pw);
    await page.getByRole('button', { name: '로그인' }).click();

    await expect(page.getByRole('heading', { name: '관리자 대시보드' })).toBeVisible();
    await expect(page.getByText(`${ADMIN.name} 님`)).toBeVisible();
    await expect(page.getByRole('button', { name: '도서 관리' })).toBeVisible();
    await expect(page.getByRole('button', { name: '대여 현황' })).toBeVisible();
    await expect(page.getByRole('button', { name: '반납 이력' })).toBeVisible();
    await expect(page.getByRole('button', { name: '불량 이용자' })).toBeVisible();
  });

  test('TC-ADMIN-LOGIN-002 | 잘못된 비밀번호로 로그인하면 오류 메시지가 표시된다', async ({ page }) => {
    await page.getByPlaceholder('관리자 아이디').fill(ADMIN.id);
    await page.getByPlaceholder('비밀번호').fill('wrong-password');
    await page.getByRole('button', { name: '로그인' }).click();

    await expect(page.getByText('아이디 또는 비밀번호가 올바르지 않습니다.')).toBeVisible();
    await expect(page.getByRole('heading', { name: '관리자 로그인' })).toBeVisible();
  });

  test('TC-ADMIN-LOGIN-004 | 로그아웃하면 로그인 화면으로 돌아간다', async ({ page }) => {
    await page.getByPlaceholder('관리자 아이디').fill(ADMIN.id);
    await page.getByPlaceholder('비밀번호').fill(ADMIN.pw);
    await page.getByRole('button', { name: '로그인' }).click();
    await expect(page.getByRole('heading', { name: '관리자 대시보드' })).toBeVisible();

    await page.getByRole('button', { name: '로그아웃' }).click();

    await expect(page.getByRole('heading', { name: '관리자 로그인' })).toBeVisible();
    await expect(page.getByRole('heading', { name: '관리자 대시보드' })).not.toBeVisible();
  });
});
