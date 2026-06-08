import type { FullConfig } from '@playwright/test';

async function globalSetup(_config: FullConfig) {
  const apiBase = process.env.API_BASE_URL ?? 'http://localhost:8080/api';

  try {
    const res = await fetch(`${apiBase}/books?page=0&size=1`);
    if (!res.ok) {
      throw new Error(`HTTP ${res.status}`);
    }
  } catch (error) {
    throw new Error(
      [
        `Spring Boot API(${apiBase})가 실행 중이어야 E2E 테스트를 진행할 수 있습니다.`,
        '먼저 프로젝트 루트에서 .\\gradlew.bat bootRun 을 실행하세요.',
        String(error),
      ].join('\n')
    );
  }
}

export default globalSetup;
